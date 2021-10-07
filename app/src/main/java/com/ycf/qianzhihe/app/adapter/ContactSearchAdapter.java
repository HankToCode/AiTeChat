package com.ycf.qianzhihe.app.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.old_data.ContactListInfo;
import com.zds.base.ImageLoad.GlideUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 联系人
 *
 * @author lhb
 */
public class ContactSearchAdapter extends BaseQuickAdapter<ContactListInfo.DataBean, BaseViewHolder> implements Filterable {
    private MyFilter myFilter;
    private List<ContactListInfo.DataBean> copyUserList;


    public ContactSearchAdapter(List<ContactListInfo.DataBean> list) {
        super(R.layout.adapter_contact_search, list);
        copyUserList = list;

    }

    @Override
    protected void convert(BaseViewHolder helper, ContactListInfo.DataBean item) {

        helper.setText(R.id.tv_name, item.getFriendNickName());
        GlideUtils.GlideLoadCircleErrorImageUtils(mContext, item.getFriendUserHead(), helper.getView(R.id.img_group), R.mipmap.img_default_avatar);

        //是否为会员vipLevel
        if (!TextUtils.isEmpty(item.getVipLevel())) {
            helper.setTextColor(R.id.tv_name,Color.parseColor("#ffff0000"));
        } else {
            helper.setTextColor(R.id.tv_name,Color.parseColor("#000000"));
        }

        Glide.with(mContext).load(item.getFriendUserHead()).into((ImageView) helper.getView(R.id.img_group));
        //是否在线
        if (!TextUtils.isEmpty(item.getLine())) {
            if (item.getLine().equals("online")) {
                helper.setBackgroundRes(R.id.iv_online_status, R.drawable.dot_green);
            } else {
                helper.setBackgroundRes(R.id.iv_online_status, R.drawable.dot_gray);
            }
        } else {
            helper.setBackgroundRes(R.id.iv_online_status, R.drawable.dot_gray);
        }

    }

    @Override
    public Filter getFilter() {
        if (myFilter == null) {
            myFilter = new MyFilter(mData);
        }
        return myFilter;
    }

    protected class MyFilter extends Filter {
        List<ContactListInfo.DataBean> mOriginalList = null;

        public MyFilter(List<ContactListInfo.DataBean> myList) {
            this.mOriginalList = myList;
        }

        @Override
        protected FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (mOriginalList == null) {
                mOriginalList = new ArrayList<ContactListInfo.DataBean>();
            }

            String charString = prefix.toString();
            if (TextUtils.isEmpty(charString)) {
                //没有过滤的内容，则使用源数据
                results.values = copyUserList;
                results.count = copyUserList.size();
            } else {
                if (copyUserList.size() > mOriginalList.size()) {
                    mOriginalList = copyUserList;
                }

                String prefixString = prefix.toString();
                final int count = mOriginalList.size();
                final ArrayList<ContactListInfo.DataBean> newValues = new ArrayList<ContactListInfo.DataBean>();
                for (int i = 0; i < count; i++) {
                    final ContactListInfo.DataBean bean = mOriginalList.get(i);
                    String username = bean.getFriendNickName();

                    if (username.contains(prefixString)) {
                        newValues.add(bean);
                    } else {
                        final String[] words = username.split(" ");
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
            mData = ((List<ContactListInfo.DataBean>) results.values);
            notifyDataSetChanged();
        }
    }

}