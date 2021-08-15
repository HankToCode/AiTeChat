package com.hyphenate.easeim.section.conversation.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.app.operate.UserOperateManager;
import com.hyphenate.easeim.app.utils.ImageUtil;
import com.hyphenate.easeui.utils.EaseSmileUtils;
import com.hyphenate.util.DateUtils;
import com.zds.base.ImageLoad.GlideUtils;

import java.util.Date;
import java.util.List;

/**
 * 代理收益
 */
public class ChatRecordAdapter extends BaseQuickAdapter<EMMessage, BaseViewHolder> {

    public ChatRecordAdapter(List<EMMessage> list) {
        super(R.layout.adapter_chat_record, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, EMMessage message) {


        helper.setText(R.id.tv_name, UserOperateManager.getInstance().getUserName(message.getFrom()));
        ImageUtil.setAvatar(helper.getView(R.id.img_head));
        GlideUtils.loadImageViewLoding(UserOperateManager.getInstance().getUserAvatar(message.getFrom()), helper.getView(R.id.img_head), R.mipmap.img_default_avatar);
        helper.setText(R.id.tv_time, DateUtils.getTimestampString(new Date(message.getMsgTime())));
        helper.setText(R.id.tv_chat_content, EaseSmileUtils.getSmiledText(mContext,  ((EMTextMessageBody) message.getBody()).getMessage()) );
    }

}