package com.ycf.qianzhihe.app.api.old_data;

import android.content.Context;

import com.blankj.utilcode.util.GsonUtils;
import com.ycf.qianzhihe.section.common.MyGroupDetailActivity;
import com.zds.base.global.BaseConstant;
import com.zds.base.util.Preference;
import com.zds.base.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ToTopMap {

    private List<String> list = new ArrayList<>();

    public List<String> getList() {
        return list;
    }

    public void setList(List<String> list) {
        this.list = list;
    }

    public static void save(Context context, String emChatId) {
        String toTopMap = Preference.getStringPreferences(context, BaseConstant.SP.KEY_TO_TOP_MAP, "");

        ToTopMap topMap;
        if (!StringUtil.isEmpty(toTopMap)) {
            topMap = GsonUtils.fromJson(toTopMap, ToTopMap.class);
        } else {
            topMap = new ToTopMap();
        }
        if(!topMap.getList().contains(emChatId)){
            topMap.getList().add(0, emChatId);
            Preference.saveStringPreferences(context, BaseConstant.SP.KEY_TO_TOP_MAP, GsonUtils.toJson(topMap));
        }
    }

    public static void delete(Context context, String emChatId) {
        String toTopMap = Preference.getStringPreferences(context, BaseConstant.SP.KEY_TO_TOP_MAP, "");

        ToTopMap topMap;
        if (!StringUtil.isEmpty(toTopMap)) {
            topMap = GsonUtils.fromJson(toTopMap, ToTopMap.class);
        } else {
            topMap = new ToTopMap();
        }
        topMap.getList().remove(emChatId);

        Preference.saveStringPreferences(context, BaseConstant.SP.KEY_TO_TOP_MAP, GsonUtils.toJson(topMap));
    }

    public static ToTopMap give(Context context) {
        String toTopMap = Preference.getStringPreferences(context, BaseConstant.SP.KEY_TO_TOP_MAP, "");

        ToTopMap topMap = null;
        if (!StringUtil.isEmpty(toTopMap)) {
            topMap = GsonUtils.fromJson(toTopMap, ToTopMap.class);
        }
        return topMap;
    }
}
