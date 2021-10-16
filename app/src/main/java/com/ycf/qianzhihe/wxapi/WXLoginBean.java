package com.ycf.qianzhihe.wxapi;

public class WXLoginBean {

    private int errCode;
    private String code;

    public WXLoginBean(int errCode, String code) {
        this.errCode = errCode;
        this.code = code;
    }

    public int getErrCode() {
        return errCode;
    }

    public String getCode() {
        return code;
    }
}
