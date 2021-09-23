package com.ycf.qianzhihe.section.discover;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.adapter.BlackAdapter;
import com.ycf.qianzhihe.app.adapter.MyCollectAdapter;
import com.ycf.qianzhihe.app.adapter.NewsAdapter;
import com.ycf.qianzhihe.app.api.old_data.CollectInfo;
import com.ycf.qianzhihe.app.api.old_data.NewsBean;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitFragment;
import com.ycf.qianzhihe.app.base.WebViewActivity;
import com.ycf.qianzhihe.app.help.RclViewHelp;
import com.ycf.qianzhihe.common.widget.BannerImageLoader;
import com.ycf.qianzhihe.section.common.MyCollectActivity;
import com.youth.banner.Banner;
import com.youth.banner.BannerConfig;
import com.youth.banner.Transformer;
import com.youth.banner.listener.OnBannerListener;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class NewsFragment extends BaseInitFragment implements OnBannerListener {


    @BindView(R.id.banner)
    Banner banner;
    @BindView(R.id.rv_recyclerview)
    RecyclerView rv_recyclerview;
    @BindView(R.id.srl_refresh)
    SmartRefreshLayout srl_refresh;
    private ArrayList<Integer> list_path;
    private ArrayList<String> list_title;
    //    private int page = 1;
    private List<NewsBean> dataBean = new ArrayList<>();
    private NewsAdapter mNewsAdapter;
    @Override
    protected int getLayoutId() {
        return R.layout.fragment_news;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        //放图片地址的集合
        list_path = new ArrayList<>();
        list_title = new ArrayList<>();
        list_path.add(R.drawable.img_b1);
        list_path.add(R.drawable.img_b2);
        list_path.add(R.mipmap.icon_top_bg);
        list_title.add("好好学习");
        list_title.add("热爱劳动");
        list_title.add("不搞对象");
        banner.setImages(list_path);
        banner.setBannerTitles(list_title);
        banner.setBannerStyle(BannerConfig.CIRCLE_INDICATOR_TITLE_INSIDE);
        banner.setImageLoader(new BannerImageLoader());
        banner.setBannerAnimation(Transformer.DepthPage);
        banner.setDelayTime(3000);
        banner.isAutoPlay(true);
        banner.setIndicatorGravity(BannerConfig.CENTER).setOnBannerListener(this).start();

        //设置添加删除动画
        rv_recyclerview.setItemAnimator(new DefaultItemAnimator());
        mNewsAdapter = new NewsAdapter(dataBean);
        RclViewHelp.initRcLmVertical(mContext, rv_recyclerview, mNewsAdapter);
        srl_refresh.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
//                page = 1;
                queryNews();
            }
        });

        srl_refresh.setOnLoadMoreListener(refreshlayout0 -> {
//            page++;
            queryNews();
        });
        srl_refresh.autoRefresh();
    }

    private void queryNews() {
        Map<String, Object> map = new HashMap<>();
//        map.put("pageNum", page);
//        map.put("pageSize", "20");
        ApiClient.requestNetHandle(mContext, AppConfig.findAllInformation, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                /*if (page == 1) {
                    dataBean.clear();
                }*/
                dataBean.clear();
                dataBean.addAll(FastJsonUtil.getList(json, NewsBean.class));
                srl_refresh.setEnableLoadMore(false);

                srl_refresh.finishLoadMore();
                mNewsAdapter.notifyDataSetChanged();
                if (srl_refresh != null && srl_refresh.isRefreshing()) {
                    srl_refresh.finishRefresh();
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                if (srl_refresh != null && srl_refresh.isRefreshing()) {
                    srl_refresh.finishRefresh();
                }
            }
        });
    }

    @Override
    public void OnBannerClick(int position) {

    }




}
