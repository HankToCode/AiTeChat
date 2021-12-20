package com.android.nanguo.section.account.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.coorchice.library.SuperTextView;
import com.android.nanguo.R;
import com.android.nanguo.app.api.Constant;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitActivity;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.util.StringUtil;

import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.HashMap;
import java.util.Map;

public class AccountManagerActivity extends BaseInitActivity implements View.OnClickListener, TextWatcher {
    public String operaterStatus = Constant.ACCOUNT_FREEZE;

    public static void actionStart(Context context, String status) {
        Intent intent = new Intent(context, AccountManagerActivity.class);
        intent.putExtra(Constant.PARAM_STATUS, status);
        context.startActivity(intent);
    }

    private CountDownTimer timer;

    private EaseTitleBar mTitleBar;
    private TextView mTvTitle;
    private ConstraintLayout mLlPhone;
    private TextView mTvPhone;
    private EditText mEtPhone;
    private ConstraintLayout mLlUserSms;
    private TextView mTvUserSms;
    private EditText mEtUserSms;
    private SuperTextView mTvSmsSend;
    private Button mBtnSubmit;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_account_manager;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        operaterStatus = intent.getExtras().getString(Constant.PARAM_STATUS);

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mLlPhone = (ConstraintLayout) findViewById(R.id.ll_phone);
        mTvPhone = (TextView) findViewById(R.id.tv_phone);
        mEtPhone = (EditText) findViewById(R.id.et_phone);
        mLlUserSms = (ConstraintLayout) findViewById(R.id.ll_user_sms);
        mTvUserSms = (TextView) findViewById(R.id.tv_user_sms);
        mEtUserSms = (EditText) findViewById(R.id.et_user_sms);
        mTvSmsSend = (SuperTextView) findViewById(R.id.tv_sms_send);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);

    }

    @Override
    protected void initListener() {
        super.initListener();

        mTvSmsSend.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);

        mEtPhone.addTextChangedListener(this);
        mEtUserSms.addTextChangedListener(this);

        mTitleBar.setOnBackPressListener(view -> {
            finish();
        });
    }

    @Override
    protected void initData() {
        super.initData();


        mTvTitle.setText(Constant.ACCOUNT_FREEZE.equals(operaterStatus) ? "冻结账号" : "解冻账号");
        countDown();

    }


    /**
     * 获取验证码
     */
    private void getCode() {
        final String phone = mEtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.toast("手机号不能为空");
            mEtPhone.requestFocus();
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);

        String url = operaterStatus.equals(Constant.ACCOUNT_FREEZE) ? AppConfig.getAccountFrozenSMSCode : AppConfig.getAccountThawSMSCode;
        ApiClient.requestNetHandle(this, url, "获取验证码", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast("发送成功");
                timer.start();

            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    private void operateAccount() {

        final String phone = mEtPhone.getText().toString().trim();
        final String smsCode = mEtUserSms.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.toast("手机号不能为空");
            mEtPhone.requestFocus();
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("authCode", smsCode);

        String url = operaterStatus.equals(Constant.ACCOUNT_FREEZE) ? AppConfig.frozenAccount : AppConfig.thawAccount;
        ApiClient.requestNetHandle(this, url, "请稍等", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    ToastUtil.toast(msg);
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    private void countDown() {
        timer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTvSmsSend.setText(millisUntilFinished / 1000 + "s后重新获取");
                mTvSmsSend.setEnabled(false);
                //   tvCode.setBackgroundResource(R.drawable.shap_gray_5);
            }

            @Override
            public void onFinish() {
                mTvSmsSend.setText("获取验证码");
                //   tvCode.setBackgroundResource(R.drawable.border_redgray5);
                mTvSmsSend.setEnabled(true);
            }
        };
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }

    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        mBtnSubmit.setEnabled(!StringUtil.isEmpty(mEtPhone) && !StringUtil.isEmpty(mEtUserSms));
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_sms_send:
                getCode();
                break;
            case R.id.btn_submit:
                operateAccount();
                break;

            default:
                break;
        }
    }
}
