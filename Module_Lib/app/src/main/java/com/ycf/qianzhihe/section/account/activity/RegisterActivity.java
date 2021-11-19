package com.ycf.qianzhihe.section.account.activity;

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
import com.ycf.qianzhihe.common.utils.ToastUtils;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.util.StringUtil;

import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class RegisterActivity extends BaseInitActivity implements View.OnClickListener, TextWatcher {

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, RegisterActivity.class);
        context.startActivity(intent);
    }

    private EaseTitleBar mTitleBar;
    private ConstraintLayout mLlPhone;
    private TextView mTvPhone;
    private EditText mEtPhone;
    private TextView mTvUserCode;
    private EditText mEtUserCode;
    private ImageView mImgCode;
    private ConstraintLayout mLlUserSms;
    private TextView mTvUserSms;
    private EditText mEtUserSms;
    private SuperTextView mTvSmsSend;
    private ConstraintLayout mLlUserPassword;
    private TextView mTvUserPassword;
    private EditText mEtUserPassword;
    private ConstraintLayout mLlUserName;
    private TextView mTvUserName;
    private EditText mEtUserName;
    private Button mBtnSubmit;
    private TextView mTvRegisterAgreement;
    private TextView mTvSelfAgreement;
    private TextView tv_refresh;


    private CountDownTimer timer;
    private int flag;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_register;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        mTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        mLlPhone = (ConstraintLayout) findViewById(R.id.ll_phone);
        mTvPhone = (TextView) findViewById(R.id.tv_phone);
        mEtPhone = (EditText) findViewById(R.id.et_phone);
        mTvUserCode = (TextView) findViewById(R.id.tv_user_code);
        mEtUserCode = (EditText) findViewById(R.id.et_user_code);
        mImgCode = (ImageView) findViewById(R.id.img_code);
        tv_refresh = (TextView) findViewById(R.id.tv_refresh);
        mLlUserSms = (ConstraintLayout) findViewById(R.id.ll_user_sms);
        mTvUserSms = (TextView) findViewById(R.id.tv_user_sms);
        mEtUserSms = (EditText) findViewById(R.id.et_user_sms);
        mTvSmsSend = (SuperTextView) findViewById(R.id.tv_sms_send);
        mLlUserPassword = (ConstraintLayout) findViewById(R.id.ll_user_password);
        mTvUserPassword = (TextView) findViewById(R.id.tv_user_password);
        mEtUserPassword = (EditText) findViewById(R.id.et_user_password);
        mLlUserName = (ConstraintLayout) findViewById(R.id.ll_user_name);
        mTvUserName = (TextView) findViewById(R.id.tv_user_name);
        mEtUserName = (EditText) findViewById(R.id.et_user_name);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);
        mTvRegisterAgreement = (TextView) findViewById(R.id.tv_register_agreement);
        mTvSelfAgreement = (TextView) findViewById(R.id.tv_self_agreement);

    }

    @Override
    protected void initListener() {
        super.initListener();
        tv_refresh.setOnClickListener(this);
        mTvSmsSend.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);
        mTvRegisterAgreement.setOnClickListener(this);
        mTvSelfAgreement.setOnClickListener(this);


        mEtPhone.addTextChangedListener(this);
        mEtUserCode.addTextChangedListener(this);
        mEtUserSms.addTextChangedListener(this);
        mEtUserPassword.addTextChangedListener(this);
        mEtUserName.addTextChangedListener(this);

        mTitleBar.setOnBackPressListener(view -> {
            finish();
        });
    }

    @Override
    protected void initData() {
        super.initData();

        countDown();
        flushTy();
    }

    @Override
    protected void onEventComing(EventCenter center) {

    }


    /**
     * 刷新图形验证码
     */
    private void flushTy() {
        flag = new Random().nextInt(99999);
        if (flag < 10000) {
            flag += 10000;
        }
        GlideUtils.loadImageViewLoding(AppConfig.tuxingCode + "?random=" + flag, mImgCode);
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
        } else if (StringUtil.isEmpty(mEtUserCode)) {
            ToastUtil.toast("图形验证码不能为空");
            mEtUserCode.requestFocus();
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
//        map.put("type", "register");
        map.put("random", flag + "");
        map.put("imgCode", mEtUserCode.getText().toString());
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
        int id = view.getId();
        if (id == R.id.tv_register_agreement) {//注册协议
            WebViewActivity.actionStart(mContext, AppConfig.user_agree);
        } else if (id == R.id.tv_self_agreement) {//注册协议
            WebViewActivity.actionStart(mContext, AppConfig.register_agree);
        } else if (id == R.id.tv_refresh) {
            if (!XClickUtil.isFastDoubleClick(view, 1500)) {
                flushTy();
            } else {
                ToastUtils.showToast("请勿连续点击");
            }
        } else if (id == R.id.tv_sms_send) {
            getTXCode();
        } else if (id == R.id.btn_submit) {
            if (!XClickUtil.isFastDoubleClick(view, 1000)) {
                register();
            }
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
        mBtnSubmit.setEnabled(!StringUtil.isEmpty(mEtPhone) && !StringUtil.isEmpty(mEtUserCode)
                && !StringUtil.isEmpty(mEtUserSms) && !StringUtil.isEmpty(mEtUserPassword)
                && !StringUtil.isEmpty(mEtUserName));
    }
}
