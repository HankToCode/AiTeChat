package com.ycf.qianzhihe.app.utils.hxSetMessageFree;

import androidx.annotation.NonNull;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.adapter.EMAChatManager;
import com.hyphenate.chat.adapter.EMAConversation;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.util.Utils;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class UnReadMsgCount {
    /**
     * 获取未读总数代码(不包括免打扰消息)
     */
    public static Observable<Integer> getUnreadMessageCount() {
        EMAChatManager emaObject = (EMAChatManager) DataTool.getSpecifiedFieldObject(EMClient.getInstance().chatManager(), "emaObject");
        return Observable.just("").map(str -> emaObject.getConversations())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map(emaConversationList ->{
                    int unRead = 0;
                    for (EMAConversation conversation : emaConversationList) {
                        if (conversation.messagesCount() > 0) {
                            if (conversation._getType() != EMAConversation.EMAConversationType.CHATROOM &&
                                    EaseSharedUtils.isEnableMsgRing(Utils.getContext(), EMClient.getInstance().getCurrentUser(), conversation.conversationId()) && conversation.latestMessage().from() != null && !"系统管理员".equals(conversation.latestMessage().from()) && !"em_system".equals(conversation.latestMessage().from())) {
                                unRead += conversation.unreadMessagesCount();
                            }
                        }
                    }
                    return unRead;
                });



    }
}
