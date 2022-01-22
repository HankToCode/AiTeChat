package com.android.nanguo.app.api.old_data;

public final class MemberBuyBean {
    public String html;//":"若用户未签约，则返回签约页面，否则返回空字符串"
    public int signStatus;//":"签约状态：0-未签约，1-已签约(int整型)"

    public String signData;//":"",
    public String orderNo;//":"YB2022012218172926569",
    public String payOrderNo;//":"113120220122387124",
    public String vipId;//":"1",
    public String payToken;//":"32022"


    public String getSignData() {
        return signData;
    }

    public void setSignData(String signData) {
        this.signData = signData;
    }

    public String getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(String orderNo) {
        this.orderNo = orderNo;
    }

    public String getPayOrderNo() {
        return payOrderNo;
    }

    public void setPayOrderNo(String payOrderNo) {
        this.payOrderNo = payOrderNo;
    }

    public String getVipId() {
        return vipId;
    }

    public void setVipId(String vipId) {
        this.vipId = vipId;
    }

    public String getPayToken() {
        return payToken;
    }

    public void setPayToken(String payToken) {
        this.payToken = payToken;
    }

    public String getHtml() {
        return html;
    }

    public void setHtml(String html) {
        this.html = html;
    }

    public int getSignStatus() {
        return signStatus;
    }

    public void setSignStatus(int signStatus) {
        this.signStatus = signStatus;
    }

    //充值结果
    public String orderStatus;


    public String getOrderStatus() {
        return orderStatus;
    }

    public void setOrderStatus(String orderStatus) {
        this.orderStatus = orderStatus;
    }
}
