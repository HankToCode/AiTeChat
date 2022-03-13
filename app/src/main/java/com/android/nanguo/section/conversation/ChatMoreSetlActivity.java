package com.android.nanguo.section.conversation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.android.nanguo.R;
import com.android.nanguo.app.api.Constant;
import com.android.nanguo.app.api.global.EventUtil;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.api.old_data.GroupDetailInfo;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.app.operate.GroupOperateManager;
import com.android.nanguo.app.weight.MyDialog;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;

import org.greenrobot.eventbus.EventBus;

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
 * 更多设置
 */
public class ChatMoreSetlActivity extends BaseInitActivity {

    @BindView(R.id.rl_black)
    RelativeLayout mRlBlack;
    @BindView(R.id.tv_report)
    TextView mTvReport;
    @BindView(R.id.switch_shut_up)
    CheckBox mSwitchShutUp;
    @BindView(R.id.fl_group_jinyan)
    FrameLayout mFlGroupJinyan;
    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.tv_del_friend)
    TextView tvDel;
    @BindView(R.id.switch_black)
    CheckBox mSwitchBlack;
    @BindView(R.id.tv_group_manager)
    TextView mTvGroupManager;
    /**
     * groupId 群id
     * mUserList 群成员列表
     */
    private String groupId;

    //1单聊用户详情过来，2群详情过来
    private String fromPerson;

    private String emChatId;
    private String nickName;
    private boolean isFriend;

    private final List<GroupDetailInfo.GroupUserDetailVoListBean> mUserList =
            new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_more_set;
    }

    @Override
    protected void initData() {
        super.initData();
        initLogic();
    }

    protected void initLogic() {
        mTitleBar.setTitle("更多设置");
        mTitleBar.setOnBackPressListener(view -> finish());

        if (isFriend) {
            tvDel.setVisibility(View.VISIBLE);
        }

        //加入黑名单
        mSwitchBlack.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                blackContact("1");
            } else {
                blackContact("0");
            }
        });

        if (!fromPerson.equals("1")) {
            GroupDetailInfo groupDetailInfo = GroupOperateManager.getInstance().getGroupData(emChatId);

            if (groupDetailInfo.getGroupSayFlag().equals("0")) {
                mSwitchShutUp.setChecked(false);
            } else {
                mSwitchShutUp.setChecked(true);
            }

            mSwitchShutUp.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (isChecked) {
                    modifyGroupUserSayStatus("1");
                } else {
                    modifyGroupUserSayStatus("0");
                }
            });
        }
    }

    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.SET_CHAT_BG) {
            finish();
        }
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        emChatId = intent.getStringExtra(Constant.PARAM_EM_CHAT_ID);
        nickName = intent.getStringExtra(Constant.NICKNAME);
        isFriend = intent.getBooleanExtra("isFriend", false);
        fromPerson = intent.getStringExtra("from");
        if ("1".equals(fromPerson)) {
            mRlBlack.setVisibility(View.VISIBLE);
        } else {
            if (intent.getIntExtra("userRank", 0) == 0) {
                mFlGroupJinyan.setVisibility(View.GONE);
            } else {
                //userRank 用户等级 0-普通用户 1-管理员 2-群主
                mFlGroupJinyan.setVisibility(View.VISIBLE);
                mTvGroupManager.setVisibility(intent.getIntExtra("userRank", 0) == 2
                        ? View.VISIBLE : View.GONE);
            }
            GroupDetailInfo info = GroupOperateManager.getInstance().getGroupData(emChatId);

            mUserList.addAll(info.getGroupUserDetailVoList());

            groupId = intent.getStringExtra("groupId");
            mTvReport.setVisibility(View.VISIBLE);

        }
    }


    @OnClick({R.id.tv_chat_bg, R.id.tv_del_friend, R.id.tv_report, R.id.tv_clear_history,
            R.id.tv_group_manager})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_chat_bg:
                //聊天背景
                ChatBgActivity.actionStart(this, "1", emChatId);
                break;
            case R.id.tv_report:
                //举报
                startActivity(new Intent(this, ReportActivity.class).putExtra("from", "2").putExtra("userGroupId", groupId));

                break;
            case R.id.tv_del_friend:
                new MyDialog(this)
                        .setTitle("删除联系人")
                        .setMessage("将联系人 " + nickName + " 删除，将同时删除与该联系人的聊天记录")
                        .setPositiveButton("删除", new MyDialog.OnMyDialogButtonClickListener() {
                            @Override
                            public void onClick() {
                                deleteContact();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();

                break;
            case R.id.tv_clear_history:
                //清空聊天记录
                new EaseAlertDialog(ChatMoreSetlActivity.this, null,
                        "确定清空聊天记录吗？", null, (confirmed, bundle) -> {
                    if (confirmed) {
                        clearSingleChatHistory();
                    }
                }, true).show();
                break;
            case R.id.tv_group_manager:
                //设置管理员
                //设置群管理员
                SetGroupManageActivity.actionStart(this, groupId, emChatId);
                break;

            default:
                break;
        }
    }


    /**
     * 禁言/取消禁言
     *
     * @param
     */
    private void modifyGroupUserSayStatus(String sayStatus) {
        List<String> muteMembers = new ArrayList<>();
        //获取禁言列表
        if (mUserList.size() > 0) {
            for (GroupDetailInfo.GroupUserDetailVoListBean bean : mUserList) {
                //用户等级 0-普通用户 1-管理员 2-群主
                if (bean.getUserRank().equals("0")) {
                    muteMembers.add(bean.getUserId() + Constant.ID_REDPROJECT);
                }
            }
        }


        Map<String, Object> map = new HashMap<>(1);
        map.put("groupId", groupId);
        //0 - 取消禁言 1 - 禁言
        map.put("sayStatus", sayStatus);

        ApiClient.requestNetHandle(this,
                AppConfig.MODIFY_GROUP_ALL_USER_SAY_STATUS, "", map,
                new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {

                        try {
                            EMClient.getInstance().groupManager().getJoinedGroupsFromServer();
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }

                        ToastUtil.toast(msg);
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });
    }


    /**
     * delete contact
     */
    public void deleteContact() {
        Map<String, Object> map = new HashMap<>(1);
        map.put("friendUserId", emChatId.split("-")[0]);
        ApiClient.requestNetHandle(this, AppConfig.DEL_USER_FRIEND, "正在删除..."
                , map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        try {
                            EMClient.getInstance().contactManager().deleteContact(emChatId);
                            EMClient.getInstance().chatManager().deleteConversation(emChatId, false);
                        } catch (HyphenateException e) {
                            e.printStackTrace();
                        }
                        EventBus.getDefault().post(new EventCenter<>(EventUtil.DELETE_CONTACT));
                        EventBus.getDefault().post(new EventCenter<>(EventUtil.REFRESH_REMARK));
                        ToastUtil.toast("删除成功");
                        finish();

                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });
    }


    /**
     * 拉黑好友
     */
    public void blackContact(String blackStatus) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("friendUserId", emChatId.split("-")[0]);
        //拉黑状态 0-未拉黑 1-已拉黑
        map.put("blackStatus", blackStatus);

        ApiClient.requestNetHandle(this, AppConfig.BLACK_USER_FRIEND,
                mSwitchBlack.isChecked() ? "正在拉黑..." : "正在取消拉黑...", map,
                new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        if (mSwitchBlack.isChecked()) {
                            ToastUtil.toast("拉黑成功");
                            EMClient.getInstance().chatManager().deleteConversation(emChatId.contains(Constant.ID_REDPROJECT) ? emChatId : emChatId + Constant.ID_REDPROJECT, false);

                            EventBus.getDefault().post(new EventCenter<>(EventUtil.OPERATE_BLACK));
                            finish();
                        }

                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });
    }


    /**
     * 清空单聊聊天记录
     */
    private void clearSingleChatHistory() {
        EventBus.getDefault().post(new EventCenter<>(EventUtil.CLEAR_HUISTROY));
        Toast.makeText(this, R.string.messages_are_empty, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
