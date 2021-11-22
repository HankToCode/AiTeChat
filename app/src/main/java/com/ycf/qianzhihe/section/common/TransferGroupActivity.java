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
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.adapter.TransferGroupAdapter;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_data.GroupDetailInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.operate.GroupOperateManager;
import com.ycf.qianzhihe.app.weight.CommonConfirmDialog;
import com.ycf.qianzhihe.app.weight.SearchBar;
import com.zds.base.json.FastJsonUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import com.zds.base.Toast.ToastUtil;

public class TransferGroupActivity extends BaseInitActivity {

    @BindView(R.id.toolbar_title)
    TextView toolbar_title;
    @BindView(R.id.searchBar)
    SearchBar searchBar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;

    private String emChatId = "";
    private String groupId = "";

    private TransferGroupAdapter mAdapter;

    private GroupDetailInfo groupDetailInfo;
    private List<GroupDetailInfo.GroupUserDetailVoListBean> mGroupMemberBeans = new ArrayList<>();
    private List<GroupDetailInfo.GroupUserDetailVoListBean> mGroupUserDetailVoListBeans = new ArrayList<>();

    public static void start(Context context, String emChatId, String groupId) {
        Intent intent = new Intent(context, TransferGroupActivity.class);
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
        setTitle("转让该群");
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
        mAdapter = new TransferGroupAdapter();
        recyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                GroupDetailInfo.GroupUserDetailVoListBean groupUsertBean = mAdapter.getData().get(position);
                showCommonConfirmDialog(groupUsertBean);
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
                if (!groupUserDetailVoListBean.getUserRank().equals("2")) {
                    mGroupMemberBeans.add(groupUserDetailVoListBean);
                }
            }

            mAdapter.setNewData(mGroupMemberBeans);
        }

    }

    private CommonConfirmDialog mCommonConfirmDialog;

    private void showCommonConfirmDialog(GroupDetailInfo.GroupUserDetailVoListBean groupUsertBean) {
        if (mCommonConfirmDialog == null) {
            mCommonConfirmDialog = new CommonConfirmDialog(this);
            mCommonConfirmDialog.setOnConfirmClickListener(new CommonConfirmDialog.OnConfirmClickListener() {
                @Override
                public void onConfirmClick(View view) {
                    transferGroup(groupUsertBean);
                }
            });
        }
        mCommonConfirmDialog.show();
        mCommonConfirmDialog.setTitle("转让");
        mCommonConfirmDialog.setContent("转让给" + groupUsertBean.getUserNickName() + "后，你将失去群主身份");
    }

    private void transferGroup(GroupDetailInfo.GroupUserDetailVoListBean groupUsertBean) {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("userId", groupUsertBean.getUserId());

        ApiClient.requestNetHandle(this, AppConfig.TRANSFER_GROUP, "正在转让群...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast("转让成功");
                //通知群详情页面刷新数据
                EventBus.getDefault().post(new EventCenter<>(EventUtil.TRANSFER_GROUP));
                finish();
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