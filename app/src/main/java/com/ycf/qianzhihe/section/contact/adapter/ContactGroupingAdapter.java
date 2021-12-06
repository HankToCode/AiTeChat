package com.ycf.qianzhihe.section.contact.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.easeui.widget.EaseImageView;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.old_data.ContactListInfo;
import com.ycf.qianzhihe.app.utils.ImageUtil;
import com.zds.base.ImageLoad.GlideUtils;

import java.util.ArrayList;
import java.util.List;

public class ContactGroupingAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private ArrayList<String> mGroup;
    private ArrayList<ArrayList<ContactListInfo.DataBean>> mItemList;
    private final LayoutInflater mInflater;

    public ContactGroupingAdapter(Context context, ArrayList<String> group, ArrayList<ArrayList<ContactListInfo.DataBean>> itemList) {
        this.mContext = context;
        this.mGroup = group;
        this.mItemList = itemList;
        mInflater = LayoutInflater.from(context);
    }

    private List<ContactListInfo.DataBean> mIdList = new ArrayList<>();

    public List<ContactListInfo.DataBean> getIdList() {
        return mIdList;
    }

    //父项的个数
    @Override
    public int getGroupCount() {
        return mGroup.size();
    }

    //某个父项的子项的个数
    @Override
    public int getChildrenCount(int groupPosition) {
        return mItemList.get(groupPosition).size();
    }

    //获得某个父项
    @Override
    public Object getGroup(int groupPosition) {
        return mGroup.get(groupPosition);
    }

    //获得某个子项
    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mItemList.get(groupPosition).get(childPosition);
    }

    //父项的Id
    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    //子项的id
    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    //获取父项的view
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_grouping, parent, false);
        }
        String group = mGroup.get(groupPosition);
        TextView tv_group_name = (TextView) convertView.findViewById(R.id.tv_group_name);
        TextView tv_number = (TextView) convertView.findViewById(R.id.tv_number);
        ImageView iv_group_arr = (ImageView) convertView.findViewById(R.id.iv_group_arr);
        tv_group_name.setText(group);
        if (isExpanded) {
            iv_group_arr.setImageResource(R.mipmap.icon_grouping_open);
        } else {
            iv_group_arr.setImageResource(R.mipmap.icon_grouping);

        }
        return convertView;
    }

    //获取子项的view
    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final ContactListInfo.DataBean child = mItemList.get(groupPosition).get(childPosition);
        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.item_contact_grouping, parent, false);
        }
        TextView tv_name = (TextView) convertView.findViewById(R.id.tv_name);
        EaseImageView avatar = (EaseImageView) convertView.findViewById(R.id.avatar);
        ImageView iv_online_status = (ImageView) convertView.findViewById(R.id.iv_online_status);
        RelativeLayout rl_item = (RelativeLayout) convertView.findViewById(R.id.rl_item);
        CheckBox ck_contact = (CheckBox) convertView.findViewById(R.id.ck_contact);
        TextView tv_sign = convertView.findViewById(R.id.tv_sign);
        tv_name.setText(child.getFriendNickName());
        ImageUtil.setAvatar((EaseImageView) avatar);
        GlideUtils.loadImageViewLoding(child.getFriendUserHead(), avatar, R.mipmap.ic_ng_avatar);
        if (!TextUtils.isEmpty(child.getUserSign())) {
            tv_sign.setText(child.getUserSign());
        } else {
            tv_sign.setText("这家伙很懒,什么都没有留下,心里却有一个你");
        }
        //是否为会员vipLevel
        if (!TextUtils.isEmpty(child.getVipLevel())) {
            tv_name.setTextColor(Color.parseColor("#ffff0000"));
        } else {
            tv_name.setTextColor(Color.parseColor("#000000"));
        }
        if (child.isChecked()) {
            ck_contact.setChecked(true);
        } else {
            ck_contact.setChecked(false);
        }

        /*rl_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("@@@点击位置=", childPosition + "选中的人="+child.getFriendUserId());

                ck_contact.setChecked(!ck_contact.isChecked());
                if (ck_contact.isChecked()) {
                    if (!mIdList.contains(child)) {
                        mIdList.add(child);
                    }
                } else {
                    if (mIdList.contains(child)) {
                        mIdList.remove(child);
                    }
                }
            }
        });*/
//        ck_contact.setClickable(false);
//        ck_contact.setEnabled(false);
//        ck_contact.setOnCheckedChangeListener(null);
        return convertView;
    }

    //子项是否可选中,如果要设置子项的点击事件,需要返回true
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
