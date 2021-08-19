package con.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import con.ycf.qianzhihe.R;
import con.ycf.qianzhihe.app.adapter.BankCardAdapter;
import con.ycf.qianzhihe.app.api.global.EventUtil;
import con.ycf.qianzhihe.app.api.old_data.EventCenter;
import con.ycf.qianzhihe.app.api.old_data.JsonBankCardList;
import con.ycf.qianzhihe.app.api.old_http.ApiClient;
import con.ycf.qianzhihe.app.api.old_http.AppConfig;
import con.ycf.qianzhihe.app.api.old_http.ResultListener;
import con.ycf.qianzhihe.app.base.BaseInitActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;
import con.zds.base.Toast.ToastUtil;
import con.zds.base.json.FastJsonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 */
public class BankActivity extends BaseInitActivity {

    private JsonBankCardList jsonBankCardList;
    private List<JsonBankCardList.DataBean> dataBean;
    @BindView(R.id.rv_recycler)
    RecyclerView mRecyclerCard;
    @BindView(R.id.iv_not_bank_card_logo)
    ImageView mNotBankCard;
    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    private boolean isedit = false;
    private BankCardAdapter mBankCardAdapter;


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, BankActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bank;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar.setTitle("银行卡");
        mTitleBar.setOnBackPressListener(view -> finish());
        initUserInfo();
    }

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.FLUSHUSERINFO || center.getEventCode() == 1011) {
            initUserInfo();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initUserInfo();
    }


    /**
     * 初始化
     */
    private void initUserInfo() {

        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", "1");
        map.put("pageSize", "99");
        ApiClient.requestNetHandle(BankActivity.this, AppConfig.bankCardList, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                jsonBankCardList = FastJsonUtil.getObject(json, JsonBankCardList.class);
                if (jsonBankCardList != null) {
                    dataBean = jsonBankCardList.getData();
                    if (dataBean.size() > 0) {
                        showBankCard(dataBean);
                        mRecyclerCard.setVisibility(View.VISIBLE);
                        mNotBankCard.setVisibility(View.GONE);
                    } else {
                        mRecyclerCard.setVisibility(View.GONE);
                        mNotBankCard.setVisibility(View.GONE);
                    }
                }
                if (json != null) {
                }
            }

            @Override
            public void onFailure(String msg) {
                dismissLoading();
                ToastUtil.toast(msg);
            }
        });
    }

    protected void showBankCard(List<JsonBankCardList.DataBean> dataBeans) {
        //设置显示布局
        mRecyclerCard.setLayoutManager(new LinearLayoutManager(this));
        //设置删除与加入的动画
        mRecyclerCard.setItemAnimator(new DefaultItemAnimator());
        mBankCardAdapter = new BankCardAdapter(this, dataBeans);
        mRecyclerCard.setLayoutManager(new StaggeredGridLayoutManager(1, StaggeredGridLayoutManager.VERTICAL));
        mRecyclerCard.setAdapter(mBankCardAdapter);
        mBankCardAdapter.setOnItemClickListener(new BankCardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (view.getId() == R.id.iv_delete) {
                    refresh();
                } else {
                    setResult(1111, new Intent()
                            .putExtra("id", dataBean.get(position).getCardId())
                            .putExtra("bankName", dataBean.get(position).getBankName())
                            .putExtra("bankCard", dataBean.get(position).getBankCard()));
                    finish();
                }
            }

            @Override
            public void onItemLongClick(View view, int position) {

            }
        });
    }

    /**
     * 初始化
     */
    private void refresh() {

        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", "1");
        map.put("pageSize", "99");
        ApiClient.requestNetHandle(BankActivity.this, AppConfig.bankCardList, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                jsonBankCardList = FastJsonUtil.getObject(json, JsonBankCardList.class);
                if (jsonBankCardList != null) {
                    dataBean = jsonBankCardList.getData();
                    if (dataBean.size() > 0) {
                        refreshBankCard(dataBean);
                        mRecyclerCard.setVisibility(View.VISIBLE);
                        mNotBankCard.setVisibility(View.GONE);
                    } else {
                        mRecyclerCard.setVisibility(View.GONE);
                        mNotBankCard.setVisibility(View.GONE);
                    }
                }
                if (json != null) {
                }
            }

            @Override
            public void onFailure(String msg) {

            }

            private void refreshBankCard(List<JsonBankCardList.DataBean> dataBean) {
                mBankCardAdapter.setData(dataBean);
                mBankCardAdapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 绑定支付宝
     */
    private void apply() {
//        if (StringUtil.isEmpty(mTvBank.getText().toString())) {
//            toast("请输入银行");
//            return;
//        } else if (StringUtil.isEmpty(mTvBankPay.getText().toString())) {
//            toast("请输入银行卡账号");
//            return;
//        } else if (StringUtil.isEmpty(mTvBankPay2.getText().toString()) || !mTvBankPay2.getText().toString().equals(mTvBankPay.getText().toString())) {
//            toast("银行卡账号输入不一致");
//            return;
//        } else if (StringUtil.isEmpty(mTvName.getText().toString())) {
//            toast("请输入真实姓名");
//            return;
//        }
//        Map<String, Object> map = new HashMap<>();
//        //姓名
//        map.put("realname", mTvName.getText().toString());
//        //银行帐号
//        map.put("bankNumber", mTvBankPay.getText().toString());
//        //银行
//        map.put("bankName", mTvBank.getText().toString());
//        ApiClient.requestNetHandle(this, AppConfig.addBank, "正在提交...", map, new ResultListener() {
//            @Override
//            public void onSuccess(String json, String msg) {
//                toast(msg);
//                isedit = false;
//                MyApplication.getInstance().UpUserInfo();
//            }
//
//            @Override
//            public void onFailure(String msg) {
//                toast(msg);
//            }
//        });
    }


    @OnClick({R.id.tv_add_bank_card})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_add_bank_card:
                BankNewActivity.actionStart(this);
                break;
            default:
        }
    }
}
