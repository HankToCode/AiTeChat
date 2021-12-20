/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.nanguo.section.common;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.android.nanguo.R;
import com.android.nanguo.app.adapter.FragmentAdapter;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.app.domain.EaseShowBigImageNewItem;
import com.android.nanguo.app.api.old_data.EventCenter;

import java.util.ArrayList;
import java.util.List;

/**
 * download and show original image
 *
 */
public class EaseShowBigImageNewActivity extends BaseInitActivity {

    private static final String TAG = "EaseShowBigImageNewActivity";
    private ViewPager mBigShowVp;

    @Override
    protected int getLayoutId() {
        return R.layout.ease_activity_show_big_image_new;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mBigShowVp = findViewById(R.id.ac_easy_big_image_vp);
        List<Fragment> fragments = new ArrayList<>();
        List<String> titles = new ArrayList<>();
        ArrayList<EaseShowBigImageNewItem> images = getIntent().getParcelableArrayListExtra("images");
        int position = getIntent().getIntExtra("position", 0);
        for (EaseShowBigImageNewItem item : images) {
            fragments.add(EaseShowBigImageNewFragment.newInstance(item));
            titles.add("");
        }
        mBigShowVp.setAdapter(new FragmentAdapter(getSupportFragmentManager(), fragments, titles));
        mBigShowVp.setOnClickListener(v -> finish());
        if (position != 0)
            mBigShowVp.setCurrentItem(position);
    }


    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {

    }

}
