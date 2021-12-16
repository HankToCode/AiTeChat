package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
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
import com.ycf.qianzhihe.app.weight.PasswordEditText;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.ycf.qianzhihe.app.weight.passwoed_keyboard.OnNumberKeyboardListener;
import com.ycf.qianzhihe.app.weight.passwoed_keyboard.XNumberKeyboardView;
import com.zds.base.json.FastJsonUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.util.NumberUtils;

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

    @BindView(R.id.tv_bank)
    TextView tv_bank;


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

        mWithdrawMoney.addTextChangedListener(new TextWatcher() {
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
                if (!reviseEditTextInput(mWithdrawMoney, 2)) {
                    return;
                }

            }
        });
        initUI();
        CommonApi.upUserInfo(DemoApplication.getInstance().getApplicationContext());
    }



    private LoginInfo loginInfo;
    private void initUI() {
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandle(this, AppConfig.USER_INFO, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                     loginInfo = JSON.parseObject(json, LoginInfo.class);
//                    mTvHand_rate.setText("提现金额（当前手续费率为" + loginInfo.getHandRate() + ")");
                    mTvHand_rate.setText("提现金额");
                    mTvMoneyHint.setText("可用余额：" + loginInfo.getMoney() + "元");
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
//                    mTvMoneyHintMin.setText("，最低" + FastJsonUtil.getString(json, "minWithdrawAmount") + "元起提");
                }
            }

            @Override
            public void onFailure(String msg) {
            }
        });

    }


    private String bankId = "";

    @OnClick({R.id.tv_new_bank_card_submit, R.id.ll_select_bank, R.id.tv_my_money_hint_min})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_my_money_hint_min:
                mWithdrawMoney.setText(loginInfo.getMoney() + "");
                break;
            case R.id.ll_select_bank://选择提现银行卡
                startActivityForResult(new Intent(this, BankActivity.class), 66);
                break;
            case R.id.tv_new_bank_card_submit://提现
                if (TextUtils.isEmpty(bankId)) {
                    showToast("请选择提现银行卡");
                    return;
                }
                double moey = NumberUtils.parseDouble(mWithdrawMoney.getText().toString());
                if (moey <= 0) {
                    ToastUtil.toast("请输入金额");
                    return;
                }
                if (moey < 50) {
                    ToastUtil.toast("提现金额单次最低50元");
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
            double moey = NumberUtils.parseDouble(mWithdrawMoney.getText().toString());
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
//                ToastUtil.toast("已提交审核");
//                startActivity(new Intent(WithdrawActivity.this,WebViewActivity.class).putExtra("url",json).putExtra("title","提现"));
                WithdrawResultActivity.actionStart(mContext);
                finish();
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
//            tv_bank.setText(data.getStringExtra("bankCard"));
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
                withdraw(id, password);
                builder.dismiss();
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
