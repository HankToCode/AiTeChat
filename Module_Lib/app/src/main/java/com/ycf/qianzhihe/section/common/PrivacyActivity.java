package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.R2;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.utils.my.MyHelper;
import com.ycf.qianzhihe.app.utils.my.MyModel;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.ycf.qianzhihe.section.contact.activity.ContactBlackListActivity;
import com.zds.base.Toast.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class PrivacyActivity extends BaseInitActivity {

    @BindView(R2.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R2.id.switch_btn)
    Switch switchBtn;
    private MyModel settingsModel;

    @BindView(R2.id.sb_verify)
    Switch sb_verify;
    private LoginInfo info;
    @BindView(R2.id.tv_black)
    TextView tv_black;


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, PrivacyActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_privacy;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar.setTitle("隐私");
        mTitleBar.setOnBackPressListener(view -> finish());
        settingsModel = MyHelper.getInstance().getModel();

        info = UserComm.getUserInfo();

        sb_verify.setChecked(info.addWay == 0);
        sb_verify.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Map<String,Object> map =new HashMap<>();
                map.put("addWay",isChecked ? 0 : 1);
                ApiClient.requestNetHandle(PrivacyActivity.this, AppConfig.MODIFY_FRIEND_CONSENT + "/" + (isChecked ? 0 : 1), "请稍候...", map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        LoginInfo loginInfo = UserComm.getUserInfo();
                        loginInfo.addWay = isChecked ? 0 : 1;
                        UserComm.saveUsersInfo(loginInfo);
                        ToastUtil.toast("修改成功");
                    }
                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);

                    }
                });
            }
        });

        if (settingsModel.getSettingMsgNotification()) {
            switchBtn.setChecked(true);
        } else {
            switchBtn.setChecked(false);
        }
        switchBtn.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    settingsModel.setSettingMsgNotification(true);
                    settingsModel.setSettingMsgVibrate(true);
                    settingsModel.setSettingMsgSound(true);

                } else {
                    settingsModel.setSettingMsgNotification(false);
                    settingsModel.setSettingMsgVibrate(false);
                    settingsModel.setSettingMsgSound(false);
                }
            }
        });

    }

    @OnClick({R2.id.tv_black})
    public void click(View v) {
        if (v.getId() == R.id.tv_black) {
            BlackListActivity.actionStart(mContext);
        }
    }




}
