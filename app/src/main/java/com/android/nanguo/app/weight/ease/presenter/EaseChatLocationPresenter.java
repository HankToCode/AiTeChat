package com.android.nanguo.app.weight.ease.presenter;

import android.content.Context;
import android.content.Intent;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.android.nanguo.app.api.Constant;
import com.android.nanguo.app.weight.ease.chatrow.EaseChatRow;
import com.android.nanguo.app.weight.ease.chatrow.EaseChatRowLocationPacket;
import com.android.nanguo.section.common.HxMapActivity;
import com.hyphenate.exceptions.HyphenateException;
import com.zds.base.util.NumberUtils;

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
        if (message.getType() == EMMessage.Type.LOCATION) {
            String latitude = message.getStringAttribute(Constant.LATITUDE, "");
            String longitude = message.getStringAttribute(Constant.LONGITUDE, "");
            String localDetail = message.getStringAttribute(Constant.LOCAL_DETAIL, "");

            Intent intent = new Intent(getContext(), HxMapActivity.class);
            intent.putExtra(Constant.LATITUDE, NumberUtils.parseDouble(latitude));
            intent.putExtra(Constant.LONGITUDE, NumberUtils.parseDouble(longitude));
            intent.putExtra(Constant.ADDRESS_DETAIL, localDetail);

            getContext().startActivity(intent);
        }


    }
}
