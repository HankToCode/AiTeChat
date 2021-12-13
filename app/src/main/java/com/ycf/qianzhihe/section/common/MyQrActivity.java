package com.ycf.qianzhihe.section.common;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.google.zxing.WriterException;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.help.QRHelper;
import com.ycf.qianzhihe.app.utils.BitmapUtil;
import com.ycf.qianzhihe.app.utils.ImageUtil;
import com.ycf.qianzhihe.app.utils.XClickUtil;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.BitmapCallback;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Administrator
 * 日期 2018/8/9
 * 描述 我的二维码
 */

public class MyQrActivity extends BaseInitActivity {


    private Bitmap qrCodeBitmap;


    /**
     * 群id 或者 用户id生成二维码
     * from 1:从个人信息过来， 2：从群组跳转过来
     */
    private String id;
    private String from = "1";
    private EaseTitleBar mTitleBar;
    private LinearLayout mLlQrcodeView;
    private TextView mTvQrName;
    private TextView mTvChatNumber;
    private ImageView mIvQrCode;
    private EaseImageView mImgQrHead;
    private ImageView mIvSaveQr;
    private ImageView mImgHead;
    private RelativeLayout rlParent;


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MyQrActivity.class);
        intent.putExtra("from", "1");
        context.startActivity(intent);
    }

    public static void actionStart(Context context, String groupId, String head, String name) {
        Intent intent = new Intent(context, MyQrActivity.class);
        Bundle bundle2 = new Bundle();
        bundle2.putString("from", "2");
        bundle2.putString("id", groupId);
        bundle2.putString("head", head);
        bundle2.putString("name", name);
        intent.putExtras(bundle2);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_myqr;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initImmersionBar(false);
        rlParent = (RelativeLayout) findViewById(R.id.rl_parent);
        mTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        mLlQrcodeView = (LinearLayout) findViewById(R.id.ll_qrcode_view);
        mTvQrName = (TextView) findViewById(R.id.tv_qr_name);
        mTvChatNumber = (TextView) findViewById(R.id.tv_chat_number);
        mIvQrCode = (ImageView) findViewById(R.id.iv_qr_code);
        mImgQrHead = (EaseImageView) findViewById(R.id.img_qr_head);
        mIvSaveQr = (ImageView) findViewById(R.id.iv_save_qr);
        mImgHead = (ImageView) findViewById(R.id.img_head);

        mTitleBar.setOnBackPressListener(view -> finish());

        if ("2".equals(from)) {
            //群分享二维码 数据排序 服务器群id - 标志符 - 邀请人id
            mTitleBar.setTitle("群二维码");
            rlParent.setBackground(ContextCompat.getDrawable(this,R.mipmap.bg_qr_bg_group));
            mTvQrName.setText(name);
            if (!TextUtils.isEmpty(head)) {
                GlideUtils.GlideLoadCircleErrorImageUtils(this, AppConfig.checkimg(head), mImgHead, R.mipmap.ic_ng_avatar);
                GlideUtils.GlideLoadCircleErrorImageUtils(this, AppConfig.checkimg(head), mImgQrHead, R.mipmap.ic_ng_avatar);
            }
            createImgQr(AppConfig.checkimg(head));
        } else {
            id = UserComm.getUserInfo().getUserId() + Constant.SEPARATOR_UNDERLINE + Constant.PREFIX_QR_USER;
            mTitleBar.setTitle("好友二维码");
            rlParent.setBackground(ContextCompat.getDrawable(this,R.mipmap.bg_qr_bg));
            if (!TextUtils.isEmpty(UserComm.getUserInfo().getUserCode())) {
                mTvChatNumber.setVisibility(View.VISIBLE);
                mTvChatNumber.setText(getString(R.string.str_chat_account, UserComm.getUserInfo().getUserCode()));
            }
            mTvQrName.setText(UserComm.getUserInfo().getNickName());
            GlideUtils.GlideLoadCircleErrorImageUtils(this, UserComm.getUserInfo().getUserHead(), mImgHead, R.mipmap.ic_ng_avatar);
            GlideUtils.GlideLoadCircleErrorImageUtils(this, UserComm.getUserInfo().getUserHead(), mImgQrHead, R.mipmap.ic_ng_avatar);
            createImgQr(UserComm.getUserInfo().getUserHead());
        }

        ImageUtil.setAvatar(mImgQrHead);


    }

    @Override
    protected void initListener() {
        super.initListener();
        mIvSaveQr.setOnClickListener(view -> {
            if (!XClickUtil.isFastDoubleClick(view, 2000))
                save();
        });
    }

    private void createImgQr(String imgUrl) {

        Bitmap logoBitmap = null;
        try {
            logoBitmap = Glide.with(this)
                    .asBitmap()
                    .load(imgUrl)
                    .submit(110, 110).get();

        } catch (Exception e) {
            logoBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_ng_avatar, null);
        }


        try {
            qrCodeBitmap = QRHelper.createQRCode(id, 500);
//            qrCodeBitmap = QRHelper.addLogo(qrCodeBitmap, logoBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        mIvQrCode.setImageBitmap(qrCodeBitmap);
    }

    private String name = "";
    private String head = "";

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        from = intent.getStringExtra("from");
        name = intent.getStringExtra("name");
        head = intent.getStringExtra("head");
        if ("2".equals(from)) {
            //群分享二维码 数据排序 服务器群id - 标志符 - 邀请人id
            id = intent.getStringExtra("id") + Constant.SEPARATOR_UNDERLINE
                    + Constant.FLAG_QR_GROUP + Constant.SEPARATOR_UNDERLINE
                    + UserComm.getUserInfo().getUserId();
        } else {
            id = UserComm.getUserInfo().getUserId() + Constant.SEPARATOR_UNDERLINE + Constant.PREFIX_QR_USER;
        }
    }

    private void save() {
        if (PermissionsUtil.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            saveFile();

        } else {
            PermissionsUtil.requestPermission(this, new PermissionListener() {
                @Override
                public void permissionGranted(@NonNull String[] permission) {
                    saveFile();
                }

                @Override
                public void permissionDenied(@NonNull String[] permission) {
                    ToastUtil.toast("请打开读写权限");
                }
            }, Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE);
        }


    }

    private void saveFile() {
        showLoading("正在保存");
//        Bitmap QeBitmap = EncodingHandler.createQRCode(yuming + AppConfig.QR + MyApplication.getInstance().getUserInfo().getInviteCode(), 800, 800, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        Tiny.getInstance().source(createViewBitmap(mLlQrcodeView)).asBitmap().compress(new BitmapCallback() {
            @Override
            public void callback(boolean isSuccess, Bitmap bitmap, Throwable t) {
                if (isSuccess) {
                    BitmapUtil.saveBitmapInFile(MyQrActivity.this, bitmap);
                    ToastUtil.toast("保存成功");
                } else {
                    ToastUtil.toast("保存失败");
                }
                dismissLoading();
            }
        });
    }

    public Bitmap createViewBitmap(View v) {
        Bitmap bitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        v.draw(canvas);
        return bitmap;
    }

}
