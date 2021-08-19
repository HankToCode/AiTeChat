package con.ycf.qianzhihe.app.weight.my_message;

import android.content.Context;
import android.widget.BaseAdapter;

import con.ycf.qianzhihe.app.api.old_data.RoomInfo;
import con.ycf.qianzhihe.app.weight.ease.chatrow.EaseChatRow;
import con.ycf.qianzhihe.app.weight.ease.presenter.EaseChatRowPresenter;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.exceptions.HyphenateException;

/**
 * 作   者：赵大帅
 * 描   述: 惩罚
 * 日   期: 2017/11/21 9:12
 * 更新日期: 2017/11/21
 */

public class ChatRedPacketPunishmentPresenter extends EaseChatRowPresenter {
    protected RoomInfo mRoomInfo;

    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter) {
        return new ChatRedPunishmentPacket(cxt, message, position, adapter, mRoomInfo);
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

    public void setRoomInfo(RoomInfo roomInfo) {
        mRoomInfo = roomInfo;
    }
}