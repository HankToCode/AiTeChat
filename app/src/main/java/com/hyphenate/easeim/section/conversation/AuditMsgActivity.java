package com.hyphenate.easeim.section.conversation;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.hyphenate.easeim.R;
import com.hyphenate.easeim.app.api.global.SP;
import com.hyphenate.easeim.app.api.old_data.EventCenter;
import com.hyphenate.easeim.app.base.BaseInitActivity;
import com.hyphenate.easeim.common.utils.PreferenceManager;
import com.hyphenate.easeui.widget.EaseTitleBar;

public class AuditMsgActivity extends BaseInitActivity {

    EaseTitleBar titleBar;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, AuditMsgActivity.class);
        context.startActivity(starter);
    }

    FriendApplyFragment friendApplyFragment;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        titleBar = findViewById(R.id.title_bar);
        titleBar.setTitle("新朋友");
        titleBar.setOnBackPressListener(view -> {
            finish();
        });

        friendApplyFragment = new FriendApplyFragment();

        getSupportFragmentManager().beginTransaction().add(R.id.container,
                friendApplyFragment).commit();

        PreferenceManager.getInstance().setParam(SP.APPLY_ADD_USER_NUM, 0);

    }

    @Override
    protected void onEventComing(EventCenter center) {

    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_audit_msg;
    }
}