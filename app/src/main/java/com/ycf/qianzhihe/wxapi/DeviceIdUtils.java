package com.ycf.qianzhihe.wxapi;

import android.Manifest;
import android.annotation.SuppressLint;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.RequiresPermission;

import com.blankj.utilcode.util.AppUtils;
import com.blankj.utilcode.util.DeviceUtils;

import java.util.UUID;

/**
 * 用于获取设备唯一码的工具类
 *
 * @author Fritz_Xu
 * @date 2019-09-12
 */
public class DeviceIdUtils {

    private DeviceIdUtils() {
        //只能静态使用
    }

    /**
     * 尝试获取设备唯一码,无法百分比准确
     *
     * @return 设备码
     */
    @RequiresPermission(allOf = {Manifest.permission.ACCESS_WIFI_STATE,Manifest.permission.INTERNET})
    public static String getDeviceId() {
        //获取设备的硬件Mac地址
        String macAddress = DeviceUtils.getMacAddress();
        if (TextUtils.isEmpty(macAddress)) {
            //Mac地址无法获取,获取AndroidId或Serial
            macAddress = TextUtils.isEmpty(DeviceUtils.getAndroidID()) ? getDeviceSerial() : DeviceUtils.getAndroidID();
        }
        String packageName = AppUtils.getAppPackageName();
        return new UUID(macAddress.hashCode(), packageName.hashCode()).toString();
    }

    @SuppressLint("HardwareIds")
    private static String getDeviceSerial() {
        return TextUtils.isEmpty(Build.SERIAL) ? UUID.randomUUID().toString() : Build.SERIAL;
    }
}
