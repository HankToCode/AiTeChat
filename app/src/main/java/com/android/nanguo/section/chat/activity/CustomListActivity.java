package com.android.nanguo.section.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.android.nanguo.R;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.section.chat.fragment.CustomServiceFragment;


/**
 * @author lhb
 * 客服列表
 */
public class CustomListActivity extends BaseInitActivity {
    private CustomServiceFragment mCustomServiceFragment;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, CustomListActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mCustomServiceFragment = new CustomServiceFragment();
        //pass parameters to chat fragment
        getSupportFragmentManager().beginTransaction().add(R.id.container, mCustomServiceFragment).commit();
    }



    @Override
    protected void onEventComing(EventCenter center) {

    }



}
