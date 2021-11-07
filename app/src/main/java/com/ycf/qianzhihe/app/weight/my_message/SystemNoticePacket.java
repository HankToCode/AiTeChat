package com.ycf.qianzhihe.app.weight.my_message;

import android.content.Context;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.operate.UserOperateManager;
import com.ycf.qianzhihe.app.utils.ProjectUtil;
import com.ycf.qianzhihe.app.weight.ease.EaseCommonUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.util.StringUtil;
import com.ycf.qianzhihe.app.weight.ease.chatrow.EaseChatRow;
import com.hyphenate.chat.EMMessage;

public class SystemNoticePacket extends EaseChatRow {
    private TextView tv_message, tv_message_new;
    private LinearLayout ll_container, llay_msg;

    public SystemNoticePacket(Context context, EMMessage message,
                              int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.SYSTEM_NOTICE)
                || message.getStringAttribute(Constant.MSGTYPE, "").equals("addgroupuser")
                || message.getStringAttribute(Constant.MSGTYPE, "").equals("delgroupuser")) {
            inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                    R.layout.system_notice_message :
                    R.layout.system_notice_message, this);
        }
    }

    @Override
    protected void onFindViewById() {
        tv_message = findViewById(R.id.tv_msg);
        tv_message_new = findViewById(R.id.tv_msg_new);
        ll_container = findViewById(R.id.ll_container);
        llay_msg = findViewById(R.id.llay_msg);
    }

    /**
     * refresh view when message status change
     *
     * @param msg
     */
    @Override
    protected void onViewUpdate(EMMessage msg) {

    }


    @Override
    protected void onSetUpView() {
        String systemNotice = EaseCommonUtils.getMessageDigest(message,
                getContext());
        if (message.getStringAttribute(Constant.MSGTYPE, "").equals("addgroupuser")
                || message.getStringAttribute(Constant.MSGTYPE, "").equals("delgroupuser")) {

            String inviterId = message.getStringAttribute(Constant.INVITER_ID, "");
            String userId = message.getStringAttribute(Constant.USER_ID, "");
            String delUserId = message.getStringAttribute(Constant.DEL_USER_ID, "");

            if ("addgroupuser".equals(Constant.MSGTYPE)) {
                systemNotice = getContext().getString(R.string.msg_group_invite_user,
                        UserOperateManager.getInstance().getUserName(userId),
                        UserOperateManager.getInstance().getUserName(inviterId)
                );
            } else if ("delgroupuser".equals(Constant.MSGTYPE)) {
                systemNotice = getContext().getString(R.string.msg_group_remove_user,
                        UserOperateManager.getInstance().getUserName(userId),
                        UserOperateManager.getInstance().getUserName(delUserId)
                );
            }

            tv_message_new.setVisibility(VISIBLE);
            llay_msg.setVisibility(GONE);
            tv_message_new.setText(StringUtil.isEmpty(systemNotice) ? "" :
                    systemNotice);
            return;
        }
        tv_message_new.setVisibility(GONE);
        llay_msg.setVisibility(VISIBLE);
        tv_message.setText(StringUtil.isEmpty(systemNotice) ? "" :
                systemNotice);

        setClickListener();
    }

    private void setClickListener() {
        if (llay_msg != null) {

            llay_msg.setOnLongClickListener(v -> {
                if (itemClickListener != null) {
                    itemClickListener.onBubbleLongClick(message);
                }
                return true;
            });
        }

    }

}
