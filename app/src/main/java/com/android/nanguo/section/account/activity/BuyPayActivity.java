package com.android.nanguo.section.account.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.nanguo.R;
import com.android.nanguo.app.api.global.UserComm;
import com.android.nanguo.app.api.new_data.VipBean;
import com.android.nanguo.app.api.old_data.LoginInfo;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.CommonApi;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.app.weight.ChooseMemberLayout;
import com.android.nanguo.app.weight.CommonDialog;
import com.android.nanguo.app.weight.PasswordEditText;
import com.android.nanguo.app.weight.passwoed_keyboard.OnNumberKeyboardListener;
import com.android.nanguo.app.weight.passwoed_keyboard.XNumberKeyboardView;
import com.android.nanguo.common.utils.ToastUtils;
import com.android.nanguo.common.widget.ItemInfoView;
import com.android.nanguo.section.common.InputPasswordActivity;
import com.android.nanguo.section.common.ResetPayPwdActivity;
import com.coorchice.library.SuperTextView;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.NumberUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

//支付界面
public class BuyPayActivity extends BaseInitActivity {

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, BuyPayActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_order_pay;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initImmersionBar(true);
    }

    @Override
    protected void initListener() {
        super.initListener();

        findViewById(R.id.btnSubmit).setOnClickListener(v -> {
            ToastUtil.toast("暂未开通支付功能，敬请期待~");
        });

        findViewById(R.id.iivWeChatPay).setOnClickListener(v -> {
            ((ItemInfoView) findViewById(R.id.iivWeChatPay)).setRightSelect(true);
            ((ItemInfoView) findViewById(R.id.iivAliPay)).setRightSelect(false);
        });
        findViewById(R.id.iivAliPay).setOnClickListener(v -> {
            ((ItemInfoView) findViewById(R.id.iivAliPay)).setRightSelect(true);
            ((ItemInfoView) findViewById(R.id.iivWeChatPay)).setRightSelect(false);
        });
    }

    @Override
    protected void initData() {
        super.initData();
    }


}
