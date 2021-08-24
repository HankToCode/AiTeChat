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
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.github.dfqin.grantor.PermissionListener;
import com.github.dfqin.grantor.PermissionsUtil;
import com.google.zxing.WriterException;
import com.hyphenate.easecallkit.widget.EaseImageView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.help.QRHelper;
import com.ycf.qianzhihe.app.utils.BitmapUtil;
import com.ycf.qianzhihe.app.utils.ImageUtil;
import com.ycf.qianzhihe.app.utils.XClickUtil;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.common.enums.SearchType;
import com.ycf.qianzhihe.section.contact.activity.AddContactActivity;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.BitmapCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author Administrator
 * 日期 2018/8/9
 * 描述 我的二维码
 */

public class MyQrActivity extends BaseInitActivity {

    @BindView(R.id.tv_qr_name)
    TextView mTvQrName;
    @BindView(R.id.iv_qr_code)
    ImageView mIvQrCode;
    @BindView(R.id.img_head)
    ImageView mImgHead;
    @BindView(R.id.img_qr_head)
    EaseImageView mImgQRHead;
    @BindView(R.id.title_bar)
    EaseTitleBar title_bar;

    @BindView(R.id.rl_qrcode_view)
    RelativeLayout rlQrCodeView;
    @BindView(R.id.tv_card_tips)
    TextView tvCardTips;
    @BindView(R.id.tv_chat_number)
    TextView tvChatNumber;
    private Bitmap qrCodeBitmap;


    /**
     * 群id 或者 用户id生成二维码
     * from 1:从个人信息过来， 2：从群组跳转过来
     */
    private String id;
    private String from = "1";


    public static void actionStart(Context context, String from) {
        Intent intent = new Intent(context, MyQrActivity.class);
        intent.putExtra("from", from);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_myqr;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
//        isTransparency(true);
        title_bar.setOnBackPressListener(view -> finish());
//        mLlayoutTitle1.setBackgroundColor(ContextCompat.getColor(this, R.color.bg_color_my_gold));
    }


    private void createImgQr(String imgUrl) {

        Bitmap logoBitmap = null;
        try {
            logoBitmap = Glide.with(this)
                    .asBitmap()
                    .load(imgUrl)
                    .submit(110, 110).get();

        } catch (Exception e) {
            logoBitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.img_default_avatar, null);
        }


        try {
            qrCodeBitmap = QRHelper.createQRCode(id, 500);
//            qrCodeBitmap = QRHelper.addLogo(qrCodeBitmap, logoBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
        mIvQrCode.setImageBitmap(qrCodeBitmap);
    }

    @Override
    protected void onEventComing(EventCenter center) {

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        if ("2".equals(intent.getStringExtra("from"))) {
            //群分享二维码 数据排序 服务器群id - 标志符 - 邀请人id

            id = intent.getStringExtra("id") + Constant.SEPARATOR_UNDERLINE
                    + Constant.FLAG_QR_GROUP + Constant.SEPARATOR_UNDERLINE
                    + UserComm.getUserInfo().getUserId();
            title_bar.setTitle("群二维码");
            mTvQrName.setText(intent.getStringExtra("name"));
            if (!TextUtils.isEmpty(intent.getStringExtra("head"))) {
                GlideUtils.GlideLoadCircleErrorImageUtils(this, AppConfig.checkimg(intent.getStringExtra("head")), mImgHead, R.mipmap.img_default_avatar);
                GlideUtils.GlideLoadCircleErrorImageUtils(this, AppConfig.checkimg(intent.getStringExtra("head")), mImgQRHead, R.mipmap.img_default_avatar);
            }
            tvCardTips.setText("扫一扫上面的二维码，加入群聊");
            createImgQr(AppConfig.checkimg(intent.getStringExtra("head")));
        } else {
            id = UserComm.getUserInfo().getUserId() + Constant.SEPARATOR_UNDERLINE + Constant.PREFIX_QR_USER;
            title_bar.setTitle("我的二维码");
            if (!TextUtils.isEmpty(UserComm.getUserInfo().getUserCode())) {
                tvChatNumber.setVisibility(View.VISIBLE);
                tvChatNumber.setText(getString(R.string.str_chat_account, UserComm.getUserInfo().getUserCode()));
            }
            mTvQrName.setText(UserComm.getUserInfo().getNickName());
            GlideUtils.GlideLoadCircleErrorImageUtils(this, UserComm.getUserInfo().getUserHead(), mImgHead, R.mipmap.img_default_avatar);
            GlideUtils.GlideLoadCircleErrorImageUtils(this, UserComm.getUserInfo().getUserHead(), mImgQRHead, R.mipmap.img_default_avatar);
            tvCardTips.setText("扫一扫上面的二维码，加我千纸鹤好友");
            createImgQr(UserComm.getUserInfo().getUserHead());
        }

        ImageUtil.setAvatar(mImgQRHead);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
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
        Tiny.getInstance().source(createViewBitmap(rlQrCodeView)).asBitmap().compress(new BitmapCallback() {
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

    @OnClick({R.id.tv_save_qr})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_save_qr:
                if (!XClickUtil.isFastDoubleClick(view, 2000))
                    save();
                break;
            default:
                break;
        }
    }

}
