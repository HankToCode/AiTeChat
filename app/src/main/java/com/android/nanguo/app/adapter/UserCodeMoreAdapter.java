package com.android.nanguo.app.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.nanguo.R;
import com.android.nanguo.app.api.old_data.UserCodeMallBean;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
 

public class UserCodeMoreAdapter extends BaseAdapter {
    private final List<UserCodeMallBean.DataBean> datas = new ArrayList<>();

    private Context context;

    public void setData(List<UserCodeMallBean.DataBean> imgUrls, Context context) {
        this.datas.clear();
        this.context = context;
        if (imgUrls != null) {
            this.datas.addAll(imgUrls);
        }
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return datas.size();
    }

    @Override
    public Object getItem(int position) {
        return datas.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        MyViewHolder holder=null;
        if (convertView == null) {
            holder = new MyViewHolder();
            convertView = View.inflate(context,R.layout.item_beautiful_mall, null);
            holder.tv_money = convertView.findViewById(R.id.tv_money);
            holder.tv_user_code = convertView.findViewById(R.id.tv_user_code);
            convertView.setTag(holder);
        } else {
            holder = (MyViewHolder) convertView.getTag();
        }
        NumberFormat nf = new DecimalFormat("#.##");
        holder.tv_money.setText(nf.format(Double.parseDouble(datas.get(position).getMoney())) + "元");
        holder.tv_user_code.setText(datas.get(position).getUserCode());



        return convertView;
    }

    private class MyViewHolder {
        private TextView tv_user_code;
        private TextView tv_money;
    }
}
