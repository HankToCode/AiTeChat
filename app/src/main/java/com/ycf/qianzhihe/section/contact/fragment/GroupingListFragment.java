package com.ycf.qianzhihe.section.contact.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ExpandableListView;

import com.blankj.utilcode.util.LogUtils;
import com.hyphenate.easeui.constants.EaseConstant;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.new_data.FriendGroupingBean;
import com.ycf.qianzhihe.app.api.old_data.ContactListInfo;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitFragment;
import com.ycf.qianzhihe.section.chat.activity.ChatActivity;
import com.ycf.qianzhihe.section.contact.adapter.GroupingAdapter;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//我的分组
public class GroupingListFragment extends BaseInitFragment {
    //    private FriendGroupingAdapter groupingAdapter;
    private int pageSize = 100;

    private int page = 1;
    private boolean isFirstStart = true;
    //    private List<FriendGroupingBean> mGroupInfoList;
    private ExpandableListView elv_expand;
    private ArrayList<String> mGroupingNameList;//分组数据
    //item成员数据
    private ArrayList<ArrayList<ContactListInfo.DataBean>> mItemSet;
    private GroupingAdapter mGroupingAdapter;


    @Override
    protected int getLayoutId() {
        return R.layout.grouping_fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        elv_expand = findViewById(R.id.elv_expand);
    }




    @Override
    protected void initListener() {
        super.initListener();
        elv_expand.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                String userId = mItemSet.get(groupPosition).get(childPosition).getFriendUserId();
                if (!userId.contains(Constant.ID_REDPROJECT)) {//friendUserId
                    userId += Constant.ID_REDPROJECT;
                }
                ChatActivity.actionStart(mContext, userId, EaseConstant.CHATTYPE_SINGLE);
                return true;
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();
//        mGroupInfoList = new ArrayList<>();
        mGroupingNameList = new ArrayList<>();
        mItemSet = new ArrayList<>();
        mGroupingAdapter = new GroupingAdapter(mContext, mGroupingNameList, mItemSet);
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
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.FLUSHGROUPING) {
            Log.d("TAG", "刷新好友分组");
            groupingList();
        }
    }


    private List<FriendGroupingBean> categoryDatas = new ArrayList<>();
    private void groupingList() {
        Map<String, Object> map = new HashMap<>(2);
        ApiClient.requestNetHandle(requireContext(), AppConfig.selectFriendCategory, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                categoryDatas.clear();
                categoryDatas = FastJsonUtil.getList(json, FriendGroupingBean.class);
                /*if (page == 0 && mGroupInfoList.size() > 0) {
                    mGroupInfoList.clear();
                }
                mGroupInfoList.addAll(datas);*/
                getContactList();

            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    public void getContactList() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("pageNum", 1);
        map.put("pageSize", 10000);
        //未同步通讯录到本地
        ApiClient.requestNetHandle(getActivity(), AppConfig.USER_FRIEND_LIST,
                "", map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        ContactListInfo info = FastJsonUtil.getObject(json,
                                ContactListInfo.class);
                        List<ContactListInfo.DataBean> mContactList = new ArrayList<>();
                        mContactList.addAll(info.getData());
                        mGroupingNameList.clear();//分组名
                        mItemSet.clear();//分组成员
                        //读取本地缓存好友数据
//                        List<ContactListInfo.DataBean> localFriendList = UserOperateManager.getInstance().getContactList();
                        if (categoryDatas.size() > 0) {
                            for (int i = 0; i < categoryDatas.size(); i++) {
                                mGroupingNameList.add(categoryDatas.get(i).getName());
                                ArrayList<ContactListInfo.DataBean> item = new ArrayList<>();
                                for (int k = 0; k < mContactList.size(); k++) {
                                    if (categoryDatas.get(i).getCategoryId().equals(mContactList.get(k).getCategoryId())) {
                                        item.add(mContactList.get(k));//成员
                                    }
                                }
                                mItemSet.add(item);
                            }
                        }
                        mGroupingAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });


        //// sorting

    }

}
