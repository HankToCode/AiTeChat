package com.hyphenate.easeim.app.weight.ease.presenter;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.BaseAdapter;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMFileMessageBody;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeim.app.adapter.ease.EaseMessageAdapter;
import com.hyphenate.easeim.app.domain.EaseShowBigImageNewItem;
import com.hyphenate.easeim.app.weight.ease.chatrow.EaseChatRow;
import com.hyphenate.easeim.app.weight.ease.chatrow.EaseChatRowImage;
import com.hyphenate.easeim.section.common.EaseShowBigImageNewActivity;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by zhangsong on 17-10-12.
 */

public class EaseChatImagePresenter extends EaseChatFilePresenter {
    @Override
    protected EaseChatRow onCreateChatRow(Context cxt, EMMessage message, int position, BaseAdapter adapter) {
        return new EaseChatRowImage(cxt, message, position, adapter);
    }

    @Override
    protected void handleReceiveMessage(final EMMessage message) {
        super.handleReceiveMessage(message);

        getChatRow().updateView(message);

        message.setMessageStatusCallback(new EMCallBack() {
            @Override
            public void onSuccess() {
                getChatRow().updateView(message);
            }

            @Override
            public void onError(int code, String error) {
                getChatRow().updateView(message);
            }

            @Override
            public void onProgress(int progress, String status) {
                getChatRow().updateView(message);
            }
        });
    }

    @Override
    public void onBubbleClick(EMMessage message) {
        EMImageMessageBody imgBody = (EMImageMessageBody) message.getBody();
        if(EMClient.getInstance().getOptions().getAutodownloadThumbnail()){
            if(imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED){
                getChatRow().updateView(message);
                // retry download with click event of user
                EMClient.getInstance().chatManager().downloadThumbnail(message);
            }
        } else{
            if(imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.DOWNLOADING ||
                    imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.PENDING ||
                       imgBody.thumbnailDownloadStatus() == EMFileMessageBody.EMDownloadStatus.FAILED){
                // retry download with click event of user
                EMClient.getInstance().chatManager().downloadThumbnail(message);
                getChatRow().updateView(message);
                return;
            }
        }

        BaseAdapter adapter = getAdapter();
        if (adapter != null && adapter instanceof EaseMessageAdapter) {
            EaseMessageAdapter messageAdapter = (EaseMessageAdapter) adapter;
            int messageCount = messageAdapter.getCount();
            ArrayList<EaseShowBigImageNewItem> list = new ArrayList<EaseShowBigImageNewItem>();
            int position = 0;
            for (int i = 0; i < messageCount; i++) {
                EMMessage itemMessage = messageAdapter.getItem(i);

                if (itemMessage == null || itemMessage.getType() != EMMessage.Type.IMAGE) {
                    continue;
                }
                EMImageMessageBody imgBody1 = (EMImageMessageBody) itemMessage.getBody();

                EaseShowBigImageNewItem imageNewItem = new EaseShowBigImageNewItem();
                File file = new File(imgBody1.getLocalUrl());
                if (file.exists()) {
                    imageNewItem.setUri(Uri.fromFile(file));
                } else {
//                    imageNewItem.setMessageId(message.getMsgId());
//                    imageNewItem.setLocalUrl(imgBody1.getLocalUrl());
                }
                imageNewItem.setMessageId(message.getMsgId());
                imageNewItem.setLocalUrl(imgBody1.getLocalUrl());
                imageNewItem.setRemoteUrl(imgBody1.getRemoteUrl());
//                Log.e("###消息列表内容", imgBody1.getRemoteUrl());
                list.add(imageNewItem);
                if (imgBody.getLocalUrl().equals(imgBody1.getLocalUrl())) {
                    position = list.size() - 1;
                }
            }

            if (list.isEmpty()) return;
            Intent intent = new Intent(getContext(), EaseShowBigImageNewActivity.class);//查看大图
            intent.putParcelableArrayListExtra("images",list);
            intent.putExtra("position",position);
            getContext().startActivity(intent);
        }

//        Intent intent = new Intent(getContext(), EaseShowBigImageActivity.class);
//        File file = new File(imgBody.getLocalUrl());
//        if (file.exists()) {
//            Uri uri = Uri.fromFile(file);
//            intent.putExtra("uri", uri);
//        } else {
//            // The local full size pic does not exist yet.
//            // ShowBigImage needs to download it from the server
//            // first
//            String msgId = message.getMsgId();
//            intent.putExtra("messageId", msgId);
//            intent.putExtra("localUrl", imgBody.getLocalUrl());
//        }
        if (message != null && message.direct() == EMMessage.Direct.RECEIVE && !message.isAcked()
                && message.getChatType() == EMMessage.ChatType.Chat) {
            try {
                EMClient.getInstance().chatManager().ackMessageRead(message.getFrom(), message.getMsgId());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
//        getContext().startActivity(intent);
    }
}
