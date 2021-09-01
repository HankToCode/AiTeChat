package com.ycf.qianzhihe.section.common;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import com.amap.api.maps2d.AMap;
import com.amap.api.maps2d.CameraUpdateFactory;
import com.amap.api.maps2d.MapView;
import com.amap.api.maps2d.model.BitmapDescriptor;
import com.amap.api.maps2d.model.BitmapDescriptorFactory;
import com.amap.api.maps2d.model.LatLng;
import com.amap.api.maps2d.model.MarkerOptions;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 环千纸鹤天地图
 *
 * @author lhb
 */
public class HxMapActivity extends BaseInitActivity {
    @BindView(R.id.map)
    MapView mMapView;
    @BindView(R.id.title_bar)
    EaseTitleBar mToolbarTitle;
    private AMap aMap;
    private LatLng mLatLng;
    private String addressDetail;


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mToolbarTitle.setTitle("位置");
        mMapView.onCreate(savedInstanceState);
        init();
    }


    @Override
    protected void onEventComing(EventCenter center) {
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mLatLng = new LatLng(intent.getDoubleExtra(Constant.LATITUDE, 0d), intent.getDoubleExtra(Constant.LONGITUDE, 0d));
        addressDetail = intent.getStringExtra(Constant.ADDRESS_DETAIL);

    }


    private void init() {
        if (aMap == null) {
            aMap = mMapView.getMap();

            MarkerOptions markerOptions = new MarkerOptions();
            markerOptions.position(mLatLng);
            markerOptions.title("当前位置 :" + "\n" + addressDetail);
            markerOptions.visible(true);
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.fromBitmap(BitmapFactory.decodeResource(getResources(), R.mipmap.dinwei_center));
            markerOptions.icon(bitmapDescriptor);
            aMap.addMarker(markerOptions);

            location();
        }
    }


    /**
     * 方法必须重写
     */
    @Override
    public void onResume() {
        super.onResume();
        if (mMapView != null) {
            mMapView.onResume();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onPause() {
        super.onPause();
        if (mMapView != null) {
            mMapView.onPause();
        }
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        mMapView.onSaveInstanceState(outState);
    }

    /**
     * 方法必须重写
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mMapView != null) {
            mMapView.onDestroy();
        }
    }

    private void location() {
        if (mLatLng != null) {
            aMap.moveCamera(CameraUpdateFactory.newLatLngZoom(mLatLng, 16));
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_hx_map;
    }

    @OnClick(R.id.img_start)
    public void onViewClicked() {
        location();
    }
}
