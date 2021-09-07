package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.widget.EditText;
import android.widget.TextView;

import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.old_data.BankDetailInfo;
import com.ycf.qianzhihe.app.api.old_data.BankDto;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.utils.PhoneFormatUtil;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import com.ycf.qianzhihe.app.weight.ConfirmInputDialog;
import com.ycf.qianzhihe.common.utils.ToastUtils;
import com.zds.base.Toast.ToastUtil;

/**
 * 银行卡
 */
public class BankNewActivity extends BaseInitActivity {
    
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, BankNewActivity.class);
        context.startActivity(intent);
    }

    @BindView(R.id.et_bank_card_number)
    EditText mBankCardNum;
    @BindView(R.id.et_bank_location)
    EditText mBankLocation;
    @BindView(R.id.et_bank_phone)
    EditText mBankPhone;
    @BindView(R.id.et_identity_card_number)
    EditText mIdentityCardNum;
    @BindView(R.id.et_bank_card_name)
    EditText mBankCardName;
    @BindView(R.id.tv_new_bank_card_submit)
    TextView mNewBankCardSubmit;

    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    BankDetailInfo bankDetailInfo;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bank_new;
    }



    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar.setTitle("添加银行卡");
        mTitleBar.setOnBackPressListener(view -> finish());
        mBankCardName.setFilters(new InputFilter[]{filter});
        mBankLocation.setFilters(new InputFilter[]{filter});
    }

    /**
     * 绑定银行卡
     */
    private void addBankCard() {
        Map<String, Object> map = new HashMap<>();
        map.put("realName", mBankCardName.getText().toString().trim());
        map.put("idCard", mIdentityCardNum.getText().toString().trim());
        map.put("bankCard", mBankCardNum.getText().toString().trim());
        map.put("bankName", mBankLocation.getText().toString().trim());
        map.put("bankPhone", mBankPhone.getText().toString().trim());
        ApiClient.requestNetHandle(this, AppConfig.addBankCardList, "正在提交...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                BankDto dto = new BankDto();
                dto.setBankCard(map.get("bankCard").toString());
                dto.setBankName(map.get("bankName").toString());
                dto.setBankPhone(map.get("bankPhone").toString());
                dto.setIdCard(map.get("idCard").toString());
                dto.setRealName(map.get("realName").toString());
                dto.setTokenId(json);
//                Bundle b = new Bundle();
//                b.putSerializable("bean",dto);
//                startActivity(CheakBankActivity.class,b);

                ConfirmInputDialog dialog = new ConfirmInputDialog(mContext);
                dialog.setOnConfirmClickListener(new ConfirmInputDialog.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(String content) {
                        sureBankCard(dto,content);
                    }
                });
                dialog.show();

                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setTitle("输入验证码");
                dialog.setContentHint("输入验证码");
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    /**
     * 确认绑定银行卡
     *
     * @param dto
     * @param content
     */
    private void sureBankCard(BankDto dto, String content) {
        Map<String, Object> map = new HashMap<>();
        map.put("realName", dto.getRealName());
        map.put("idCard", dto.getIdCard());
        map.put("bankCard", dto.getBankCard());
        map.put("bankName", dto.getBankName());
        map.put("bankPhone", dto.getBankPhone());
        map.put("tokenId", dto.getTokenId());
        map.put("verifyCode", content);
        ApiClient.requestNetHandle(this, AppConfig.check_bank, "请稍后...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtils.showToast("绑定成功");
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });

    }


    @OnClick(R.id.tv_new_bank_card_submit)
    public void onViewClicked() {
        if (checkForm()) {
            addBankCard();
        }
    }

    private boolean checkForm() {
        if (mBankCardNum.getText().toString().trim().length() <= 0) {
            ToastUtil.toast("请输入卡号");
            return false;
        } else if (mBankLocation.getText().toString().trim().length() <= 0) {
            ToastUtil.toast("请输入开户行");
            return false;
        } else if (mBankPhone.getText().toString().trim().length() <= 0) {
            ToastUtil.toast("请输入银行预留电话");
            return false;
        } else if (!PhoneFormatUtil.isPhoneNumberValid(mBankPhone.getText().toString().trim())) {
            ToastUtil.toast("请输入正确的银行预留电话");
            return false;
        } else if (mIdentityCardNum.getText().toString().trim().length() <= 0) {
            ToastUtil.toast("请输入您的身份证号");
            return false;
        }
//        else if (!IDCardValidateUtils.validate_effective(mIdentityCardNum.getText().toString().trim())) {
//            toast("请输入正确的身份证号");
//            return false;
//        }
        else if (mBankCardName.getText().toString().trim().length() <= 0) {
            ToastUtil.toast("请输入您的姓名");
            return false;
        }
        return true;
    }

    /**
     * 绑定银行卡
     */
    private void apply() {
//
//        Map<String, Object> map = new HashMap<>();
//        //银行帐号
//        map.put("bankNumber", erKahao.getText().toString());
//        //银行
//        map.put("bankName", erBank.getText().toString());
//        //类型
//        map.put("type","1");
//        ApiClient.requestNetHandle(this, AppConfig.addBank, "正在提交...", map, new ResultListener() {
//            @Override
//            public void onSuccess(String json, String msg) {
//                toast(msg);
//                finish();
//            }
//
//            @Override
//            public void onFailure(String msg) {
//                toast(msg);
//            }
//        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 13) {
            if (resultCode == 102) {
                String realName = data.getStringExtra("realName");
                //upRealName(realName);
            }
        }
    }

//     /**
//     * 更新真实姓名
//     */
//    private void upRealName(final String realName) {
//        Map<String, Object> map = new HashMap<>();
//        map.put("realName", realName);
//        ApiClient.requestNetHandle(this, AppConfig.upDataRealName, "正在更新...", map, new ResultListener() {
//            @Override
//            public void onSuccess(String json, String msg) {
//                UserInfo userInfo = MyApplication.getInstance().getUserInfo();
//                userInfo.setRealName(realName);
//                MyApplication.getInstance().saveUserInfo(userInfo);
//                erName.setText(realName);
//                ToastUtil.toast(msg);
//            }
//
//            @Override
//            public void onFailure(String msg) {
//                ToastUtil.toast(msg);
//            }
//        });
//    }

    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == 1011) {
            finish();
        }
    }


    /**
     * 判定输入汉字
     *
     * @param c
     * @return
     */
    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        if (ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS) {
            return true;
        }
        return false;
    }

    InputFilter filter = new InputFilter() {
        @Override
        public CharSequence filter(CharSequence source, int start, int end,
                                   Spanned dest, int dstart, int dend) {
            for (int i = start; i < end; i++) {
                if (!isChinese(source.charAt(i))) {
                    return "";
                }
            }
            return null;
        }
    };



}
