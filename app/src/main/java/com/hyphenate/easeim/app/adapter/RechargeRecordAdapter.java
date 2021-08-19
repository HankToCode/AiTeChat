package com.hyphenate.easeim.app.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.app.api.old_data.RechargeRecordInfo;
import com.zds.base.util.StringUtil;

import java.util.List;

public class RechargeRecordAdapter extends BaseQuickAdapter<RechargeRecordInfo.DataBean, BaseViewHolder> {
    public RechargeRecordAdapter(@Nullable List<RechargeRecordInfo.DataBean> data) {
        super(R.layout.adapter_recharge_record, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, RechargeRecordInfo.DataBean item) {
        helper.setText(R.id.tv_time, StringUtil.formatDateMinute(item.getCreateTime()));
        helper.setText(R.id.tv_money, item.getRechargeMoney() + "");
    }
}
