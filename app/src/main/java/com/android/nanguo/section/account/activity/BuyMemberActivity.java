package com.android.nanguo.section.account.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.android.nanguo.DemoApplication;
import com.android.nanguo.app.api.old_data.MemberBuyBean;
import com.android.nanguo.app.api.old_data.WalletRechargeBean;
import com.android.nanguo.app.base.RechargeWebViewActivity;
import com.android.nanguo.app.base.WebViewActivity;
import com.android.nanguo.app.weight.ConfirmInputDialog;
import com.android.nanguo.section.common.BankActivity;
import com.android.nanguo.section.common.SendGroupRedPackageActivity;
import com.coorchice.library.SuperTextView;
import com.android.nanguo.R;
import com.android.nanguo.app.api.global.UserComm;
import com.android.nanguo.app.api.new_data.VipBean;
import com.android.nanguo.app.api.old_data.LoginInfo;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.CommonApi;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.app.weight.ChooseMemberLayout;
import com.android.nanguo.app.weight.CommonDialog;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.android.nanguo.app.weight.PasswordEditText;
import com.android.nanguo.app.weight.passwoed_keyboard.OnNumberKeyboardListener;
import com.android.nanguo.app.weight.passwoed_keyboard.XNumberKeyboardView;
import com.android.nanguo.common.utils.ToastUtils;
import com.android.nanguo.section.common.InputPasswordActivity;
import com.android.nanguo.section.common.ResetPayPwdActivity;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.NumberUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

//会员信息
public class BuyMemberActivity extends BaseInitActivity {

    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.ll_my_info)
    ConstraintLayout mLlMyInfo;
    @BindView(R.id.iv_avatar)
    EaseImageView mIvAvatar;
    @BindView(R.id.tv_nick_name)
    TextView mTvNickName;
    @BindView(R.id.tv_user_id)
    TextView mTvUserId;
    @BindView(R.id.tv_user_level)
    SuperTextView tv_user_level;
    @BindView(R.id.iv_user_level_tag)
    ImageView iv_user_level_tag;
    @BindView(R.id.tv_pay)
    SuperTextView tv_pay;
    @BindView(R.id.cml_member)
    ChooseMemberLayout cml_member;
    private String vipId = "";
    private int vipLevel = 0;
    private String vipPrice = "";
    @BindView(R.id.tv_bank)
    TextView tv_bank;


    public static void actionStart(Context context) {
        Intent starter = new Intent(context, BuyMemberActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bug_member;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initImmersionBar(true);
    }

    @Override
    protected void initListener() {
        super.initListener();
//        mIvAvatar.setOnClickListener(this);
//        tv_pay.setOnClickListener(this);
        mTitleBar.setOnBackPressListener(view -> finish());
        mTitleBar.setLeftImageResource(R.mipmap.icon_back_white);
    }

    @Override
    protected void initData() {
        super.initData();
        initUserInfo();
        initVipInfo();
    }

    private void initVipInfo() {
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandle(mContext, AppConfig.getSysVipInfo, "加载中..", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    List<VipBean> vipBeanList = new ArrayList<>(FastJsonUtil.getList(json, VipBean.class));
                    showVipView(vipBeanList);
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtils.showToast(msg);
            }
        });
    }

    private void showVipView(List<VipBean> data) {

        //初始化金额选择
        cml_member.setMoneyData(data);
        if (!TextUtils.isEmpty(UserComm.getUserInfo().getVipLevel())) {
            int lve = (int) NumberUtils.parseDouble(UserComm.getUserInfo().getVipLevel());
            cml_member.setDefaultPositon(lve - 1);
        } else {
            cml_member.setDefaultPositon(0);
        }
        int size = 4;
        int length = 100;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 4) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        cml_member.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        cml_member.setColumnWidth(itemWidth); // 设置列表项宽
        cml_member.setHorizontalSpacing(2); // 设置列表项水平间距
        cml_member.setStretchMode(GridView.NO_STRETCH);
        cml_member.setNumColumns(size); // 设置列数量=列表集合数
        cml_member.setOnChoseMoneyListener(new ChooseMemberLayout.onChoseMoneyListener() {
            @Override
            public void chooseMoney(int position, boolean isCheck, VipBean itemData) {
                Log.d("tag", "会员选择=" + itemData.getVipPrice());
                //选择金额回调
                if (isCheck) {
                    vipId = itemData.getVipId();
                    vipLevel = (int) NumberUtils.parseDouble(itemData.getVipLevel());
                    vipPrice = itemData.getVipPrice();
                } else {
                    vipId = "";
                    vipPrice = "";
                    vipLevel = 0;
                }
            }
        });
    }

    /**
     * 初始化用户信息
     */
    private void initUserInfo() {
        LoginInfo loginInfo = UserComm.getUserInfo();
        GlideUtils.GlideLoadCircleErrorImageUtils(this, AppConfig.checkimg(loginInfo.getUserHead()), mIvAvatar, R.mipmap.ic_ng_avatar);
        mTvNickName.setText(loginInfo.getNickName());
        if (TextUtils.isEmpty(loginInfo.getUserCode())) {
            mTvUserId.setText("ID： 无");
        } else {
            mTvUserId.setText("ID： " + loginInfo.getUserCode());
        }
        tv_user_level.setText("lv " + loginInfo.getUserLevel());
        //是否是会员 vipid
        if (!TextUtils.isEmpty(loginInfo.getVipLevel())) {
            iv_user_level_tag.setBackgroundResource(R.drawable.ic_mine_level_tag);
//            tv_pay.setShaderStartColor(R.color.cl72);
//            tv_pay.setShaderStartColor(R.color.cl21);
            if (NumberUtils.parseDouble(loginInfo.getVipLevel()) < 4) {
                tv_pay.setText("立即升级 尊享更高权益");
            } else {
                tv_pay.setShaderStartColor(R.color.cl72);
//            tv_pay.setShaderStartColor(R.color.cl21);
                tv_pay.setText("您已是当前最高会员等级");
                tv_pay.setEnabled(false);
            }
        } else {
            tv_pay.setText("立即开通 永久尊享权益");
//            tv_pay.setShaderStartColor(R.color.cle1);
//            tv_pay.setShaderStartColor(R.color.clcb);
            iv_user_level_tag.setBackgroundResource(R.drawable.ic_mine_level_tag_normal);
        }

    }


    @OnClick({R.id.tv_pay, R.id.ll_select_bank})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.tv_pay:
                if (TextUtils.isEmpty(bankId)) {
                    ToastUtils.showToast("请选择银行卡");
                    return;
                }

//                BuyPayActivity.actionStart(BuyMemberActivity.this);
                /*if (!TextUtils.isEmpty(UserComm.getUserInfo().getVipLevel())) {
                    if (vipLevel <= NumberUtils.parseDouble(UserComm.getUserInfo().getVipLevel())) {
                        ToastUtil.toast("请选择更高会员等级");
                        return;
                    }
                }
                */
                payPassword();
                break;
            case R.id.ll_select_bank:
                //BankActivity.actionStart(this,"2");
                Intent intent = new Intent(this, BankActivity.class);
                intent.putExtra("fromType", "2");
                startActivityForResult(intent, 66);
                break;
        }
    }


    private String bankId = "";

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 66 && resultCode == 1111) {
            bankId = data.getStringExtra("id");
//            tv_bank.setText(data.getStringExtra("id"));
            tv_bank.setText(data.getStringExtra("bankCard"));
        }
    }


    private void payPassword() {
        LoginInfo userInfo = UserComm.getUserInfo();
        if (userInfo.getPayPwdFlag() == 0) {
            startActivity(new Intent(BuyMemberActivity.this, InputPasswordActivity.class));
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
        map.put("cardId", bankId);
        map.put("vipId", vipId);
        map.put("payPassword", password);
        ApiClient.requestNetHandle(mContext, AppConfig.saveUserVip, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null && json.length() > 0) {
                    MemberBuyBean memberBuyBean = FastJsonUtil.getObject(json, MemberBuyBean.class);
                    System.out.println("###未签约="+memberBuyBean.getSignStatus());
                    System.out.println("###未签约html="+memberBuyBean.getHtml());
                    //"signStatus":"签约状态：0-未签约，1-已签约(int整型)"
                    if (memberBuyBean.getSignStatus()==0) {
//                        startActivity(new Intent(RechargeActivity.this, WebViewActivity.class).putExtra("url", walletRechargeBean.getHtml()).putExtra("showTitle", "充值"));
                        RechargeWebViewActivity.actionStart(mContext,memberBuyBean.getHtml(),true);
                    } else {
                        ConfirmInputDialog dialog = new ConfirmInputDialog(mContext);
                        dialog.setOnConfirmClickListener(new ConfirmInputDialog.OnConfirmClickListener() {
                            @Override
                            public void onConfirmClick(String content) {
                                doNewRechargeClick(memberBuyBean, content);
                            }
                        });
                        dialog.show();
                        dialog.setCancelable(false);
                        dialog.setCanceledOnTouchOutside(false);
                        dialog.setTitle("输入验证码");
                        dialog.setContentHint("输入验证码");
                    }
                } else {
                    ToastUtils.showToast("服务器开小差，请稍后重试");
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtils.showToast(msg);
            }
        });
    }


    private void doNewRechargeClick(MemberBuyBean memberBuyBean, String verifyCode) {
        Map<String, Object> map = new HashMap<>();
        map.put("payToken", memberBuyBean.getPayToken());
        map.put("orderNo", memberBuyBean.getOrderNo());
        map.put("payOrderNo", memberBuyBean.getPayOrderNo());
        map.put("vipId", vipId);
        map.put("verifyCode", verifyCode);
        ApiClient.requestNetHandle(this, AppConfig.sureUserVip, "确认中...",
                map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        if (json != null && json.length() > 0) {
                            waiting();
                        } else {
                            ToastUtil.toast("服务器开小差，请稍后重试");
                        }
                    }
                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });
    }
    private void waiting() {
        showLoading("购买确认中");
        new Handler().postDelayed(() -> {
            //要执行的操作
            ToastUtils.showToast("购买成功");
            CommonApi.upUserInfo(DemoApplication.getInstance().getApplicationContext());
            dismissLoading();
            finish();
        }, 3000);//3秒
    }

}
