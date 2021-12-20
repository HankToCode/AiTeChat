/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.nanguo.app.adapter.ease;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMOptions;
import com.android.nanguo.app.api.Constant;
import com.android.nanguo.app.api.old_data.GroupDetailInfo;
import com.android.nanguo.app.operate.UserOperateManager;
import com.android.nanguo.app.weight.ease.EaseChatMessageList;
import com.android.nanguo.app.weight.ease.chatrow.EaseCustomChatRowProvider;
import com.android.nanguo.app.weight.ease.presenter.EaseChatBigExpressionPresenter;
import com.android.nanguo.app.weight.ease.presenter.EaseChatFilePresenter;
import com.android.nanguo.app.weight.ease.presenter.EaseChatImagePresenter;
import com.android.nanguo.app.weight.ease.presenter.EaseChatLocationPresenter;
import com.android.nanguo.app.weight.ease.presenter.EaseChatRowPresenter;
import com.android.nanguo.app.weight.ease.presenter.EaseChatTextPresenter;
import com.android.nanguo.app.weight.ease.presenter.EaseChatVideoPresenter;
import com.android.nanguo.app.weight.ease.presenter.EaseChatVoicePresenter;
import com.android.nanguo.app.weight.my_message.ChatRedPacketturnPresenter;
import com.android.nanguo.app.weight.my_message.ChatidCardPresenter;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.android.nanguo.common.manager.OptionsHelper;
import com.zds.base.util.NumberUtils;

import java.util.List;

/**
 * @author lhb
 * 消息适配器
 */
public class EaseMessageAdapter extends BaseAdapter {

    private final static String TAG = "msg";

    private Context context;

    private static final int HANDLER_MESSAGE_REFRESH_LIST = 0;
    private static final int HANDLER_MESSAGE_SELECT_LAST = 1;
    private static final int HANDLER_MESSAGE_SEEK_TO = 2;

    private static final int MESSAGE_TYPE_RECV_TXT = 0;
    private static final int MESSAGE_TYPE_SENT_TXT = 1;
    private static final int MESSAGE_TYPE_SENT_IMAGE = 2;
    private static final int MESSAGE_TYPE_SENT_LOCATION = 3;
    private static final int MESSAGE_TYPE_RECV_LOCATION = 4;
    private static final int MESSAGE_TYPE_RECV_IMAGE = 5;
    private static final int MESSAGE_TYPE_SENT_VOICE = 6;
    private static final int MESSAGE_TYPE_RECV_VOICE = 7;
    private static final int MESSAGE_TYPE_SENT_VIDEO = 8;
    private static final int MESSAGE_TYPE_RECV_VIDEO = 9;
    private static final int MESSAGE_TYPE_SENT_FILE = 10;
    private static final int MESSAGE_TYPE_RECV_FILE = 11;
    private static final int MESSAGE_TYPE_SENT_EXPRESSION = 12;
    private static final int MESSAGE_TYPE_RECV_EXPRESSION = 13;
    private static final int MESSAGE_TYPE_SEND_EXTENDS = 14;
    private static final int MESSAGE_TYPE_RECV_EXTENDS = 15;

    public int itemTypeCount;

    // reference to conversation object in chatsdk
    private EMConversation conversation;
    EMMessage[] messages = null;

    private String toChatUsername;

    private EaseChatMessageList.MessageListItemClickListener itemClickListener;
    private EaseCustomChatRowProvider customRowProvider;

    private boolean showUserNick;
    private boolean showAvatar;
    private Drawable myBubbleBg;
    private Drawable otherBuddleBg;

    private ListView listView;
    private EaseMessageListItemStyle itemStyle;
    private boolean isCanShowLast = true;
    private List<GroupDetailInfo.GroupUserDetailVoListBean> groupUserList;

    public EaseMessageAdapter(Context context, String username, int chatType, ListView listView) {
        this.context = context;
        this.listView = listView;
        toChatUsername = username;
        EMOptions options = EMClient.getInstance().getOptions();
        options.setSortMessageByServerTime(true);
        this.conversation = EMClient.getInstance().chatManager().getConversation(username, EaseCommonUtils.getConversationType(chatType), true);
        OptionsHelper.getInstance().setSortMessageByServerTime(true);

    }

    //将List按照时间倒序排列
    @SuppressLint("SimpleDateFormat")
    private EMMessage[] invertOrderList(EMMessage[] messages) {
        EMMessage temp_r;
        //做一个冒泡排序，大的在数组的前列
        for (int i = 0; i < messages.length - 1; i++) {
            for (int j = i + 1; j < messages.length; j++) {
                if (messages[i].localTime() > messages[j].localTime()) {//如果队前日期靠前，调换顺序
                    temp_r = messages[i];
                    messages[i] = messages[j];
                    messages[j] = temp_r;
                }
            }
        }
        return messages;
    }

    Handler handler = new Handler() {
        private void refreshList(int position) {
            // you should not call getAllMessages() in UI thread
            // otherwise there is problem when refreshing UI and there is new message arrive
            List<EMMessage> var = conversation.getAllMessages();
            conversation.markAllMessagesAsRead();
            messages = var.toArray(new EMMessage[var.size()]);

            notifyDataSetChanged();
            if (position >= 0) {
                listView.setSelection(position);
            }

            /*Observable.just(messages).map(new Function<EMMessage[], EMMessage[]>() {
                @Override
                public EMMessage[] apply(@NonNull EMMessage[] messages) throws Exception {
                    return invertOrderList(messages);
                }
            }).subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(m -> {
                        messages = m;
                        notifyDataSetChanged();
                        if (position >= 0) {
                            listView.setSelection(position);
                        }
                    });*/
        }

        @Override
        public void handleMessage(android.os.Message message) {
            switch (message.what) {
                case HANDLER_MESSAGE_REFRESH_LIST:
                    if (message.obj != null && NumberUtils.parseInt(message.obj.toString()) > 0) {
                        refreshList(NumberUtils.parseInt(message.obj.toString()));
                    } else {
                        refreshList(-1);
                    }

                    break;
                case HANDLER_MESSAGE_SELECT_LAST:
                    if (messages != null && messages.length > 0) {
                        if (isCanShowLast) {
                            listView.setSelection(messages.length - 1);
                        }

                    }
                    break;
                case HANDLER_MESSAGE_SEEK_TO:
                    int position = message.arg1;
                    listView.setSelection(position);
                    break;
                default:
                    break;
            }
        }
    };

    public void setCanShowLast(boolean canShowLast) {
        isCanShowLast = canShowLast;
    }

    public void refresh() {
        if (handler.hasMessages(HANDLER_MESSAGE_REFRESH_LIST)) {
            return;
        }
        android.os.Message msg = handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST);
        handler.sendMessage(msg);
    }

    /**
     * refresh and select the last
     */
    public void refreshSelectLast() {
        final int TIME_DELAY_REFRESH_SELECT_LAST = 100;
        handler.removeMessages(HANDLER_MESSAGE_REFRESH_LIST);
        handler.removeMessages(HANDLER_MESSAGE_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_REFRESH_LIST, TIME_DELAY_REFRESH_SELECT_LAST);
        handler.sendEmptyMessageDelayed(HANDLER_MESSAGE_SELECT_LAST, TIME_DELAY_REFRESH_SELECT_LAST);
    }

    /**
     * refresh and seek to the position
     */
    public void refreshSeekTo(int position) {
        handler.sendMessage(handler.obtainMessage(HANDLER_MESSAGE_REFRESH_LIST, position));
    }

    @Override
    public EMMessage getItem(int position) {
        if (messages != null && position < messages.length) {
            return messages[position];
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    /**
     * get count of messages
     */
    @Override
    public int getCount() {
        return messages == null ? 0 : messages.length;
    }

    /**
     * get number of message type, here 14 = (EMMessage.Type) * 2
     */
    @Override
    public int getViewTypeCount() {
        if (customRowProvider != null && customRowProvider.getCustomChatRowTypeCount() > 0) {
            //默认的14种方法改成16添加名片  (16改为18) 地图   (18改为20) 转账
            return customRowProvider.getCustomChatRowTypeCount() + 14;
        }
        return 50;
    }


    /**
     * get type of item
     */
    @Override
    public int getItemViewType(int position) {
        EMMessage message = getItem(position);
        if (message == null) {
            return -1;
        }

        if (customRowProvider != null && customRowProvider.getCustomChatRowType(message) > 0) {
            //13改成15(添加名片) 15改为17（地图） 17改为19（转账）
            return customRowProvider.getCustomChatRowType(message) + 13;
        }

        if (message.getType() == EMMessage.Type.TXT) {
            if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_EXPRESSION : MESSAGE_TYPE_SENT_EXPRESSION;
            } else if (message.getBooleanAttribute(Constant.SEND_CARD, false)) {
                //自定义消息扩展(名片) APP自己实现
                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_EXTENDS : MESSAGE_TYPE_SEND_EXTENDS;
            } else if (message.getBooleanAttribute(Constant.TURN, false)) {
                //自定义消息扩展（发送位置）APP自己实现
                return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_EXTENDS : MESSAGE_TYPE_SEND_EXTENDS;
            }
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_TXT : MESSAGE_TYPE_SENT_TXT;
        }
        if (message.getType() == EMMessage.Type.IMAGE) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_IMAGE : MESSAGE_TYPE_SENT_IMAGE;
        }
        if (message.getType() == EMMessage.Type.LOCATION) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_LOCATION : MESSAGE_TYPE_SENT_LOCATION;
        }
        if (message.getType() == EMMessage.Type.VOICE) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VOICE : MESSAGE_TYPE_SENT_VOICE;
        }
        if (message.getType() == EMMessage.Type.VIDEO) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_VIDEO : MESSAGE_TYPE_SENT_VIDEO;
        }
        if (message.getType() == EMMessage.Type.FILE) {
            return message.direct() == EMMessage.Direct.RECEIVE ? MESSAGE_TYPE_RECV_FILE : MESSAGE_TYPE_SENT_FILE;
        }

        return -1;// invalid
    }


    protected EaseChatRowPresenter createChatRowPresenter(EMMessage message, int position) {
        if (customRowProvider != null && customRowProvider.getCustomChatRow(message, position, this) != null) {
            return customRowProvider.getCustomChatRow(message, position, this);
        }

        EaseChatRowPresenter presenter = null;

        switch (message.getType()) {
            case TXT:
                if (message.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                    presenter = new EaseChatBigExpressionPresenter();
                } else if (message.getBooleanAttribute(Constant.SEND_CARD, false)) {
                    presenter = new ChatidCardPresenter();
                } else if (message.getBooleanAttribute(Constant.TURN, false)) {
                    presenter = new ChatRedPacketturnPresenter();
                }/* else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.SURE_TURN)) {
                    presenter = new ChatSureTurnPresenter();
                }*/ else {
                    presenter = new EaseChatTextPresenter();
                }
                break;
            case LOCATION:
                presenter = new EaseChatLocationPresenter();
                break;
            case FILE:
                presenter = new EaseChatFilePresenter();
                break;
            case IMAGE:
                presenter = new EaseChatImagePresenter();
                break;
            case VOICE:
                presenter = new EaseChatVoicePresenter();
                break;
            case VIDEO:
                presenter = new EaseChatVideoPresenter();
                break;
            default:
                break;
        }

        return presenter;
    }


    @Override
    @SuppressLint("NewApi")
    public View getView(final int position, View convertView, ViewGroup parent) {
        EMMessage message = getItem(position);

        EaseChatRowPresenter presenter;

        if (convertView == null) {
            presenter = createChatRowPresenter(message, position);
            convertView = presenter.createChatRow(context, message, position, this);
            convertView.setTag(presenter);
        } else {
            presenter = (EaseChatRowPresenter) convertView.getTag();
        }

        if (message.getChatType() == EMMessage.ChatType.GroupChat) {

            String userName = message.getStringAttribute(Constant.NICKNAME, "");
            if (UserOperateManager.getInstance().hasUserName(message.getFrom())) {
                userName = UserOperateManager.getInstance().getUserName(message.getFrom());
            }

            message.setAttribute(Constant.USER_IN_GROUP_NAME, userName);

        }
        presenter.setup(message, position, itemClickListener, itemStyle);

        return convertView;
    }


    public void setItemStyle(EaseMessageListItemStyle itemStyle) {
        this.itemStyle = itemStyle;
    }


    public void setItemClickListener(EaseChatMessageList.MessageListItemClickListener listener) {
        itemClickListener = listener;
    }

    public void setCustomChatRowProvider(EaseCustomChatRowProvider rowProvider) {
        customRowProvider = rowProvider;
    }


    public boolean isShowUserNick() {
        return showUserNick;
    }


    public boolean isShowAvatar() {
        return showAvatar;
    }


    public Drawable getMyBubbleBg() {
        return myBubbleBg;
    }


    public Drawable getOtherBubbleBg() {
        return otherBuddleBg;
    }

}
