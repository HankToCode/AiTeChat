package com.hyphenate.easeim.app.domain;

import com.hyphenate.easeui.utils.EaseCommonUtils;

public class EaseUser extends com.hyphenate.easeui.domain.EaseUser {

    public EaseUser() {

    }

    public EaseUser(String username) {
        super(username);
    }


    /**
     * 艾特号
     */
    private String friendUserCode;

    private String friendUserId;

    private String friendNickName;

    private String nickName;
    private String line;

    private String type;


    public String getFriendNickName() {
        return friendNickName;
    }

    public void setFriendNickName(String friendNickName) {
        this.friendNickName = friendNickName;
    }

    public String getFriendUserId() {
        return friendUserId;
    }

    public void setFriendUserId(String friendUserId) {
        this.friendUserId = friendUserId;
    }

    public String getFriendUserCode() {
        return friendUserCode;
    }

    public void setFriendUserCode(String friendUserCode) {
        this.friendUserCode = friendUserCode;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getNickName() {
        return nickName;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }


}
