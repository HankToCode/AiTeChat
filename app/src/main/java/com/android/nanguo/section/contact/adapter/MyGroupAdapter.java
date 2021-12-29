package com.android.nanguo.section.contact.adapter;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.nanguo.R;
import com.android.nanguo.app.api.Constant;
import com.android.nanguo.app.api.EaseConstant;
import com.android.nanguo.app.api.old_data.GroupInfo;
import com.android.nanguo.app.api.old_data.GroupSuperInfo;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.section.chat.activity.ChatActivity;
import com.hyphenate.easeui.widget.EaseImageView;
import com.zds.base.ImageLoad.GlideUtils;

import java.util.List;

/**
 * 我的群组
 *
 * @author lhb
 */
public class MyGroupAdapter extends BaseExpandableListAdapter {

    private List<GroupSuperInfo> copyGroupList;
    //    private MyFilter myFilter;
    private Context mContext;
    private ExpandableListView mRvList;

    /**
     * ViewHolder
     */
    private ViewHolder mViewHolder;

    public MyGroupAdapter(Context context, List<GroupSuperInfo> list, ExpandableListView rvList) {
        mContext = context;
        copyGroupList = list;
        mRvList = rvList;
        mViewHolder = ViewHolder.getInstance();
    }


    @Override
    public int getGroupCount() {
        return copyGroupList == null ? 0 : copyGroupList.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (copyGroupList == null) return 0;
        List<GroupInfo> childDataBeans = copyGroupList.get(groupPosition).getGroupInfos();
        return childDataBeans == null ? 0 : childDataBeans.size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return copyGroupList == null ? null : copyGroupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        if (copyGroupList == null) return null;
        List<GroupInfo> childDataBeans = copyGroupList.get(groupPosition).getGroupInfos();
        return childDataBeans == null ? null : childDataBeans.get(childPosition);

    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_my_group_header, parent, false);
        }
        GroupSuperInfo item = copyGroupList.get(groupPosition);


        mViewHolder.setText(convertView, R.id.tv_title, item.getTitle());
        if (isExpanded) {
            GlideUtils.loadRoundCircleImage(AppConfig.checkimg(item.getTitle()), (ImageView) mViewHolder.get(convertView, R.id.tv_img), R.mipmap.ic_group_normal, 30);
        } else {
            GlideUtils.loadRoundCircleImage(AppConfig.checkimg(item.getTitle()), (ImageView) mViewHolder.get(convertView, R.id.tv_img), R.mipmap.ic_group_select, 30);
        }

//        mViewHolder.get(convertView, R.id.ll_item).setOnClickListener(v -> {
//            imageView.setSelected(!imageView.isSelected());
//            item.setExpend(imageView.isSelected());
//
//            if(mRvList.isGroupExpanded(groupPosition)){
//                mRvList.collapseGroup(groupPosition);
//            }else{
//                mRvList.expandGroup(groupPosition);
//            }
//        });


        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.adapter_my_group, parent, false);
        }
        GroupSuperInfo groupSuperInfo = copyGroupList.get(groupPosition);
        List<GroupInfo> childDataBeans = groupSuperInfo.getGroupInfos();
        GroupInfo item = childDataBeans.get(childPosition);

        mViewHolder.setText(convertView, R.id.tv_group_name, item.getGroupName());
        GlideUtils.loadRoundCircleImage(AppConfig.checkimg(item.getGroupHead()), (ImageView) mViewHolder.get(convertView, R.id.img_group), R.mipmap.ic_group_default, 30);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }


    /**
     * 作者: Ocean<br><br>
     * 时间: 2020/3/28 9:57 PM<br><br>
     * 邮箱: xinzhaohaibo@aliyun.com<br><br>
     * 描述: ViewHolder
     * 见张神博客：https://blog.csdn.net/lmj623565791/article/details/38902805/
     */
    public static class ViewHolder {

        /**
         * 静态内部类单例
         */
        private static class SingleHolder {
            private static final ViewHolder sViewHolder = new ViewHolder();
        }

        /**
         * 获取SingleHolder实例
         *
         * @return ViewHolder 对象
         */
        public static ViewHolder getInstance() {
            return SingleHolder.sViewHolder;
        }

        /**
         * 私有的构造方法，避免这个类在外部被实例化
         */
        private ViewHolder() {
        }

        /***
         * 获取资源对象
         * @param convertView  视图
         * @param id    控件ID
         * @param <T>   泛型
         * @return 对应的视图类型
         */
        @SuppressWarnings("unchecked")
        public <T extends View> T get(View convertView, int id) {
            // 通过getTag获取控件view的标签，并强转成SparseArray对象,如果它为空这进行实例化，并把起设置成view的标签
            SparseArray<View> viewHolder = (SparseArray<View>) convertView.getTag();
            if (viewHolder == null) {
                viewHolder = new SparseArray<>();
                convertView.setTag(viewHolder);
            }
            View childView = viewHolder.get(id);
            if (childView == null) {
                childView = convertView.findViewById(id);
                viewHolder.put(id, childView);
            }
            return (T) childView;
        }


        /**
         * 设置文字
         *
         * @param convertView 布局View
         * @param childViewId 子控件id
         * @param s           内容
         * @return ViewHolder
         */
        public ViewHolder setText(View convertView, int childViewId, String s) {
            TextView textView = get(convertView, childViewId);
            textView.setText(s);
            return this;
        }

        /**
         * 设置文字
         *
         * @param convertView 布局View
         * @param childViewId 子控件id
         * @param s           内容
         * @return ViewHolder
         */
        public ViewHolder setText(View convertView, int childViewId, int s) {
            TextView textView = get(convertView, childViewId);
            textView.setText(s);
            return this;
        }

        /**
         * 设置图片
         *
         * @param convertView 布局View
         * @param childViewId 子控件id
         * @param drawable    drawable
         * @return ViewHolder
         */
        public ViewHolder setImage(View convertView, int childViewId, Drawable drawable) {
            ImageView imageView = get(convertView, childViewId);
            imageView.setImageDrawable(drawable);
            return this;
        }

        /**
         * 设置图文数据
         *
         * @param convertView   布局View
         * @param childViewId   子控件id
         * @param txt           文本
         * @param leftDrawable  左边图片
         * @param rightDrawable 右图片
         * @return ViewHolder ViewHolder
         */
        public ViewHolder setImageText(View convertView, int childViewId, String txt, Drawable leftDrawable, Drawable rightDrawable) {
            TextView view = get(convertView, childViewId);
            if (isStrNull(txt)) {
                txt = "";
            }
            view.setText(txt);
            view.setCompoundDrawablesWithIntrinsicBounds(leftDrawable, null, rightDrawable, null);
            return this;
        }

        /**
         * 设置文字的右图片
         *
         * @param convertView 布局View
         * @param childViewId 子控件id
         * @return ViewHolder ViewHolder
         */
        public ViewHolder setImageTextRight(View convertView, int childViewId, Drawable rightDrawable) {
            TextView view = get(convertView, childViewId);
            view.setCompoundDrawablesWithIntrinsicBounds(null, null, rightDrawable, null);
            return this;
        }


        /**
         * 空字符串判断
         *
         * @param string 要判空的字符串
         * @return true 空串， false非空
         */
        private boolean isStrNull(String string) {
            if (string == null || "null".equals(string) || "NULL".equals(string)) {
                return true;
            }
            // TextUtils.isEmpty(string);
            String str = string.replace(" ", "");
            return str.length() == 0;
        }

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