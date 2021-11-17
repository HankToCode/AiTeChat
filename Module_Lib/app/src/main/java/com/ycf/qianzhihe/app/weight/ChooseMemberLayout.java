package com.ycf.qianzhihe.app.weight;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.new_data.VipBean;
import com.zds.base.util.NumberUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

//选择会员购买
public class ChooseMemberLayout extends GridView {
    //    private int[] moneyList = {}; //数据源
    private List<VipBean> data = new ArrayList<>();
    private LayoutInflater mInflater;
    private MyAdapter adapter; //适配器
    int defaultChoose = 0; //默认选中项

    public ChooseMemberLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        setData();
    }

    public void setData() {
        mInflater = LayoutInflater.from(getContext());
        //配置适配器
        adapter = new MyAdapter();
        setAdapter(adapter);
    }

    /**
     * 设置默认选择项目，
     *
     * @param defaultChoose
     */
    public void setDefaultPositon(int defaultChoose) {
        this.defaultChoose = defaultChoose;
        adapter.notifyDataSetChanged();
    }

    /**
     * 设置数据源
     *
     * @param moneyData
     */
    public void setMoneyData(int[] moneyData) {
//        this.moneyList = moneyData;
    }

    public void setMoneyData(List<VipBean> data) {
        this.data=data;
    }

    class MyAdapter extends BaseAdapter {
        private CheckBox checkBox;

        @Override
        public int getCount() {
            return data.size();
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            MyViewHolder holder;
            if (convertView == null) {
                holder = new MyViewHolder();
                convertView = mInflater.inflate(R.layout.item_choose_member, parent, false);
                holder.cb_money = (CheckBox) convertView.findViewById(R.id.cb_money);
                holder.tv_money = convertView.findViewById(R.id.tv_money);
                holder.tv_lev = convertView.findViewById(R.id.tv_lev);
                holder.iv_member = convertView.findViewById(R.id.iv_member);
                holder.rl_item = convertView.findViewById(R.id.rl_item);
                holder.iv_member_me = convertView.findViewById(R.id.iv_member_me);
                convertView.setTag(holder);
            } else {
                holder = (MyViewHolder) convertView.getTag();
            }
            NumberFormat nf = new DecimalFormat("#.##");
            holder.tv_money.setText(nf.format(Double.parseDouble(data.get(position).getVipPrice())) + "");
            holder.tv_lev.setText(data.get(position).getName());
            if (!TextUtils.isEmpty(UserComm.getUserInfo().getVipLevel())) {
                if (NumberUtils.parseDouble(UserComm.getUserInfo().getVipLevel()) - 1 == position) {
                    holder.iv_member_me.setVisibility(VISIBLE);
                } else {
                    holder.iv_member_me.setVisibility(GONE);
                }
            } else {
                holder.iv_member_me.setVisibility(GONE);
            }
            holder.cb_money.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        //设置选中文字颜色
//                        buttonView.setBackgroundResource(R.drawable.icon_rechar_bg);
                        holder.rl_item.setBackgroundResource(R.drawable.bor_check_member_p);
                        holder.iv_member.setBackgroundResource(R.mipmap.icon_member_y);

                        //取消上一个选择
                        if (checkBox != null) {
                            checkBox.setChecked(false);
                        }
                        checkBox = (CheckBox) buttonView;
                    } else {
                        checkBox = null;
                        //设置不选中文字颜色
//                        buttonView.setBackgroundResource(R.drawable.shap_gray_bg);
                        holder.rl_item.setBackgroundResource(R.drawable.bor_check_member_un);
                        holder.iv_member.setBackgroundResource(R.mipmap.icon_member_gray);

                    }
                    //回调
                    listener.chooseMoney(position, isChecked, data.get(position));
                }
            });
            if (position == defaultChoose) {
                holder.rl_item.setBackgroundResource(R.drawable.bor_check_member_p);
                defaultChoose = -1;
                holder.cb_money.setChecked(true);
                checkBox = holder.cb_money;
            }
            return convertView;
        }

        private class MyViewHolder {
            private CheckBox cb_money;
            private ImageView iv_member,iv_member_me;
            private TextView tv_money;
            private TextView tv_lev;
            private RelativeLayout rl_item;
        }
    }

    /**
     * 解决嵌套显示不完
     *
     * @param widthMeasureSpec
     * @param heightMeasureSpec
     */

    @Override

    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,

                MeasureSpec.AT_MOST);

        super.onMeasure(widthMeasureSpec, expandSpec);

    }

    private onChoseMoneyListener listener;

    public void setOnChoseMoneyListener(onChoseMoneyListener listener) {
        this.listener = listener;

    }

    public interface onChoseMoneyListener {
        /**
         * 选择金额返回
         *  @param position gridView的位置
         * @param isCheck  是否选中
         * @param data data
         */

        void chooseMoney(int position, boolean isCheck, VipBean data);

    }


}
