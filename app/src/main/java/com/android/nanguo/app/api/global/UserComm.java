package com.android.nanguo.app.api.global;

import android.text.TextUtils;

import com.android.nanguo.common.aes.AESCipher;
import com.android.nanguo.common.utils.PreferenceManager;
import com.android.nanguo.app.api.old_data.LoginInfo;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.StringUtil;


public class UserComm {
    protected static String userToken;
    protected static LoginInfo userInfo = null;

    public static void init() {
        readUserInfo();
    }

    public static boolean IsOnLine() {
        return userInfo != null;
    }

    public static String getToken() {
        if (IsOnLine()) {
            return userInfo.getTokenId();
        } else {
            return "";
        }
    }

    public static String getUserId() {
        if (IsOnLine()) {
            return userInfo.getUserId();
        } else {
            return "";
        }
    }

    public static void saveUsersInfo(LoginInfo info) {
        if (StringUtil.isEmpty(info.getAccount())) {
            info.setAccount(UserComm.getUserInfo().getAccount());
        }
        if (StringUtil.isEmpty(info.getPassword())) {
            info.setPassword(UserComm.getUserInfo().getPassword());
        }
        userInfo = info;
        String data = FastJsonUtil.toJSONString(userInfo);
        data = AESCipher.encrypt(data);
        PreferenceManager.getInstance().saveUserData(SP.TAG_USER_INFO, data);
    }

    public static LoginInfo getUserInfo() {
        if (userInfo == null) {
            if (!readUserInfo())
                return new LoginInfo();
        }

        return userInfo;
    }

    /**
     * 清除本地缓存的用户登录信息
     */
    public static void clearUserInfo() {
        userInfo = null;
        PreferenceManager.getInstance().setParam(SP.TAG_USER_INFO, "");
    }

    /**
     * 主动读取用户数据到内存，进行快速登录功能的实现
     */
    public static boolean readUserInfo() {
        boolean result = false;
        String data = (String) PreferenceManager.getInstance().getUserData(SP.TAG_USER_INFO);
        if (!TextUtils.isEmpty(data)) {
            data = AESCipher.decrypt(data);
            userInfo = FastJsonUtil.getObject(data, LoginInfo.class);
            result = true;
        }
        return result;
    }
}
