package com.android.nanguo.app.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.nanguo.R;
import com.android.nanguo.app.api.old_data.Custom1BaseInfo;

import java.util.List;

public class Custom1Adapter extends BaseQuickAdapter<Custom1BaseInfo, BaseViewHolder> {

    public Custom1Adapter(@Nullable @org.jetbrains.annotations.Nullable List<Custom1BaseInfo> data) {
        super(R.layout.activity_custom1_item,data);
    }

    @Override
    protected void convert(BaseViewHolder baseViewHolder, Custom1BaseInfo s) {
        baseViewHolder.setText(R.id.text,s.getCode())
                .setText(R.id.content,s.getMsg())
                .setGone(R.id.content,s.isOpen());
    }
}
