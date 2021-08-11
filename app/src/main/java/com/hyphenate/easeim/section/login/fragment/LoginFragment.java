package com.hyphenate.easeim.section.login.fragment;

import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSON;
import com.hyphenate.EMError;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.MainActivity;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.common.db.DemoDbHelper;
import com.hyphenate.easeim.common.interfaceOrImplement.OnResourceParseCallback;
import com.hyphenate.easeim.common.utils.DeviceIdUtil;
import com.hyphenate.easeim.common.utils.PreferenceManager;
import com.hyphenate.easeim.common.utils.SystemUtil;
import com.hyphenate.easeim.common.utils.ToastUtil;
import com.hyphenate.easeim.common.utils.ToastUtils;
import com.hyphenate.easeim.section.api.Global;
import com.hyphenate.easeim.section.api.bean.LoginInfo;
import com.hyphenate.easeim.section.api.global.SP;
import com.hyphenate.easeim.section.api.global.UserComm;
import com.hyphenate.easeim.section.api.http.OldApiClient;
import com.hyphenate.easeim.section.api.http.OldAppUrls;
import com.hyphenate.easeim.section.api.http.ResultListener;
import com.hyphenate.easeim.section.base.BaseInitFragment;
import com.hyphenate.easeim.section.login.viewmodels.LoginFragmentViewModel;
import com.hyphenate.easeim.section.login.viewmodels.LoginViewModel;
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
    private LoginViewModel mViewModel;
    private boolean isTokenFlag;//是否是token登录
    private LoginFragmentViewModel mFragmentViewModel;
    private Drawable clear;
    private Drawable eyeOpen;
    private Drawable eyeClose;
    private boolean isClick;

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
        mViewModel = new ViewModelProvider(mContext).get(LoginViewModel.class);
        mViewModel.getRegisterObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<String>(true) {
                @Override
                public void onSuccess(String data) {
                    mEtLoginName.setText(TextUtils.isEmpty(data) ? "" : data);
                    mEtLoginPwd.setText("");
                }
            });

        });
        DemoDbHelper.getInstance(mContext).getDatabaseCreatedObservable().observe(getViewLifecycleOwner(), response -> {
            mFragmentViewModel.HXlogin();
        });
    }

    @Override
    protected void initData() {
        super.initData();
        //切换密码可见不可见的两张图片
        eyeClose = getResources().getDrawable(R.drawable.d_pwd_hide);
        eyeOpen = getResources().getDrawable(R.drawable.d_pwd_show);
        clear = getResources().getDrawable(R.drawable.d_clear);
        EaseEditTextUtils.showRightDrawable(mEtLoginName, clear);
        EaseEditTextUtils.changePwdDrawableRight(mEtLoginPwd, eyeClose, eyeOpen, null, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login_register:
                mViewModel.clearRegisterInfo();
                mViewModel.setPageSelect(1);
                break;
            case R.id.btn_login:
                hideKeyboard();
                loginToServer();
                break;
        }
    }

    private void loginToServer() {
        if (TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(mPwd)) {
            ToastUtils.showToast(R.string.em_login_btn_info_incomplete);
            return;
        }
        isClick = true;

        passwordLogin();
        //先初始化数据库
//        DemoDbHelper.getInstance(mContext).initDb(mUserName);
    }

    /**
     * =========================================用户密码登录=================================================
     */
    public void passwordLogin() {

        showLoading("正在登录");
        DemoDbHelper.getInstance(mContext).closeDb();
        Map<String, Object> map = new HashMap<>();
        map.put("phone", mUserName);
        map.put("password", mPwd);
        map.put("deviceId", DeviceIdUtil.getDeviceId(requireContext()));
        map.put("os", "Android");
        map.put("version", Global.loginVersion);
        map.put("deviceName", SystemUtil.getDeviceManufacturer() + " " + SystemUtil.getSystemModel());
        OldApiClient.requestNetHandle(mContext, OldAppUrls.multiLogin, "", map, new ResultListener() {
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
        EaseEditTextUtils.showRightDrawable(mEtLoginName, clear);
        EaseEditTextUtils.showRightDrawable(mEtLoginPwd, isTokenFlag ? null : eyeClose);
        setButtonEnable(!TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mPwd));
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

    private abstract class MyClickableSpan extends ClickableSpan {

        @Override
        public void updateDrawState(@NonNull TextPaint ds) {
            super.updateDrawState(ds);
            ds.bgColor = Color.TRANSPARENT;
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        isClick = false;
    }

}
