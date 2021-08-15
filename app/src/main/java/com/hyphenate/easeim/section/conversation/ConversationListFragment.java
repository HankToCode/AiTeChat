package com.hyphenate.easeim.section.conversation;

import static com.hyphenate.easeui.widget.EaseImageView.ShapeType.RECTANGLE;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.app.api.Constant;
import com.hyphenate.easeim.app.api.global.EventUtil;
import com.hyphenate.easeim.app.api.global.SP;
import com.hyphenate.easeim.app.api.old_data.EventCenter;
import com.hyphenate.easeim.app.base.BaseActivity;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.common.livedatas.LiveDataBus;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeim.common.utils.PreferenceManager;
import com.hyphenate.easeim.common.utils.ToastUtils;
import com.hyphenate.easeim.section.chat.activity.ChatActivity;
import com.hyphenate.easeim.section.chat.viewmodel.MessageViewModel;
import com.hyphenate.easeim.section.contact.adapter.ContactListAdapter;
import com.hyphenate.easeim.section.conversation.adapter.ConversationListHeadAdapter;
import com.hyphenate.easeim.section.conversation.viewmodel.ConversationListViewModel;
import com.hyphenate.easeim.section.dialog.DemoDialogFragment;
import com.hyphenate.easeim.section.dialog.SimpleDialogFragment;
import com.hyphenate.easeim.section.message.SystemMsgsActivity;
import com.hyphenate.easeim.section.myeaseim.fragment.EaseConversationListFragment;
import com.hyphenate.easeui.manager.EaseAtMessageHelper;
import com.hyphenate.easeui.manager.EaseSystemMsgManager;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationInfo;
import com.hyphenate.easeui.modules.conversation.model.EaseConversationSetStyle;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.zds.base.json.FastJsonUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.util.ArrayList;
import java.util.List;


public class ConversationListFragment extends EaseConversationListFragment implements View.OnClickListener {

    private ConversationListViewModel mViewModel;
    private ConversationListHeadAdapter conversationListHeadAdapter;

    @Override
    public void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        initConversationListLayout();
    }

    /**
     * EventBus接收消息
     *
     * @param center 消息接收
     */
    @Subscribe
    public void onEventMainThread(EventCenter center) {
        if (null != center) {
            if (center.getEventCode() == EventUtil.NOTICNUM) {
                refreshApplyLayout();
            } else if (center.getEventCode() == EventUtil.REFRESH_REMARK) {
                //刷新通讯录和本地数据库
                refreshList(true);
            }
        }
    }

    @Override
    public void onDestroy() {
        EventBus.getDefault().unregister(this);
        super.onDestroy();
    }

    private void initConversationListLayout() {

        conversationListHeadAdapter = new ConversationListHeadAdapter();
        conversationListLayout.addHeaderAdapter(conversationListHeadAdapter);
        //设置无数据时空白页面
        conversationListLayout.getListAdapter().setEmptyLayoutId(R.layout.ease_layout_default_no_data);
        //获取列表控件
        //EaseContactListLayout contactList = contactLayout.getContactList();
        //设置条目高度
        //contactList.setItemHeight((int) EaseCommonUtils.dip2px(mContext, 80));
        //设置条目背景
        //contactList.setItemBackGround(ContextCompat.getDrawable(mContext, R.color.gray));
        //设置头像样式
        conversationListLayout.setAvatarShapeType(RECTANGLE);
        conversationListLayout.setAvatarSize(DensityUtil.dp2px(35));
        conversationListLayout.setAvatarDefaultSrc(ContextCompat.getDrawable(mContext, R.drawable.ic_new_friends));
        //设置头像圆角
        conversationListLayout.setAvatarRadius((int) EaseCommonUtils.dip2px(mContext, 5));

        conversationListLayout.showUnreadDotPosition(EaseConversationSetStyle.UnreadDotPosition.RIGHT);
        conversationListLayout.showSystemMessage(true);

    }


    @Override
    public void onRefresh() {
        super.onRefresh();
        refreshApplyLayout();
    }

    public void refreshApplyLayout() {
        requireActivity().runOnUiThread(() -> {
            int applyJoinGroupcount = (int) PreferenceManager.getInstance().getParam(SP.APPLY_JOIN_GROUP_NUM, 0);
            conversationListHeadAdapter.setApplyJoinGroupCount(applyJoinGroupcount);
            int addUserCount = (int) PreferenceManager.getInstance().getParam(SP.APPLY_ADD_USER_NUM, 0);
            conversationListHeadAdapter.setAddUserCount(addUserCount);
        });
    }

    @Override
    public boolean onMenuItemClick(MenuItem item, int position) {
        EaseConversationInfo info = conversationListLayout.getItem(position);
        Object object = info.getInfo();

        if (object instanceof EMConversation) {
            switch (item.getItemId()) {
                case R.id.action_con_make_top:
                    conversationListLayout.makeConversationTop(position, info);
                    return true;
                case R.id.action_con_cancel_top:
                    conversationListLayout.cancelConversationTop(position, info);
                    return true;
                case R.id.action_con_delete:
                    showDeleteDialog(position, info);
                    return true;
            }
        }
        return super.onMenuItemClick(item, position);
    }

    private void showDeleteDialog(int position, EaseConversationInfo info) {
        new SimpleDialogFragment.Builder((BaseActivity) mContext)
                .setTitle(R.string.delete_conversation)
                .setOnConfirmClickListener(R.string.delete, new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        conversationListLayout.deleteConversation(position, info);
                        LiveDataBus.get().with(DemoConstant.CONVERSATION_DELETE).postValue(new EaseEvent(DemoConstant.CONVERSATION_DELETE, EaseEvent.TYPE.MESSAGE));
                    }
                })
                .showCancelButton(true)
                .show();
    }

    @Override
    public void initListener() {
        super.initListener();

    }

    @Override
    public void initData() {
        initViewModel();

        //需要两个条件，判断是否触发从服务器拉取会话列表的时机，一是第一次安装，二则本地数据库没有会话列表数据
        if (DemoHelper.getInstance().isFirstInstall() && EMClient.getInstance().chatManager().getAllConversations().isEmpty()) {
            mViewModel.fetchConversationsFromServer();
        } else {
            super.initData();
        }
    }

    private void initViewModel() {
        mViewModel = new ViewModelProvider(this).get(ConversationListViewModel.class);

        mViewModel.getDeleteObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
                    mViewModel.loadConversationList();
                    conversationListLayout.loadDefaultData();
                }
            });
        });

        mViewModel.getReadObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
                    conversationListLayout.loadDefaultData();
                }
            });
        });

        mViewModel.getConversationInfoObservable().observe(getViewLifecycleOwner(), response -> {
            parseResource(response, new OnResourceParseCallback<List<EaseConversationInfo>>(true) {
                @Override
                public void onSuccess(@Nullable List<EaseConversationInfo> data) {
                    conversationListLayout.setData(data);
                }
            });
        });

        MessageViewModel messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        LiveDataBus messageChange = messageViewModel.getMessageChange();
        messageChange.with(DemoConstant.NOTIFY_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.CONVERSATION_DELETE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.CONVERSATION_READ, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), this::loadList);
        messageChange.with(DemoConstant.MESSAGE_CALL_SAVE, Boolean.class).observe(getViewLifecycleOwner(), this::refreshList);
        messageChange.with(DemoConstant.MESSAGE_NOT_SEND, Boolean.class).observe(getViewLifecycleOwner(), this::refreshList);
    }

    private void refreshList(Boolean event) {
        if (event == null) {
            return;
        }
        if (event) {
            conversationListLayout.loadDefaultData();
        }
    }

    private void loadList(EaseEvent change) {
        if (change == null) {
            return;
        }
        if (change.isMessageChange() || change.isNotifyChange()
                || change.isGroupLeave() || change.isChatRoomLeave()
                || change.isContactChange()
                || change.type == EaseEvent.TYPE.CHAT_ROOM || change.isGroupChange()) {
            conversationListLayout.loadDefaultData();
        }
    }

    /**
     * 解析Resource<T>
     *
     * @param response
     * @param callback
     * @param <T>
     */
    public <T> void parseResource(Resource<T> response, @NonNull OnResourceParseCallback<T> callback) {
        if (mContext instanceof BaseActivity) {
            ((BaseActivity) mContext).parseResource(response, callback);
        }
    }

    /**
     * toast by string
     *
     * @param message
     */
    public void showToast(String message) {
        ToastUtils.showToast(message);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        }
    }

    @Override
    public void onItemClick(View view, int position) {
        super.onItemClick(view, position);
        Object item = conversationListLayout.getItem(position).getInfo();
        if (item instanceof EMConversation) {
            EMConversation conversation = (EMConversation) item;
            String emUserId = conversation.conversationId();
            if (EaseSystemMsgManager.getInstance().isSystemConversation((EMConversation) item)) {
                SystemMsgsActivity.actionStart(mContext);
            } else {

                boolean isSystem = false;
                String nickName = "";

                if (conversation != null && conversation.getLastMessage() != null && conversation.getLastMessage().ext() != null) {
                    String json = FastJsonUtil.toJSONString(conversation.getLastMessage().ext());

                    if (json.contains("msgType")) {
                        String msgType = FastJsonUtil.getString(json, "msgType");

                        if ("systematic".equals(msgType)) {
                            nickName = "艾特官方";
                            isSystem = true;
                        } else if ("walletMsg".equals(msgType)) {
                            nickName = "钱包助手";
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
                    //移除群组at标志
                    EaseAtMessageHelper.get().removeAtMeGroup(emUserId);
                } else {
                    //设置单聊中环信ID是否包含 -youxin (不包含，加上)
                    if (!emUserId.contains(Constant.ID_REDPROJECT)) {
                        emUserId += Constant.ID_REDPROJECT;
                    }
                }
                ChatActivity.actionStart(mContext, emUserId, EaseCommonUtils.getChatType((EMConversation) item), nickName, isSystem);

            }
        }
    }

    @Override
    public void notifyItemChange(int position) {
        super.notifyItemChange(position);
        LiveDataBus.get().with(DemoConstant.MESSAGE_CHANGE_CHANGE).postValue(new EaseEvent(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.TYPE.MESSAGE));
    }

    @Override
    public void notifyAllChange() {
        super.notifyAllChange();
    }

}
