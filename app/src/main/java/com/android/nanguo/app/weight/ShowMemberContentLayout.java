package com.android.nanguo.app.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.nanguo.R;
import com.android.nanguo.app.api.new_data.VipBean;

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
        this.data = data;
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
                holder.tv_member_tip.setText("200");
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

            //判断下更高级别会员的权益 显示红色箭头
            if (data.size() == 4) {//一级会员
                if (data.get(position).contains("加速升级")) {
                    holder.tv_member_tip.setText("1.2倍");
                    holder.tv_member_tip.setVisibility(VISIBLE);
                } else if (data.get(position).contains("好友人数")) {
                    holder.tv_member_tip.setText("200");
                    holder.tv_member_tip.setVisibility(VISIBLE);
                }
            } else if (data.size() == 5) {//二级会员
                if (data.get(position).contains("加速升级")) {
                    holder.tv_member_tip.setText("1.5倍");
                    holder.tv_member_tip.setVisibility(VISIBLE);
                    holder.iv_up.setVisibility(VISIBLE);
                } else if (data.get(position).contains("好友人数")) {
                    holder.tv_member_tip.setText("500");
                    holder.tv_member_tip.setVisibility(VISIBLE);
                    holder.iv_up.setVisibility(VISIBLE);
                } else if (data.get(position).contains("红色昵称")) {
                    holder.tv_member_tip.setVisibility(GONE);
                    holder.iv_up.setVisibility(VISIBLE);
                } else if (data.get(position).contains("星标好友")) {
                    holder.tv_member_tip.setVisibility(GONE);
                    holder.iv_up.setVisibility(VISIBLE);
                }
            } else if (data.size() == 6) {
                if (data.get(position).contains("加速升级")) {
                    holder.tv_member_tip.setText("1.8倍");
                    holder.tv_member_tip.setVisibility(VISIBLE);
                    holder.iv_up.setVisibility(VISIBLE);
                } else if (data.get(position).contains("好友人数")) {
                    holder.tv_member_tip.setText("1000");
                    holder.tv_member_tip.setVisibility(VISIBLE);
                    holder.iv_up.setVisibility(VISIBLE);
                } else if (data.get(position).contains("动态表情")) {
                    holder.tv_member_tip.setVisibility(GONE);
                    holder.iv_up.setVisibility(VISIBLE);
                }
            } else if (data.size() == 8) {
                if (data.get(position).contains("加速升级")) {
                    holder.tv_member_tip.setText("2倍");
                    holder.tv_member_tip.setVisibility(VISIBLE);
                    holder.iv_up.setVisibility(VISIBLE);
                } else if (data.get(position).contains("好友人数")) {
                    holder.tv_member_tip.setText("3000");
                    holder.tv_member_tip.setVisibility(VISIBLE);
                    holder.iv_up.setVisibility(VISIBLE);
                } else if (data.get(position).contains("群排名靠前")) {
                    holder.tv_member_tip.setVisibility(GONE);
                    holder.iv_up.setVisibility(VISIBLE);
                } else if (data.get(position).contains("聊天背景")) {
                    holder.tv_member_tip.setVisibility(GONE);
                    holder.iv_up.setVisibility(VISIBLE);
                }
            }

            return convertView;
        }

        private class MyViewHolder {
            private CheckBox cb_money;
            private ImageView iv_member_icon, iv_up;
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
         *
         * @param position gridView的位置
         * @param isCheck  是否选中
         * @param data     data
         */

        void chooseMoney(int position, boolean isCheck, VipBean data);

    }


}
