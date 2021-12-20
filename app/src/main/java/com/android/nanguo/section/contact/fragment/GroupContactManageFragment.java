package com.android.nanguo.section.contact.fragment;

import android.os.Bundle;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.nanguo.R;
import com.android.nanguo.app.api.global.EventUtil;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.api.old_data.GroupInfo;
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
    private int pageSize = 100;

    private int page = 1;
    private boolean isFirstStart = true;
    private List<GroupInfo> mGroupInfoList;


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

        mGroupInfoList = new ArrayList<>();
        mAdapter = new MyGroupAdapter(mGroupInfoList);
        rvList.setAdapter(mAdapter);
        mAdapter.setOnLoadMoreListener(() -> {
            page++;
            groupList();
        }, rvList);
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
        groupList();
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
                    if (page == 0 && mGroupInfoList.size() > 0) {
                        mGroupInfoList.clear();
                    }
                    mGroupInfoList.addAll(myGroupInfo.getData());
                    mAdapter.notifyDataSetChanged();
                    mAdapter.loadMoreComplete();
                } else {
                    mAdapter.loadMoreEnd(true);
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                mAdapter.loadMoreFail();
            }
        });
    }

}
