package com.szxb.buspay.manager;

import android.text.TextUtils;

import com.example.zhoukai.modemtooltest.ModemToolTest;
import com.google.gson.Gson;
import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.entity.bean.ConfigParam;
import com.szxb.buspay.db.entity.bean.FTPEntity;
import com.szxb.buspay.db.entity.card.LineInfoEntity;
import com.szxb.buspay.db.manager.DBManager;
import com.szxb.buspay.db.sp.CommonSharedPreferences;
import com.szxb.buspay.db.sp.FetchAppConfig;
import com.szxb.buspay.util.DateUtil;
import com.szxb.buspay.util.Util;
import com.szxb.mlog.SLog;

import java.util.List;

import static com.szxb.buspay.util.Util.string2Int;

/**
 * 作者：Tangren on 2018-07-18
 * 包名：com.szxb.buspay.manager
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class PosManager implements IPosManager, IAddRess, ISwitch {

    /**
     * 路线名
     */
    private String lineName;
    /**
     * 路线号
     */
    private String lineNo;
    /**
     * 起始站名
     */
    private String startStationName;
    /**
     * 终点站名
     */
    private String endStationName;
    /**
     * 设备号
     */
    private String posSn;

    /**
     * 基础金额
     */
    private int basePrice;
    /**
     * 微信实际扣款金额
     */
    private int paymarkedPrice;

    /**
     * 银联实际扣款
     */
    private int unionPaymarkedPrice;

    /**
     * 银联二维码
     */
    private int unionScanPrice;

    /**
     * 370300zibo  371200laiwu
     */
    private String cityCode;
    /**
     * 站点ID
     */
    private int inStationId = 13;
    /**
     * 备注
     */
    private String orderDesc = "腾讯乘车码";
    /**
     * key
     */
    private byte[] key;
    /**
     * 车牌号
     */
    private String bus_no;
    /**
     * 腾讯商户号
     * <p>
     * 09 zibo  10laiwu
     */
    private String app_id = "00000000";
    /**
     * 今日交易笔数
     */
    private long payNum;
    /**
     * K21是否完成更新
     */
    private String k21Init;
    /**
     * 签到状态,默认未签到
     */
    private boolean isSign;
    /**
     * 单位号
     */
    private String unitno;
    /**
     * 司机号
     */
    private String driverNo;
    /**
     * 司机号卡号
     */
    private String empNo;

    private String ftpIP;
    private int ftpPort;
    private String ftpUser;
    private String ftpPsw;

    private FTPEntity ftpEntity;

    //是否存在线路
    private boolean exitLine;

    //当前线路信息
    private LineInfoEntity lineInfoEntity;

    //上个bin版本
    private String lastVersion;

    //当前bin
    private String binName;

    //上个黑名单版本
    private String lastBlackVersion;

    //上一个银联参数文件
    private String lastParamsFileName;

    /**
     * ################淄博      栖霞、               莱芜                泰安、沂源、招远
     * coefficient[0]:普通卡      普通卡              普通卡                 普通卡
     * coefficient[1]:学生卡      学生卡              学生卡                 学生卡
     * coefficient[2]:老年卡      老年卡              老年卡                 老年卡
     * coefficient[3]:免费        免费卡              免费卡                 免费卡
     * coefficient[4]:员工        优惠1               残疾人卡               优惠1
     * coefficient[5]:优惠1       员工卡              员工卡                 员工卡
     * coefficient[6]:优惠2       优惠2               优惠2                  优惠2
     * coefficient[7]:优惠3       优惠3               优惠3                  优惠3
     * coefficient[8]:微信        微信                微信                    微信
     * coefficient[9]:银联        银联                银联                    银联
     * coefficient[10]############################### 济南卡 #################优抚卡
     * coefficient[11]#######################################################月票卡
     * coefficient[12]######################################################银联二维码
     */
    private String[] coefficient;

    //备注（线路信息）
    private String chineseName;

    //是否支持微信支付
    private boolean isSuppScanPay = false;

    //是否支持银联卡支付
    private boolean isSuppUnionPay = false;

    //是否支付刷公交卡
    private boolean isSuppIcPay = false;

    //是否支持票价键盘
    private boolean isSuppKeyBoard = false;

    //是否是半价(招远键盘使用)
    private boolean halfPrice = false;

    //机构代码
    private String organization = "03754790";


    //递增流水号
    private int numSeq = 0;

    //补采flag
    private String flag = "0";

    //月票卡刷卡时间
    private String rmk1 = "0";

    //补采时间
    private String[] times = new String[]{"0", "0"};

    //补采总数
    private long SupplementaryMiningCn = 0;

    //招远月票卡允许使用的时间
    private String zy_month_enable_time;

    @Override
    public void loadFromPrefs(final int city, final String bin) {
        //pos基础参数
        initBase(bin);

        //初始化SN号
        initSn();

        //初始化折扣
        initDis();

        //读取配置参数
        config(city);

        //读取线路信息
        lineInfoEntity = DBManager.readLine();
        if (lineInfoEntity != null) {
            setZYMonthEnableTime(lineInfoEntity.getRmk1() != null ? lineInfoEntity.getRmk1() : "0");
        }
    }

    private void initBase(String bin) {
        basePrice = FetchAppConfig.getBasePrice();
        lineName = FetchAppConfig.getLineName();
        unitno = FetchAppConfig.unitno();
        lastVersion = FetchAppConfig.getLastVersion();
        binName = bin;
        lineNo = FetchAppConfig.getLineNo();
        bus_no = FetchAppConfig.getBusNo();
        driverNo = FetchAppConfig.getDriverNo();
        empNo = FetchAppConfig.getEmpNo();
        unitno = FetchAppConfig.unitno();
        numSeq = Integer.valueOf(FetchAppConfig.getNumSeq());
        chineseName = FetchAppConfig.chinese_name();
        lastBlackVersion = FetchAppConfig.getLastBlackVersion();

        lastParamsFileName = FetchAppConfig.getLastParamsFileName();

        zy_month_enable_time = FetchAppConfig.getZYmonthEnableTime();
    }

    private void config(int city) {
        String config = Util.readAssetsFile("config.json", BusApp.getInstance().getApplicationContext());
        ConfigParam configParam = new Gson().fromJson(config, ConfigParam.class);
        List<ConfigParam.ConfigBean> configList = configParam.getConfig();
        ConfigParam.ConfigBean configBean = configList.get(city);
        isSuppIcPay = configBean.isIs_supp_ic_pay();
        isSuppScanPay = configBean.isIs_supp_scan_pay();
        isSuppUnionPay = configBean.isIs_supp_union_pay();
        organization = configBean.getOrganization();
        app_id = configBean.getMch_id();
        cityCode = configBean.getCity_code();
        ftpIP = configBean.getIp();
        ftpPort = configBean.getPort();
        ftpUser = configBean.getUser();
        ftpPsw = configBean.getPsw();
        isSuppKeyBoard = configBean.isIs_supp_key_board();
        ftpEntity = new FTPEntity(ftpIP, ftpPort, ftpUser, ftpPsw);
    }

    private void initDis() {
        String dis = FetchAppConfig.coefficient();
        int len = dis.length() / 3;
        coefficient = new String[len];
        int index = 0;
        for (int i = 0; i < len; i++) {
            coefficient[i] = dis.substring(index, index += 3);
        }
        paymarkedPrice = string2Int(coefficient[8]) * basePrice / 100;
        unionPaymarkedPrice = string2Int(coefficient[9]) * basePrice / 100;
        unionScanPrice = string2Int(coefficient[12]) * basePrice / 100;
    }

    private void initSn() {
        String item;
        try {
            item = ModemToolTest.getItem(7);
        } catch (Exception e) {
            item = "default";
        }
        if (TextUtils.isEmpty(item)) {
            posSn = "default";
        } else {
            posSn = item;
        }
    }

    @Override
    public String getLineName() {
        return lineName;
    }

    @Override
    public void setLineName(String var1) {
        this.lineName = var1;
        CommonSharedPreferences.put("line_name", var1);
    }

    @Override
    public String getLineNo() {
        return lineNo;
    }

    @Override
    public void setLineNo(String var1) {
        this.lineNo = var1;
        CommonSharedPreferences.put("line_no", var1);
    }

    @Override
    public int getBasePrice() {
        return basePrice;
    }

    @Override
    public void setBasePrice(int var1) {
        this.basePrice = var1;
        CommonSharedPreferences.put("base_price", var1);
    }

    @Override
    public int getWcPayPrice() {
        return paymarkedPrice;
    }

    @Override
    public void setWcPrice(int var1) {
        this.paymarkedPrice = var1;
    }

    @Override
    public void setUnionPayPrice(int price) {
        this.unionPaymarkedPrice = price;
    }

    @Override
    public int getUnionPayPrice() {
        return unionPaymarkedPrice;
    }

    @Override
    public void setUnionScanPrice(int price) {
        this.unionScanPrice = price;
    }

    @Override
    public int getUnionScanPrice() {
        return unionScanPrice;
    }

    @Override
    public String getMac(String keyId) {
        return DBManager.getMac(keyId);
    }

    @Override
    public String getPublicKey(String keyId) {
        return DBManager.getPublicKey(keyId);
    }

    @Override
    public long getOrderTime() {
        return DateUtil.currentLong();
    }

    @Override
    public String getmchTrxId() {
        if (numSeq >= 99999999) {
            numSeq = 0;
        }
        numSeq++;
        String numSeqStr = Util.getNumSeq(numSeq);
        CommonSharedPreferences.put("num_seq", numSeqStr);

        return Util.Random(10) + numSeqStr;
    }

    @Override
    public String geCityCode() {
        return cityCode;
    }

    @Override
    public int getInStationId() {
        return 0;
    }

    @Override
    public String getInStationName() {
        return null;
    }

    @Override
    public String getChinese_name() {
        return chineseName;
    }

    @Override
    public void setChinese_name(String name) {
        this.chineseName = name;
        CommonSharedPreferences.put("chinese_name", name);
    }

    @Override
    public byte[] getKey() {
        return new byte[0];
    }

    @Override
    public void setKey(String privateKey) {

    }

    @Override
    public String getBusNo() {
        return bus_no;
    }

    @Override
    public void setBusNo(String bus_no) {
        this.bus_no = bus_no;
        CommonSharedPreferences.put("bus_no", bus_no);
    }

    @Override
    public String getAppId() {
        return app_id;
    }

    @Override
    public void setAppId(String app_id) {

    }

    @Override
    public String getCityCode() {
        return cityCode;
    }

    @Override
    public void setCityCode() {

    }

    @Override
    public void setUnitno(String unitno) {
        this.unitno = unitno;
        CommonSharedPreferences.put("unitno", unitno);
    }

    @Override
    public String getUnitno() {
        return unitno;
    }

    @Override
    public void setDriverNo(String no, String empNo) {
        this.driverNo = no;
        this.empNo = empNo;
        CommonSharedPreferences.put("driver_no", no);
        CommonSharedPreferences.put("emp_no", empNo);
    }

    @Override
    public String getDriverNo() {
//        return "00112234";
        return driverNo;
    }

    @Override
    public String getEmpNo() {
        return empNo;
    }

    @Override
    public String getPosSN() {
        return posSn;
    }

    @Override
    public LineInfoEntity getLineInfoEntity() {
        return lineInfoEntity;
    }

    @Override
    public void setLineInfoEntity() {
        lineInfoEntity = DBManager.readLine();
    }

    @Override
    public String getLastVersion() {
        return lastVersion;
    }

    @Override
    public void setLastVersion(String version) {
        this.lastVersion = version;
        CommonSharedPreferences.put("last_bin", version);
    }

    @Override
    public String getBinVersion() {
        return binName;
    }

    @Override
    public String[] getCoefficent() {
        return coefficient;
    }

    @Override
    public void setCoefficent(String coefficent) {
        coefficent = Util.addZeroRight(coefficent, 39);
        int len = coefficent.length() / 3;
        coefficient = new String[len];
        int index = 0;
        for (int i = 0; i < len; i++) {
            coefficient[i] = coefficent.substring(index, index += 3);
        }
        setWcPrice(string2Int(coefficient[8]) * basePrice / 100);
        setUnionPayPrice(string2Int(coefficient[9]) * basePrice / 100);
        SLog.d("PosManager(setCoefficent.java:501)coefficent=" + string2Int(coefficient[12]) + "     " + basePrice);
        setUnionScanPrice(string2Int(coefficient[12]) * basePrice / 100);
        CommonSharedPreferences.put("coefficient", coefficent);
//        090 090 090 090 100 100 100 100 090 090 100 001 100
//        095 050 000 100 050 100 100 100 100 100 000 001 100
        SLog.d("PosManager(setCoefficent.java:501)coefficent=" + coefficent);
    }

    @Override
    public void setBlackVersion(String version) {
        this.lastBlackVersion = version;
        CommonSharedPreferences.put("last_black_version", version);
    }

    @Override
    public String getBlackVersion() {
        return lastBlackVersion;
    }

    @Override
    public void setLastParamsFileName(String var) {
        this.lastParamsFileName = var;
        CommonSharedPreferences.put("last_params_file_name", var);
    }

    @Override
    public String getLastParamsFileName() {
        return lastParamsFileName;
    }

    @Override
    public void setFlag(String flag) {
        this.flag = flag;
    }

    @Override
    public String getFlag() {
        return flag;
    }

    @Override
    public void setSupMinTims(String[] times) {
        this.times = times;
    }

    @Override
    public String[] getTimes() {
        return times;
    }

    @Override
    public void setSupplementaryMiningCnt(long cnt) {
        this.SupplementaryMiningCn = cnt;
    }

    @Override
    public long getSupplementaryMiningCnt() {
        return SupplementaryMiningCn;
    }

    @Override
    public void setZYMonthEnableTime(String str) {
        this.zy_month_enable_time = str;
        CommonSharedPreferences.get("zy_month_enable_time", str);
    }

    @Override
    public String getZYMonthEnableTime() {
        return zy_month_enable_time;
    }

    @Override
    public void setZYMouthCardAllowUse(String rmk1) {

    }


    @Override
    public void setFtpIp(String ip) {
        this.ftpIP = ip;
        CommonSharedPreferences.put("ftp_ip", ip);
    }

    @Override
    public String getFtpIP() {
        return ftpIP;
    }

    @Override
    public void setPort(int port) {
        this.ftpPort = port;
        CommonSharedPreferences.put("ftp_port", port);
    }

    @Override
    public int getPort() {
        return ftpPort;
    }

    @Override
    public void setFtpPsw(String psw) {
        this.ftpPsw = psw;
        CommonSharedPreferences.put("ftp_psw", psw);
    }

    @Override
    public String getFtpPsw() {
        return ftpPsw;
    }

    @Override
    public void setFtpUser(String user) {
        this.ftpUser = user;
        CommonSharedPreferences.put("ftp_user", user);
    }

    @Override
    public String getFtpUser() {
        return ftpUser;
    }

    @Override
    public void setUrlIp(String ip) {

    }

    @Override
    public String getUrlIp() {
        return null;
    }

    @Override
    public void setFTP(FTPEntity ftp) {
        this.ftpEntity = ftp;
        CommonSharedPreferences.put("ftp_ip", ftp.getI());
        CommonSharedPreferences.put("ftp_user", ftp.getU());
        CommonSharedPreferences.put("ftp_psw", ftp.getPsw());
        CommonSharedPreferences.put("ftp_port", ftp.getP());
    }

    @Override
    public FTPEntity getFTP() {
        return ftpEntity;
    }

    @Override
    public boolean isSuppScanPay() {
        return isSuppScanPay;
    }

    @Override
    public boolean isSuppIcPay() {
        return isSuppIcPay;
    }

    @Override
    public boolean isSuppUnionPay() {
        return isSuppUnionPay;
    }

    @Override
    public boolean isHalfPrices() {
        return halfPrice;
    }

    @Override
    public void setHalfPrices(boolean half) {
        this.halfPrice = half;
    }

    @Override
    public boolean isSuppKeyBoard() {
        return isSuppKeyBoard;
    }

    public String getOrganization() {
        return organization;
    }

}
