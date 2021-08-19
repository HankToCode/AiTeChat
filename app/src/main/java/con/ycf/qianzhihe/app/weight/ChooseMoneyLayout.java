package con.ycf.qianzhihe.app.weight;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;

import con.ycf.qianzhihe.R;

public class ChooseMoneyLayout extends GridView {
    private int[] moneyList = {}; //数据源
    private LayoutInflater mInflater;
    private MyAdapter adapter; //适配器
    int defaultChoose = 0; //默认选中项

    public ChooseMoneyLayout(Context context, AttributeSet attrs) {
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
     * @param defaultChoose
     */
    public void setDefaultPositon(int defaultChoose) {
        this.defaultChoose = defaultChoose;
        adapter.notifyDataSetChanged();
    }
    /**
     * 设置数据源
     * @param moneyData
     */
    public void setMoneyData(int[] moneyData){
        this.moneyList = moneyData;
    }

    class MyAdapter extends BaseAdapter {
        private CheckBox checkBox;
        @Override
        public int getCount() {
            return moneyList.length;
        }
        @Override
        public Object getItem(int position) {
            return moneyList[position];
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
                convertView = mInflater.inflate(R.layout.item_money_pay, parent, false);
                holder.cb_money = (CheckBox) convertView.findViewById(R.id.cb_money);
                convertView.setTag(holder);
            } else {
                holder = (MyViewHolder) convertView.getTag();
            }

            holder.cb_money.setText(getItem(position) + "元");
            holder.cb_money.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        //设置选中文字颜色
                        buttonView.setBackgroundResource(R.drawable.icon_rechar_bg);
                        //取消上一个选择
                        if (checkBox != null) {
                            checkBox.setChecked(false);
                        }
                        checkBox = (CheckBox) buttonView;
                    } else {
                        checkBox = null;
                        //设置不选中文字颜色
                        buttonView.setBackgroundResource(R.drawable.shap_gray_bg);
                    }
                    //回调
                    listener.chooseMoney(position, isChecked, (Integer) getItem(position));
                }
            });
            if (position == defaultChoose) {
                defaultChoose = -1;
                holder.cb_money.setChecked(true);
                checkBox = holder.cb_money;
            }
            return convertView;
        }
        private class MyViewHolder {
            private CheckBox cb_money;
        }
    }

    /**

     * 解决嵌套显示不完

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

         * @param isCheck 是否选中

         * @param moneyNum 钱数

         */

        void chooseMoney(int position, boolean isCheck, int moneyNum);

    }


}
