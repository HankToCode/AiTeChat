package com.hyphenate.easeim.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Switch;

import com.hyphenate.easeim.R;
import com.hyphenate.easeim.app.api.global.UserComm;
import com.hyphenate.easeim.app.api.old_data.EventCenter;
import com.hyphenate.easeim.app.api.old_http.ApiClient;
import com.hyphenate.easeim.app.api.old_http.AppConfig;
import com.hyphenate.easeim.app.api.old_http.ResultListener;
import com.hyphenate.easeim.app.base.BaseInitActivity;
import com.hyphenate.easeim.app.weight.CommonDialog;
import com.hyphenate.easeim.app.weight.VerifyCodeView;
import com.zds.base.Toast.ToastUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
//零钱锁
public class WalletLockActivity extends BaseInitActivity {
    @BindView(R.id.switch_lock)
    Switch switchLock;
    private VerifyCodeView verifyCodeView;
    private CommonDialog verifyDialog;
    private boolean openLock = false;


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, BankActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wallet_lock;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setTitle("零钱锁");
        verifyCodeView = new VerifyCodeView(this);
        verifyDialog  = new CommonDialog.Builder(this).fullWidth().center()
                .setView(verifyCodeView)
                .loadAniamtion()
                .create();
        verifyCodeView.setPhone(UserComm.getUserInfo().getPhone().replaceAll("(\\d{3})\\d{4}(\\d{4})","$1****$2"));
        verifyCodeView.setVerifyCodeListener(new VerifyCodeView.OnVerifyCodeListener() {
            @Override
            public void inputComplete(String code) {
                verifyDialog.cancel();
                verifyCodeView.reset();
                if (!openLock) {
                    openLock(code);
                }else {
                    closeLock(code);
                }
            }
            @Override
            public void invalidContent() { }
            @Override
            public void getCode() {
                getSMSCode();
            }

            @Override
            public void close() {
                verifyDialog.dismiss();
                setLockStatus(openLock);
            }
        });

        checkLockStatus();
    }


    public void setLockStatus(boolean isOpen) {
        switchLock.setOnCheckedChangeListener(null);
        switchLock.setChecked(isOpen);

        switchLock.setOnCheckedChangeListener((buttonView, isChecked) -> {
            verifyDialog.show();
        });
    }
    public void checkLockStatus() {
        Map<String,Object> map =new HashMap<>();
        ApiClient.requestNetHandle(this, AppConfig.walletLockStatus, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                openLock = "1".equals(json);
                setLockStatus(openLock);
            }
            @Override
            public void onFailure(String msg) {
                setLockStatus(false);
            }
        });
    }
    public void getSMSCode(){
        Map<String,Object> map =new HashMap<>();
        ApiClient.requestNetHandle(this, AppConfig.walletLockGetCode, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast(msg);
            }
            @Override
            public void onFailure(String msg) {

            }
        });
    }
    public void openLock(String code){
        Map<String,Object> map =new HashMap<>();
        map.put("authCode", code);
        ApiClient.requestNetHandle(this, AppConfig.walletLockOpen, "零钱锁开启中", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast("零钱锁开启成功");
                openLock = true;
                setLockStatus(openLock);
                verifyCodeView.stopTimer();
            }
            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                openLock = false;
                setLockStatus(openLock);

            }
        });
    }
     public void closeLock(String code){
        Map<String,Object> map =new HashMap<>();
        map.put("authCode", code);
        ApiClient.requestNetHandle(this, AppConfig.walletLockClose, "零钱锁关闭中", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast("零钱锁关闭成功");
                openLock = false;
                setLockStatus(openLock);
                verifyCodeView.stopTimer();
            }
            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                openLock = true;
                setLockStatus(openLock);

            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        verifyCodeView.stopTimer();
    }
}
