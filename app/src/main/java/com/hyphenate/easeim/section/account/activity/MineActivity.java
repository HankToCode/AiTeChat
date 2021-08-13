package com.hyphenate.easeim.section.account.activity;

import static com.darsh.multipleimageselect.helpers.Constants.REQUEST_CODE;
import static com.zds.base.code.activity.CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.coorchice.library.SuperTextView;
import com.hyphenate.EMCallBack;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.app.api.Constant;
import com.hyphenate.easeim.app.api.Global;
import com.hyphenate.easeim.app.api.old_data.EventCenter;
import com.hyphenate.easeim.app.api.old_data.LoginInfo;
import com.hyphenate.easeim.app.api.old_data.StoreShowSwitchBean;
import com.hyphenate.easeim.app.api.global.EventUtil;
import com.hyphenate.easeim.app.api.global.UserComm;
import com.hyphenate.easeim.app.api.old_http.ApiClient;
import com.hyphenate.easeim.app.api.old_http.AppConfig;
import com.hyphenate.easeim.app.api.old_http.ResultListener;
import com.hyphenate.easeim.app.base.BaseInitActivity;
import com.hyphenate.easeim.app.operate.UserOperateManager;
import com.hyphenate.easeim.app.utils.my.MyHelper;
import com.hyphenate.easeim.common.utils.DeviceIdUtil;
import com.hyphenate.easeim.section.me.activity.MultiDeviceActivity;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseImageView;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.code.activity.CaptureActivity;
import com.zds.base.json.FastJsonUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MineActivity extends BaseInitActivity implements View.OnClickListener {
    private ImageView mIvBack;
    private EaseImageView mIvAvatar;
    private TextView mTvNickName;
    private TextView mTvUserId;
    private SuperTextView mTvUserLevel;
    private ImageView mIvUserLevelTag;
    private ImageView mIvScan;
    private ImageView mIvQr;
    private TextView mTvSwitchAccount;
    private TextView mTvLogout;
    private ConstraintLayout mLlMyInfo;


    private TextView mTvPackage;
    private TextView mTvMember;
    private TextView mTvCollection;
    private TextView mTvSettings;
    private TextView mTvHelpline;
    private TextView mTvGroups;


    public static void actionStart(Context context) {
        Intent starter = new Intent(context, MineActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_mine;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initImmersionBar(false);
        mIvBack = (ImageView) findViewById(R.id.iv_back);
        mIvAvatar = findViewById(R.id.iv_avatar);
        mTvNickName = (TextView) findViewById(R.id.tv_nick_name);
        mTvUserId = (TextView) findViewById(R.id.tv_user_id);
        mTvUserLevel = (SuperTextView) findViewById(R.id.tv_user_level);
        mIvUserLevelTag = (ImageView) findViewById(R.id.iv_user_level_tag);
        mIvScan = (ImageView) findViewById(R.id.iv_scan);
        mIvQr = (ImageView) findViewById(R.id.iv_qr);
        mTvSwitchAccount = (TextView) findViewById(R.id.tv_switch_account);
        mTvLogout = (TextView) findViewById(R.id.tv_logout);
        mLlMyInfo = (ConstraintLayout) findViewById(R.id.ll_my_info);


        mTvPackage = (TextView) findViewById(R.id.tv_package);
        mTvMember = (TextView) findViewById(R.id.tv_member);
        mTvCollection = (TextView) findViewById(R.id.tv_collection);
        mTvSettings = (TextView) findViewById(R.id.tv_settings);
        mTvHelpline = (TextView) findViewById(R.id.tv_helpline);
        mTvGroups = (TextView) findViewById(R.id.tv_groups);
    }

    @Override
    protected void initListener() {
        super.initListener();

        mIvBack.setOnClickListener(this);
        mIvAvatar.setOnClickListener(this);
        mTvUserLevel.setOnClickListener(this);
        mIvUserLevelTag.setOnClickListener(this);
        mIvScan.setOnClickListener(this);
        mIvQr.setOnClickListener(this);
        mTvPackage.setOnClickListener(this);
        mTvMember.setOnClickListener(this);
        mTvCollection.setOnClickListener(this);
        mTvSettings.setOnClickListener(this);
        mTvHelpline.setOnClickListener(this);
        mTvGroups.setOnClickListener(this);
        mTvSwitchAccount.setOnClickListener(this);
        mTvLogout.setOnClickListener(this);
        mLlMyInfo.setOnClickListener(this);

    }

    @Override
    protected void initData() {
        super.initData();

        initUserInfo();

        getStoreShowSwitch();

    }

    /**
     * 初始化用户信息
     */
    private void initUserInfo() {
        LoginInfo loginInfo = UserComm.getUserInfo();
        if (loginInfo != null) {
            GlideUtils.GlideLoadCircleErrorImageUtils(this, AppConfig.checkimg(loginInfo.getUserHead()), mIvAvatar, R.mipmap.img_default_avatar);
            mTvNickName.setText(loginInfo.getNickName());
            if (TextUtils.isEmpty(loginInfo.getUserCode())) {
                mTvUserId.setText("ID： 无");
            } else {
                mTvUserId.setText("ID： " + loginInfo.getUserCode());
            }
        }
    }

    private void getStoreShowSwitch() {
        mTvGroups.setVisibility(View.GONE);
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandleNoParam(this, AppConfig.showStore,
                "", new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        if (json != null && json.length() > 0) {
                            StoreShowSwitchBean storeShowSwitchBean = FastJsonUtil.getObject(json, StoreShowSwitchBean.class);
                            if (storeShowSwitchBean.status == 1) {
                                mTvGroups.setVisibility(View.VISIBLE);
                            }
                        }
                    }

                    @Override
                    public void onFailure(String msg) {

                    }
                });
    }

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.FLUSHUSERINFO) {
            initUserInfo();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                String result = bundle.getString(INTENT_EXTRA_KEY_QR_SCAN);
                if (result.contains("person") || result.contains("group")) {
                    if ("person".equals(result.split("_")[1])) {
//                        startActivity(new Intent(this, UserInfoDetailActivity.class).putExtra("friendUserId", result.split("_")[0]));
                    } else {
                        UserOperateManager.getInstance().scanInviteContact(this, result);

                    }
                }
            }
        }
    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_avatar:
                break;
            case R.id.tv_user_level:
                break;
            case R.id.iv_user_level_tag:
                break;
            case R.id.iv_scan:
                Global.addUserOriginType = Constant.ADD_USER_ORIGIN_TYPE_QRCODE;
                Intent intent = new Intent(this, CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.iv_qr:
                //分享
//                startActivity(ShareQrActivity.class);
                break;
            case R.id.tv_package:
                //我的钱包支付
//                startActivity(WalletActivity.class);
                break;
            case R.id.ll_my_info:
                //个人信息
//                startActivity(MyInfoActivity.class);
                break;
            case R.id.tv_member:
                break;
            case R.id.tv_collection:
                //我的收藏
//                startActivity(MyCollectActivity.class);
                break;
            case R.id.tv_settings:
                //设置
//                startActivity(SetActivity.class);
                break;
            case R.id.tv_helpline:
                //客服
//                startActivity(CustomListActivity.class);
                break;
            case R.id.tv_groups:
                //靓号商城
//                startActivity(new Intent(getActivity(), WebViewActivity.class).putExtra("title","艾特商城").putExtra("url",AppConfig.shopUrl));

                break;
            case R.id.tv_switch_account:
                startActivity(new Intent(this, MultiDeviceActivity.class));
                break;
            case R.id.tv_logout:
                new EaseAlertDialog(this, "确定退出帐号？", null, null, new EaseAlertDialog.AlertDialogUser() {
                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if (confirmed) {
                            logout();
                        }
                    }
                }, true).show();
                break;
            default:
                break;

        }

    }


    private void logout() {
        String st = getResources().getString(R.string.Are_logged_out);
        showLoading(st);

        Map<String, Object> map = new HashMap<>();
        map.put("deviceId", DeviceIdUtil.getDeviceId(this));
        ApiClient.requestNetHandle(this, AppConfig.multiDeviceLogout, "请稍候...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast(json);
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);

            }
        });

        MyHelper.getInstance().logout(false, new EMCallBack() {

            @Override
            public void onSuccess() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoading();
                        UserComm.clearUserInfo();
                        finish();
                        EventBus.getDefault().post(new EventCenter(EventUtil.LOSETOKEN, "关闭"));
                    }
                });
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
                        dismissLoading();
                        Toast.makeText(MineActivity.this, "退出失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
