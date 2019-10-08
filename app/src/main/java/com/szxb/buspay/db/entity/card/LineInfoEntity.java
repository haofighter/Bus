package com.szxb.buspay.db.entity.card;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Unique;

/**
 * 作者：Tangren on 2018-07-18
 * 包名：com.szxb.buspay.db.entity.card
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */
@Entity
public class LineInfoEntity {

    /**
     * line : 0501
     * lineFileName : 005001
     * version : 20170921120802
     * up_station : 17
     * down_station : 17
     * chinese_name : 威海荣成测试线路
     * is_fixed_price : 0
     * is_keyboard : 1
     * fixed_price : 600
     * coefficient : 100100100100100100100100
     * shortcut_price : 010002000300040005000600070008000900
     */

    @Id(autoincrement = true)
    private Long id;
    @Unique
    private String line;   //线路号
    private String lineFileName;   //线路文件名
    private String version;  //版本号
    private String up_station;//线路上行站点数量
    private String down_station;//线路下行站点数量
    private String chinese_name;//线路名称
    private String is_fixed_price;//是否为固定票价线路,1=是, 其他=不是
    private String is_keyboard;//是否支持键盘，0为不支持，1为支持，当线路为非单一票价并且不支持键盘时，下面的站点经纬度和票价表生效，当线路为非单一票价并且支持键盘时，下面键盘快捷价格生效
    private String fixed_price;//如果是固定票价线路,此项才有意义,是票价,单位分
    private String coefficient;//8种卡折扣率，依次为普通卡、学生卡、老年卡、免费卡、员工卡、优惠卡1、优惠卡2、优惠卡3、微信支付
    private String shortcut_price;//快捷票价,通过小键盘按键输入，数字1-9，直接代表金额，以分为单位。
    private String fileName;//文件名
    private String rmk1;//month_enable_time 月票支持日期
    private String rmk2;
    private String rmk3;

    @Generated(hash = 1116727395)
    public LineInfoEntity(Long id, String line, String lineFileName, String version, String up_station, String down_station,
            String chinese_name, String is_fixed_price, String is_keyboard, String fixed_price, String coefficient,
            String shortcut_price, String fileName, String rmk1, String rmk2, String rmk3) {
        this.id = id;
        this.line = line;
        this.lineFileName = lineFileName;
        this.version = version;
        this.up_station = up_station;
        this.down_station = down_station;
        this.chinese_name = chinese_name;
        this.is_fixed_price = is_fixed_price;
        this.is_keyboard = is_keyboard;
        this.fixed_price = fixed_price;
        this.coefficient = coefficient;
        this.shortcut_price = shortcut_price;
        this.fileName = fileName;
        this.rmk1 = rmk1;
        this.rmk2 = rmk2;
        this.rmk3 = rmk3;
    }

    @Generated(hash = 739564802)
    public LineInfoEntity() {
    }

    public Long getId() {
        return this.id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLine() {
        return this.line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public String getVersion() {
        return this.version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUp_station() {
        return this.up_station;
    }

    public void setUp_station(String up_station) {
        this.up_station = up_station;
    }

    public String getDown_station() {
        return this.down_station;
    }

    public void setDown_station(String down_station) {
        this.down_station = down_station;
    }

    public String getChinese_name() {
        return this.chinese_name;
    }

    public void setChinese_name(String chinese_name) {
        this.chinese_name = chinese_name;
    }

    public String getIs_fixed_price() {
        return this.is_fixed_price;
    }

    public void setIs_fixed_price(String is_fixed_price) {
        this.is_fixed_price = is_fixed_price;
    }

    public String getIs_keyboard() {
        return this.is_keyboard;
    }

    public void setIs_keyboard(String is_keyboard) {
        this.is_keyboard = is_keyboard;
    }

    public String getFixed_price() {
        return this.fixed_price;
    }

    public void setFixed_price(String fixed_price) {
        this.fixed_price = fixed_price;
    }

    public String getCoefficient() {
        return this.coefficient;
    }

    public void setCoefficient(String coefficient) {
        this.coefficient = coefficient;
    }

    public String getShortcut_price() {
        return this.shortcut_price;
    }

    public void setShortcut_price(String shortcut_price) {
        this.shortcut_price = shortcut_price;
    }

    public String getFileName() {
        return this.fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getRmk1() {
        return this.rmk1;
    }

    public void setRmk1(String rmk1) {
        this.rmk1 = rmk1;
    }

    public String getRmk2() {
        return this.rmk2;
    }

    public void setRmk2(String rmk2) {
        this.rmk2 = rmk2;
    }

    public String getRmk3() {
        return this.rmk3;
    }

    public void setRmk3(String rmk3) {
        this.rmk3 = rmk3;
    }

    public String getLineFileName() {
        return this.lineFileName;
    }

    public void setLineFileName(String lineFileName) {
        this.lineFileName = lineFileName;
    }

}
