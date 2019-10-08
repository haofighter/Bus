package com.szxb.unionpay.unionutil;

import java.text.DecimalFormat;

/**
 * 作者：Tangren on 2018-07-07
 * 包名：com.szxb.unionpay.unionutil
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class HexUtil {
    /**
     * 求长度用的 int转byte[]
     *
     * @param value
     * @param len
     * @return
     */
    public static byte[] int2Bytes(int value, int len) {
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            b[len - i - 1] = (byte) ((value >> 8 * i) & 0xff);
        }
        return b;
    }


    /**
     * byte 转hex
     *
     * @param src
     * @return
     */
    public static String bytesToHexString(byte[] src) {
        StringBuilder stringBuilder = new StringBuilder("");
        if (src == null || src.length <= 0) {
            return null;
        }
        for (byte aSrc : src) {
            int v = aSrc & 0xFF;
            String hv = Integer.toHexString(v);
            if (hv.length() < 2) {
                stringBuilder.append(0);
            }
            stringBuilder.append(hv);
        }
        return stringBuilder.toString();
    }

    /**
     * 十六进制串转化为byte数组
     *
     * @return the array of byte
     */
    public static byte[] hex2byte(String hex)
            throws IllegalArgumentException {
        if (hex.length() % 2 != 0) {
            throw new IllegalArgumentException();
        }
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap = "" + arr[i++] + arr[i];
            int byteint = Integer.parseInt(swap, 16) & 0xFF;
            b[j] = Integer.valueOf(byteint).byteValue();
        }
        return b;
    }


    public static String yuan2Fen(String var) {
        try {
            int i = Integer.valueOf(var);
            DecimalFormat format = new DecimalFormat("0.00");
            return format.format((float) i / (float) 100);
        } catch (Exception e) {
            e.printStackTrace();
            return "错误";
        }
    }
}
