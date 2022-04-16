package com.android.nanguo.section.conversation;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.android.nanguo.R;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.base.BaseInitFragment;
import com.android.nanguo.app.help.RclViewHelp;
import com.android.nanguo.app.weight.SearchBar;
import com.android.nanguo.app.weight.SlideRecyclerView;
import com.android.nanguo.common.utils.comlist.ListCacheUtil;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.android.nanguo.app.api.old_data.GroupUserAuditInfo;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.section.conversation.adapter.GroupMemberAdapter;
import com.android.nanguo.section.conversation.adapter.GroupUserApplyAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author lhb
 * 加群申请
 */
public class GroupUserApplyFragment extends BaseInitFragment implements GroupUserApplyAdapter.OnAgreeListener {
//    SlideRecyclerView mRvNewFriend;

    SlideRecyclerView mRvNewFriend;
    SearchBar searchBar;
    private final List<GroupUserAuditInfo.DataBean> mStringList = new ArrayList<>();
    private GroupUserApplyAdapter mNewFriendAdapter;
    private HashMap<String, Integer> lettes;
    RecyclerView rv_new_friend;
    SmartRefreshLayout mSmart;
    TextView tv_no_content;
    private int page = 1;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_apply_join_group;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        searchBar = findViewById(R.id.search_bar);
        rv_new_friend = findViewById(R.id.rv_new_friend);
        mSmart = findViewById(R.id.smart);
        tv_no_content = findViewById(R.id.tv_no_content);
        searchBar.setOnSearchBarListener((s, start, before, count) -> mNewFriendAdapter.getFilter().filter(s));
        lettes = new HashMap<>();
        mSmart.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                queryUserStatus();
            }
        });

        mSmart.setOnLoadMoreListener(refreshlayout0 -> {
            page++;
            queryUserStatus();
        });
        mStringList.addAll(ListCacheUtil.groupUserAuditInfoData);
        mNewFriendAdapter = new GroupUserApplyAdapter(mStringList, mContext, lettes);
        RclViewHelp.initRcLmVertical(mContext, rv_new_friend, mNewFriendAdapter);
        mNewFriendAdapter.setOnAgreeListener(this);
        mNewFriendAdapter.setOnDelClickListener(new GroupMemberAdapter.OnDelClickListener() {
            @Override
            public void delUser(int pos) {
                delApplyUserData(pos);
            }
        });

//        mSmart.autoRefresh();
        queryUserStatus();
    }

    @Override
    protected void onEventComing(EventCenter center) {

    }


    /**
     * 查询用户申请列表
     */
    private void queryUserStatus() {
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", page);
        map.put("pageSize", 10);
        ApiClient.requestNetHandle(mContext, AppConfig.FIND_APPLY_GROUP_USER, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (page == 1) {
                    mStringList.clear();
                    ListCacheUtil.groupUserAuditInfoData.clear();
                }
                GroupUserAuditInfo info = FastJsonUtil.getObject(json, GroupUserAuditInfo.class);
                if (info != null && info.getData().size() > 0) {
                    mStringList.addAll(info.getData());
                    ListCacheUtil.groupUserAuditInfoData.addAll(info.getData());
                    tv_no_content.setVisibility(View.GONE);
                } else {
                    if (page == 1) {
                        tv_no_content.setVisibility(View.VISIBLE);
                    }
                    mSmart.setEnableLoadMore(false);
                    page = 1;
                }
                mSmart.finishLoadMore();
                mNewFriendAdapter.notifyDataSetChanged();
                if (mSmart != null && mSmart.isRefreshing()) {
                    mSmart.finishRefresh();
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                if (mSmart != null && mSmart.isRefreshing()) {
                    mSmart.finishRefresh();
                }
            }
        });

    }


    private void delApplyUserData(int position) {
        Map<String, Object> map = new HashMap<>();
        map.put("applyId", mStringList.get(position).getApplyId());
        ApiClient.requestNetHandle(mContext, AppConfig.DEL_APPLY_GROUP_USER, "", map, null);
        mStringList.remove(position);
        mNewFriendAdapter.notifyDataSetChanged();
    }
    /**
     * 同意拒绝好友申请
     */
    private void agreeApply(String applyId, int type,String groupId,String userId) {
        Map<String, Object> map = new HashMap<>();
        map.put("applyId", applyId);
        map.put("applyStatus", type);
        map.put("userId", userId);
        map.put("groupId", groupId);

        ApiClient.requestNetHandle(mContext, AppConfig.AGREE_GROUP_USER, type == 1 ? "正在同意..." : "正在拒绝...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                page = 1;
                queryUserStatus();

                if (type == 1) {
                    ToastUtil.toast("已同意");
                } else {
                    ToastUtil.toast("已拒绝");
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });

    }


    /**
     * 进群申请同意
     */
    @Override
    public void agree(String applyId, int type,String groupId,String userId) {
        agreeApply(applyId, type,groupId,userId);
    }


}
