package com.android.nanguo.app.base;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.ColorRes;
import androidx.annotation.LayoutRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Lifecycle;

import com.gyf.immersionbar.ImmersionBar;
import com.hyphenate.EMCallBack;
import com.android.nanguo.DemoApplication;
import com.android.nanguo.DemoHelper;
import com.android.nanguo.R;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.utils.sound.SoundMediaPlayer;
import com.android.nanguo.common.constant.DemoConstant;
import com.android.nanguo.common.enums.Status;
import com.android.nanguo.common.interfaceOrImplement.OnResourceParseCallback;
import com.android.nanguo.common.interfaceOrImplement.UserActivityLifecycleCallbacks;
import com.android.nanguo.common.livedatas.LiveDataBus;
import com.android.nanguo.common.net.Resource;
import com.android.nanguo.common.utils.ToastUtils;
import com.android.nanguo.common.widget.EaseProgressDialog;
import com.android.nanguo.section.account.activity.LoginActivity;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.utils.StatusBarCompat;
import com.hyphenate.util.EMLog;
import com.lzy.okgo.OkGo;
import com.uber.autodispose.AutoDispose;
import com.uber.autodispose.AutoDisposeConverter;
import com.uber.autodispose.android.lifecycle.AndroidLifecycleScopeProvider;
import com.uber.autodispose.lifecycle.LifecycleScopeProvider;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.lang.ref.WeakReference;
import java.util.List;

import butterknife.ButterKnife;

/**
 * 作为基础activity,放置一些公共的方法
 */
public class BaseActivity extends AppCompatActivity {
    public BaseActivity mContext;
    private EaseProgressDialog dialog;
    private AlertDialog logoutDialog;
    private long dialogCreateTime;//dialog生成事件，用以判断dialog的展示时间
    private Handler handler = new Handler();//用于dialog延迟消失

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        clearFragmentsBeforeCreate();
        registerAccountObservable();

        EventBus.getDefault().register(this);
        ActivityStackManager.getInstance().addActivity(new WeakReference<>(this));
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);

    }

    @Override
    public void setContentView(@LayoutRes int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view) {
        super.setContentView(view);
        ButterKnife.bind(this);
    }

    @Override
    public void setContentView(View view, ViewGroup.LayoutParams params) {
        super.setContentView(view, params);
        ButterKnife.bind(this);
    }


    /**
     * 添加账号异常监听
     */
    protected void registerAccountObservable() {
        LiveDataBus.get().with(DemoConstant.ACCOUNT_CHANGE, EaseEvent.class).observe(this, event -> {
            if (event == null) {
                return;
            }
            if (!event.isAccountChange()) {
                return;
            }
            String accountEvent = event.event;
            if (TextUtils.equals(accountEvent, DemoConstant.ACCOUNT_REMOVED) ||
                    TextUtils.equals(accountEvent, DemoConstant.ACCOUNT_KICKED_BY_CHANGE_PASSWORD) ||
                    TextUtils.equals(accountEvent, DemoConstant.ACCOUNT_KICKED_BY_OTHER_DEVICE)) {
                DemoHelper.getInstance().logout(false, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        finishOtherActivities();
//                        startActivity(new Intent(mContext, LoginActivity.class));
                        LoginActivity.actionStart(mContext);
                        finish();
                    }

                    @Override
                    public void onError(int code, String error) {
                        EMLog.e("logout", "logout error: error code = " + code + " error message = " + error);
                        showToast("logout error: error code = " + code + " error message = " + error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            } else if (TextUtils.equals(accountEvent, DemoConstant.ACCOUNT_CONFLICT)
                    || TextUtils.equals(accountEvent, DemoConstant.ACCOUNT_REMOVED)
                    || TextUtils.equals(accountEvent, DemoConstant.ACCOUNT_FORBIDDEN)) {
                DemoHelper.getInstance().logout(false, null);
                showExceptionDialog(accountEvent);
            }
        });
    }

    private void showExceptionDialog(String accountEvent) {
        if (mContext.isFinishing()) {
            return;
        }
        if (logoutDialog != null && logoutDialog.isShowing() && !mContext.isFinishing()) {
            logoutDialog.dismiss();
        }
        logoutDialog = new AlertDialog.Builder(mContext)
                .setTitle(R.string.em_account_logoff_notification)
                .setMessage(getExceptionMessageId(accountEvent))
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finishOtherActivities();
//                        startActivity(new Intent(mContext, LoginActivity.class));
                        LoginActivity.actionStart(mContext);
                        finish();
                    }
                })
                .setCancelable(false)
                .create();
        logoutDialog.show();
    }

    private int getExceptionMessageId(String exceptionType) {
        if (exceptionType.equals(DemoConstant.ACCOUNT_CONFLICT)) {
            return R.string.em_account_connect_conflict;
        } else if (exceptionType.equals(DemoConstant.ACCOUNT_REMOVED)) {
            return R.string.em_account_user_remove;
        } else if (exceptionType.equals(DemoConstant.ACCOUNT_FORBIDDEN)) {
            return R.string.em_account_user_forbidden;
        }
        return R.string.Network_error;
    }

    /**
     * 结束除了当前Activity外的其他Activity
     */
    protected void finishOtherActivities() {
        UserActivityLifecycleCallbacks lifecycleCallbacks = DemoApplication.getInstance().getLifecycleCallbacks();
        if (lifecycleCallbacks == null) {
            finish();
            return;
        }
        List<Activity> activities = lifecycleCallbacks.getActivityList();
        if (activities == null || activities.isEmpty()) {
            finish();
            return;
        }
        for (Activity activity : activities) {
            if (activity != lifecycleCallbacks.current()) {
                activity.finish();
            }
        }
    }


    /**
     * 初始化toolbar
     *
     * @param toolbar
     */
    public void initToolBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);//有返回
            getSupportActionBar().setDisplayShowTitleEnabled(false);//不显示title
        }
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

    }

    @Override
    public void onBackPressed() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
        super.onBackPressed();

    }

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    protected void onEventComing(EventCenter center) {

    }

    /**
     * EventBus接收消息
     *
     * @param center 消息接收
     */
    @Subscribe
    public void onEventMainThread(EventCenter center) {
        if (null != center) {
            onEventComing(center);
        }

    }

    @Override
    protected void onDestroy() {
        EventBus.getDefault().unregister(this);
        ActivityStackManager.getInstance().removeActivity(new WeakReference<Activity>(this));
        OkGo.getInstance().cancelTag(this);
        dismissLoading();
        SoundMediaPlayer.getInstance().destroy();
        super.onDestroy();

    }

    /**
     * hide keyboard
     */
    public void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null && getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * toast by string
     *
     * @param message
     */
    public void showToast(String message) {
        ToastUtils.showToast(message);
    }

    /**
     * toast by string res
     *
     * @param messageId
     */
    public void showToast(@StringRes int messageId) {
        ToastUtils.showToast(messageId);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (null != this.getCurrentFocus()) {
            /**
             * 点击空白位置 隐藏软键盘
             */
            InputMethodManager mInputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
        }

        return super.onTouchEvent(event);
    }


    /**
     * 通用页面设置
     */
    public void setFitSystemForTheme() {
        setFitSystemForTheme(true, R.color.white);
        setStatusBarTextColor(true);
    }

    /**
     * 通用页面，需要设置沉浸式
     *
     * @param fitSystemForTheme
     */
    public void setFitSystemForTheme(boolean fitSystemForTheme) {
        setFitSystemForTheme(fitSystemForTheme, R.color.white);
        setStatusBarTextColor(false);
    }

    /**
     * 通用页面，需要设置沉浸式
     *
     * @param fitSystemForTheme
     */
    public void setFitSystemForTheme2(boolean fitSystemForTheme) {
        setFitSystemForTheme(fitSystemForTheme, "#ffffffff");
        setStatusBarTextColor(true);
    }

    /**
     * 设置是否是沉浸式，并可设置状态栏颜色
     *
     * @param fitSystemForTheme
     * @param colorId           颜色资源路径
     */
    public void setFitSystemForTheme(boolean fitSystemForTheme, @ColorRes int colorId) {
        setFitSystem(fitSystemForTheme);
        //初始设置
        StatusBarCompat.compat(this, ContextCompat.getColor(mContext, colorId));
    }

    /**
     * 修改状态栏文字颜色
     *
     * @param isLight 是否是浅色字体
     */
    public void setStatusBarTextColor(boolean isLight) {
        StatusBarCompat.setLightStatusBar(mContext, !isLight);
    }


    /**
     * 设置是否是沉浸式，并可设置状态栏颜色
     *
     * @param fitSystemForTheme true 不是沉浸式
     * @param color             状态栏颜色
     */
    public void setFitSystemForTheme(boolean fitSystemForTheme, String color) {
        setFitSystem(fitSystemForTheme);
        //初始设置
        StatusBarCompat.compat(mContext, Color.parseColor(color));
    }

    /**
     * 设置是否是沉浸式
     *
     * @param fitSystemForTheme
     */
    public void setFitSystem(boolean fitSystemForTheme) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT && Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
        if (fitSystemForTheme) {
            ViewGroup contentFrameLayout = (ViewGroup) findViewById(Window.ID_ANDROID_CONTENT);
            View parentView = contentFrameLayout.getChildAt(0);
            if (parentView != null && Build.VERSION.SDK_INT >= 14) {
                parentView.setFitsSystemWindows(true);
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        }
    }

    /**
     * 解析Resource<T>
     *
     * @param response
     * @param callback
     * @param <T>
     */
    public <T> void parseResource(Resource<T> response, @NonNull OnResourceParseCallback<T> callback) {
        if (response == null) {
            return;
        }
        if (response.status == Status.SUCCESS) {
            callback.hideLoading();
            callback.onSuccess(response.data);
        } else if (response.status == Status.ERROR) {
            callback.hideLoading();
            if (!callback.hideErrorMsg) {
                showToast(response.getMessage());
            }
            callback.onError(response.errorCode, response.getMessage());
        } else if (response.status == Status.LOADING) {
            callback.onLoading(response.data);
        }
    }

    public boolean isMessageChange(String message) {
        if (TextUtils.isEmpty(message)) {
            return false;
        }
        if (message.contains("message")) {
            return true;
        }
        return false;
    }

    public boolean isContactChange(String message) {
        if (TextUtils.isEmpty(message)) {
            return false;
        }
        if (message.contains("contact")) {
            return true;
        }
        return false;
    }

    public boolean isGroupInviteChange(String message) {
        if (TextUtils.isEmpty(message)) {
            return false;
        }
        if (message.contains("invite")) {
            return true;
        }
        return false;
    }

    public boolean isNotify(String message) {
        if (TextUtils.isEmpty(message)) {
            return false;
        }
        if (message.contains("invite")) {
            return true;
        }
        return false;
    }

    public void showLoading() {
        showLoading(getString(R.string.loading));
    }

    public void showLoading(String message) {
        if (mContext.isFinishing()) {
            return;
        }
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
        dialogCreateTime = System.currentTimeMillis();
        dialog = new EaseProgressDialog.Builder(mContext)
                .setLoadingMessage(message)
                .setCancelable(true)
                .setCanceledOnTouchOutside(true)
                .show();
    }

    public void dismissLoading() {
        if (!mContext.isFinishing() && dialog != null && dialog.isShowing()) {
            //如果dialog的展示时间过短，则延迟1s再消失
            if (System.currentTimeMillis() - dialogCreateTime < 500 && !isFinishing()) {
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (dialog != null && dialog.isShowing()) {
                            dialog.dismiss();
                            dialog = null;
                        }
                    }
                }, 1000);
            } else {
                dialog.dismiss();
                dialog = null;
            }

        }
    }

    protected void initImmersionBar(boolean isStatusBarDartFont) {
        initImmersionBar(isStatusBarDartFont, false);
    }

    protected void initImmersionBar(boolean isStatusBarDartFont, boolean isKeyboardEnable) {
        //初始化，默认透明状态栏和黑色导航栏。
        ImmersionBar.with(this)
                .keyboardEnable(isKeyboardEnable)
                //原理：如果当前设备支持状态栏字体变色，会设置状态栏字体为黑色，如果当前设备不支持状态栏字体变色，会使当前状态栏加上透明度，否则不执行透明度
                .statusBarDarkFont(isStatusBarDartFont, 0.2f)
                .fullScreen(false)
                //.navigationBarColor("#E9E9E9")
                //采用系统默认导航栏颜色
                .navigationBarEnable(false)
                .init();//有时需要直接由子类实现该功能
    }

    /**
     * 处理因为Activity重建导致的fragment叠加问题
     */
    public void clearFragmentsBeforeCreate() {
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments.size() == 0) {
            return;
        }
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        for (Fragment fragment : fragments) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.commitNow();
    }


    public LifecycleScopeProvider<Lifecycle.Event> getScopeProvider() {
        return AndroidLifecycleScopeProvider.from(this, Lifecycle.Event.ON_DESTROY);
    }

    public <Bean> AutoDisposeConverter<Bean> autoDispose() {
        return AutoDispose.autoDisposable(getScopeProvider());
    }

    public <Bean> AutoDisposeConverter<Bean> autoDispose(Lifecycle.Event event) {
        return AutoDispose.autoDisposable(AndroidLifecycleScopeProvider.from(this, event));
    }

    protected InputMethodManager inputMethodManager;

    protected void hideSoftKeyboard() {
        if (getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }
}
