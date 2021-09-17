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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ycf.qianzhihe.DemoApplication;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_data.WalletRechargeBean;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.CommonApi;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.utils.XClickUtil;
import com.ycf.qianzhihe.app.weight.ChooseMoneyLayout;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.ycf.qianzhihe.app.weight.CommonDialog;
import com.ycf.qianzhihe.app.weight.ConfirmInputDialog;
import com.ycf.qianzhihe.app.weight.PasswordEditText;
import com.ycf.qianzhihe.app.weight.passwoed_keyboard.OnNumberKeyboardListener;
import com.ycf.qianzhihe.app.weight.passwoed_keyboard.XNumberKeyboardView;
import com.ycf.qianzhihe.common.utils.ToastUtils;
import com.ycf.qianzhihe.section.account.activity.BuyMemberActivity;
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
    @BindView(R.id.ll_select_bank)
    LinearLayout ll_select_bank;
    @BindView(R.id.tv_bank)
    TextView tv_bank;
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
                cml_money.setDefaultPositon(-1);
                if (!reviseEditTextInput(et_amount, 2)) {
                    return;
                }

            }
        });

        //初始化金额选择
        cml_money.setMoneyData(new int[]{30, 50, 100, 200, 500, 3000});
        cml_money.setDefaultPositon(-1);
        cml_money.setOnChoseMoneyListener(new ChooseMoneyLayout.onChoseMoneyListener() {
            @Override
            public void chooseMoney(int position, boolean isCheck, int moneyNum) {
                //选择金额回调
                if (isCheck) {
                    et_amount.setText("");
                    rechargeMoney = moneyNum;
                } else {
                    rechargeMoney = 0;
                }
            }
        });

        //充值说明
        getWithdrawExplain();

    }


    @OnClick({R.id.tv_recharge, R.id.ll_select_bank})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_recharge:

                if (!XClickUtil.isFastDoubleClick(view, 1000)) {
                    if (!TextUtils.isEmpty(et_amount.getText().toString().trim())) {
                        rechargeMoney = Integer.parseInt(et_amount.getText().toString().trim());
                    }
                    if (rechargeMoney <= 0) {
                        ToastUtils.showToast("请选择充值金额");
                        return;
                    }
                    if (TextUtils.isEmpty(bankId)) {
                        ToastUtils.showToast("请选择银行卡");
                        return;
                    }
                    payPassword();

                } else {
                    ToastUtils.showToast("请勿连续提交");
                }
                break;
            case R.id.ll_select_bank:
                startActivityForResult(new Intent(this, BankActivity.class), 66);
                break;

        }
    }

    private String bankId = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 66 && resultCode == 1111) {
            bankId = data.getStringExtra("id");
            tv_bank.setText(data.getStringExtra("bankName"));
//            tv_bank_card.setText(data.getStringExtra("bankCard"));
        }
    }


    private void payPassword() {
        LoginInfo userInfo = UserComm.getUserInfo();
        if (userInfo.getPayPwdFlag() == 0) {
            startActivity(new Intent(mContext, InputPasswordActivity.class));
            return;
        }
        final CommonDialog.Builder builder = new CommonDialog.Builder(this).fullWidth().fromBottom()
                .setView(R.layout.dialog_customer_keyboard);
        builder.setOnClickListener(R.id.delete_dialog,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });
        builder.create().show();

        builder.getView(R.id.tv_forget).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetPayPwdActivity.actionStart(mContext);
            }
        });

        final XNumberKeyboardView mCustomerKeyboard = builder.getView(R.id.kb_board);
        final PasswordEditText mPasswordEditText = builder.getView(R.id.password_edit_text);
        mCustomerKeyboard.setOnNumberKeyboardListener(new OnNumberKeyboardListener() {
            @Override
            public void onNumberKey(int keyCode, String insert) {
                // 右下角按键的点击事件，删除一位输入的文字
                if (keyCode == XNumberKeyboardView.KEYCODE_BOTTOM_RIGHT) {
                    mPasswordEditText.deleteLastPassword();
                }
                // 左下角按键和数字按键的点击事件，输入文字
                else {
                    mPasswordEditText.addPassword(insert);
                }

            }
        });

        mPasswordEditText.setOnPasswordFullListener(new PasswordEditText.PasswordFullListener() {
            @Override
            public void passwordFull(String password) {
                doRechargeClick(password);
                builder.dismiss();
            }
        });

    }

    private void doRechargeClick(String password) {
        Map<String, Object> map = new HashMap<>();
        map.put("rechargeMoney", Double.parseDouble(String.valueOf(rechargeMoney)));
        map.put("cardId", bankId);
        map.put("payPassword", password);
        map.put("payType", 1);
        ApiClient.requestNetHandle(this, AppConfig.rechargeUrl, "充值中...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
//                startActivity(new Intent(RechargeActivity.this, WebViewActivity.class).putExtra("url", json).putExtra("title", "充值"));
                if (json != null && json.length() > 0) {
                    WalletRechargeBean walletRechargeBean = FastJsonUtil.getObject(json, WalletRechargeBean.class);

                    ConfirmInputDialog dialog = new ConfirmInputDialog(mContext);
                    dialog.setOnConfirmClickListener(new ConfirmInputDialog.OnConfirmClickListener() {
                        @Override
                        public void onConfirmClick(String content) {
                            doNewRechargeClick(walletRechargeBean.getOrderId(), content);
                        }
                    });
                    dialog.show();
                    dialog.setCancelable(false);
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.setTitle("输入验证码");
                    dialog.setContentHint("输入验证码");
                } else {
                    ToastUtils.showToast("服务器开小差，请稍后重试");
                }
            }


            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });

    }

    private void doNewRechargeClick(String orderId, String code) {
        maxCount = 5;
        Map<String, Object> map = new HashMap<>();
        map.put("orderId", orderId);
        map.put("verifyCode", code);
        ApiClient.requestNetHandle(this, AppConfig.rechargeSure, "充值中...",
                map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        if (json != null && json.length() > 0) {
                            WalletRechargeBean walletRechargeBean = FastJsonUtil.getObject(json, WalletRechargeBean.class);
                            waiting();
                            /*WalletPay walletPay = WalletPay.Companion.getInstance();
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
                                    walletRechargeBean.token, AuthType.RECHARGE.name());*/

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
    Handler waitHandler = new Handler();
    private void waiting() {
        showLoading("充值确认中");
        waitHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                //要执行的操作
                ToastUtils.showToast("充值成功");
                CommonApi.upUserInfo(DemoApplication.getInstance().getApplicationContext());
                dismissLoading();
                finish();
            }

        }, 3000);//3秒后执行Runnable中的run方法
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
