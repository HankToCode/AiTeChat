package com.ycf.qianzhihe.section.contact.fragment;

import android.os.Bundle;
import android.text.TextUtils;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.adapter.FriendGroupingAdapter;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.new_data.FriendGroupingBean;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_data.GroupInfo;
import com.ycf.qianzhihe.app.api.old_data.MyGroupInfoList;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitFragment;
import com.ycf.qianzhihe.app.help.RclViewHelp;
import com.ycf.qianzhihe.section.contact.adapter.MyGroupAdapter;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class GroupingListFragment extends BaseInitFragment {
    public RecyclerView rvList;
    private FriendGroupingAdapter groupingAdapter;
    private int pageSize = 100;

    private int page = 1;
    private boolean isFirstStart = true;
    private List<FriendGroupingBean> mGroupInfoList;


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
        groupingAdapter = new FriendGroupingAdapter(mGroupInfoList);
        rvList.setAdapter(groupingAdapter);
        /*groupingAdapter.setOnLoadMoreListener(() -> {
            page++;
            groupList();
        }, rvList);*/
        RclViewHelp.initRcLmVertical(requireContext(), rvList, groupingAdapter);

        groupList();
    }

    @Override
    public void onResume() {
        super.onResume();
       /* if (isFirstStart) {
            isFirstStart = false;
        } else {
            page = 0;
            groupList();
        }*/
    }


    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.CREATE_GROUP_SUCCESS || center.getEventCode() == EventUtil.DEL_EXIT_GROUP) {
//            refresh();
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
//        map.put("pageSize", pageSize);
//        map.put("pageNum", page);

        ApiClient.requestNetHandle(requireContext(), AppConfig.selectFriendCategory, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<FriendGroupingBean> datas = FastJsonUtil.getList(json, FriendGroupingBean.class);
                if (datas != null && datas.size() > 0) {
                    if (page == 0 && mGroupInfoList.size() > 0) {
                        mGroupInfoList.clear();
                    }
                    mGroupInfoList.addAll(datas);
                    groupingAdapter.notifyDataSetChanged();
                    groupingAdapter.loadMoreComplete();
                } else {
                    groupingAdapter.loadMoreEnd(true);
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                groupingAdapter.loadMoreFail();
            }
        });


    }

}
