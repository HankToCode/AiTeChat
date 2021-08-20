package com.ycf.qianzhihe.section.account.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.coorchice.library.SuperTextView;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.CommonApi;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.weight.CommonDialog;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

public class MyInfoActivity extends BaseInitActivity implements View.OnClickListener {

    private EaseTitleBar mTitleBar;
    private ConstraintLayout mLlMyInfo;
    private EaseImageView mIvAvatar;
    private TextView mTvNickName;
    private TextView mTvUserId;
    private SuperTextView mTvUserLevel;
    private ImageView mIvUserLevelTag;
    private RecyclerView mRecyclerView;
    private SuperTextView mTvPay;


    public static void actionStart(Context context) {
        Intent starter = new Intent(context, MyInfoActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_info;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initImmersionBar(true);
        mTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        mLlMyInfo = (ConstraintLayout) findViewById(R.id.ll_my_info);
        mIvAvatar = (EaseImageView) findViewById(R.id.iv_avatar);
        mTvNickName = (TextView) findViewById(R.id.tv_nick_name);
        mTvUserId = (TextView) findViewById(R.id.tv_user_id);
        mTvUserLevel = (SuperTextView) findViewById(R.id.tv_user_level);
        mIvUserLevelTag = (ImageView) findViewById(R.id.iv_user_level_tag);
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mTvPay = (SuperTextView) findViewById(R.id.tv_pay);

    }

    @Override
    protected void initListener() {
        super.initListener();

        mIvAvatar.setOnClickListener(this);
        mTvPay.setOnClickListener(this);

        mTitleBar.setOnBackPressListener(view -> finish());

    }

    @Override
    protected void initData() {
        super.initData();

        initUserInfo();
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


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_avatar:
                toSelectPic();
                break;
            case R.id.tv_pay:
                break;
            default:
                break;

        }

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

    /**
     * 上传头像地址到服务器
     */
    private void saveHead(File file) {
        ApiClient.requestNetHandleFile(MyInfoActivity.this, AppConfig.groupUpHead, "正在上传...", file, new ResultListener() {
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

        ApiClient.requestNetHandle(MyInfoActivity.this, AppConfig.MODIFY_USER_HEAD, "", map, new ResultListener() {

            @Override
            public void onSuccess(String json, String msg) {
                GlideUtils.GlideLoadCircleErrorImageUtils(MyInfoActivity.this, AppConfig.checkimg(filePath), mIvAvatar, R.mipmap.img_default_avatar);
                CommonApi.upUserInfo(MyInfoActivity.this);
                ToastUtil.toast("上传成功");
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });

    }

}
