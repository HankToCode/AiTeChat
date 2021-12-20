package com.android.nanguo.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hyphenate.easeui.widget.EaseTitleBar;
import com.android.nanguo.R;
import com.android.nanguo.app.base.BaseInitActivity;

import butterknife.BindView;

//提现结果
public class WithdrawResultActivity extends BaseInitActivity {

    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, WithdrawResultActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_withdraw_result;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar.setTitle("提现");
        mTitleBar.setOnBackPressListener(view -> finish());
    }

}
