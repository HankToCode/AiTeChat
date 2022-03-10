package com.android.nanguo.section.account.fragment;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.alibaba.fastjson.JSON;
import com.coorchice.library.SuperTextView;
import com.hyphenate.EMCallBack;
import com.hyphenate.easeui.utils.EaseEditTextUtils;
import com.android.nanguo.DemoHelper;
import com.android.nanguo.MainActivity;
import com.android.nanguo.R;
import com.android.nanguo.app.api.Constant;
import com.android.nanguo.app.api.Global;
import com.android.nanguo.app.api.global.SP;
import com.android.nanguo.app.api.global.UserComm;
import com.android.nanguo.app.api.old_data.LoginInfo;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.CommonApi;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitFragment;
import com.android.nanguo.app.domain.EaseUser;
import com.android.nanguo.app.operate.UserOperateManager;
import com.android.nanguo.app.utils.my.MyHelper;
import com.android.nanguo.common.db.DemoDbHelper;
import com.android.nanguo.common.interfaceOrImplement.OnResourceParseCallback;
import com.android.nanguo.common.utils.DeviceIdUtil;
import com.android.nanguo.common.utils.NetworkUtil;
import com.android.nanguo.common.utils.PreferenceManager;
import com.android.nanguo.common.utils.ToastUtils;
import com.android.nanguo.section.account.activity.AccountManagerActivity;
import com.android.nanguo.section.account.activity.RegisterActivity;
import com.android.nanguo.section.account.activity.UpDataPasswordActivity;
import com.android.nanguo.section.account.viewmodels.LoginFragmentViewModel;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.StringUtil;
import com.zds.base.util.SystemUtil;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
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

    private ConstraintLayout mLlPwd;
    private ConstraintLayout mLlSms;
    private TextView mTvSms;
    private EditText mEtLoginSms;
    private SuperTextView mTvSmsSend;

    private CountDownTimer loginTimer;
    private ImageView mIvLogo;
    private ImageView mIvAvatar;
    private TextView mTvUnfrozen;
    private TextView mTvNickName;

    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_login;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setRetainInstance(true);

        mEtLoginName = findViewById(R.id.et_login_name);
        mEtLoginPwd = findViewById(R.id.et_login_pwd);
        mTvLoginRegister = findViewById(R.id.tv_login_register);
        mBtnLogin = findViewById(R.id.btn_login);
        mTvTitle = findViewById(R.id.tv_title);
        mTvLoginName = findViewById(R.id.tv_login_name);
        mTvPwd = findViewById(R.id.tv_pwd);
        mTvForgetPassword = findViewById(R.id.tv_forget_password);
        mTvSwitchLogin = findViewById(R.id.tv_switch_login);
        mTvOtherLogin = findViewById(R.id.tv_other_login);
        mIvChatLogin = findViewById(R.id.iv_chat_login);
        mTvFrozen = findViewById(R.id.tv_frozen);
        mLlPwd = findViewById(R.id.ll_pwd);
        mLlSms = findViewById(R.id.ll_sms);
        mTvSms = findViewById(R.id.tv_sms);
        mEtLoginSms = findViewById(R.id.et_login_sms);
        mTvSmsSend = findViewById(R.id.tv_sms_send);

        mIvLogo = (ImageView) findViewById(R.id.ivLogo);
        mIvAvatar = (ImageView) findViewById(R.id.iv_avatar);
        mTvUnfrozen = (TextView) findViewById(R.id.tv_unfrozen);
        mTvNickName = (TextView) findViewById(R.id.tv_nick_name);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mEtLoginName.addTextChangedListener(this);
        mEtLoginPwd.addTextChangedListener(this);
        mEtLoginSms.addTextChangedListener(this);
        mTvLoginRegister.setOnClickListener(this);
        mBtnLogin.setOnClickListener(this);
        mEtLoginPwd.setOnEditorActionListener(this);
        mEtLoginSms.setOnEditorActionListener(this);

        mTvForgetPassword.setOnClickListener(this);
        mTvSwitchLogin.setOnClickListener(this);
        mIvChatLogin.setOnClickListener(this);
        mTvFrozen.setOnClickListener(this);
        mTvUnfrozen.setOnClickListener(this);
        mTvSmsSend.setOnClickListener(this);


        EaseEditTextUtils.clearEditTextListener(mEtLoginName);
        EaseEditTextUtils.clearEditTextListener(mEtLoginPwd);
        EaseEditTextUtils.clearEditTextListener(mEtLoginSms);
        //加载已登录数据
//        localFriendList = UserOperateManager.getInstance().getAccountList();
    }

//    private List<EaseUser> localFriendList;

    @Override
    protected void initViewModel() {
        super.initViewModel();

        mFragmentViewModel = new ViewModelProvider(this).get(LoginFragmentViewModel.class);
        mFragmentViewModel.getLoginObservable().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<EaseUser>(true) {
                @Override
                public void onSuccess(EaseUser data) {
                    dismissLoading();
                    try {
                        DemoHelper.getInstance().setAutoLogin(true);//环信自动登录标志
                        /*LoginInfo currentUser = UserComm.getUserInfo();
                        EaseUser user = new EaseUser();
                        user.setNickname(currentUser.getNickName());
                        user.setAvatar(currentUser.getUserHead());
                        user.setUserCode(currentUser.getUserCode());
                        user.setAccount(currentUser.getAccount());
                        user.setPassword(currentUser.getPassword());
                        //去重
                        if (localFriendList.size() > 0) {
                            Iterator<EaseUser> it = localFriendList.iterator();
                            while (it.hasNext()) {
                                EaseUser easeUser = it.next();
                                if (user.getAccount().equals(easeUser.getAccount()) || StringUtil.isEmpty(easeUser.getAccount())) {
                                    it.remove();
                                }
                            }
                        }
                        localFriendList.add(user);
                        UserOperateManager.getInstance().saveLoginAccountToLocal(FastJsonUtil.toJSONString(localFriendList));*/
                    } catch (Exception ignored) {

                    }

//                    mFragmentViewModel.getMyModel().saveLoginAccount(user);
                    //跳转到主页
                    MainActivity.actionStart(mContext);
                    mContext.finish();
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    Log.d("TAG", message + "环信登录Error code=" + code);
                    dismissLoading();
                    /*if (code == 218) {//另一个用户已经登录
                        //###code=218  Another User is already login
                        mContext.finish();
                    }*/
                    /*if (code == EMError.USER_AUTHENTICATION_FAILED) {
                        ToastUtils.showToast(R.string.demo_error_user_authentication_failed);
                    } else if (code == EMError.USER_ALREADY_LOGIN) {
                        //Same User is already login环信登录Error code=200
                        // TODO: 2021/8/21 用户已登录 需处理
                    }  else {
                        ToastUtils.showToast(message);
                    }*/
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
//        EaseEditTextUtils.showRightDrawable(mEtLoginName, clear);
//        EaseEditTextUtils.changePwdDrawableRight(mEtLoginPwd, eyeClose, eyeOpen, null, null, null);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_login_register:
                RegisterActivity.actionStart(requireContext());
                if (getActivity() != null) {
                    getActivity().finish();
                }
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
        //切换登录方式时把密码或验证码框置空
        mEtLoginPwd.setText("");
        mEtLoginSms.setText("");
        mLlPwd.setVisibility(loginMethod == 0 ? View.VISIBLE : View.GONE);
        mLlSms.setVisibility(loginMethod == 1 ? View.VISIBLE : View.GONE);
        mTvSwitchLogin.setText(loginMethod == 0 ? "短信登录" : "密码登录");
    }

    private String fromType = "";

    @Override
    protected void initArgument() {
        super.initArgument();
        Bundle extras = getArguments();
        fromType = extras.getString("fromType");
    }

    private void loginToServer() {
        if (!TextUtils.isEmpty(fromType)) {//处理账号切换
            //判断之前是否登录过账号
            if (DemoHelper.getInstance().isLoggedIn()) {
                logout();// 退出登录接口 退出环信
            } else {
                if (loginMethod == 0) {
                    passwordLogin();
                } else {
                    smsCodeLogin();
                }
            }
        } else {
            //处理多端登录异常
            if (DemoHelper.getInstance().isLoggedIn()) {
                logouHx();//当前设备已登录过，则退出环信SDK 再走登录
            } else {
                if (loginMethod == 0) {
                    passwordLogin();
                } else {
                    smsCodeLogin();
                }
            }

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
        ApiClient.requestNetHandle(mContext, AppConfig.multiLogin, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    LoginInfo loginInfo = JSON.parseObject(json, LoginInfo.class);
                    if (loginInfo != null) {
                        loginInfo.setPassword(mPwd);
                        loginInfo.setAccount(loginInfo.getPhone());
                        PreferenceManager.getInstance().setParam(SP.SP_LANDED_ON_LOGIN, mUserName);
                        UserComm.saveUsersInfo(loginInfo);
                        DemoDbHelper.getInstance(mContext).initDb(mUserName);
//                        CommonApi.upUserInfo(mContext);//更新用户信息
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

        if (TextUtils.isEmpty(mUserName) || TextUtils.isEmpty(mSms)) {
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

        ApiClient.requestNetHandle(requireContext(), AppConfig.toSMSMultiLoginUrl, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    LoginInfo loginInfo = JSON.parseObject(json, LoginInfo.class);
                    if (loginInfo != null) {
                        loginInfo.setAccount(loginInfo.getPhone());
                        PreferenceManager.getInstance().setParam(SP.SP_LANDED_ON_LOGIN, mUserName);

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

        ApiClient.requestNetHandle(requireContext(), AppConfig.getSMSCodeForLogin, "获取验证码", map, new ResultListener() {
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
        } else if (mEtLoginSms.hasFocus()) {
            mEtLoginSms.setImeOptions(enable ? EditorInfo.IME_ACTION_DONE : EditorInfo.IME_ACTION_PREVIOUS);
        } else if (mEtLoginName.hasFocus()) {
            mEtLoginSms.setImeOptions(enable ? EditorInfo.IME_ACTION_DONE : EditorInfo.IME_ACTION_NEXT);
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
            if (!TextUtils.isEmpty(mUserName) && !TextUtils.isEmpty(mSms)) {
                hideKeyboard();
                loginToServer();
                return true;
            }
        }
        return false;
    }

    private void logouHx() {
        MyHelper.getInstance().logout(false, new EMCallBack() {
            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoading();
                        UserComm.clearUserInfo();
                        if (loginMethod == 0) {
                            passwordLogin();
                        } else {
                            smsCodeLogin();
                        }
                    }
                });
            }

            @Override
            public void onProgress(int progress, String status) {
            }

            @Override
            public void onError(int code, String message) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoading();
                    }
                });
            }
        });
    }

    private void logout() {
        Map<String, Object> map = new HashMap<>();
        map.put("deviceId", DeviceIdUtil.getDeviceId(mContext));
        ApiClient.requestNetHandle(mContext, AppConfig.multiDeviceLogout, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                MyHelper.getInstance().logout(false, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoading();
                                UserComm.clearUserInfo();
                                if (loginMethod == 0) {
                                    passwordLogin();
                                } else {
                                    smsCodeLogin();
                                }
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String message) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoading();
                            }
                        });
                    }
                });
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if (loginTimer != null)
            loginTimer.cancel();
    }
}
