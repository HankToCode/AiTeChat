package com.ycf.qianzhihe.wxapi;

import android.app.Activity;
import android.os.Bundle;

import com.blankj.utilcode.util.Utils;
import com.ycf.qianzhihe.R;

/**
 * 防止跳转微信，因为横竖屏导致闪退
 */
public class PortraitActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_portrait);
        //防止意外没退出，增加点击退出
        findViewById(R.id.cl_root).setOnClickListener(v -> finish());

        Utils.runOnUiThreadDelayed(this::finish, 1000);
    }
}