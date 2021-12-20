package com.android.nanguo.section.chat.activity;

import android.os.Bundle;
import android.view.WindowManager;

import com.android.nanguo.R;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.section.chat.fragment.SelContactFragment;


/**
 * 作   者：lhb
 * 描   述：发送名片-联系人列表
 * 日   期: 2017/11/17 18:07
 * 更新日期: 2017/11/17
 *
 * @author Administrator
 */
public class SelContactActivity extends BaseInitActivity {
    private SelContactFragment mSelContactFragment;


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
    }




    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }


    @Override
    protected void initData() {
        super.initData();
        //use SelContactFragment
        mSelContactFragment = new SelContactFragment();
        //pass parameters to chat fragment
        mSelContactFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container, mSelContactFragment).commit();
    }

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {

    }



}
