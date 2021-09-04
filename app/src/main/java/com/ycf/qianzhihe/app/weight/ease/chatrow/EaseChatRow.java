package com.ycf.qianzhihe.app.weight.ease.chatrow;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.EaseImageView;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.adapter.ease.EaseMessageAdapter;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.operate.UserOperateManager;
import com.ycf.qianzhihe.app.utils.ProjectUtil;
import com.ycf.qianzhihe.app.weight.ease.EaseChatMessageList;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;

import com.hyphenate.easeui.model.styles.EaseMessageListItemStyle;
import com.hyphenate.easeui.utils.EaseUserUtils;
import com.hyphenate.util.DateUtils;

import java.util.Date;

public abstract class EaseChatRow extends LinearLayout {
    public interface EaseChatRowActionCallback {
        void onResendClick(EMMessage message);

        void onBubbleClick(EMMessage message);

        void onDetachedFromWindow();
    }

    protected static final String TAG = EaseChatRow.class.getSimpleName();

    protected LayoutInflater inflater;
    protected Context context;
    protected BaseAdapter adapter;
    protected EMMessage message;
    protected int position;

    protected TextView timeStampView;
    protected EaseImageView userAvatarView;
    protected View bubbleLayout;
    protected TextView usernickView;

    protected TextView percentageView;
    protected ProgressBar progressBar;
    protected ImageView statusView;
    protected Activity activity;

    protected TextView ackedView;
    protected TextView deliveredView;

    protected EaseChatMessageList.MessageListItemClickListener itemClickListener;
    protected EaseMessageListItemStyle itemStyle;

    private EaseChatRowActionCallback itemActionCallback;

    protected EMCallBack messageSendCallback;
    protected EMCallBack messageReceiveCallback;

    public EaseChatRow(Context context, EMMessage message, int position,
                       BaseAdapter adapter) {
        super(context);
        this.context = context;
        this.message = message;
        this.position = position;
        this.adapter = adapter;
        this.activity = (Activity) context;
        inflater = LayoutInflater.from(context);

        initView();
    }

    @Override
    protected void onDetachedFromWindow() {
        itemActionCallback.onDetachedFromWindow();
        super.onDetachedFromWindow();
    }

    public void updateView(final EMMessage msg) {
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                onViewUpdate(msg);
            }
        });
    }

    private void initView() {
        onInflateView();
        timeStampView = findViewById(R.id.timestamp);
        userAvatarView = findViewById(R.id.iv_userhead);
        bubbleLayout = findViewById(R.id.bubble);
        usernickView = findViewById(R.id.tv_userid);

        progressBar = findViewById(R.id.progress_bar);
        statusView = findViewById(R.id.msg_status);
        ackedView = findViewById(R.id.tv_ack);
        deliveredView = findViewById(R.id.tv_delivered);

        onFindViewById();
    }

    /**
     * set property according message and postion
     *
     * @param message
     * @param position
     */
    public void setUpView(EMMessage message, int position,
                          EaseChatMessageList.MessageListItemClickListener itemClickListener,
                          EaseChatRowActionCallback itemActionCallback,
                          EaseMessageListItemStyle itemStyle) {
        this.message = message;
        this.position = position;
        this.itemClickListener = itemClickListener;
        this.itemActionCallback = itemActionCallback;
        this.itemStyle = itemStyle;

        try {
            setUpBaseView();
        } catch (Exception e) {
            e.printStackTrace();
        }
        onSetUpView();
        setClickListener();
    }

    private void setUpBaseView() throws Exception {
        // set nickname, avatar and background of bubble
        TextView timestamp = findViewById(R.id.timestamp);
        if (timestamp != null) {
            if (position == 0) {
                timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                timestamp.setVisibility(View.VISIBLE);
            } else {
                // show time stamp if interval with last message is > 30 seconds
                EMMessage prevMessage =
                        (EMMessage) adapter.getItem(position - 1);
                if (prevMessage != null && DateUtils.isCloseEnough(message.getMsgTime(), prevMessage.getMsgTime())) {
                    timestamp.setVisibility(View.GONE);
                } else {
                    timestamp.setText(DateUtils.getTimestampString(new Date(message.getMsgTime())));
                    timestamp.setVisibility(View.VISIBLE);
                }
            }
        }
        if (userAvatarView != null) {
            EaseImageView easeImageView = ((EaseImageView) userAvatarView);
//            ImageUtil.setAvatar(easeImageView);//转换异常
            easeImageView.setShapeType(2);
            easeImageView.setRadius(20);
        }
        if (message.direct() == EMMessage.Direct.SEND) {
            if (!TextUtils.isEmpty(UserComm.getUserInfo().getUserHead()))
                GlideUtils.loadImageViewLoding(AppConfig.checkimg(UserComm.getUserInfo().getUserHead()), userAvatarView, R.mipmap.img_default_avatar);
            if (usernickView != null)
                usernickView.setText(UserComm.getUserInfo().getNickName());

            Log.d("chatAdapter", "conversationId=" + message.conversationId());
            Log.d("chatAdapter", "from=" + message.getFrom());
        } else {
            if (message.conversationId().contains("6a1bec8f64fe11eba89700163e0654c2")) {
                userAvatarView.setImageResource(R.mipmap.icon_kefu_avatar);
            } else if (message.conversationId().contains("0d777a9c8f9311eb844f00163e0654c2")) {
                userAvatarView.setImageResource(R.mipmap.icon_exception_handle_kefu_avatar);
            } else if (!TextUtils.isEmpty(message.getStringAttribute(Constant.AVATARURL)))
                GlideUtils.loadImageViewLodingByCircle(AppConfig.checkimg(message.getStringAttribute(Constant.AVATARURL)), userAvatarView, R.mipmap.img_default_avatar);

            if (message.getChatType() == EMMessage.ChatType.Chat) {
                EaseUserUtils.setUserNick(message.getFrom(), usernickView);
            } else if (message.getChatType() == EMMessage.ChatType.GroupChat) {
                String userName = message.getStringAttribute(Constant.USER_IN_GROUP_NAME);

                if (usernickView != null) {
                    usernickView.setText(userName);
                }
            }

        }

        if (EMClient.getInstance().getOptions().getRequireDeliveryAck()) {
            if (deliveredView != null) {
                if (message.isDelivered()) {
                    deliveredView.setVisibility(View.VISIBLE);
                } else {
                    deliveredView.setVisibility(View.INVISIBLE);
                }
            }
        }

        if (EMClient.getInstance().getOptions().getRequireAck()) {
            if (ackedView != null) {
                if (message.isAcked()) {
                    if (deliveredView != null) {
                        deliveredView.setVisibility(View.INVISIBLE);
                    }
                    ackedView.setVisibility(View.VISIBLE);
                } else {
                    ackedView.setVisibility(View.INVISIBLE);
                }
            }
        }

        if (itemStyle != null) {
            if (userAvatarView != null) {
                if (itemStyle.isShowAvatar()) {
                    userAvatarView.setVisibility(View.VISIBLE);
                } else {
                    userAvatarView.setVisibility(View.GONE);
                }
            }
            if (usernickView != null) {
                if (itemStyle.isShowUserNick()) {
                    usernickView.setVisibility(View.VISIBLE);
                } else {
                    usernickView.setVisibility(View.GONE);
                }
            }
            if (bubbleLayout != null) {
                if (message.direct() == EMMessage.Direct.SEND) {
                    if (itemStyle.getMyBubbleBg() != null) {
                        bubbleLayout.setBackgroundDrawable(((EaseMessageAdapter) adapter).getMyBubbleBg());
                    }
                } else if (message.direct() == EMMessage.Direct.RECEIVE) {
                    if (itemStyle.getOtherBubbleBg() != null) {
                        bubbleLayout.setBackgroundDrawable(((EaseMessageAdapter) adapter).getOtherBubbleBg());
                    }
                }
            }
        }

    }

    private void setClickListener() {
        if (bubbleLayout != null) {
            bubbleLayout.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null && itemClickListener.onBubbleClick(message)) {
                        return;
                    }
                    if (itemActionCallback != null) {
                        itemActionCallback.onBubbleClick(message);
                    }
                }
            });

            bubbleLayout.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener != null) {
                        itemClickListener.onBubbleLongClick(message);
                    }
                    return true;
                }
            });
        }

        if (statusView != null) {
            statusView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemActionCallback != null) {
                        itemActionCallback.onResendClick(message);
                    }
                }
            });
        }

        if (userAvatarView != null) {
            userAvatarView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (itemClickListener != null) {
                        if (message.direct() == EMMessage.Direct.SEND) {
                            itemClickListener.onUserAvatarClick(EMClient.getInstance().getCurrentUser());
                        } else {
                            itemClickListener.onUserAvatarClick(message.getFrom());
                        }
                    }
                }
            });
            userAvatarView.setOnLongClickListener(new OnLongClickListener() {

                @Override
                public boolean onLongClick(View v) {
                    if (itemClickListener != null) {

                        if (message.direct() == EMMessage.Direct.SEND) {
                            ToastUtil.toast("不能@自己");
                        } else {
                            String fromNick = message.getStringAttribute(Constant.NICKNAME, "");
                            if (!UserOperateManager.getInstance().hasUserName(message.getFrom())) {
                                UserOperateManager.getInstance().updateUserName(ProjectUtil.transformId(message.getFrom()), fromNick);
                            }
                            itemClickListener.onUserAvatarLongClick(message.getFrom());
                        }
                        return true;
                    }
                    return false;
                }
            });
        }
    }

    /**
     * set callback for sending message
     */
    protected void setMessageSendCallback() {
        if (messageSendCallback == null) {
            messageSendCallback = new EMCallBack() {

                @Override
                public void onSuccess() {
                    if (message != null) {
                        updateView(message);
                    }
                }

                @Override
                public void onProgress(final int progress, String status) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (percentageView != null) {
                                percentageView.setText(progress + "%");
                            }

                        }
                    });
                }

                @Override
                public void onError(int code, String error) {
                    if (message != null) {
                        updateView(message);
                    }
                }
            };
        }
        message.setMessageStatusCallback(messageSendCallback);
    }

    /**
     * set callback for receiving message
     */
    protected void setMessageReceiveCallback() {
        if (messageReceiveCallback == null) {
            messageReceiveCallback = new EMCallBack() {

                @Override
                public void onSuccess() {
                    if (message != null) {
                        updateView(message);
                    }
                }

                @Override
                public void onProgress(final int progress, String status) {
                    activity.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (percentageView != null) {
                                percentageView.setText(progress + "%");
                            }
                        }
                    });
                }

                @Override
                public void onError(int code, String error) {
                    if (message != null) {
                        updateView(message);
                    }
                }
            };
        }
        message.setMessageStatusCallback(messageReceiveCallback);
    }

    protected abstract void onInflateView();

    /**
     * find view by id
     */
    protected abstract void onFindViewById();

    /**
     * refresh view when message status change
     */
    protected abstract void onViewUpdate(EMMessage msg);

    /**
     * setup view
     */
    protected abstract void onSetUpView();
}
