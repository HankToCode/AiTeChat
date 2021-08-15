package com.hyphenate.easeim.section.conversation.adapter;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import com.coorchice.library.SuperTextView;
import com.hyphenate.chat.EMChatRoom;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.app.api.global.SP;
import com.hyphenate.easeim.common.db.entity.InviteMessage;
import com.hyphenate.easeim.common.db.entity.InviteMessageStatus;
import com.hyphenate.easeim.common.db.entity.MsgTypeManageEntity;
import com.hyphenate.easeim.common.enums.SearchType;
import com.hyphenate.easeim.common.manager.PushAndMessageHelper;
import com.hyphenate.easeim.common.utils.PreferenceManager;
import com.hyphenate.easeim.section.contact.activity.AddContactActivity;
import com.hyphenate.easeim.section.contact.activity.GroupContactManageActivity;
import com.hyphenate.easeim.section.conversation.AuditMsgActivity;
import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.manager.EaseAtMessageHelper;
import com.hyphenate.easeui.utils.EaseCommonUtils;
import com.hyphenate.easeui.utils.EaseDateUtils;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.easeui.widget.EaseImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ConversationListHeadAdapter extends EaseBaseRecyclerViewAdapter<Object> {

    public ConversationListHeadAdapter() {
        List<Object> headList = new ArrayList<>();
        int applyJoinGroupcount = (int) PreferenceManager.getInstance().getParam(SP.APPLY_JOIN_GROUP_NUM, 0);
        int addUserCount = (int) PreferenceManager.getInstance().getParam(SP.APPLY_ADD_USER_NUM, 0);

        headList.add(new ConversationListHeadAdapter.NoticeInfo(addUserCount, applyJoinGroupcount));
        setData(headList);
    }

    @Override
    public ViewHolder getViewHolder(ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(mContext).inflate(R.layout.layout_message_notice, parent, false));
    }

    @Override
    public int getEmptyLayoutId() {
        return R.layout.demo_layout_no_data_show_nothing;
    }

    public void setApplyJoinGroupCount(int applyJoinGroupcount) {
        NoticeInfo noticeInfo = (NoticeInfo) getData().get(0);
        noticeInfo.setNewGroupsUnreadCount(applyJoinGroupcount);

    }

    public void setAddUserCount(int addUserCount) {
        NoticeInfo noticeInfo = (NoticeInfo) getData().get(0);
        noticeInfo.setNewFriendsUnreadCount(addUserCount);
    }


    private class MyViewHolder extends ViewHolder<NoticeInfo> implements View.OnClickListener {
        private ConstraintLayout mLlNewFriends;
        private ImageView mIvNewFriends;
        private TextView mTvNewFriendsDate;
        private SuperTextView mTvNewFriendsNum;
        private ConstraintLayout mLlGroupNotice;
        private ImageView mIvGroupNotice;
        private TextView mTvGroupNoticeDate;
        private SuperTextView mTvGroupNoticeNum;


        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        @Override
        public void onClick(View view) {
            NoticeInfo noticeInfo = (NoticeInfo) getData().get(0);
            switch (view.getId()) {
                case R.id.ll_new_friends:
                    noticeInfo.setNewFriendsUnreadCount(0);
                    AuditMsgActivity.actionStart(mContext);
                    break;
                case R.id.ll_group_notice:
                    noticeInfo.setNewGroupsUnreadCount(0);
                    break;
            }
        }

        @Override
        public void initView(View itemView) {
            mLlNewFriends = (ConstraintLayout) findViewById(R.id.ll_new_friends);
            mIvNewFriends = (ImageView) findViewById(R.id.iv_new_friends);
            mTvNewFriendsDate = (TextView) findViewById(R.id.tv_new_friends_date);
            mTvNewFriendsNum = (SuperTextView) findViewById(R.id.tv_new_friends_num);
            mLlGroupNotice = (ConstraintLayout) findViewById(R.id.ll_group_notice);
            mIvGroupNotice = (ImageView) findViewById(R.id.iv_group_notice);
            mTvGroupNoticeDate = (TextView) findViewById(R.id.tv_group_notice_date);
            mTvGroupNoticeNum = (SuperTextView) findViewById(R.id.tv_group_notice_num);

            mLlNewFriends.setOnClickListener(this);
            mLlGroupNotice.setOnClickListener(this);

        }

        @Override
        public void setData(NoticeInfo noticeInfo, int position) {

            mTvNewFriendsNum.setVisibility(noticeInfo.getNewFriendsUnreadCount() == 0 ? View.INVISIBLE : View.VISIBLE);
            mTvGroupNoticeNum.setVisibility(noticeInfo.getNewGroupsUnreadCount() == 0 ? View.INVISIBLE : View.VISIBLE);

        }
    }

    public class NoticeInfo {
        private int newFriendsUnreadCount;
        private int newGroupsUnreadCount;

        public int getNewFriendsUnreadCount() {
            return newFriendsUnreadCount;
        }

        public void setNewFriendsUnreadCount(int newFriendsUnreadCount) {
            this.newFriendsUnreadCount = newFriendsUnreadCount;
        }

        public int getNewGroupsUnreadCount() {
            return newGroupsUnreadCount;
        }

        public void setNewGroupsUnreadCount(int newGroupsUnreadCount) {
            this.newGroupsUnreadCount = newGroupsUnreadCount;
        }

        public NoticeInfo(int newFriendsUnreadCount, int newGroupsUnreadCount) {
            this.newFriendsUnreadCount = newFriendsUnreadCount;
            this.newGroupsUnreadCount = newGroupsUnreadCount;
        }
    }
}
