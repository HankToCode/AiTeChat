package com.ycf.qianzhihe.section.conversation;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.db.InviteMessgeDao;
import com.ycf.qianzhihe.app.weight.ConversationItemView;
import com.ycf.qianzhihe.common.constant.DemoConstant;
import com.ycf.qianzhihe.common.livedatas.LiveDataBus;
import com.ycf.qianzhihe.common.utils.PreferenceManager;
import com.ycf.qianzhihe.section.search.SearchConversationActivity;
import com.zds.base.json.FastJsonUtil;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.global.SP;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.section.chat.activity.ChatActivity;
import com.ycf.qianzhihe.section.chat.viewmodel.MessageViewModel;
import com.hyphenate.easeui.manager.EaseAtMessageHelper;
import com.hyphenate.easeui.model.EaseEvent;


public class ConversationListFragment extends BaseConversationListFragment implements View.OnClickListener {

    private ConversationItemView friendNoticeItem;
    private ConversationItemView groupdNoticeItem;
    private TextView tv_search;

    @Override
    protected void onEventComing(EventCenter center) {
        //刷新通讯录和本地数据库
        if (center.getEventCode() == EventUtil.REFRESH_REMARK) {
            refresh();
        }
        if (center.getEventCode() == EventUtil.NOTICNUM) {
            refresh();
        }
        //修改头像和修改群名称
        if (center.getEventCode() == EventUtil.REFRESH_CONVERSION || center.getEventCode() == EventUtil.REFRESH_GROUP_NAME) {
            refresh();
        }
        if (center.getEventCode() == EventUtil.UNREADCOUNT) {
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
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        conversationListView.setDrag(true);

        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.em_conversation_header, null);
        HeaderItemClickListener clickListener = new HeaderItemClickListener();
        friendNoticeItem = headerView.findViewById(R.id.friend_notice);
        groupdNoticeItem = headerView.findViewById(R.id.group_notice);
        tv_search = headerView.findViewById(R.id.tv_search);
        tv_search.setOnClickListener(this);
        headerView.findViewById(R.id.friend_notice).setOnClickListener(clickListener);
        headerView.findViewById(R.id.group_notice).setOnClickListener(clickListener);
        conversationListView.addHeaderView(headerView);
        conversationListView.setOnItemClickListener((parent, view, position, id) -> {
            EMConversation conversation =
                    conversationListView.getItem(position);
            String emUserId = conversation.conversationId();//在消息列表，不确定点到哪了  报空指针闪退

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
                            intent.putExtra(Constant.NICKNAME, "千纸鹤官方");
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
        });
        //red packet code : 红包回执消息在会话列表最后一条消息的展示

        conversationListView.setConversationListHelper(lastMessage -> {
            if (lastMessage.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RETURNGOLD)) {
                return "红包退还通知";
            } else if ("系统管理员".equals(lastMessage.getFrom())) {
                return "房间创建成功";
            } else if (lastMessage.getBooleanAttribute("cmd", false)) {
                return "";
            }
            return null;
        });
    }

    @Override
    protected void onConnectionDisconnected() {
        super.onConnectionDisconnected();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v,
                                    ContextMenu.ContextMenuInfo menuInfo) {
        requireActivity().getMenuInflater().inflate(R.menu.em_delete_message, menu);
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

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_search) {
            SearchConversationActivity.actionStart(mContext);
        }
    }

    protected class HeaderItemClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.friend_notice:
                    // 好友申请列表
                    friendNoticeItem.setUnreadCount(0);
                    AuditMsgActivity.actionStart(requireContext());
                    break;
                case R.id.group_notice:
                    groupdNoticeItem.setUnreadCount(0);
                    ApplyJoinGroupActivity.actionStart(requireContext());
                    break;
                default:
                    break;
            }
        }

    }

    @Override
    public void refreshApplyLayout() {
        super.refreshApplyLayout();
        requireActivity().runOnUiThread(() -> {
            int applyJoinGroupcount = (int) PreferenceManager.getInstance().getParam(SP.APPLY_JOIN_GROUP_NUM, 0);
            groupdNoticeItem.setUnreadCount(applyJoinGroupcount);
            int addUserCount = (int) PreferenceManager.getInstance().getParam(SP.APPLY_ADD_USER_NUM, 0);
            friendNoticeItem.setUnreadCount(addUserCount);
        });
    }


}
