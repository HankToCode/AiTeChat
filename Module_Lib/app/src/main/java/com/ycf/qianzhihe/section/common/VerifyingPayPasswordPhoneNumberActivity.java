package com.ycf.qianzhihe.section.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.R2;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.util.StringUtil;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import butterknife.BindView;
import butterknife.OnClick;
import com.zds.base.Toast.ToastUtil;

/**
 * 描   述: 忘记支付密码修改手机号
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/11/17 10:19
 * 更新日期: 2017/11/17
 *
 * @author lhb
 */
public class VerifyingPayPasswordPhoneNumberActivity extends BaseInitActivity {


    @BindView(R2.id.bar)
    View mBar;
    @BindView(R2.id.ll_back)
    LinearLayout mLlBack;
    @BindView(R2.id.toolbar_subtitle)
    TextView mToolbarSubtitle;
    @BindView(R2.id.img_right)
    ImageView mImgRight;
    @BindView(R2.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R2.id.toolbar)
    Toolbar mToolbar;
    @BindView(R2.id.et_password)
    EditText mEtPassword;
    @BindView(R2.id.tv_step)
    TextView mTvStep;
    @BindView(R2.id.tv_code)
    TextView mTvCode;
    @BindView(R2.id.llayout_title_1)
    RelativeLayout llayoutTitle1;
    @BindView(R2.id.et_tuxing)
    EditText etTuxing;
    @BindView(R2.id.img_code)
    ImageView imgCode;

    private CountDownTimer timer;
    private int flag;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_verify_pay_password_phone_number;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setTitle("忘记支付密码");
        mEtPassword.addTextChangedListener(watcher);
        countDown();
        flushTy();
    }

    /**
     * 刷新图形验证码
     */
    private void flushTy() {
        getRandom();
        GlideUtils.loadImageViewLoding(AppConfig.tuxingCode + "?random=" + flag, imgCode);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (timer != null) {
            timer.cancel();
        }

    }

    private TextWatcher watcher = new TextWatcher() {

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

    private void countDown() {
        timer = new CountDownTimer(60 * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTvCode.setText(millisUntilFinished / 1000 + "s后重新获取");
                mTvCode.setEnabled(false);
                mTvCode.setBackgroundResource(R.drawable.shap_gray_5);
            }

            @Override
            public void onFinish() {
                mTvCode.setText("获取验证码");
                mTvCode.setBackgroundResource(R.drawable.shap_red_5);
                mTvCode.setEnabled(true);
            }
        };
    }

    /**
     * 获取验证码
     */
    private void getCode() {
        if (StringUtil.isEmpty(etTuxing)) {
            ToastUtil.toast("图形验证码不能为空");
            etTuxing.requestFocus();
            return;
        }
        final String phone = UserComm.getUserInfo().getAccount();
        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("random", flag + "");
        map.put("imgCode", etTuxing.getText().toString());
        ApiClient.requestNetHandle(VerifyingPayPasswordPhoneNumberActivity.this, AppConfig.sendForgetPayPwdCode, "获取验证码...", map, new ResultListener() {
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
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.CLOSE2) {
            finish();
        }
    }

    @OnClick({R2.id.tv_code, R2.id.img_code, R2.id.tv_step})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.img_code) {
            flushTy();
        } else if (id == R.id.tv_code) {
            getCode();
        } else if (id == R.id.tv_step) {
            if (StringUtil.isEmpty(mEtPassword.getText().toString())) {
                ToastUtil.toast("验证码不能为空");
                return;
            }
            startActivity(new Intent(VerifyingPayPasswordPhoneNumberActivity.this, AppPayPassActivity.class)
                    .putExtra("authCode", mEtPassword.getText().toString())
                    .putExtra("from", "3"));
            finish();
        }
    }
}
