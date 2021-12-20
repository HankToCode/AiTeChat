package com.android.nanguo.app.api.old_data;

import java.util.ArrayList;

public class GroupSuperInfo {
    private String title = "";
    private boolean isExpend = false;
    private ArrayList<GroupInfo> groupInfos = new ArrayList<>();

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ArrayList<GroupInfo> getGroupInfos() {
        return groupInfos;
    }

    public void setGroupInfos(ArrayList<GroupInfo> groupInfos) {
        this.groupInfos = groupInfos;
    }

    public boolean isExpend() {
        return isExpend;
    }

    public void setExpend(boolean expend) {
        isExpend = expend;
    }
}
