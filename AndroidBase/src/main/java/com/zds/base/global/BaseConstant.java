package com.zds.base.global;

public final class BaseConstant {

    /**
     * 存储SP文件的key
     */
    public interface SP{
        String KEY_TOKEN = "token";
        String KEY_IS_FIRST_ENTER = "is_first_enter";
        String KEY_IS_AGREE_USER_PROTOCOL = "is_agree_user_protocol";
        String KEY_TO_TOP_MAP = "to_top_map";
        /**
         * 用户今日是否操作过清除数据逻辑。
         */
        String KEY_IS_AS_LOGIN_TIME = "as_login_time";
    }
}
