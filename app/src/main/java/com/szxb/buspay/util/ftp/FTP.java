package com.szxb.buspay.util.ftp;

import android.text.TextUtils;
import android.util.Log;

import com.szxb.buspay.BusApp;
import com.szxb.buspay.util.Util;
import com.szxb.mlog.SLog;

import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.net.ftp.FTPFile;
import org.apache.commons.net.ftp.FTPReply;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * 作者: Tangren on 2017/7/27
 * 包名：com.szxb.ftp
 * 邮箱：996489865@qq.com
 * TODO:FTP工具类
 */

public class FTP {

    private String url;
    private int port = 21;
    private String username;
    private String password;
    private String path;
    private String ftpPath;
    private String[] fileName;
    private String sinfileName;
    private List<InputStream> input;

    private String tag = "TAG";



    private String packgeName;

    private String posSn;

    public FTP builder(String url) {
        this.url = url;
        return this;
    }

    public FTP setPort(int port) {
        this.port = port;
        return this;
    }

    public FTP setLogin(String username, String psw) {
        this.username = username;
        this.password = psw;
        return this;
    }

    public FTP setPath(String path) {
        this.path = path;
        return this;
    }

    public FTP setFTPPath(String ftpPath) {
        this.ftpPath = ftpPath;
        return this;
    }

    public FTP setFileName(String sinfileName) {
        this.sinfileName = sinfileName;
        return this;
    }

    public FTP setFileName(String[] fileName) {
        this.fileName = fileName;
        return this;
    }

    public FTP setInput(List<InputStream> input) {
        this.input = input;
        return this;
    }

    public FTP setPackgeName(String packgeName) {
        this.packgeName = packgeName;
        return this;
    }

    public FTP setPosSn(String posSn) {
        this.posSn = posSn;
        return this;
    }

    public String getTag() {
        return tag;
    }

    public FTP setTag(String tag) {
        this.tag = tag;
        return this;
    }

    public boolean push() {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        int reply;
        ftp.setConnectTimeout(12000);
        try {
            ftp.connect(url, port);// 连接FTP服务器

            // 如果采用默认端口，可以使用ftp.connect(url)的方式直接连接FTP服务器
            ftp.login(username, password);// 登录
            //连接的状态码
            reply = ftp.getReplyCode();
            ftp.setDataTimeout(12000);
            //判断是否连接上ftp
            if (!FTPReply.isPositiveCompletion(reply)) {
                SLog.d("FTP(build.java:100)ftp>>连接失败");
                ftp.disconnect();
                return false;
            }

            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            //对传入的路径进行拆分，ftp里创建路径要一层一层的创建。
            String[] argPath = path.split("/");
            System.out.println("path:" + path);
            String pathName = null;
            //一层一层的开始创建路径
            for (String anArgPath : argPath) {
                pathName = anArgPath;
                ftp.makeDirectory(pathName);
                //创建一层就切换到该目录下
                ftp.changeWorkingDirectory(pathName);
            }
            SLog.d("FTP(build.java:123)FTP创建目录成功");
            //保存文件到路径下。
            for (int i = 0; i < input.size(); i++) {
                ftp.storeFile(fileName[i], input.get(i));
                input.get(i).close();
                Log.d("FTP",
                        "build(FTP.java:100)提交第" + (i + 1) + "个");
            }
            Log.i(TAG, "FTP上传文件成功");
            //关闭流，退出ftp
            ftp.logout();
            success = true;
            //判断是否退出成功，不成功就再断开连接。
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    throw new RuntimeException("FTP disconnect fail!", ioe);
                }
            }
        } catch (IOException e) {
            Log.d("FTP",
                    "build(FTP.java:117)" + e.getMessage());
            e.printStackTrace();
        }
        return success;
    }


    public boolean download() {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        BufferedOutputStream buffOut = null;
        int reply;
        ftp.setConnectTimeout(5000);
        try {
            ftp.connect(url, port);// 连接FTP服务器
            ftp.login(username, password);// 登录
            //连接的状态码
            reply = ftp.getReplyCode();
            ftp.setDataTimeout(12000);
            //判断是否连接上ftp
            if (!FTPReply.isPositiveCompletion(reply)) {
                SLog.d("FTP(download.java:163)FTP连接失败>>tag=" + getTag());
                ftp.disconnect();
                return false;
            }

            SLog.d("FTP(download.java:168)FTP连接成功>>tag=" + getTag());
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

            ftp.setBufferSize(1024);
            ftp.setControlEncoding("UTF-8");
            ftp.enterLocalPassiveMode();
            buffOut = new BufferedOutputStream(new FileOutputStream(path + sinfileName), 8 * 1024);
            success = ftp.retrieveFile(ftpPath, buffOut);

            SLog.d("FTP(download.java:178)检索文件：" + success + ">>tag=" + getTag());

            buffOut.flush();
            buffOut.close();

            ftp.logout();
            ftp.disconnect();
            //判断是否退出成功，不成功就再断开连接。
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    ioe.printStackTrace();
                    SLog.e("FTP(download.java:188)IOException>>>>tag=" + getTag() + ">>" + ioe.toString());
                    success = false;
                }
            }
        } catch (IOException e) {
            success = false;
            e.printStackTrace();
            SLog.e("FTP(download.java:193)FTP异常>>tag=" + getTag() + ">>>" + e.toString());
        }

        return success;
    }

    public boolean downloads() {
        boolean success = false;
        FTPClient ftp = new FTPClient();
        BufferedOutputStream buffOut = null;
        int reply;
        ftp.setConnectTimeout(5000);
        try {
            ftp.connect(url, port);// 连接FTP服务器
            ftp.login(username, password);// 登录
            //连接的状态码
            reply = ftp.getReplyCode();
            Log.d("FTP",
                    "call(FTP.java:218)状态码=" + reply);
            ftp.setDataTimeout(12000);
            //判断是否连接上ftp
            if (!FTPReply.isPositiveCompletion(reply)) {
                Log.d("FTP",
                        "call(FTP.java:221)FTP连接失败");
                ftp.disconnect();
                return false;
            }

            Log.d("FTP",
                    "call(FTP.java:227)FTP连接成功");
            ftp.enterLocalPassiveMode();
            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);

            ftp.setBufferSize(1024);
            ftp.setControlEncoding("UTF-8");
            ftp.enterLocalPassiveMode();
            for (String aFileName : fileName) {
                buffOut = new BufferedOutputStream(new FileOutputStream(path + aFileName), 8 * 1024);
                ftp.retrieveFile(packgeName + "/" + aFileName, buffOut);
                buffOut.flush();
                buffOut.close();
            }
            success = true;

            ftp.logout();
            ftp.disconnect();
            //判断是否退出成功，不成功就再断开连接。
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    Log.d("FTP",
                            "call(FTP.java:252)" + ioe.toString());
                    throw new RuntimeException("FTP disconnect fail!", ioe);
                }
            }
        } catch (IOException e) {
            success = false;
            Log.d("FTP",
                    "call(FTP.java:257)FTP异常" + e.toString());
            e.printStackTrace();
        }

        return success;
    }


    public int downUnionPayParasFile(boolean forceUpdate) {
        int success = 0;
        FTPClient ftp = new FTPClient();
        BufferedOutputStream buffOut = null;
        int reply;
        ftp.setConnectTimeout(10000);
        try {
            ftp.connect(url, port);// 连接FTP服务器
            ftp.login(username, password);// 登录
            //连接的状态码
            reply = ftp.getReplyCode();
            Log.d("FTP", reply + "11111");
            ftp.setDataTimeout(10000);
            //判断是否连接上ftp
            if (!FTPReply.isPositiveCompletion(reply)) {
                ftp.disconnect();
                return success;
            }

            ftp.setFileType(FTPClient.BINARY_FILE_TYPE);
            ftp.setBufferSize(1024);
            ftp.setControlEncoding("UTF-8");
            ftp.enterLocalPassiveMode();
            FTPFile[] ftpFile = ftp.listFiles(ftpPath);
            for (FTPFile file : ftpFile) {
                //如果包含
                if (file.getName().contains(posSn)) {
                    String lastParamFileName = BusApp.getPosManager().getLastParamsFileName();
                    //判断是否需要更新
                    if (!TextUtils.equals(lastParamFileName, file.getName()) || forceUpdate) {
                        //下载更新
                        SLog.d("FTP(downUnionPayParasFile.java:306)下载银联参数文件>>" + file.getName());
                        buffOut = new BufferedOutputStream(new FileOutputStream(path + file.getName()), 8 * 1024);
                        ftp.retrieveFile(ftpPath + file.getName(), buffOut);
                        success = 1;
                        buffOut.flush();
                        buffOut.close();
                        BusApp.getPosManager().setLastParamsFileName(file.getName());
                    } else {
                        success = 2;
                        SLog.d("FTP(downUnionPayParasFile.java:309)银联参数文件无需更新>>当前版本=" + lastParamFileName + "ftp版本=" + file.getName());
                    }
                }
            }
            ftp.logout();
            ftp.disconnect();
            //判断是否退出成功，不成功就再断开连接。
            if (ftp.isConnected()) {
                try {
                    ftp.disconnect();
                } catch (IOException ioe) {
                    SLog.e("FTP(downUnionPayParasFile.java:321)IOException异常>>" + ioe.toString());
                }
            }
        } catch (IOException e) {
            SLog.e("FTP(downUnionPayParasFile.java:326)IOException异常>>" + e.toString());
            e.printStackTrace();
        }

        return success;
    }

}
