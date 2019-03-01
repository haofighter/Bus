package com.szxb.buspay.wifi;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.NoRouteToHostException;
import java.net.Socket;
import java.util.Timer;
import java.util.TimerTask;

/**
 * 作者：Tangren on 2019-02-18
 * 包名：com.szxb.wifihot
 * 邮箱：996489865@qq.com
 * TODO:接收消息（下车机）
 */
public class ReceiverService extends Service {
    private Socket socket;
    private Thread thread;
    private OutputStream out;
    private InputStream in;
    private SocketBinder socketBinder = new SocketBinder();
    private String ip;
    private int port = 1024;

    //是否接收消息
    private boolean isReceMes = true;
    private boolean isReConnect = true;

    private OnMessageListener listener;
    private Timer timer = new Timer();
    private TimerTask task;

    public void setOnMessageListener(OnMessageListener listener) {
        this.listener = listener;
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.e("ReceiverService",
                "onBind(ReceiverService.java:38)onBind");
        ip = intent.getStringExtra("ip");
        initSocket();
        return socketBinder;
    }

    class SocketBinder extends Binder {
        public ReceiverService getService() {
            return ReceiverService.this;
        }
    }


    private void initSocket() {
        isReceMes = true;
        if (socket == null && thread == null) {
            //实际中使用线程池处理
            Log.e("ReceiverService",
                    "initSocket(ReceiverService.java:59)开启任务");
            sendMessage("开启任务");
            thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        socket = new Socket(ip, port);
                        Log.d("SocketService",
                                "run(SocketService.java:62)建立连接>>ip=" + ip + "," + socket.isConnected());
                        sendMessage("建立连接>>ip=" + ip + "," + socket.isConnected());
                        in = socket.getInputStream();
                        //在此可发送心跳
                        sendBeatData();
                        //接收数据
                        do {
                            //接收消息
                            byte[] buffer = new byte[1024];
                            int len = 0;
                            if ((len = in.read(buffer)) != -1) {
                                byte[] data = new byte[len];
                                System.arraycopy(buffer, 0, data, 0, data.length);
                                String msg = new String(data);
                                sendMessage("接收到的消息:" + msg);
                                //处理接收的消息

                            }
                        } while (isReceMes);

                    } catch (IOException e) {
                        e.printStackTrace();
                        isReceMes = false;
                        if (e instanceof NoRouteToHostException) {
                            Log.e("SocketService",
                                    "run(SocketService.java:67)地址不存在,停止服务");
                            stopSelf();
                        } else {
                            Log.e("SocketService",
                                    "run(SocketService.java:71)出现了" + e.getClass().getName() + "异常,2S后重连");
                            sendMessage(e.getClass().getName() + "异常,2S后重连");
                            SystemClock.sleep(2 * 1000);
                            releaseSocket();
                        }
                    }
                }
            });
            thread.start();
        }
    }

    //心跳
    private void sendBeatData() {
        if (timer == null) {
            timer = new Timer();
        }

        if (task == null) {
            task = new TimerTask() {
                @Override
                public void run() {
                    try {
                        out = socket.getOutputStream();
                        if (out != null) {
                            out.write("heart".getBytes());
                        }
                    } catch (IOException e) {
                        e.printStackTrace();
                        sendMessage("心跳检测出连接异常,2S后重连");
                        SystemClock.sleep(2 * 1000);
                        initSocket();
                    }
                }
            };
        }

        timer.schedule(task, 0, 2000);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isReConnect = false;
        releaseSocket();
    }

    private void releaseSocket() {
        if (task != null) {
            task.cancel();
            task = null;
        }
        if (timer != null) {
            timer.purge();
            timer.cancel();
            timer = null;
        }
        if (out != null) {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            out = null;
        }

        if (in != null) {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            in = null;
        }

        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            socket = null;
        }

        if (thread != null) {
            thread = null;
        }

        if (isReConnect) {
            initSocket();
        }
    }


    private void sendMessage(String msg) {
        if (listener != null) {
            listener.sendMessage(msg + "\n");
        }
    }

}
