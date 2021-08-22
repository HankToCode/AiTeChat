package com.ycf.qianzhihe.section.account.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.EMCallBack;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.CommonApi;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.utils.my.MyHelper;
import com.ycf.qianzhihe.app.weight.CommonDialog;
import com.ycf.qianzhihe.app.weight.ease.EaseAlertDialog;
import com.ycf.qianzhihe.common.utils.DeviceIdUtil;
import com.ycf.qianzhihe.section.common.EditInfoActivity;
import com.ycf.qianzhihe.section.common.MyQrActivity;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.widget.SwitchButton;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

import static com.zds.base.Toast.ToastUtil.toast;

/**
 * @author lhb
 * 个人资料
 */
public class MyInfoActivity extends BaseInitActivity {
    @BindView(R.id.title_bar)
    EaseTitleBar title_bar;
    @BindView(R.id.img_head)
    ImageView mImgHead;
    @BindView(R.id.tv_name)
    TextView mTvName;
    @BindView(R.id.tv_account)
    TextView mTvAccount;
    @BindView(R.id.tv_sign)
    TextView mTvSign;
    @BindView(R.id.tv_phone)
    TextView mTvPhone;
    private LoginInfo info;

    @BindView(R.id.sb_verify)
    SwitchButton sb_verify;
    @BindView(R.id.rg_sex)
    RadioGroup rg_sex;
    @BindView(R.id.rb_male)
    RadioButton rb_male;
    @BindView(R.id.rb_female)
    RadioButton rb_female;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_info;
    }


    public static void actionStart(Context context) {
        Intent starter = new Intent(context, MyInfoActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        title_bar.setTitle("我的资料");
        title_bar.setLeftImageResource(R.mipmap.icon_back_white);
        title_bar.setOnBackPressListener(view -> finish());
        info = UserComm.getUserInfo();
        //手机号
        mTvPhone.setText(info.getPhone());
        //昵称
        mTvName.setText(info.getNickName());
        //头像
        GlideUtils.GlideLoadCircleErrorImageUtils(this, AppConfig.checkimg(info.getUserHead()), mImgHead, R.mipmap.img_default_avatar);

        if (!TextUtils.isEmpty(info.getUserCode())) {
            //艾特号
            mTvAccount.setText(info.getUserCode());
        }

        if (!TextUtils.isEmpty(info.getSign())) {
            //艾特号
            mTvSign.setText(info.getSign());
        }


        sb_verify.setChecked(info.addWay == 0);
        rg_sex.check(info.sex == 0 ? R.id.rb_male : R.id.rb_female);

        sb_verify.setOnCheckedChangeListener(new SwitchButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(SwitchButton view, boolean isChecked) {

                Map<String,Object> map =new HashMap<>();
                map.put("addWay",isChecked ? 0 : 1);
                ApiClient.requestNetHandle(MyInfoActivity.this, AppConfig.MODIFY_FRIEND_CONSENT + "/" + (isChecked ? 0 : 1), "请稍候...", map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        LoginInfo loginInfo = UserComm.getUserInfo();
                        loginInfo.addWay = isChecked ? 0 : 1;
                        UserComm.saveUsersInfo(loginInfo);
                        toast("修改成功");
                    }
                    @Override
                    public void onFailure(String msg) {
                        toast(msg);

                    }
                });
            }
        });

        rg_sex.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int id) {
                Map<String,Object> map =new HashMap<>();
                map.put("sex",id == R.id.rb_male ? 0 : 1);
                ApiClient.requestNetHandle(MyInfoActivity.this, AppConfig.MODIFY_SEX + "/" + (id == R.id.rb_male ? 0 : 1), "请稍候...", map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        LoginInfo loginInfo = UserComm.getUserInfo();
                        loginInfo.sex = id == R.id.rb_male ? 0 : 1;
                        UserComm.saveUsersInfo(loginInfo);
                        toast("修改成功");
                    }
                    @Override
                    public void onFailure(String msg) {
                        toast(msg);
                    }
                });
            }
        });
    }




    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.FLUSHUSERINFO) {
            mTvName.setText(UserComm.getUserInfo().getNickName());
            mTvSign.setText(UserComm.getUserInfo().getSign());
            mTvAccount.setText(UserComm.getUserInfo().getUserCode());
        }
    }



    /**
     * 上传头像地址到服务器
     *
     */
    private void saveHead(File file) {
        ApiClient.requestNetHandleFile(MyInfoActivity.this, AppConfig.groupUpHead, "正在上传...", file, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                modifyHead(json);
            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
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

        ApiClient.requestNetHandle(MyInfoActivity.this, AppConfig.MODIFY_USER_HEAD, "", map, new ResultListener() {

            @Override
            public void onSuccess(String json, String msg) {
                GlideUtils.GlideLoadCircleErrorImageUtils(MyInfoActivity.this, AppConfig.checkimg(filePath), mImgHead, R.mipmap.img_default_avatar);
                CommonApi.upUserInfo(MyInfoActivity.this);
                toast("上传成功");
            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
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
                PictureSelector.create(MyInfoActivity.this)
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
                PictureSelector.create(MyInfoActivity.this)
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


    @OnClick({R.id.rl_my_share, R.id.img_head, R.id.tv_name, R.id.rl_my_sign, R.id.tv_account,  R.id.rl_my_qr,R.id.tv_logoff})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.img_head:
                toSelectPic();
                break;
            case R.id.rl_my_qr:
                MyQrActivity.actionStart(mContext, "1");
                break;
            case R.id.tv_name://修改昵称
                startActivity(new Intent(this, EditInfoActivity.class).putExtra("from", "1"));
                break;
            case R.id.rl_my_sign://修改个性签名
                startActivity(new Intent(this, EditInfoActivity.class).putExtra("from", "3"));
                break;
            /*case R.id.tv_account:
                Bundle bundle3 = new Bundle();
                bundle3.putString("from", "2");
                startActivity(EditInfoActivity.class, bundle3);
                break;*/
            /*case R.id.tv_exit:
                new EaseAlertDialog(this, "确定退出帐号？", null, null, new EaseAlertDialog.AlertDialogUser() {
                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if (confirmed) {
                            logout();
                        }
                    }
                }).show();
                break;*/
            case R.id.rl_my_share:
                Bundle bundle2 = new Bundle();
                bundle2.putString("nickName", info.getNickName());
                bundle2.putString("userCode", info.getUserCode());
                bundle2.putString("avatar", info.getUserHead());
                bundle2.putString("friendUserId", info.getUserId());

//                startActivity(ShareCardIdActivity.class, bundle2);
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
//                showLogoffDialog();
                break;
            default:
                break;
        }
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


    private void logout() {
        String st = getResources().getString(R.string.Are_logged_out);
        showLoading(st);

        Map<String,Object> map =new HashMap<>();
        map.put("deviceId", DeviceIdUtil.getDeviceId(this));
        ApiClient.requestNetHandle(this, AppConfig.multiDeviceLogout, "请稍候...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                toast(json);
            }
            @Override
            public void onFailure(String msg) {
                toast(msg);

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
                        MyInfoActivity.this.finish();
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
                        Toast.makeText(MyInfoActivity.this, "退出失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }


}
