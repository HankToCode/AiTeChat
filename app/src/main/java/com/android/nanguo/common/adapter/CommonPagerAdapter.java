package com.android.nanguo.common.adapter;

import android.annotation.SuppressLint;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.viewpager.widget.PagerAdapter;

import com.hyphenate.easeui.ui.base.EaseBaseFragment;

import java.util.ArrayList;
import java.util.List;

public class CommonPagerAdapter extends PagerAdapter {
    private List<EaseBaseFragment> fragments = new ArrayList<>();
    private static final String TAG = "FragmentPagerAdapter";
    private static final boolean DEBUG = false;
    private final FragmentManager mFragmentManager;
    private FragmentTransaction mCurTransaction = null;
    private EaseBaseFragment mCurrentPrimaryItem = null;

    public CommonPagerAdapter(FragmentManager fm, List<EaseBaseFragment> fragments) {
        this(fm);
        this.fragments = fragments;
    }

    public CommonPagerAdapter(FragmentManager fm) {
        this.mFragmentManager = fm;
    }

    public Fragment getItem(int var1) {
        return fragments.get(var1);
    }


    @Override
    public int getCount() {
        return fragments.size();
    }

    @Override
    public void startUpdate(@NonNull ViewGroup container) {
        if (container.getId() == -1) {
            throw new IllegalStateException("ViewPager with adapter " + this + " requires a view id");
        }
    }

    @Override
    @SuppressLint("CommitTransaction")
    @NonNull
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        if (this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }

        long itemId = this.getItemId(position);
        String name = makeFragmentName(container.getId(), itemId);
        Fragment fragment = this.mFragmentManager.findFragmentByTag(name);
        if (fragment != null) {
            this.mCurTransaction.attach(fragment);
            this.mCurTransaction.show(fragment);
        } else {
            fragment = this.getItem(position);
            this.mCurTransaction.add(container.getId(), fragment, makeFragmentName(container.getId(), itemId));
        }

        if (fragment != this.mCurrentPrimaryItem) {
            fragment.setMenuVisibility(false);
            fragment.setUserVisibleHint(false);
        }

        return fragment;
    }

    @Override
    @SuppressLint("CommitTransaction")
    public void destroyItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        if (this.mCurTransaction == null) {
            this.mCurTransaction = this.mFragmentManager.beginTransaction();
        }

        this.mCurTransaction.hide((Fragment) object);
    }

    @Override
    public void setPrimaryItem(@NonNull ViewGroup container, int position, @NonNull Object object) {
        EaseBaseFragment fragment = (EaseBaseFragment) object;
        if (fragment != this.mCurrentPrimaryItem) {
            if (this.mCurrentPrimaryItem != null) {
                this.mCurrentPrimaryItem.setMenuVisibility(false);
                this.mCurrentPrimaryItem.setUserVisibleHint(false);
            }

            fragment.setMenuVisibility(true);
            fragment.setUserVisibleHint(true);
            this.mCurrentPrimaryItem = fragment;
        }

    }

    @Override
    public void finishUpdate(@NonNull ViewGroup container) {
        if (this.mCurTransaction != null) {
            this.mCurTransaction.commitNowAllowingStateLoss();
            this.mCurTransaction = null;
        }

    }

    @Override
    public boolean isViewFromObject(@NonNull View view, @NonNull Object object) {
        return ((Fragment) object).getView() == view;
    }

    @Override
    public Parcelable saveState() {
        return null;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
    }

    public long getItemId(int position) {
        return (long) position;
    }

    private static String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}