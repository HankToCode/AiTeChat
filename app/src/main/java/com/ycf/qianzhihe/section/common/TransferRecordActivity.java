package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.adapter.TransferAdapter;
import com.ycf.qianzhihe.app.api.old_data.TransferRecordInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.help.RclViewHelp;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import com.zds.base.Toast.ToastUtil;


/**
 * 转账记录
 */
public class TransferRecordActivity extends BaseInitActivity {
    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_no_data)
    TextView mTvNoData;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private TransferAdapter mTransferAdapter;
    private List<TransferRecordInfo> mRecordInfoList = new ArrayList<>();
    private int page = 1;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, TransferRecordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_transfer_record;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar.setTitle("转账记录");
        mTitleBar.setOnBackPressListener(view -> finish());
        mTransferAdapter = new TransferAdapter(mRecordInfoList);
        RclViewHelp.initRcLmVertical(this, mRecyclerView, mTransferAdapter);
        mTransferAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                transferRecord();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright,
                R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                transferRecord();
            }
        });

        swipeRefreshLayout.setRefreshing(true);
        transferRecord();
    }




    /**
     * 转账记录
     */
    private void transferRecord() {
        Map<String, Object> map = new HashMap<>(1);
        map.put("pageNum", page);
        map.put("pageSize", 10);
        ApiClient.requestNetHandle(this, AppConfig.TRANSFER_RECORD, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (page == 1) {
                    mRecordInfoList.clear();
                }
                swipeRefreshLayout.setRefreshing(false);
                if (FastJsonUtil.getList(json, TransferRecordInfo.class) != null && FastJsonUtil.getList(json, TransferRecordInfo.class).size() > 0) {
                    mRecordInfoList.addAll(FastJsonUtil.getList(json, TransferRecordInfo.class));
                    mTransferAdapter.notifyDataSetChanged();
                    mTransferAdapter.loadMoreComplete();
                    mTvNoData.setVisibility(View.GONE);
                } else {
                    if (page == 1) {
                        mTvNoData.setVisibility(View.VISIBLE);
                    }
                    mTransferAdapter.loadMoreEnd(false);

                }
            }
            @Override
            public void onFailure(String msg) {
                mTransferAdapter.loadMoreFail();
                ToastUtil.toast(msg);
            }
        });
    }


}
