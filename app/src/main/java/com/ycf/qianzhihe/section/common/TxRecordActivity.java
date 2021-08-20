package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.adapter.RechargeAdapter;
import com.ycf.qianzhihe.app.api.old_data.RechargeRecordInfo;
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
 * 提现记录
 */
public class TxRecordActivity extends BaseInitActivity {
    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;

    @BindView(R.id.recyclerView)
    RecyclerView mRvRedRecord;
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

        queryTxRecord();
    }



    private boolean isRequest;
    /**
     * 提现
     */
    private void withdraw(String id){
        if (isRequest){
            ToastUtil.toast("加载中，请勿重复提交");
            return;
        }
        isRequest=true;
        Map<String,Object> map =new HashMap<>();
        map.put("walletWithdrawId",id);
        ApiClient.requestNetHandle(this, AppConfig.afterWithdraw, "请求中...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
//                startActivity(new Intent(TxRecordActivity.this,WebViewActivity.class).putExtra("url",json).putExtra("title","提现"));
            }

            @Override
            public void onFinsh() {
                super.onFinsh();
                isRequest=false;
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }



    /**
     * 查询提现记录
     */
    private void queryTxRecord() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("pageNum", page);
        map.put("pageSize", 15);
        ApiClient.requestNetHandle(this, AppConfig.GET_WITHDRAAW_RECORD, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                RechargeRecordInfo info = FastJsonUtil.getObject(json, RechargeRecordInfo.class);
                if (info.getData() != null && info.getData().size() > 0) {
                    mRecordInfoList.addAll(info.getData());
                    mRechargeAdapter.notifyDataSetChanged();
                    mRechargeAdapter.loadMoreComplete();
                } else {
                    mRechargeAdapter.loadMoreEnd(true);
                }
            }
            @Override
            public void onFailure(String msg) {
                mRechargeAdapter.loadMoreFail();
            }
        });


    }


}
