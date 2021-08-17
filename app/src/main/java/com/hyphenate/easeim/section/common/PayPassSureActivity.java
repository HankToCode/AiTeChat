package com.hyphenate.easeim.section.common;

import static com.zds.base.Toast.ToastUtil.toast;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;


import com.hyphenate.easeim.DemoApplication;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.app.api.global.EventUtil;
import com.hyphenate.easeim.app.api.old_data.EventCenter;
import com.hyphenate.easeim.app.api.old_http.ApiClient;
import com.hyphenate.easeim.app.api.old_http.AppConfig;
import com.hyphenate.easeim.app.api.old_http.CommonApi;
import com.hyphenate.easeim.app.api.old_http.ResultListener;
import com.hyphenate.easeim.app.base.BaseInitActivity;
import com.hyphenate.easeim.app.weight.OnPasswordInputFinish;
import com.hyphenate.easeim.app.weight.PasswordViewSure;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * @author lhb
 * 支付密码
 */
public class PayPassSureActivity extends BaseInitActivity {
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.pwd_view)
    PasswordViewSure mPwdView;
    private String mFrom;
    private String oldPass;
    private String authCode;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pay_pass_sure;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        if (mFrom.equals("1")) {
            mToolbarTitle.setText("支付密码");
        } else if (mFrom.equals("2")) {
            mToolbarTitle.setText("修改支付密码");
            mPwdView.getTvSetTitle().setText("请输入新的支付密码");
        } else if (mFrom.equals("3")) {
            mToolbarTitle.setText("忘记支付密码");
        }
        mPwdView.setOnFinishInput(new OnPasswordInputFinish() {
            @Override
            public void inputFinish(String pass) {
                if (!pass.equals(oldPass) && ("1".equals(mFrom) || "3".equals(mFrom))) {
                    toast("两次密码不一致");
                    return;
                }

                if (mFrom.equals("1")) {
                    toAddpassword(pass);
                } else if (mFrom.equals("2")) {
                    toUppassword(pass);
                } else {
                    forgetPassword(pass);
                }
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

    @Override
    protected void onEventComing(EventCenter center) {

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        Bundle extras = intent.getExtras();
        mFrom = extras.getString("from");
        oldPass = extras.getString("pass");
        if (mFrom.equals("3")) {
            authCode = extras.getString("authCode", "");
        }
    }


    /**
     * 添加支付密码
     *
     * @param password
     */
    private void toAddpassword(String password) {
        Map<String, Object> map = new HashMap<>();
        map.put("payPassword", password);
        ApiClient.requestNetHandle(this, AppConfig.addPayPassword, "支付密码生成中...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                toast("支付密码设置成功");
                EventBus.getDefault().post(new EventCenter(EventUtil.CLOSE1));
                CommonApi.upUserInfo(DemoApplication.getInstance().getApplicationContext());
                WalletActivity.actionStart(PayPassSureActivity.this);
                finish();
            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
            }
        });
    }


    /**
     * 修改密码
     *
     * @param password
     */
    private void toUppassword(String password) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("newPayPassword", password);
        map.put("payPassword", oldPass);
        ApiClient.requestNetHandle(this, AppConfig.upPassword, "正在提交修改...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                toast("支付密码修改成功");
                EventBus.getDefault().post(new EventCenter(EventUtil.CLOSE1));
                finish();
            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
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
        map.put("authCode", authCode);
        ApiClient.requestNetHandle(this, AppConfig.forgetPassword, "正在提交...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                toast("支付密码修改成功");
                CommonApi.upUserInfo(DemoApplication.getInstance().getApplicationContext());
                EventBus.getDefault().post(new EventCenter(EventUtil.CLOSE2));
                finish();
            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
                finish();
            }
        });
    }


}
