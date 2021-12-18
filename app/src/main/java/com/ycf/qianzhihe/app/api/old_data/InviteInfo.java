package com.ycf.qianzhihe.app.api.old_data;

import com.ycf.qianzhihe.app.api.Constant;

import java.io.Serializable;

/**
 * @author lhb
 * 用户登录信息
 */
public class InviteInfo implements Serializable {


    private String inviteCode;//": "",邀请码
    private String userHead;//": "",邀请人头像
    private String userName;//": "",用户名
    private String userCode;//": "",用户码
    private String inviteCount;//": 0,已成功邀请数

    public String getInviteCode() {
        return inviteCode;
    }

    public void setInviteCode(String inviteCode) {
        this.inviteCode = inviteCode;
    }

    public String getUserHead() {
        return userHead;
    }

    public void setUserHead(String userHead) {
        this.userHead = userHead;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserCode() {
        return userCode;
    }

    public void setUserCode(String userCode) {
        this.userCode = userCode;
    }

    public String getInviteCount() {
        return inviteCount;
    }

    public void setInviteCount(String inviteCount) {
        this.inviteCount = inviteCount;
    }
}