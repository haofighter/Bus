package com.szxb.buspay.util;

import android.os.Environment;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.szxb.buspay.BusApp;
import com.szxb.buspay.db.entity.bean.QRCode;
import com.szxb.buspay.db.entity.bean.QRScanMessage;
import com.szxb.buspay.db.entity.card.LineInfoEntity;
import com.szxb.buspay.db.entity.scan.PosRecord;
import com.szxb.buspay.util.rx.RxBus;
import com.szxb.buspay.util.sound.SoundPoolUtil;
import com.szxb.jni.libszxb;
import com.szxb.mlog.SLog;

import java.io.ByteArrayOutputStream;
import java.io.File;

import static java.lang.System.arraycopy;

/**
 * 作者：Tangren on 2018-07-17
 * 包名：com.szxb.buspay.util
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class HexUtil {

    /**
     * byte 2 hex
     *
     * @param data .
     * @return .
     */
    public static String printHexBinary(byte[] data) {
        StringBuilder r = new StringBuilder(data.length * 2);
        for (byte b : data) {
            r.append(hexCode[(b >> 4) & 0xF]);
            r.append(hexCode[(b & 0xF)]);
        }
        return r.toString();
    }

    private static final char[] hexCode = "0123456789ABCDEF".toCharArray();


    public static byte[] int2Bytes(int value, int len) {
        byte[] b = new byte[len];
        for (int i = 0; i < len; i++) {
            b[len - i - 1] = (byte) ((value >> 8 * i) & 0xff);
        }
        return b;
    }

    /**
     * 检查是否有文件
     *
     * @param name .
     * @return .
     */

    public static boolean check(String name) {
        String path = Environment.getExternalStorageDirectory() + "/" + name;
        File file = new File(path);
        //文件是否存在
        return file.exists();
    }

    /**
     * 根据线路号下载更新线路文件
     *
     * @param lineName .
     * @return .
     */
    public static String fileName(String lineName) {
        try {
            String start = lineName.substring(0, 2);
            String end = lineName.substring(2, 4);
            int i = Integer.parseInt(start, 16);
            int i1 = Integer.parseInt(end, 16);
            return i + "," + i1 + ".json";
        } catch (Exception e) {
            return "0";
        }
    }

    /**
     * 合并byte数组
     *
     * @param datas .
     * @return .
     */
    public static byte[] mergeByte(byte[]... datas) {
        int length = 0;
        byte[] endData = new byte[2048];
        for (byte[] data : datas) {
            arraycopy(data, 0, endData, length, data.length);
            length += data.length;
        }
        byte[] data = new byte[length];
        arraycopy(endData, 0, data, 0, length);
        return data;
    }


    /**
     * 10进制串转为BCD码<br/>
     *
     * @param data 10进制串
     * @return byte[] BCD码
     */
    public static byte[] str2Bcd(String data) {
        if (data.length() == 0) {
            return new byte[0];
        }

        String str = data;
        // 奇数个数字需左补零
        if (str.length() % 2 != 0) {
            str = "0" + str;
        }
        ByteArrayOutputStream baOs = new ByteArrayOutputStream();
        char[] cs = str.toCharArray();
        for (int i = 0; i < cs.length; i += 2) {
            int high = cs[i] - 48;
            int low = cs[i + 1] - 48;
            baOs.write(high << 4 | low);
        }
        return baOs.toByteArray();
    }

    /**
     * byte[]bcd 转str
     *
     * @param bytes .
     * @return .
     */
    public static String bcd2Str(byte[] bytes) {
        if (bytes.length == 0) {
            return "00";
        }
        StringBuilder sb = new StringBuilder();
        for (byte aByte : bytes) {
            int h = ((aByte & 0xff) >> 4) + 48;
            sb.append((char) h);
            int l = (aByte & 0x0f) + 48;
            sb.append((char) l);
        }
        return sb.toString();
    }

    /**
     * 十六进制串转化为byte数组
     *
     * @return the array of byte
     */
    public static byte[] hex2byte(String hex)
            throws IllegalArgumentException {
        if (hex.length() % 2 != 0) {
            hex = hex + "0";
        }
        char[] arr = hex.toCharArray();
        byte[] b = new byte[hex.length() / 2];
        for (int i = 0, j = 0, l = hex.length(); i < l; i++, j++) {
            String swap = "" + arr[i++] + arr[i];
            int byteInt = Integer.parseInt(swap, 16) & 0xFF;
            b[j] = Integer.valueOf(byteInt).byteValue();
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


    public static String convertHexToString(String hex) {
        StringBuilder sb = new StringBuilder();
        StringBuilder temp = new StringBuilder();
        for (int i = 0; i < hex.length() - 1; i += 2) {
            String output = hex.substring(i, (i + 2));
            int decimal = Integer.parseInt(output, 16);
            sb.append((char) decimal);
            temp.append(decimal);
        }

        return sb.toString();
    }

    /**
     * 票价键盘回显
     *
     * @param str .
     */
    public static void sendBackToKeyBoard(String str) {
        try {
            str = Util.addZeroForNum(str, 3);
            byte[] by = str.getBytes();
            byte[] sendoffs = new byte[20];
            sendoffs[0] = (byte) 0xaa;
            sendoffs[1] = (byte) 0xbb;
            sendoffs[2] = (byte) 0x00;
            sendoffs[3] = (byte) 0x03;
            sendoffs[4] = (byte) 0x08;
            sendoffs[5] = (byte) 0x02;
            sendoffs[6] = by[0];
            sendoffs[7] = by[1];
            sendoffs[8] = by[2];
            sendoffs[9] = (byte) 0x00;
            sendoffs[10] = (byte) 0x00;

            libszxb.deviceSerialSend(3, sendoffs, 11);
        } catch (Exception e) {
            e.printStackTrace();
            SLog.e("HexUtil(sendBackToKeyBoard.java:220)" + Util.getExectionStr(e));
        }
    }

    public static void parseLine(LineInfoEntity onLineInfo) {
        parseLine(onLineInfo, null);
    }

    public static void parseLine(LineInfoEntity onLineInfo, String busNo) {
        BusApp.getPosManager().setChinese_name(onLineInfo.getChinese_name());
        BusApp.getPosManager().setBasePrice(Util.string2Int(onLineInfo.getFixed_price()));
        BusApp.getPosManager().setLineName(onLineInfo.getChinese_name());
        BusApp.getPosManager().setCoefficent(onLineInfo.getCoefficient());
        BusApp.getPosManager().setZYMonthEnableTime(onLineInfo.getRmk1());
        String lineNo = onLineInfo.getLine();
        BusApp.getPosManager().setLineNo(lineNo);
        if (lineNo.length() >= 2) {
            BusApp.getPosManager().setUnitno(onLineInfo.getLine().substring(0, 2));
        }
        if (busNo != null) {
            BusApp.getPosManager().setBusNo(busNo);
        }
        if (AppUtil.isForeground("com.szxb.buspay.MainActivity")) {
            SLog.d("HexUtil(parseLine.java:252)Rx=" + RxBus.getInstance().hasObservers());
            SoundPoolUtil.play(Config.IC_SET);
            RxBus.getInstance().send(new QRScanMessage(new PosRecord(), QRCode.REFRESH_VIEW));
        }
    }

    /**
     * @param version ftp黑名单版本
     * @return 是否需要更新 true 更新，否则不更新
     */
    public static boolean checkBlackVersion(String version) {
        String lastBlackVersion = BusApp.getPosManager().getBlackVersion();
        return !TextUtils.equals(lastBlackVersion, version);
    }


    /**
     * 格式转换
     *
     * @param data .
     */
    public static JSONObject parseObject(byte[] data) {
        if (data == null) {
            return null;
        }
        try {
            return JSONObject.parseObject(new String(data, "GB2312"));
        } catch (Exception e) {
            e.printStackTrace();
            SLog.d("Util(parseObject.java:386)不支持的编码格式" + e.toString());
        }
        return null;
    }
}
