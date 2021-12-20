package com.android.nanguo.app.api.new_data;

import java.util.List;

public class UserCodeMallListBean {
    private List<SpecialOffer>specialOffer;
    private List<SpecialOffer>choiceness;
    private List<SpecialOffer>doublingNo;


    public List<SpecialOffer> getSpecialOffer() {
        return specialOffer;
    }

    public void setSpecialOffer(List<SpecialOffer> specialOffer) {
        this.specialOffer = specialOffer;
    }

    public List<SpecialOffer> getChoiceness() {
        return choiceness;
    }

    public void setChoiceness(List<SpecialOffer> choiceness) {
        this.choiceness = choiceness;
    }

    public List<SpecialOffer> getDoublingNo() {
        return doublingNo;
    }

    public void setDoublingNo(List<SpecialOffer> doublingNo) {
        this.doublingNo = doublingNo;
    }

    public class SpecialOffer {
        private String userCode;//":"",//艾特号
        private String money;//":0,//价格
        private String category;//":0,// 0：特价靓号，1：精选靓号，2：精选叠号
        private String soldOut;//":0//0 未售 ， 1 已售
        private String codeId;//":

        public String getCodeId() {
            return codeId;
        }

        public void setCodeId(String codeId) {
            this.codeId = codeId;
        }

        public String getUserCode() {
            return userCode;
        }

        public void setUserCode(String userCode) {
            this.userCode = userCode;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getCategory() {
            return category;
        }

        public void setCategory(String category) {
            this.category = category;
        }

        public String getSoldOut() {
            return soldOut;
        }

        public void setSoldOut(String soldOut) {
            this.soldOut = soldOut;
        }
    }

}
