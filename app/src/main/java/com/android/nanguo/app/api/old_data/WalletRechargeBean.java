package com.android.nanguo.app.api.old_data;

public final class WalletRechargeBean {
    public String token;
    public String orderId;
    public String html;//":"若用户未签约，则返回签约页面，否则返回空字符串"
    public double signStatus;//":"签约状态：0-未签约，1-已签约(int整型)"

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public double getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(double signStatus) {
        this.signStatus = signStatus;
    }

    //充值结果
    public String orderStatus;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
