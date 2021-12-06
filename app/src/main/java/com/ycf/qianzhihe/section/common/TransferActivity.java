package com.ycf.qianzhihe.section.common;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.EaseImageView;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.operate.UserOperateManager;
import com.ycf.qianzhihe.app.utils.CashierInputFilter;
import com.ycf.qianzhihe.app.utils.ImageUtil;
import com.ycf.qianzhihe.app.weight.CommonDialog;
import com.ycf.qianzhihe.app.weight.PasswordEditText;
import com.ycf.qianzhihe.app.weight.passwoed_keyboard.OnNumberKeyboardListener;
import com.ycf.qianzhihe.app.weight.passwoed_keyboard.XNumberKeyboardView;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import com.zds.base.Toast.ToastUtil;

/**
 * @author lhb
 * 转账
 */
public class TransferActivity extends BaseInitActivity {
    @BindView(R.id.img_head)
    EaseImageView mImgHead;
    @BindView(R.id.nickName)
    TextView mNickName;
    @BindView(R.id.et_money)
    EditText mEtMoney;
    @BindView(R.id.et_remark)
    EditText mEtRemark;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.tv_transfer)
    TextView mTvTransfer;
    private String emChatId;

    @BindView(R.id.ll_back)
    LinearLayout ll_back;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_transfer;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mToolbarTitle.setText("转账");
        mEtMoney.setFilters(new InputFilter[]{new CashierInputFilter()});
        mEtMoney.addTextChangedListener(new TextWatcher() {
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
                    mTvTransfer.setBackgroundResource(R.drawable.shap_blue);
                } else {
                    mTvTransfer.setBackgroundResource(R.drawable.shape_transter_nor);
                }

            }
        });
        ll_back.setOnClickListener(view -> finish());
    }

    @Override
    protected void onEventComing(EventCenter center) {

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);

        emChatId = intent.getStringExtra("emChatId");
        mNickName.setText(UserOperateManager.getInstance().getUserName(emChatId));
        ImageUtil.setAvatar(mImgHead);
        GlideUtils.loadImageViewLoding(UserOperateManager.getInstance().getUserAvatar(emChatId), mImgHead, R.mipmap.ic_ng_avatar);

    }

    /**
     * 转账
     */
    private void transfer(String password) {
        if (StringUtil.isEmpty(mEtMoney)) {
            ToastUtil.toast("请输入转账金额");
            return;
        }
        Map<String, Object> map = new HashMap<>(1);
        map.put("money", mEtMoney.getText().toString().trim());
        map.put("payPassword", password);
        map.put("remark", mEtRemark.getText().toString());
        map.put("userId", emChatId.contains(Constant.ID_REDPROJECT) ?
                emChatId.split("-")[0] : emChatId);
        ApiClient.requestNetHandle(this, AppConfig.transfer, "发送中...", map,
                new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        //发送名片
                        Intent intent = new Intent();
                        intent.putExtra("money", mEtMoney.getText().toString().trim());
                        intent.putExtra("remark", mEtRemark.getText().toString().trim());
                        intent.putExtra("turnId", json);
                        setResult(Activity.RESULT_OK, intent);
                        ToastUtil.toast("转账成功");
                        finish();
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });
    }


    /**
     * send  拓展消息message （转账）
     */
    protected void sendExTextMessage(String content, String money,
                                     String toChatUsername) {
        EMMessage message = EMMessage.createTxtSendMessage(content,
                toChatUsername);
        message.setAttribute(Constant.TURN, true);
        message.setAttribute("money", money);
        message.setAttribute(Constant.AVATARURL,
                UserComm.getUserInfo().getUserHead());
        message.setAttribute(Constant.NICKNAME,
                UserComm.getUserInfo().getNickName());
        EMClient.getInstance().chatManager().sendMessage(message);
    }

    @OnClick({R.id.tv_transfer})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_transfer:
                hideKeyboard();
                //转账
                PayPassword();
                break;
            default:
                break;
        }
    }


    /**
     * 支付密码
     */
    private void PayPassword() {
        if (mEtMoney.getText().length() <= 0 || mEtMoney.getText().toString().equals("") || mEtMoney.getText().toString().equals("0.") || mEtMoney.getText().toString().equals("0.0")
                || mEtMoney.getText().toString().equals("0.00")) {
            ToastUtil.toast("请填写正确的金额");
            return;
        }

        double price =
                Double.parseDouble(mEtMoney.getText().toString().trim());
        if (price > 10000) {
            ToastUtil.toast("金额不得超过10000元");
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
                transfer(password);
            }
        });

    }
}
