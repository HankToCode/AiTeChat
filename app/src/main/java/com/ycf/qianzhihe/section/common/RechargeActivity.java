package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ehking.sdk.wepay.interfaces.WalletPay;
import com.ehking.sdk.wepay.net.bean.AuthType;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.WalletRechargeBean;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.utils.XClickUtil;
import com.ycf.qianzhihe.app.weight.ChooseMoneyLayout;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.StringUtil;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import com.zds.base.Toast.ToastUtil;

//充值页面
public class RechargeActivity extends BaseInitActivity {

    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.et_amount)
    EditText et_amount;
    @BindView(R.id.tv_recharge)
    TextView tv_recharge;
    @BindView(R.id.tv_chongzhi)
    TextView tv_chongzhi;//充值说明
    @BindView(R.id.tv_money)
    TextView tv_money;//当前余额
    @BindView(R.id.cml_money)
    ChooseMoneyLayout cml_money;
    private int rechargeMoney = 0;


    //结果返回最多重新查询次数
    private int maxCount = 5;
    private Handler handler = new Handler();


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, RechargeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_recharge;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        tv_money.setText(StringUtil.getFormatValue2(UserComm.getUserInfo().getMoney()));
        mTitleBar.setTitle("充值");
        mTitleBar.setOnBackPressListener(view -> finish());
        mTitleBar.setRightTitle("充值记录");
        mTitleBar.setOnRightClickListener(view -> RechargeRecordActivity.actionStart(mContext));

        et_amount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                String afterStr = editable.toString().trim();

                if (TextUtils.isEmpty(afterStr)) {
                    return;
                }

                if (!reviseEditTextInput(et_amount, 2)) {
                    return;
                }
            }
        });

        //初始化金额选择
        cml_money.setMoneyData(new int[]{100, 500, 1000, 3000, 5000,10000});
        cml_money.setDefaultPositon(0);
        cml_money.setOnChoseMoneyListener(new ChooseMoneyLayout.onChoseMoneyListener() {
            @Override
            public void chooseMoney(int position, boolean isCheck, int moneyNum) {
                //选择金额回调
                if(isCheck){
                    rechargeMoney = moneyNum;
                }else{
                    rechargeMoney = 0;
                }
            }
        });

        //充值说明
        getWithdrawExplain();

    }


    @OnClick({R.id.tv_recharge})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_recharge:
                if (!XClickUtil.isFastDoubleClick(view, 1000)) {
                    doRechargeClick();
                }
                break;

        }
    }

    private void doRechargeClick() {
        maxCount = 5;
        rechargeMoney = Integer.parseInt(et_amount.getText().toString().trim());
        if (rechargeMoney <= 0) {
            ToastUtil.toast("请选择充值金额");
            return;
        }
       /* if (TextUtils.isEmpty(rechargeMoney)) {
            toast("请输入充值金额");
            return;
        }*/

        Map<String, Object> map = new HashMap<>();
        map.put("rechargeMoney", Double.parseDouble(String.valueOf(rechargeMoney)));
        map.put("cardId", "xxx");
        //map.put("payPassword", psw);
        map.put("payType", 1);
        ApiClient.requestNetHandle(this, AppConfig.rechargeUrl, "充值中...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
//                startActivity(new Intent(RechargeActivity.this, WebViewActivity.class).putExtra("url", json).putExtra("title", "充值"));
                Log.d("####", json.toString());
                if (json != null && json.length() > 0) {
                    WalletRechargeBean walletRechargeBean = FastJsonUtil.getObject(json, WalletRechargeBean.class);

                    WalletPay walletPay = WalletPay.Companion.getInstance();
                    walletPay.init(RechargeActivity.this);
                    walletPay.walletPayCallback = new WalletPay.WalletPayCallback() {
                        @Override
                        public void callback(@Nullable String source, @Nullable String status, @Nullable String errorMessage) {
                            if (status == "SUCCESS" || status == "PROCESS") {
                                //queryResult(walletRechargeBean.requestId);
                                ToastUtil.toast("充值成功");
                                finish();
                            } else {//后加的
                                ToastUtil.toast("充值失败");
                            }
                        }
                    };
                    //支付SDK
                    /*ArrayList<String> list = new ArrayList<>();
                    list.add(AuthType.APP_PAY.name());
                    walletPay.setOnlySupportBalance(true,list);*/
                    //商户编号  钱包id  后台返回的支付token和requestId
                    walletPay.evoke(Constant.MERCHANT_ID, UserComm.getUserInfo().ncountUserId,
                            walletRechargeBean.token, AuthType.APP_PAY.name());


                } else {
                    ToastUtil.toast("服务器开小差，请稍后重试");
                }
            }


            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });

    }

    private void doNewRechargeClick() {
        maxCount = 5;
        String inputAmount = et_amount.getText().toString().trim();
        if (TextUtils.isEmpty(inputAmount)) {
            ToastUtil.toast("请输入充值金额");
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("rechargeMoney", Double.parseDouble(inputAmount));
        ApiClient.requestNetHandle(this, AppConfig.walletRecharge, "请稍等...",
                map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        if (json != null && json.length() > 0) {
                            WalletRechargeBean walletRechargeBean = FastJsonUtil.getObject(json, WalletRechargeBean.class);

                            WalletPay walletPay = WalletPay.Companion.getInstance();
                            walletPay.init(RechargeActivity.this);
                            walletPay.walletPayCallback = new WalletPay.WalletPayCallback() {
                                @Override
                                public void callback(@Nullable String source, @Nullable String status, @Nullable String errorMessage) {
                                    if (status == "SUCCESS" || status == "PROCESS") {
                                        queryResult(walletRechargeBean.requestId);
                                    }
                                }
                            };
                            //调起SDK的充值
                            walletPay.evoke(Constant.MERCHANT_ID, UserComm.getUserInfo().ncountUserId,
                                    walletRechargeBean.token, AuthType.RECHARGE.name());

                        } else {
                            ToastUtil.toast("服务器开小差，请稍后重试");
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });
    }

    private void queryResult(String requestId) {
        Map<String, Object> map = new HashMap<>();
        map.put("requestId", requestId);
        ApiClient.requestNetHandleByGet(this, AppConfig.walletRechargeQuery, "请稍等...",
                map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        if (json != null && json.length() > 0) {
//                            WalletRechargeQueryBean walletRechargeQueryBean = FastJsonUtil.getObject(json, WalletRechargeQueryBean.class);
                            WalletRechargeBean walletRechargeQueryBean = FastJsonUtil.getObject(json, WalletRechargeBean.class);
                            switch (walletRechargeQueryBean.orderStatus) {
                                case "SUCCESS":
                                    // TODO: 2021/3/22 关闭当前页面，并刷新钱包余额
                                    ToastUtil.toast("充值成功");
                                    finish();
                                    break;
                                case "PROCESS":
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            maxCount--;
                                            if (maxCount <= 0) {
                                                ToastUtil.toast("充值处理中");
                                                return;
                                            }
                                            queryResult(requestId);
                                        }
                                    }, 2000);
                                    break;
                                default:
                                    ToastUtil.toast("充值失败");
                                    break;
                            }

                        } else {
                            ToastUtil.toast("服务器开小差，请稍后重试");
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });
    }



    private void getWithdrawExplain() {
        Map<String, Object> map1 = new HashMap<>();
        ApiClient.requestNetHandle(this, AppConfig.getWithdrawExplain, "", map1, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    //{"withdrawExplain":"银行卡\r\n费率:0.6%+1元/笔银行付款费\r\n单日最多提现3次单笔最高10000元,单日最高30000元。每个账户只能同时进行一笔提现\r\n个别订单有可能被银行风控系统拦截,会延迟到账,我们会与相关机构沟通,在1-2个工作日内处理\r\n如您使用的银行卡多次出现打款失败,通常为卡片兼容问题,请更换银行卡后再试。\r\n注意:部分银行小额打款时不会发送短信通知,到账情况请以银行流水为准。"}
                    tv_chongzhi.setText(FastJsonUtil.getString(json, "rechargeExplain"));
                }
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }


    /**
     * 修正输入框输入内容
     *
     * @param editText
     * @param bitCount 小数点位数
     * @return 返回false表示不能继续输入
     */
    private boolean reviseEditTextInput(EditText editText, int bitCount) {
        boolean canContinueInput = true;

        String text = editText.getText().toString().trim();
        //删除“.”后面超过2位后的数据
        if (text.contains(".")) {
            if (text.length() - 1 - text.indexOf(".") > bitCount) {
                text = text.substring(0, text.indexOf(".") + bitCount + 1);
                editText.setText(text);
                editText.setSelection(text.length()); //光标移到最后
            }
        }
        //如果"."在起始位置,则起始位置自动补0，同时return false拦截后续的操作，
        // 否则Double.parseDouble(afterStr)会报数字格式异常
        if (text.substring(0, 1).equals(".")) {
            text = "0" + text;
            editText.setText(text);
            editText.setSelection(2);
            return false;//
        }
        //如果起始位置为0,且第二位跟的不是".",则无法后续输入
        if (text.startsWith("0") && text.length() > 1) {
            if (!text.substring(1, 2).equals(".")) {
                editText.setText(text.substring(0, 1));
                editText.setSelection(1);
                return false;
            }
        }

        return canContinueInput;
    }


}
