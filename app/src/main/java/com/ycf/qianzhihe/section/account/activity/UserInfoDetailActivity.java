package com.ycf.qianzhihe.section.account.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.Global;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_data.FriendInfo;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.ActivityStackManager;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.operate.UserOperateManager;
import com.ycf.qianzhihe.app.utils.ProjectUtil;
import com.ycf.qianzhihe.app.utils.hxSetMessageFree.EaseSharedUtils;
import com.ycf.qianzhihe.app.utils.my.MyHelper;
import com.ycf.qianzhihe.app.weight.MyDialog;
import com.ycf.qianzhihe.section.chat.activity.ChatActivity;
import com.ycf.qianzhihe.section.conversation.ChatBgActivity;
import com.ycf.qianzhihe.section.conversation.ChatMoreSetlActivity;
import com.ycf.qianzhihe.section.conversation.ChatRecordActivity;
import com.ycf.qianzhihe.section.conversation.ModifyFriendRemarkActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;
import com.ycf.qianzhihe.section.conversation.ReportActivity;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.log.XLog;
import com.zds.base.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author lhb
 * 用户详情
 */
public class UserInfoDetailActivity extends BaseInitActivity {
    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.img_head)
    ImageView mImgHead;
    @BindView(R.id.tv_account)
    TextView mTvAccount;
    @BindView(R.id.tv_nick_name)
    TextView mTvNickName;
    @BindView(R.id.ll_friend)
    LinearLayout mLlFriend;
    @BindView(R.id.tv_add_friend)
    TextView mTvAddFriend;
    @BindView(R.id.line_status)
    TextView mTvLineStatus;
    @BindView(R.id.switch_msg)
    CheckBox mSwitchMsg;
    @BindView(R.id.switch_mute)
    CheckBox mSwitchMute;
    @BindView(R.id.rl_mute)
    View rlMute;
    @BindView(R.id.tv_inviter)
    TextView tvInviter;
    @BindView(R.id.layout_inviter)
    RelativeLayout layoutInviter;
    @BindView(R.id.tv_remark)
    TextView tv_remark;
    @BindView(R.id.llay_remark)
    RelativeLayout mLlayRemark;
    @BindView(R.id.kick_out)
    View kickOut;
    @BindView(R.id.switch_top_conversation)
    CheckBox mSwitchTopConversation;
    @BindView(R.id.fl_send_msg)
    FrameLayout flSendButton;
    private String userId, userName;
    private String inviterUserId;
    private int chatType;
    private String emGroupId;
    private String groupId;
    private String from = "";
    private FriendInfo info;
    @BindView(R.id.tv_sign)
    TextView tv_sign;
    @BindView(R.id.switch_black)
    CheckBox mSwitchBlack;
    @BindView(R.id.iv_online_status)
    ImageView iv_online_status;
    @BindView(R.id.iv_start)
    ImageView iv_start;
    @BindView(R.id.ll_grouping)
    LinearLayout ll_grouping;
    @BindView(R.id.tv_grouping)
    TextView tv_grouping;
    @BindView(R.id.tv_del_friend)
    TextView tv_del_friend;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_userinfo_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initLogic();
    }

    protected void initLogic() {
        mTitleBar.setTitle("用户详情");
        mTitleBar.setOnBackPressListener(view -> finish());
        /*mImgRight.setOnClickListener(v ->
                startActivity(new Intent(UserInfoDetailActivity.this, ChatMoreSetlActivity.class)
                        .putExtra(Constant.PARAM_EM_CHAT_ID, userId + Constant.ID_REDPROJECT)
//                        .putExtra(Constant.NICKNAME, info.getFriendNickName()==null?info.getUserNickName():info.getFriendNickName())
                        .putExtra(Constant.NICKNAME, userName)
                        .putExtra("isFriend", info.getFriendFlag().equals("1"))
                        .putExtra("from", "1")));*/

        if (userId.contains(UserComm.getUserId())) {
            //自己
            LoginInfo loginInfo =
                    UserComm.getUserInfo();
            GlideUtils.GlideLoadCircleErrorImageUtils(UserInfoDetailActivity.this, loginInfo.getUserHead(), mImgHead,
                    R.mipmap.img_default_avatar);
            mTvNickName.setText(loginInfo.getNickName());
            mTvAccount.setText(loginInfo.getUserCode());
            mLlFriend.setVisibility(View.GONE);
            mLlayRemark.setVisibility(View.GONE);
            rlMute.setVisibility(View.GONE);
            kickOut.setVisibility(View.GONE);
            mTvAddFriend.setVisibility(View.GONE);
        } else {
            if ("3".equals(from)) {
                mTvAddFriend.setText("移出黑名单");
            }


            if (!TextUtils.isEmpty(inviterUserId)) {
                queryInviter();
            }

            if (!TextUtils.isEmpty(emGroupId)) {
                getGroupMuteList();
                rlMute.setVisibility(View.VISIBLE);
                kickOut.setVisibility(View.GONE);
            }
        }
        queryFriendInfo();

        String isMsgFree =
                MyHelper.getInstance().getModel().getConversionMsgIsFree(userId.contains(Constant.ID_REDPROJECT) ? userId :
                        userId + Constant.ID_REDPROJECT);
        if (null != isMsgFree && isMsgFree.equals("false")) {
            //设置消息免打扰
            mSwitchMsg.setChecked(true);
        } else {
            //取消消息免打扰
            mSwitchMsg.setChecked(false);
        }

        //消息免打扰
        mSwitchMsg.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!userId.contains(Constant.ID_REDPROJECT)) {
                userId += Constant.ID_REDPROJECT;
            }
            if (isChecked) {
                EaseSharedUtils.setEnableMsgRing(Utils.getContext(),
                        UserComm.getUserId() + Constant.ID_REDPROJECT,
                        userId, false);
                MyHelper.getInstance().getModel().saveChatBg(userId,
                        null, "false", null);
            } else {
                EaseSharedUtils.setEnableMsgRing(Utils.getContext(),
                        UserComm.getUserId() + Constant.ID_REDPROJECT,
                        userId, true);
                MyHelper.getInstance().getModel().saveChatBg(userId,
                        null, "true", null);
            }
        });

        /*mEtRemark.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_GO) {
                    addRemark();
                }
                return false;
            }
        });*/

        //加入黑名单
        mSwitchBlack.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                blackContact("1");
            } else {
                blackContact("0");
            }
        });


        EMConversation emConversation = EMClient.getInstance().chatManager().getConversation(userId + Constant.ID_REDPROJECT);
        if (emConversation != null) {
            if (emConversation.getExtField().equals("toTop")) {
                mSwitchTopConversation.setChecked(true);
            } else {
                mSwitchTopConversation.setChecked(false);
            }
        }


        //会话置顶
        mSwitchTopConversation.setOnCheckedChangeListener((buttonView,
                                                           isChecked) -> {

            if (!emConversation.conversationId().equals(Constant.ADMIN)) {
                if (emConversation.conversationId().contains(userId)) {
                    if (isChecked) {
                        emConversation.setExtField("toTop");
                    } else {
                        emConversation.setExtField("false");
                    }
                }
            }
        });
    }

    private void setMuteListener() {
        mSwitchMute.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                modifyGroupUserSayStatus(b);
            }
        });
    }

    private void addRemark() {
        Map<String, Object> map = new HashMap<>();
        if (userId.contains(Constant.ID_REDPROJECT)) {
            userId = userId.split("-")[0];
        }
        map.put("friendUserId", userId);
        map.put("friendNickName", tv_remark.getText().toString().trim());
        ApiClient.requestNetHandle(this, AppConfig.ADD_GOODS_FRIEND_REMARK,
                "请稍后...", map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        String name = tv_remark.getText().toString().trim();
                        UserOperateManager.getInstance().updateUserName(userId, name);
                        //mTvNickName.setText(StringUtil.isEmpty(name) ? info.getNickName() + " " : name);

                        EventBus.getDefault().post(new EventCenter<>(EventUtil.REFRESH_REMARK));
                        ToastUtil.toast("设置成功");
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });
    }

    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.OPERATE_BLACK || center.getEventCode() == EventUtil.DELETE_CONTACT) {
            finish();
        } else if (center.getEventCode() == EventUtil.REFRESH_REMARK) {
            tv_remark.setText(UserOperateManager.getInstance().getUserName(userId));
        }
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupId = intent.getStringExtra(Constant.PARAM_GROUP_ID);
        emGroupId = intent.getStringExtra(Constant.PARAM_EM_GROUP_ID);
        userId = intent.getStringExtra("friendUserId");
        userName = intent.getStringExtra("userName");
        chatType = intent.getIntExtra("chatType", 0);
        from = intent.getStringExtra("from");
        inviterUserId = intent.getStringExtra("entryUserId");
    }

    private void getGroupMuteList() {

        EMClient.getInstance().groupManager().asyncFetchGroupMuteList(emGroupId, 0, 10000, new EMValueCallBack<Map<String, Long>>() {
            @Override
            public void onSuccess(Map<String, Long> stringLongMap) {
                for (String key : stringLongMap.keySet()) {
                    if (key.contains(userId))
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                mSwitchMute.setChecked(true);
                            }
                        });
                }

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setMuteListener();
                    }
                });


            }

            @Override
            public void onError(int i, String s) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setMuteListener();
                    }
                });
            }
        });

    }

    private void modifyGroupUserSayStatus(boolean isMute) {

        Map<String, Object> map = new HashMap<>(1);
        if (userId.contains(Constant.ID_REDPROJECT)) {
            userId = userId.split("-")[0];
        }
        String[] s = new String[1];
        s[0] = userId;
        String s1 = FastJsonUtil.toJSONString(s);
        map.put("groupId", groupId);
        map.put("userIds", s);
        map.put("sayStatus", isMute ? 1 : 0);

        ApiClient.requestNetHandle(this, AppConfig.MODIFY_GROUP_USER_SAY_STATUS, "请求提交中", map,
                new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        XLog.e("mute", msg);
                    }

                    @Override
                    public void onFailure(String msg) {
                    }
                });

//        List<String> muteMembers = new ArrayList<>();
//        muteMembers.add(friendUserId+Constant.ID_REDPROJECT);
//        if (isMute) {
//
//            EMClient.getInstance().groupManager().aysncMuteGroupMembers(emGroupId, muteMembers, 12*30*24*60*60*1000, new EMValueCallBack<EMGroup>() {
//                @Override
//                public void onSuccess(EMGroup emGroup) {}
//
//                @Override
//                public void onError(int i, String s) {}
//            });
//        }else {
//            EMClient.getInstance().groupManager().asyncUnMuteGroupMembers(emGroupId, muteMembers, new EMValueCallBack<EMGroup>() {
//                @Override
//                public void onSuccess(EMGroup emGroup) {}
//
//                @Override
//                public void onError(int i, String s) {}
//            });
//        }


    }

    private void queryFriendInfo() {
        Map<String, Object> map = new HashMap<>(1);
        if (userId.contains(Constant.ID_REDPROJECT)) {
            userId = userId.split("-")[0];
        }
        map.put("friendUserId", userId);

        ApiClient.requestNetHandle(this, AppConfig.FRIEND_INFO, "", map,
                new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        if (null != json && json.length() > 0) {
                            info = FastJsonUtil.getObject(json,
                                    FriendInfo.class);
                            GlideUtils.GlideLoadCircleErrorImageUtils(UserInfoDetailActivity.this, info.getUserHead(), mImgHead,
                                    R.mipmap.img_default_avatar);
                            userName = info.getNickName();
                            mTvAccount.setText(getString(R.string.str_chat_account, info.getUserCode()));
                            mTvLineStatus.setText(info.getLine().equals(
                                    "online") ?
                                    "在线" : "离线");
                            if (TextUtils.isEmpty(info.getSign())) {
                                tv_sign.setText("这家伙很懒，啥都没写");
                            } else {
                                tv_sign.setText(info.getSign());
                            }

                            //是否在线
                            if (!TextUtils.isEmpty(info.getLine())) {
                                if (info.getLine().equals("online")) {
                                    iv_online_status.setBackgroundResource(R.drawable.dot_green);
                                } else {
                                    iv_online_status.setBackgroundResource(R.drawable.dot_gray);
                                }
                            } else {
                                iv_online_status.setBackgroundResource(R.drawable.dot_gray);
                            }

                            mTvNickName.setText(info.getNickName());
                            //好友标识 0 不是好友 1 是好友
                            if (info.getFriendFlag().equals("1")) {
                                //是不是星标 starTarget	0:未加星标，1：星标
                                if (!TextUtils.isEmpty(info.getStarTarget())) {
                                    if (info.getStarTarget().equals("1")) {
                                        iv_start.setVisibility(View.VISIBLE);
                                    } else {
                                        iv_start.setVisibility(View.GONE);
                                    }
                                } else {
                                    iv_start.setVisibility(View.GONE);
                                }
                                ll_grouping.setVisibility(View.VISIBLE);
                                tv_grouping.setText(info.getCategoryName());
                                if (info.getBlackStatus().equals("1")) {
                                    //黑名单中
                                    mLlFriend.setVisibility(View.GONE);
                                    mTvAddFriend.setVisibility(View.VISIBLE);
                                    mTvAddFriend.setText("移出黑名单");
                                    tv_del_friend.setVisibility(View.GONE);
                                } else {
                                    tv_del_friend.setVisibility(View.VISIBLE);
                                    flSendButton.setVisibility(View.VISIBLE);
                                    mLlFriend.setVisibility(View.VISIBLE);
                                    mTvAddFriend.setVisibility(View.GONE);
                                }
                                mLlayRemark.setVisibility(View.VISIBLE);
                                try {
                                    String remark =
                                            UserOperateManager.getInstance().getUserName(userId);
                                    tv_remark.setText(info.getFriendNickName());
                                    /*mEtRemark.setSelection(StringUtil.isEmpty(remark) ? 0 :
                                            remark.length());*/
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                mTvAddFriend.setVisibility(View.VISIBLE);
                                mLlFriend.setVisibility(View.GONE);
                                mLlayRemark.setVisibility(View.GONE);
                                ll_grouping.setVisibility(View.GONE);
                                tv_del_friend.setVisibility(View.GONE);//删除好友
                            }
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });
    }

    private void queryInviter() {
        Map<String, Object> map = new HashMap<>(1);
        map.put("friendUserId", inviterUserId);
        ApiClient.requestNetHandle(this, AppConfig.FRIEND_INFO, "", map,
                new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        FriendInfo inviterInfo = FastJsonUtil.getObject(json,
                                FriendInfo.class);

                        layoutInviter.setVisibility(View.VISIBLE);
                        tvInviter.setText(inviterInfo.getNickName());
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });
    }

    /**
     * 移出群员
     *
     * @param
     */
    private void delGroupUser() {
        Map<String, Object> map = new HashMap<>(1);
        map.put("groupId", groupId);
        map.put("userId", userId);

        ApiClient.requestNetHandle(this, AppConfig.DEL_GROUP_USER, "正在踢出成员...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast("踢出成功");
                EventBus.getDefault().post(new EventCenter<>(EventUtil.INVITE_USER_ADD_GROUP));
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 301 && resultCode == RESULT_OK) {
            tv_grouping.setText(data.getStringExtra("categoryName"));
            queryFriendInfo();
        }
    }

    @OnClick({R.id.tv_chat_record, R.id.fl_send_msg, R.id.tv_add_friend, R.id.kick_out, R.id.llay_remark, R.id.tv_remark
            , R.id.tv_clear_history, R.id.tv_chat_bg, R.id.tv_report, R.id.tv_del_friend, R.id.ll_grouping})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_grouping:
                Intent intent1 = new Intent(mContext, FriendGroupingActvity.class);
                intent1.putExtra("friendUserId", userId);
                intent1.putExtra("categoryId", info.getCategoryId());
                intent1.putExtra("categoryName", info.getCategoryName());
                startActivityForResult(intent1, 301);
//                FriendGroupingActvity.actionStart(mContext,userId,info.getCategoryId(),info.getCategoryName());
                break;
            case R.id.tv_del_friend:
                new MyDialog(this)
                        .setTitle("删除联系人")
                        .setMessage("将联系人 " + info.getNickName() + " 删除，将同时删除与该联系人的聊天记录")
                        .setPositiveButton("删除", new MyDialog.OnMyDialogButtonClickListener() {
                            @Override
                            public void onClick() {
                                deleteContact();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();

                break;
            case R.id.tv_report:
                //举报
                startActivity(new Intent(this, ReportActivity.class).putExtra("from", "2").putExtra("userGroupId", groupId));
                break;
            case R.id.tv_chat_bg:
                //聊天背景
                ChatBgActivity.actionStart(this, "1", info.getNickName());
                break;
            case R.id.tv_clear_history:
                //清空聊天记录
                new EaseAlertDialog(UserInfoDetailActivity.this, null,
                        "确定清空聊天记录吗？", null, (confirmed, bundle) -> {
                    if (confirmed) {
                        clearSingleChatHistory();
                    }
                }, true).show();
                break;
            case R.id.kick_out:
                delGroupUser();
                break;

            case R.id.tv_chat_record:
                //查找聊天记录
                startActivity(new Intent(this, ChatRecordActivity.class).putExtra("chatId", userId + Constant.ID_REDPROJECT));

                break;
            case R.id.fl_send_msg:
                //好友标识 0 不是好友 1 是好友
                if (info.getFriendFlag().equals("0")) {
                    ToastUtil.toast("还不是好友哦，无法发送消息");
                    return;
                }
                //发消息
                if (!userId.contains(Constant.ID_REDPROJECT)) {//friendUserId
                    userId += Constant.ID_REDPROJECT;
                }
                ActivityStackManager.getInstance().killActivity(ChatActivity.class);
                ChatActivity.actionStart(mContext, userId, EaseConstant.CHATTYPE_SINGLE);
                finish();
               /* if (chatType == Constant.CHATTYPE_SINGLE) {
                    finish();
                } else {
                    *//*Intent intent = new Intent(this, ChatActivity.class);
                    if (!userId.contains(Constant.ID_REDPROJECT)) {
                        userId += Constant.ID_REDPROJECT;
                    }
                    intent.putExtra(Constant.EXTRA_USER_ID, userId);
                    startActivity(intent);*//*
                }*/
                break;
            case R.id.tv_add_friend:
                //加好友
                //好友拉黑状态
                if (info.getFriendFlag().equals("1")) {
                    if (info.getBlackStatus() != null && info.getBlackStatus().equals("1")) {
                        blackContact("0");
                    }
                } else {
                    addUser();
                }

//                if (from.equals("3")) {
//                } else {
//                    addUser();
//                }
                break;
//            case R.id.tv_report:
//                //举报
//                startActivity(new Intent(this, ReportActivity.class).putExtra("from", "1").putExtra("userGroupId", userId));
//                break;
            case R.id.llay_remark:
            case R.id.tv_remark:
                if (info == null) {
                    return;
                }
                ModifyFriendRemarkActivity.actionStart(this, userId, tv_remark.getText().toString());
                break;
            default:
                break;
        }
    }

    /**
     * 清空单聊聊天记录
     */
    private void clearSingleChatHistory() {
        EventBus.getDefault().post(new EventCenter<>(EventUtil.CLEAR_HUISTROY));
        Toast.makeText(this, R.string.messages_are_empty, Toast.LENGTH_SHORT).show();
    }

    /**
     * 添加好友
     */
    private void addUser() {
        Map<String, Object> map = new HashMap<>();
        map.put("toUserId", ProjectUtil.transformId(userId));
        if (!TextUtils.isEmpty(Global.addUserOriginType))
            map.put("originType", Global.addUserOriginType);
        if (!TextUtils.isEmpty(Global.addUserOriginId)) {
            map.put("originName", Global.addUserOriginName);
            map.put("originId", Global.addUserOriginId);
        }
        ApiClient.requestNetHandle(this, AppConfig.APPLY_ADD_USER, "正在添加",
                map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        EMMessage cmdMsg =
                                EMMessage.createSendMessage(EMMessage.Type.CMD);

                        //支持单聊和群聊，默认单聊，如果是群聊添加下面这行
                        //cmdMsg.setChatType(ChatType.GroupChat)
                        //action可以自定义
                        String action = Constant.ACTION_APPLY_ADD_FRIEND;
                        EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
                        cmdMsg.addBody(cmdBody);
                        //发送给某个人
                        String toUsername =
                                userId.contains(Constant.ID_REDPROJECT) ?
                                        userId :
                                        userId + Constant.ID_REDPROJECT;

                        cmdMsg.setTo(toUsername);
                        cmdMsg.setFrom(UserComm.getUserId());
                        cmdMsg.setAttribute(Constant.APPLY_ADD_FRIEND_ID,
                                UserComm.getUserInfo().getUserId());

                        EMClient.getInstance().chatManager().sendMessage(cmdMsg);
                        ToastUtil.toast(msg);

                        Global.addUserOriginType = "";
                        Global.addUserOriginName = "";
                        Global.addUserOriginId = "";

                        finish();
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });


    }


    /**
     * 解除拉黑好友
     */
    public void blackContact(String blackStatus) {
       /* Map<String, Object> map = new HashMap<>(2);
        map.put("friendUserId", userId);
        //拉黑状态 0-未拉黑 1-已拉黑
        map.put("blackStatus", "0");
        ApiClient.requestNetHandle(this, AppConfig.BLACK_USER_FRIEND,
                "正在移除黑名单...", map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        EventBus.getDefault().post(new EventCenter<>(EventUtil.REFRESH_BLACK));
                        ToastUtil.toast(" 移除成功");
                        finish();
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });*/
        Map<String, Object> map = new HashMap<>(2);
        map.put("friendUserId", userId.split("-")[0]);
        //拉黑状态 0-未拉黑 1-已拉黑
        map.put("blackStatus", blackStatus);

        ApiClient.requestNetHandle(this, AppConfig.BLACK_USER_FRIEND,
                mSwitchBlack.isChecked() ? "正在拉黑..." : "正在取消拉黑...", map,
                new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        if (from.equals("3")) {
                            EventBus.getDefault().post(new EventCenter<>(EventUtil.REFRESH_BLACK));
                            ToastUtil.toast(" 移除成功");
                            finish();
                        } else {
                            if (mSwitchBlack.isChecked()) {
                                ToastUtil.toast("拉黑成功");
                                EMClient.getInstance().chatManager().deleteConversation(userId.contains(Constant.ID_REDPROJECT) ? userName : userId + Constant.ID_REDPROJECT, false);
                                EventBus.getDefault().post(new EventCenter<>(EventUtil.OPERATE_BLACK));
                                finish();
                            }
                        }
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
        Map<String, Object> map = new HashMap<>();
        map.put("friendUserId", userId);
        ApiClient.requestNetHandle(this, AppConfig.DEL_USER_FRIEND, "正在删除...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {

                try {
                    EMClient.getInstance().contactManager().deleteContact(userId + Constant.ID_REDPROJECT);
                    ToastUtil.toast(" 删除成功");
                    finish();
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }
}
