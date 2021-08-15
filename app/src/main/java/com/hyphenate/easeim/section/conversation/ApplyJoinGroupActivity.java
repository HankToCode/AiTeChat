package com.hyphenate.easeim.section.conversation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.hyphenate.easeim.R;
import com.hyphenate.easeim.app.api.global.SP;
import com.hyphenate.easeim.app.api.old_data.EventCenter;
import com.hyphenate.easeim.app.base.BaseInitActivity;
import com.hyphenate.easeim.common.utils.PreferenceManager;
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