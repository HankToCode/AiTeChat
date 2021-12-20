package com.android.nanguo.common.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.android.nanguo.R;

import net.lucode.hackware.magicindicator.FragmentContainerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.model.PositionData;

import java.util.List;


/**
 * 包裹住内容区域的指示器，类似天天快报的切换效果，需要和IMeasurablePagerTitleView配合使用
 * 博客: http://hackware.lucode.net
 * Created by hackware on 2016/6/26.
 */
public class WrapRadiusIndicator extends View implements IPagerIndicator {
    private int mFillColor;

    private List<PositionData> mPositionDataList;
    private Paint mPaint;


    public WrapRadiusIndicator(Context context) {
        super(context);
        init(context);
    }

    private Bitmap bitmapSelect;
    private Bitmap bitmapLeft;
    private Bitmap bitmapCenter;
    private Bitmap bitmapRight;

    private void init(Context context) {
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStyle(Paint.Style.FILL);

        bitmapLeft = ((BitmapDrawable) ContextCompat.getDrawable(context, R.mipmap.bg_tab_contact_left)).getBitmap();
        bitmapCenter = ((BitmapDrawable) ContextCompat.getDrawable(context, R.mipmap.bg_tab_contact_center)).getBitmap();
        bitmapRight = ((BitmapDrawable) ContextCompat.getDrawable(context, R.mipmap.bg_tab_contact_right)).getBitmap();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPaint.setColor(mFillColor);
        if (bitmapSelect != null) {
            canvas.drawBitmap(bitmapSelect, left, 0, mPaint);
        }
    }

    private float left = 0;

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
        if (mPositionDataList == null || mPositionDataList.isEmpty()) {
            return;
        }

        // 计算锚点位置
        PositionData current = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position);
        PositionData pre = FragmentContainerHelper.getImitativePositionData(mPositionDataList, position - 1);

        left = (pre.mContentRight + current.mContentLeft) / 2f;

        if (position == 0) {
            bitmapSelect = bitmapLeft;
            left = 0;
        } else if (position == mPositionDataList.size() - 1) {
            bitmapSelect = bitmapRight;
        } else {
            bitmapSelect = bitmapCenter;
        }

        invalidate();
    }

    @Override
    public void onPageSelected(int position) {
    }

    @Override
    public void onPageScrollStateChanged(int state) {
    }

    @Override
    public void onPositionDataProvide(List<PositionData> dataList) {
        mPositionDataList = dataList;
    }



    public void setFillColor(int fillColor) {
        mFillColor = fillColor;
    }

}
