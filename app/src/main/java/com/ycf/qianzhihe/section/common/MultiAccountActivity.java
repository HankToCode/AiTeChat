package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.alibaba.fastjson.JSON;
import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;
import com.ycf.qianzhihe.DemoApplication;
import com.ycf.qianzhihe.DemoHelper;
import com.ycf.qianzhihe.MainActivity;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.adapter.LoginAccountAdapter;
import com.ycf.qianzhihe.app.api.Global;
import com.ycf.qianzhihe.app.api.global.SP;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.CommonApi;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.domain.EaseUser;
import com.ycf.qianzhihe.app.help.RclViewHelp;
import com.ycf.qianzhihe.app.operate.UserOperateManager;
import com.ycf.qianzhihe.app.utils.my.MyHelper;
import com.ycf.qianzhihe.app.utils.my.MyModel;
import com.ycf.qianzhihe.app.weight.CommonDialog;
import com.ycf.qianzhihe.common.db.DemoDbHelper;
import com.ycf.qianzhihe.common.interfaceOrImplement.DemoEmCallBack;
import com.ycf.qianzhihe.common.interfaceOrImplement.OnResourceParseCallback;
import com.ycf.qianzhihe.common.utils.DeviceIdUtil;
import com.ycf.qianzhihe.common.utils.PreferenceManager;
import com.ycf.qianzhihe.common.utils.ToastUtils;
import com.ycf.qianzhihe.common.utils.log.LogUtils;
import com.ycf.qianzhihe.section.account.activity.LoginActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.ycf.qianzhihe.section.account.viewmodels.SplashViewModel;
import com.ycf.qianzhihe.section.dialog.EditTextDialogFragment;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.util.SystemUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

public class MultiAccountActivity extends BaseInitActivity implements LoginAccountAdapter.OnItemClickListener {

    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ll_add)
    LinearLayout ll_add;

    private List<EaseUser> loginInfos = new ArrayList<>();
    private LoginAccountAdapter adapter;
    private int position;
    private MyModel myModel;



    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MultiAccountActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_multi_account_login;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar.setTitle("切换账号");
        mTitleBar.setOnBackPressListener(view -> finish());
        adapter = new LoginAccountAdapter(mContext,loginInfos);
        RclViewHelp.initRcLmVertical(this, recyclerView, adapter);

        myModel = MyHelper.getInstance().getModel();
        LoginInfo currentUser = UserComm.getUserInfo();//当前登录用户
        //加载已登录用户
        List<EaseUser> localFriendList = UserOperateManager.getInstance().getAccountList();
        loginInfos.addAll(localFriendList);
        adapter.notifyDataSetChanged();
        adapter.setOnItemClickListener(this);

        ll_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginActivity.actionStart(mContext,"1");
                finish();
            }
        });

    }

    @Override
    public void onItemClick(View view, int position) {
        this.position = position;
        logout();//先退出之前登录的账号
    }
    @Override
    public void onItemLongClick(View view, int position) {
    }

    private void logout() {
        Map<String, Object> map = new HashMap<>();
        map.put("deviceId", DeviceIdUtil.getDeviceId(mContext));
        ApiClient.requestNetHandle(mContext, AppConfig.multiDeviceLogout, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                //退出环信登录
                MyHelper.getInstance().logout(false, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoading();
                                UserComm.clearUserInfo();
                                //判断一下 点击保存的账号 有无密码
                                if (!TextUtils.isEmpty(loginInfos.get(position).getPassword())) {
                                    //有密码则进行登录
                                    passwordLogin(loginInfos.get(position).getPassword());//把旧账号退出环信后 再把需要登录的账号进行登录
                                } else {
                                    //无密码需要弹窗输入密码再进行登录密码，
                                    new EditTextDialogFragment.Builder(mContext)
                                            .setContentHint("请输入登录密码")
                                            .setConfirmClickListener(new EditTextDialogFragment.ConfirmClickListener() {
                                                @Override
                                                public void onConfirmClick(View view, String content) {
                                                    passwordLogin(content);//
                                                }
                                            })
                                            .setTitle("验证密码")
                                            .show();
                                }
                            }
                        });
                    }

                    @Override
                    public void onProgress(int progress, String status) { }
                    @Override
                    public void onError(int code, String message) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                dismissLoading();
                            }
                        });
                    }
                });
            }
            @Override
            public void onFailure(String msg) { }
        });
    }

    public void passwordLogin(String password) {
        showLoading("正在登录");
        DemoDbHelper.getInstance(mContext).closeDb();
        Map<String, Object> map = new HashMap<>();
        map.put("phone", loginInfos.get(position).getAccount());
        map.put("password", password);
        map.put("deviceId", DeviceIdUtil.getDeviceId(mContext));
        map.put("os", "Android");
        map.put("version", Global.loginVersion);
        map.put("deviceName", SystemUtil.getDeviceManufacturer() + " " + SystemUtil.getSystemModel());
        ApiClient.requestNetHandle(mContext, AppConfig.multiLogin, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                dismissLoading();
                if (json != null) {
                    LoginInfo loginInfo = JSON.parseObject(json, LoginInfo.class);
                    loginInfo.setPassword(loginInfos.get(position).getPassword());
                    PreferenceManager.getInstance().setParam(SP.SP_LANDED_ON_LOGIN, loginInfos.get(position).getAccount());

                    if (loginInfo != null) {
                        UserComm.saveUsersInfo(loginInfo);
                        DemoDbHelper.getInstance(mContext).initDb(loginInfos.get(position).getAccount());
                        CommonApi.upUserInfo(mContext);
                    }
                    // TODO: 2021/8/28/028 接口登录成功后 这里需要走环信SDK登录操作
                    loginSDK();
                }
            }

            @Override
            public void onFailure(String msg) {
                dismissLoading();
                ToastUtil.toast(msg);
            }
        });
    }

    private void loginSDK() {
        final LoginInfo loginInfo = UserComm.getUserInfo();
        DemoHelper.getInstance().init(DemoApplication.getInstance());
        DemoHelper.getInstance().getModel().setCurrentUserName(loginInfo.getNickName());
        DemoHelper.getInstance().getModel().setCurrentUserPwd(loginInfo.getPassword());
        EMClient.getInstance().login(loginInfo.getIdh(), "123456", new DemoEmCallBack() {
            @Override
            public void onSuccess() {
                EMClient.getInstance().groupManager().loadAllGroups();
                EMClient.getInstance().chatManager().loadAllConversations();
                // update current user's display name for APNs
                try {
                    EMClient.getInstance().pushManager().updatePushNickname(loginInfo.getNickName());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                finish();
                // get user's info (this should be get from App's server or 3rd party service)
            }

            @Override
            public void onError(int code, String error) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (code == 200) {
                            EMClient.getInstance().logout(false);
                        }
                        dismissLoading();
                        ToastUtils.showToast("登录失败 code=" + code + " " );
                    }
                });
            }
        });
    }
}
