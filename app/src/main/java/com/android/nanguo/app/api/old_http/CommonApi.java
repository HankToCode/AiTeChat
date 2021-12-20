package com.android.nanguo.app.api.old_http;

import android.content.Context;

import com.alibaba.fastjson.JSON;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.api.old_data.LoginInfo;
import com.android.nanguo.app.api.global.EventUtil;
import com.android.nanguo.app.api.global.UserComm;
import com.android.nanguo.section.account.activity.LoginActivity;
import com.zds.base.util.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

public class CommonApi {

    /**
     * 清除缓存用户信息
     *
     * @param
     */
    public static void cleanUserInfo() {
        UserComm.clearUserInfo();
    }


    /**
     * 更新用户信息
     */
    public static void upUserInfo(Context context) {
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandle(context, AppConfig.USER_INFO, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    LoginInfo loginInfo = JSON.parseObject(json, LoginInfo.class);
                    if (loginInfo != null) {
                        UserComm.saveUsersInfo(loginInfo);
                        EventBus.getDefault().post(new EventCenter(EventUtil.FLUSHUSERINFO));
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }


    /**
     * 用户存在是ture 否则是false
     *
     * @return
     */
    public static boolean checkUser() {
        if (StringUtil.isEmpty(getUserLoginInfo().getTokenId())) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * 用户存在是ture 否则是false
     *
     * @return
     */
    public static boolean checkUserToLogin(Context context) {
        if (StringUtil.isEmpty(getUserLoginInfo().getTokenId())) {
//            Intent intent = new Intent(context, /*AuthenticationActivity.class*/LoginActivity.class);
//            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            context.startActivity(intent);
            LoginActivity.actionStart(context);
            return false;
        } else {
            return true;
        }
    }


    /**
     * 获取缓存用户登录信息
     *
     * @return
     */
    public static LoginInfo getUserLoginInfo() {
        return UserComm.getUserInfo();
    }


}
