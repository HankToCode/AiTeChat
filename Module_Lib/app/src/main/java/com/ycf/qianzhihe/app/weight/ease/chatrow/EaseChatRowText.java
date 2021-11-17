package com.ycf.qianzhihe.app.weight.ease.chatrow;

import android.content.Context;
import android.text.Spannable;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import androidx.core.content.ContextCompat;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.old_http.CommonApi;
import com.ycf.qianzhihe.app.utils.ease.EaseSmileUtils;

public class EaseChatRowText extends EaseChatRow {

    private TextView contentView;
    private LinearLayout ll_message;
    private int[] vipTextColor = {R.color.black, R.color.user_type_1, R.color.user_type_2, R.color.user_type_3, R.color.user_type_4};

    public EaseChatRowText(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }

    @Override
    protected void onInflateView() {
        inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                R.layout.ease_row_received_message : R.layout.ease_row_sent_message, this);
    }

    @Override
    protected void onFindViewById() {
        contentView = (TextView) findViewById(R.id.tv_chatcontent);
        ll_message = findViewById(R.id.ll_message);
    }

    @Override
    public void onSetUpView() {
        EMTextMessageBody txtBody = (EMTextMessageBody) message.getBody();
        Spannable span = EaseSmileUtils.getSmiledText(context, txtBody.getMessage());
        // 设置内容
        contentView.setText(span, BufferType.SPANNABLE);
        int vipLevel = message.getIntAttribute(Constant.USERVIPLEVEL, 0);

        if (vipLevel != 0 && vipLevel <= vipTextColor.length) {
            contentView.setTextColor(ContextCompat.getColor(context, vipTextColor[vipLevel]));
        } else {
            int textColor = message.direct() == EMMessage.Direct.SEND ? ContextCompat.getColor(context, R.color.color_425563) : ContextCompat.getColor(context, R.color.color_425563);
            contentView.setTextColor(textColor);
        }

        if ("系统管理员".equals(message.getFrom())) {
            ll_message.setVisibility(GONE);
        } else {
            ll_message.setVisibility(VISIBLE);
        }

        contentView.setOnLongClickListener(view -> {
            if (itemClickListener != null) {
                itemClickListener.onBubbleLongClick(message);
            }
            return true;
        });
    }

    @Override
    protected void onViewUpdate(EMMessage msg) {
        switch (msg.status()) {
            case CREATE:
                onMessageCreate();
                break;
            case SUCCESS:
                onMessageSuccess();
                break;
            case FAIL:
                onMessageError();
                break;
            case INPROGRESS:
                onMessageInProgress();
                break;
        }
    }

    private void onMessageCreate() {
        progressBar.setVisibility(VISIBLE);
        statusView.setVisibility(GONE);
    }

    private void onMessageSuccess() {
        progressBar.setVisibility(GONE);
        statusView.setVisibility(GONE);
    }

    private void onMessageError() {
        progressBar.setVisibility(GONE);
        statusView.setVisibility(VISIBLE);
    }

    private void onMessageInProgress() {
        progressBar.setVisibility(VISIBLE);
        statusView.setVisibility(GONE);
    }
}
