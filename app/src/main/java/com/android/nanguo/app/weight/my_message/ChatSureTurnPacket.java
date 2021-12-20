package com.android.nanguo.app.weight.my_message;

import android.content.Context;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.android.nanguo.R;
import com.android.nanguo.app.api.Constant;
import com.android.nanguo.app.weight.ease.chatrow.EaseChatRow;
import com.zds.base.util.StringUtil;

public class ChatSureTurnPacket extends EaseChatRow {
    private TextView tv_message,tv_messageRemark;
    private TextView tv_time;

    public ChatSureTurnPacket(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.TURN)) {
            inflater.inflate(R.layout.sent_transfer, this);
        } else {
            inflater.inflate(R.layout.received_transfer, this);
        }

    }

    @Override
    protected void onFindViewById() {
        tv_message = findViewById(R.id.tv_transfer_received);
        tv_messageRemark = findViewById(R.id.tv_money_greeting);
        tv_time = findViewById(R.id.tv_time);

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
        tv_messageRemark.setText("已收款");
        tv_message.setText(message.getStringAttribute("money", "") + getResources().getString(R.string.glod));
        tv_time.setText(StringUtil.formatDateMinute(message.getMsgTime()));
    }


}
