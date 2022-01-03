package com.android.nanguo.app.adapter;

import android.content.Intent;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hyphenate.easecallkit.widget.EaseImageView;
import com.android.nanguo.R;
import com.android.nanguo.app.api.Constant;
import com.android.nanguo.app.api.EaseConstant;
import com.android.nanguo.app.api.Global;
import com.android.nanguo.app.operate.GroupOperateManager;
import com.android.nanguo.section.common.ContactGroupingActivity;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.android.nanguo.app.api.old_data.GroupDetailInfo;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.section.account.activity.UserInfoDetailActivity;
import com.android.nanguo.section.common.GroupUserDelActivity;

import java.util.List;

/**
 * 房间成员
 *
 * @author lhb
 */
public class MyRoomDeatilAdapter extends BaseQuickAdapter<GroupDetailInfo.GroupUserDetailVoListBean, BaseViewHolder> {
    private String groupName = "";
    private boolean seeFriendFlag = false;//是否能允许群成员之间互相查看详情
    private String emGroupId;
    private int currentUserRank;

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public void setCurrentUserRank(int currentUserRank) {
        this.currentUserRank = currentUserRank;
    }

    public String getEmGroupId() {
        return emGroupId;
    }

    public void setEmGroupId(String emGroupId) {
        this.emGroupId = emGroupId;
    }

    public OnDelClickListener getOnDelClickListener() {
        return mOnDelClickListener;
    }

    public MyRoomDeatilAdapter(List<GroupDetailInfo.GroupUserDetailVoListBean> list) {
        super(R.layout.room_grid, list);
    }

    private int mode = 0;
    private OnDelClickListener mOnDelClickListener;


    @Override
    protected void convert(BaseViewHolder helper,
                           GroupDetailInfo.GroupUserDetailVoListBean item) {
        helper.convertView.setVisibility(View.VISIBLE);
        helper.setGone(R.id.tv_room_mine, false);

        if (item.getUserId().equals("add")) {
            helper.setText(R.id.tv_name, "");
            helper.setImageResource(R.id.iv_avatar, R.mipmap.img_group_member_add);
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //邀请好友入群
//                    ContactActivity.actionStart(mContext, "2", groupName, item.getGroupId());//邀请--联系人列表
                    ContactGroupingActivity.actionStart(mContext, "2", groupName, item.getGroupId());//按联系人分组邀请
                }
            });

        } else if (item.getUserId().equals("del")) {
            helper.setText(R.id.tv_name, "");
            helper.setImageResource(R.id.iv_avatar, R.mipmap.img_group_member_del);
            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mContext.startActivity(new Intent(mContext,
                            GroupUserDelActivity.class)
                            .putExtra("from", "2")
                            .putExtra("groupName", groupName)
                            .putExtra("groupId", item.getGroupId())
                            .putExtra(Constant.PARAM_EM_CHAT_ID, emGroupId)
                    );
                }
            });

        } else {
//            EaseUser userInfo = EaseUserUtils.getUserInfo(item.getNickName());//这里有bug产生，IOS群 安卓获取昵称异常

            if (mode == 1) {
                helper.setGone(R.id.badge_delete, true);
            } else {
                helper.setGone(R.id.badge_delete, false);
            }

//            if (!TextUtils.isEmpty(userInfo.getNickname())) {
//                helper.setText(R.id.tv_name, userInfo.getNickname());
//            }

            if (!TextUtils.isEmpty(item.getFriendNickName())) {
                helper.setText(R.id.tv_name, item.getFriendNickName());
            } else if (!TextUtils.isEmpty(item.getUserNickName())) {
                helper.setText(R.id.tv_name, item.getUserNickName());
            }/* else if (userInfo != null && !TextUtils.isEmpty(userInfo.getNickname())) {
                helper.setText(R.id.tv_name, userInfo.getNickname());
            } */

            //是否为会员vipLevel
            if (!TextUtils.isEmpty(item.getVipLevel())) {
                helper.setTextColor(R.id.tv_name, Color.parseColor("#ffff0000"));
            } else {
                helper.setTextColor(R.id.tv_name, Color.parseColor("#000000"));
            }

//            GlideUtils.GlideLoadCircleErrorImageUtils(mContext, AppConfig.checkimg(item.getUserHead()), (EaseImageView) helper.getView(R.id.iv_avatar), R.mipmap.ic_ng_avatar);
            GlideUtils.loadImageViewLoding(mContext, AppConfig.checkimg(item.getUserHead()), (EaseImageView) helper.getView(R.id.iv_avatar), R.mipmap.ic_ng_avatar, R.mipmap.ic_ng_avatar);


            helper.setOnClickListener(R.id.badge_delete,
                    v -> {
                        if (mOnDelClickListener != null) {
                            mOnDelClickListener.delUser(helper.getPosition());
                        }

                    });

            if (item.getUserRank().equals("1")) {
                helper.setGone(R.id.tv_room_mine, true);
                helper.setText(R.id.tv_room_mine, "管理员");
            }
            if (item.getUserRank().equals("2")) {
                helper.setGone(R.id.tv_room_mine, true);
                helper.setText(R.id.tv_room_mine, "群主");
            }

            if (helper.getPosition() == 0) {
                helper.setGone(R.id.badge_delete, false);
//            if (StringUtil.isEmpty(userInfo.getNickname())) {
//                helper.setText(R.id.tv_name, userInfo.getUsername());
//            }
            }

            helper.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!seeFriendFlag) {
                        ToastUtil.toast("暂未开启查看群成员详情功能");
                        return;
                    }
                    Global.addUserOriginType = Constant.ADD_USER_ORIGIN_TYPE_GROUPCHAT;
                    Global.addUserOriginName = groupName;
                    Global.addUserOriginId = GroupOperateManager.getInstance().getGroupId(emGroupId);
                    Intent intent = new Intent(mContext,
                            UserInfoDetailActivity.class)
                            .putExtra("from", "1")
                            .putExtra("friendUserId", item.getUserId())
                            .putExtra("entryUserId", item.getEntryUserId())
                            .putExtra("chatType", EaseConstant.CHATTYPE_GROUP)
                            .putExtra("userName", item.getFriendNickName() == null ? item.getNickName() : item.getFriendNickName())
                            .putExtra("currentUserRank",currentUserRank);

                    if (currentUserRank == 2 || (currentUserRank == 1 && item.getUserRank().equals("0"))) {
                        intent.putExtra(Constant.PARAM_GROUP_ID, item.getGroupId())
                                .putExtra(Constant.PARAM_EM_GROUP_ID, emGroupId);
                    }

                    mContext.startActivity(intent);
                }
            });

        }

//        //0-普通用户 1-管理员 2-群主
//        helper.setGone(R.id.badge_delete, !"2".equals(item.getUserRank())
//                && "1".equals(item.getUserRank())
//                && helper.getPosition() != mData.size() - 1
//                && helper.getAdapterPosition() != 0);

    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }


    public void setOnDelClickListener(OnDelClickListener onDelClickListener) {
        mOnDelClickListener = onDelClickListener;
    }

    public void setUserReadDetail(boolean seeFriendFlag) {
        this.seeFriendFlag = seeFriendFlag;
    }

    public interface OnDelClickListener {
        void delUser(int pos);
    }

}