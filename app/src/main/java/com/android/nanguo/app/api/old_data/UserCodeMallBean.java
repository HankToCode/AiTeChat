package com.android.nanguo.app.api.old_data;

import java.util.List;

public class UserCodeMallBean {
    private List<DataBean> data;

    public List<DataBean> getData() {
        return data;
    }

    public void setData(List<DataBean> data) {
        this.data = data;
    }

    public class DataBean {
        private String userCode;//":"",//用户号
        private String money;//":0,// 金额
        private String category;//":0,//0：特价靓号，1：精选靓号，2：精选叠号
        private String soldOut;//":0,// 0 未售 ， 1 已售
        private String codeId;//":""//靓号id

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

        public String getCodeId() {
            return codeId;
        }

        public void setCodeId(String codeId) {
            this.codeId = codeId;
        }
    }


}
