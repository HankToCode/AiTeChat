package com.ycf.qianzhihe.section.account.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.coorchice.library.SuperTextView;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.R2;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.new_data.VipBean;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.CommonApi;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.weight.ChooseMemberLayout;
import com.ycf.qianzhihe.app.weight.CommonDialog;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.ycf.qianzhihe.app.weight.PasswordEditText;
import com.ycf.qianzhihe.app.weight.passwoed_keyboard.OnNumberKeyboardListener;
import com.ycf.qianzhihe.app.weight.passwoed_keyboard.XNumberKeyboardView;
import com.ycf.qianzhihe.common.utils.ToastUtils;
import com.ycf.qianzhihe.section.common.InputPasswordActivity;
import com.ycf.qianzhihe.section.common.ResetPayPwdActivity;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.NumberUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

//会员信息
public class BuyMemberActivity extends BaseInitActivity implements View.OnClickListener {

    @BindView(R2.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R2.id.ll_my_info)
    ConstraintLayout mLlMyInfo;
    @BindView(R2.id.iv_avatar)
    EaseImageView mIvAvatar;
    @BindView(R2.id.tv_nick_name)
    TextView mTvNickName;
    @BindView(R2.id.tv_user_id)
    TextView mTvUserId;
    @BindView(R2.id.tv_user_level)
    SuperTextView tv_user_level;
    @BindView(R2.id.iv_user_level_tag)
    ImageView iv_user_level_tag;
    @BindView(R2.id.tv_pay)
    SuperTextView tv_pay;
    @BindView(R2.id.cml_member)
    ChooseMemberLayout cml_member;
    private String vipId = "";
    private int vipLevel = 0;
    private String vipPrice = "";


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
        mIvAvatar.setOnClickListener(this);
        tv_pay.setOnClickListener(this);
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
                gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
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
        GlideUtils.GlideLoadCircleErrorImageUtils(this, AppConfig.checkimg(loginInfo.getUserHead()), mIvAvatar, R.mipmap.img_default_avatar);
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


    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.tv_pay) {
            if (!TextUtils.isEmpty(UserComm.getUserInfo().getVipLevel())) {
                if (vipLevel <= NumberUtils.parseDouble(UserComm.getUserInfo().getVipLevel())) {
                    ToastUtil.toast("请选择更高会员等级");
                    return;
                }
            }
            payPassword();
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
        map.put("vipId", vipId);
        map.put("payPassword", password);
        ApiClient.requestNetHandle(mContext, AppConfig.saveUserVip, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
//                    payDialog.setSucc();
                    ToastUtils.showToast("购买成功");
                    CommonApi.upUserInfo(mContext);//刷新用户数据
                    finish();
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtils.showToast(msg);
            }
        });
    }

}
