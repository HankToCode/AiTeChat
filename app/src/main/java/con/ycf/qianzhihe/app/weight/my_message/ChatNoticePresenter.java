package con.ycf.qianzhihe.app.weight.my_message;

import android.content.Context;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import con.ycf.qianzhihe.app.weight.ease.chatrow.EaseChatRow;
import con.ycf.qianzhihe.app.weight.ease.presenter.EaseChatRowPresenter;
import com.hyphenate.exceptions.HyphenateException;

/**
 * 作   者：赵大帅
 * 描   述: 通知
 * 日   期: 2017/11/21 9:12
 * 更新日期: 2017/11/21
 */

public class ChatNoticePresenter extends EaseChatRowPresenter {

    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter) {
        return new ChatNoticePacket(cxt, message, position, adapter);
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