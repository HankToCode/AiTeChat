package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.base.WebViewActivity;
import com.ycf.qianzhihe.app.utils.my.MyHelper;
import com.ycf.qianzhihe.common.widget.LogoffDialog;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.util.DataCleanManager;
import com.zds.base.util.StringUtil;
import com.zds.base.util.SystemUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class SetActivity extends BaseInitActivity {

    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.tv_save)
    TextView tv_save;
    @BindView(R.id.tv_privacy)
    TextView tv_privacy;
    @BindView(R.id.ll_clean)
    LinearLayout ll_clean;
    @BindView(R.id.ll_clean_message)
    LinearLayout ll_clean_message;
    @BindView(R.id.tv_m)
    TextView tv_m;
    @BindView(R.id.ll_banben)
    LinearLayout ll_banben;
    @BindView(R.id.tv_version)
    TextView tv_version;
    @BindView(R.id.tv_user_agreement)
    TextView tv_user_agreement;
    @BindView(R.id.tv_register_agreement)
    TextView tv_register_agreement;
    @BindView(R.id.tv_app)
    TextView tv_app;


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, SetActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_set;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar.setTitle("设置");
        mTitleBar.setOnBackPressListener(view -> finish());

        tv_version.setText("当前版本：v" + SystemUtil.getAppVersionName());

        try {
            tv_m.setText(StringUtil.isEmpty(DataCleanManager.getTotalCacheSize(this)) ? "" : DataCleanManager.getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @OnClick({R.id.tv_save, R.id.tv_privacy, R.id.ll_clean, R.id.ll_clean_message, R.id.ll_banben, R.id.tv_user_agreement,
            R.id.tv_register_agreement, R.id.tv_logoff, R.id.tv_app})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.tv_save:
                AccountSafeActivity.actionStart(this);
                break;
            case R.id.tv_privacy:
                PrivacyActivity.actionStart(this);
                break;
            case R.id.ll_clean:
                DataCleanManager.clearAllCache(this);

                ToastUtil.toast("清理完成");
                tv_m.setText("");

                break;

            case R.id.ll_clean_message:

                /*for (EMConversation emConversation : EMClient.getInstance().chatManager().getAllConversations().values()) {
                    emConversation.clearAllMessages();
                }
                EMClient.getInstance().chatManager().loadAllConversations();
                ToastUtil.toast("清理成功");*/

                Observable.just("").map(str -> EMClient.getInstance().chatManager().getAllConversations().values()).subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .as(autoDispose())
                        .subscribe(new Observer<Collection<EMConversation>>() {
                            @Override
                            public void onSubscribe(@NonNull Disposable d) {

                            }

                            @Override
                            public void onNext(@NonNull Collection<EMConversation> o) {
                                for (EMConversation emConversation : o) {
                                    emConversation.clearAllMessages();
                                }
                                EMClient.getInstance().chatManager().loadAllConversations();
                                ToastUtil.toast("清理成功");
                            }

                            @Override
                            public void onError(@NonNull Throwable e) {
                                ToastUtil.toast("清理失败");
                            }

                            @Override
                            public void onComplete() {

                            }
                        });

                break;
            case R.id.ll_banben:
//                    AppConfig.checkVersion(SetActivity.this, false);
                break;
            case R.id.tv_user_agreement:
//                startActivity(new Intent(this, WebViewActivity.class).putExtra("title", "lan").putExtra("url", AppConfig.user_agree));
                WebViewActivity.actionStart(mContext, AppConfig.user_agree, true);
                break;
            case R.id.tv_register_agreement:
//                startActivity(new Intent(this, WebViewActivity.class).putExtra("title", "lan").putExtra("url", AppConfig.register_agree));
                WebViewActivity.actionStart(mContext, AppConfig.register_agree, true);
                break;
            case R.id.tv_app:
                WebViewActivity.actionStart(mContext, AppConfig.appurl, true);
//                startActivity(new Intent(this, WebViewActivity.class).putExtra("title", "官网").putExtra("url", AppConfig.appurl));
                break;
            case R.id.tv_logoff:
                //注销账号
                /*new EaseAlertDialog(this, "确定注销帐号？", null, null, new EaseAlertDialog.AlertDialogUser() {
                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if (confirmed) {
                            logoff();
                        }
                    }
                }).show();*/
                showLogoffDialog();
                break;
        }
    }

    private LogoffDialog mLogoffDialog;

    private void showLogoffDialog() {
        if (mLogoffDialog == null) {
            mLogoffDialog = new LogoffDialog(this);
            mLogoffDialog.setOnConfirmClickListener(new LogoffDialog.OnConfirmClickListener() {
                @Override
                public void onConfirmClick(View view) {
                    logoff();
                }
            });
        }
        mLogoffDialog.show();
    }

    private void logoff() {
        Map<String, Object> map = new HashMap<>();
        map.put("userId", UserComm.getUserInfo().getUserId());
        ApiClient.requestNetHandle(SetActivity.this, AppConfig.toLogoff, "请稍候...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                //关闭当前所有页面并跳转到登录页面
                ToastUtil.toast("注销成功");
                EventBus.getDefault().post(new EventCenter(EventUtil.LOSETOKEN, "关闭"));

                MyHelper.getInstance().logout(false, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        /*runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                SetActivity.this.finish();
                            }
                        });*/
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }

                    @Override
                    public void onError(int code, String message) {
                        runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                // TODO Auto-generated method stub
                                //dismissLoading();
                                Toast.makeText(SetActivity.this, "退出环信失败", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);

            }
        });
    }

}
