package con.ycf.qianzhihe.section.account.activity;

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
import con.ycf.qianzhihe.R;
import con.ycf.qianzhihe.app.api.old_data.EventCenter;
import con.ycf.qianzhihe.app.api.old_http.ApiClient;
import con.ycf.qianzhihe.app.api.old_http.AppConfig;
import con.ycf.qianzhihe.app.api.old_http.ResultListener;
import con.ycf.qianzhihe.app.base.BaseInitActivity;
import con.zds.base.ImageLoad.GlideUtils;
import con.zds.base.Toast.ToastUtil;
import con.zds.base.util.StringUtil;

import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;


/**
 * 描   述: 忘记密码
 * 日   期: 2017/11/13 15:10
 * 更新日期: 2017/11/13
 *
 * @author lhb
 */
public class UpDataPasswordActivity extends BaseInitActivity implements View.OnClickListener, TextWatcher {

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, UpDataPasswordActivity.class);
        context.startActivity(intent);
    }

    private EaseTitleBar mTitleBar;
    private ConstraintLayout mLlUserName;
    private TextView mTvUserName;
    private EditText mEtPhone;
    private ConstraintLayout mLlUserCode;
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
    private ConstraintLayout mLlUserPasswordConfirm;
    private TextView mTvUserPasswordConfirm;
    private EditText mEtUserPasswordConfirm;
    private Button mBtnSubmit;


    private CountDownTimer timer;

    private int flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_up_password;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        mTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        mLlUserName = (ConstraintLayout) findViewById(R.id.ll_phone);
        mTvUserName = (TextView) findViewById(R.id.tv_phone);
        mEtPhone = (EditText) findViewById(R.id.et_phone);
        mLlUserCode = (ConstraintLayout) findViewById(R.id.ll_user_code);
        mTvUserCode = (TextView) findViewById(R.id.tv_user_code);
        mEtUserCode = (EditText) findViewById(R.id.et_user_code);
        mImgCode = (ImageView) findViewById(R.id.img_code);
        mLlUserSms = (ConstraintLayout) findViewById(R.id.ll_user_sms);
        mTvUserSms = (TextView) findViewById(R.id.tv_user_sms);
        mEtUserSms = (EditText) findViewById(R.id.et_user_sms);
        mTvSmsSend = (SuperTextView) findViewById(R.id.tv_sms_send);
        mLlUserPassword = (ConstraintLayout) findViewById(R.id.ll_user_password);
        mTvUserPassword = (TextView) findViewById(R.id.tv_user_password);
        mEtUserPassword = (EditText) findViewById(R.id.et_user_password);
        mLlUserPasswordConfirm = (ConstraintLayout) findViewById(R.id.ll_user_password_confirm);
        mTvUserPasswordConfirm = (TextView) findViewById(R.id.tv_user_password_confirm);
        mEtUserPasswordConfirm = (EditText) findViewById(R.id.et_user_password_confirm);
        mBtnSubmit = (Button) findViewById(R.id.btn_submit);


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

    @Override
    protected void initListener() {
        super.initListener();

        mImgCode.setOnClickListener(this);
        mTvSmsSend.setOnClickListener(this);
        mBtnSubmit.setOnClickListener(this);


        mEtPhone.addTextChangedListener(this);
        mEtUserCode.addTextChangedListener(this);
        mEtUserSms.addTextChangedListener(this);
        mEtUserPassword.addTextChangedListener(this);
        mEtUserPasswordConfirm.addTextChangedListener(this);

        mTitleBar.setOnBackPressListener(view -> {
            finish();
        });

    }

    /**
     * 刷新图形验证码
     */
    private void flushTy() {
        getRandom();
        GlideUtils.loadImageViewLoding(AppConfig.tuxingCode + "?random=" + flag, mImgCode);
    }

    /**
     *
     */
    private void getRandom() {
        flag = new Random().nextInt(99999);
        if (flag < 10000) {
            flag += 10000;
        }
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

    /**
     * 注册
     */
    public void register() {
        final String phone = mEtPhone.getText().toString();
        final String pwd = mEtUserPassword.getText().toString();
        final String pwd2 = mEtUserPasswordConfirm.getText().toString();
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
        } else if (StringUtil.isEmpty(pwd2) || !pwd.equals(pwd2)) {
            ToastUtil.toast("密码输入不一致");
            mEtUserPasswordConfirm.requestFocus();
            return;
        }

        final Map<String, Object> map = new HashMap<>();
        map.put("password", pwd);
        map.put("phone", phone);
        map.put("authCode", sms);
        ApiClient.requestNetHandle(this, AppConfig.forgetpasswordUrl, "正在提交...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast("修改成功");
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    /**
     * 获取验证码
     */
    private void getSms() {
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
        map.put("type", "updatePwd");
        map.put("random", flag + "");
        map.put("imgCode", mEtUserCode.getText().toString());

        ApiClient.requestNetHandle(this, AppConfig.getForgetPhoneCodeUrl, "获取验证码...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    ToastUtil.toast("发送成功");
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
            case R.id.img_code:
                flushTy();
                break;
            case R.id.tv_sms_send:
                getSms();
                break;
            case R.id.btn_submit:
                register();
                break;
            default:
                break;
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
                && !StringUtil.isEmpty(mEtUserPasswordConfirm));
    }
}
