package com.hyphenate.easeim.section.conversation.adapter;

import android.view.View;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.app.api.old_data.ChatBgInfo;

import java.util.List;

public class ChatBgAdapter extends BaseQuickAdapter<ChatBgInfo, BaseViewHolder> {

    private SelChatBgOnListener mSelChatBgOnListener;

    public void setSelChatBgOnListener(SelChatBgOnListener selChatBgOnListener) {
        mSelChatBgOnListener = selChatBgOnListener;
    }

    public ChatBgAdapter(@Nullable List<ChatBgInfo> data) {
        super(R.layout.adapter_chat_bg, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ChatBgInfo item) {
        helper.setImageResource(R.id.img_bg, item.getBg());

        if (item.isSel()) {
            helper.setGone(R.id.tv_sel, true);
        } else {
            helper.setGone(R.id.tv_sel, false);
        }

        helper.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mSelChatBgOnListener != null) {
                    mSelChatBgOnListener.selBg(item.getId());
                }
            }
        });
    }

    public interface SelChatBgOnListener {
        void selBg(int id);
    }
}
