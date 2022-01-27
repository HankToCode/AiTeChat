package com.android.nanguo.section.me;

import static com.darsh.multipleimageselect.helpers.Constants.REQUEST_CODE;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.alibaba.fastjson.JSON;
import com.android.nanguo.app.api.global.EventUtil;
import com.android.nanguo.app.api.new_data.VipBean;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.WebViewActivity;
import com.android.nanguo.common.utils.ToastUtils;
import com.coorchice.library.SuperTextView;
import com.hyphenate.easeui.widget.EaseImageView;
import com.android.nanguo.R;
import com.android.nanguo.app.api.Constant;
import com.android.nanguo.app.api.Global;
import com.android.nanguo.app.api.global.UserComm;
import com.android.nanguo.app.api.old_data.LoginInfo;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.base.BaseInitFragment;
import com.android.nanguo.app.operate.UserOperateManager;
import com.android.nanguo.section.account.activity.AccountInfoActivity;
import com.android.nanguo.section.account.activity.BuyMemberActivity;
import com.android.nanguo.section.account.activity.MyInfoActivity;
import com.android.nanguo.section.account.activity.UserInfoDetailActivity;
import com.android.nanguo.section.chat.activity.CustomListActivity;
import com.android.nanguo.section.common.MyQrActivity;
import com.android.nanguo.section.common.NanGuoSetActivity;
import com.android.nanguo.section.common.NanguoWalletActivity;
import com.android.nanguo.section.common.RealAuthActivity;
import com.android.nanguo.section.me.activity.AboutHxActivity;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.code.activity.CaptureActivity;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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
    private TextView mTvMall;
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
        mTvMall = (TextView) findViewById(R.id.tvMall);


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
        mTvMall.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.ivAvatar:
            case R.id.tvNickName:
                //个人信息
                MyInfoActivity.actionStart(requireContext());
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

            case R.id.stReNew://开通会员
                BuyMemberActivity.actionStart(requireContext());
                break;
            case R.id.tvSettings:
                //设置
//                SetActivity.actionStart(requireContext());
                NanGuoSetActivity.actionStart(mContext);
                break;
            case R.id.tvAboutMe://APP 官网
//                WebViewActivity.actionStart(requireContext(), AppConfig.appurl, true);
                AboutHxActivity.actionStart(mContext);
                break;
            case R.id.tvMall://mall
//                WebViewActivity.actionStart(requireContext(), AppConfig.appurl, true);
                WebViewActivity.actionStart(mContext, "http://120.79.221.59:82/shop#/index");
                break;
            default:
                break;

        }
    }

    @Override
    protected void onEventComing(EventCenter center) {
        super.onEventComing(center);
        if (center.getEventCode() == EventUtil.FLUSHUSERINFO) {
            initUserInfo();
        }
    }

    @Override
    protected void initData() {
        super.initData();
        initUserInfo();
    }

    @Override
    public void onResume() {
        super.onResume();
        initUserInfo();
        walletCloseStatus();
    }

    private void walletCloseStatus() {
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandle(mContext, AppConfig.walletCloseStatus, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    LoginInfo loginInfo = JSON.parseObject(json, LoginInfo.class);
                    Log.d("TAG", "钱包控制状态=" + loginInfo.getWalletCloseStatus());
                    if (loginInfo.getWalletCloseStatus().equals("close")) {
                        mTvPay.setVisibility(View.GONE);
                    } else {
                        mTvPay.setVisibility(View.VISIBLE);
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtils.showToast(msg);
            }
        });
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
            if (!TextUtils.isEmpty(loginInfo.getSign())) {
                mTvDesc.setText(loginInfo.getSign());
            } else {
                mTvDesc.setText("这家伙很懒，啥都没写");
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
