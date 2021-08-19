package con.ycf.qianzhihe;

import android.content.Context;
import android.os.Build;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.multidex.MultiDex;

import com.baidu.mapapi.cloud.VersionInfo;
import con.ycf.qianzhihe.R;
import con.zds.base.SelfAppContext;
import con.zds.base.upDated.utils.CretinAutoUpdateUtils;
import con.ycf.qianzhihe.app.api.Constant;
import con.ycf.qianzhihe.app.api.global.UserComm;
import con.ycf.qianzhihe.app.api.old_http.AppConfig;
import con.ycf.qianzhihe.app.utils.my.MyHelper;
import con.ycf.qianzhihe.app.weight.CommonDialog;
import con.ycf.qianzhihe.common.interfaceOrImplement.UserActivityLifecycleCallbacks;
import con.ycf.qianzhihe.common.utils.PreferenceManager;
import com.hyphenate.util.EMLog;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.DefaultRefreshFooterCreator;
import com.scwang.smartrefresh.layout.api.DefaultRefreshHeaderCreator;
import com.scwang.smartrefresh.layout.api.RefreshFooter;
import com.scwang.smartrefresh.layout.api.RefreshHeader;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.constant.SpinnerStyle;
import com.scwang.smartrefresh.layout.footer.ClassicsFooter;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class DemoApplication extends SelfAppContext implements Thread.UncaughtExceptionHandler {
    private static DemoApplication instance;
    private UserActivityLifecycleCallbacks mLifecycleCallbacks = new UserActivityLifecycleCallbacks();

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        initThrowableHandler();

        initApp();

        initHx();
        registerActivityLifecycleCallbacks();
        closeAndroidPDialog();
    }

    private void initApp() {
        MyHelper.getInstance().init(getApplicationContext());
        registerWx();
        CretinAutoUpdateUtils.Builder builder = new CretinAutoUpdateUtils.Builder()
                //设置更新api
                .setBaseUrl(AppConfig.checkVersion)
                //设置是否显示忽略此版本
                .setIgnoreThisVersion(true)
                //设置下载显示形式 对话框或者通知栏显示 二选一
                .setShowType(CretinAutoUpdateUtils.Builder.TYPE_DIALOG_WITH_PROGRESS)
                //设置下载时展示的图标
                .setIconRes(R.mipmap.ic_launcher)
                //设置是否打印log日志
                .showLog(true)
                //设置请求方式
                .setRequestMethod(CretinAutoUpdateUtils.Builder.METHOD_GET)
                //设置下载时展示的应用名称
                .setAppName(getResources().getString(R.string.app_name))
                //设置自定义的Model类
                .setTransition(new VersionInfo())
                .build();
        CretinAutoUpdateUtils.init(builder);

        UserComm.init();
    }

    private void initThrowableHandler() {
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    private void initHx() {
        // 初始化PreferenceManager
        PreferenceManager.init(this);
        // init hx sdk
        if(DemoHelper.getInstance().getAutoLogin()) {
            EMLog.i("DemoApplication", "application initHx");
            DemoHelper.getInstance().init(this);
        }

    }
    private IWXAPI mIWXAPI;

    public IWXAPI registerWx() {
        mIWXAPI = WXAPIFactory.createWXAPI(this, Constant.WXAPPID, true);
        mIWXAPI.registerApp(Constant.WXAPPID);
        return mIWXAPI;
    }

    private void registerActivityLifecycleCallbacks() {
        this.registerActivityLifecycleCallbacks(mLifecycleCallbacks);
    }

    public static DemoApplication getInstance() {
        return instance;
    }

    public UserActivityLifecycleCallbacks getLifecycleCallbacks() {
        return mLifecycleCallbacks;
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    static {
        //设置全局的Header构建器
        SmartRefreshLayout.setDefaultRefreshHeaderCreator(new DefaultRefreshHeaderCreator() {
            @Override
            public RefreshHeader createRefreshHeader(Context context, RefreshLayout layout) {
                return new ClassicsHeader(context).setSpinnerStyle(SpinnerStyle.Translate);//指定为经典Header，默认是 贝塞尔雷达Header
            }
        });
        //设置全局的Footer构建器
        SmartRefreshLayout.setDefaultRefreshFooterCreator(new DefaultRefreshFooterCreator() {
            @Override
            public RefreshFooter createRefreshFooter(Context context, RefreshLayout layout) {
                //指定为经典Footer，默认是 BallPulseFooter
                return new ClassicsFooter(context).setSpinnerStyle(SpinnerStyle.Translate);
            }
        });
    }

    /**
     * 为了兼容5.0以下使用vector图标
     */
    static {
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        }
    }

    @Override
    public void uncaughtException(@NonNull Thread t, @NonNull Throwable e) {
        EMLog.e("demoApp", e.getMessage());

    }

    /**
     * 解决androidP 第一次打开程序出现莫名弹窗
     * 弹窗内容“detected problems with api ”
     */
    private void closeAndroidPDialog(){
        if(Build.VERSION.SDK_INT == Build.VERSION_CODES.P) {
            try {
                Class aClass = Class.forName("android.content.pm.PackageParser$Package");
                Constructor declaredConstructor = aClass.getDeclaredConstructor(String.class);
                declaredConstructor.setAccessible(true);
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                Class cls = Class.forName("android.app.ActivityThread");
                Method declaredMethod = cls.getDeclaredMethod("currentActivityThread");
                declaredMethod.setAccessible(true);
                Object activityThread = declaredMethod.invoke(null);
                Field mHiddenApiWarningShown = cls.getClass().getDeclaredField("mHiddenApiWarningShown");
                mHiddenApiWarningShown.setAccessible(true);
                mHiddenApiWarningShown.setBoolean(activityThread, true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private CommonDialog commonDialog;

    /**
     * 分享
     */
    public void shareDialog(Context context, final View.OnClickListener onClickListener) {
        //分享弹窗
        if (commonDialog != null && commonDialog.isShowing()) {
            commonDialog.dismiss();
        }
        commonDialog = new CommonDialog.Builder(context).setView(R.layout.share_dialog).fromBottom().fullWidth().loadAniamtion()
                .setOnClickListener(R.id.wx_chat, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickListener.onClick(view);
                        if (commonDialog != null && commonDialog.isShowing()) {
                            commonDialog.dismiss();
                        }
                    }
                })
                .setOnClickListener(R.id.wx_qun, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        onClickListener.onClick(view);
                        if (commonDialog != null && commonDialog.isShowing()) {
                            commonDialog.dismiss();
                        }
                    }
                }).setOnClickListener(R.id.tv_cell, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (commonDialog != null && commonDialog.isShowing()) {
                            commonDialog.dismiss();
                        }
                    }
                }).create();
        commonDialog.show();
    }
}
