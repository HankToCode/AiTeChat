package com.ycf.qianzhihe.section.account.activity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.Context;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.lifecycle.ViewModelProvider;

import com.airbnb.lottie.LottieAnimationView;
import com.hyphenate.util.EMLog;
import com.ycf.qianzhihe.MainActivity;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.weight.CommonConfirmDialog;
import com.ycf.qianzhihe.common.interfaceOrImplement.OnResourceParseCallback;
import com.ycf.qianzhihe.section.account.viewmodels.SplashViewModel;
import com.ycf.qianzhihe.section.dialog.UserProtocolDialog;
import com.zds.base.global.BaseConstant;
import com.zds.base.util.Preference;

import java.io.FileDescriptor;
import java.io.IOException;


public class SplashActivity extends BaseInitActivity {
    private ImageView ivSplash;
    private TextView tvProduct;
    private SplashViewModel model;
    private UserProtocolDialog mUserProtocolDialog;
    private CommonConfirmDialog mCommonConfirmDialog;

    private LottieAnimationView mAnim;

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
        mAnim = findViewById(R.id.anim);
        initMedia();
    }

    @Override
    protected void initData() {
        super.initData();
        model = new ViewModelProvider(this).get(SplashViewModel.class);

        mAnim.setVisibility(View.VISIBLE);
        mAnim.setImageAssetsFolder("/lp/");
        mAnim.setAnimation("logo.json");
        mAnim.addAnimatorListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mAnim.removeAnimatorListener(this);
                mAnim.setVisibility(View.GONE);
                alphaSplash();

            }
        });
        mAnim.playAnimation();


    }

    private void initMedia() {
        MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.qzh);
        //用prepare方法，会报错误java.lang.IllegalStateExceptio
        //mediaPlayer.prepare();
        mediaPlayer.start();
    }

    private void alphaSplash() {
        Animation alphaAnimation = AnimationUtils.loadAnimation(this,
                R.anim.splash_alpha_in);
        alphaAnimation.setFillEnabled(true);//启动Fill保持
        alphaAnimation.setFillAfter(true);//设置动画的最后一帧是保留在view上的
        alphaAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                ivSplash.setVisibility(View.VISIBLE);
                tvProduct.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                userProtocolDialog();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        ivSplash.setAnimation(alphaAnimation);
        tvProduct.setAnimation(alphaAnimation);

        alphaAnimation.start();

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
                    LoginActivity.actionStart(mContext, "");
                    finish();
                }
            });

        });
    }
}
