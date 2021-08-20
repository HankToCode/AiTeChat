package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.base.WebViewActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.zds.base.util.DataCleanManager;
import com.zds.base.util.StringUtil;
import com.zds.base.util.SystemUtil;

import butterknife.BindView;
import butterknife.OnClick;
import com.zds.base.Toast.ToastUtil;

public class SetActivity extends BaseInitActivity {

    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.tv_save)
    TextView tv_save;
    @BindView(R.id.tv_privacy)
    TextView tv_privacy;
    @BindView(R.id.ll_clean)
    LinearLayout ll_clean;
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

    @OnClick({R.id.tv_save, R.id.tv_privacy, R.id.ll_clean, R.id.ll_banben, R.id.tv_user_agreement, R.id.tv_register_agreement})
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
            case R.id.ll_banben:
//                    AppConfig.checkVersion(SetActivity.this, false);
                break;
            case R.id.tv_user_agreement:
                startActivity(new Intent(this, WebViewActivity.class).putExtra("title", "lan").putExtra("url", AppConfig.user_agree));
                break;
            case R.id.tv_register_agreement:
                startActivity(new Intent(this, WebViewActivity.class).putExtra("title", "lan").putExtra("url", AppConfig.register_agree));
                break;
        }
    }

}
