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
import android.widget.LinearLayout;
import android.widget.TextView;

import com.baidu.mapapi.common.SysOSUtil;
import com.bumptech.glide.Glide;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.R2;
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

    @BindView(R2.id.ll_dc1)
    LinearLayout iv_dc1;
    @BindView(R2.id.ll_dc2)
    LinearLayout iv_dc2;
    @BindView(R2.id.ll_dc3)
    LinearLayout iv_dc3;
    @BindView(R2.id.ll_dc4)
    LinearLayout iv_dc4;
    @BindView(R2.id.ll_dc5)
    LinearLayout iv_dc5;
    @BindView(R2.id.ll_dc6)
    LinearLayout iv_dc6;
    @BindView(R2.id.banner)
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

    @OnClick({R2.id.ll_dc1, R2.id.ll_dc2, R2.id.ll_dc3, R2.id.ll_dc4, R2.id.ll_dc5, R2.id.ll_dc6})
    public void click(View v) {
        String url = "";
        int id = v.getId();
        if (id == R.id.ll_dc1) {//                url = "http://meishi.meituan.com/i/?ci=268&stid_b=1&cevent=imt%2Fhomepage%2Fcategory1%2F1";
            url = "https://www.meituan.com/";
        } else if (id == R.id.ll_dc2) {
            url = "https://h5.ele.me/";
        } else if (id == R.id.ll_dc3) {
            url = "https://www.jd.com/brand/13196fb3651ed8a45efe.html";
        } else if (id == R.id.ll_dc4) {
            url = "https://s.taobao.com/";
        } else if (id == R.id.ll_dc5) {
            url = "https://news.qq.com/";
        } else if (id == R.id.ll_dc6) {
            url = "https://new.qq.com/ch/cul/";
        }
        startActivity(new Intent(mContext, WebViewActivity.class).putExtra("title", "lan").putExtra("url", url));
    }



}
