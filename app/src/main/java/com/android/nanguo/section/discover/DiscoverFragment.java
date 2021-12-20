package com.android.nanguo.section.discover;

import static com.darsh.multipleimageselect.helpers.Constants.REQUEST_CODE;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.nanguo.R;
import com.android.nanguo.app.api.Constant;
import com.android.nanguo.app.api.Global;
import com.android.nanguo.app.base.BaseInitFragment;
import com.android.nanguo.app.base.WebViewActivity;
import com.android.nanguo.app.operate.UserOperateManager;
import com.android.nanguo.section.account.activity.UserInfoDetailActivity;
import com.android.nanguo.section.common.InviteActivity;
import com.android.nanguo.section.contact.activity.AddUserActivity;
import com.zds.base.code.activity.CaptureActivity;

public class DiscoverFragment extends BaseInitFragment implements View.OnClickListener {


    private ConstraintLayout mClSYS;
    private ImageView mIvSYS;
    private ConstraintLayout mClKYK;
    private ImageView mIvKYK;
    private ConstraintLayout mClYQ;
    private ImageView mIvYQ;
    private ConstraintLayout mClQRC;
    private ImageView mIvQRC;
    private ConstraintLayout mClWYW;
    private ImageView mIvWYW;

    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_discover;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mClSYS = (ConstraintLayout) findViewById(R.id.clSYS);
        mIvSYS = (ImageView) findViewById(R.id.ivSYS);
        mClKYK = (ConstraintLayout) findViewById(R.id.clKYK);
        mIvKYK = (ImageView) findViewById(R.id.ivKYK);
        mClYQ = (ConstraintLayout) findViewById(R.id.clYQ);
        mIvYQ = (ImageView) findViewById(R.id.ivYQ);
        mClQRC = (ConstraintLayout) findViewById(R.id.clQRC);
        mIvQRC = (ImageView) findViewById(R.id.ivQRC);
        mClWYW = (ConstraintLayout) findViewById(R.id.clWYW);
        mIvWYW = (ImageView) findViewById(R.id.ivWYW);
    }

    @Override
    protected void initListener() {
        super.initListener();
        mClSYS.setOnClickListener(this);
        mClKYK.setOnClickListener(this);
        mClYQ.setOnClickListener(this);
        mClQRC.setOnClickListener(this);
        mClWYW.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.clSYS:
                AddUserActivity.actionStart(requireContext());
                break;
            case R.id.clKYK:
                WebViewActivity.actionStart(mContext, "http://dsp009.wojiazhe.com/", true);
                break;
            case R.id.clYQ:
                InviteActivity.actionStart(mContext);
                break;
            case R.id.clQRC:
                Global.addUserOriginType = Constant.ADD_USER_ORIGIN_TYPE_QRCODE;
                Intent intent = new Intent(requireContext(),
                        CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.clWYW:
            WebViewActivity.actionStart(mContext, "http://www.itmind.net/tetris/", true);
                break;
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
                String result = bundle.getString(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);

                if (result.contains("person")) {
                    startActivity(new Intent(mContext,
                            UserInfoDetailActivity.class).putExtra(
                            "friendUserId", result.split("_")[0]));
                } else if (result.contains("group")) {
                    UserOperateManager.getInstance().scanInviteContact(mContext, result);
                }
            }
        }
    }



}
