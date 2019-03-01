package com.szxb.buspay.wifi;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * 作者：Tangren on 2019-02-18
 * 包名：com.szxb.wifihot
 * 邮箱：996489865@qq.com
 * TODO:wifi热点端、消息发送端（前车机）
 */
public class WifiHotService extends Service {

    private Socket socket;
    private Thread thread;
    private ServerSocket serverSocket;
    private OutputStream out;
    private SocketBinder socketBinder = new SocketBinder();
    private int port = 1024;

    private boolean isReConnect = true;

    public OutputStream getOut() {
        return out;
    }

    private OnMessageListener listener;

    public void setOnMessageListener(OnMessageListener listener) {
        this.listener = listener;
    }


    @Override
    public IBinder onBind(Intent intent) {
        initSocket();
        return socketBinder;
    }

    class SocketBinder extends Binder {
        public WifiHotService getService() {
            return WifiHotService.this;
        }
    }


    private void initSocket() {
        Log.e("WifiHotService",
                "initSocket(WifiHotService.java:54)初始化热点服务");
        if (socket == null && thread == null) {
            //实际中使用线程池处理
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        serverSocket = new ServerSocket(port);
                        socket = serverSocket.accept();
                        Log.e("WifiHotService",
                                "run(WifiHotService.java:62)设备已连接");
                        sendMessage("设备已连接");
                        out = socket.getOutputStream();

                    } catch (IOException e) {
                        e.printStackTrace();
                        sendMessage(e.getClass().getName() + ",异常,2S重连");
                        Log.e("WifiHotService",
                                "run(WifiHotService.java:65)出现了" + e.getClass().getName() + "异常,2S重连");
                        SystemClock.sleep(2 * 1000);
                        releaseSocket();
                    }
                }
            });

            thread.start();
        }
    }

    private void sendMessage(String msg) {
        if (listener != null) {
            listener.sendMessage(msg + "\n");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isReConnect = false;
        releaseSocket();
    }

    public void releaseSocket() {
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            out = null;
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }

        if (serverSocket != null) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            serverSocket = null;
        }

        if (thread != null) {
            thread = null;
        }

        if (isReConnect) {
            initSocket();
        }
    }


}
