package com.ycf.qianzhihe.section.account.activity;

import android.content.Context;
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
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;
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
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.operate.UserOperateManager;
import com.ycf.qianzhihe.app.utils.ProjectUtil;
import com.ycf.qianzhihe.app.utils.hxSetMessageFree.EaseSharedUtils;
import com.ycf.qianzhihe.app.utils.my.MyHelper;
import com.ycf.qianzhihe.app.weight.MyDialog;
import com.ycf.qianzhihe.common.utils.ToastUtils;
import com.ycf.qianzhihe.section.chat.activity.ChatActivity;
import com.ycf.qianzhihe.section.contact.activity.AddUserActivity;
import com.ycf.qianzhihe.section.conversation.ChatBgActivity;
import com.ycf.qianzhihe.section.conversation.ChatRecordActivity;
import com.ycf.qianzhihe.section.conversation.ModifyFriendRemarkActivity;
import com.ycf.qianzhihe.section.conversation.ReportActivity;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.log.XLog;
import com.zds.base.util.StringUtil;
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
public class AddUserDetailActivity extends BaseInitActivity {
    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.img_head)
    EaseImageView img_head;
    @BindView(R.id.tv_nick_name)
    TextView tv_nick_name;
    @BindView(R.id.tv_account)
    TextView tv_account;
    @BindView(R.id.iv_online_status)
    ImageView iv_online_status;
    @BindView(R.id.et_remark)
    TextView et_remark;
    @BindView(R.id.tv_sign)
    TextView tv_sign;
    @BindView(R.id.tv_add_friend)
    TextView tv_add_friend;
    LoginInfo infoBean;

    public static void actionStart(Context context, LoginInfo infoBean) {
        Intent intent = new Intent(context, AddUserDetailActivity.class);
        intent.putExtra("infoBean", infoBean);
        context.startActivity(intent);
    }


    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        infoBean = (LoginInfo) intent.getSerializableExtra("infoBean");

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_user_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar.setTitle("用户详情");
        mTitleBar.setOnBackPressListener(view -> finish());
        tv_nick_name.setText(infoBean.getNickName());
        tv_account.setText(infoBean.getNickName());
        GlideUtils.GlideLoadCircleErrorImageUtils(AddUserDetailActivity.this, infoBean.getUserHead(), img_head,
                R.mipmap.img_default_avatar);

        //是否在线
        if (!TextUtils.isEmpty(infoBean.getLine())) {
            if (infoBean.getLine().equals("online")) {
                iv_online_status.setBackgroundResource(R.drawable.dot_green);
            } else {
                iv_online_status.setBackgroundResource(R.drawable.dot_gray);
            }
        } else {
            iv_online_status.setBackgroundResource(R.drawable.dot_gray);
        }
        tv_sign.setText(infoBean.getSign());

        tv_add_friend.setOnClickListener(view -> addUser());

    }


    /**
     * 添加好友
     */
    private void addUser() {
        if (StringUtil.isEmpty(et_remark.getText().toString())) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("toUserId", infoBean.getUserId());
        map.put("originType", Constant.ADD_USER_ORIGIN_TYPE_SEARCH);
        ApiClient.requestNetHandle(this, AppConfig.APPLY_ADD_USER, "正在添加", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);

//支持单聊和群聊，默认单聊，如果是群聊添加下面这行
//                cmdMsg.setChatType(ChatType.GroupChat)
                //action可以自定义
                String action = Constant.ACTION_APPLY_ADD_FRIEND;
                EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
                cmdMsg.addBody(cmdBody);
//                //发送给某个人
                String toUsername = infoBean.getUserId() + Constant.ID_REDPROJECT;
                cmdMsg.setTo(toUsername);
                cmdMsg.setFrom(UserComm.getUserId());
                cmdMsg.setAttribute(Constant.APPLY_ADD_FRIEND_ID, UserComm.getUserInfo().getUserId());

                EMClient.getInstance().chatManager().sendMessage(cmdMsg);
                ToastUtils.showToast("申请成功");
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });


    }

}
