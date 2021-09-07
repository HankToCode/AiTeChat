package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;


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
                if (UserComm.getUserInfo().getOpenAccountFlag() == 0) {
//                    showAuthDialog();
                    RealAuthActivity.actionStart(mContext);
                    return;
                }
                //startActivity(RechargeActivity.class);
                //判断是否开通钱包账户
                /*if (TextUtils.isEmpty(UserComm.getUserInfo().ncountUserId)) {
                    OpenWalletActivity.start(WalletActivity.this);
                } else {
                    RechargeNewActivity.start(WalletActivity.this);
                }*/
                RechargeActivity.actionStart(WalletActivity.this);//充值页面
                break;
            case R.id.tv_withdraw:
                //提现  //判断如果未实名，提示进行实名认证
                if (UserComm.getUserInfo().getOpenAccountFlag() == 0) {
//                    showAuthDialog();
                    RealAuthActivity.actionStart(mContext);
                    return;
                }
                WithdrawActivity.actionStart(WalletActivity.this);
                break;
            case R.id.tv_pay_manage://银行卡
                //判断如果未实名，提示进行实名认证
                if (UserComm.getUserInfo().getOpenAccountFlag() == 0) {
//                    showAuthDialog();
                    RealAuthActivity.actionStart(mContext);
                    return;
                }
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
        CommonApi.upUserInfo(DemoApplication.getInstance().getApplicationContext());
    }


    private CommonDialog.Builder builder;
    private EditText etName, etCard, etPhone;

    /**
     * 实名认证弹窗
     */
    private void showAuthDialog() {
        if (builder != null) {
            builder.dismiss();
        }
        builder = new CommonDialog.Builder(this).fullWidth().center()
                .setView(R.layout.dialog_custinfo);

        builder.setOnClickListener(R.id.tv_sure, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //确定
                if (etName.getText().toString().trim().length() <= 0) {
                    ToastUtil.toast("请填写姓名");
                    return;
                } else if (etCard.getText().toString().trim().length() <= 0) {
                    ToastUtil.toast("请填写身份证号码");
                    return;
                } else if (etPhone.getText().toString().trim().length() <= 0) {
                    ToastUtil.toast("请填写手机号");
                    return;
                }
                /*if (!XClickUtil.isFastDoubleClick(view, 2000))
                    save();*/
                openAccount(etName.getText().toString().trim(), etCard.getText().toString().trim(), etPhone.getText().toString().trim());

            }
        });
        builder.setOnClickListener(R.id.img_close, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        CommonDialog dialog = builder.create();
        etName = (EditText) dialog.getView(R.id.et_name);
        etCard = (EditText) dialog.getView(R.id.et_card);
        etPhone = (EditText) dialog.getView(R.id.et_phone);
        dialog.show();
    }

    private boolean isrenzheng;

    /**
     * 认证
     */
    private void openAccount(String name, String certificateNo, String mobile) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("certificateNo", certificateNo);
        map.put("mobile", mobile);
        if (isrenzheng == true) {
            ToastUtil.toast("认证中，请勿重复提交");
            return;
        }
        isrenzheng = true;
        ApiClient.requestNetHandle(this, AppConfig.openAccount, "认证中", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                LoginInfo loginInfo = UserComm.getUserInfo();
                loginInfo.setOpenAccountFlag(1);
                UserComm.saveUsersInfo(loginInfo);
                ToastUtil.toast(msg);
                if (builder != null) {
                    builder.dismiss();
                }
            }

            @Override
            public void onFinsh() {
                super.onFinsh();
                isrenzheng = false;
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);

            }
        });
    }


}
