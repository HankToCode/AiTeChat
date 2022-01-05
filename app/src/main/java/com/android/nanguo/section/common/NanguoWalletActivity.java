package com.android.nanguo.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.alibaba.fastjson.JSON;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.android.nanguo.R;
import com.android.nanguo.app.api.global.EventUtil;
import com.android.nanguo.app.api.global.UserComm;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.api.old_data.LoginInfo;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitActivity;
import com.zds.base.util.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 钱包
 */
public class NanguoWalletActivity extends BaseInitActivity {
    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.tv_money)
    TextView tv_money;
    @BindView(R.id.tv_status)
    TextView tv_status;
    @BindView(R.id.tv_bank_status)
    TextView tv_bank_status;



    public static void actionStart(Context context) {
        Intent intent = new Intent(context, NanguoWalletActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_wallet_nanguo;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initImmersionBar(false);
        mTitleBar.setOnBackPressListener(view -> finish());
        mTitleBar.getRightText().setTextColor(ContextCompat.getColor(mContext, R.color.white));
        mTitleBar.setOnRightClickListener(view -> JYRecordActivity.actionStart(mContext));

        tv_money.setText(StringUtil.getFormatValue2(UserComm.getUserInfo().getMoney()));
        if (UserComm.getUserInfo().getOpenAccountFlag() == 0) {
            tv_status.setText("待实名");
        } else {
            tv_status.setText("已实名");
        }
        tv_bank_status.setText("");

    }


    @OnClick({ R.id.ll_name, R.id.tv_recharge, R.id.tv_withdraw, R.id.ll_bank})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_name:
                if (UserComm.getUserInfo().getOpenAccountFlag() == 0) {
                    RealAuthActivity.actionStart(mContext);
                    return;
                }
                break;
            case R.id.tv_recharge://充值
                if (UserComm.getUserInfo().getOpenAccountFlag() == 0) {
                    RealAuthActivity.actionStart(mContext);
                    return;
                }
                RechargeActivity.actionStart(NanguoWalletActivity.this);//充值页面
                break;
            case R.id.tv_withdraw://提现
                if (UserComm.getUserInfo().getOpenAccountFlag() == 0) {
                    RealAuthActivity.actionStart(mContext);
                    return;
                }
                WithdrawActivity.actionStart(NanguoWalletActivity.this);
                break;
            case R.id.ll_bank://银行卡
//                startActivity(new Intent(WalletActivity.this,WebViewActivity.class).putExtra("url",url).putExtra("title","银行卡"));
                if (UserComm.getUserInfo().getOpenAccountFlag() == 0) {
                    RealAuthActivity.actionStart(mContext);
                    return;
                }
                BankActivity.actionStart(this,"1");
                break;
           /* case R.id.tv_my_redpack_record:
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
                break;*/
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
            tv_money.setText(StringUtil.getFormatValue2(UserComm.getUserInfo().getMoney()));
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
                        tv_money.setText(StringUtil.getFormatValue2(loginInfo.getMoney()));
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }


}
