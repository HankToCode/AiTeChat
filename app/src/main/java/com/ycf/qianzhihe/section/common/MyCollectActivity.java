package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.adapter.MyCollectAdapter;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.old_data.CollectInfo;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.help.RclViewHelp;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.scwang.smartrefresh.layout.SmartRefreshLayout;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * @author lhb
 * 我的收藏
 */
public class MyCollectActivity extends BaseInitActivity {

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, MyCollectActivity.class);
        context.startActivity(starter);
    }

    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.rv_collect)
    RecyclerView mRvCollect;
    @BindView(R.id.smart)
    SmartRefreshLayout mSmart;
    @BindView(R.id.tv_no_collect)
    TextView mTvNoCollect;

    private MyCollectAdapter mCollectAdapter;
    private CollectInfo collectInfo;
    private List<CollectInfo.DataBean> dataBean = new ArrayList<>();
    private int page = 1;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_my_collect;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar.setOnBackPressListener(view -> finish());
        mSmart.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(RefreshLayout refreshlayout) {
                page = 1;
                queryCollect();
            }
        });

        mSmart.setOnLoadMoreListener(refreshlayout0 -> {
            page++;
            queryCollect();
        });


        //先设置布局器
//        mRvCollect.setLayoutManager(new LinearLayoutManager(MyCollectActivity.this));
        //设置添加删除动画
        mRvCollect.setItemAnimator(new DefaultItemAnimator());
        mCollectAdapter = new MyCollectAdapter(dataBean);
        RclViewHelp.initRcLmVertical(this, mRvCollect, mCollectAdapter);

//        mRvCollect.setAdapter(mCollectAdapter);


        mCollectAdapter.setDeleteCollectListener(new MyCollectAdapter.OnDeleteCollectListener() {
            @Override
            public void delCollect(String collectId) {
                Map<String, Object> map = new HashMap<>();
                map.put("collectId", collectId);
                ApiClient.requestNetHandle(MyCollectActivity.this, AppConfig.CancelCollect, "", map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        ToastUtil.toast(msg);
                        queryCollect();
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });
            }

            @Override
            public void collect(String collectId, int position) {
                // TODO: 2021/3/4 跳转页面
                String json = FastJsonUtil.toJSONString(dataBean.get(position));
                setResult(1023, new Intent().putExtra(Constant.JSON, json));
                finish();
            }

        });
        mSmart.autoRefresh();
    }

    private void queryCollect() {
        Map<String, Object> map = new HashMap<>();
        map.put("pageNum", page);
        map.put("pageSize", "20");
        ApiClient.requestNetHandle(MyCollectActivity.this, AppConfig.CollectList, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (page == 1) {
                    dataBean.clear();
                }
                collectInfo = FastJsonUtil.getObject(json, CollectInfo.class);
                if (collectInfo != null && collectInfo.getData().size() > 0) {
                    dataBean.addAll(collectInfo.getData());
                    mTvNoCollect.setVisibility(View.GONE);
                } else {
                    if (page == 1) {
                        mTvNoCollect.setVisibility(View.VISIBLE);
                        mSmart.setEnableLoadMore(false);
                    }

                }
                mSmart.finishLoadMore();
                mCollectAdapter.notifyDataSetChanged();
                if (mSmart != null && mSmart.isRefreshing()) {
                    mSmart.finishRefresh();
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                if (mSmart != null && mSmart.isRefreshing()) {
                    mSmart.finishRefresh();
                }
            }
        });
    }


    @Override
    protected void onEventComing(EventCenter center) {
    }


}