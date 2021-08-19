package con.ycf.qianzhihe.section.account.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.google.zxing.WriterException;
import con.ycf.qianzhihe.R;
import con.ycf.qianzhihe.app.base.BaseInitActivity;
import con.ycf.qianzhihe.app.help.QRHelper;

import com.hyphenate.easeui.widget.EaseTitleBar;

public class ShareQrActivity extends BaseInitActivity implements View.OnClickListener {

    private EaseTitleBar mTitleBar;
    private ImageView mIvQr;

    private Bitmap qrCodeBitmap;


    public static void actionStart(Context context) {
        Intent starter = new Intent(context, ShareQrActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar = (EaseTitleBar) findViewById(R.id.title_bar);
        mIvQr = (ImageView) findViewById(R.id.iv_qr);

    }


    @Override
    protected void initListener() {
        super.initListener();
        mTitleBar.setOnBackPressListener(view -> {
            finish();
        });
    }

    @Override
    protected void initData() {
        super.initData();
    }


    protected void initLogic() {
        setTitle("分享");
        createImgQr();
    }

    private void createImgQr() {
        try {
            qrCodeBitmap = QRHelper.addLogo(QRHelper.createQRCode("https://a.app.qq.com/o/simple.jsp?pkgname=com.aite.chat", 800), BitmapFactory.decodeResource(getResources(), R.mipmap.webox_dialog_face_bg));
            mIvQr.setImageBitmap(qrCodeBitmap);
        } catch (WriterException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onClick(View view) {
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_share_qr;
    }
}