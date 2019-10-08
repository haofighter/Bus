package com.tencent.wlxsdk;

import android.text.TextUtils;

/**
 * 作者: Tangren on 2017-09-13
 * 包名：com.tencent.wlxsdk
 * 邮箱：996489865@qq.com
 * TODO:腾讯jni
 */

public class WlxSdk {
    private static final int ENTER = 1;
    private static final int EXIT = 2;
    private byte[] record = null;
    private byte[] open_id = null;
    private int key_id = -1;
    private byte[] ver_info = null;
    private byte[] mac_root_id = null;
    private String qrcode;

    public WlxSdk() {
    }

    private native int getQrCodeElemets(String var1);

    private native int verifyQrCode(String var1, String var2, String var3, int var4, byte var5, byte var6, String var7, String var8, String var9);

    private native int getVersionInfo();

    public int init(String qrcode) {
        if (TextUtils.isEmpty(qrcode)) {
            return -1;
        } else {
            this.qrcode = qrcode;
            return this.getQrCodeElemets(qrcode);
        }
    }

    public int get_key_id() {
        return this.key_id;
    }

    public String get_mac_root_id() {
        return this.mac_root_id != null ? new String(this.mac_root_id) : "";
    }

    public String get_open_id() {
        return this.open_id != null ? new String(this.open_id) : "";
    }

    public int verify(String open_id, String pub_key, int payfee, byte scene, byte scantype, String pos_id, String pos_trx_id, String aes_mac_root) {
        return !TextUtils.isEmpty(this.qrcode) && !TextUtils.isEmpty(open_id) && !TextUtils.isEmpty(pub_key) && payfee >= 0 && (scantype == 1 || scantype == 2) ? this.verifyQrCode(this.qrcode, open_id, pub_key, payfee, scene, scantype, pos_id, pos_trx_id, aes_mac_root) : -1;
    }

    public String get_record() {
        return this.record != null ? new String(this.record) : "";
    }

    public String getVerInfo() {
        return 0 == this.getVersionInfo() ? (this.ver_info != null ? new String(this.ver_info) : "") : "";
    }

    static {
        System.loadLibrary("wlxsdkcore");
    }
}
