package com.ycf.qianzhihe.app.weight.my_message;

import android.content.Context;
import android.text.TextUtils;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.operate.UserOperateManager;
import com.ycf.qianzhihe.app.weight.ease.chatrow.EaseChatRow;
import com.zds.base.util.StringUtil;

/**
 * 作   者：赵大帅
 * 描   述: 转账
 * 日   期: 2017/11/27 17:26
 * 更新日期: 2017/11/27
 */
public class ChatRedturnPacket extends EaseChatRow {
    private TextView tv_message, tv_messageRemark, tv_time;
    private RelativeLayout bubble_mask;

    public ChatRedturnPacket(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        if (message.getBooleanAttribute(Constant.TURN, false)) {
            inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                    R.layout.received_transfer : R.layout.sent_transfer, this);
        }
    }

    @Override
    protected void onFindViewById() {
        tv_message = findViewById(R.id.tv_transfer_received);
        tv_messageRemark = findViewById(R.id.tv_money_greeting);
        tv_time = findViewById(R.id.tv_time);
        bubble_mask = findViewById(R.id.bubble_mask);

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
        String remark = message.getStringAttribute("remark", "");
        //有BUG 第二条无备注的会自动显示上一条转账备注信息
        /*if (remark != null && remark.length() > 0) {
            tv_messageRemark.setText(remark);
        }*/

        /*String localRobNikeName = "";
        if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.TURN)) {
            localRobNikeName = UserOperateManager.getInstance().getUserName(message.getFrom());
        } else {
            localRobNikeName = UserOperateManager.getInstance().getUserName(message.getTo());
        }
        if (!TextUtils.isEmpty(remark)) {
            tv_messageRemark.setText(remark);
        } else {
            tv_messageRemark.setText(localRobNikeName+"的转账");
        }*/

        tv_time.setText(StringUtil.formatDateMinute(message.getMsgTime()));
        if (message.direct() == EMMessage.Direct.RECEIVE) {
            tv_messageRemark.setText("收到转账");
            bubble_mask.setVisibility(VISIBLE);
        } else {
            String localRobNikeName = UserOperateManager.getInstance().getUserName(message.getTo());
            tv_messageRemark.setText("转账给" + localRobNikeName);
            bubble_mask.setVisibility(GONE);
        }
        tv_message.setText(message.getStringAttribute("money", "") + getResources().getString(R.string.glod));
    }


}
