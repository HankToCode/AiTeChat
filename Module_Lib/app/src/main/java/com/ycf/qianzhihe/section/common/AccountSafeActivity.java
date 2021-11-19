package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.R2;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.section.account.activity.UpDataPasswordActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.ycf.qianzhihe.section.me.activity.MultiDeviceActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class AccountSafeActivity extends BaseInitActivity {

    @BindView(R2.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R2.id.tv_pay_pwd)
    TextView tv_pay_pwd;
    @BindView(R2.id.tv_login_pwd)
    TextView tv_login_pwd;
    @BindView(R2.id.tv_wallet_lock)
    TextView tv_wallet_lock;
    @BindView(R2.id.tv_multi_device)
    TextView tv_multi_device;


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, AccountSafeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_accountsafe;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar.setTitle("账号与安全");
        mTitleBar.setOnBackPressListener(view -> finish());


    }

    @OnClick({R2.id.tv_pay_pwd, R2.id.tv_login_pwd, R2.id.tv_wallet_lock, R2.id.tv_multi_device})
    public void click(View v) {
        int id = v.getId();
        if (id == R.id.tv_multi_device) {
            MultiDeviceActivity.actionStart(this);
        } else if (id == R.id.tv_pay_pwd) {
            ResetPayPwdActivity.actionStart(this);
        } else if (id == R.id.tv_login_pwd) {
            UpDataPasswordActivity.actionStart(this);
        } else if (id == R.id.tv_wallet_lock) {//零钱锁
            WalletLockActivity.actionStart(this);
        }
    }

}
