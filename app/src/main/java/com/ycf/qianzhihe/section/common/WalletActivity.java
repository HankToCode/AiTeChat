package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


import com.alibaba.fastjson.JSON;
import com.ycf.qianzhihe.DemoApplication;
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
import com.ycf.qianzhihe.app.utils.XClickUtil;
import com.ycf.qianzhihe.app.weight.CommonDialog;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.zds.base.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import com.zds.base.Toast.ToastUtil;

import org.greenrobot.eventbus.EventBus;

/**
 * 钱包
 */
public class WalletActivity extends BaseInitActivity {
    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.tv_amount)
    TextView mTvAmount;



    public static void actionStart(Context context) {
        Intent intent = new Intent(context, WalletActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wallet;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar.setOnBackPressListener(view -> finish());
        mTvAmount.setText(StringUtil.getFormatValue2(UserComm.getUserInfo().getMoney()));
    }


    @OnClick({ R.id.tv_wallet_lock, R.id.tv_recharge, R.id.tv_withdraw, R.id.tv_pay_manage, R.id.tv_my_redpack_record, R.id.tv_my_transfer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_recharge:
                //充值
                //判断是否开通钱包账户
                /*if (TextUtils.isEmpty(UserComm.getUserInfo().ncountUserId)) {
                    OpenWalletActivity.start(WalletActivity.this);
                } else {
                    RechargeNewActivity.start(WalletActivity.this);
                }*/
                RechargeActivity.actionStart(WalletActivity.this);//充值页面
                break;
            case R.id.tv_withdraw:

                WithdrawActivity.actionStart(WalletActivity.this);
                break;
            case R.id.tv_pay_manage://银行卡
//                startActivity(new Intent(WalletActivity.this,WebViewActivity.class).putExtra("url",url).putExtra("title","银行卡"));
                BankActivity.actionStart(this);
                break;
            case R.id.tv_my_redpack_record:
                //我的红包记录
//                startActivity(MyRedRecordActivity.class);
                ChatRedRecordActivity.actionStart(mContext);
                break;
            case R.id.tv_my_transfer:
                //我的转账记录
                TransferRecordActivity.actionStart(this);
                break;
            case R.id.tv_wallet_lock:
                //零钱锁
                WalletLockActivity.actionStart(this);
                break;
            default:
                break;
        }
    }

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.FLUSHUSERINFO) {
            mTvAmount.setText(StringUtil.getFormatValue2(UserComm.getUserInfo().getMoney()));
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
//        CommonApi.upUserInfo(DemoApplication.getInstance().getApplicationContext());
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandle(mContext, AppConfig.USER_INFO, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    LoginInfo loginInfo = JSON.parseObject(json, LoginInfo.class);
                    if (loginInfo != null) {
                        loginInfo.setPassword(UserComm.getUserInfo().getPassword());
                        UserComm.saveUsersInfo(loginInfo);
                        EventBus.getDefault().post(new EventCenter(EventUtil.FLUSHUSERINFO));
                        mTvAmount.setText(StringUtil.getFormatValue2(loginInfo.getMoney()));
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }


}
