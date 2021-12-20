package com.android.nanguo.section.account.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.coorchice.library.SuperTextView;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.android.nanguo.R;
import com.android.nanguo.app.api.global.UserComm;
import com.android.nanguo.app.api.new_data.VipBean;
import com.android.nanguo.app.api.old_data.LoginInfo;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.app.weight.ShowMemberContentLayout;
import com.android.nanguo.common.utils.ToastUtils;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.NumberUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

//账号等级权益
public class AccountInfoActivity extends BaseInitActivity {

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

    @BindView(R.id.cml_member)
    ShowMemberContentLayout cml_member;
    @BindView(R.id.cml_member2)
    ShowMemberContentLayout cml_member2;
    @BindView(R.id.cml_member3)
    ShowMemberContentLayout cml_member3;
    @BindView(R.id.cml_member4)
    ShowMemberContentLayout cml_member4;
    @BindView(R.id.tv_l1)
    TextView tv_l1;
    @BindView(R.id.tv_l2)
    TextView tv_l2;
    @BindView(R.id.tv_l3)
    TextView tv_l3;
    @BindView(R.id.tv_l4)
    TextView tv_l4;
    @BindView(R.id.tv_l1_tip)
    TextView tv_l1_tip;
    @BindView(R.id.tv_l2_tip)
    TextView tv_l2_tip;
    @BindView(R.id.tv_l3_tip)
    TextView tv_l3_tip;
    @BindView(R.id.tv_l4_tip)
    TextView tv_l4_tip;
    @BindView(R.id.iv_up)
    ImageView iv_up;


    public static void actionStart(Context context) {
        Intent starter = new Intent(context, AccountInfoActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_account_info;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initImmersionBar(true);
        if (!TextUtils.isEmpty(UserComm.getUserInfo().getVipLevel())) {
            if (NumberUtils.parseDouble(UserComm.getUserInfo().getVipLevel()) == 1) {
                tv_l1.setText("我的Lv1会员权益");
                tv_l1.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else if (NumberUtils.parseDouble(UserComm.getUserInfo().getVipLevel()) == 2) {
                tv_l2.setText("我的Lv2会员权益");
                tv_l2.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else if (NumberUtils.parseDouble(UserComm.getUserInfo().getVipLevel()) == 3) {
                tv_l3.setText("我的Lv3会员权益");
                tv_l3.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            } else if (NumberUtils.parseDouble(UserComm.getUserInfo().getVipLevel()) == 4) {
                tv_l4.setText("我的Lv4会员权益");
                tv_l4.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
            }
        }
        tv_l1_tip.setText(Html.fromHtml("LV1全部" + "<font color='#C29525'>" + "4项" + "</font>" + "权益"));
        tv_l2_tip.setText(Html.fromHtml("LV2全部" + "<font color='#C29525'>" + "5项" + "</font>" + "权益"));
        tv_l3_tip.setText(Html.fromHtml("LV3全部" + "<font color='#C29525'>" + "6项" + "</font>" + "权益"));
        tv_l4_tip.setText(Html.fromHtml("LV4全部" + "<font color='#C29525'>" + "8项" + "</font>" + "权益"));
    }

    @OnClick({R.id.iv_up})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.iv_up:
                BuyMemberActivity.actionStart(this);
                finish();
                break;
        }
    }

    @Override
    protected void initListener() {
        super.initListener();
        mTitleBar.setOnBackPressListener(view -> finish());
        mTitleBar.setLeftImageResource(R.mipmap.icon_back_white);
    }

    @Override
    protected void initData() {
        super.initData();
        initUserInfo();
//        initVipInfo();
        showVipView();
    }

    private void initVipInfo() {
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandle(mContext, AppConfig.getSysVipInfo, "加载中..", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    List<VipBean> vipBeanList = new ArrayList<>();
                    vipBeanList.addAll(FastJsonUtil.getList(json, VipBean.class));

                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtils.showToast(msg);
            }
        });
    }

    private void showVipView() {
        List<String> data = new ArrayList<>();
        data.add("好友人数");
        data.add("加速升级");
        data.add("会员标识");
        data.add("红色昵称");
        //初始化
        cml_member.setMoneyData(data);
        int size = data.size();
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
        cml_member.setHorizontalSpacing(5); // 设置列表项水平间距
        cml_member.setStretchMode(GridView.NO_STRETCH);
        cml_member.setNumColumns(size); // 设置列数量=列表集合数

        //l2
        List<String> data2 = new ArrayList<>();
        data2.add("好友人数");
        data2.add("加速升级");
        data2.add("会员标识");
        data2.add("红色昵称");
        data2.add("星标好友");
        //初始化
        cml_member2.setMoneyData(data2);
        int size2 = data2.size();
        int gridviewWidth2 = (int) (size2 * (length + 4) * density);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                gridviewWidth2, LinearLayout.LayoutParams.FILL_PARENT);
        cml_member2.setLayoutParams(params2); // 设置GirdView布局参数,横向布局的关键
        cml_member2.setColumnWidth(itemWidth); // 设置列表项宽
        cml_member2.setHorizontalSpacing(5); // 设置列表项水平间距
        cml_member2.setStretchMode(GridView.NO_STRETCH);
        cml_member2.setNumColumns(size2); // 设置列数量=列表集合数

        //l3
        List<String> data3 = new ArrayList<>();
        data3.add("好友人数");
        data3.add("加速升级");
        data3.add("会员标识");
        data3.add("红色昵称");
        data3.add("星标好友");
        data3.add("动态表情");
        //初始化
        cml_member3.setMoneyData(data3);
        int size3 = data3.size();
        int gridviewWidth3 = (int) (size3 * (length + 4) * density);
        LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
                gridviewWidth3, LinearLayout.LayoutParams.FILL_PARENT);
        cml_member3.setLayoutParams(params3); // 设置GirdView布局参数,横向布局的关键
        cml_member3.setColumnWidth(itemWidth); // 设置列表项宽
        cml_member3.setHorizontalSpacing(5); // 设置列表项水平间距
        cml_member3.setStretchMode(GridView.NO_STRETCH);
        cml_member3.setNumColumns(size3); // 设置列数量=列表集合数

        //l4
        List<String> data4 = new ArrayList<>();
        data4.add("好友人数");
        data4.add("加速升级");
        data4.add("会员标识");
        data4.add("红色昵称");
        data4.add("星标好友");
        data4.add("动态表情");
        data4.add("群排名靠前");
        data4.add("聊天背景");
        //初始化
        cml_member4.setMoneyData(data4);
        int size4 = data4.size();
        int gridviewWidth4 = (int) (size4 * (length + 4) * density);
        LinearLayout.LayoutParams params4 = new LinearLayout.LayoutParams(
                gridviewWidth4, LinearLayout.LayoutParams.FILL_PARENT);
        cml_member4.setLayoutParams(params4); // 设置GirdView布局参数,横向布局的关键
        cml_member4.setColumnWidth(itemWidth); // 设置列表项宽
        cml_member4.setHorizontalSpacing(5); // 设置列表项水平间距
        cml_member4.setStretchMode(GridView.NO_STRETCH);
        cml_member4.setNumColumns(size4); // 设置列数量=列表集合数
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
        //是否是会员
        if (!TextUtils.isEmpty(loginInfo.getVipLevel())) {
            iv_user_level_tag.setBackgroundResource(R.drawable.ic_mine_level_tag);
        } else {
            iv_user_level_tag.setBackgroundResource(R.drawable.ic_mine_level_tag_normal);
        }

    }

}
