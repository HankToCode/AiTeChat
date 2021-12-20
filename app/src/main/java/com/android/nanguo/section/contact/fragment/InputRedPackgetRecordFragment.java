package com.android.nanguo.section.contact.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.android.nanguo.R;
import com.android.nanguo.app.adapter.ChatRedRecordAdapter;
import com.android.nanguo.app.api.old_data.MyRedInfo;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitFragment;
import com.android.nanguo.app.help.RclViewHelp;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;


public class InputRedPackgetRecordFragment extends BaseInitFragment {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_no_data)
    TextView tv_no_data;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private ChatRedRecordAdapter mRedRecordAdapter;
    private List<MyRedInfo.DataBean> mPacketInfoList = new ArrayList<>();
    private int page = 1;


    @Override
    protected int getLayoutId() {
        return R.layout.fragemnt_record;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mRedRecordAdapter = new ChatRedRecordAdapter(mPacketInfoList);
        RclViewHelp.initRcLmVertical(mContext, recyclerView, mRedRecordAdapter);

        mRedRecordAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                queryRed();
            }
        });

        swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright,
                R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                queryRed();
            }
        });
        queryRed();
        swipeRefreshLayout.setRefreshing(true);
    }



    @Override
    protected void initData() {
        super.initData();
        queryRed();
    }

    /**
     * 查询红包
     */
    private void queryRed() {
        Map<String, Object> map = new HashMap<>(3);
        map.put("type", "101");//收101   发的100
        map.put("pageNum", page);
        map.put("pageSize", 10);

        ApiClient.requestNetHandle(mContext, AppConfig.RED_PACK_RECORD, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                MyRedInfo info = FastJsonUtil.getObject(json, MyRedInfo.class);
                if (page == 1) {
                    mPacketInfoList.clear();
                }
                swipeRefreshLayout.setRefreshing(false);
                if (info.getData() != null && info.getData().size() > 0) {
                    mPacketInfoList.addAll(info.getData());
                    mRedRecordAdapter.notifyDataSetChanged();
                    mRedRecordAdapter.loadMoreComplete();
                    tv_no_data.setVisibility(View.GONE);
                } else {
                    if (page == 1) {
                        tv_no_data.setVisibility(View.VISIBLE);
                    }
                    mRedRecordAdapter.loadMoreEnd(false);
                }
            }

            @Override
            public void onFailure(String msg) {
                mRedRecordAdapter.loadMoreFail();
            }
        });


    }


}
