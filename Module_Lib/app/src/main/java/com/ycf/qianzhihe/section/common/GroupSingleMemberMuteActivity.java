package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.JsonArray;
import com.hyphenate.chat.EMClient;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.R2;
import com.ycf.qianzhihe.app.adapter.GroupSingleMemberMuteAdapter;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_data.GroupDetailInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.operate.GroupOperateManager;
import com.ycf.qianzhihe.app.weight.SearchBar;
import com.hyphenate.exceptions.HyphenateException;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import com.zds.base.Toast.ToastUtil;

public class GroupSingleMemberMuteActivity extends BaseInitActivity {
    @BindView(R2.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R2.id.searchBar)
    SearchBar searchBar;
    @BindView(R2.id.recyclerView)
    RecyclerView recyclerView;

    private String emChatId = "";
    private String groupId = "";

    private GroupSingleMemberMuteAdapter mAdapter;

    private GroupDetailInfo groupDetailInfo;
    private List<GroupDetailInfo.GroupUserDetailVoListBean> mGroupMemberBeans = new ArrayList<>();
    private List<GroupDetailInfo.GroupUserDetailVoListBean> mGroupUserDetailVoListBeans = new ArrayList<>();

    public static void start(Context context, String emChatId, String groupId) {
        Intent intent = new Intent(context, GroupSingleMemberMuteActivity.class);
        intent.putExtra("key_intent_emChatId", emChatId);
        intent.putExtra("key_intent_groupId", groupId);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_transfer_group;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        getIntentData();
        setTitle("群内禁言");
        initRecyclerView();

        searchBar.setOnSearchBarListener(new SearchBar.OnSearchBarListener() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (mGroupMemberBeans != null && mGroupMemberBeans.size() > 0) {
                    String inputStr = s.toString();
                    if (TextUtils.isEmpty(inputStr)) {
                        mAdapter.setNewData(mGroupMemberBeans);
                        return;
                    }

                    mGroupUserDetailVoListBeans.clear();
                    for (int i = 0; i < mGroupMemberBeans.size(); i++) {
                        GroupDetailInfo.GroupUserDetailVoListBean groupUserDetailVoListBean = mGroupMemberBeans.get(i);
                        if (groupUserDetailVoListBean.getUserNickName().contains(inputStr)) {
                            mGroupUserDetailVoListBeans.add(groupUserDetailVoListBean);
                        }
                    }
                    mAdapter.setNewData(mGroupUserDetailVoListBeans);
                }
            }
        });

        loadGroupDataFromLocal();
    }

    private void getIntentData() {
        Intent intent = getIntent();
        emChatId = intent.getStringExtra("key_intent_emChatId");
        groupId = intent.getStringExtra("key_intent_groupId");
    }

    private void initRecyclerView() {
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new GroupSingleMemberMuteAdapter();
        recyclerView.setAdapter(mAdapter);

        /*mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GroupDetailInfo.GroupUserDetailVoListBean groupUsertBean = mAdapter.getData().get(position);
                showCommonConfirmDialog(groupUsertBean);
            }
        });*/

        mAdapter.setOnItemChildClickListener(new BaseQuickAdapter.OnItemChildClickListener() {
            @Override
            public void onItemChildClick(BaseQuickAdapter adapter, View view, int position) {
                if (view.getId() == R.id.iv_switch_mute) {
                    modifyGroupSingleMemberSayStatus(position);
                }

            }
        });

    }

    public void loadGroupDataFromLocal() {
        groupDetailInfo = GroupOperateManager.getInstance().getGroupMemberList(emChatId);
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        if (groupDetailInfo != null)
            setGroupMemberData();

        ApiClient.requestNetHandle(this, AppConfig.CHECK_GROUP_DATA_VERSION, "",
                map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
//                        int groupVersion = FastJsonUtil.getInt(json, "groupVersion");
                        //本地数据版本更服务器不一致 就需要更新数据接口
//                        if (groupDetailInfo.getGroupVersion() != groupVersion) {
                        queryGroupDetail();
//                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        queryGroupDetail();
                    }
                });

        //queryGroupDetail();

    }

    public void queryGroupDetail() {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        ApiClient.requestNetHandle(this, AppConfig.GET_GROUP_DETAIL, "请稍等...",
                map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        if (json != null && json.length() > 0) {
                            groupDetailInfo = FastJsonUtil.getObject(json, GroupDetailInfo.class);
                            setGroupMemberData();
                            GroupOperateManager.getInstance().saveGroupMemberList(emChatId, groupDetailInfo, json);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                    }
                });
    }

    public void setGroupMemberData() {
        if (null != groupDetailInfo && groupDetailInfo.getGroupUserDetailVoList().size() > 0) {
            mGroupMemberBeans.clear();
            List<GroupDetailInfo.GroupUserDetailVoListBean> groupUserDetailVoList = groupDetailInfo.getGroupUserDetailVoList();
            for (int i = 0; i < groupUserDetailVoList.size(); i++) {
                GroupDetailInfo.GroupUserDetailVoListBean groupUserDetailVoListBean = groupUserDetailVoList.get(i);
                //只显示普通成员
                if (groupUserDetailVoListBean.getUserRank().equals("0")) {
                    mGroupMemberBeans.add(groupUserDetailVoListBean);
                }
            }

            mAdapter.setNewData(mGroupMemberBeans);
        }

    }

    private void modifyGroupSingleMemberSayStatus(int position) {
        /*List<String> muteMembers = new ArrayList<>();
        //获取禁言列表
        if (info.getGroupUserDetailVoList().size() > 0) {
            for (GroupDetailInfo.GroupUserDetailVoListBean bean : info.getGroupUserDetailVoList()) {
                //用户等级 0-普通用户 1-管理员 2-群主
                if (bean.getUserRank().equals("0")) {
                    muteMembers.add(bean.getUserId() + Constant.ID_REDPROJECT);
                }
            }
        }*/

        GroupDetailInfo.GroupUserDetailVoListBean groupUserBean = mAdapter.getData().get(position);

        Map<String, Object> map = new HashMap<>(1);
        map.put("groupId", groupId);
        //0 - 取消禁言 1 - 禁言
        String sayStatus = "0".equals(groupUserBean.getSayStatus()) ? "1" : "0";
        map.put("sayStatus", sayStatus);

        JsonArray jsonArray = new JsonArray();
        jsonArray.add(groupUserBean.getUserId());

        map.put("userIds", jsonArray);

        ApiClient.requestNetHandle(this,
                AppConfig.MODIFY_GROUP_USER_SAY_STATUS, "", map,
                new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {

                        try {
                            EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }

                        if (sayStatus.equals("1")) {
                            ToastUtil.toast("禁言成功");
                            groupUserBean.setSayStatus("1");
                            mAdapter.notifyItemChanged(position);
                        } else if (sayStatus.equals("0")) {
                            ToastUtil.toast("取消禁言成功");
                            groupUserBean.setSayStatus("0");
                            mAdapter.notifyItemChanged(position);
                        }

                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });
    }

    @Override
    protected void onEventComing(EventCenter center) {

    }
}