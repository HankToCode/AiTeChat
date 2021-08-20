package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.ycf.qianzhihe.DemoApplication;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.CommonApi;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.weight.CommonDialog;
import com.ycf.qianzhihe.app.weight.CustomerKeyboard;
import com.ycf.qianzhihe.app.weight.PasswordEditText;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.zds.base.json.FastJsonUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import com.zds.base.Toast.ToastUtil;

/**
 * 提现
 *
 * @author Administrator
 */

public class WithdrawActivity extends BaseInitActivity {

    @BindView(R.id.tv_new_bank_card_submit)
    TextView mNewBankCard;
    @BindView(R.id.et_withdraw_money)
    EditText mWithdrawMoney;
    @BindView(R.id.tv_hand_rate)
    TextView mTvHand_rate;
    @BindView(R.id.tv_my_money_hint)
    TextView mTvMoneyHint;
    @BindView(R.id.tv_my_money_hint_min)
    TextView mTvMoneyHintMin;
    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;

    @BindView(R.id.tv_tixian)
    TextView tv_tixian;

    @BindView(R.id.rl_black)
    RelativeLayout rl_black;
    @BindView(R.id.tv_bank)
    TextView tv_bank;
    @BindView(R.id.tv_bank_card)
    TextView tv_bank_card;

    private boolean isSelectBalance = false;


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, WithdrawActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_withdraw;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        mTitleBar.setTitle("提现");
        mTitleBar.setOnBackPressListener(view -> finish());
        mTitleBar.setRightTitle("提现记录");
        mTitleBar.setOnRightClickListener(view -> TxRecordActivity.actionStart(mContext));


        initUI();
        CommonApi.upUserInfo(DemoApplication.getInstance().getApplicationContext());
    }



    private void initUI() {
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandle(this, AppConfig.USER_INFO, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    LoginInfo loginInfo = JSON.parseObject(json, LoginInfo.class);
//                    mTvHand_rate.setText("提现金额（当前手续费率为" + loginInfo.getHandRate() + ")");
                    mTvHand_rate.setText("提现金额");
                    mTvMoneyHint.setText("我的余额：" + loginInfo.getMoney() + "元");
                }
            }

            @Override
            public void onFailure(String msg) {
            }
        });

        Map<String, Object> map1 = new HashMap<>();
        ApiClient.requestNetHandle(this, AppConfig.getWithdrawExplain, "", map1, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    //{"withdrawExplain":"银行卡\r\n费率:0.6%+1元/笔银行付款费\r\n单日最多提现3次单笔最高10000元,单日最高30000元。每个账户只能同时进行一笔提现\r\n个别订单有可能被银行风控系统拦截,会延迟到账,我们会与相关机构沟通,在1-2个工作日内处理\r\n如您使用的银行卡多次出现打款失败,通常为卡片兼容问题,请更换银行卡后再试。\r\n注意:部分银行小额打款时不会发送短信通知,到账情况请以银行流水为准。"}
                    tv_tixian.setText(FastJsonUtil.getString(json, "withdrawExplain"));
                    mTvMoneyHintMin.setText("，最低" + FastJsonUtil.getInt(json, "minWithdrawAmount") + "元起提");
                }
            }

            @Override
            public void onFailure(String msg) {
            }
        });

    }


    private String bankId = "";

    @OnClick({R.id.tv_new_bank_card_submit, R.id.rl_black})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.rl_black://选择提现银行卡
                startActivityForResult(new Intent(this, BankActivity.class), 66);
                break;
            case R.id.tv_new_bank_card_submit://提现
                if (TextUtils.isEmpty(bankId)) {
                    showToast("请选择提现银行卡");
                    return;
                }
                PayPassword(bankId);
                break;
            default:
                break;
        }
    }

    private boolean isRequest;

    /**
     * 提现
     */
    private void withdraw(String id, String psw) {
        try {
            double moey = Double.valueOf(mWithdrawMoney.getText().toString());
            if (moey <= 0) {
                ToastUtil.toast("请输入金额");
                return;
            }

            if (moey > 10000) {
                ToastUtil.toast("提现金额单次最高不得超过10000元");
                return;
            }

        } catch (Exception e) {
            e.getStackTrace();
            ToastUtil.toast("请输入正确金额");
            return;
        }
        if (isRequest) {
            ToastUtil.toast("加载中，请勿重复提交");
            return;
        }
        isRequest = true;
        Map<String, Object> map = new HashMap<>();
        map.put("withdrawMoney", mWithdrawMoney.getText().toString());
        map.put("cardId", id);
        map.put("payPassword", psw);
        ApiClient.requestNetHandle(this, AppConfig.withdrawUrl, "正在提现...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast("已提交审核");
//                startActivity(new Intent(WithdrawActivity.this,WebViewActivity.class).putExtra("url",json).putExtra("title","提现"));
            }

            @Override
            public void onFinsh() {
                super.onFinsh();
                isRequest = false;
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    @Override
    protected void onEventComing(EventCenter center) {

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 66 && resultCode == 1111) {
             bankId = data.getStringExtra("id");
            tv_bank.setText(data.getStringExtra("bankName"));
            tv_bank_card.setText(data.getStringExtra("bankCard"));
        }
    }

    /**
     * 支付密码
     */
    private void PayPassword(String id) {

        LoginInfo userInfo = UserComm.getUserInfo();
        if (userInfo.getPayPwdFlag() == 0) {
            startActivity(new Intent(WithdrawActivity.this,
                    InputPasswordActivity.class));
            return;
        }
        final CommonDialog.Builder builder =
                new CommonDialog.Builder(this).fullWidth().fromBottom()
                        .setView(R.layout.dialog_customer_keyboard);

        builder.setOnClickListener(R.id.delete_dialog,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });
        builder.create().show();

        LinearLayout mLlaySelectMode = builder.getView(R.id.llay_select_mode);
        mLlaySelectMode.setVisibility(View.GONE);

        RelativeLayout mLlayBalanceSelect =
                builder.getView(R.id.llay_balance_select);
        ImageView mImgBalanceSelect = builder.getView(R.id.img_balance_select);

        RelativeLayout mLlayBankCarSelect =
                builder.getView(R.id.llay_bank_car_select);
        ImageView mImgBankCarSelect = builder.getView(R.id.img_bank_car_select);

        mLlayBalanceSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectBalance = true;
                mImgBalanceSelect.setVisibility(View.VISIBLE);
                mImgBankCarSelect.setVisibility(View.GONE);
            }
        });

        mLlayBankCarSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectBalance = false;
                mImgBalanceSelect.setVisibility(View.GONE);
                mImgBankCarSelect.setVisibility(View.VISIBLE);
            }
        });


        final CustomerKeyboard mCustomerKeyboard =
                builder.getView(R.id.custom_key_board);
        final PasswordEditText mPasswordEditText =
                builder.getView(R.id.password_edit_text);
        mCustomerKeyboard.setOnCustomerKeyboardClickListener(new CustomerKeyboard.CustomerKeyboardClickListener() {
            @Override
            public void click(String number) {
                if ("返回".equals(number)) {
                    builder.dismiss();
                } else if ("忘记密码？".equals(number)) {
//                    if (MyApplication.getInstance().getUserInfo().getIsBind() == 2) {
//                        toast("请先绑定手机号");
//                    } else {
                    startActivity(new Intent(WithdrawActivity.this, VerifyingPayPasswordPhoneNumberActivity.class));
//                    }
                } else {
                    mPasswordEditText.addPassword(number);
                }
            }

            @Override
            public void delete() {
                mPasswordEditText.deleteLastPassword();
            }
        });

        mPasswordEditText.setOnPasswordFullListener(new PasswordEditText.PasswordFullListener() {
            @Override
            public void passwordFull(String password) {
                withdraw(id, password);
                builder.dismiss();
            }
        });

    }

}