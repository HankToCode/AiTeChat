package com.ycf.qianzhihe.app.api.old_data;


import com.zds.base.upDated.model.LibraryUpdateEntity;

/**
 */

public class AesInfo {


    private String status;//:"old" // old = 老密钥 , new=新密钥;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
