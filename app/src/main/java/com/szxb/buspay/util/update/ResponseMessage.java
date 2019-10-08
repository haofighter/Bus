package com.szxb.buspay.util.update;

/**
 * 作者：Tangren on 2018-08-21
 * 包名：com.szxb.buspay.util.update
 * 邮箱：996489865@qq.com
 * TODO:一句话描述
 */

public class ResponseMessage {

    public static final int SUCCESSFUL = 0;
    public static final int NOUPDATE = 1;
    public static final int SUCCESS = 2;
    public static final int FAIL = 3;
    public static final int FILE_NO_EXIT = 4;

    public static final int WHAT_BLACK = 100;
    public static final int WHAT_UNION = 101;
    public static final int WHAT_LINE = 102;
    public static final int WHAT_SCAN = 103;

    private int what;

    /**
     * 0:成功
     * 1:无需更新
     * 2.部分成功
     * 3：失败
     */
    private int status = FAIL;
    private String msg = "网络或服务器异常";
    private Throwable throwable;

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }

    public int getWhat() {
        return what;
    }

    public void setWhat(int what) {
        this.what = what;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    @Override
    public String toString() {
        String th = throwable != null ? throwable.toString() : "";
        return "响应>>what=" + what + ">>" + "status=" + getStatus() + ">>>msg=" + msg + ">>" + th;
    }
}
