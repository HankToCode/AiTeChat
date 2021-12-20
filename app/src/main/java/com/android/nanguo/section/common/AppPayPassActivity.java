package com.android.nanguo.section.common;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.android.nanguo.R;
import com.android.nanguo.app.api.global.EventUtil;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.app.weight.OnPasswordInputFinish;
import com.android.nanguo.app.weight.PasswordView;

import butterknife.BindView;

/**
 * @author lhb
 * 支付密码
 */
public class AppPayPassActivity extends BaseInitActivity {
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.pwd_view)
    PasswordView mPwdView;
    private String mFrom;
    private String authCode;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_pay_pass_app;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        if (mFrom.equals("1")) {
            mToolbarTitle.setText("支付密码");
        } else if (mFrom.equals("2")) {
            mToolbarTitle.setText("修改支付密码");
            mPwdView.getTvSetTitle().setText("请输入旧的支付密码");
        } else if (mFrom.equals("3")) {
            mToolbarTitle.setText("忘记支付密码");
        }
        mPwdView.setOnFinishInput(new OnPasswordInputFinish() {
            @Override
            public void inputFinish(String pass) {
                Intent intent = new Intent(AppPayPassActivity.this, PayPassSureActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("from", mFrom);
                bundle.putString("pass", pass);
                bundle.putString("authCode", authCode);
                intent.putExtras(intent);
                startActivity(intent);
                finish();
            }

            //取消支付
            @Override
            public void outfo() {
                finish();
            }

            //忘记密码回调事件
            @Override
            public void forgetPwd() {

            }
        });
    }

    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.CLOSE1) {
            finish();
        }
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mFrom = intent.getStringExtra("from");
        authCode = intent.getStringExtra("authCode");
    }


    /**
     * 添加支付密码
     *
     * @param password
     */
    /*private void toAddpassword(String password) {
        Map<String, Object> map = new HashMap<>();
        map.put("payPassword", password);
        ApiClient.requestNetHandle(this, AppConfig.addPayPassword, "支付密码生成中...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                toast(msg);
                CommonApi.upUserInfo(DemoApplication.getInstance().getApplicationContext());
                startActivity(WalletActivity.class);
                finish();
            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
                finish();
            }
        });
    }*/

}
