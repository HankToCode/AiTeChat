package com.android.nanguo.section.conversation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

import com.android.nanguo.R;
import com.android.nanguo.app.api.Constant;
import com.android.nanguo.app.api.global.EventUtil;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.api.old_data.GroupManageListInfo;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.app.help.RclViewHelp;
import com.android.nanguo.section.conversation.adapter.SetGroupManageAdapter;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.zds.base.Toast.ToastUtil;

/**
 * @author lhb
 * 设置群管理员
 */
public class SetGroupManageActivity extends BaseInitActivity implements SetGroupManageAdapter.OnCancelGroupUserListener {


    public static void actionStart(Context context, String groupId, String emChatId) {
//        Intent starter = new Intent(context, GroupAdminAuthorityActivity.class);
        Intent starter = new Intent(context, SetGroupManageActivity.class);
        starter.putExtra("groupId", groupId);
        starter.putExtra(Constant.PARAM_EM_CHAT_ID, emChatId);
        context.startActivity(starter);
    }


    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.rv_group_manage)
    RecyclerView mRvGroupManage;
    private SetGroupManageAdapter mSetGroupManageAdapter;
    private List<GroupManageListInfo> mManageListInfoList;

    private String groupId;
    private String emChatId;

    @Override
    protected void initData() {
        super.initData();
        initLogic();
    }

    protected void initLogic() {

        mTitleBar.setTitle("设置群管理员");
        mTitleBar.setOnBackPressListener(view -> finish());

        mManageListInfoList = new ArrayList<>();

        mSetGroupManageAdapter = new SetGroupManageAdapter(mManageListInfoList);
        mSetGroupManageAdapter.setOnDelGroupUserListener(this);
        RclViewHelp.initRcLmVertical(this, mRvGroupManage, mSetGroupManageAdapter);
        mRvGroupManage.addItemDecoration(new RecyclerView.ItemDecoration() {
            @Override
            public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
                super.getItemOffsets(outRect, view, parent, state);
                outRect.top = 1;
                outRect.bottom = 1;
            }
        });
        queryGroupManage();
    }

    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.SET_GROUP_MANAGE) {
            mManageListInfoList.clear();
            queryGroupManage();
        }

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupId = intent.getStringExtra("groupId");
        emChatId = intent.getStringExtra(Constant.PARAM_EM_CHAT_ID);
    }


    /**
     * 查询群管理员
     *
     * @param
     */
    private void queryGroupManage() {
        Map<String, Object> map = new HashMap<>(1);
        map.put("groupId", groupId);
        ApiClient.requestNetHandle(this, AppConfig.LIST_GROUP_MANAGE, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null && json.length() > 0) {
                    mManageListInfoList.addAll(FastJsonUtil.getList(json, GroupManageListInfo.class));
                    mSetGroupManageAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }


    /**
     * 取消群管理员
     *
     * @param
     */
    private void CancelGroupManage(String userId, int pos) {
        Map<String, Object> map = new HashMap<>(3);
        map.put("groupId", groupId);
        map.put("userId", userId);
        //userRank0-普通用户 1-管理员
        map.put("userRank", "0");

        ApiClient.requestNetHandle(this, AppConfig.MODIFY_GROUP_MANEGER, "正在取消...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast("取消成功");
                mManageListInfoList.remove(pos);
                mSetGroupManageAdapter.notifyItemRemoved(pos);
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }


    /**
     * 取消群管理员成功回调
     */
    public void delGroup(String userId, int pos) {
        new EaseAlertDialog(this, null, "确定取消该成员的管理员的权限？", null, new EaseAlertDialog.AlertDialogUser() {

            @Override
            public void onResult(boolean confirmed, Bundle bundle) {
                if (confirmed) {
                    CancelGroupManage(userId, pos);
                }
            }
        }, true).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set_group_manage;
    }

    @OnClick({R.id.rv_group_manage, R.id.ll_footerView})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rv_group_manage:
                break;
            case R.id.ll_footerView:
                GroupUserListActivity.actionStart(this, groupId, emChatId);
                break;
            default:
                break;
        }
    }
}
