package com.ycf.qianzhihe.section.contact.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ExpandableListView;

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
import com.ycf.qianzhihe.section.contact.adapter.GroupingAdapter;
import com.ycf.qianzhihe.section.contact.adapter.MyGroupAdapter;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//我的分组
public class GroupingListFragment extends BaseInitFragment {
    public RecyclerView rvList;
//    private FriendGroupingAdapter groupingAdapter;
    private int pageSize = 100;

    private int page = 1;
    private boolean isFirstStart = true;
    private List<FriendGroupingBean> mGroupInfoList;
    private ExpandableListView elv_expand;
    private ArrayList<String> mGroupingList;//分组数据
    //item成员数据
    private ArrayList<ArrayList<String>> mItemSet;
    private GroupingAdapter mGroupingAdapter;



    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_group_public_contact_manage;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        rvList = findViewById(R.id.rv_list);
        elv_expand = findViewById(R.id.elv_expand);
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
        mGroupingList = new ArrayList<>();
        mItemSet = new ArrayList<>();
        mGroupingAdapter = new GroupingAdapter(mContext,mGroupingList,mItemSet);
        elv_expand.setAdapter(mGroupingAdapter);
        elv_expand.setGroupIndicator(null);//不设置大组指示器图标，因为我们自定义设置了
        elv_expand.setDivider(null);//设置图片可拉伸的
//        groupingAdapter = new FriendGroupingAdapter(mGroupInfoList);
//        rvList.setAdapter(groupingAdapter);
        /*groupingAdapter.setOnLoadMoreListener(() -> {
            page++;
            groupList();
        }, rvList);*/
//        RclViewHelp.initRcLmVertical(requireContext(), rvList, groupingAdapter);


        groupingList();
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
        if (center.getEventCode() == EventUtil.FLUSHGROUPING) {
            Log.d("TAG", "刷新好友分组");
            refresh();
        }
    }

    public void refresh() {
        page = 0;
        groupingList();
    }

    /**
     * 我的群组列表
     *
     * @param
     */
    private void groupingList() {
        Map<String, Object> map = new HashMap<>(2);
        ApiClient.requestNetHandle(requireContext(), AppConfig.selectFriendCategory, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                List<FriendGroupingBean> datas = FastJsonUtil.getList(json, FriendGroupingBean.class);
                if (page == 0 && mGroupInfoList.size() > 0) {
                    mGroupInfoList.clear();
                }
                mGroupInfoList.addAll(datas);
                for (int i = 0; i < mGroupInfoList.size(); i++) {
                    mGroupingList.add(mGroupInfoList.get(i).getName());
                    ArrayList<String> itemList1 = new ArrayList<>();
                    itemList1.add(mGroupInfoList.get(i).getUserId());
                    mItemSet.add(itemList1);
                }

                mGroupingAdapter.notifyDataSetChanged();
//                groupingAdapter.loadMoreComplete();
//                groupingAdapter.loadMoreEnd(false);
                /*if (datas != null && datas.size() > 0) {

                } else {

                }*/
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
//                groupingAdapter.loadMoreFail();
            }
        });


    }

}
