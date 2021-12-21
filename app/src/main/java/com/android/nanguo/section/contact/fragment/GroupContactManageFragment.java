package com.android.nanguo.section.contact.fragment;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.nanguo.R;
import com.android.nanguo.app.api.global.EventUtil;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.api.old_data.GroupDetailInfo;
import com.android.nanguo.app.api.old_data.GroupInfo;
import com.android.nanguo.app.api.old_data.GroupSuperInfo;
import com.android.nanguo.app.api.old_data.MyGroupInfoList;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitFragment;
import com.android.nanguo.app.help.RclViewHelp;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.android.nanguo.section.contact.adapter.MyGroupAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GroupContactManageFragment extends BaseInitFragment {
    public RecyclerView rvList;
    public MyGroupAdapter mAdapter;
    private int pageSize = 1000;

    private int page = 1;
    private boolean isFirstStart = true;
    private ArrayList<GroupSuperInfo> mList = new ArrayList<>();
    private ArrayList<GroupInfo> mAllGroupInfoList = new ArrayList<>();

    //我创建的
    private ArrayList<GroupInfo> mMyGroupInfoList = new ArrayList<>();
    //我管理的
    private ArrayList<GroupInfo> mMaGroupInfoList = new ArrayList<>();
    //我加入的
    private ArrayList<GroupInfo> mInGroupInfoList = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_group_public_contact_manage;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        rvList = findViewById(R.id.rv_list);
    }


    @Override
    protected void initListener() {
        super.initListener();
    }

    @Override
    protected void initData() {
        super.initData();
        rvList.setLayoutManager(new LinearLayoutManager(mContext));

        mList = new ArrayList<>();
        mAdapter = new MyGroupAdapter(requireContext(), mList);
        rvList.setAdapter(mAdapter);
        /*mAdapter.setOnLoadMoreListener(() -> {
            page++;
            groupList();
        }, rvList);*/
        RclViewHelp.initRcLmVertical(requireContext(), rvList, mAdapter);

        groupList();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstStart) {
            isFirstStart = false;
        } else {
            page = 0;
            groupList();
        }
    }


    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.CREATE_GROUP_SUCCESS || center.getEventCode() == EventUtil.DEL_EXIT_GROUP) {
            refresh();
        }
    }

    public void refresh() {
        page = 0;
//        groupList();
    }

    /**
     * 我的群组列表
     *
     * @param
     */
    private void groupList() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("pageSize", pageSize);
        map.put("pageNum", page);

        ApiClient.requestNetHandle(requireContext(), AppConfig.MY_GROUP_LIST, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                MyGroupInfoList myGroupInfo = FastJsonUtil.getObject(json, MyGroupInfoList.class);
                if (myGroupInfo.getData() != null && myGroupInfo.getData().size() > 0) {
                    if (page == 0 && mAllGroupInfoList.size() > 0) {
                        mAllGroupInfoList.clear();
                    }
                    mAllGroupInfoList.addAll(myGroupInfo.getData());

                    mMyGroupInfoList.clear();
                    mMaGroupInfoList.clear();
                    mInGroupInfoList.clear();
                    for (GroupInfo groupInfo : mAllGroupInfoList) {
                        queryGroupDetail(groupInfo.getGroupId());
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    public void queryGroupDetail(String groupId) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        ApiClient.requestNetHandle(requireContext(), AppConfig.GET_GROUP_DETAIL, "请稍等...",
                map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        if (json != null && json.length() > 0) {
                            GroupDetailInfo groupDetailInfo = FastJsonUtil.getObject(json,
                                    GroupDetailInfo.class);


                            for (GroupInfo groupInfo : mAllGroupInfoList) {
                                if (groupInfo.getGroupId().equals(groupId)) {
                                    if (groupDetailInfo.getGroupUserRank() == 0) {
                                        mInGroupInfoList.add(groupInfo);
                                    } else if (groupDetailInfo.getGroupUserRank() == 1) {
                                        mMaGroupInfoList.add(groupInfo);
                                    } else if (groupDetailInfo.getGroupUserRank() == 2) {
                                        mMyGroupInfoList.add(groupInfo);
                                    }
                                    refreshGroup();
                                    return;
                                }
                            }


                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                    }
                });
    }

    private void refreshGroup() {

        mList.clear();

        GroupSuperInfo groupSuperInfo = new GroupSuperInfo();
        groupSuperInfo.setTitle("我创建的群(" + mMyGroupInfoList.size() + ")");
        groupSuperInfo.setGroupInfos(mMyGroupInfoList);
        mList.add(groupSuperInfo);

        GroupSuperInfo groupSuperInfo1 = new GroupSuperInfo();
        groupSuperInfo1.setTitle("我管理的群(" + mMaGroupInfoList.size() + ")");
        groupSuperInfo1.setGroupInfos(mMaGroupInfoList);
        mList.add(groupSuperInfo1);

        GroupSuperInfo groupSuperInfo2 = new GroupSuperInfo();
        groupSuperInfo2.setTitle("我加入的群(" + mInGroupInfoList.size() + ")");
        groupSuperInfo2.setGroupInfos(mInGroupInfoList);
        mList.add(groupSuperInfo2);

        mAdapter.notifyDataChanged();
    }

}
