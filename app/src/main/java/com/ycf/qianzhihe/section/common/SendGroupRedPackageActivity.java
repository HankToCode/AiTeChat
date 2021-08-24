package com.ycf.qianzhihe.section.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.ehking.sdk.wepay.interfaces.WalletPay;
import com.ehking.sdk.wepay.net.bean.AuthType;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_data.WalletTransferBean;
import com.ycf.qianzhihe.app.api.old_data.WalletTransferQueryBean;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.base.WebViewActivity;
import com.ycf.qianzhihe.app.weight.CommonDialog;
import com.ycf.qianzhihe.app.weight.CustomerKeyboard;
import com.ycf.qianzhihe.app.weight.PasswordEditText;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.upDated.utils.NetWorkUtils;
import com.zds.base.util.StringUtil;

import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import com.zds.base.Toast.ToastUtil;

/**
 * @author lhb
 * 发送个人红包
 */
public class SendGroupRedPackageActivity extends BaseInitActivity {
    @BindView(R.id.title_bar)
    EaseTitleBar title_bar;
    @BindView(R.id.et_red_amount)
    EditText mEtRedAmount;
    @BindView(R.id.tv_red_amount)
    TextView mTvRedAmount;
    @BindView(R.id.et_red_num)
    EditText mEtRedNum;
    @BindView(R.id.et_remark)
    EditText mEtRedMark;

    @BindView(R.id.tv_send_red)
    TextView mTvSendRed;

    private int isSelectBalance = 0;
    private TextView tv_bank_name;
    private String bankId = "";
    private String groupId;
    private String emGroupId;
    private int mGroupUserCount;

    //结果返回最多重新查询次数
    private int maxCount = 5;
    private Handler handler = new Handler();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_send_group_red_package;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        title_bar.setTitle("发送红包");
        title_bar.setOnBackPressListener(view -> finish());

        mTvSendRed.setOnClickListener(view -> {
            //发红包
            payPassword();
//        sendRedPacket();
//        doSendRedPackageClick();
        });

        mEtRedAmount.setFilters(new InputFilter[]{
                new InputFilter() {
                    @Override
                    public CharSequence filter(CharSequence source, int start
                            , int end, Spanned dest, int dstart, int dend) {
                        if (source.equals(".") && dest.toString().length() == 0) {
                            return "0.";
                        }

                        if (dest.toString().contains(".")) {
                            int index = dest.toString().indexOf(".");
                            int length =
                                    dest.toString().substring(index).length();
                            if (length == 3) {
                                return "";
                            }
                        }

                        return null;
                    }
                }
        });

        mEtRedAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mTvRedAmount.setText("￥" + mEtRedAmount.getText().toString().trim());
                    mTvSendRed.setBackgroundResource(R.drawable.bor_login_sel);
                } else {
                    mTvRedAmount.setText("￥0.00");
                    mTvSendRed.setBackgroundResource(R.drawable.bor_login);
                }

            }
        });

    }

    @Override
    protected void onEventComing(EventCenter center) {

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        emGroupId = intent.getStringExtra(Constant.PARAM_EM_GROUP_ID);
        groupId = intent.getStringExtra("groupId");
        mGroupUserCount = intent.getIntExtra("key_intent_group_user_count", 0);
    }


    private long mLastClickTime = 0;
    public static final long TIME_INTERVAL = 1000L;

    /**
     * 发红包
     */
    private void sendRedPacket(String password) {
        long nowTime = System.currentTimeMillis();

        if (nowTime - mLastClickTime > TIME_INTERVAL) {
            mLastClickTime = nowTime;
        } else {
            return;
        }

        Map<String, Object> map = new HashMap<>(1);
        map.put("money", mEtRedAmount.getText().toString().trim());
        map.put("payPassword", password);
        map.put("groupId", groupId);
        map.put("cardId", bankId);
        map.put("packetAmount", mEtRedNum.getText().toString().trim());
        map.put("payType", isSelectBalance);
        map.put("huanxinGroupId", emGroupId);
        String remark =
                StringUtil.isEmpty(mEtRedMark.getText().toString().trim()) ?
                        "恭喜发财，大吉大利！" : mEtRedMark.getText().toString().trim();
        map.put("remark", remark);
        map.put("type", "1");//type：1-群红包 2-个人红包 （由于是群红包，这里固定为1即可，必传）
        map.put("redPacketType", "0");//0-拼手气，非专属红包 1-专属红包,2-群平均红包 （必传）
        // TODO: 2021/8/25/025
        /*toUserNickName：专属红包用户昵称 （发送给谁的昵称 ， redPacketType==1时必传）
        belongUserId：专属红包用户id （发送给谁的userId ， redPacketType==1时必传）
        注意：如果是专属红包，则红包个数需要固定为1，单个红包最多200*/

        ApiClient.requestNetHandle(this, AppConfig.CREATE_RED_PACKE, "正在发送红包." +
                "..", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
//                if (isSelectBalance) {
//                toast(msg);
                if (json.length() > 1500) {
                    startActivity(new Intent(SendGroupRedPackageActivity.this, WebViewActivity.class).putExtra("url", json).putExtra("title", "充值"));
                }
                finish();
//                } else {
//                    Bundle bundle = new Bundle();
//                    bundle.putString("url", json);
//                    bundle.putString("title", "银行卡支付");
//                    bundle.putBoolean("isRedPage", true);
//                    bundle.putString("groupId", userName);
//                    startActivity(WebViewActivity.class, bundle);
//                }
                bankId = "";
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                bankId = "";
                mLastClickTime = 0;
            }
        });
    }

    private void doSendRedPackageClick() {
        maxCount = 5;
        isSelectBalance = 0;
        if (mEtRedAmount.getText().length() <= 0
                || mEtRedAmount.getText().toString().equals("")
                || mEtRedAmount.getText().toString().equals("0.")
                || mEtRedAmount.getText().toString().equals("0.0")
                || mEtRedAmount.getText().toString().equals("0.00")) {
            ToastUtil.toast("请填写正确的金额");
            return;
        }

        if (mEtRedNum.getText().toString().length() <= 0) {
            ToastUtil.toast("请填写红包个数");
            return;
        }

        //1.总金额是200乘以个数 单个平均最高是200 2.红包个数最多是100个，同时还要小于群人数；
        int redCount = Integer.parseInt(mEtRedNum.getText().toString().trim());
        //校验总金额
        double redAmount = Double.parseDouble(mEtRedAmount.getText().toString().trim());
        if (redAmount > 200 * redCount) {
            ToastUtil.toast("单个红包不能超过200元");
            return;
        }
        //校验红包个数
        if (redCount > Math.min(mGroupUserCount, 100)) {
            if (mGroupUserCount < 100) {
                ToastUtil.toast("红包个数不能超过群成员总数");
            } else {
                ToastUtil.toast("红包个数不能超过100");
            }
            return;
        }

        long nowTime = System.currentTimeMillis();

        if (nowTime - mLastClickTime > TIME_INTERVAL) {
            mLastClickTime = nowTime;
        } else {
            return;
        }

        Map<String, Object> map = new HashMap<>(1);
        map.put("money", mEtRedAmount.getText().toString().trim());
        map.put("payPassword", "123456");
        map.put("groupId", groupId);
        map.put("cardId", bankId);
        map.put("packetAmount", mEtRedNum.getText().toString().trim());
        map.put("payType", isSelectBalance);
        map.put("huanxinGroupId", emGroupId);
        String remark =
                StringUtil.isEmpty(mEtRedMark.getText().toString().trim()) ?
                        "恭喜发财，大吉大利！" : mEtRedMark.getText().toString().trim();
        map.put("remark", remark);
        map.put("ip", NetWorkUtils.getIPAddress(true));

        ApiClient.requestNetHandle(this, AppConfig.CREATE_RED_PACKE, "正在发送红包." +
                "..", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                /*toast(msg);
                if (json.length()>1500){
                    startActivity(new Intent(SendGroupRedPackageActivity.this, WebViewActivity.class).putExtra("url", json).putExtra("title", "充值"));
                }
                finish();
                bankId = "";*/

                if (json != null && json.length() > 0) {
                    WalletTransferBean walletTransferBean = FastJsonUtil.getObject(json, WalletTransferBean.class);

                    WalletPay walletPay = WalletPay.Companion.getInstance();
                    walletPay.init(SendGroupRedPackageActivity.this);
                    walletPay.walletPayCallback = new WalletPay.WalletPayCallback() {
                        @Override
                        public void callback(@Nullable String source, @Nullable String status, @Nullable String errorMessage) {
                            if (status == "SUCCESS" || status == "PROCESS") {
                                //queryResult(walletTransferBean.requestId);
                            }
                            finish();
                        }
                    };
                    //调起SDK的转账
                    walletPay.evoke(Constant.MERCHANT_ID, UserComm.getUserInfo().ncountUserId,
                            walletTransferBean.token, AuthType./*TRANSFER*/APP_PAY.name());

                } else {
                    ToastUtil.toast("服务器开小差，请稍后重试");
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                bankId = "";
                mLastClickTime = 0;
            }
        });
    }

    private void queryResult(String requestId) {
        Map<String, Object> map = new HashMap<>();
        map.put("requestId", requestId);
        ApiClient.requestNetHandleByGet(this, AppConfig.walletTransferQuery, "请稍等...",
                map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        if (json != null && json.length() > 0) {
                            WalletTransferQueryBean walletTransferQueryBean = FastJsonUtil.getObject(json, WalletTransferQueryBean.class);
                            switch (walletTransferQueryBean.orderStatus) {
                                case "SEND":
                                    ToastUtil.toast("发送红包成功");
                                    finish();
                                    bankId = "";
                                    break;
                                case "PROCESS":
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            maxCount--;
                                            if (maxCount <= 0) {
                                                ToastUtil.toast("发送红包处理中");
                                                finish();
                                                bankId = "";
                                                return;
                                            }
                                            queryResult(requestId);
                                        }
                                    }, 2000);
                                    break;
                                default:
                                    ToastUtil.toast("发送红包失败");
                                    break;
                            }

                        } else {
                            ToastUtil.toast("服务器开小差，请稍后重试");
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                    }
                });
    }


    /**
     * 支付密码
     */
    private void payPassword() {
        isSelectBalance = 0;
        if (mEtRedAmount.getText().length() <= 0
                || mEtRedAmount.getText().toString().equals("")
                || mEtRedAmount.getText().toString().equals("0.")
                || mEtRedAmount.getText().toString().equals("0.0")
                || mEtRedAmount.getText().toString().equals("0.00")) {
            ToastUtil.toast("请填写正确的金额");
            return;
        }

        if (mEtRedNum.getText().toString().length() <= 0) {
            ToastUtil.toast("请填写红包个数");
            return;
        }

        //1.总金额是200乘以个数 单个平均最高是200 2.红包个数最多是100个，同时还要小于群人数；
        int redCount = Integer.parseInt(mEtRedNum.getText().toString().trim());
        //校验总金额
        double redAmount = Double.parseDouble(mEtRedAmount.getText().toString().trim());
        if (redAmount > 200 * redCount) {
            ToastUtil.toast("单个红包不能超过200元");
            return;
        }
        //校验红包个数
        if (redCount > Math.min(mGroupUserCount, 100)) {
            if (mGroupUserCount < 100) {
                ToastUtil.toast("红包个数不能超过群成员总数");
            } else {
                ToastUtil.toast("红包个数不能超过100");
            }
            return;
        }

        LoginInfo userInfo = UserComm.getUserInfo();
        if (userInfo.getPayPwdFlag() == 0) {
            startActivity(new Intent(SendGroupRedPackageActivity.this,
                    InputPasswordActivity.class));
            return;
        }
        final CommonDialog.Builder builder =
                new CommonDialog.Builder(this).fullWidth().fromBottom()
                        .setView(R.layout.dialog_customer_keyboard);

        builder.setOnClickListener(R.id.delete_dialog,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });
        builder.create().show();

        LinearLayout mLlaySelectMode = builder.getView(R.id.llay_select_mode);
        mLlaySelectMode.setVisibility(View.VISIBLE);

        RelativeLayout mLlayBalanceSelect = builder.getView(R.id.llay_balance_select);
        ImageView mImgBalanceSelect = builder.getView(R.id.img_balance_select);

        RelativeLayout mLlayBankCarSelect = builder.getView(R.id.llay_bank_car_select);
        mLlayBankCarSelect.setVisibility(View.GONE);
        ImageView mImgBankCarSelect = builder.getView(R.id.img_bank_car_select);

        RelativeLayout mLlayZfbCarSelect = builder.getView(R.id.llay_zfb_car_select);
        ImageView mImgZfbCarSelect = builder.getView(R.id.img_zfb_car_select);


        tv_bank_name = builder.getView(R.id.tv_bank_name);
        mLlayBalanceSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectBalance = 0;
                mImgBalanceSelect.setVisibility(View.VISIBLE);
                mImgBankCarSelect.setVisibility(View.GONE);
                mImgZfbCarSelect.setVisibility(View.GONE);
            }
        });

        mLlayBankCarSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectBalance = 1;
                mImgBalanceSelect.setVisibility(View.GONE);
                mImgBankCarSelect.setVisibility(View.VISIBLE);
                mImgZfbCarSelect.setVisibility(View.GONE);
                startActivityForResult(new Intent(SendGroupRedPackageActivity.this, BankActivity.class), 66);
            }
        });
        mLlayZfbCarSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isSelectBalance = 2;
                mImgBalanceSelect.setVisibility(View.GONE);
                mImgBankCarSelect.setVisibility(View.GONE);
                mImgZfbCarSelect.setVisibility(View.VISIBLE);
            }
        });


        final CustomerKeyboard mCustomerKeyboard =
                builder.getView(R.id.custom_key_board);
        final PasswordEditText mPasswordEditText =
                builder.getView(R.id.password_edit_text);
        mCustomerKeyboard.setOnCustomerKeyboardClickListener(new CustomerKeyboard.CustomerKeyboardClickListener() {
            @Override
            public void click(String number) {
                if ("返回".equals(number)) {
                    builder.dismiss();
                } else if ("忘记密码？".equals(number)) {
                    startActivity(new Intent(SendGroupRedPackageActivity.this, VerifyingPayPasswordPhoneNumberActivity.class));
                } else {
                    mPasswordEditText.addPassword(number);
                }
            }

            @Override
            public void delete() {
                mPasswordEditText.deleteLastPassword();
            }
        });

        mPasswordEditText.setOnPasswordFullListener(new PasswordEditText.PasswordFullListener() {
            @Override
            public void passwordFull(String password) {
                sendRedPacket(password);
                builder.dismiss();
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 66 && resultCode == 1111) {
            bankId = data.getStringExtra("id");
            String name = data.getStringExtra("name");
            if (tv_bank_name != null) {
                tv_bank_name.setText(name);
            }
        }
    }
}

