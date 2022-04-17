package com.android.nanguo.common.utils.comlist;

import com.android.nanguo.app.api.old_data.ApplyFriendData;
import com.android.nanguo.app.api.old_data.GroupUserAuditInfo;

import java.util.ArrayList;
import java.util.List;

public final class ListCacheUtil {

    private static ListCacheUtil cacheUtil;

    private ListCacheUtil() {
        applyFriendData = new ArrayList<>();
        groupUserAuditInfoData = new ArrayList<>();
    }

    public static ListCacheUtil getInstance() {
        if (cacheUtil == null) {
            cacheUtil = new ListCacheUtil();
        }
        return cacheUtil;
    }

    public List<ApplyFriendData> applyFriendData;

    public List<GroupUserAuditInfo.DataBean> groupUserAuditInfoData;


}
