package com.zds.base.upDated.model;

/**
 * Created by cretin on 2017/3/8.
 */

public class UpdateEntity implements LibraryUpdateEntity {
    public int versionCode = 0;
    //是否强制更新 0 不强制更新 1 hasAffectCodes拥有字段强制更新 2 所有版本强制更新
    public int isForceUpdate = 0;
    public int openTransaction = 0;//是否开启交易0-关闭，1-开启

    //上一个版本版本号
    public int preBaselineCode = 0;
    //版本号 描述作用
    public String versionName = "";
    //新安装包的下载地址
    public String downurl = "";
    //更新日志
    public String updateLog = "";
    //安装包大小 单位字节
    public String size = "";
    //受影响的版本号 如果开启强制更新 那么这个字段包含的所有版本都会被强制更新 格式 2|3|4
    public String hasAffectCodes = "";


    //{"downloadUrl":"https://d.hnyunliao.com/app/qianzhihe.apk","isForceUpdate":1.0,"isGrade":1.0,"newVersion":"1.1.7"}
    public int isGrade;
    public String downloadUrl = "";
    public String newVersion = "";

    public int getOpenTransaction() {
        return openTransaction;
    }

    public void setOpenTransaction(int openTransaction) {
        this.openTransaction = openTransaction;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getNewVersion() {
        return newVersion;
    }

    public void setNewVersion(String newVersion) {
        this.newVersion = newVersion;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public int getIsForceUpdate() {
        return isForceUpdate;
    }

    public void setIsForceUpdate(int isForceUpdate) {
        this.isForceUpdate = isForceUpdate;
    }

    public int getPreBaselineCode() {
        return preBaselineCode;
    }

    public void setPreBaselineCode(int preBaselineCode) {
        this.preBaselineCode = preBaselineCode;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public String getDownurl() {
        return downurl;
    }

    public void setDownurl(String downurl) {
        this.downurl = downurl;
    }

    public String getUpdateLog() {
        return updateLog;
    }

    public void setUpdateLog(String updateLog) {
        this.updateLog = updateLog;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public String getHasAffectCodes() {
        return hasAffectCodes;
    }

    public void setHasAffectCodes(String hasAffectCodes) {
        this.hasAffectCodes = hasAffectCodes;
    }

    public int getIsGrade() {
        return isGrade;
    }

    public void setIsGrade(int isGrade) {
        this.isGrade = isGrade;
    }

    @Override
    public int getVersionCodes() {
        return getVersionCode();
    }

    @Override
    public int getIsForceUpdates() {
        return getIsForceUpdate();
    }

    @Override
    public int getPreBaselineCodes() {
        return getPreBaselineCode();
    }

    @Override
    public String getVersionNames() {
        return getVersionName();
    }

    @Override
    public String getDownurls() {
        return getDownurl();
    }

    @Override
    public String getUpdateLogs() {
        return getUpdateLog();
    }

    @Override
    public String getApkSizes() {
        return getSize();
    }

    @Override
    public String getHasAffectCodess() {
        return getHasAffectCodes();
    }

    @Override
    public int isGrade() {
        return 0;
    }


}
