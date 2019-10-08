package com.szxb.buspay.http;

import com.szxb.buspay.BusApp;
import com.szxb.unionpay.unionutil.SSLContextUtil;
import com.yanzhenjie.nohttp.RequestMethod;
import com.yanzhenjie.nohttp.rest.ByteArrayRequest;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;

/**
 * 作者：Tangren on 2018-09-10
 * 包名：com.szxb.buspay.http
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class BaseByteRequest extends ByteArrayRequest {

    public BaseByteRequest(String url, RequestMethod requestMethod) {
        super(url, requestMethod);
        this.setHeader("User-Agent", "Donjin Http 0.1");
        this.setHeader("Cache-Control", "no-cache");
        this.setHeader("Accept", "*/*");
        this.setHeader("Accept-Encoding", "*");
        this.setHeader("Connection", "close");
        this.setHeader("HOST", "120.204.69.139:30000");

        SSLContext sslContext = SSLContextUtil.getSSLContext(BusApp.getInstance().getApplicationContext());
        if (sslContext == null) {
            sslContext = SSLContextUtil.getDefaultSLLContext();
        }
        this.setHostnameVerifier(SSLContextUtil.getHostnameVerifier());
        SSLSocketFactory socketFactory = sslContext.getSocketFactory();
        this.setSSLSocketFactory(socketFactory);
        this.setConnectTimeout(3000);
        this.setReadTimeout(3000);
    }
}
