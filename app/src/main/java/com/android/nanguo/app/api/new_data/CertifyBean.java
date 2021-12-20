package com.android.nanguo.app.api.new_data;

import java.io.Serializable;

public class CertifyBean implements Serializable {
    //body:{"errcode":"L0000","
    // result":{"certifyid":"ZTA9FA4725092A4486AEDB34DEEB267D50"},"
    // encresult":"","jobid":"ZT20210907161436066961823","responsetime":"20210907161436088","errmsg":"正常使用"}
    private String errcode;
    private ResultData result;
    private String encresult;
    private String jobid;
    private String responsetime;
    private String errmsg;

    public String getErrcode() {
        return errcode;
    }

    public void setErrcode(String errcode) {
        this.errcode = errcode;
    }

    public ResultData getResult() {
        return result;
    }

    public void setResult(ResultData result) {
        this.result = result;
    }

    public String getEncresult() {
        return encresult;
    }

    public void setEncresult(String encresult) {
        this.encresult = encresult;
    }

    public String getJobid() {
        return jobid;
    }

    public void setJobid(String jobid) {
        this.jobid = jobid;
    }

    public String getResponsetime() {
        return responsetime;
    }

    public void setResponsetime(String responsetime) {
        this.responsetime = responsetime;
    }

    public String getErrmsg() {
        return errmsg;
    }

    public void setErrmsg(String errmsg) {
        this.errmsg = errmsg;
    }

    public class ResultData {
        private String certifyid;

        public String getCertifyid() {
            return certifyid;
        }

        public void setCertifyid(String certifyid) {
            this.certifyid = certifyid;
        }
    }

}
