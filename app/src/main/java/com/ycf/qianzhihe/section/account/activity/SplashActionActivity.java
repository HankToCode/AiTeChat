package com.ycf.qianzhihe.section.account.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.coorchice.library.SuperTextView;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.base.BaseInitActivity;

import butterknife.BindView;
import butterknife.OnClick;


/**
 * 启动页选择页面
 * 新增开屏后选择登陆注册页面
 */
public class SplashActionActivity extends BaseInitActivity {

    @BindView(R.id.stRegister)
    SuperTextView stRegister;

    @BindView(R.id.stLogin)
    SuperTextView stLogin;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_splash_action;
    }

    @Override
    protected void initSystemFit() {
        setFitSystemForTheme(false, R.color.transparent);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        initImmersionBar(true);
        super.initView(savedInstanceState);
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @OnClick({R.id.stRegister, R.id.stLogin})
    public void onViewClicked(View view) {
        if (view.getId() == R.id.stRegister) {
            RegisterActivity.actionStart(mContext);
            finish();
        } else if (view.getId() == R.id.stLogin) {
            LoginActivity.actionStart(mContext, "");
            finish();
        }
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SplashActionActivity.class);
        context.startActivity(intent);
    }
}
