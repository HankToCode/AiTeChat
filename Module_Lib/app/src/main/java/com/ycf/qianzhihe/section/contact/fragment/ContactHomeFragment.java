package com.ycf.qianzhihe.section.contact.fragment;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.base.BaseInitFragment;
import com.ycf.qianzhihe.common.utils.AdapterHelper;

import com.hyphenate.easeui.ui.base.EaseBaseFragment;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.ycf.qianzhihe.section.common.ContactSearchActivity;
import com.ycf.qianzhihe.section.search.SearchConversationActivity;

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
import butterknife.OnClick;

public class ContactHomeFragment extends BaseInitFragment {
    private MagicIndicator indicator;
    private ViewPager viewPager;
    TextView tv_search;

    private List<String> titles = Arrays.asList("我的好友", "我的群组", "我的分组");
    private List<EaseBaseFragment> fragments = Arrays.asList(new ContactListFragment(), new GroupContactManageFragment(), new GroupingListFragment());

    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_contact_home;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        indicator = findViewById(R.id.indicator);
        viewPager = findViewById(R.id.view_pager);
        tv_search = findViewById(R.id.tv_search);
        tv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SearchConversationActivity.actionStart(mContext);
                ContactSearchActivity.actionStart(mContext);
            }
        });

        initIndicator(titles);
    }

    @Override
    protected void initListener() {
        super.initListener();
    }

    private void initIndicator(List<String> titles) {

        CommonNavigator navigator = new CommonNavigator(requireContext());
        navigator.setAdjustMode(true);
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titles.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {

                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(Color.parseColor("#ACACBC"));
                colorTransitionPagerTitleView.setSelectedColor(Color.WHITE);
                colorTransitionPagerTitleView.setText(titles.get(index));
                colorTransitionPagerTitleView.setTextSize(15);
                colorTransitionPagerTitleView.setOnClickListener(view -> viewPager.setCurrentItem(index));

                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                //设置头部标签指示器
                WrapPagerIndicator indicator = new WrapPagerIndicator(context);
                indicator.setFillColor(Color.parseColor("#53B0F7"));
                indicator.setHorizontalPadding(DensityUtil.dp2px(10));
                indicator.setVerticalPadding(DensityUtil.dp2px(5));
                indicator.setRoundRadius(DensityUtil.dp2px(25));
                return indicator;
            }
        });

        indicator.setNavigator(navigator);
        ViewPagerHelper.bind(indicator, viewPager);
        AdapterHelper.bind(viewPager, getChildFragmentManager(), fragments);

    }


}
