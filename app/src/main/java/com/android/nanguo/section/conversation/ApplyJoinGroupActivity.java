package com.android.nanguo.section.conversation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.nanguo.R;
import com.android.nanguo.app.api.global.SP;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.common.utils.PreferenceManager;

import com.hyphenate.easeui.widget.EaseTitleBar;

public class ApplyJoinGroupActivity extends BaseInitActivity {

    EaseTitleBar titleBar;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, ApplyJoinGroupActivity.class);
        context.startActivity(starter);
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("群通知");
        titleBar.setOnBackPressListener(view -> {
            finish();
        });

        getSupportFragmentManager().beginTransaction().add(R.id.container,
                new GroupUserApplyFragment()).commit();

        PreferenceManager.getInstance().setParam(SP.APPLY_JOIN_GROUP_NUM, 0);

    }

    @Override
    protected void onEventComing(EventCenter center) {

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_audit_msg;
    }
}