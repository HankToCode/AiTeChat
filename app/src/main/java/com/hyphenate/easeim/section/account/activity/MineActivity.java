package com.hyphenate.easeim.section.account.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.coorchice.library.SuperTextView;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.app.api.bean.EventCenter;
import com.hyphenate.easeim.app.api.bean.LoginInfo;
import com.hyphenate.easeim.app.api.bean.StoreShowSwitchBean;
import com.hyphenate.easeim.app.api.global.EventUtil;
import com.hyphenate.easeim.app.api.global.UserComm;
import com.hyphenate.easeim.app.api.http_old.ApiClient;
import com.hyphenate.easeim.app.api.http_old.AppUrls;
import com.hyphenate.easeim.app.api.http_old.ResultListener;
import com.hyphenate.easeim.app.base.BaseInitActivity;
import com.hyphenate.easeim.common.utils.GlideUtils;
import com.hyphenate.easeim.common.utils.json.FastJsonUtil;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class MineActivity extends BaseInitActivity implements View.OnClickListener {
    private ImageView mIvBack;
    private CircleImageView mIvAvatar;
    private TextView mTvNickName;
    private TextView mTvUserId;
    private SuperTextView mTvUserLevel;
    private ImageView mIvUserLevelTag;
    private ImageView mIvScan;
    private ImageView mIvQr;
    private LinearLayout mLlPackage;
    private LinearLayout mLlMember;
    private LinearLayout mLlCollection;
    private LinearLayout mLlSettings;
    private LinearLayout mLlHelpline;
    private LinearLayout mLlGroups;
    private TextView mTvSwitchAccount;
    private TextView mTvLogout;


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
        mIvAvatar = (CircleImageView) findViewById(R.id.iv_avatar);
        mTvNickName = (TextView) findViewById(R.id.tv_nick_name);
        mTvUserId = (TextView) findViewById(R.id.tv_user_id);
        mTvUserLevel = (SuperTextView) findViewById(R.id.tv_user_level);
        mIvUserLevelTag = (ImageView) findViewById(R.id.iv_user_level_tag);
        mIvScan = (ImageView) findViewById(R.id.iv_scan);
        mIvQr = (ImageView) findViewById(R.id.iv_qr);
        mLlPackage = (LinearLayout) findViewById(R.id.ll_package);
        mLlMember = (LinearLayout) findViewById(R.id.ll_member);
        mLlCollection = (LinearLayout) findViewById(R.id.ll_collection);
        mLlSettings = (LinearLayout) findViewById(R.id.ll_settings);
        mLlHelpline = (LinearLayout) findViewById(R.id.ll_helpline);
        mLlGroups = (LinearLayout) findViewById(R.id.ll_groups);
        mTvSwitchAccount = (TextView) findViewById(R.id.tv_switch_account);
        mTvLogout = (TextView) findViewById(R.id.tv_logout);

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
        mLlPackage.setOnClickListener(this);
        mLlMember.setOnClickListener(this);
        mLlCollection.setOnClickListener(this);
        mLlSettings.setOnClickListener(this);
        mLlHelpline.setOnClickListener(this);
        mLlGroups.setOnClickListener(this);
        mTvSwitchAccount.setOnClickListener(this);
        mTvLogout.setOnClickListener(this);

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
            GlideUtils.GlideLoadCircleErrorImageUtils(this, AppUrls.checkimg(loginInfo.getUserHead()), mIvAvatar, R.mipmap.img_default_avatar);
            mTvNickName.setText(loginInfo.getNickName());
            if (TextUtils.isEmpty(loginInfo.getUserCode())) {
                mTvUserId.setText("ID： 无");
            } else {
                mTvUserId.setText("ID： " + loginInfo.getUserCode());
            }
        }
    }

    private void getStoreShowSwitch() {
        mLlGroups.setVisibility(View.GONE);
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandleNoParam(this, AppUrls.showStore,
                "", new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        if (json != null && json.length() > 0) {
                            StoreShowSwitchBean storeShowSwitchBean = FastJsonUtil.getObject(json, StoreShowSwitchBean.class);
                            if(storeShowSwitchBean.status == 1){
                                mLlGroups.setVisibility(View.VISIBLE);
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
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.FLUSHUSERINFO) {
            initUserInfo();
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
                break;
            case R.id.iv_qr:
                break;
            case R.id.ll_package:
                break;
            case R.id.ll_member:
                break;
            case R.id.ll_collection:
                break;
            case R.id.ll_settings:
                break;
            case R.id.ll_helpline:
                break;
            case R.id.ll_groups:
                break;
            case R.id.tv_switch_account:
                break;
            case R.id.tv_logout:
                break;
            default:
                break;

        }

    }
}
