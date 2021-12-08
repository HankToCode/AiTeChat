package com.ycf.qianzhihe.section.account.activity;

import android.app.Dialog;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.coorchice.library.SuperTextView;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.base.WebViewActivity;
import com.ycf.qianzhihe.app.utils.XClickUtil;
import com.ycf.qianzhihe.app.weight.GraphicVerificationDialog;
import com.ycf.qianzhihe.common.utils.ToastUtils;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.util.StringUtil;

import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RegisterActivity extends BaseInitActivity implements View.OnClickListener, TextWatcher {

    private ImageView mIvLogo;
    private LinearLayout mLlPoint1;
    private EditText mEtUserName;
    private ConstraintLayout mLlPhone;
    private TextView mTvPhone;
    private EditText mEtPhone;
    private ConstraintLayout mLlUserSms;
    private TextView mTvUserSms;
    private EditText mEtUserSms;
    private SuperTextView mTvSmsSend;
    private ConstraintLayout mLlInvitation;
    private TextView mTvInvitation;
    private EditText mEtInvitation;
    private TextView mTvLogin;
    private Button mBtnNext;
    private LinearLayout mLlPoint2;
    private ImageView mIvBack;
    private ConstraintLayout mLlUserPassword;
    private TextView mTvUserPassword;
    private EditText mEtUserPassword;
    private ConstraintLayout mLlUserPassword2;
    private TextView mTvUserPassword2;
    private EditText mEtUserPassword2;
    private Button mBtnSubmit;
    private TextView mTvRegisterAgreement;
    private TextView mTvSelfAgreement;


    private CountDownTimer timer;//获取验证码倒计时
    private int flag;//随机flag
    private String mImgCode = "";//图形验证码

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        mIvLogo = (ImageView) findViewById(R.id.ivLogo);
        mLlPoint1 = (LinearLayout) findViewById(R.id.llPoint1);
        mEtUserName = (EditText) findViewById(R.id.et_user_name);
        mLlPhone = (ConstraintLayout) findViewById(R.id.ll_phone);
        mTvPhone = (TextView) findViewById(R.id.tv_phone);
        mEtPhone = (EditText) findViewById(R.id.et_phone);
        mLlUserSms = (ConstraintLayout) findViewById(R.id.ll_user_sms);
        mTvUserSms = (TextView) findViewById(R.id.tv_user_sms);
        mEtUserSms = (EditText) findViewById(R.id.et_user_sms);
        mTvSmsSend = (SuperTextView) findViewById(R.id.tv_sms_send);
        mLlInvitation = (ConstraintLayout) findViewById(R.id.ll_invitation);
        mTvInvitation = (TextView) findViewById(R.id.tv_invitation);
        mEtInvitation = (EditText) findViewById(R.id.et_invitation);
        mTvLogin = (TextView) findViewById(R.id.tv_login);
        mBtnNext = (Button) findViewById(R.id.btn_next);
        mLlPoint2 = (LinearLayout) findViewById(R.id.llPoint2);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mLlUserPassword = (ConstraintLayout) findViewById(R.id.ll_user_password);
        mTvUserPassword = (TextView) findViewById(R.id.tv_user_password);
        mEtUserPassword = (EditText) findViewById(R.id.et_user_password);
        mLlUserPassword2 = (ConstraintLayout) findViewById(R.id.ll_user_password2);
        mTvUserPassword2 = (TextView) findViewById(R.id.tv_user_password2);
        mEtUserPassword2 = (EditText) findViewById(R.id.et_user_password2);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);
        mTvRegisterAgreement = (TextView) findViewById(R.id.tv_register_agreement);
        mTvSelfAgreement = (TextView) findViewById(R.id.tv_self_agreement);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mTvSmsSend.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
        mTvRegisterAgreement.setOnClickListener(this);
        mTvSelfAgreement.setOnClickListener(this);


        mEtPhone.addTextChangedListener(this);
        mEtUserSms.addTextChangedListener(this);
        mEtUserPassword.addTextChangedListener(this);
        mEtUserName.addTextChangedListener(this);

        mBtnNext.setOnClickListener(this);
        mIvBack.setOnClickListener(this);
        mTvLogin.setOnClickListener(this);

    }

    @Override
    protected void initData() {
        super.initData();

        countDown();
    }

    @Override
    protected void onEventComing(EventCenter center) {

    }


    private void countDown() {
        timer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTvSmsSend.setText(millisUntilFinished / 1000 + "s后重新获取");
                mTvSmsSend.setEnabled(false);
            }

            @Override
            public void onFinish() {
                mTvSmsSend.setText("获取验证码");
                mTvSmsSend.setEnabled(true);
            }
        };
    }

    /**
     * 注册
     */
    public void register() {
        final String phone = mEtPhone.getText().toString();
        final String pwd = mEtUserPassword.getText().toString();
        final String pwd2 = mEtUserPassword2.getText().toString();
        String sms = mEtUserSms.getText().toString();

        if (StringUtil.isEmpty(phone)) {
            ToastUtil.toast("手机号不能为空");
            mEtPhone.requestFocus();
            return;
        } else if (StringUtil.isEmpty(sms)) {
            ToastUtil.toast("验证码不能为空");
            mEtUserSms.requestFocus();
            return;
        } else if (StringUtil.isEmpty(pwd)) {
            ToastUtil.toast("密码不能为空");
            mEtUserPassword.requestFocus();
            return;
        } else if (pwd.length() > 16 || pwd.length() < 6) {
            ToastUtil.toast("请输入6-16位密码");
            mEtUserPassword.requestFocus();
            return;
        } else if (pwd2.length() > 16 || pwd2.length() < 6) {
            ToastUtil.toast("请输入6-16位密码");
            mEtUserPassword2.requestFocus();
            return;
        } else if (!pwd2.equals(pwd)) {
            ToastUtil.toast("请确认两个密码为一致");
            mEtUserPassword2.requestFocus();
            return;
        } else if (StringUtil.isEmpty(mEtUserName)) {
            ToastUtil.toast("昵称不能为空");
            mEtUserName.requestFocus();
            return;
        }

        final Map<String, Object> map = new HashMap<>();
        map.put("password", pwd);
        map.put("phone", phone);
        map.put("authCode", sms);
        map.put("nickName", mEtUserName.getText().toString().trim());

        ApiClient.requestNetHandle(this, AppConfig.toRegister, "正在注册...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast("注册成功");
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    /**
     * 获取图形验证码
     */
    private void getTXCode() {
        final String phone = mEtPhone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            ToastUtil.toast("手机号不能为空");
            mEtPhone.requestFocus();
            return;
        } else if (StringUtil.isEmpty(mImgCode)) {
            ToastUtil.toast("图形验证码不能为空");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
//        map.put("type", "register");
        map.put("random", flag + "");
        map.put("imgCode", mImgCode);
        ApiClient.requestNetHandle(this, AppConfig.getPhoneCodeUrl, "获取验证码...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    ToastUtil.toast(msg);
                    timer.start();
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    @Override
    protected void onDestroy() {
        timer.cancel();
        super.onDestroy();
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_register_agreement:
                //注册协议
                WebViewActivity.actionStart(mContext, AppConfig.user_agree);
                break;
            case R.id.tv_self_agreement:
                //注册协议
                WebViewActivity.actionStart(mContext, AppConfig.register_agree);
                break;
            case R.id.tv_sms_send:

                new GraphicVerificationDialog(mContext).setPositiveButton(dialog -> {
                    mImgCode = dialog.getCode();
                    getTXCode();
                }).setNegativeButton(dialog -> {
                }).show();

                break;
            case R.id.tv_login:
                LoginActivity.actionStart(mContext);
                finish();
                break;
            case R.id.btn_next:
                mLlPoint1.setVisibility(View.GONE);
                mLlPoint2.setVisibility(View.VISIBLE);
                break;
            case R.id.iv_back:
                mLlPoint1.setVisibility(View.VISIBLE);
                mLlPoint2.setVisibility(View.GONE);
                break;
            case R.id.btn_submit:
                if (!XClickUtil.isFastDoubleClick(view, 1000)) {
                    register();
                }
                break;
            default:
        }
    }


    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

    }

    @Override
    public void afterTextChanged(Editable editable) {
        mBtnSubmit.setEnabled(!StringUtil.isEmpty(mEtPhone)
                && !StringUtil.isEmpty(mEtUserSms)
                && !StringUtil.isEmpty(mEtUserPassword) && !StringUtil.isEmpty(mEtUserPassword2)
                && !StringUtil.isEmpty(mEtUserName));
    }
}
