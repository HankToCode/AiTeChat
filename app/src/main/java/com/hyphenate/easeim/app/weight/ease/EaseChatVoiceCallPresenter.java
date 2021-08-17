package com.hyphenate.easeim.app.weight.ease;

import android.content.Context;
import android.widget.BaseAdapter;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeim.app.weight.ease.chatrow.ChatRowVoiceCall;
import com.hyphenate.easeim.app.weight.ease.chatrow.EaseChatRow;
import com.hyphenate.easeim.app.weight.ease.presenter.EaseChatRowPresenter;

/**
 * Created by zhangsong on 17-10-12.
 */

public class EaseChatVoiceCallPresenter extends EaseChatRowPresenter {
    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter) {
        return new ChatRowVoiceCall(cxt, message, position, adapter);
    }
}
