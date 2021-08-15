package com.hyphenate.easeim.app.weight;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.easeim.R;


public class ConversationItemView extends LinearLayout{

    private TextView unreadMsgView;

    public ConversationItemView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ConversationItemView(Context context) {
        super(context);
        init(context, null);
    }
    
    private void init(Context context, AttributeSet attrs){
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ConversationItemView);
        String name = ta.getString(R.styleable.ConversationItemView_conversationItemName);
        Drawable image = ta.getDrawable(R.styleable.ConversationItemView_conversationItemImage);
        ta.recycle();
        
        LayoutInflater.from(context).inflate(R.layout.em_widget_conversation_item, this);
        ImageView avatar = (ImageView) findViewById(R.id.avatar);
        unreadMsgView = (TextView) findViewById(R.id.unread_msg_number);
        TextView nameView = (TextView) findViewById(R.id.name);
        if(image != null){
            avatar.setImageDrawable(image);
        }
        nameView.setText(name);
    }
    
    public void setUnreadCount(int unreadCount){
        if (unreadCount <= 0){
            unreadMsgView.setVisibility(View.GONE);
            return;
        }
        unreadMsgView.setVisibility(View.VISIBLE);
        unreadMsgView.setText(String.valueOf(unreadCount));
    }
    
    public void showUnreadMsgView(){
        unreadMsgView.setVisibility(View.VISIBLE);
    }
    public void hideUnreadMsgView(){
        unreadMsgView.setVisibility(View.INVISIBLE);
    }
    
}
