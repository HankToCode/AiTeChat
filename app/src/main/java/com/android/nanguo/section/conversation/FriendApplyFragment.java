package com.android.nanguo.section.conversation;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.android.nanguo.R;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.base.BaseInitFragment;
import com.android.nanguo.app.help.RclViewHelp;
import com.android.nanguo.app.weight.SearchBar;
import com.android.nanguo.app.weight.SlideRecyclerView;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.android.nanguo.app.api.old_data.ApplyFriendData;
import com.android.nanguo.app.api.old_data.NewFriendInfo;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.section.conversation.adapter.NewFriendAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FriendApplyFragment extends BaseInitFragment implements NewFriendAdapter.OnAgreeListener {
    SlideRecyclerView mRvNewFriend;
    SearchBar searchBar;
    private final List<ApplyFriendData> mStringList = new ArrayList<>();
    private NewFriendAdapter mNewFriendAdapter;
    private HashMap<String, Integer> lettes;
    SmartRefreshLayout mSmart;
    TextView tv_no_content;
    private int page = 1;

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        searchBar = findViewById(R.id.search_bar);
        mRvNewFriend = findViewById(R.id.rv_new_friend);
        searchBar.setOnSearchBarListener((s, start, before, count) -> mNewFriendAdapter.getFilter().filter(s));
        mSmart = findViewById(R.id.smart);
        tv_no_content = findViewById(R.id.tv_no_content);
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
        mNewFriendAdapter = new NewFriendAdapter(mStringList, mContext, lettes);
        RclViewHelp.initRcLmVertical(mContext, mRvNewFriend, mNewFriendAdapter);
        mNewFriendAdapter.setOnAgreeListener(this);

        mNewFriendAdapter.setOnDelClickListener(new NewFriendAdapter.OnDelClickListener() {
            @Override
            public void delUser(int pos) {
                delApplyUserData(pos);
            }
        });
//        mSmart.autoRefresh();
        queryUserStatus();
    }

    @Override
    public void onResume() {
        super.onResume();
        queryUserStatus();
    }

    /**
     * 查询用户申请列表
     */
    private void delApplyUserData(int position) {
        Map<String, Object> map = new HashMap<>();
        map.put("applyId", mStringList.get(position).getApplyId());
        ApiClient.requestNetHandle(mContext, AppConfig.DEL_APPLY_ADD_USER, "", map, null);
        mStringList.remove(position);
        mNewFriendAdapter.notifyDataSetChanged();
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
        map.put("pageSize", 15);
        ApiClient.requestNetHandle(mContext, AppConfig.APPLY_ADD_USER_LIST, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (null != json && json.length() > 0) {
                    if (page == 1) {
                        mStringList.clear();
                    }
                    NewFriendInfo info = FastJsonUtil.getObject(json, NewFriendInfo.class);
                    if (info != null && info.getData().size() > 0) {
                        mStringList.addAll(info.getData());
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

    /**
     * 同意拒绝好友申请
     */
    private void agreeApply(String applyId, int type) {
        Map<String, Object> map = new HashMap<>();
        map.put("applyId", applyId);
        map.put("applyStatus", type);

        ApiClient.requestNetHandle(mContext, AppConfig.APPLY_ADD_USER_STATUS, type == 1 ? "正在同意..." : "正在拒绝...", map, new ResultListener() {
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
     * 好友申请同意
     */
    @Override
    public void agree(String applyId, int type) {
        agreeApply(applyId, type);
    }


    public boolean isDisGoodFriend() {
        int q = 0;
        for (int i = 0; i < mStringList.size(); i++) {
            if (!"0".equals(mStringList.get(i).getApplyStatus())) {
                q += 1;
            }
        }
        return q == mStringList.size();
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_new_friend;
    }
}
