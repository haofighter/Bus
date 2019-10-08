/**
 * Project Name:Q6
 * File Name:libtest.java
 * Package Name:com.szxb.jni
 * Date:Apr 13, 20177:37:45 PM
 * Copyright (c) 2017, chenzhou1025@126.com All Rights Reserved.
 */

package com.szxb.jni;


import android.util.Log;

/**
 * ClassName:libtest <br/>
 * Function: ADD FUNCTION. <br/>
 * Reason:ADD REASON. <br/>
 * Date: Apr 13, 2017 7:37:45 PM <br/>
 *
 * @author lilei
 * @see
 * @since JDK 1.6
 */
public class Alipay {

    // Used to load the 'native-lib' library on application startup.
    static {
        try {
            System.loadLibrary("alipay-lib");
        } catch (Throwable e) {
            Log.e("Alipay", "alipay-lib");
        }

    }

    public static final int SUCCESS = 1;
    public static final int MALFORMED_QRCODE = -1;
    public static final int QRCODE_KEY_EXPIRED = -3;
    public static final int POS_PARAM_ERROR = -4;
    public static final int QUOTA_EXCEEDED = -5;
    public static final int NO_ENOUGH_MEMORY = -6;
    public static final int SYSTEM_ERROR = -7;
    public static final int CARDTYPE_UNSUPPORTED = -8;
    public static final int NOT_INITIALIZED = -9;
    public static final int ILLEGAL_PARAM = -10;
    public static final int PROTO_UNSUPPORTED = -11;
    public static final int QRCODE_DUPLICATED = -12;

    public class VERIFY_REQUEST_V2 {
        byte[] qrcode;
        int qrcode_len;
        String pos_param;
        int amount_cent;

        public VERIFY_REQUEST_V2(byte[] qrcode, int qrcode_len, String pos_param, int amount_cent) {
            this.qrcode = qrcode;
            this.qrcode_len = qrcode_len;
            this.pos_param = pos_param;
            this.amount_cent = amount_cent;
        }
    }

    public class VERIFY_RESPONSE_V2 {
        int nRet;
        byte[] uid;
        byte[] record;
        byte[] card_no;
        byte[] card_data;
        byte[] card_type;

        public VERIFY_RESPONSE_V2(int nRet, byte[] uid, byte[] record, byte[] card_no, byte[] card_data, byte[] card_type) {
            this.nRet = nRet;
            this.uid = uid;
            this.record = record;
            this.card_no = card_no;
            this.card_data = card_data;
            this.card_type = card_type;
        }

        public int getnRet() {
            return nRet;
        }

        public void setnRet(int nRet) {
            this.nRet = nRet;
        }

        public byte[] getUid() {
            return uid;
        }

        public void setUid(byte[] uid) {
            this.uid = uid;
        }

        public byte[] getRecord() {
            return record;
        }

        public void setRecord(byte[] record) {
            this.record = record;
        }

        public byte[] getCard_no() {
            return card_no;
        }

        public void setCard_no(byte[] card_no) {
            this.card_no = card_no;
        }

        public byte[] getCard_data() {
            return card_data;
        }

        public void setCard_data(byte[] card_data) {
            this.card_data = card_data;
        }

        public byte[] getCard_type() {
            return card_type;
        }

        public void setCard_type(byte[] card_type) {
            this.card_type = card_type;
        }
    }

    public static native int init_pos_verify(String key_list, String card_type_list);

    public static native VERIFY_RESPONSE_V2 verify_qrcode_v2(VERIFY_REQUEST_V2 QR_Request);


}
