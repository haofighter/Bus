package com.szxb.buspay.db.entity.bean.card;

import com.szxb.buspay.util.HexUtil;

import static java.lang.System.arraycopy;

/**
 * 作者：Tangren on 2018-07-19
 * 包名：com.szxb.buspay.db.entity.bean.card
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class SearchCard {
    //状态：00为寻到卡
    public String status;
    //城市代码
    public String cityCode;
    //卡号
    public String cardNo;
    //卡消费类型
    public String cardType;
    //卡模块类型CPU/M1
    public String cardModuleType;

    //方向
    public String direction;
    //站点
    public String stationID;
    //上下车标志
    public String upDnFlag;
    //银联AID长度
    public int aidLength;
    //AID
    public String aid;
    //交通部返回码
    public String errorCode;
    //发卡机构
    public String company;
    //白名单标示
    public String CardTag;

    public SearchCard(byte[] datas) {
        int index = 0;
        byte[] status_byte = new byte[1];
        arraycopy(datas, index, status_byte, 0, status_byte.length);
        status = HexUtil.printHexBinary(status_byte);

        byte[] city_code_byte = new byte[4];
        arraycopy(datas, index += status_byte.length, city_code_byte, 0, city_code_byte.length);
        cityCode = HexUtil.printHexBinary(city_code_byte);

        byte[] card_no_byte = new byte[4];
        arraycopy(datas, index += city_code_byte.length, card_no_byte, 0, card_no_byte.length);
        cardNo = HexUtil.printHexBinary(card_no_byte);

        byte[] card_type_byte = new byte[1];
        arraycopy(datas, index += card_no_byte.length, card_type_byte, 0, card_type_byte.length);
        cardType = HexUtil.printHexBinary(card_type_byte);

        byte[] card_module_type_byte = new byte[1];
        arraycopy(datas, index + card_type_byte.length, card_module_type_byte, 0, card_module_type_byte.length);
        cardModuleType = HexUtil.printHexBinary(card_module_type_byte);

//        00A00000033301010100F000000008A0000003330101010000000000000000000000000000000000

        index += 5;
        byte[] aidLen = new byte[1];
        arraycopy(datas, index, aidLen, 0, aidLen.length);
        aidLength = Integer.valueOf(HexUtil.printHexBinary(aidLen));

        byte[] aid_byte = new byte[aidLength];
        arraycopy(datas, index + aidLen.length, aid_byte, 0, aid_byte.length);
        aid = HexUtil.printHexBinary(aid_byte);

        byte[] error_code = new byte[2];
        arraycopy(datas, 31, error_code, 0, error_code.length);
        errorCode = HexUtil.printHexBinary(error_code);

        byte[] companybyte = new byte[4];
        arraycopy(datas, 33, companybyte, 0, companybyte.length);
        company = HexUtil.printHexBinary(companybyte);

        byte[] cardTag = new byte[4];
        arraycopy(datas, 37, cardTag, 0, cardTag.length);
        CardTag = HexUtil.printHexBinary(cardTag);
    }

    @Override
    public String toString() {
        return "SearchCard[" +
                "状态='" + status + '\'' +
                ", 城市代码='" + cityCode + '\'' +
                ", 卡号='" + cardNo + '\'' +
                ", 卡类型='" + cardType + '\'' +
                ", 卡结构类型='" + cardModuleType + '\'' +
                ", AID长度='" + aidLength + '\'' +
                ", AID='" + aid + '\'' +
                ", errorCode='" + errorCode + '\'' +
                ", company='" + company + '\'' +
                ", CardTag='" + CardTag + '\'' +
                ']';
    }
}
