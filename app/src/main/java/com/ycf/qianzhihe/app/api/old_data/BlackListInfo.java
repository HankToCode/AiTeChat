package com.ycf.qianzhihe.app.api.old_data;

import java.io.Serializable;
import java.util.List;

public class BlackListInfo {
    private List<ContactInfo> data;

    public List<ContactInfo> getData() {
        return data;
    }

    public void setData(List<ContactInfo> data) {
        this.data = data;
    }

    public class ContactInfo implements Serializable {

        private String userId;
        private String friendUserId;
        private String blackStatus;
        private String friendNickName;
        private String friendUserHead;
        private String addGroupFlag;

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

        public String getFriendUserHead() {
            return friendUserHead;
        }

        public void setFriendUserHead(String friendUserHead) {
            this.friendUserHead = friendUserHead;
        }

        public String getAddGroupFlag() {
            return addGroupFlag;
        }

        public void setAddGroupFlag(String addGroupFlag) {
            this.addGroupFlag = addGroupFlag;
        }
    }
}