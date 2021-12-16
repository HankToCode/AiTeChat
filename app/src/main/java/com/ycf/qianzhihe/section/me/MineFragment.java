package com.ycf.qianzhihe.section.me;

import static com.darsh.multipleimageselect.helpers.Constants.REQUEST_CODE;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.coorchice.library.SuperTextView;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseImageView;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.entity.LocalMedia;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.Global;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.base.BaseInitFragment;
import com.ycf.qianzhihe.app.base.WebViewActivity;
import com.ycf.qianzhihe.app.operate.UserOperateManager;
import com.ycf.qianzhihe.section.account.activity.AccountInfoActivity;
import com.ycf.qianzhihe.section.account.activity.BuyMemberActivity;
import com.ycf.qianzhihe.section.account.activity.MineActivity;
import com.ycf.qianzhihe.section.account.activity.MyInfoActivity;
import com.ycf.qianzhihe.section.account.activity.UserCodeActivity;
import com.ycf.qianzhihe.section.account.activity.UserInfoDetailActivity;
import com.ycf.qianzhihe.section.chat.activity.CustomListActivity;
import com.ycf.qianzhihe.section.common.MultiAccountActivity;
import com.ycf.qianzhihe.section.common.MyCollectActivity;
import com.ycf.qianzhihe.section.common.MyQrActivity;
import com.ycf.qianzhihe.section.common.NanGuoSetActivity;
import com.ycf.qianzhihe.section.common.NanguoWalletActivity;
import com.ycf.qianzhihe.section.common.RealAuthActivity;
import com.ycf.qianzhihe.section.common.SetActivity;
import com.ycf.qianzhihe.section.common.WalletActivity;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.code.activity.CaptureActivity;

import java.util.List;

public class MineFragment extends BaseInitFragment implements View.OnClickListener {


    private EaseImageView mIvAvatar;
    private ImageView mIvQR;
    private ImageView mIvQRC;
    private TextView mTvNickName;
    private TextView mTvID;
    private TextView mTvDesc;
    private ConstraintLayout mClVip;
    private ImageView mIvVipIcon;
    private TextView mTvVipIcon;
    private SuperTextView mStReNew;
    private TextView mTvService;
    private TextView mTvPay;
    private TextView mTvKF;
    private TextView mTvSettings;
    private TextView mTvAboutMe;
    private ImageView mIvVipLevel;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_mine;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mIvAvatar = findViewById(R.id.ivAvatar);
        mIvQR = (ImageView) findViewById(R.id.ivQR);
        mIvQRC = (ImageView) findViewById(R.id.ivQRC);
        mTvNickName = (TextView) findViewById(R.id.tvNickName);
        mTvID = (TextView) findViewById(R.id.tvID);
        mTvDesc = (TextView) findViewById(R.id.tvDesc);
        mClVip = (ConstraintLayout) findViewById(R.id.clVip);
        mIvVipIcon = (ImageView) findViewById(R.id.ivVipIcon);
        mTvVipIcon = (TextView) findViewById(R.id.tvVipIcon);
        mStReNew = (SuperTextView) findViewById(R.id.stReNew);
        mTvService = (TextView) findViewById(R.id.tvService);
        mTvPay = (TextView) findViewById(R.id.tvPay);
        mTvKF = (TextView) findViewById(R.id.tvKF);
        mTvSettings = (TextView) findViewById(R.id.tvSettings);
        mTvAboutMe = (TextView) findViewById(R.id.tvAboutMe);


        mIvVipLevel = (ImageView) findViewById(R.id.ivVipLevel);
    }

    @Override
    protected void initListener() {
        super.initListener();

        mIvAvatar.setOnClickListener(this);
        mIvQR.setOnClickListener(this);
        mIvQRC.setOnClickListener(this);
        mTvNickName.setOnClickListener(this);
        mTvID.setOnClickListener(this);
        mTvDesc.setOnClickListener(this);
        mClVip.setOnClickListener(this);
        mIvVipIcon.setOnClickListener(this);
        mTvVipIcon.setOnClickListener(this);
        mStReNew.setOnClickListener(this);
        mTvService.setOnClickListener(this);
        mTvPay.setOnClickListener(this);
        mTvKF.setOnClickListener(this);
        mTvSettings.setOnClickListener(this);
        mTvAboutMe.setOnClickListener(this);
        mIvVipLevel.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ivAvatar:
                MineActivity.actionStart(mContext);
                break;
            case R.id.ivVipLevel://等级
                AccountInfoActivity.actionStart(mContext);//账号等级权益
                break;
            case R.id.ivQRC:
                Global.addUserOriginType = Constant.ADD_USER_ORIGIN_TYPE_QRCODE;
                Intent intent = new Intent(requireContext(), CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
                break;
            case R.id.ivQR:
                //二维码
                MyQrActivity.actionStart(mContext);
                break;
            case R.id.tvPay:
                //判断如果未实名，提示进行实名认证
                if (UserComm.getUserInfo().getOpenAccountFlag() == 0) {
                    RealAuthActivity.actionStart(mContext);
                    return;
                }
                //我的钱包支付
                NanguoWalletActivity.actionStart(mContext);
                break;
            case R.id.tvKF:
                //客服
                CustomListActivity.actionStart(requireContext());
                break;
            case R.id.tvNickName:
                //个人信息
                MyInfoActivity.actionStart(requireContext());
                break;
            case R.id.stReNew://开通会员
                BuyMemberActivity.actionStart(requireContext());
                break;
            case R.id.tvSettings:
                //设置
//                SetActivity.actionStart(requireContext());
                NanGuoSetActivity.actionStart(mContext);
                break;
            case R.id.tvAboutMe://APP 官网
                WebViewActivity.actionStart(requireContext(), AppConfig.appurl, true);
                break;
            default:
                break;

        }
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
            GlideUtils.loadImageViewLoding(AppConfig.checkimg(loginInfo.getUserHead()), mIvAvatar, R.mipmap.ic_ng_avatar);
            mTvNickName.setText(loginInfo.getNickName());
            if (TextUtils.isEmpty(loginInfo.getUserCode())) {
                mTvID.setText("ID： 无");
            } else {
                mTvID.setText("ID： " + loginInfo.getUserCode());
            }
//            mTvUserLevel.setText("lv " + loginInfo.getUserLevel());
            //是否是会员 vipid
            if (!TextUtils.isEmpty(loginInfo.getVipLevel())) {
                mIvVipIcon.setSelected(true);
                mIvVipLevel.setVisibility(View.VISIBLE);
                mStReNew.setText("立即续费");
                mTvVipIcon.setText("已开通会员");
            } else {
                mIvVipIcon.setSelected(false);
                mIvVipLevel.setVisibility(View.GONE);
                mTvVipIcon.setText("未开通会员");
                mStReNew.setText("立即开通");
            }

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
                if (result.contains("person") || result.contains("group")) {
                    if ("person".equals(result.split("_")[1])) {
                        startActivity(new Intent(requireContext(), UserInfoDetailActivity.class).putExtra("friendUserId", result.split("_")[0]));
                    } else {
                        UserOperateManager.getInstance().scanInviteContact(requireContext(), result);

                    }
                }
            }
        }
    }
}
