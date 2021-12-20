package com.android.nanguo.app.api.old_data;

import com.android.nanguo.app.api.Constant;

import java.io.Serializable;

/**
 * @author lhb
 * 用户登录信息
 */
public class LoginInfo implements Serializable {


    /**
     * createTime : 2019-06-15 12:05:56.000
     * updateTime : 2019-06-15 14:54:30.883
     * userId : 1a5435ee8f2311e999de00ffbd97fd96
     * nickName : 18895397923
     * userHead : null
     * userCode : null
     * phone : 18895397923
     * password : null
     * passwordSalt : null
     * sign : null
     * userType : 0
     * authStatus : null
     * tokenId : 66e5c77479164186a2b81449f0e57b5c
     * money : 0
     */

    private String createTime;
    private String updateTime;
    private String userId;
    private String nickName;
    private String userHead;
    private String userCode;
    private String phone;
    private String account = "";
    private String password;
    private String passwordSalt;
    private String sign;
    private String userType;
    private String authStatus;
    private String tokenId;
    private String handRate;
    private double money;
    private int payPwdFlag;
    private String custId;
    private String sessionKey;
    private String sessionValue;
    private int openAccountFlag;
    private String myPassword;

    public int addWay;//0-需要 1-不需要 默认为1
    public int sex;//0-男 1-女 默认为0
    //钱包id
    public String ncountUserId;
    //新增字段
    private String vipId;
    private String vipLevel;
    private String openid;
    private String userLevel;
    private String line;

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getNcountUserId() {
        return ncountUserId;
    }

    public void setNcountUserId(String ncountUserId) {
        this.ncountUserId = ncountUserId;
    }

    public String getVipId() {
        return vipId;
    }

    public void setVipId(String vipId) {
        this.vipId = vipId;
    }

    public String getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(String vipLevel) {
        this.vipLevel = vipLevel;
    }

    public String getOpenid() {
        return openid;
    }

    public void setOpenid(String openid) {
        this.openid = openid;
    }

    public String getUserLevel() {
        return userLevel;
    }

    public void setUserLevel(String userLevel) {
        this.userLevel = userLevel;
    }

    public String getMyPassword() {
        return myPassword;
    }

    public void setMyPassword(String myPassword) {
        this.myPassword = myPassword;
    }

    public int getOpenAccountFlag() {
        return openAccountFlag;
    }

    public void setOpenAccountFlag(int openAccountFlag) {
        this.openAccountFlag = openAccountFlag;
    }

    public String getSessionKey() {
        return sessionKey;
    }

    public void setSessionKey(String sessionKey) {
        this.sessionKey = sessionKey;
    }

    public String getSessionValue() {
        return sessionValue;
    }

    public void setSessionValue(String sessionValue) {
        this.sessionValue = sessionValue;
    }

    public String getCustId() {
        return custId;
    }

    public void setCustId(String custId) {
        this.custId = custId;
    }

    public String getIdh() {
        return userId + Constant.ID_REDPROJECT;
    }

    public int getPayPwdFlag() {
        return payPwdFlag;
    }

    public void setPayPwdFlag(int payPwdFlag) {
        this.payPwdFlag = payPwdFlag;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    /**
     *
     * @return 用到这个字段使用account代替
     */
    @Deprecated
    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordSalt() {
        return passwordSalt;
    }

    public void setPasswordSalt(String passwordSalt) {
        this.passwordSalt = passwordSalt;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getUserType() {
        return userType;
    }

    public void setUserType(String userType) {
        this.userType = userType;
    }

    public String getAuthStatus() {
        return authStatus;
    }

    public void setAuthStatus(String authStatus) {
        this.authStatus = authStatus;
    }

    public String getTokenId() {
        return tokenId;
    }

    public void setTokenId(String tokenId) {
        this.tokenId = tokenId;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        this.money = money;
    }

    public String getHandRate() {
        return handRate;
    }

    public void setHandRate(String handRate) {
        this.handRate = handRate;
    }

    public String getAccount() {
        return account;
    }

    public void setAccount(String account) {
        this.account = account;
    }
}