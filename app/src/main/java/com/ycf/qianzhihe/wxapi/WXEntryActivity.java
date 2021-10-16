package com.ycf.qianzhihe.wxapi;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.blankj.utilcode.util.ActivityUtils;
import com.blankj.utilcode.util.LogUtils;
import com.google.gson.Gson;
import com.luck.picture.lib.rxbus2.RxBus;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.zds.base.Toast.ToastUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * 微信回调页面
 */
public class WXEntryActivity extends Activity implements IWXAPIEventHandler {

    /**
     * 登录
     */
    private static final int RETURN_MSG_TYPE_LOGIN = 1;
    /**
     * 分享
     */
    private static final int RETURN_MSG_TYPE_SHARE = 2;
    private IWXAPI mWxapi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //注册回调
        mWxapi = WXAPIFactory.createWXAPI(this, Constant.WXAPPID, true);
        try {
            mWxapi.handleIntent(getIntent(), this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        mWxapi.handleIntent(intent, this);
    }

    /**
     * 微信发送请求到第三方应用时,会回调到该方法
     */
    @Override
    public void onReq(BaseReq baseReq) {

    }

    /**
     * 第三方应用发送到微信的请求处理后的响应结果,会回调到该方法
     * app发送消息给微信,处理返回消息的回调
     */
    @Override
    public void onResp(BaseResp baseResp) {
        //类型：分享还是登录
        int type = baseResp.getType();
        LogUtils.e("baseresp.getType = " + type);
        LogUtils.e("baseresp.errCode = " + baseResp.errCode);
        if (ActivityUtils.isActivityExistsInStack(PortraitActivity.class)) {
            ActivityUtils.finishActivity(PortraitActivity.class);
        }
        if (type == RETURN_MSG_TYPE_LOGIN) {
            WXLoginBean wxLoginBean = new WXLoginBean(baseResp.errCode, ((SendAuth.Resp) baseResp).code);
            Gson gson = new Gson();
            EventBus.getDefault().post(new EventCenter(EventUtil.WECHAT_LOGIN_KEY, gson.toJson(wxLoginBean)));
        }

        switch (baseResp.errCode) {
            case BaseResp.ErrCode.ERR_AUTH_DENIED:
                //用户拒绝授权
                ToastUtil.toast("拒绝授权微信登录");
            case BaseResp.ErrCode.ERR_USER_CANCEL:
                if (type == RETURN_MSG_TYPE_LOGIN) {
                    ToastUtil.toast("取消了微信登录");
                } else if (type == RETURN_MSG_TYPE_SHARE) {
                    ToastUtil.toast("取消了微信分享");
                }
                finish();
                break;

            case BaseResp.ErrCode.ERR_OK:
                LogUtils.e("ERR_OK");
                if (type == RETURN_MSG_TYPE_LOGIN) {
                    //用户换取access_token的code,仅在ErrCode为0时有效
                    String code = ((SendAuth.Resp) baseResp).code;
                    //这里拿到了这个code,去做2次网络请求获取access_token和用户个人信息
                    LogUtils.e("code:------>" + code);
                    WeChatManagerUtils.setLoginCode(code);
                } else if (type == RETURN_MSG_TYPE_SHARE) {
                    WeChatManagerUtils.setShareFlag(true);
                }
                finish();
                break;
            default:
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        //避免内存泄漏
        mWxapi.detach();
    }
}
