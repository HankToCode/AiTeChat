package com.ycf.qianzhihe.section.discover;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.baidu.mapapi.common.SysOSUtil;
import com.bumptech.glide.Glide;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.new_data.ImageListBean;
import com.ycf.qianzhihe.app.base.BaseInitFragment;
import com.ycf.qianzhihe.app.base.WebViewActivity;
import com.ycf.qianzhihe.common.widget.BannerImageLoader;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.youth.banner.loader.ImageLoader;
import com.youth.banner.loader.ImageLoaderInterface;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.OnClick;

public class DiscoverFragment extends BaseInitFragment implements OnBannerListener {

    @BindView(R.id.iv_dc1)
    TextView iv_dc1;
    @BindView(R.id.iv_dc2)
    TextView iv_dc2;
    @BindView(R.id.iv_dc3)
    TextView iv_dc3;
    @BindView(R.id.iv_dc4)
    TextView iv_dc4;
    @BindView(R.id.iv_dc5)
    TextView iv_dc5;
    @BindView(R.id.iv_dc6)
    TextView iv_dc6;
    @BindView(R.id.banner)
    Banner banner;
    private ArrayList<Integer> list_path;
    private ArrayList<String> list_title;

    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_discover;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        //放图片地址的集合
        list_path = new ArrayList<>();
        list_title = new ArrayList<>();
        list_path.add(R.mipmap.qznn);
        list_path.add(R.drawable.img_b1);
        list_path.add(R.drawable.img_b2);
        list_title.add("不搞对象");
        list_title.add("热爱劳动");
        list_title.add("好好学习");
        banner.setImages(list_path);
        banner.setBannerTitles(list_title);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        banner.setImageLoader(new BannerImageLoader());
        banner.setBannerAnimation(Transformer.DepthPage);
        banner.setDelayTime(3000);
        banner.isAutoPlay(true);
        banner.setIndicatorGravity(BannerConfig.CENTER).setOnBannerListener(this).start();
    }

    @Override
    public void OnBannerClick(int position) {

    }

    @OnClick({R.id.iv_dc1, R.id.iv_dc2, R.id.iv_dc3, R.id.iv_dc4, R.id.iv_dc5, R.id.iv_dc6})
    public void click(View v) {
        String url = "";
        switch (v.getId()) {
            case R.id.iv_dc1:
                url = "https://www.meituan.com/";
                break;
            case R.id.iv_dc2:
                url = "https://h5.ele.me/";
                break;
            case R.id.iv_dc3:
                url = "https://www.jd.com/brand/13196fb3651ed8a45efe.html";
                break;
            case R.id.iv_dc4:
                url = "https://main.m.taobao.com/index.html";
                break;
            case R.id.iv_dc5:
                url = "https://cn.bing.com/";
                break;
            case R.id.iv_dc6:
                url = "https://www.baidu.com/";
                break;
        }
        startActivity(new Intent(mContext, WebViewActivity.class).putExtra("title", "lan").putExtra("url", url));
    }



}
