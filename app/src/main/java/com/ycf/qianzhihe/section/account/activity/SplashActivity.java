package com.ycf.qianzhihe.section.account.activity;

import android.animation.Animator;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.ycf.qianzhihe.MainActivity;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.weight.CommonConfirmDialog;
import com.ycf.qianzhihe.common.interfaceOrImplement.OnResourceParseCallback;
import com.ycf.qianzhihe.section.account.viewmodels.SplashViewModel;
import com.hyphenate.util.EMLog;
import com.ycf.qianzhihe.section.dialog.UserProtocolDialog;
import com.zds.base.global.BaseConstant;
import com.zds.base.util.Preference;

public class SplashActivity extends BaseInitActivity {
    private ImageView ivSplash;
    private TextView tvProduct;
    private SplashViewModel model;
    private UserProtocolDialog mUserProtocolDialog;
    private CommonConfirmDialog mCommonConfirmDialog;

    @Override
    protected int getLayoutId() {
        return R.layout.demo_splash_activity;
    }

    @Override
    protected void initSystemFit() {
        setFitSystemForTheme(false, R.color.transparent);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        initImmersionBar(true);
        super.initView(savedInstanceState);
        ivSplash = findViewById(R.id.iv_splash);
        tvProduct = findViewById(R.id.tv_product);
    }

    @Override
    protected void initData() {
        super.initData();
        model = new ViewModelProvider(this).get(SplashViewModel.class);

        ivSplash.animate()
                .alpha(1)
                .setDuration(500)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {

                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        userProtocolDialog();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();

        tvProduct.animate()
                .alpha(1)
                .setDuration(500)
                .start();

    }

    private void userProtocolDialog() {
        //1.用户协议弹窗
        if (Preference.getBoolPreferences(SplashActivity.this, BaseConstant.SP.KEY_IS_AGREE_USER_PROTOCOL, false)) {
            //已同意
//            toApp();
            loginSDK();
        } else {
            //未同意
            showUserProtocolDialog();
        }
    }

    private void showUserProtocolDialog() {
        if (mUserProtocolDialog == null) {
            mUserProtocolDialog = new UserProtocolDialog(this);

            mUserProtocolDialog.setOnAgreeClickListener(new UserProtocolDialog.OnAgreeClickListener() {
                @Override
                public void onAgreeClick() {
                    Preference.saveBoolPreferences(SplashActivity.this, BaseConstant.SP.KEY_IS_AGREE_USER_PROTOCOL, true);
//                    toApp();
                    loginSDK();
                }
            });

            mUserProtocolDialog.setOnNotAgreeClickListener(new UserProtocolDialog.OnNotAgreeClickListener() {
                @Override
                public void onNotAgreeClick() {
                    //弹出再次提醒弹窗
                    showCommonConfirmDialog();
                }
            });
        }
        mUserProtocolDialog.show();
    }

    private void showCommonConfirmDialog() {
        if (mCommonConfirmDialog == null) {
            mCommonConfirmDialog = new CommonConfirmDialog(this);

            mCommonConfirmDialog.setOnConfirmClickListener(new CommonConfirmDialog.OnConfirmClickListener() {
                @Override
                public void onConfirmClick(View view) {
                    showUserProtocolDialog();
                }
            });
            mCommonConfirmDialog.setOnCancelClickListener(new CommonConfirmDialog.OnCancelClickListener() {
                @Override
                public void onCancelClick(View view) {
                    //退出App
                    //MobclickAgent.onKillProcess(activity);
                    finish();
                }
            });
        }
        mCommonConfirmDialog.show();

        mCommonConfirmDialog.setCancelable(false);
        mCommonConfirmDialog.setCanceledOnTouchOutside(false);

        mCommonConfirmDialog.setTitle("您需要同意本隐私协议才能继续使用千纸鹤");
        mCommonConfirmDialog.setContent("若您不同意本隐私协议，很遗憾我们将无法为您提供服务");
        mCommonConfirmDialog.setButtonText("仍不同意", "查看协议");
    }

    private void loginSDK() {
        model.getLoginData().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>(true) {
                @Override
                public void onSuccess(Boolean data) {
                    MainActivity.actionStart(mContext);
                    finish();
                }

                @Override
                public void onError(int code, String message) {
                    super.onError(code, message);
                    EMLog.i("TAG", "error message = " + response.getMessage());
                    LoginActivity.actionStart(mContext,"");
                    finish();
                }
            });

        });
    }
}
