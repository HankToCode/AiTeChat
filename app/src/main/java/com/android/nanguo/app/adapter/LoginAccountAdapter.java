package com.android.nanguo.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.nanguo.R;
import com.android.nanguo.app.api.global.UserComm;
import com.android.nanguo.app.api.old_data.LoginInfo;
import com.android.nanguo.app.domain.EaseUser;
import com.zds.base.ImageLoad.GlideUtils;

import com.hyphenate.easeui.widget.EaseImageView;

import java.util.List;

/**
 * @author by created chen cloudy 2018/10/22 16:22
 **/

public class LoginAccountAdapter extends RecyclerView.Adapter<LoginAccountAdapter.MyViewHolder> {
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

    private List<EaseUser> dataBean;
    private final Context mContext;

    public void setData(List<EaseUser> dataBean) {
        this.dataBean = dataBean;
    }

    public LoginAccountAdapter(Context mContext, List<EaseUser> dataBean) {
        this.mContext = mContext;
        this.dataBean = dataBean;
    }

    public void removeData(int position) {
        dataBean.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public LoginAccountAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        MyViewHolder holder = new MyViewHolder(
                LayoutInflater.from(mContext).inflate(R.layout.item_multi_login_account, parent, false));
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final LoginAccountAdapter.MyViewHolder holder, int position) {

        holder.tv_name.setText( dataBean.get(position).getNickname());
        holder.tv_id.setText( dataBean.get(position).getUserCode());
        String url = dataBean.get(position).getAvatar();
        GlideUtils.GlideLoadCircleErrorImageUtils(mContext, url, holder.img_head, R.mipmap.ic_ng_avatar);

        LoginInfo loginInfo = UserComm.getUserInfo();
        if (loginInfo.getUserCode().equals(dataBean.get(position).getUserCode())) {
            holder.tv_status.setVisibility(View.VISIBLE);
        } else {
            holder.tv_status.setVisibility(View.GONE);
        }
        holder.ll_item.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mOnItemClickListener != null) {
                    mOnItemClickListener.onItemClick(holder.ll_item,position);
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return dataBean.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {
        EaseImageView img_head;
        TextView tv_name;
        TextView tv_id;
        TextView tv_status;
        RelativeLayout ll_item;

        public MyViewHolder(View view) {
            super(view);
            img_head = view.findViewById(R.id.img_head);
            tv_name = view.findViewById(R.id.tv_name);
            tv_id = view.findViewById(R.id.tv_id);
            tv_status = view.findViewById(R.id.tv_status);
            ll_item = view.findViewById(R.id.ll_item);
        }
    }
}
