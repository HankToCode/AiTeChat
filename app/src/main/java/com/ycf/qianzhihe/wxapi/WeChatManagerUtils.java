package com.ycf.qianzhihe.wxapi;

import android.content.Context;
import android.graphics.BitmapFactory;

import com.tencent.mm.opensdk.modelmsg.SendMessageToWX;
import com.tencent.mm.opensdk.modelmsg.WXImageObject;
import com.tencent.mm.opensdk.modelmsg.WXMediaMessage;
import com.tencent.mm.opensdk.modelmsg.WXWebpageObject;
import com.tencent.mm.opensdk.modelpay.PayReq;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.ycf.qianzhihe.app.api.Constant;


/**
 * 微信相关的工具类
 *
 * @author Guoyp on 2017/5/16.
 */

public class WeChatManagerUtils {

    private static String mLoginCode;
    private static boolean mShareFlag = false;

    public static boolean getShareFlag() {
        return mShareFlag;
    }

    public static void setShareFlag(boolean flag) {
        mShareFlag = flag;
    }

    public static String getLoginCode() {
        return mLoginCode;
    }

    public static void setLoginCode(String loginCode) {
        mLoginCode = loginCode;
    }

    public static void weChatLogin(Context context) {
        IWXAPI mWXApi = WXAPIFactory.createWXAPI(context, Constant.WXAPPID, true);
        mWXApi.registerApp(Constant.WXAPPID);

    }

    /**
     * 判断是否安装微信
     *
     * @param context 上下文
     * @return false 未安装
     */
    public static boolean isWeChatAppInstalledAndSupported(Context context) {
        IWXAPI msgApi = WXAPIFactory.createWXAPI(context, null);
        msgApi.registerApp(Constant.WXAPPID);
        return msgApi.isWXAppInstalled();
    }

}
