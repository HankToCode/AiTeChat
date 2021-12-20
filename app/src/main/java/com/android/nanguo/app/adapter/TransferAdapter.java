package com.android.nanguo.app.adapter;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.nanguo.R;
import com.zds.base.util.StringUtil;
import com.android.nanguo.app.api.old_data.TransferRecordInfo;

import java.util.List;

/**
 * @author by created chen cloudy 2018/10/22 16:22
 **/

public class TransferAdapter extends BaseQuickAdapter<TransferRecordInfo, BaseViewHolder> {
    public TransferAdapter(@Nullable List<TransferRecordInfo> data) {
        super(R.layout.adapter_transfer_record, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TransferRecordInfo dataBean) {
        //transferType 1:转出， 2：收入tv_tag
        if (dataBean.getTransferType().equals("1")) {
            helper.setText(R.id.tv_name, "向" + dataBean.getNickName()+"转账");
            helper.setText(R.id.tv_money, "" + StringUtil.getFormatValue2(dataBean.getMoney()));
        } else {
            helper.setText(R.id.tv_name, "收入来自" + dataBean.getNickName());
            helper.setText(R.id.tv_money, "+" + StringUtil.getFormatValue2(dataBean.getMoney()));
        }

        helper.setText(R.id.tv_time,  StringUtil.formatDateMinute(dataBean.getCreateTime()));
        if (dataBean.getSureStatus()==2){
            helper.setVisible(R.id.tv_tag, true);
        }else {
            helper.setVisible(R.id.tv_tag, false);
        }
    }
   
}
