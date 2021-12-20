package com.android.nanguo.section.contact.adapter;

import android.content.Context;
import android.content.Intent;
import android.widget.ImageView;

import com.android.nanguo.R;
import com.android.nanguo.app.api.Constant;
import com.android.nanguo.app.api.EaseConstant;
import com.android.nanguo.app.api.old_data.GroupInfo;
import com.android.nanguo.app.api.old_data.GroupSuperInfo;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.section.chat.activity.ChatActivity;
import com.donkingliang.groupedadapter.adapter.GroupedRecyclerViewAdapter;
import com.donkingliang.groupedadapter.holder.BaseViewHolder;
import com.hyphenate.easeui.widget.EaseImageView;
import com.zds.base.ImageLoad.GlideUtils;

import java.util.List;

/**
 * 我的群组
 *
 * @author lhb
 */
public class MyGroupAdapter extends GroupedRecyclerViewAdapter {

    private List<GroupSuperInfo> copyGroupList;
//    private MyFilter myFilter;

    public MyGroupAdapter(Context context, List<GroupSuperInfo> list) {
        super(context);
        copyGroupList = list;
    }


    @Override
    public int getGroupCount() {
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 0;
    }

    @Override
    public boolean hasHeader(int groupPosition) {
        return true;
    }

    @Override
    public boolean hasFooter(int groupPosition) {
        return false;
    }

    //返回头部的布局id。(如果hasHeader返回false，这个方法不会执行)
    @Override
    public int getHeaderLayout(int viewType) {
        return R.layout.adapter_my_group_header;
    }

    //返回尾部的布局id。(如果hasFooter返回false，这个方法不会执行)
    @Override
    public int getFooterLayout(int viewType) {
        return 0;
    }

    //返回子项的布局id。
    @Override
    public int getChildLayout(int viewType) {
        return R.layout.adapter_my_group;
    }

    @Override
    public void onBindHeaderViewHolder(BaseViewHolder holder, int groupPosition) {
        GroupSuperInfo item = copyGroupList.get(groupPosition);
        holder.setText(R.id.tv_title, item.getTitle());
        ImageView imageView = (ImageView) holder.get(R.id.tv_img);

        holder.get(R.id.ll_item).setOnClickListener(v -> {
            imageView.setSelected(!imageView.isSelected());
            if (imageView.isSelected()) {
                GlideUtils.loadRoundCircleImage(AppConfig.checkimg(item.getTitle()), (ImageView) holder.get(R.id.tv_img), R.mipmap.ic_group_select, 30);
            } else {
                GlideUtils.loadRoundCircleImage(AppConfig.checkimg(item.getTitle()), (ImageView) holder.get(R.id.tv_img), R.mipmap.ic_group_normal, 30);
            }
        });
    }

    @Override
    public void onBindFooterViewHolder(BaseViewHolder holder, int groupPosition) {

    }

    @Override
    public void onBindChildViewHolder(BaseViewHolder holder, int groupPosition, int childPosition) {

        GroupInfo item = copyGroupList.get(groupPosition).getGroupInfos().get(childPosition);
        holder.setText(R.id.tv_group_name, item.getGroupName());
        GlideUtils.loadRoundCircleImage(AppConfig.checkimg(item.getGroupHead()), (EaseImageView) holder.get(R.id.img_group), R.mipmap.ic_group_default, 30);

        holder.get(R.id.ll_item).setOnClickListener(v -> mContext.startActivity(new Intent(mContext, ChatActivity.class).putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP)
                .putExtra(EaseConstant.EXTRA_USER_ID, item.getHuanxinGroupId())
                .putExtra(Constant.ROOMTYPE, 0)));

    }

/*
    @Override
    protected void convert(BaseViewHolder helper, GroupInfo item) {
        helper.setText(R.id.tv_group_name, item.getGroupName());

        GlideUtils.loadRoundCircleImage(AppConfig.checkimg(item.getGroupHead()), (EaseImageView) helper.getView(R.id.img_group), R.mipmap.ic_group_default, 30);

        helper.setOnClickListener(R.id.ll_item, v -> mContext.startActivity(new Intent(mContext, ChatActivity.class).putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_GROUP)
                .putExtra(EaseConstant.EXTRA_USER_ID, item.getHuanxinGroupId())
                .putExtra(Constant.ROOMTYPE, 0)));
    }


    @Override
    public Filter getFilter() {
        if (myFilter == null) {
            myFilter = new MyFilter(mData);
        }
        return myFilter;
    }

   protected class MyFilter extends Filter {
        List<GroupInfo> mOriginalList = null;

        public MyFilter(List<GroupInfo> myList) {
            this.mOriginalList = myList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (mOriginalList == null) {
                mOriginalList = new ArrayList<GroupInfo>();
            }

            String charString = prefix.toString();
            if (TextUtils.isEmpty(charString)) {
                //没有过滤的内容，则使用源数据
                results.values = copyGroupList;
                results.count = copyGroupList.size();
            } else {
                if (copyGroupList.size() > mOriginalList.size()) {
                    mOriginalList = copyGroupList;
                }

                String prefixString = prefix.toString();
                final int count = mOriginalList.size();
                final ArrayList<GroupInfo> newValues = new ArrayList<GroupInfo>();
                for (int i = 0; i < count; i++) {
                    final GroupInfo bean = mOriginalList.get(i);
                    String groupName = bean.getGroupName();

                    if (groupName.contains(prefixString)) {
                        newValues.add(bean);
                    } else {
                        final String[] words = groupName.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (String word : words) {
                            if (word.contains(prefixString)) {
                                newValues.add(bean);
                                break;
                            }
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            //把过滤后的值返回出来
            mData = ((List<GroupInfo>) results.values);
            notifyDataSetChanged();
        }
    }*/

}