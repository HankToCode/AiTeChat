package com.android.nanguo.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.nanguo.DemoApplication;
import com.android.nanguo.R;
import com.android.nanguo.app.api.global.EventUtil;
import com.android.nanguo.app.api.global.UserComm;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.CommonApi;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.app.utils.XClickUtil;
import com.android.nanguo.app.weight.OnPasswordInputFinish;
import com.android.nanguo.app.weight.PasswordView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.android.nanguo.common.utils.ToastUtils;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.util.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import com.zds.base.Toast.ToastUtil;

public class ResetPayPwdActivity extends BaseInitActivity {

    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.et_img)
    EditText et_img;
    @BindView(R.id.iv_code)
    ImageView iv_code;
    @BindView(R.id.et_code)
    EditText et_code;
    @BindView(R.id.tv_code)
    TextView tv_code;
    @BindView(R.id.tv_step)
    TextView tv_step;
    @BindView(R.id.pwd_view)
    PasswordView mPwdView;
    @BindView(R.id.pwd_true)
    PasswordView pwd_true;
    @BindView(R.id.et_pwd2)
    EditText et_pwd2;
    @BindView(R.id.et_pwd)
    EditText et_pwd;
    @BindView(R.id.tv_refresh)
    TextView tv_refresh;

    private CountDownTimer timer;
    private int flag;
    private String pwd,pwd2;


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ResetPayPwdActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_reset_paypwd;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar.setTitle("重置支付密码");
        mTitleBar.setOnBackPressListener(view -> finish());

        et_code.addTextChangedListener(watcher);
        countDown();//初始化倒计时
        flushTy();//刷新图形验证码

        mPwdView.setOnFinishInput(new OnPasswordInputFinish() {
            @Override
            public void inputFinish(String pass) {
                pwd = pass;
            }

            //取消支付
            @Override
            public void outfo() {
                finish();
            }

            //忘记密码回调事件
            @Override
            public void forgetPwd() {

            }
        });
        pwd_true.setOnFinishInput(new OnPasswordInputFinish() {
            @Override
            public void inputFinish(String pass) {
                pwd2=pass;
            }

            //取消支付
            @Override
            public void outfo() {
                finish();
            }

            //忘记密码回调事件
            @Override
            public void forgetPwd() {

            }
        });
    }

    /**
     * 忘记支付密码
     *
     * @param password
     */
    private void forgetPassword(String password) {
        Map<String, Object> map = new HashMap<>();
        map.put("payPassword", password);
        map.put("authCode", et_code.getText().toString().trim());
        ApiClient.requestNetHandle(this, AppConfig.forgetPassword, "正在提交...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast("支付密码重置成功");
//                MyApplication.getInstance().UpUserInfo();
                CommonApi.upUserInfo(DemoApplication.getInstance().getApplicationContext());
                EventBus.getDefault().post(new EventCenter(EventUtil.CLOSE2));
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                finish();
            }
        });
    }

    @OnClick({R.id.tv_code, R.id.tv_step, R.id.tv_refresh})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_refresh:
                if (!XClickUtil.isFastDoubleClick(view, 1500)) {
                    flushTy();
                } else {
                    ToastUtils.showToast("请勿连续点击");
                }
                break;
            case R.id.tv_code:
                getCode();
                break;
            case R.id.tv_step:
                pwd = et_pwd.getText().toString().trim();
                pwd2 = et_pwd2.getText().toString().trim();
                if (StringUtil.isEmpty(et_code.getText().toString())) {
                    ToastUtil.toast("验证码不能为空");
                    return;
                }
                if (TextUtils.isEmpty(pwd)) {
                    ToastUtil.toast("密码不能为空");
                    return;
                }

                if (!pwd.equals(pwd2)) {
                    ToastUtil.toast("两次密码不一致");
                    return;
                }
                forgetPassword(pwd);
                break;
            default:
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void getCode() {
        if (StringUtil.isEmpty(et_img)) {
            ToastUtil.toast("图形验证码不能为空");
            et_img.requestFocus();
            return;
        }
        final String phone = UserComm.getUserInfo().getAccount();
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("random", flag + "");
        map.put("imgCode", et_img.getText().toString());
        ApiClient.requestNetHandle(ResetPayPwdActivity.this, AppConfig.sendForgetPayPwdCode, "获取验证码...", map, new ResultListener() {
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

    /**
     * 刷新图形验证码
     */
    private void flushTy() {
        flag = new Random().nextInt(99999);
        if (flag < 10000) {
            flag += 10000;
        }
        GlideUtils.loadImageViewLoding(AppConfig.tuxingCode + "?random=" + flag, iv_code);
    }


    private void countDown() {
        timer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                tv_code.setText(millisUntilFinished / 1000 + "s后重新获取");
                tv_code.setEnabled(false);
//                tv_code.setBackgroundResource(R.drawable.shap_gray_5);
            }

            @Override
            public void onFinish() {
                tv_code.setText("获取验证码");
//                tv_code.setBackgroundResource(R.drawable.shap_red_5);
                tv_code.setEnabled(true);
            }
        };
    }


    private final TextWatcher watcher = new TextWatcher() {

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            // TODO Auto-generated method stub
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub
        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub
        }
    };

}
