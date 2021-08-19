package com.hyphenate.easeim.section.account.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hyphenate.easeim.R;
import com.hyphenate.easeim.app.base.BaseInitActivity;
import com.hyphenate.easeim.section.account.fragment.LoginFragment;

public class LoginActivity extends BaseInitActivity {

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_login;
    }

    @Override
    protected void initSystemFit() {
        setFitSystemForTheme(false, R.color.transparent);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        initImmersionBar(true);
        getSupportFragmentManager().
                beginTransaction().
                replace(R.id.fl_fragment, new LoginFragment()).
                commit();
    }

    @Override
    protected void initData() {
        super.initData();
    }

}