package com.ycf.qianzhihe.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.old_data.RechargeRecordInfo;
import com.zds.base.util.StringUtil;
import com.ycf.qianzhihe.app.api.old_data.TransferRecordInfo;
import com.hyphenate.easeui.widget.EaseImageView;

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
            helper.setText(R.id.tv_name, "转账给" + dataBean.getNickName());
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
