package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.adapter.RechargeAdapter;
import com.ycf.qianzhihe.app.api.old_data.RechargeRecordInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.help.RclViewHelp;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 提现记录
 */
public class TxRecordActivity extends BaseInitActivity {
    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    @BindView(R.id.recyclerView)
    RecyclerView mRvRedRecord;
    @BindView(R.id.tv_no_data)
    TextView tv_no_data;
    private List<RechargeRecordInfo.DataBean> mRecordInfoList = new ArrayList<>();
    private RechargeAdapter mRechargeAdapter;
    private int page = 1;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, TxRecordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_transfer_record;
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar.setTitle("提现记录");
        mTitleBar.setOnBackPressListener(view -> finish());
        mRechargeAdapter = new RechargeAdapter(mRecordInfoList);
        RclViewHelp.initRcLmVertical(this, mRvRedRecord, mRechargeAdapter);
        mRechargeAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                queryTxRecord();
            }
        });
        swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright,
                R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                page = 1;
                queryTxRecord();
            }
        });

        queryTxRecord();
        swipeRefreshLayout.setRefreshing(true);

    }

    /**
     * 查询提现记录
     */
    private void queryTxRecord() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("pageNum", page);
        map.put("pageSize", 10);
        ApiClient.requestNetHandle(this, AppConfig.GET_WITHDRAAW_RECORD, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                RechargeRecordInfo info = FastJsonUtil.getObject(json, RechargeRecordInfo.class);
                if (page == 1) {
                    mRecordInfoList.clear();
                }
                swipeRefreshLayout.setRefreshing(false);
                if (info.getData() != null && info.getData().size() > 0) {
                    mRecordInfoList.addAll(info.getData());
                    mRechargeAdapter.notifyDataSetChanged();
                    mRechargeAdapter.loadMoreComplete();
                    tv_no_data.setVisibility(View.GONE);
                } else {
                    if (page == 1) {
                        tv_no_data.setVisibility(View.VISIBLE);
                    }
                    mRechargeAdapter.loadMoreEnd(false);
                }

            }
            @Override
            public void onFailure(String msg) {
                mRechargeAdapter.loadMoreFail();
                ToastUtil.toast(msg);
            }
        });


    }


}
