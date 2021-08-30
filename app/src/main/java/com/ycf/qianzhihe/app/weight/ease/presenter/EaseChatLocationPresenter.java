package com.ycf.qianzhihe.app.weight.ease.presenter;

import android.content.Context;
import android.content.Intent;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.weight.ease.chatrow.EaseChatRow;
import com.ycf.qianzhihe.app.weight.ease.chatrow.EaseChatRowLocationPacket;
import com.ycf.qianzhihe.section.common.HxMapActivity;
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
            String latitude = message.getStringAttribute(Constant.LATITUDE, "");
            String longitude = message.getStringAttribute(Constant.LONGITUDE, "");
            String localDetail = message.getStringAttribute(Constant.LOCAL_DETAIL, "");

            Intent intent = new Intent(getContext(), HxMapActivity.class);
            intent.putExtra(Constant.LATITUDE, Double.valueOf(latitude));
            intent.putExtra(Constant.LONGITUDE, Double.valueOf(longitude));
            intent.putExtra(Constant.ADDRESS_DETAIL, localDetail);

            getContext().startActivity(intent);
        }


    }
}
