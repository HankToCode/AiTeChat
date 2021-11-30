package com.ycf.qianzhihe.app.weight.ease.presenter;

import android.content.Context;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.weight.ease.chatrow.EaseChatRow;
import com.ycf.qianzhihe.app.weight.ease.chatrow.EaseChatRowText;
import com.hyphenate.exceptions.HyphenateException;
import com.zds.base.json.FastJsonUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * Created by zhangsong on 17-10-12.
 */

public class EaseChatTextPresenter extends EaseChatRowPresenter {

    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter) {
        return new EaseChatRowText(cxt, message, position, adapter);
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

        try {
            EMConversation conversation = EMClient.getInstance().chatManager().getConversation(message.conversationId());
            String json = FastJsonUtil.toJSONString(conversation.getLastMessage().ext());
            if (json.contains("msgType") && "deleteuser".equals(FastJsonUtil.getString(json, "msgType"))) {
//                EMClient.getInstance().chatManager().deleteConversation(message.conversationId(), true);
                EventBus.getDefault().post(new EventCenter<>(EventUtil.DELETE_CONTACT));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
