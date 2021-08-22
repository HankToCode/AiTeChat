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

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;

//展示会员权益
public class ShowMemberContentLayout extends GridView {
    //    private int[] moneyList = {}; //数据源
    private List<String> data = new ArrayList<>();
    private LayoutInflater mInflater;
    private MyAdapter adapter; //适配器
    int defaultChoose = 0; //默认选中项

    public ShowMemberContentLayout(Context context, AttributeSet attrs) {
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

    public void setMoneyData(List<String> data) {
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
                convertView = mInflater.inflate(R.layout.item_show_member_content, parent, false);
                holder.cb_money = (CheckBox) convertView.findViewById(R.id.cb_money);
                holder.iv_member_icon = convertView.findViewById(R.id.iv_member_icon);
                holder.iv_up = convertView.findViewById(R.id.iv_up);
                holder.tv_member_content = convertView.findViewById(R.id.tv_member_content);
                holder.tv_member_tip = convertView.findViewById(R.id.tv_member_tip);
                convertView.setTag(holder);
            } else {
                holder = (MyViewHolder) convertView.getTag();
            }

            if (data.get(position).equals("好友人数")) {
                holder.iv_member_icon.setBackgroundResource(R.mipmap.icon_member_c1);
                holder.tv_member_content.setText("好友人数");
                holder.tv_member_tip.setText("100");
                holder.tv_member_tip.setVisibility(VISIBLE);
            } else if (data.get(position).equals("加速升级")) {
                holder.iv_member_icon.setBackgroundResource(R.mipmap.icon_member_c2);
                holder.tv_member_content.setText("加速升级");
                holder.tv_member_tip.setText("1.2倍");
                holder.tv_member_tip.setVisibility(VISIBLE);
            } else if (data.get(position).equals("会员标识")) {
                holder.iv_member_icon.setBackgroundResource(R.mipmap.icon_member_c3);
                holder.tv_member_content.setText("会员标识");
                holder.tv_member_tip.setVisibility(GONE);
            } else if (data.get(position).equals("红色昵称")) {
                holder.iv_member_icon.setBackgroundResource(R.mipmap.icon_member_c4);
                holder.tv_member_content.setText("红色昵称");
                holder.tv_member_tip.setVisibility(GONE);
            } else if (data.get(position).equals("星标好友")) {
                holder.iv_member_icon.setBackgroundResource(R.mipmap.icon_member_c5);
                holder.tv_member_content.setText("星标好友");
                holder.tv_member_tip.setVisibility(GONE);
            } else if (data.get(position).equals("动态表情")) {
                holder.iv_member_icon.setBackgroundResource(R.mipmap.icon_member_c6);
                holder.tv_member_content.setText("动态表情");
                holder.tv_member_tip.setVisibility(GONE);
            } else if (data.get(position).equals("群排名靠前")) {
                holder.iv_member_icon.setBackgroundResource(R.mipmap.icon_member_c7);
                holder.tv_member_content.setText("群排名靠前");
                holder.tv_member_tip.setVisibility(GONE);
            } else if (data.get(position).equals("聊天背景")) {
                holder.iv_member_icon.setBackgroundResource(R.mipmap.icon_member_c8);
                holder.tv_member_content.setText("聊天背景");
                holder.tv_member_tip.setVisibility(GONE);
            }

            /*holder.cb_money.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        //设置选中文字颜色
//                        buttonView.setBackgroundResource(R.drawable.icon_rechar_bg);
                        //取消上一个选择
                        if (checkBox != null) {
                            checkBox.setChecked(false);
                        }
                        checkBox = (CheckBox) buttonView;
                    } else {
                        checkBox = null;
                        //设置不选中文字颜色
//                        buttonView.setBackgroundResource(R.drawable.shap_gray_bg);
                    }
                    //回调
                    listener.chooseMoney(position, isChecked, data.get(position));
                }
            });*/
            /*if (position == defaultChoose) {
                holder.rl_item.setBackgroundResource(R.drawable.bor_check_member_p);
                defaultChoose = -1;
                holder.cb_money.setChecked(true);
                checkBox = holder.cb_money;
            }*/
            return convertView;
        }

        private class MyViewHolder {
            private CheckBox cb_money;
            private ImageView iv_member_icon,iv_up;
            private TextView tv_member_content;
            private TextView tv_member_tip;
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
