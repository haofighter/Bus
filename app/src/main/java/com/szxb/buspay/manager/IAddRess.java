package com.szxb.buspay.manager;

import com.szxb.buspay.db.entity.bean.FTPEntity;

/**
 * 作者：Tangren on 2018-07-18
 * 包名：com.szxb.buspay.manager
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public interface IAddRess {

    void setFtpIp(String ip);

    String getFtpIP();

    void setPort(int port);

    int getPort();

    void setFtpPsw(String psw);

    String getFtpPsw();

    void setFtpUser(String user);

    String getFtpUser();

    void setUrlIp(String ip);

    String getUrlIp();

    void setFTP(FTPEntity ftp);

    FTPEntity getFTP();
}
