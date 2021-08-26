package com.ycf.qianzhihe.section.contact.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.easecallkit.widget.EaseImageView;
import com.ycf.qianzhihe.R;

import java.util.ArrayList;

public class GroupingAdapter extends BaseExpandableListAdapter {
    private Context mContext;
    private ArrayList<String> mGroup;
    private ArrayList<ArrayList<String>> mItemList;
    private final LayoutInflater mInflater;
    public GroupingAdapter(Context context, ArrayList<String> group, ArrayList<ArrayList<String>> itemList){
        this.mContext = context;
        this.mGroup = group;
        this.mItemList = itemList;
        mInflater = LayoutInflater.from(context);
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
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.item_grouping,parent,false);
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
        final String child = mItemList.get(groupPosition).get(childPosition);
        if (convertView == null){
            convertView = mInflater.inflate(R.layout.item_grouping_child,parent,false);
        }
        TextView tv_name = (TextView)convertView.findViewById(R.id.tv_name);
        EaseImageView avatar = (EaseImageView)convertView.findViewById(R.id.avatar);
        ImageView iv_online_status = (ImageView)convertView.findViewById(R.id.iv_online_status);
        tv_name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(mContext,child,Toast.LENGTH_SHORT).show();
            }
        });
        tv_name.setText(child);
        return convertView;
    }
    //子项是否可选中,如果要设置子项的点击事件,需要返回true
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
