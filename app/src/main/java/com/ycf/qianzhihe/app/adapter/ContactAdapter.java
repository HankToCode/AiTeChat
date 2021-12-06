package com.ycf.qianzhihe.app.adapter;

import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.zds.base.ImageLoad.GlideUtils;
import com.ycf.qianzhihe.app.api.old_data.ContactListInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 联系人
 *
 * @author lhb
 */
public class ContactAdapter extends BaseQuickAdapter<ContactListInfo.DataBean, BaseViewHolder> implements Filterable {
    private String from = "1";
    private MyFilter myFilter;
    private List<ContactListInfo.DataBean> copyUserList;

    public void setFrom(String from) {
        this.from = from;
    }

    private List<ContactListInfo.DataBean> mIdList = new ArrayList<>();

    private Map<Integer, Boolean> map = new HashMap<>();// 存放已被选中的CheckBox

    public List<ContactListInfo.DataBean> getIdList() {
        return mIdList;
    }

    public ContactAdapter(List<ContactListInfo.DataBean> list) {
        super(R.layout.adapter_contact, list);
        copyUserList = list;

    }

    @Override
    protected void convert(BaseViewHolder helper, ContactListInfo.DataBean item) {

        helper.setText(R.id.tv_name, item.getFriendNickName());
        GlideUtils.GlideLoadCircleErrorImageUtils(mContext, item.getFriendUserHead(), helper.getView(R.id.img_group), R.mipmap.ic_ng_avatar);

        //是否为会员vipLevel
        if (!TextUtils.isEmpty(item.getVipLevel())) {
            helper.setTextColor(R.id.tv_name,Color.parseColor("#ffff0000"));
        } else {
            helper.setTextColor(R.id.tv_name,Color.parseColor("#000000"));
        }

        Glide.with(mContext).load(item.getFriendUserHead()).into((ImageView) helper.getView(R.id.img_group));

        CheckBox checkBox = helper.getView(R.id.ck_contact);

        checkBox.setOnCheckedChangeListener(null);
        checkBox.setChecked(false);
        checkBox.setClickable(true);

        if (from.equals("1")) {
            helper.setGone(R.id.ck_contact, true);
            //是否加入群组 0 未加入 1 已加入
            if (Constant.FLAG_ADD_GROUP.equals(item.getAddGroupFlag())) {
                checkBox.setChecked(true);
                checkBox.setClickable(false);
                helper.itemView.setOnClickListener(null);
            }

            if (mIdList.contains(item)) {
                checkBox.setChecked(true);
            }
            helper.setOnCheckedChangeListener(R.id.ck_contact, (buttonView, isChecked) -> {

                if (isChecked) {
                    if (!mIdList.contains(item)) {
                        mIdList.add(item);
                    }
                } else {
                    if (mIdList.contains(item)) {
                        mIdList.remove(item);
                    }
                }
            });
        } else if (from.equals("4")) {
            helper.setGone(R.id.ck_contact, true);
            if (mIdList.contains(item)) {
                checkBox.setChecked(true);
            }
            helper.setOnCheckedChangeListener(R.id.ck_contact, (buttonView, isChecked) -> {
                mIdList.clear();
                mIdList.add(item);
            });
        } else {
            helper.setGone(R.id.ck_contact, false);
        }


        helper.itemView.setOnClickListener(view -> checkBox.setChecked(!checkBox.isChecked()));


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