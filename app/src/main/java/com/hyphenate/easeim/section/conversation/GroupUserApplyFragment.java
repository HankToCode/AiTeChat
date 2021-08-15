package com.hyphenate.easeim.section.conversation;

import android.os.Bundle;

import androidx.annotation.Nullable;

import com.hyphenate.easeim.R;
import com.hyphenate.easeim.app.api.old_data.EventCenter;
import com.hyphenate.easeim.app.api.old_data.GroupUserAuditInfo;
import com.hyphenate.easeim.app.api.old_http.ApiClient;
import com.hyphenate.easeim.app.api.old_http.AppConfig;
import com.hyphenate.easeim.app.api.old_http.ResultListener;
import com.hyphenate.easeim.app.base.BaseInitFragment;
import com.hyphenate.easeim.app.help.RclViewHelp;
import com.hyphenate.easeim.app.weight.SearchBar;
import com.hyphenate.easeim.app.weight.SlideRecyclerView;
import com.hyphenate.easeim.section.conversation.adapter.GroupMemberAdapter;
import com.hyphenate.easeim.section.conversation.adapter.GroupUserApplyAdapter;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author lhb
 * 加群申请
 */
public class GroupUserApplyFragment extends BaseInitFragment implements GroupUserApplyAdapter.OnAgreeListener {
    SlideRecyclerView mRvNewFriend;
    SearchBar searchBar;
    private List<GroupUserAuditInfo.DataBean> mStringList = new ArrayList<>();
    private GroupUserApplyAdapter mNewFriendAdapter;
    private HashMap<String, Integer> lettes;

    private int page = 1;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_apply_join_group;
    }

    @Override
    protected void initView(@Nullable Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        searchBar = findViewById(R.id.search_bar);
        mRvNewFriend = findViewById(R.id.rv_new_friend);

        searchBar.setOnSearchBarListener((s, start, before, count) -> mNewFriendAdapter.getFilter().filter(s));

        lettes = new HashMap<>();

        mNewFriendAdapter = new GroupUserApplyAdapter(mStringList, mContext, lettes);
        RclViewHelp.initRcLmVertical(mContext, mRvNewFriend, mNewFriendAdapter);
        mNewFriendAdapter.setOnAgreeListener(this);
        queryUserStatus();
        mNewFriendAdapter.setOnDelClickListener(new GroupMemberAdapter.OnDelClickListener() {
            @Override
            public void delUser(int pos) {
                delApplyUserData(pos);
            }
        });
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
        map.put("pageSize", 100);
        ApiClient.requestNetHandle(mContext, AppConfig.FIND_APPLY_GROUP_USER, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (null != json && json.length() > 0) {
                    if (page == 1 && mStringList.size() > 0) {
                        mStringList.clear();
                    }
                    GroupUserAuditInfo info = FastJsonUtil.getObject(json, GroupUserAuditInfo.class);

                    mStringList.addAll(info.getData());
                    mNewFriendAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String msg) {

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
