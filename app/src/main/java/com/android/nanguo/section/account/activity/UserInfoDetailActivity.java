package com.android.nanguo.section.account.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.coorchice.library.SuperTextView;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;
import com.android.nanguo.R;
import com.android.nanguo.app.api.Constant;
import com.android.nanguo.app.api.Global;
import com.android.nanguo.app.api.global.EventUtil;
import com.android.nanguo.app.api.global.UserComm;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.api.old_data.FriendInfo;
import com.android.nanguo.app.api.old_data.LoginInfo;
import com.android.nanguo.app.api.old_data.ToTopMap;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.ActivityStackManager;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.app.operate.UserOperateManager;
import com.android.nanguo.app.utils.ProjectUtil;
import com.android.nanguo.app.utils.hxSetMessageFree.EaseSharedUtils;
import com.android.nanguo.app.utils.my.MyHelper;
import com.android.nanguo.app.weight.MyDialog;
import com.android.nanguo.common.widget.UserInfoMoreWindow;
import com.android.nanguo.section.chat.activity.ChatActivity;
import com.android.nanguo.section.common.MyQrActivity;
import com.android.nanguo.section.conversation.ChatBgActivity;
import com.android.nanguo.section.conversation.ChatRecordActivity;
import com.android.nanguo.section.conversation.ModifyFriendRemarkActivity;
import com.android.nanguo.section.conversation.ReportActivity;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.log.XLog;
import com.zds.base.util.StringUtil;
import com.zds.base.util.Utils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.OnClick;

/**
 * @author lhb
 * 用户详情
 */
public class UserInfoDetailActivity extends BaseInitActivity {
    private String userId, userName;
    private String inviterUserId;
    private int chatType;
    private String emGroupId;
    private String groupId;
    private FriendInfo info;
    private int currentUserRank;
    private String isFromBlack = "";


    private EaseTitleBar mTitleBar;
    private ImageView mImgHead;
    private TextView mTvRemark;
    private TextView mTvNickName;
    private ImageView mIvVipIcon;
    private TextView mTvAccount;
    private TextView mTvDescription;
    private SuperTextView mTvToTop;
    private LinearLayout mLlBottomFriend;
    private SuperTextView mTvDelFriend;
    private SuperTextView mTvSendMsg;
    private SuperTextView mTvAddFriend;
    private SuperTextView mTvFree;
    private TextView mTvInviter;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_userinfo_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        mImgHead = (ImageView) findViewById(R.id.img_head);
        mTvRemark = (TextView) findViewById(R.id.tv_remark);
        mTvNickName = (TextView) findViewById(R.id.tv_nick_name);
        mIvVipIcon = (ImageView) findViewById(R.id.iv_vip_icon);
        mTvAccount = (TextView) findViewById(R.id.tv_account);
        mTvDescription = (TextView) findViewById(R.id.tv_description);
        mTvToTop = (SuperTextView) findViewById(R.id.tv_to_top);
        mLlBottomFriend = (LinearLayout) findViewById(R.id.ll_bottom_friend);
        mTvDelFriend = (SuperTextView) findViewById(R.id.tv_del_friend);
        mTvSendMsg = (SuperTextView) findViewById(R.id.tv_send_msg);
        mTvAddFriend = (SuperTextView) findViewById(R.id.tv_add_friend);
        mTvFree = (SuperTextView) findViewById(R.id.tv_free);
        mTvInviter = (TextView) findViewById(R.id.tv_inviter);

        initLogic();

    }

    //是否禁言
    private boolean isMute = false;
    //是否拉黑
    private boolean isBlack = false;
    //是否朋友
    private boolean isFriend = false;
    //是否管理员
    private boolean isUserRank = false;

    public interface UserInfoDetailInfoInterface {
        boolean isMute();

        boolean isBlack();

        boolean isFriend();

        boolean isUserRank();

        void initBottomVis();
    }

    protected void initLogic() {

        initImmersionBar(false);
        mTitleBar.setOnBackPressListener(view -> finish());
        mTitleBar.setRightLayoutClickListener(view -> {
            //弹出
            UserInfoMoreWindow userInfoMoreWindow = new UserInfoMoreWindow(this, chatType, integer -> {
                switch (integer) {
                    case 1:
                    case 7:
                        //禁言
                        modifyGroupUserSayStatus(!isMute);
                        break;
                    case 2:
                    case 8:
                        //移出群组
                        delGroupUser();
                        break;
                    case 3:
                    case 12:
                        //查找聊天记录
                        startActivity(new Intent(UserInfoDetailActivity.this, ChatRecordActivity.class).putExtra("chatId", userId + Constant.ID_REDPROJECT));
                        break;
                    case 4:
                    case 10:
                        //分组
                        FriendGroupingActvity.actionStart(mContext, userId, info.getCategoryId(), info.getCategoryName());
                        break;
                    case 5:
                    case 11:
                        //拉黑
                        if (info.getFriendFlag().equals("1")) {
                            if (info.getBlackStatus() != null && info.getBlackStatus().equals("1")) {
                                isBlack = false;
                                blackContact("0");
                            } else {
                                isBlack = true;
                                blackContact("1");
                            }
                        } else {
                            isFriend = false;
                        }
                        break;
                    case 6:
                    case 9:
                    case 13:
                        if (chatType == Constant.CHATTYPE_GROUP) {
                            startActivity(new Intent(this, ReportActivity.class).putExtra("from", "2").putExtra("userGroupId", groupId));
                        } else {
                            startActivity(new Intent(this, ReportActivity.class).putExtra("from", "1").putExtra("userGroupId", userId));
                        }
                        break;
                }

                return null;
            }, new UserInfoDetailInfoInterface() {

                @Override
                public boolean isMute() {
                    return isMute;
                }

                @Override
                public boolean isBlack() {
                    return isBlack;
                }

                @Override
                public boolean isFriend() {
                    return isFriend;
                }

                @Override
                public boolean isUserRank() {
                    return isUserRank;
                }

                @Override
                public void initBottomVis() {
                    UserInfoDetailActivity.this.initBottomVis();
                }
            });
            mLlBottomFriend.setVisibility(View.GONE);
            mTvAddFriend.setVisibility(View.GONE);
            userInfoMoreWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0);
        });
        /*mImgRight.setOnClickListener(v ->
                startActivity(new Intent(UserInfoDetailActivity.this, ChatMoreSetlActivity.class)
                        .putExtra(Constant.PARAM_EM_CHAT_ID, userId + Constant.ID_REDPROJECT)
//                        .putExtra(Constant.NICKNAME, info.getFriendNickName()==null?info.getUserNickName():info.getFriendNickName())
                        .putExtra(Constant.NICKNAME, userName)
                        .putExtra("isFriend", info.getFriendFlag().equals("1"))
                        .putExtra("from", "1")));*/

        if (chatType == Constant.CHATTYPE_GROUP) {
            if (currentUserRank == 2 || currentUserRank == 1) {//2群主  1管理员
                isUserRank = true;
            }
        } else {
            isUserRank = false;
        }

        if (userId.contains(UserComm.getUserId())) {
            //自己
            LoginInfo loginInfo =
                    UserComm.getUserInfo();
            GlideUtils.GlideLoadCircleErrorImageUtils(UserInfoDetailActivity.this, loginInfo.getUserHead(), mImgHead,
                    R.mipmap.ic_ng_avatar);
            mTvNickName.setText(loginInfo.getNickName());
            mTvAccount.setText(loginInfo.getUserCode());
            mLlBottomFriend.setVisibility(View.GONE);
            mTvRemark.setVisibility(View.GONE);
            mTvFree.setVisibility(View.GONE);
            mTvAddFriend.setVisibility(View.GONE);
        } else {
            if (!TextUtils.isEmpty(emGroupId)) {
                getGroupMuteList();
            }

            mTvRemark.setVisibility(View.VISIBLE);
        }
        queryFriendInfo();

        String isMsgFree =
                MyHelper.getInstance().getModel().getConversionMsgIsFree(userId.contains(Constant.ID_REDPROJECT) ? userId :
                        userId + Constant.ID_REDPROJECT);
        if (null != isMsgFree && isMsgFree.equals("false")) {
            //设置消息免打扰
            mTvFree.setSelected(true);
            mTvFree.setSolid(ContextCompat.getColor(this, R.color.color_66_white));
            mTvFree.setText("已开启免打扰");
        } else {
            //取消消息免打扰
            mTvFree.setSelected(false);
            mTvFree.setSolid(ContextCompat.getColor(this, R.color.color_22_white));
            mTvFree.setText("消息免打扰");
        }
        //消息免打扰
        mTvFree.setOnClickListener(view -> {
            if (!userId.contains(Constant.ID_REDPROJECT)) {
                userId += Constant.ID_REDPROJECT;
            }
            view.setSelected(!view.isSelected());
            if (view.isSelected()) {
                EaseSharedUtils.setEnableMsgRing(Utils.getContext(),
                        UserComm.getUserId() + Constant.ID_REDPROJECT,
                        userId, false);
                MyHelper.getInstance().getModel().saveChatBg(userId,
                        null, "false", null);
                mTvFree.setSolid(ContextCompat.getColor(this, R.color.color_66_white));
                mTvFree.setText("已开启免打扰");
            } else {
                EaseSharedUtils.setEnableMsgRing(Utils.getContext(),
                        UserComm.getUserId() + Constant.ID_REDPROJECT,
                        userId, true);
                MyHelper.getInstance().getModel().saveChatBg(userId,
                        null, "true", null);
                mTvFree.setSolid(ContextCompat.getColor(this, R.color.color_22_white));
                mTvFree.setText("消息免打扰");
            }
        });

        EMConversation emConversation = EMClient.getInstance().chatManager().getConversation(userId + Constant.ID_REDPROJECT);
        if (emConversation != null) {
            if (emConversation.getExtField().equals("toTop")) {
                mTvToTop.setSelected(true);
                mTvToTop.setSolid(ContextCompat.getColor(this, R.color.color_66_white));
                mTvToTop.setText("聊天已置顶");
            } else {
                mTvToTop.setSelected(false);
                mTvToTop.setSolid(ContextCompat.getColor(this, R.color.color_22_white));
                mTvToTop.setText("聊天置顶");
            }
        }

        mTvToTop.setOnClickListener(v -> {
            if (emConversation != null && !emConversation.conversationId().equals(Constant.ADMIN)) {
                if (emConversation.conversationId().contains(userId)) {
                    mTvToTop.setSelected(!mTvToTop.isSelected());
                    boolean isSelect = mTvToTop.isSelected();
                    if (isSelect) {
                        emConversation.setExtField("toTop");
                        ToTopMap.save(UserInfoDetailActivity.this, emConversation.conversationId());
                        mTvToTop.setSolid(ContextCompat.getColor(this, R.color.color_66_white));
                        mTvToTop.setText("聊天已置顶");
                    } else {
                        emConversation.setExtField("false");
                        ToTopMap.delete(UserInfoDetailActivity.this, emConversation.conversationId());
                        mTvToTop.setSolid(ContextCompat.getColor(this, R.color.color_22_white));
                        mTvToTop.setText("聊天置顶");
                    }
                }
            }
        });

        //switch_start  0:未加星标，1：星标
        /*switch_start.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                switchStart("1");
            } else {
                switchStart("0");
            }
        });*/
    }

    /*private void addRemark() {
        Map<String, Object> map = new HashMap<>();
        if (userId.contains(Constant.ID_REDPROJECT)) {
            userId = userId.split("-")[0];
        }
        map.put("friendUserId", userId);
        map.put("friendNickName", mTvRemark.getText().toString().trim());
        ApiClient.requestNetHandle(this, AppConfig.ADD_GOODS_FRIEND_REMARK,
                "请稍后...", map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        String name = mTvRemark.getText().toString().trim();
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
    }*/

    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.OPERATE_BLACK || center.getEventCode() == EventUtil.DELETE_CONTACT) {
            finish();
        } else if (center.getEventCode() == EventUtil.REFRESH_REMARK) {
            mTvRemark.setText(UserOperateManager.getInstance().getUserName(userId));
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
        inviterUserId = intent.getStringExtra("entryUserId");
        currentUserRank = intent.getIntExtra("currentUserRank", 0);
        isFromBlack = intent.getStringExtra("from");
    }

    private void getGroupMuteList() {

        EMClient.getInstance().groupManager().asyncFetchGroupMuteList(emGroupId, 0, 10000, new EMValueCallBack<Map<String, Long>>() {
            @Override
            public void onSuccess(Map<String, Long> stringLongMap) {
                for (String key : stringLongMap.keySet()) {
                    if (key.contains(userId))
                        runOnUiThread(() -> isMute = true);
                }

            }

            @Override
            public void onError(int i, String s) {
            }
        });

    }

    private void modifyGroupUserSayStatus(boolean isMute) {
        this.isMute = isMute;
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

    }

    private void queryFriendInfo() {
        Map<String, Object> map = new HashMap<>(1);
        if (userId.contains(Constant.ID_REDPROJECT)) {
            userId = userId.split("-")[0];
        }
        map.put("friendUserId", userId);
        map.put("flag", "cache");
        if (!TextUtils.isEmpty(groupId)) {
            map.put("groupId", groupId);
        }
        ApiClient.requestNetHandle(this, AppConfig.FRIEND_INFO, "加载中...", map,
                new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        if (null != json && json.length() > 0) {
                            info = FastJsonUtil.getObject(json,
                                    FriendInfo.class);
                            GlideUtils.GlideLoadCircleErrorImageUtils(UserInfoDetailActivity.this, info.getUserHead(), mImgHead,
                                    R.mipmap.ic_ng_avatar);
                            userName = info.getNickName();
                            mTvAccount.setText(getString(R.string.str_chat_account, info.getUserCode()));
                            /*mTvLineStatus.setText(info.getLine().equals(
                                    "online") ?
                                    "在线" : "离线");*/
                            if (TextUtils.isEmpty(info.getSign())) {
                                mTvDescription.setText("这家伙很懒，啥都没写");
                            } else {
                                mTvDescription.setText(info.getSign());
                            }

                            //是否在线
                            /*if (!TextUtils.isEmpty(info.getLine())) {
                                if (info.getLine().equals("online")) {
                                    iv_online_status.setBackgroundResource(R.drawable.dot_green);
                                } else {
                                    iv_online_status.setBackgroundResource(R.drawable.dot_gray);
                                }
                            } else {
                                iv_online_status.setBackgroundResource(R.drawable.dot_gray);
                            }*/

                            mTvNickName.setText(info.getNickName());

                            initBottomVis();
                            //改动一下，不用inviterUserId查询邀请者了，查详情时如有传groupId则返回entryUserId 用来查邀请者
                           /* if (!TextUtils.isEmpty(inviterUserId)) {
                                queryInviter();
                            }*/

                            if (!TextUtils.isEmpty(info.getEntryUserId())) {
                                queryInviter(info.getEntryUserId());
                            }
                        } else {
                            ToastUtil.toast("数据异常");
                            finish();
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                        finish();
                    }
                });
    }

    private void initBottomVis() {
        if (info == null) return;
        //好友标识 0 不是好友 1 是好友
        if (info.getFriendFlag().equals("1")) {
                                /*//是不是星标 starTarget	0:未加星标，1：星标
                                if (!TextUtils.isEmpty(info.getStarTarget())) {
                                    if (Double.parseDouble(info.getStarTarget()) == 1) {
                                        iv_start.setVisibility(View.VISIBLE);
                                        switch_start.setChecked(true);
                                    } else {
                                        iv_start.setVisibility(View.GONE);
                                        switch_start.setChecked(false);
                                    }
                                } else {
                                    iv_start.setVisibility(View.GONE);
                                    switch_start.setChecked(false);
                                }*/
            isFriend = true;
            if (info.getBlackStatus().equals("1")) {
                isBlack = true;
                //黑名单中
                mLlBottomFriend.setVisibility(View.GONE);
            } else {
                isBlack = false;
                mLlBottomFriend.setVisibility(View.VISIBLE);
            }
            try {
                mTvRemark.setText(info.getFriendNickName());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            isFriend = false;
            if (userId.contains(UserComm.getUserId())) {//自己
                mLlBottomFriend.setVisibility(View.GONE);
                mTvAddFriend.setVisibility(View.GONE);
            } else {
                mLlBottomFriend.setVisibility(View.GONE);
                mTvAddFriend.setVisibility(View.VISIBLE);
            }
        }
    }

    private void queryInviter(String entryUserId) {
        Map<String, Object> map = new HashMap<>(1);
       /*此处废处 inviterUserId
       Log.d("####查询邀请者=", inviterUserId);
        map.put("friendUserId", inviterUserId);*/
        Log.d("####查询邀请者=", entryUserId);
        map.put("friendUserId", entryUserId);
        ApiClient.requestNetHandle(this, AppConfig.FRIEND_INFO, "加载中", map,
                new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        FriendInfo inviterInfo = FastJsonUtil.getObject(json,
                                FriendInfo.class);
                        Log.d("####查询邀请者", inviterInfo.getNickName());
                        if (!StringUtil.isEmpty(inviterInfo.getNickName())) {
                            mTvInviter.setVisibility(View.VISIBLE);
                            mTvInviter.setText("邀请者:" + inviterInfo.getNickName());
                        } else {
                            mTvInviter.setVisibility(View.GONE);
                        }

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


    @OnClick({R.id.tv_send_msg, R.id.tv_add_friend, R.id.tv_remark, R.id.tv_del_friend, R.id.iv_qr})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.iv_qr:
                MyQrActivity.actionStart(mContext);
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
            case R.id.tv_chat_bg:
                //聊天背景
                ChatBgActivity.actionStart(this, "1", info.getUserId() + Constant.ID_REDPROJECT);
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
            case R.id.tv_send_msg:
                //好友标识 0 不是好友 1 是好友
                if (info.getFriendFlag().equals("0")) {
                    ToastUtil.toast("还不是好友哦，无法发送消息");
                    return;
                }
                //发消息
                if (!userId.contains(Constant.ID_REDPROJECT)) {//friendUserId
                    userId += Constant.ID_REDPROJECT;
                }
                //这里有BUG，群名片跳过来的 发消息有偶发机率还是进入群会话，
                ActivityStackManager.getInstance().killActivity(ChatActivity.class);
                ChatActivity.actionStart(mContext, userId, EaseConstant.CHATTYPE_SINGLE);
                finish();
               /* if (chatType == Constant.CHATTYPE_SINGLE) {//如果是单聊则直接返回复用上个聊天页面
                    ChatActivity.actionStart(mContext, userId, EaseConstant.CHATTYPE_SINGLE);
                    finish();
                } else {//如果是群聊中的名片跳过来的或群个人详情过来的 需要先关闭群聊会话窗口 再进入单聊会话
                    ActivityStackManager.getInstance().killActivity(ChatActivity.class);
                    ChatActivity.actionStart(mContext, userId, EaseConstant.CHATTYPE_SINGLE);
                    finish();
                }*/
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
                addUser();
                break;
            case R.id.tv_remark:
                if (info == null) {
                    return;
                }
                ModifyFriendRemarkActivity.actionStart(this, userId, mTvRemark.getText().toString());
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
        Map<String, Object> map = new HashMap<>(2);
        map.put("friendUserId", userId.split("-")[0]);
        //拉黑状态 0-未拉黑 1-已拉黑
        map.put("blackStatus", blackStatus);

        ApiClient.requestNetHandle(this, AppConfig.BLACK_USER_FRIEND,
                isBlack ? "正在拉黑..." : "正在取消拉黑...", map,
                new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        if (isBlack) {
                            ToastUtil.toast("拉黑成功");
                            EMClient.getInstance().chatManager().deleteConversation(userId.contains(Constant.ID_REDPROJECT) ? userName : userId + Constant.ID_REDPROJECT, false);
                            EventBus.getDefault().post(new EventCenter<>(EventUtil.OPERATE_BLACK));
                            finish();
                        } else {
                            EventBus.getDefault().post(new EventCenter<>(EventUtil.REFRESH_BLACK));
                            ToastUtil.toast("移除成功");
                            finish();
                        }
                        if (isFromBlack.equals("3")) {
                            EventBus.getDefault().post(new EventCenter<>(EventUtil.REFRESH_BLACK));
                            ToastUtil.toast(" 移除成功");
                            finish();
                        } else {
                            if (isBlack) {
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


    public void switchStart(String status) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("friendUserId", userId.split("-")[0]);
        //0:未加星标，1：星标
        map.put("starTarget", status);
        ApiClient.requestNetHandle(this, AppConfig.modifyFriendStarTarget, "正在设置...", map,
                new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        ToastUtil.toast("设置成功");
                        EventBus.getDefault().post(new EventCenter<>(EventUtil.REFRESH_CONTACT));
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

                    //删除和某个user会话，如果需要保留聊天记录，传false
                    EMClient.getInstance().chatManager().deleteConversation(userId + Constant.ID_REDPROJECT, true);
                    EventBus.getDefault().post(new EventCenter<>(EventUtil.DELETE_CONTACT));


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
