package com.ycf.qianzhihe.section.common;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ehking.sdk.wepay.interfaces.WalletPay;
import com.ehking.sdk.wepay.net.bean.AuthType;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.ContactListInfo;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_data.WalletTransferBean;
import com.ycf.qianzhihe.app.api.old_data.WalletTransferQueryBean;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.base.WebViewActivity;
import com.ycf.qianzhihe.app.utils.NumberExKt;
import com.ycf.qianzhihe.app.weight.CommonDialog;
import com.ycf.qianzhihe.app.weight.PasswordEditText;
import com.ycf.qianzhihe.app.weight.passwoed_keyboard.OnNumberKeyboardListener;
import com.ycf.qianzhihe.app.weight.passwoed_keyboard.XNumberKeyboardView;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.upDated.utils.NetWorkUtils;
import com.zds.base.util.NumberUtils;
import com.zds.base.util.StringUtil;

import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * @author lhb
 * 发送个人红包
 */
public class SendGroupRedPackageActivity extends BaseInitActivity {
    @BindView(R.id.title_bar)
    EaseTitleBar title_bar;

    @BindView(R.id.ll_send_to)
    LinearLayout llSendTo;
    @BindView(R.id.tv_send_to)
    TextView tvSendTo;

    @BindView(R.id.ll_num)
    LinearLayout llNum;
    @BindView(R.id.et_red_num)
    EditText etRedNum;

    @BindView(R.id.ll_total_amount)
    LinearLayout llTotalAmount;
    @BindView(R.id.et_red_amount)
    EditText etRedAmount;
    @BindView(R.id.tv_total_title)
    TextView tvTotalTitle;

    @BindView(R.id.ll_remark)
    LinearLayout llRemark;
    @BindView(R.id.et_remark)
    EditText etRemark;

    @BindView(R.id.tv_red_amount)
    TextView tvRedAmount;
    @BindView(R.id.tv_send_red)
    TextView mTvSendRed;

    @BindView(R.id.tv_select)
    TextView tvSelect;
    @BindView(R.id.root)
    LinearLayout root;
    @BindView(R.id.tv_member)
    TextView tv_member;

    private int isSelectBalance = 0;
    private TextView tv_bank_name;
    private String bankId = "";
    private String groupId;
    private String emGroupId;
    private int mGroupUserCount;

    //结果返回最多重新查询次数
    private int maxCount = 5;
    private Handler handler = new Handler();

    private List<String> list = new ArrayList<>();


    @Override
    protected int getLayoutId() {
        return R.layout.activity_send_group_red_package;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initImmersionBar(false);
        title_bar.setTitle("千纸鹤红包");
        title_bar.setOnBackPressListener(view -> finish());

        mTvSendRed.setOnClickListener(view -> {
            //发红包
            payPassword();
        });

        llSendTo.setOnClickListener(view -> {
            ContactActivity.actionStart(mContext, "4", "", groupId);
        });

        list.add("拼手气红包");
        list.add("专属红包");
        list.add("普通红包");
        tvSelect.setOnClickListener(view -> {
            toSelectItem();//更换样式
        });


        etRedAmount.setFilters(filters);

        etRedAmount.addTextChangedListener(textWatcher);
        etRedNum.addTextChangedListener(textWatcher);

        switchMethod(currentRedPackageMethod);
        tv_member.setOnClickListener(view -> ToastUtil.toast("即将推出，敬请期待"));

    }

    private void toSelectItem() {
        final CommonDialog.Builder builder = new CommonDialog.Builder(this).fullWidth().fromBottom().setView(R.layout.dialog_redpackage_select);
        builder.setOnClickListener(R.id.tv_psj, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                switchMethod(0);
            }
        });
        builder.setOnClickListener(R.id.tv_zs, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                switchMethod(1);
            }
        });
        builder.setOnClickListener(R.id.tv_pt, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                builder.dismiss();
                switchMethod(2);
            }
        });
        builder.setOnClickListener(R.id.tv_cell, view -> builder.dismiss());
        builder.create().show();
    }

    private ContactListInfo.DataBean dataBean;

    @Override
    protected void onEventComing(EventCenter center) {

        if (center.getEventCode() == EventUtil.SEND_PERSON_RED_PKG_PRIVATE) {
            dataBean = (ContactListInfo.DataBean) center.getData();
            tvSendTo.setText(dataBean.getNickName());
        }
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        emGroupId = intent.getStringExtra(Constant.PARAM_EM_GROUP_ID);
        groupId = intent.getStringExtra("groupId");
        mGroupUserCount = intent.getIntExtra("key_intent_group_user_count", 0);
    }


    private final InputFilter[] filters = new InputFilter[]{
            (source, start, end, dest, dstart, dend) -> {
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
    };

    private String money;

    private final TextWatcher textWatcher = new TextWatcher() {
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
            changeAmount();
        }
    };

    private void changeAmount() {

        String redAmount = etRedAmount.getText().toString().trim();

        if (currentRedPackageMethod == 0) {
            money = redAmount;
        } else if (currentRedPackageMethod == 1) {
            money = redAmount;
        } else if (currentRedPackageMethod == 2) {
            money = "" + (NumberUtils.parseDouble(redAmount) * NumberUtils.parseInt(etRedNum.getText().toString().trim()));
        }

        if (NumberUtils.parseDouble(money) > 0) {
            tvRedAmount.setText("￥" + money);
            mTvSendRed.setEnabled(true);
        } else {
            tvRedAmount.setText("￥0.00");
            mTvSendRed.setEnabled(false);
        }

    }

    private void changeAmountSet() {

        if (StringUtil.isEmpty(etRedNum.getText().toString().trim()) || StringUtil.isEmpty(money))
            return;

        if (NumberUtils.parseDouble(money) > 0) {
            if (currentRedPackageMethod == 0) {
                etRedAmount.setText(money);
            } else if (currentRedPackageMethod == 1) {
                etRedAmount.setText(money);
            } else if (currentRedPackageMethod == 2) {
                etRedAmount.setText(NumberExKt.format2(NumberUtils.parseDouble(money) / NumberUtils.parseInt(etRedNum.getText().toString().trim())));
            }
        }
    }

    private int currentRedPackageMethod = 0;//0-拼手气红包 1-专属红包,2-普通发红包

    private void switchMethod(int redPackageMethod) {
        currentRedPackageMethod = redPackageMethod;
        tvSelect.setText(list.get(redPackageMethod));
        llNum.setVisibility(View.GONE);
        llTotalAmount.setVisibility(View.GONE);
        llSendTo.setVisibility(View.GONE);
        llRemark.setVisibility(View.GONE);

        if (redPackageMethod == 0) {
            llNum.setVisibility(View.VISIBLE);
            llTotalAmount.setVisibility(View.VISIBLE);
            tvTotalTitle.setText("总金额");
            llRemark.setVisibility(View.VISIBLE);
        } else if (redPackageMethod == 1) {
            llSendTo.setVisibility(View.VISIBLE);
            llTotalAmount.setVisibility(View.VISIBLE);
            tvTotalTitle.setText("单个金额");
            llRemark.setVisibility(View.VISIBLE);
        } else if (redPackageMethod == 2) {
            llNum.setVisibility(View.VISIBLE);
            llTotalAmount.setVisibility(View.VISIBLE);
            tvTotalTitle.setText("单个金额");
            llRemark.setVisibility(View.VISIBLE);
        }

        changeAmountSet();

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
        map.put("money", money);
        map.put("payPassword", password);
        map.put("groupId", groupId);
        map.put("cardId", bankId);
        map.put("packetAmount", currentRedPackageMethod == 1 ? 1 : etRedNum.getText().toString().trim());
        map.put("payType", isSelectBalance);
        map.put("huanxinGroupId", emGroupId);
        map.put("type", 1);//type：1-群红包 2-个人红包 （由于是群红包，这里固定为1即可，必传）
        map.put("redPacketType", currentRedPackageMethod);//0-拼手气，非专属红包 1-专属红包,2-群平均红包 （必传）
        if (currentRedPackageMethod == 1) {
            if (dataBean != null) {
                map.put("toUserNickName", dataBean.getNickName());
                map.put("belongUserId", dataBean.getFriendUserId());//对方的id
            } else {
                ToastUtil.toast("请先选择群成员");
                return;
            }

        }

        String remark =
                StringUtil.isEmpty(etRemark.getText().toString().trim()) ?
                        "恭喜发财，大吉大利！" : etRemark.getText().toString().trim();
        map.put("remark", remark);

        ApiClient.requestNetHandle(this, AppConfig.CREATE_RED_PACKE, "发送中...", map, new ResultListener() {
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
        if (etRedAmount.getText().length() <= 0
                || etRedAmount.getText().toString().equals("")
                || etRedAmount.getText().toString().equals("0.")
                || etRedAmount.getText().toString().equals("0.0")
                || etRedAmount.getText().toString().equals("0.00")) {
            ToastUtil.toast("请填写正确的金额");
            return;
        }

        if (etRedNum.getText().toString().length() <= 0) {
            ToastUtil.toast("请填写红包个数");
            return;
        }

        //1.总金额是200乘以个数 单个平均最高是200 2.红包个数最多是100个，同时还要小于群人数；
        int redCount = currentRedPackageMethod == 1 ? 1 : Integer.parseInt(etRedNum.getText().toString().trim());
        //校验总金额
        double redAmount = Double.parseDouble(etRedAmount.getText().toString().trim());
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
        map.put("money", etRedAmount.getText().toString().trim());
        map.put("payPassword", "123456");
        map.put("groupId", groupId);
        map.put("cardId", bankId);
        map.put("packetAmount", money);
        map.put("payType", isSelectBalance);
        map.put("huanxinGroupId", emGroupId);
        String remark =
                StringUtil.isEmpty(etRemark.getText().toString().trim()) ?
                        "恭喜发财，大吉大利！" : etRemark.getText().toString().trim();
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
        if (etRedAmount.getText().length() <= 0
                || etRedAmount.getText().toString().equals("")
                || etRedAmount.getText().toString().equals("0.")
                || etRedAmount.getText().toString().equals("0.0")
                || etRedAmount.getText().toString().equals("0.00")) {
            ToastUtil.toast("请填写正确的金额");
            return;
        }

        if (currentRedPackageMethod != 1 && etRedNum.getText().toString().length() <= 0) {
            ToastUtil.toast("请填写红包个数");
            return;
        }

        //1.总金额是200乘以个数 单个平均最高是200 2.红包个数最多是100个，同时还要小于群人数；
        int redCount = currentRedPackageMethod == 1 ? 1 : Integer.parseInt(etRedNum.getText().toString().trim());
        //校验总金额
        double redAmount = NumberUtils.parseDouble(etRedAmount.getText().toString().trim());
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
        final CommonDialog.Builder builder = new CommonDialog.Builder(this).fullWidth().fromBottom()
                        .setView(R.layout.dialog_customer_keyboard);
        builder.setOnClickListener(R.id.delete_dialog,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        builder.dismiss();
                    }
                });
        builder.create().show();
        builder.getView(R.id.tv_forget).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ResetPayPwdActivity.actionStart(mContext);
            }
        });
        final XNumberKeyboardView mCustomerKeyboard = builder.getView(R.id.kb_board);
        final PasswordEditText mPasswordEditText = builder.getView(R.id.password_edit_text);
        mCustomerKeyboard.setOnNumberKeyboardListener(new OnNumberKeyboardListener() {
            @Override
            public void onNumberKey(int keyCode, String insert) {
                // 右下角按键的点击事件，删除一位输入的文字
                if (keyCode == XNumberKeyboardView.KEYCODE_BOTTOM_RIGHT) {
                    mPasswordEditText.deleteLastPassword();
                }
                // 左下角按键和数字按键的点击事件，输入文字
                else {
                    mPasswordEditText.addPassword(insert);
                }

            }
        });

        mPasswordEditText.setOnPasswordFullListener(new PasswordEditText.PasswordFullListener() {
            @Override
            public void passwordFull(String password) {
                builder.dismiss();
                sendRedPacket(password);
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

