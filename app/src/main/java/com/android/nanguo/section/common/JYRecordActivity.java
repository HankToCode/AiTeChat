package com.android.nanguo.section.common;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.viewpager.widget.ViewPager;

import com.hyphenate.easeui.ui.base.EaseBaseFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.android.nanguo.R;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.common.utils.AdapterHelper;
import com.android.nanguo.section.contact.fragment.InputRedPackgetRecordFragment;
import com.android.nanguo.section.contact.fragment.SendRedPackgetRecordFragment;
import com.android.nanguo.section.contact.fragment.TransferRecordFragment;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.Arrays;
import java.util.List;

import butterknife.BindView;

/**
 * 南国时光交易记录页面（红包记录，转账记录）
 */
public class JYRecordActivity extends BaseInitActivity {
    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.indicator)
    MagicIndicator indicator;
    @BindView(R.id.view_pager)
    ViewPager view_pager;
    private List<EaseBaseFragment> fragments = Arrays.asList(new InputRedPackgetRecordFragment(), new SendRedPackgetRecordFragment(), new TransferRecordFragment());
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, JYRecordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.wallet_jyrecord_activity;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar.setTitle("交易记录");
        mTitleBar.setOnBackPressListener(view -> finish());
        List<String> titles = Arrays.asList("收到红包", "发出红包", "转账记录");
        initIndicator(titles);
    }

    private void initIndicator(List<String> titles) {
        CommonNavigator navigator = new CommonNavigator(mContext);
        navigator.setAdjustMode(true);
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titles.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {
                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(Color.parseColor("#999999"));
                colorTransitionPagerTitleView.setSelectedColor(Color.BLACK);
                colorTransitionPagerTitleView.setText(titles.get(index));
                colorTransitionPagerTitleView.setTextSize(15);
                colorTransitionPagerTitleView.setOnClickListener(view -> view_pager.setCurrentItem(index));
                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                //设置头部标签指示器
               /* WrapRadiusIndicator indicator = new WrapRadiusIndicator(context);
                indicator.setFillColor(Color.WHITE);*/
                //设置头部标签指示器
                WrapPagerIndicator indicator = new WrapPagerIndicator(context);
                indicator.setFillColor(Color.WHITE);
                indicator.setHorizontalPadding(DensityUtil.dp2px(10));
                indicator.setVerticalPadding(DensityUtil.dp2px(5));
                indicator.setRoundRadius(DensityUtil.dp2px(25));
                return indicator;
            }
        });

        indicator.setNavigator(navigator);
        ViewPagerHelper.bind(indicator, view_pager);
        AdapterHelper.bind(view_pager, getSupportFragmentManager(), fragments);

    }



}
