package com.android.nanguo.app.weight.my_message;

import android.content.Context;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.android.nanguo.R;
import com.android.nanguo.app.api.Constant;
import com.android.nanguo.app.weight.ease.chatrow.EaseChatRow;

public class ChatRedFinalPacket extends EaseChatRow {
    private TextView tv_message;
    private LinearLayout ll_container;

    public ChatRedFinalPacket(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RABEND)) {
            inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                    R.layout.row_red_packet_ack_message : R.layout.row_red_packet_ack_message, this);
        }
    }

    @Override
    protected void onFindViewById() {
        tv_message = findViewById(R.id.tv_money_msg);
        ll_container = findViewById(R.id.ll_container);
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
        ll_container.setVisibility(VISIBLE);
        if (message.getChatType().equals(EMMessage.ChatType.GroupChat)) {
            final SpannableStringBuilder sp = new SpannableStringBuilder("红包被抢完");
            sp.setSpan(new ForegroundColorSpan(0xffFA9E3B), 0, 2, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE); //字体颜色
            tv_message.setText(sp);
        }
    }
}