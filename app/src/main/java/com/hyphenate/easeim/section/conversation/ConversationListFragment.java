package com.hyphenate.easeim.section.conversation;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Toast;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.app.api.Constant;
import com.hyphenate.easeim.app.api.global.EventUtil;
import com.hyphenate.easeim.app.api.old_data.EventCenter;
import com.hyphenate.easeim.app.db.InviteMessgeDao;
import com.hyphenate.easeim.app.utils.hxSetMessageFree.UnReadMsgCount;
import com.hyphenate.easeim.app.weight.EaseConversationList;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.section.chat.activity.ChatActivity;
import com.hyphenate.easeim.section.chat.viewmodel.MessageViewModel;
import com.hyphenate.easeui.manager.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseEvent;
import com.zds.base.json.FastJsonUtil;

import butterknife.ButterKnife;


public class ConversationListFragment extends BaseConversationListFragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.ease_fragment_conversation_list
                , container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    protected void onEventComing(EventCenter center) {
        //刷新通讯录和本地数据库
        if (center.getEventCode() == EventUtil.REFRESH_REMARK) {
            refresh();
        }
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        MessageViewModel messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        LiveDataBus messageChange = messageViewModel.getMessageChange();
        messageChange.with(DemoConstant.NOTIFY_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), easeEvent -> loadConversationList());
        messageChange.with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), easeEvent -> loadConversationList());
        messageChange.with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), easeEvent -> loadConversationList());
        messageChange.with(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), easeEvent -> loadConversationList());
        messageChange.with(DemoConstant.CONVERSATION_DELETE, EaseEvent.class).observe(getViewLifecycleOwner(), easeEvent -> loadConversationList());
        messageChange.with(DemoConstant.CONVERSATION_READ, EaseEvent.class).observe(getViewLifecycleOwner(), easeEvent -> loadConversationList());
        messageChange.with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), easeEvent -> loadConversationList());
        messageChange.with(DemoConstant.MESSAGE_CALL_SAVE, Boolean.class).observe(getViewLifecycleOwner(), easeEvent -> refresh());
        messageChange.with(DemoConstant.MESSAGE_NOT_SEND, Boolean.class).observe(getViewLifecycleOwner(), easeEvent -> refresh());
    }

    @Override
    protected void initLogic() {
        super.initLogic();
        conversationListView.setDrag(true);
        conversationListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EMConversation conversation =
                        conversationListView.getItem(position);
                String emUserId = conversation.conversationId();

                if (id == Integer.MAX_VALUE) {
                    //删除和某个user会话，如果需要保留聊天记录，传false
                    EMClient.getInstance().chatManager().deleteConversation(emUserId, true);
                    refresh();
                    return;
                }

                if (emUserId.equals(EMClient.getInstance().getCurrentUser())) {
                    Toast.makeText(getActivity(),
                            R.string.Cant_chat_with_yourself,
                            Toast.LENGTH_SHORT).show();
                } else {
                    // start chat acitivity
                    Intent intent = new Intent(getActivity(), ChatActivity.class);
                    boolean isSystem = false;
                    if (conversation != null && conversation.getLastMessage() != null && conversation.getLastMessage().ext() != null) {
                        String json = FastJsonUtil.toJSONString(conversation.getLastMessage().ext());

                        if (json.contains("msgType")) {
                            String msgType = FastJsonUtil.getString(json, "msgType");

                            if ("systematic".equals(msgType)) {
                                intent.putExtra(Constant.NICKNAME, "艾特官方");
                                isSystem = true;
                            } else if ("walletMsg".equals(msgType)) {
                                intent.putExtra(Constant.NICKNAME, "钱包助手");
                                isSystem = true;
                            }

                        }
                        try {
                            emUserId = conversation.conversationId();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if (conversation.isGroup()) {
                        if (conversation.getType() == EMConversation.EMConversationType.ChatRoom) {
                            // it's group chat
                            intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_CHATROOM);
                        } else {
                            intent.putExtra(Constant.EXTRA_CHAT_TYPE, Constant.CHATTYPE_GROUP);
                        }
                        //移除群组at标志
                        EaseAtMessageHelper.get().removeAtMeGroup(emUserId);
                    } else {
                        //设置单聊中环信ID是否包含 -youxin (不包含，加上)
                        if (!emUserId.contains(Constant.ID_REDPROJECT)) {
                            emUserId += Constant.ID_REDPROJECT;
                        }
                    }
                    // it's single chat
                    intent.putExtra(Constant.EXTRA_USER_ID, emUserId);
                    intent.putExtra("isSystem", isSystem);
                    startActivity(intent);
                }
            }
        });
        //red packet code : 红包回执消息在会话列表最后一条消息的展示

        conversationListView.setConversationListHelper(new EaseConversationList.EaseConversationListHelper() {
            @Override
            public String onSetItemSecondaryText(EMMessage lastMessage) {
                if (lastMessage.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RETURNGOLD)) {
                    return "红包退还通知";
                } else if ("系统管理员".equals(lastMessage.getFrom())) {
                    return "房间创建成功";
                } else if (lastMessage.getBooleanAttribute("cmd", false)) {
                    return "";
                }
                return null;
            }
        });
    }

    @Override
    protected void onConnectionDisconnected() {
        super.onConnectionDisconnected();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        getActivity().getMenuInflater().inflate(R.menu.em_delete_message, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        boolean deleteMessage = false;
        if (item.getItemId() == R.id.delete_message) {
            deleteMessage = true;
        } else if (item.getItemId() == R.id.delete_conversation) {
            deleteMessage = false;
        }
        EMConversation tobeDeleteCons =
                conversationListView.getItem(((AdapterView.AdapterContextMenuInfo) item.getMenuInfo()).position);
        if (tobeDeleteCons == null) {
            return true;
        }

        try {
            // delete conversation
            EMClient.getInstance().chatManager().deleteConversation(tobeDeleteCons.conversationId(), deleteMessage);
            InviteMessgeDao inviteMessgeDao =
                    new InviteMessgeDao(getActivity());
            inviteMessgeDao.deleteMessage(tobeDeleteCons.conversationId());
        } catch (Exception e) {
            e.printStackTrace();
        }
        refresh();

        return true;
    }


}
