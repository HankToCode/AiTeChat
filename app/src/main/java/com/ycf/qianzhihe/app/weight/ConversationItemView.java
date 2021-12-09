package com.ycf.qianzhihe.app.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ycf.qianzhihe.R;
import com.hyphenate.util.DateUtils;

import java.util.Date;


public class ConversationItemView extends LinearLayout {

    private TextView msgDateView;
    private TextView unreadMsgView;
    private ImageView avatar;
    private TextView nameView;

    public ConversationItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ConversationItemView(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ConversationItemView);
        String name = ta.getString(R.styleable.ConversationItemView_conversationItemName);
        Drawable image = ta.getDrawable(R.styleable.ConversationItemView_conversationItemImage);
        ta.recycle();

        LayoutInflater.from(context).inflate(R.layout.em_widget_conversation_item, this);
        avatar = (ImageView) findViewById(R.id.avatar);
        msgDateView = (TextView) findViewById(R.id.msg_date);
        unreadMsgView = (TextView) findViewById(R.id.unread_msg_number);
        nameView = (TextView) findViewById(R.id.name);
        if (image != null) {
            avatar.setImageDrawable(image);
        }
        nameView.setText(name);

        msgDateView.setText(DateUtils.getTimestampString(new Date(System.currentTimeMillis())));
        msgDateView.setVisibility(GONE);
    }

    public void setName(String name) {
        nameView.setText(name);
    }

    public void setAvatar(Drawable image) {
        avatar.setImageDrawable(image);
    }

    public void setUnreadCount(int unreadCount) {
        if (unreadCount <= 0) {
            unreadMsgView.setVisibility(View.INVISIBLE);
            return;
        }
        unreadMsgView.setVisibility(View.VISIBLE);
        unreadMsgView.setText(String.valueOf(unreadCount));
    }

    public void setUnreadDate(long unreadDate) {
        if (unreadDate <= 0) {
            msgDateView.setVisibility(View.INVISIBLE);
            return;
        }
        msgDateView.setVisibility(View.VISIBLE);
        msgDateView.setText(DateUtils.getTimestampString(new Date(unreadDate)));
    }

    public void showUnreadMsgView() {
        unreadMsgView.setVisibility(View.VISIBLE);
    }

    public void hideUnreadMsgView() {
        unreadMsgView.setVisibility(View.INVISIBLE);
    }

}
