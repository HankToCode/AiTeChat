package com.ycf.qianzhihe.section.conversation;

import static com.darsh.multipleimageselect.helpers.Constants.REQUEST_CODE;

import android.content.Intent;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.ViewModelProvider;

import com.blankj.utilcode.util.ScreenUtils;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.Global;
import com.ycf.qianzhihe.app.db.InviteMessgeDao;
import com.ycf.qianzhihe.app.operate.UserOperateManager;
import com.ycf.qianzhihe.app.weight.PopWinShare;
import com.ycf.qianzhihe.app.weight.ease.model.EaseAtMessageHelper;
import com.ycf.qianzhihe.common.constant.DemoConstant;
import com.ycf.qianzhihe.common.livedatas.LiveDataBus;
import com.ycf.qianzhihe.common.rx.NextObserver;
import com.ycf.qianzhihe.section.account.activity.UserInfoDetailActivity;
import com.ycf.qianzhihe.section.common.ContactActivity;
import com.ycf.qianzhihe.section.common.ContactSearchActivity;
import com.ycf.qianzhihe.section.common.MyQrActivity;
import com.ycf.qianzhihe.section.contact.activity.AddUserActivity;
import com.zds.base.code.activity.CaptureActivity;
import com.zds.base.json.FastJsonUtil;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.section.chat.activity.ChatActivity;
import com.ycf.qianzhihe.section.chat.viewmodel.MessageViewModel;
import com.hyphenate.easeui.model.EaseEvent;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;


public class ConversationListFragment extends BaseConversationListFragment implements View.OnClickListener {

    private TextView tvSearch;
    private ImageView ivOptions;

    @Override
    protected void onEventComing(EventCenter center) {
        //刷新通讯录和本地数据库
        //修改头像和修改群名称
        if (center.getEventCode() == EventUtil.REFRESH_CONVERSION ||
                center.getEventCode() == EventUtil.REFRESH_GROUP_NAME ||
                center.getEventCode() == EventUtil.FLUSHRENAME ||
                center.getEventCode() == EventUtil.NOTICNUM ||
                center.getEventCode() == EventUtil.REFRESH_REMARK) {
            refresh();
        }
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        MessageViewModel messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        LiveDataBus messageChange = messageViewModel.getMessageChange();
        messageChange.with(DemoConstant.NOTIFY_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), easeEvent -> refresh());
        messageChange.with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), easeEvent -> refresh());
        messageChange.with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), easeEvent -> refresh());
        messageChange.with(DemoConstant.CHAT_ROOM_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), easeEvent -> refresh());
        messageChange.with(DemoConstant.CONVERSATION_DELETE, EaseEvent.class).observe(getViewLifecycleOwner(), easeEvent -> refresh());
        messageChange.with(DemoConstant.CONVERSATION_READ, EaseEvent.class).observe(getViewLifecycleOwner(), easeEvent -> refresh());
        messageChange.with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(getViewLifecycleOwner(), easeEvent -> refresh());
        messageChange.with(DemoConstant.MESSAGE_CALL_SAVE, Boolean.class).observe(getViewLifecycleOwner(), easeEvent -> refresh());
        messageChange.with(DemoConstant.MESSAGE_NOT_SEND, Boolean.class).observe(getViewLifecycleOwner(), easeEvent -> refresh());
    }

    @Override
    protected void initListener() {
        super.initListener();
        ivOptions.setOnClickListener(this);
        tvSearch.setOnClickListener(this);

        conversationListView.setOnItemClickListener((parent, view, position, id) -> {
            EMConversation conversation =
                    conversationListView.getItem(position);
            String emUserId = conversation.conversationId();

            if (id == Integer.MAX_VALUE) {
                Observable.just(emUserId)
                        .map(str -> {
                            //删除和某个user会话，如果需要保留聊天记录，传false
                            EMClient.getInstance().chatManager().deleteConversation(str, true);
                            return str;
                        })
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .as(autoDispose())
                        .subscribe(new NextObserver<String>() {
                            @Override
                            public void onNext(@NonNull String o) {
                                refresh();
                            }
                        });
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
                            intent.putExtra(Constant.NICKNAME, "南国小助手");//南国官方
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
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        conversationListView.setDrag(true);

        View headerView = LayoutInflater.from(getActivity()).inflate(R.layout.em_conversation_header, null);
        tvSearch = headerView.findViewById(R.id.tvSearch);
        ivOptions = headerView.findViewById(R.id.ivOptions);
        conversationListView.addHeaderView(headerView);

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
        if (v.getId() == R.id.tvSearch) {
//            SearchConversationActivity.actionStart(mContext);
            ContactSearchActivity.actionStart(mContext);
        } else if (v.getId() == R.id.ivOptions) {
            showPopWinShare(ivOptions);
        }
    }

    /*protected class HeaderItemClickListener implements View.OnClickListener {

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

    }*/

    private PopWinShare popWinShare;

    /**
     * 显示浮窗菜单
     */
    private void showPopWinShare(View view) {
        if (popWinShare == null) {
            View.OnClickListener paramOnClickListener =
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //扫一扫
                            if (v.getId() == R.id.layout_saoyisao) {
                                Global.addUserOriginType = Constant.ADD_USER_ORIGIN_TYPE_QRCODE;
                                Intent intent = new Intent(mContext,
                                        CaptureActivity.class);
                                startActivityForResult(intent, REQUEST_CODE);
                            }//添加好友  //搜索添加好友
                            else if (v.getId() == R.id.layout_add_firend) {
//                                AddContactActivity.actionStart(mContext, SearchType.CHAT);
                                AddUserActivity.actionStart(mContext);
                            } else if (v.getId() == R.id.layout_group) {
                                ContactActivity.actionStart(mContext, "1", null, null);
                            } else if (v.getId() == R.id.layout_my_qr) {
                                Intent intent = new Intent(mContext, MyQrActivity.class);
                                intent.putExtra("from", "1");
                                startActivity(intent);
                            }

                            popWinShare.dismiss();
                        }


                    };

            popWinShare = new PopWinShare(mContext, paramOnClickListener
                    , ivOptions.getTop() + ivOptions.getHeight() + 10,
                    (int) (ScreenUtils.getScreenWidth() - ivOptions.getX() - ivOptions.getWidth()));
            //监听窗口的焦点事件，点击窗口外面则取消显示
            popWinShare.getContentView().setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    popWinShare.dismiss();
                }
            });
        }
        //设置默认获取焦点
        popWinShare.setFocusable(true);
        //以某个控件的x和y的偏移量位置开始显示窗口
        popWinShare.showAsDropDown(view);
        //如果窗口存在，则更新
        popWinShare.update();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                String result = bundle.getString(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);

                if (result.contains("person")) {
                    startActivity(new Intent(mContext,
                            UserInfoDetailActivity.class).putExtra(
                            "friendUserId", result.split("_")[0]));
                } else if (result.contains("group")) {
                    UserOperateManager.getInstance().scanInviteContact(mContext, result);
                }
            }
        }
    }


}
