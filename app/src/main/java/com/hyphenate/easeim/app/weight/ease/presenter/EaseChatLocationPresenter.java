package com.hyphenate.easeim.app.weight.ease.presenter;

import android.content.Context;
import android.content.Intent;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeim.app.api.Constant;
import com.hyphenate.easeim.app.weight.ease.chatrow.EaseChatRow;
import com.hyphenate.easeim.app.weight.ease.chatrow.EaseChatRowLocationPacket;
import com.hyphenate.easeim.section.common.HxMapActivity;
import com.hyphenate.exceptions.HyphenateException;

/**
 * Created by zhangsong on 17-10-12.
 */

public class EaseChatLocationPresenter extends EaseChatRowPresenter {
    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter) {
        return new EaseChatRowLocationPacket(cxt, message, position, adapter);
    }

    @Override
    protected void handleReceiveMessage(EMMessage message) {
        if (!message.isAcked() && message.getChatType() == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (HyphenateException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        if (message.getBooleanAttribute(Constant.SEND_LOCATION, false)) {
            String latitude = message.getStringAttribute("latitude", "");
            String longitude = message.getStringAttribute("longitude", "");
            String localDetail = message.getStringAttribute("localDetail", "");

            Intent intent = new Intent(getContext(), HxMapActivity.class);
            intent.putExtra("latitude", Double.valueOf(latitude));
            intent.putExtra("longitude", Double.valueOf(longitude));
            intent.putExtra("addressDetail", localDetail);

            getContext().startActivity(intent);
        }


    }
}