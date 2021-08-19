package com.hyphenate.easeim.app.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easeim.R;
import com.hyphenate.easeim.app.api.old_data.JsonBankCardList;
import com.hyphenate.easeim.app.api.old_data.TransferRecordInfo;
import com.hyphenate.easeim.app.api.old_http.ApiClient;
import com.hyphenate.easeim.app.api.old_http.AppConfig;
import com.hyphenate.easeim.app.api.old_http.ResultListener;
import com.hyphenate.easeim.app.weight.ease.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseImageView;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.util.StringUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author by created chen cloudy 2018/10/22 16:22
 **/

public class TransferAdapter extends RecyclerView.Adapter<TransferAdapter.MyViewHolder> {
    private OnItemClickListener mOnItemClickListener;

    public interface OnItemClickListener {
        /**
         * 单击的点击事件
         *
         * @param view     画层
         * @param position 点击的条目
         */
        void onItemClick(View view, int position);

        /**
         * 长按的点击事件
         *
         * @param view     画层
         * @param position 点击的条目
         */
        void onItemLongClick(View view, int position);
    }

    public void setOnItemClickListener(OnItemClickListener mOnItemClickListener) {
        this.mOnItemClickListener = mOnItemClickListener;
    }

    private List<TransferRecordInfo> dataBean;
    private Context mContext;

    public void setData(List<TransferRecordInfo> dataBean) {
        this.dataBean = dataBean;
    }

    public TransferAdapter(Context mContext, List<TransferRecordInfo> dataBean) {
        this.mContext = mContext;
        this.dataBean = dataBean;
    }

    public void removeData(int position) {
        dataBean.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public TransferAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.adapter_transfer_record, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final TransferAdapter.MyViewHolder holder, int position) {

        //transferType 1:转出， 2：收入tv_tag
        if (dataBean.get(position).getTransferType().equals("1")) {
            holder.tv_name.setText( "转账给" + dataBean.get(position).getNickName());
            holder.tv_money.setText("" + StringUtil.getFormatValue2(dataBean.get(position).getMoney()));
        } else {
            holder.tv_name.setText( "收入来自" + dataBean.get(position).getNickName());
            holder.tv_money.setText( "+" + StringUtil.getFormatValue2(dataBean.get(position).getMoney()));
        }

//        helper.setText(R.id.tv_time, StringUtil.formatDateMinute(item.getCreateTime()));
        holder.tv_time.setText( com.zds.base.util.StringUtil.formatDateMinute(dataBean.get(position).getCreateTime()));
        if (dataBean.get(position).getSureStatus()==2){
            holder.tv_tag.setVisibility(View.VISIBLE);
        }else {
            holder.tv_tag.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return dataBean.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        EaseImageView img_head;
        TextView tv_money;
        TextView tv_name;
        TextView tv_tag;
        TextView tv_time;

        public MyViewHolder(View view) {
            super(view);
            img_head = view.findViewById(R.id.img_head);
            tv_name = view.findViewById(R.id.tv_name);
            tv_tag = view.findViewById(R.id.tv_tag);
            tv_time = view.findViewById(R.id.tv_time);
            tv_money = view.findViewById(R.id.tv_money);
        }
    }
}
