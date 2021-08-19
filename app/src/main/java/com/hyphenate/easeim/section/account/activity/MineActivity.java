package com.hyphenate.easeim.section.account.activity;

import static com.darsh.multipleimageselect.helpers.Constants.REQUEST_CODE;
import static com.zds.base.code.activity.CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.coorchice.library.SuperTextView;
import com.hyphenate.EMCallBack;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.app.api.Constant;
import com.hyphenate.easeim.app.api.Global;
import com.hyphenate.easeim.app.api.global.EventUtil;
import com.hyphenate.easeim.app.api.global.UserComm;
import com.hyphenate.easeim.app.api.old_data.EventCenter;
import com.hyphenate.easeim.app.api.old_data.LoginInfo;
import com.hyphenate.easeim.app.api.old_data.StoreShowSwitchBean;
import com.hyphenate.easeim.app.api.old_http.ApiClient;
import com.hyphenate.easeim.app.api.old_http.AppConfig;
import com.hyphenate.easeim.app.api.old_http.CommonApi;
import com.hyphenate.easeim.app.api.old_http.ResultListener;
import com.hyphenate.easeim.app.base.BaseInitActivity;
import com.hyphenate.easeim.app.operate.UserOperateManager;
import com.hyphenate.easeim.app.utils.my.MyHelper;
import com.hyphenate.easeim.app.weight.CommonDialog;
import com.hyphenate.easeim.common.utils.DeviceIdUtil;
import com.hyphenate.easeim.section.common.SetActivity;
import com.hyphenate.easeim.section.common.WalletActivity;
import com.hyphenate.easeim.section.common.MyCollectActivity;
import com.hyphenate.easeim.section.me.activity.MultiDeviceActivity;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseImageView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.code.activity.CaptureActivity;
import com.zds.base.json.FastJsonUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

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
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_back:
                finish();
                break;
            case R.id.iv_avatar:
                toSelectPic();
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
                ShareQrActivity.actionStart(this);
                break;
            case R.id.tv_package:
                //我的钱包支付
                WalletActivity.actionStart(mContext);
                break;
            case R.id.ll_my_info:
                //个人信息
                MyInfoActivity.actionStart(this);
                break;
            case R.id.tv_member:
                break;
            case R.id.tv_collection:
                //我的收藏
                MyCollectActivity.actionStart(this);
                break;
            case R.id.tv_settings:
                //设置
                SetActivity.actionStart(this);
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

    /**
     * 选择图片上传
     */
    private void toSelectPic() {
        final CommonDialog.Builder builder = new CommonDialog.Builder(this).fullWidth().fromBottom()
                .setView(R.layout.dialog_select_head);
        builder.setOnClickListener(R.id.tv_cell, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.setOnClickListener(R.id.tv_xiangji, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
                PictureSelector.create(MineActivity.this)
                        .openCamera(PictureMimeType.ofImage())
                        .selectionMode(PictureConfig.SINGLE)
                        .withAspectRatio(1, 1)
                        .enableCrop(true)
                        .showCropFrame(false)
                        .showCropGrid(false)
                        .freeStyleCropEnabled(true)
                        .circleDimmedLayer(false)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
            }
        });
        builder.setOnClickListener(R.id.tv_xiangce, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
                PictureSelector.create(MineActivity.this)
                        .openGallery(PictureMimeType.ofImage())
                        .selectionMode(PictureConfig.SINGLE)
                        .withAspectRatio(1, 1)
                        .enableCrop(true)
                        .showCropFrame(false)
                        .showCropGrid(false)
                        .freeStyleCropEnabled(true)
                        .circleDimmedLayer(false)
                        .forResult(PictureConfig.CHOOSE_REQUEST);

            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList.size() > 0) {
                    if (selectList.get(0).isCut()) {
                        compressImg(selectList.get(0).getCutPath());

                    } else {
                        compressImg(selectList.get(0).getPath());
                    }
                }
            }
        } else if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                String result = bundle.getString(INTENT_EXTRA_KEY_QR_SCAN);
                if (result.contains("person") || result.contains("group")) {
                    if ("person".equals(result.split("_")[1])) {
                        startActivity(new Intent(this, UserInfoDetailActivity.class).putExtra("friendUserId", result.split("_")[0]));
                    } else {
                        UserOperateManager.getInstance().scanInviteContact(this, result);

                    }
                }
            }
        }
    }

    public void compressImg(String path) {
        Luban.with(this)
                .load(path)                                     // 传人要压缩的图片列表
                .ignoreBy(80)                                   // 忽略不压缩图片的大小
                .setTargetDir(this.getExternalCacheDir().getAbsolutePath())                             // 设置压缩后文件存储位置
                .setCompressListener(new OnCompressListener() { //设置回调
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        saveHead(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                    }
                }).launch();
    }

    /**
     * 上传头像地址到服务器
     */
    private void saveHead(File file) {
        ApiClient.requestNetHandleFile(MineActivity.this, AppConfig.groupUpHead, "正在上传...", file, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                modifyHead(json);
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });

    }


    /**
     * 更新头像
     *
     * @param filePath
     */
    private void modifyHead(String filePath) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("userHead", filePath);

        ApiClient.requestNetHandle(MineActivity.this, AppConfig.MODIFY_USER_HEAD, "", map, new ResultListener() {

            @Override
            public void onSuccess(String json, String msg) {
                GlideUtils.GlideLoadCircleErrorImageUtils(MineActivity.this, AppConfig.checkimg(filePath), mIvAvatar, R.mipmap.img_default_avatar);
                CommonApi.upUserInfo(MineActivity.this);
                ToastUtil.toast("上传成功");
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });

    }

}
