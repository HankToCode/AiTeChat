package com.android.nanguo.section.contact.fragment;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.android.nanguo.R;
import com.android.nanguo.app.adapter.TransferAdapter;
import com.android.nanguo.app.api.old_data.TransferRecordInfo;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitFragment;
import com.android.nanguo.app.help.RclViewHelp;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;


public class TransferRecordFragment extends BaseInitFragment {
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_no_data)
    TextView tv_no_data;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private TransferAdapter mTransferAdapter;
    private List<TransferRecordInfo> mRecordInfoList = new ArrayList<>();
    private int page = 1;


    @Override
    protected int getLayoutId() {
        return R.layout.fragemnt_record;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTransferAdapter = new TransferAdapter(mRecordInfoList);
        RclViewHelp.initRcLmVertical(mContext, recyclerView, mTransferAdapter);

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
        transferRecord();
        swipeRefreshLayout.setRefreshing(true);
    }



    @Override
    protected void initData() {
        super.initData();
        transferRecord();
    }
    /**
     * 转账记录
     */
    private void transferRecord() {
        Map<String, Object> map = new HashMap<>(1);
        map.put("pageNum", page);
        map.put("pageSize", 10);
        ApiClient.requestNetHandle(mContext, AppConfig.TRANSFER_RECORD, "", map, new ResultListener() {
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
                    tv_no_data.setVisibility(View.GONE);
                } else {
                    if (page == 1) {
                        tv_no_data.setVisibility(View.VISIBLE);
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
