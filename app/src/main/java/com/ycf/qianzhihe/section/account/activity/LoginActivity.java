package com.ycf.qianzhihe.section.account.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.section.account.fragment.LoginFragment;

public class LoginActivity extends BaseInitActivity {

    public static void actionStart(Context context,String fromType) {
        Intent intent = new Intent(context, LoginActivity.class);
        intent.putExtra("fromType", fromType);
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
        LoginFragment loginFragment = new LoginFragment();
        loginFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().
                replace(R.id.fl_fragment, loginFragment).
                commit();
    }

    @Override
    protected void initData() {
        super.initData();
    }

}
