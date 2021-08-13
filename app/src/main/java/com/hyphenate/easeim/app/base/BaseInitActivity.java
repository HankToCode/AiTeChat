package com.hyphenate.easeim.app.base;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;

import com.hyphenate.easeim.app.api.old_data.EventCenter;
import com.lzy.okgo.OkGo;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;

public abstract class BaseInitActivity extends BaseActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(getLayoutId());

        initSystemFit();
        initIntent(getIntent());
        initView(savedInstanceState);
        initListener();
        initData();

    }

    protected void initSystemFit() {
        setFitSystemForTheme(true);
    }

    /**
     * get layout id
     *
     * @return
     */
    protected abstract int getLayoutId();

    /**
     * init intent
     *
     * @param intent
     */
    protected void initIntent(Intent intent) {
    }

    /**
     * init view
     *
     * @param savedInstanceState
     */
    protected void initView(Bundle savedInstanceState) {

    }

    /**
     * init listener
     */
    protected void initListener() {
    }

    /**
     * init data
     */
    protected void initData() {
    }

}
