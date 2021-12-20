package com.android.nanguo.app.api.new_data;

import java.io.Serializable;

public class VipBean implements Serializable {
    /**vipId":"",//vip id
     "name":"",//vip name
     "vipPrice":0,//vip money
     "vipLevel":0,//vip level
     "growthDoubling":0,//成长值加速
     "friendLimit":0//好友限制*/
    private String vipId;
    private String name;
    private String vipPrice;
    private String vipLevel;
    private String growthDoubling;
    private String friendLimit;

    public String getVipId() {
        return vipId;
    }

    public void setVipId(String vipId) {
        this.vipId = vipId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVipPrice() {
        return vipPrice;
    }

    public void setVipPrice(String vipPrice) {
        this.vipPrice = vipPrice;
    }

    public String getVipLevel() {
        return vipLevel;
    }

    public void setVipLevel(String vipLevel) {
        this.vipLevel = vipLevel;
    }

    public String getGrowthDoubling() {
        return growthDoubling;
    }

    public void setGrowthDoubling(String growthDoubling) {
        this.growthDoubling = growthDoubling;
    }

    public String getFriendLimit() {
        return friendLimit;
    }

    public void setFriendLimit(String friendLimit) {
        this.friendLimit = friendLimit;
    }




}
