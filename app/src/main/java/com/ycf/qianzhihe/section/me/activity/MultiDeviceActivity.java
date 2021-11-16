package com.ycf.qianzhihe.section.me.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMDeviceInfo;
import com.ycf.qianzhihe.DemoHelper;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.adapter.MultiDeviceAdapter;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.domain.MultiDevice;
import com.ycf.qianzhihe.app.help.RclViewHelp;
import com.ycf.qianzhihe.app.weight.CommonDialog;
import com.ycf.qianzhihe.app.weight.VerifyCodeView;
import com.ycf.qianzhihe.common.model.DemoModel;

import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.StringUtil;
import com.zds.base.util.SystemUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class MultiDeviceActivity extends BaseInitActivity {

    @BindView(R.id.title_bar)
    EaseTitleBar title_bar;
    @BindView(R.id.switch_multi)
    Switch switchMulti;
    @BindView(R.id.mobile)
    TextView mobile;
    @BindView(R.id.ip)
    TextView ip;
    @BindView(R.id.address)
    TextView address;
    @BindView(R.id.time)
    TextView time;
    @BindView(R.id.kick_out)
    TextView kickOut;
    @BindView(R.id.recycler_view)
    RecyclerView recyclerView;
    MultiDeviceAdapter adapter;
    private VerifyCodeView verifyCodeView;
    private CommonDialog verifyDialog;

    private MultiDevice multiDevice;
    public boolean openMultiDeviceStatus;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MultiDeviceActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_multi_device;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
//        title_bar.setTitle();
        title_bar.setOnBackPressListener(view -> finish());

        mobile.setText(SystemUtil.getDeviceManufacturer() + " " + SystemUtil.getSystemModel());

        verifyCodeView = new VerifyCodeView(this);
        verifyDialog = new CommonDialog.Builder(this).fullWidth().center()
                .setView(verifyCodeView)
                .loadAniamtion()
                .create();
        verifyCodeView.setPhone(UserComm.getUserInfo().getAccount().replaceAll("(\\d{3})\\d{4}(\\d{4})", "$1****$2"));


        verifyCodeView.setVerifyCodeListener(new VerifyCodeView.OnVerifyCodeListener() {
            @Override
            public void inputComplete(String code) {
                verifyDialog.cancel();
                verifyCodeView.reset();

                if (!openMultiDeviceStatus) {
                    openMultiDevice(code);
                } else {
                    closeMultiDevice(code);
                }
            }

            @Override
            public void invalidContent() {

            }

            @Override
            public void getCode() {
                getSMSCode();
            }

            @Override
            public void close() {
                verifyDialog.dismiss();
                setMultiStatus(openMultiDeviceStatus);
            }
        });
        checkMultiStatus();
        getMultiDevices();
    }
    public void checkMultiStatus() {
        Map<String,Object> map =new HashMap<>();
        ApiClient.requestNetHandle(this, AppConfig.isOpenMultiDevice, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                openMultiDeviceStatus = "1".equals(json);
                setMultiStatus(openMultiDeviceStatus);
            }
            @Override
            public void onFailure(String msg) {
                setMultiStatus(false);
            }
        });
    }


    public void getMultiDevices() {
        Map<String,Object> map =new HashMap<>();
        ApiClient.requestNetHandle(this, AppConfig.getDeviceList, "请稍候", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                multiDevice =  FastJsonUtil.getObject(json, MultiDevice.class);
                setData();
            }
            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    public void setData() {
        mobile.setText(multiDevice.getDname());
        time.setText("登录时间: "+ StringUtil.formatTime(multiDevice.getUt(),"yyyy-MM-dd HH:mm:ss"));
        address.setText("登录地点:  "+multiDevice.getAddress());
        ip.setText("IP地址:  "+ multiDevice.getIp());
        adapter = new MultiDeviceAdapter(multiDevice.getDevList());
        RclViewHelp.initRcLmVertical(this, recyclerView, adapter);
    }

    public void openMultiDevice(String code){
        Map<String,Object> map =new HashMap<>();
        map.put("authCode", code);
        ApiClient.requestNetHandle(this, AppConfig.openMultiDevice, "多设备登录开启中", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast("多设备登录开启成功");
                openMultiDeviceStatus = true;
                setMultiStatus(openMultiDeviceStatus);
                verifyCodeView.stopTimer();
            }
            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                openMultiDeviceStatus = false;
                setMultiStatus(openMultiDeviceStatus);
                verifyCodeView.stopTimer();
            }
        });
    }

    public void closeMultiDevice(String code){
        Map<String,Object> map =new HashMap<>();
        map.put("authCode", code);
        ApiClient.requestNetHandle(this, AppConfig.stopMultiDevice, "多设备登录关闭中", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast("多设备登录关闭成功");
                openMultiDeviceStatus = false;
                setMultiStatus(openMultiDeviceStatus);
                verifyCodeView.stopTimer();
            }
            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                openMultiDeviceStatus = true;
                setMultiStatus(openMultiDeviceStatus);
                verifyCodeView.stopTimer();

            }
        });
    }

    public void setMultiStatus(boolean isOpen) {
        switchMulti.setOnCheckedChangeListener(null);
        switchMulti.setChecked(isOpen);

        switchMulti.setOnCheckedChangeListener((buttonView, isChecked) -> {
            verifyDialog.show();
        });
    }

    public void getSMSCode(){
        Map<String,Object> map =new HashMap<>();
        ApiClient.requestNetHandle(this, AppConfig.sendMultiDeviceCode, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast(msg);
            }
            @Override
            public void onFailure(String msg) {

            }
        });
    }


    @Override
    protected void initListener() {
        super.initListener();

    }

    @Override
    protected void initData() {
        super.initData();

    }




    private int getDeviceTypeIcon(String deviceResource) {
        if(TextUtils.isEmpty(deviceResource) || !deviceResource.contains("_")) {
            return 0;
        }
        String deviceType = deviceResource.substring(0, deviceResource.indexOf("_"));
        if(deviceType.equalsIgnoreCase("ios")) {
            return R.drawable.demo_device_ios;
        }else if(deviceType.equalsIgnoreCase("android")) {
            return R.drawable.demo_device_android;
        }else if(deviceType.equalsIgnoreCase("web")) {
            return R.drawable.demo_device_web;
        }else if(deviceType.equalsIgnoreCase("win")) {
            return R.drawable.demo_device_win;
        }else if(deviceType.equalsIgnoreCase("iMac")) {
            return R.drawable.demo_device_imac;
        }
        return 0;
    }

}
