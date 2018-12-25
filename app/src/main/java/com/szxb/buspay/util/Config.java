package com.szxb.buspay.util;

/**
 * 作者：Tangren on 2018-07-18
 * 包名：com.szxb.buspay.util
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class Config {

    //私钥
    public static final String private_key = "MIICXAIBAAKBgQCrnFUPueWNY3HLcVf55kXzJDb+ftYINmhde+4EMbKjPY38xaZQ\n" +
            "k+OjeXykbo8XgIi/xBpRvogWyOwZKOr4kdnV/PdLSoXCrr3DoTRU9INFiOKZPxFY" +
            "8nYmH6KI4c/z5ooeats8+1bwN5lZdXwXWL/MJA7JrSSSUt0qCwy9MI7+OQIDAQAB" +
            "AoGAIL37HL0DJy7KD17Ywj1FK1bFh1j7zSVUVEHI79PrmWmtJYUwbj9JN29+cIEH" +
            "nBxR+wSXYPFRVceQBFziN/rb7MAS0qNmBxcSzJfqjenoHPZa9smZXpX6W1zHuFTd" +
            "IloV8juM7ssQyRNRNLSIDs2zZBNXHV6eDqW0cdIJuWaKyYECQQDTkZpgv6531pby" +
            "trtWrdgIIjC55YsLZKWv3VqCfvHbhodETA+1EL9y/BV0F0yXE8oDlMbIR99uuU4X" +
            "24/q93mlAkEAz6Z+1KGqy2twmQ1JRO/8B4zfqgUlitYu41dWu+iHDfTC2ex84BRQ" +
            "dXVND2HGiz/fRB3yubc/WAnToLv/kdTGBQJAcDQnQKpH2CyJj52Ty0uVZ/LiDqUL" +
            "UfaF3LgzWUQD9t3o/TKtneSM9Gl240O8Dd+j4rRTnEJp3+oM3aBHOmEXNQJBAJR5" +
            "K/7FieXhcKU/BsCwB7kuVU6wV2OqOeR8Mpwxaz/jXt+LZM6kN9OEiBETjG9MwEto" +
            "ToHUMQq2HAe15MtVJDECQF7lh83AMlL31AtdmFkaHvqu8vrwYiDwqlam+dGADWPG" +
            "+Cpn7fcXw0wBqRLLMLymz6IAp2mJCN+N7W76j8GP08E=";


    private static final String IP = "http://111.230.85.238";//139.199.158.253
//    private static final String IP = "http://112.74.102.125";
//    private static final String IP = "http://2t183d9338.iask.in:27781";

    //小兵mac
    public static final String MAC_KEY = IP + "/bipbus/interaction/getmackey";

    //小兵公钥
    public static final String PUBLIC_KEY = IP + "/bipbus/interaction/getpubkey";

    //小兵黑名单
    public static final String BLACK_LIST = IP + "/bipbus/interaction/blacklist";

    //小兵腾讯支付
    public static final String XBPAY = IP + "/bipbus/interaction/posrecv";

    //IC卡上传
    public static final String IC_CARD_RECORD = IP + "/bipbus/interaction/carduploadzb";

    //银联卡记录
    public static final String UNION_CARD_RECORD = IP + "/bipbus/interaction/bankjourAll";

    //校准时间
    public static final String REG_TIME_URL = "http://134.175.56.14/bipeqt/interaction/getStandardTime";


    public static final int SCAN_SUCCESS = 1;//扫码成功

    public static final int EC_RE_QR_CODE = SCAN_SUCCESS + 1;//请刷新二维码

    public static final int EC_FEE = EC_RE_QR_CODE + 1;//超出最大金额

    public static final int VERIFY_FAIL = EC_FEE + 1;//.验码失败

    public static final int EC_BALANCE = VERIFY_FAIL + 1;//.余额不足

    public static final int IC_BASE = EC_BALANCE + 1;//.铛

    public static final int IC_BASE2 = IC_BASE + 1;//.铛铛

    public static final int IC_DIS = IC_BASE2 + 1;//.优惠卡

    public static final int IC_EMP = IC_DIS + 1;//.员工卡

    public static final int IC_BLOOD = IC_EMP + 1;//.无偿献血卡

    public static final int IC_FREE = IC_BLOOD + 1;//.免费卡

    public static final int IC_HONOR = IC_FREE + 1;//.荣军卡

    public static final int IC_OLD = IC_HONOR + 1;//.老年卡

    public static final int IC_STUDENT = IC_OLD + 1;//.学生卡

    public static final int IC_INVALID = IC_STUDENT + 1;//.卡失效

    public static final int IC_LOVE = IC_INVALID + 1;//.爱心卡

    public static final int IC_TO_WORK = IC_LOVE + 1;//.上班

    public static final int IC_OFF_WORK = IC_TO_WORK + 1;//.下班

    public static final int IC_PUSH_MONEY = IC_OFF_WORK + 1;//请投币

    public static final int IC_RE = IC_PUSH_MONEY + 1;//重新刷卡

    public static final int IC_YEARLY = IC_RE + 1;//请年检

    public static final int QR_ERROR = IC_YEARLY + 1;//.二维码有误

    public static final int IC_RECHARGE = QR_ERROR + 1;//.请充值

    public static final int IC_DEFECT = IC_RECHARGE + 1;//.优抚卡

    public static final int IC_MANAGER = IC_DEFECT + 1;//.管理卡

    public static final int IC_MONTH = IC_MANAGER + 1;//.月票卡

    public static final int IC_SET = IC_MONTH + 1;//.设置音

    public static final int IC_LLLEGAL = IC_SET + 1;//.非法卡

    public static final int IC_ERROR = IC_LLLEGAL + 1;//错误

    public static final int IC_HIGHT_CARD = IC_ERROR + 1;//.高中卡

    public static final int IC_YINLIAN_CARD = IC_HIGHT_CARD + 1;//银联卡

    public static final int WANGLUOYICAHNG = IC_YINLIAN_CARD + 1;//网络异常

    public static final int WU_XIAO_KA = WANGLUOYICAHNG + 1;//无效卡

    public static final int LAORENKA_T = WU_XIAO_KA + 1;//老人卡

    public static final int JUNRENKA_T = LAORENKA_T + 1;//军人卡

    public static final int GUANAIKA_T = LAORENKA_T + 1;//关爱卡


    //菜单
    //公交卡刷卡
    public static final int POSITION_BUS_RECORD = 0;

    //扫码记录
    public static final int POSITION_SCAN_RECORD = POSITION_BUS_RECORD + 1;

    //银联卡记录
    public static final int POSITION_UNION_RECORD = POSITION_SCAN_RECORD + 1;

    //汇总
    public static final int POSITION_CNT = POSITION_UNION_RECORD + 1;

    //更新参数
    public static final int POSITION_UPDATE_PARAMS = POSITION_CNT + 1;

    //数据库导出
    public static final int POSITION_EXPORT_DB = POSITION_UPDATE_PARAMS + 1;

    //日志导出
    public static final int POSITION_EXPORT_LOG = POSITION_EXPORT_DB + 1;

    //检测网络
    public static final int POSITION_CHECK_NET = POSITION_EXPORT_LOG + 1;

    //校准时间
    public static final int POSITION_TIME = POSITION_CHECK_NET + 1;

    //导出7天记录
    public static final int POSITION_EXPORT_7 = POSITION_TIME + 1;

    //导出1个月记录
    public static final int POSITION_EXPORT_1_M = POSITION_EXPORT_7 + 1;

    //导出3个月记录
    public static final int POSITION_EXPORT_3_M = POSITION_EXPORT_1_M + 1;

    //查看参数信息
    public static final int POSITION_READ_PARAM = POSITION_EXPORT_3_M + 1;

    //补传
    public static final int POSITION_PUSH_FILL = POSITION_READ_PARAM + 1;

    //下载银联参数
    public static final int POSITION_DN_UNION_PARAMS = POSITION_PUSH_FILL + 1;

    public static String tip() {
        return tip[(int) (Math.random() * 20)];
    }

    //交通提示语
    private static String[] tip = new String[]{
            "安全靠着你我他，和谐交通靠大家！",
            "你争我抢道路窄，互谦互让心路宽！",
            "与文明一起上路，伴平安一起回家！",
            "人人需要文明交通，交通需要人人文明！",
            "用礼让传递文明，用安全构筑和谐！",
            "出行因礼让而畅通，道路因畅通而和谐！",
            "一路文明一路情，用安全构筑和谐！",
            "带十分小心上路，携一份平安回家！",
            "出行多点小心，家人少点担心！",
            "冒险是事故之根，谨慎为安全之本！",
            "文明在于一言一行，安全源于一点一滴",
            "创优良交通秩序，闪精神文明之光！",
            "交通安全系万家，平平安安是幸福！",
            "安全开车是大事，文明走路非小节！",
            "红灯常在心中亮，绿灯才能伴一生！",
            "树立安全第一的思想，落实预防为主的方针",
            "多一分麻痹，多一分危险。多一些谨慎，多一些安全！",
            "交通安全系万家，平平安安是幸福！",
            "创优良交通秩序，闪精神文明之光！",
            "文明在于一言一行，安全源于一点一滴！"
    };

    //银联卡
    public static final int PAY_TYPE_BANK_IC = "BANK_IC".hashCode();
    //银联二维码
    public static final int PAY_TYPE_BANK_QR = "BANK_QR".hashCode();

}
