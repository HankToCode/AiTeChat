package com.android.nanguo.app.api.old_data;

/**
 * @author lhb
 * 好南国时光息
 */
public class FriendInfo {
    /**
     * userId : 1a5435ee8f2311e999de00ffbd97fd96
     * nickName : 大同
     * userHead : http://47.107.131.59:8763/chatinte/profile/20190624/40efec3d702d6762518225965229eed8.jpg
     * userCode : 1324564654
     */

    private String userId;
    private String nickName;
    private String userNickName;
    private String userHead;
    private String userCode = "";
    private String friendFlag;
    private String blackStatus;
    private String friendNickName;
    private String line;
    private String sign;
    private String starTarget;//0:未加星标，1：星标
    private String categoryId;
    private String categoryName;

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public String getStarTarget() {
        return starTarget;
    }

    public void setStarTarget(String starTarget) {
        this.starTarget = starTarget;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getUserNickName() {
        return userNickName;
    }

    public void setUserNickName(String userNickName) {
        this.userNickName = userNickName;
    }

    public String getFriendNickName() {
        return friendNickName;
    }

    public void setFriendNickName(String friendNickName) {
        this.friendNickName = friendNickName;
    }

    public String getBlackStatus() {
        return blackStatus;
    }

    public void setBlackStatus(String blackStatus) {
        this.blackStatus = blackStatus;
    }

    public String getLine() {
        return line;
    }

    public void setLine(String line) {
        this.line = line;
    }



    public String getFriendFlag() {
        return friendFlag;
    }

    public void setFriendFlag(String friendFlag) {
        this.friendFlag = friendFlag;
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
}
