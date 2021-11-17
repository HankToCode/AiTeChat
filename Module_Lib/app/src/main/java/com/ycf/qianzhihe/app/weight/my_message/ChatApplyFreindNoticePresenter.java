package com.ycf.qianzhihe.app.weight.my_message;

import android.content.Context;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.ycf.qianzhihe.app.weight.ease.chatrow.EaseChatRow;
import com.ycf.qianzhihe.app.weight.ease.presenter.EaseChatRowPresenter;
import com.hyphenate.exceptions.HyphenateException;


/**
 * @author lhb
 * 好友申请通知
 */
public class ChatApplyFreindNoticePresenter extends EaseChatRowPresenter {

    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter) {
        return new ChatApplyFriendNoticePacket(cxt, message, position, adapter);
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
}
