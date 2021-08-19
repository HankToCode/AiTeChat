package con.ycf.qianzhihe.section.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.Spanned;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ehking.sdk.wepay.interfaces.WalletPay;
import com.ehking.sdk.wepay.net.bean.AuthType;
import con.ycf.qianzhihe.R;
import con.ycf.qianzhihe.app.api.Constant;
import con.ycf.qianzhihe.app.api.global.EventUtil;
import con.ycf.qianzhihe.app.api.global.UserComm;
import con.ycf.qianzhihe.app.api.old_data.EventCenter;
import con.ycf.qianzhihe.app.api.old_data.LoginInfo;
import con.ycf.qianzhihe.app.api.old_data.WalletTransferBean;
import con.ycf.qianzhihe.app.api.old_data.WalletTransferQueryBean;
import con.ycf.qianzhihe.app.api.old_http.ApiClient;
import con.ycf.qianzhihe.app.api.old_http.AppConfig;
import con.ycf.qianzhihe.app.api.old_http.ResultListener;
import con.ycf.qianzhihe.app.base.BaseInitActivity;
import con.ycf.qianzhihe.app.weight.CommonDialog;
import con.ycf.qianzhihe.app.weight.CustomerKeyboard;
import con.ycf.qianzhihe.app.weight.PasswordEditText;
import con.zds.base.json.FastJsonUtil;
import con.zds.base.upDated.utils.NetWorkUtils;
import con.zds.base.util.StringUtil;

import org.greenrobot.eventbus.EventBus;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import con.zds.base.Toast.ToastUtil;

/**
 * @author lhb
 * 发送个人红包
 */
public class SendPersonRedPackageActivity extends BaseInitActivity {
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.et_red_amount)
    EditText mEtRedAmount;
    @BindView(R.id.et_remark)
    EditText mEtRemark;
    @BindView(R.id.tv_red_amount)
    TextView mTvRedAmount;
    @BindView(R.id.tv_send_red)
    TextView mTvSendRed;

    private String toChatUsername;
    private boolean isSelectBalance = true;

    //结果返回最多重新查询次数
    private int maxCount = 5;
    private Handler handler = new Handler();


    @Override
    protected int getLayoutId() {
        return R.layout.activity_send_person_red_package;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mToolbarTitle.setText("发送红包");
        mEtRedAmount.setInputType(InputType.TYPE_NUMBER_FLAG_DECIMAL | InputType.TYPE_CLASS_NUMBER);
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

        mTvSendRed.setOnClickListener(view -> payPassword());
    }

    @Override
    protected void onEventComing(EventCenter center) {

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        toChatUsername = intent.getStringExtra("username");
    }

    private long mLastClickTime = 0;
    public static final long TIME_INTERVAL = 1000L;

    /**
     * 发红包
     */
    private void sendRedPacket(String password) {

        long nowTime = System.currentTimeMillis();

        if (nowTime - mLastClickTime > TIME_INTERVAL) {
            // do something
            mLastClickTime = nowTime;
        } else {
            return;
        }

        Map<String, Object> map = new HashMap<>(1);
        map.put("money", mEtRedAmount.getText().toString().trim());
        map.put("payPassword", password);
        map.put("toUserId", toChatUsername.split("-")[0]);
//        map.put("payType", isSelectBalance ? 0 : 1);
        String remark =
                StringUtil.isEmpty(mEtRemark.getText().toString().trim()) ?
                        "恭喜发财，大吉大利！" : mEtRemark.getText().toString().trim();
        map.put("remark", remark);

        ApiClient.requestNetHandle(this, AppConfig.CREATE_PERSON_RED_PACKE,
                "正在发红包.." +
                        ".", map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        Intent intent = new Intent();
                        intent.putExtra("money",
                                mEtRedAmount.getText().toString().trim());
                        intent.putExtra("remark",
                                remark);
                        intent.putExtra("redId", json);
                        setResult(Activity.RESULT_OK, intent);
                        ToastUtil.toast(msg);
                        finish();
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                        mLastClickTime = 0;
                    }
                });
    }

    private void doSendRedPackageClick() {
        maxCount = 5;
        if (mEtRedAmount.getText().length() <= 0 || mEtRedAmount.getText().toString().equals("") || mEtRedAmount.getText().toString().equals("0.") || mEtRedAmount.getText().toString().equals("0.0")
                || mEtRedAmount.getText().toString().equals("0.00")) {
            ToastUtil.toast("请填写正确的金额");
            return;
        }

        double price =
                Double.parseDouble(mEtRedAmount.getText().toString().trim());
        if (price > 200) {
            ToastUtil.toast("金额不得超过200元");
            return;
        }

        long nowTime = System.currentTimeMillis();

        if (nowTime - mLastClickTime > TIME_INTERVAL) {
            // do something
            mLastClickTime = nowTime;
        } else {
            return;
        }

        Map<String, Object> map = new HashMap<>(1);
        map.put("money", mEtRedAmount.getText().toString().trim());
        map.put("toUserId", toChatUsername.split("-")[0]);
        map.put("payPassword", "123456");
//        map.put("payType", isSelectBalance ? 0 : 1);
        String remark =
                StringUtil.isEmpty(mEtRemark.getText().toString().trim()) ?
                        "恭喜发财，大吉大利！" : mEtRemark.getText().toString().trim();
        map.put("remark", remark);
        map.put("ip", NetWorkUtils.getIPAddress(true));
        ApiClient.requestNetHandle(this, AppConfig.CREATE_PERSON_RED_PACKE,
                "正在发红包.." +
                        ".", map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        /*Intent intent = new Intent();
                        intent.putExtra("money",
                                mEtRedAmount.getText().toString().trim());
                        intent.putExtra("remark",
                                remark);
                        intent.putExtra("redId", json);
                        setResult(Activity.RESULT_OK, intent);
                        toast(msg);
                        finish();*/
                        if (json != null && json.length() > 0) {
                            WalletTransferBean walletTransferBean = FastJsonUtil.getObject(json, WalletTransferBean.class);

                            WalletPay walletPay = WalletPay.Companion.getInstance();
                            walletPay.init(SendPersonRedPackageActivity.this);
                            walletPay.walletPayCallback = new WalletPay.WalletPayCallback() {
                                @Override
                                public void callback(@Nullable String source, @Nullable String status, @Nullable String errorMessage) {
                                    // TODO: 2021/4/1 通知聊天页面去环信后台拉取红包消息
                                    EventBus.getDefault().post(new EventCenter(EventUtil.SEND_PERSON_RED_PKG));
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
                                    break;
                                case "PROCESS":
                                    handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            maxCount--;
                                            if (maxCount <= 0) {
                                                ToastUtil.toast("发送红包处理中");
                                                finish();
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
        if (mEtRedAmount.getText().length() <= 0 || mEtRedAmount.getText().toString().equals("") || mEtRedAmount.getText().toString().equals("0.") || mEtRedAmount.getText().toString().equals("0.0")
                || mEtRedAmount.getText().toString().equals("0.00")) {
            ToastUtil.toast("请填写正确的金额");
            return;
        }

        double price =
                Double.parseDouble(mEtRedAmount.getText().toString().trim());
        if (price > 200) {
            ToastUtil.toast("金额不得超过200元");
            return;
        }

        LoginInfo userInfo = UserComm.getUserInfo();
        if (userInfo.getPayPwdFlag() == 0) {
            startActivity(new Intent(this,
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
        final CustomerKeyboard mCustomerKeyboard =
                builder.getView(R.id.custom_key_board);
        final PasswordEditText mPasswordEditText =
                builder.getView(R.id.password_edit_text);

        LinearLayout mLlaySelectMode = builder.getView(R.id.llay_select_mode);
        mLlaySelectMode.setVisibility(View.GONE);

        RelativeLayout mLlayBalanceSelect =
                builder.getView(R.id.llay_balance_select);
        ImageView mImgBalanceSelect = builder.getView(R.id.img_balance_select);

        RelativeLayout mLlayBankCarSelect =
                builder.getView(R.id.llay_bank_car_select);
        ImageView mImgBankCarSelect = builder.getView(R.id.img_bank_car_select);

        mLlayBalanceSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectBalance = true;
                mImgBalanceSelect.setVisibility(View.VISIBLE);
                mImgBankCarSelect.setVisibility(View.GONE);
            }
        });

        mLlayBankCarSelect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSelectBalance = false;
                mImgBalanceSelect.setVisibility(View.GONE);
                mImgBankCarSelect.setVisibility(View.VISIBLE);
            }
        });

        mCustomerKeyboard.setOnCustomerKeyboardClickListener(new CustomerKeyboard.CustomerKeyboardClickListener() {
            @Override
            public void click(String number) {
                if ("返回".equals(number)) {
                    builder.dismiss();
                } else if ("忘记密码？".equals(number)) {
                    startActivity(new Intent(SendPersonRedPackageActivity.this, VerifyingPayPasswordPhoneNumberActivity.class));
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

}