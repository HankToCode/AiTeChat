package com.ycf.qianzhihe.section.discover;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.base.BaseInitFragment;
import com.ycf.qianzhihe.app.base.WebViewActivity;

import butterknife.BindView;
import butterknife.OnClick;

public class DiscoverFragment extends BaseInitFragment {

    @BindView(R.id.iv_dc1)
    ImageView iv_dc1;
    @BindView(R.id.iv_dc2)
    ImageView iv_dc2;
    @BindView(R.id.iv_dc3)
    ImageView iv_dc3;
    @BindView(R.id.iv_dc4)
    ImageView iv_dc4;
    @BindView(R.id.iv_dc5)
    ImageView iv_dc5;
    @BindView(R.id.iv_dc6)
    ImageView iv_dc6;
    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_discover;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

    }


    @OnClick({R.id.iv_dc1,R.id.iv_dc2,R.id.iv_dc3,R.id.iv_dc4,R.id.iv_dc5,R.id.iv_dc6})
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
