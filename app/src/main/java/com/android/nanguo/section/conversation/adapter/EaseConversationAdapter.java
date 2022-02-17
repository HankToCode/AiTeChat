package com.android.nanguo.section.conversation.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.BufferType;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.coorchice.library.SuperTextView;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMConversation.EMConversationType;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.EaseImageView;
import com.android.nanguo.R;
import com.android.nanguo.app.api.Constant;
import com.android.nanguo.app.api.global.UserComm;
import com.android.nanguo.app.api.old_data.GroupInfo;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.operate.GroupOperateManager;
import com.android.nanguo.app.operate.UserOperateManager;
import com.android.nanguo.app.utils.ImageUtil;
import com.android.nanguo.app.utils.ProjectUtil;
import com.android.nanguo.app.utils.ease.EaseSmileUtils;
import com.android.nanguo.app.utils.hxSetMessageFree.EaseSharedUtils;
import com.android.nanguo.app.weight.ease.EaseCommonUtils;
import com.android.nanguo.app.weight.ease.EaseConversationList;
import com.android.nanguo.app.weight.ease.model.EaseAtMessageHelper;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.Utils;

import com.hyphenate.util.DateUtils;

import java.util.Date;
import java.util.List;

/**
 * conversation list adapter
 */
public class EaseConversationAdapter extends ArrayAdapter<EMConversation> {
    private static final String TAG = "ChatAllHistoryAdapter";
    private List<EMConversation> conversationList;
    private boolean notiyfyByFilter;

    protected int primaryColor;
    protected int secondaryColor;
    protected int timeColor;
    protected int secondarySize;
    protected float timeSize;
    protected boolean isDrag; // 判断是否可以右滑删除功能

    private String type;
    private String status;

    public EaseConversationAdapter(Context context, int resource,
                                   List<EMConversation> objects) {
        super(context, resource, objects);
        conversationList = objects;
        isDrag = false;
    }

    public void setDrag(boolean isDrag) {
        this.isDrag = isDrag;
    }

    @Override
    public int getCount() {
        return conversationList.size();
    }

    @Override
    public EMConversation getItem(int arg0) {
        if (arg0 < conversationList.size()) {
            return conversationList.get(arg0);
        }
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.ease_row_chat_history, parent, false);
        }
        ViewHolder holder = (ViewHolder) convertView.getTag();
        if (holder == null) {
            holder = new ViewHolder();
            holder.name = (TextView) convertView.findViewById(R.id.name);
            holder.iv_aite_guanfang = (SuperTextView) convertView.findViewById(R.id.iv_aite_guanfang);
            holder.iv_to_top = (SuperTextView) convertView.findViewById(R.id.iv_to_top);
            holder.unreadLabel = (TextView) convertView.findViewById(R.id.unread_msg_number);
            holder.message = (TextView) convertView.findViewById(R.id.message);
            holder.time = (TextView) convertView.findViewById(R.id.time);
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.ImgMsgFree = (ImageView) convertView.findViewById(R.id.img_msg_free);
            holder.chaUnreadPoint = (ImageView) convertView.findViewById(R.id.img_chat_unread_point);
            holder.msgState = convertView.findViewById(R.id.msg_state);
            holder.list_itease_layout = (RelativeLayout) convertView.findViewById(R.id.list_itease_layout);
            holder.bg_view = convertView.findViewById(R.id.bg_view);
            holder.motioned = (TextView) convertView.findViewById(R.id.mentioned);
            holder.deleteLayout = convertView.findViewById(R.id.delete_layout);
            holder.swipeRevealLayout = convertView.findViewById(R.id.swipe_layout);
            convertView.setTag(holder);
        }
        holder.swipeRevealLayout.setLockDrag(!isDrag);

        View finalConvertView = convertView;

        holder.list_itease_layout.setOnClickListener(v -> {
            ListView listView = (ListView) parent;
            if(onItemClickListenerCopy != null){
                onItemClickListenerCopy.onItemClick(listView, finalConvertView, position, position);
            }
        });


        ViewHolder finalHolder = holder;

        holder.deleteLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finalHolder.swipeRevealLayout.close(true);
                ListView listView = (ListView) parent;
                if (listView.getOnItemClickListener() != null) {
                    listView.getOnItemClickListener().onItemClick(listView, finalConvertView, position, Integer.MAX_VALUE);
                }
            }
        });

        holder.message.setText("");
        // get conversation
        EMConversation conversation = getItem(position);
        // get username or group id
        String conversationId = conversation.conversationId();

        if (conversation.getExtField().equals("toTop")) {
            holder.iv_to_top.setVisibility(View.VISIBLE);
            holder.bg_view.setBackgroundColor(Color.parseColor("#F4F9FC"));
        } else {
            holder.iv_to_top.setVisibility(View.GONE);
            holder.bg_view.setBackgroundColor(Color.WHITE);
        }

        String username = "";

        String tempNickname = "";
        //false为不允许响铃，即开启了免打扰
        if (!EaseSharedUtils.isEnableMsgRing(Utils.getContext(), UserComm.getUserId() + Constant.ID_REDPROJECT, conversationId)) {
            holder.ImgMsgFree.setVisibility(View.VISIBLE);
        } else {
            holder.ImgMsgFree.setVisibility(View.GONE);
        }

        holder.iv_aite_guanfang.setVisibility(View.GONE);

        if (conversation.getType() == EMConversationType.GroupChat) {
            String groupId = conversationId;
            if (EaseAtMessageHelper.get().hasAtMeMsg(conversationId)) {
                holder.motioned.setVisibility(View.VISIBLE);
            } else {
                holder.motioned.setVisibility(View.GONE);
            }
            // group message, show group avatar
            EMGroup group1 = EMClient.getInstance().groupManager().getGroup(groupId);
            GroupInfo group = GroupOperateManager.getInstance().getGroupInfo(groupId);
            String groupHead = "";
            /*if (conversation.getLastMessage() != null) {
                EMMessage emMessage = conversation.getLastMessage();
                try {
                    groupHead = emMessage.getStringAttribute("groupHead");
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
            }*/
            if (TextUtils.isEmpty(groupHead)) {
                groupHead = GroupOperateManager.getInstance().getGroupAvatar(groupId);
            }

            String imgUrl = ImageUtil.checkimg(groupHead);
//            Log.e("LEOLEO", "头像地址："+imgUrl);
            GlideUtils.loadImageViewLoding(imgUrl, holder.avatar, R.mipmap.ic_ng_avatar);
            holder.name.setText(group != null ? group.getGroupName() : (group1 != null ? group1.getGroupName() : "异常昵称"));

        } else {

            if (conversation != null && conversation.getLastMessage() != null && conversation.getLastMessage().ext() != null) {
                String json = FastJsonUtil.toJSONString(conversation.getLastMessage().ext());

                String msgType = FastJsonUtil.getString(json, "msgType");
                status = FastJsonUtil.getString(json, "status");
                type = FastJsonUtil.getString(json, "type");
                if (!TextUtils.isEmpty(msgType) && "systematic".equals(msgType)) {
                    username = Constant.ADMIN;
                }
                if (!TextUtils.isEmpty(msgType) && "walletMsg".equals(msgType)) {
                    username = Constant.WALLET;
                }
                if ("1".equals(FastJsonUtil.getString(json, "applyStatus"))) {
                    tempNickname = FastJsonUtil.getString(json, "nickname");
                }

            }

//            holder.ImgMsgFree.setVisibility(View.GONE);
            if (username.equals(Constant.ADMIN)) {//778899445566112233-qianzhihe
                holder.swipeRevealLayout.setLockDrag(true);
                holder.name.setText("南国时光小助手");//南国时光官方
                holder.iv_aite_guanfang.setVisibility(View.VISIBLE);
                /*RichTextUtils.getBuilder("南国时光")
                        .append("官方")
                        .setForegroundColor(Color.parseColor("#762BFF"))
                        .setProportion(0.8f)
                        .setSuperscript()
                        .into(holder.name);*/
                GlideUtils.loadImageView(R.mipmap.ic_logo, holder.avatar);
            } else if (username.equals(Constant.WALLET)) {
                holder.iv_aite_guanfang.setVisibility(View.VISIBLE);
                holder.swipeRevealLayout.setLockDrag(true);
                holder.name.setText("钱包助手");
                GlideUtils.loadImageView(R.mipmap.ic_main_message_wallet, holder.avatar);
            } else {//客服走这里
                String contactId = conversationId;
                String headImg = AppConfig.checkimg(UserOperateManager.getInstance().getUserAvatar(contactId));
                // TODO: 2021/3/30 xgp 处理客服图像 写死
                if (contactId.contains("6a1bec8f64fe11eba89700163e0654c2")) {
                    holder.iv_aite_guanfang.setVisibility(View.VISIBLE);//客服显示官方
                    holder.avatar.setImageResource(R.mipmap.ic_main_message_kf);
                } else if (contactId.contains("d816636e130411ecab930c42a1a8807a")) {//fbce17090a6611ecab930c42a1a8807a
                    holder.iv_aite_guanfang.setVisibility(View.VISIBLE);//客服显示官方
                    holder.avatar.setImageResource(R.mipmap.ic_main_message_kf);
                } else {
                    GlideUtils.loadImageViewLoding(headImg, holder.avatar, R.mipmap.ic_ng_avatar);
                }
                //GlideUtils.loadImageViewLoding(headImg, holder.avatar, R.mipmap.ic_ng_avatar);
                if (UserOperateManager.getInstance().hasUserName(contactId)) {
                    username = UserOperateManager.getInstance().getUserName(contactId);
                } else if (!TextUtils.isEmpty(tempNickname)) {
                    username = tempNickname;
                }

                if (contactId.contains("d816636e130411ecab930c42a1a8807a")) {//fbce17090a6611ecab930c42a1a8807a
                    holder.name.setText("南国时光客服");
                } else if (contactId.contains("0d777a9c8f9311eb844f00163e0654c2")) {
                    holder.name.setText("异常处理客服");
                } else {
                    holder.name.setText(username);
                }
            }

            holder.motioned.setVisibility(View.GONE);

        }
        ImageUtil.setAvatar((EaseImageView) holder.avatar);

        if (conversation.getUnreadMsgCount() > 0) {
            // show unread message count
            //false为不允许响铃，即开启了免打扰不显示具体未读消息数
            if (!EaseSharedUtils.isEnableMsgRing(Utils.getContext(), UserComm.getUserId() + Constant.ID_REDPROJECT, conversationId)) {
                holder.unreadLabel.setVisibility(View.GONE);
                holder.chaUnreadPoint.setVisibility(View.VISIBLE);
            } else {
                holder.unreadLabel.setText(String.valueOf(conversation.getUnreadMsgCount()));
                holder.unreadLabel.setVisibility(View.VISIBLE);
                holder.chaUnreadPoint.setVisibility(View.GONE);
            }
        } else {
            holder.unreadLabel.setVisibility(View.INVISIBLE);
            holder.chaUnreadPoint.setVisibility(View.GONE);
        }

        if (conversation.getAllMsgCount() != 0) {
            // show the content of latest message
            EMMessage lastMessage = conversation.getLastMessage();

            if (lastMessage.getBooleanAttribute("cmd", false)) {
                List<EMMessage> messages = conversation.getAllMessages();

                //old 减1 new:不减->红包消息可以显示
//                int lent = messages.size() - 1;
                int lent = messages.size() - 1;
                for (int i = lent; i > 0; i--) {
                    if (messages.get(i).getBooleanAttribute("cmd", false)) {

                    } else {
                        lastMessage = messages.get(i);
                        break;
                    }
                }
            }
            String content = null;
            if (cvsListHelper != null) {

                if (lastMessage.getChatType() == EMMessage.ChatType.GroupChat) {
                    if (!lastMessage.getFrom().equals("系统管理员")) {
                        content = cvsListHelper.onSetItemSecondaryText(lastMessage);
                    }
                } else {
                    content = cvsListHelper.onSetItemSecondaryText(lastMessage);
                }

            }

            if (!EaseSharedUtils.isEnableMsgRing(Utils.getContext(), UserComm.getUserId() + Constant.ID_REDPROJECT, conversationId)) {
                if (conversation.getUnreadMsgCount() > 0) {
                    holder.message.setText("[" + conversation.getUnreadMsgCount() + "条未读]  " + EaseSmileUtils.getSmiledText(getContext(), EaseCommonUtils.getMessageDigest(lastMessage, (this.getContext()))), BufferType.SPANNABLE);
                } else {
                    holder.message.setText(EaseSmileUtils.getSmiledText(getContext(), EaseCommonUtils.getMessageDigest(lastMessage, (this.getContext()))),
                            BufferType.SPANNABLE);
                }
            } else {
                holder.message.setText(EaseSmileUtils.getSmiledText(getContext(), EaseCommonUtils.getMessageDigest(lastMessage, (this.getContext()))),
                        BufferType.SPANNABLE);
            }


            if (content != null) {
                if (!EaseSharedUtils.isEnableMsgRing(Utils.getContext(), UserComm.getUserId() + Constant.ID_REDPROJECT, conversationId)) {
                    if (conversation.getUnreadMsgCount() > 0) {
                        holder.message.setText("[" + conversation.getUnreadMsgCount() + "]" + content);
                    }
                } else {
                    holder.message.setText(content);
                }
            }

            if (username.equals(Constant.WALLET)) {
                holder.message.setText(ProjectUtil.getWalletMessageTips(type, status));
            }


//            if (conversation.getAllMessages().size() != 0 && conversation.getExtField().equals("toTop")) {
//                holder.ImgMsgFree.setImageResource(R.mipmap.top);
//            } else {
//                holder.ImgMsgFree.setImageResource(R.mipmap.msg_free);
//            }


            holder.time.setText(DateUtils.getTimestampString(new Date(lastMessage.getMsgTime())));
            if (lastMessage.direct() == EMMessage.Direct.SEND && lastMessage.status() == EMMessage.Status.FAIL) {
                holder.msgState.setVisibility(View.VISIBLE);
            } else {
                holder.msgState.setVisibility(View.GONE);
            }
        }


        return convertView;
    }

    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        /*if (!notiyfyByFilter) {
            copyConversationList.clear();
            copyConversationList.addAll(conversationList);
            notiyfyByFilter = false;
        }*/
    }

    public void setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
    }

    public void setSecondaryColor(int secondaryColor) {
        this.secondaryColor = secondaryColor;
    }

    public void setTimeColor(int timeColor) {
        this.timeColor = timeColor;
    }


    public void setSecondarySize(int secondarySize) {
        this.secondarySize = secondarySize;
    }

    public void setTimeSize(float timeSize) {
        this.timeSize = timeSize;
    }


    private EaseConversationList.EaseConversationListHelper cvsListHelper;

    public void setCvsListHelper(EaseConversationList.EaseConversationListHelper cvsListHelper) {
        this.cvsListHelper = cvsListHelper;
    }

    private static class ViewHolder {
        /**
         * who you chat with
         */
        TextView name;
        /**
         * unread message count
         */
        TextView unreadLabel;
        /**
         * content of last message
         */
        TextView message;
        /**
         * time of last message
         */
        TextView time;
        /**
         * avatar
         */
        ImageView avatar;
        /**
         * 消息提醒表示
         */
        ImageView ImgMsgFree;

        /**
         * 消息未读数红色图标
         */
        ImageView chaUnreadPoint;

        /**
         * status of last message
         */
        View msgState;
        /**
         * layout
         */
        RelativeLayout list_itease_layout;
        View bg_view;
        TextView motioned;
        /**
         * 右滑删除
         */
        SwipeRevealLayout swipeRevealLayout;
        View deleteLayout;

        SuperTextView iv_aite_guanfang;
        SuperTextView iv_to_top;
    }


    private AdapterView.OnItemClickListener onItemClickListenerCopy;

    public void setOnItemClickListenerCopy(AdapterView.OnItemClickListener onItemClickListenerCopy) {
        this.onItemClickListenerCopy = onItemClickListenerCopy;
    }
}

