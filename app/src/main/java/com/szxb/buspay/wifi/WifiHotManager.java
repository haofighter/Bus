package com.szxb.buspay.wifi;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * 作者：Tangren on 2019-02-18
 * 包名：com.szxb.wifihot
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */
public class WifiHotManager {


    public static final int NO_PASS = 0;
    public static final int WPA_PSK = 1;
    public static final int WPA2_PSK = 2;

    private WifiManager wifiManager;
    private ConnectivityManager connectivityManager;
    private WifiConfiguration apconfig;

    public WifiHotManager(Context context) {
        wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    //开启热点
    public boolean openwifiap(String name, String password, int type) {
        apconfig = new WifiConfiguration();
        apconfig.SSID = name;//设置WiFi名字

        //热点相关设置
        switch (type) {
            case NO_PASS:
                apconfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                apconfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                apconfig.wepKeys[0] = "";
                apconfig.wepTxKeyIndex = 0;
                break;
            case WPA_PSK:
                apconfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                apconfig.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
                apconfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                apconfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                apconfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                apconfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                apconfig.preSharedKey = password;
                break;
            case WPA2_PSK:
                //由于wpa2是不能直接访问的，但是KeyMgmt中却有。所以我们这样写
                for (int i = 0; i < WifiConfiguration.KeyMgmt.strings.length; i++) {
                    if ("WPA2_PSK".equals(WifiConfiguration.KeyMgmt.strings[i])) {
                        apconfig.allowedKeyManagement.set(i);//直接给它赋索引的值
                        Log.e("wpa2索引", String.valueOf(i));//不同手机索引不同
                    }
                }
                apconfig.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
                apconfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                apconfig.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                apconfig.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
                apconfig.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
                apconfig.preSharedKey = password;
                break;
        }

        try {
            if (isWifiEnabled()) {
                wifiManager.setWifiEnabled(false);
            }

            // 【注意：如果已经打开ap热点，名称和密码不一致，则需要先关闭AP热点，重新设置AP配置，然后再次打开AP热点】
            // 2.2.关闭ap热点
            if (isWifiApEnabled()) {
                setWifiApEnabled(apconfig, false);
            }

            // 2.3.重新设置ap配置
            setWifiApConfiguration(apconfig);

            // 2.4.打开ap热点
            setWifiApEnabled(apconfig, true);

            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
//            setWifiApConfiguration(apconfig);
            return (boolean) method.invoke(wifiManager, apconfig, true);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return false;
    }

    private boolean setWifiApEnabled(WifiConfiguration wifiConfig, boolean enabled) {
        try {
            // TODO WifiManager.setWifiApEnabled()已经被废弃；在android 7以上，需要替换为ConnectivityManager.startTethering()
            Method method = null;
            if (Build.VERSION.SDK_INT >= 26) {
//            method = connectivityManager.getClass().getMethod("startTethering",WifiConfiguration.class, Boolean.TYPE);
            } else {
                method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, Boolean.TYPE);
            }
            return (Boolean) method.invoke(wifiManager, wifiConfig, enabled);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    private boolean isWifiEnabled() {
        return wifiManager.isWifiEnabled();
    }


    private boolean setWifiApConfiguration(WifiConfiguration wifiConfig) {
        try {
            Method method = wifiManager.getClass().getMethod("setWifiApConfiguration", WifiConfiguration.class);
            return (Boolean) method.invoke(wifiManager, wifiConfig);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    //关闭热点
    public void closewifiap() {
        try {
            Method method = wifiManager.getClass().getMethod("setWifiApEnabled", WifiConfiguration.class, boolean.class);
            method.invoke(wifiManager, apconfig, false);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }


    public boolean connectWifi(String ssid, String psw) {
        WifiConfiguration configuration = createWifiInfo(ssid, psw);
        int wcgID = wifiManager.addNetwork(configuration);
//         int wcgID = wifiManager.updateNetwork(configuration);
        return wifiManager.enableNetwork(wcgID, true);
    }


    public WifiConfiguration createWifiInfo(String ssid, String psw) {
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + ssid + "\"";

        config.preSharedKey = "\"" + psw + "\"";
        config.hiddenSSID = true;
        config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
        config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
        config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
        config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
        config.status = WifiConfiguration.Status.ENABLED;
        return config;
    }

    /**
     * 是否打开指定共享热点：名称和密码
     */
    private boolean isWifiApEnabled(String ssid, String password) {
        boolean ret = isWifiApEnabled();
        if (!ret) {
            return false;
        }

        WifiConfiguration wifiConfig = getWifiApConfiguration();
        String apName = wifiConfig.SSID;
        String apPassword = wifiConfig.preSharedKey;
        // TODO 可能要注意引号等特殊字符
        return ssid.equals(apName) && password.equals(apPassword);
    }

    /**
     * 获取AP热点配置信息
     */
    private WifiConfiguration getWifiApConfiguration() {
        try {
            Method method = wifiManager.getClass().getMethod("getWifiApConfiguration");
            method.setAccessible(true);
            return (WifiConfiguration) method.invoke(wifiManager);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 热点开关是否打开
     *
     * @return
     */
    public boolean isWifiApEnabled() {
        try {
            Method method = wifiManager.getClass().getMethod("isWifiApEnabled");
            method.setAccessible(true);
            return (Boolean) method.invoke(wifiManager);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    public static boolean isConnectWifi(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        return networkInfo.isConnected();
    }
}
