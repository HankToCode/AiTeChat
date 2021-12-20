package com.android.nanguo.common.utils;

import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import com.android.nanguo.common.adapter.CommonPagerAdapter;
import com.hyphenate.easeui.ui.base.EaseBaseFragment;

import java.util.List;

public class AdapterHelper {

    public static void bind(ViewPager viewPager, FragmentManager fm, List<EaseBaseFragment> items) {
        CommonPagerAdapter adapter = new CommonPagerAdapter(fm, items);
        viewPager.setAdapter(adapter);

    }


}
