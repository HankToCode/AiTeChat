package com.hyphenate.easeim.section.account.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSON;
import com.coorchice.library.SuperTextView;
import com.hyphenate.EMError;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.MainActivity;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.db.DemoDbHelper;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.common.utils.DeviceIdUtil;
import com.hyphenate.easeim.common.utils.NetworkUtil;
import com.hyphenate.easeim.common.utils.PreferenceManager;
import com.hyphenate.easeim.common.utils.SystemUtil;
import com.hyphenate.easeim.common.utils.ToastUtil;
import com.hyphenate.easeim.common.utils.ToastUtils;
import com.hyphenate.easeim.app.api.Constant;
import com.hyphenate.easeim.app.api.Global;
import com.hyphenate.easeim.app.api.bean.LoginInfo;
import com.hyphenate.easeim.app.api.global.SP;
import com.hyphenate.easeim.app.api.global.UserComm;
import com.hyphenate.easeim.app.api.http_old.ApiClient;
import com.hyphenate.easeim.app.api.http_old.AppUrls;
import com.hyphenate.easeim.app.api.http_old.ResultListener;
import com.hyphenate.easeim.app.base.BaseInitFragment;
import com.hyphenate.easeim.section.account.activity.AccountManagerActivity;
import com.hyphenate.easeim.section.account.activity.RegisterActivity;
import com.hyphenate.easeim.section.account.activity.UpDataPasswordActivity;
import com.hyphenate.easeim.section.account.viewmodels.LoginFragmentViewModel;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.utils.EaseEditTextUtils;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends BaseInitFragment implements View.OnClickListener, TextWatcher, TextView.OnEditorActionListener {
    private EditText mEtLoginName;
    private EditText mEtLoginPwd;
    private TextView mTvLoginRegister;
    private Button mBtnLogin;
    private String mUserName;
    private String mPwd;
    private String mSms;
    private LoginFragmentViewModel mFragmentViewModel;
    private Drawable clear;
    private Drawable eyeOpen;
    private Drawable eyeClose;


    private TextView mTvTitle;
    private TextView mTvLoginName;
    private TextView mTvPwd;
    private TextView mTvForgetPassword;
    private TextView mTvSwitchLogin;
    private TextView mTvOtherLogin;
    private ImageView mIvChatLogin;
    private TextView mTvFrozen;
    private TextView mTvUnbind;

    private ConstraintLayout mLlPwd;
    private ConstraintLayout mLlSms;
    private TextView mTvSms;
    private EditText mEtLoginSms;
    private SuperTextView mTvSmsSend;

    private CountDownTimer loginTimer;

    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_login;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mEtLoginName = findViewById(R.id.et_login_name);
        mEtLoginPwd = findViewById(R.id.et_login_pwd);
        mTvLoginRegister = findViewById(R.id.tv_login_register);
        mBtnLogin = findViewById(R.id.btn_login);
        mTvTitle = (TextView) findViewById(R.id.tv_title);
        mTvLoginName = (TextView) findViewById(R.id.tv_login_name);
        mTvPwd = (TextView) findViewById(R.id.tv_pwd);
        mTvForgetPassword = (TextView) findViewById(R.id.tv_forget_password);
        mTvSwitchLogin = (TextView) findViewById(R.id.tv_switch_login);
        mTvOtherLogin = (TextView) findViewById(R.id.tv_other_login);
        mIvChatLogin = (ImageView) findViewById(R.id.iv_chat_login);
        mTvFrozen = (TextView) findViewById(R.id.tv_frozen);
        mTvUnbind = (TextView) findViewById(R.id.tv_unfrozen);
        mLlPwd = (ConstraintLayout) findViewById(R.id.ll_pwd);
        mLlSms = (ConstraintLayout) findViewById(R.id.ll_sms);
        mTvSms = (TextView) findViewById(R.id.tv_sms);
        mEtLoginSms = (EditText) findViewById(R.id.et_login_sms);
        mTvSmsSend = (SuperTextView) findViewById(R.id.tv_sms_send);

        // 保证切换fragment后相关状态正确
        boolean enableTokenLogin = DemoHelper.getInstance().getModel().isEnableTokenLogin();
        if (!TextUtils.isEmpty(DemoHelper.getInstance().getCurrentLoginUser())) {
            mEtLoginName.setText(DemoHelper.getInstance().getCurrentLoginUser());
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        mEtLoginName.addTextChangedListener(this);
        mEtLoginPwd.addTextChangedListener(this);
        mTvLoginRegister.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        mEtLoginPwd.setOnEditorActionListener(this);

        mTvForgetPassword.setOnClickListener(this);
        mTvSwitchLogin.setOnClickListener(this);
        mIvChatLogin.setOnClickListener(this);
        mTvFrozen.setOnClickListener(this);
        mTvUnbind.setOnClickListener(this);
        mTvSmsSend.setOnClickListener(this);


        EaseEditTextUtils.clearEditTextListener(mEtLoginName);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        mFragmentViewModel = new ViewModelProvider(this).get(LoginFragmentViewModel.class);
        mFragmentViewModel.getLoginObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EaseUser>(true) {
                @Override
                public void onSuccess(EaseUser data) {
                    dismissLoading();
                    DemoHelper.getInstance().setAutoLogin(true);
                    //跳转到主页
                    MainActivity.startAction(mContext);
                    mContext.finish();
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    dismissLoading();
                    if (code == EMError.USER_AUTHENTICATION_FAILED) {
                        ToastUtils.showToast(R.string.demo_error_user_authentication_failed);
                    } else {
                        ToastUtils.showToast(message);
                    }
                }

                @Override
                public void onLoading(EaseUser data) {
                    super.onLoading(data);
                    showLoading();
                }

                @Override
                public void hideLoading() {
                    super.hideLoading();
                    LoginFragment.this.dismissLoading();
                }
            });

        });
    }

    @Override
    protected void initViewModel() {
        super.initViewModel();
        DemoDbHelper.getInstance(mContext).getDatabaseCreatedObservable().observe(getViewLifecycleOwner(), response -> {
            mFragmentViewModel.HXlogin();
        });
    }

    @Override
    protected void initData() {
        super.initData();
        //切换密码可见不可见的两张图片
        eyeClose = ContextCompat.getDrawable(requireContext(), R.drawable.d_pwd_hide);
        eyeOpen = ContextCompat.getDrawable(requireContext(), R.drawable.d_pwd_show);
        clear = ContextCompat.getDrawable(requireContext(), R.drawable.d_clear);
        EaseEditTextUtils.showRightDrawable(mEtLoginName, clear);
        EaseEditTextUtils.changePwdDrawableRight(mEtLoginPwd, eyeClose, eyeOpen, null, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login_register:
                RegisterActivity.actionStart(requireContext());
                break;
            case R.id.btn_login:
                hideKeyboard();
                loginToServer();
                break;
            case R.id.tv_forget_password:
                hideKeyboard();
                UpDataPasswordActivity.actionStart(requireContext());
                break;
            case R.id.tv_switch_login:
                hideKeyboard();
                switchLoginMethod();
                break;
            case R.id.iv_chat_login:
                hideKeyboard();
                break;
            case R.id.tv_frozen:
                hideKeyboard();
                AccountManagerActivity.actionStart(requireContext(), Constant.ACCOUNT_FREEZE);
                break;
            case R.id.tv_unfrozen:
                hideKeyboard();
                AccountManagerActivity.actionStart(requireContext(), Constant.ACCOUNT_THAW);
                break;
            case R.id.tv_sms_send:
                initLoginCountTimer();
                getLoginSMSCode();
                break;
            default:
                break;
        }
    }

    private int loginMethod = 0;//0密码 ,1短信

    private void switchLoginMethod() {
        if (loginMethod == 0) {
            loginMethod = 1;
        } else {
            loginMethod = 0;
        }

        mLlPwd.setVisibility(loginMethod == 0 ? View.VISIBLE : View.GONE);
        mLlSms.setVisibility(loginMethod == 1 ? View.VISIBLE : View.GONE);
    }


    private void loginToServer() {

        if (loginMethod == 0) {
            passwordLogin();
        } else {
            smsCodeLogin();
        }
    }

    /**
     * =========================================用户密码登录=================================================
     */
    public void passwordLogin() {
        if (TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(mPwd)) {
            ToastUtils.showToast(R.string.em_login_btn_info_incomplete);
            return;
        }
        showLoading("正在登录");
        DemoDbHelper.getInstance(mContext).closeDb();
        Map<String, Object> map = new HashMap<>();
        map.put("phone", mUserName);
        map.put("password", mPwd);
        map.put("deviceId", DeviceIdUtil.getDeviceId(requireContext()));
        map.put("os", "Android");
        map.put("version", Global.loginVersion);
        map.put("deviceName", SystemUtil.getDeviceManufacturer() + " " + SystemUtil.getSystemModel());
        ApiClient.requestNetHandle(mContext, AppUrls.multiLogin, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    LoginInfo loginInfo = JSON.parseObject(json, LoginInfo.class);
                    loginInfo.setPassword(mPwd);
                    PreferenceManager.getInstance().setParam(SP.SP_LANDED_ON_LOGIN, mUserName);

                    if (loginInfo != null) {
                        UserComm.saveUsersInfo(loginInfo);
                        DemoDbHelper.getInstance(mContext).initDb(mUserName);

                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                dismissLoading();
                ToastUtil.toast(msg);
            }
        });
    }

    /**
     * * =========================================用户验证码登录=================================================
     */
    public void smsCodeLogin() {
        if (!NetworkUtil.isNetworkAvailable(requireContext())) {
            Toast.makeText(requireContext(), R.string.em_error_network_error, Toast.LENGTH_SHORT).show();
            return;
        }

        if (TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(mPwd)) {
            ToastUtils.showToast(R.string.em_login_btn_sms_incomplete);
            return;
        }
        showLoading("正在登录");
        Map<String, Object> map = new HashMap<>();
        map.put("phone", mUserName);
        map.put("authCode", mSms);
        map.put("deviceId", DeviceIdUtil.getDeviceId(requireContext()));
        map.put("os", "Android");
        map.put("version", Global.loginVersion);
        map.put("deviceName", SystemUtil.getDeviceManufacturer() + " " + SystemUtil.getSystemModel());

        ApiClient.requestNetHandle(requireContext(), AppUrls.toSMSMultiLoginUrl, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    LoginInfo loginInfo = JSON.parseObject(json, LoginInfo.class);

                    PreferenceManager.getInstance().setParam(SP.SP_LANDED_ON_LOGIN, mUserName);

                    if (loginInfo != null) {
                        UserComm.saveUsersInfo(loginInfo);
                        DemoDbHelper.getInstance(mContext).initDb(mUserName);
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                dismissLoading();
                ToastUtil.toast(msg);
            }
        });
    }

    private void initLoginCountTimer() {
        if (loginTimer == null)
            loginTimer = new CountDownTimer(60 * 1000, 1000) {
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

    private void getLoginSMSCode() {
        if (TextUtils.isEmpty(mUserName)) {
            ToastUtil.toast("手机号不能为空");
            mEtLoginName.requestFocus();
            return;
        }

        Map<String, Object> map = new HashMap<>();
        map.put("phone", mUserName);

        ApiClient.requestNetHandle(requireContext(), AppUrls.getSMSCodeForLogin, "获取验证码", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast("发送成功");
                loginTimer.start();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }


    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        mUserName = mEtLoginName.getText().toString().trim();
        mPwd = mEtLoginPwd.getText().toString().trim();
        mSms = mEtLoginSms.getText().toString().trim();
        EaseEditTextUtils.showRightDrawable(mEtLoginName, clear);
        EaseEditTextUtils.showRightDrawable(mEtLoginPwd, clear);
        EaseEditTextUtils.showRightDrawable(mEtLoginSms, clear);
        if (loginMethod == 0) {
            setButtonEnable(!TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mPwd));
        } else {
            setButtonEnable(!TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mSms));
        }
    }

    private void setButtonEnable(boolean enable) {
        mBtnLogin.setEnabled(enable);
        if (mEtLoginPwd.hasFocus()) {
            mEtLoginPwd.setImeOptions(enable ? EditorInfo.IME_ACTION_DONE : EditorInfo.IME_ACTION_PREVIOUS);
        } else if (mEtLoginName.hasFocus()) {
            mEtLoginPwd.setImeOptions(enable ? EditorInfo.IME_ACTION_DONE : EditorInfo.IME_ACTION_NEXT);
        }

    }

    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            if (!TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mPwd)) {
                hideKeyboard();
                loginToServer();
                return true;
            }
        }
        return false;
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loginTimer != null)
            loginTimer.cancel();
    }
}
