package com.ycf.qianzhihe.app.api.old_data;

import java.io.Serializable;
import java.util.List;

public class ContactListInfo implements Serializable {

    private int cacheVersion;

    public void setAppUserFriendVoList(List<DataBean> appUserFriendVoList) {
        this.appUserFriendVoList = appUserFriendVoList;
    }

    private List<DataBean> appUserFriendVoList;

    public List<DataBean> getData() {
        return appUserFriendVoList;
    }

    public List<DataBean> getAppUserFriendVoList() {
        return appUserFriendVoList;
    }

    public int getCacheVersion() {
        return cacheVersion;
    }

    public void setCacheVersion(int cacheVersion) {
        this.cacheVersion = cacheVersion;
    }

    public static class DataBean implements Serializable {

        private String userId;
        private String friendUserId;
        private String blackStatus;
        private String friendNickName;
        private String friendUserHead;
        private String addGroupFlag;
        private String nickName;
        private String line;
        private String categoryId;//":"f1fe5602064911ecab930c42a1a8807a",
        private String categoryName;//":"哈哈"
        private String vipLevel;
        private String userSign;
        private boolean isChecked;

        public boolean isChecked() {
            return isChecked;
        }

        public void setChecked(boolean checked) {
            isChecked = checked;
        }

        public String getVipLevel() {
            return vipLevel;
        }

        public void setVipLevel(String vipLevel) {
            this.vipLevel = vipLevel;
        }

        public String getUserSign() {
            return userSign;
        }

        public void setUserSign(String userSign) {
            this.userSign = userSign;
        }

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

        public String getLine() {
            return line;
        }

        public void setLine(String line) {
            this.line = line;
        }

        public String getFriendUserCode() {
            return friendUserCode;
        }

        public void setFriendUserCode(String friendUserCode) {
            this.friendUserCode = friendUserCode;
        }

        private String friendUserCode;

        public String getAddGroupFlag() {
            return addGroupFlag;
        }

        public void setAddGroupFlag(String addGroupFlag) {
            this.addGroupFlag = addGroupFlag;
        }

        public String getFriendUserHead() {
            return friendUserHead;
        }

        public void setFriendUserHead(String friendUserHead) {
            this.friendUserHead = friendUserHead;
        }

        public String getUserId() {
            return userId;
        }

        public void setUserId(String userId) {
            this.userId = userId;
        }

        public String getFriendUserId() {
            return friendUserId;
        }

        public void setFriendUserId(String friendUserId) {
            this.friendUserId = friendUserId;
        }

        public String getBlackStatus() {
            return blackStatus;
        }

        public void setBlackStatus(String blackStatus) {
            this.blackStatus = blackStatus;
        }

        public String getFriendNickName() {
            return friendNickName;
        }

        public void setFriendNickName(String friendNickName) {
            this.friendNickName = friendNickName;
        }

        public String getNickName() {
            return nickName;
        }

        public void setNickName(String nickName) {
            this.nickName = nickName;
        }
    }
}