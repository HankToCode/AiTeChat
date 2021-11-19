package com.ycf.qianzhihe.section.account.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.hyphenate.easeui.widget.EaseTitleBar;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.R2;
import com.ycf.qianzhihe.app.adapter.SpecialOfferAdapter;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.new_data.UserCodeMallListBean;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.weight.CommonDialog;
import com.ycf.qianzhihe.app.weight.PasswordEditText;
import com.ycf.qianzhihe.app.weight.passwoed_keyboard.OnNumberKeyboardListener;
import com.ycf.qianzhihe.app.weight.passwoed_keyboard.XNumberKeyboardView;
import com.ycf.qianzhihe.common.utils.ToastUtils;
import com.ycf.qianzhihe.section.common.InputPasswordActivity;
import com.ycf.qianzhihe.section.common.ResetPayPwdActivity;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class BeautifulMallDetailActivity extends BaseInitActivity {

    @BindView(R2.id.title_bar)
    EaseTitleBar title_bar;
    @BindView(R2.id.tv_pay)
    TextView tv_pay;
    @BindView(R2.id.tv_money)
    TextView tv_money;
    @BindView(R2.id.tv_user_code)
    TextView tv_user_code;
    private String money,userCode,id;

    public static void actionStart(Context context, String money, String userCode, String id) {
        Intent intent = new Intent(context, BeautifulMallDetailActivity.class);
        intent.putExtra("money", money);
        intent.putExtra("userCode", userCode);
        intent.putExtra("id", id);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_beautiful_mall_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        title_bar.setOnBackPressListener(view -> finish());
        tv_user_code.setText(userCode);
        tv_money.setText("￥"+money);

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        money = intent.getStringExtra("money");
        userCode = intent.getStringExtra("userCode");
        id = intent.getStringExtra("id");
    }

    @OnClick(R2.id.tv_pay)
    public void click(View v) {
        if (v.getId() == R.id.tv_pay) {
            payPassword();
        }
    }

    private void payPassword() {
        LoginInfo userInfo = UserComm.getUserInfo();
        if (userInfo.getPayPwdFlag() == 0) {
            startActivity(new Intent(mContext, InputPasswordActivity.class));
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
                bugVip(password);
                builder.dismiss();
            }
        });

    }


    private void bugVip(String password) {
        Map<String, Object> map = new HashMap<>();
        map.put("codeId", id);
        map.put("payPassword", password);
        ApiClient.requestNetHandle(mContext, AppConfig.saveUserCodeMall, "购买中...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtils.showToast("购买成功，已为您开启新的ID");
                System.out.println("###会员返回=" + json.toString());
                if (json != null) {
//                    payDialog.setSucc();

                }
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtils.showToast(msg);
            }
        });
    }

}
