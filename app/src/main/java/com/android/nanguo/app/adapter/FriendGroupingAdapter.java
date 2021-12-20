package com.android.nanguo.app.adapter;


import android.view.View;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.nanguo.R;
import com.android.nanguo.app.api.new_data.FriendGroupingBean;

import java.util.List;

public class FriendGroupingAdapter extends BaseQuickAdapter<FriendGroupingBean, BaseViewHolder> {
    public FriendGroupingAdapter(@Nullable List<FriendGroupingBean> data) {
        super(R.layout.item_friend_grouping, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, FriendGroupingBean item) {
        helper.setText(R.id.tv_name, item.getName());

        if (item.isCheck()) {
            helper.getView(R.id.iv_check).setVisibility(View.VISIBLE);
        } else {
            helper.getView(R.id.iv_check).setVisibility(View.GONE);
        }

//        helper.addOnClickListener(R.id.tv_yhk);

    }
}
