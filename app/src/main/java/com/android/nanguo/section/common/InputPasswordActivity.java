package com.android.nanguo.section.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import androidx.appcompat.widget.Toolbar;

import com.android.nanguo.DemoApplication;
import com.android.nanguo.R;
import com.android.nanguo.app.api.global.EventUtil;
import com.android.nanguo.app.api.global.UserComm;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.CommonApi;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.app.weight.PasswordInputEdt;
import com.zds.base.Toast.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;

/**
 * 作   者：赵大帅
 * 描   述:
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/11/17 9:28
 * 更新日期: 2017/11/17
 */
public class InputPasswordActivity extends BaseInitActivity {


    @BindView(R.id.bar)
    View mBar;
    @BindView(R.id.ll_back)
    LinearLayout mLlBack;
    @BindView(R.id.toolbar_subtitle)
    TextView mToolbarSubtitle;
    @BindView(R.id.img_right)
    ImageView mImgRight;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
    @BindView(R.id.edittext)
    PasswordInputEdt mEdittext;


    private String oldPassword;
    private String code;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_input_password;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setTitle("支付密码");
        mEdittext.setOnInputOverListener(new PasswordInputEdt.onInputOverListener() {
            @Override
            public void onInputOver(String text) {
                if (oldPassword != null && !"".equals(oldPassword)) {
                    toUppassword(text);
                } else if (code != null && !"".equals(code)) {
                    forgetPassword(text);
                } else {
                    toAddpassword(text);
                }
            }
        });

    }

    /**
     * 修改密码
     *
     * @param password
     */
    private void toUppassword(String password) {
        Map<String, Object> map = new HashMap<>();
        map.put("newPayPassword", password);
        map.put("payPassword", oldPassword);
        ApiClient.requestNetHandle(this, AppConfig.upPassword, "正在提交修改...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast(msg);
                EventBus.getDefault().post(new EventCenter(EventUtil.CLOSE1));
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                finish();
            }
        });
    }

    /**
     * 添加支付密码
     *
     * @param password
     */
    private void toAddpassword(String password) {
        Map<String, Object> map = new HashMap<>();
        map.put("payPassword", password);
        ApiClient.requestNetHandle(this, AppConfig.addPayPassword, "正在提交...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast(msg);
                CommonApi.upUserInfo(DemoApplication.getInstance().getApplicationContext());
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                finish();
            }
        });
    }

    /**
     * 忘记支付密码
     *
     * @param password
     */
    private void forgetPassword(String password) {
        Map<String, Object> map = new HashMap<>();
        map.put("newPayPwd", password);
        map.put("authCode", code);
        map.put("phone", UserComm.getUserInfo().getAccount());
        ApiClient.requestNetHandle(this, AppConfig.forgetPassword, "正在提交...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast(msg);
                CommonApi.upUserInfo(DemoApplication.getInstance().getApplicationContext());
                EventBus.getDefault().post(new EventCenter(EventUtil.CLOSE2));
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                finish();
            }
        });
    }

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {

    }


    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        oldPassword = intent.getStringExtra("password");
        code = intent.getStringExtra("code");

    }

}
