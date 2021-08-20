package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.adapter.RechargeRecordAdapter;
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

/**
 * 充值记录
 */
public class RechargeRecordActivity extends BaseInitActivity {


    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.rv_red_record)
    RecyclerView mRvRedRecord;
    private List<RechargeRecordInfo.DataBean> mRecordInfoList = new ArrayList<>();
    private RechargeRecordAdapter mRechargeAdapter;
    private int page = 1;


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, RechargeRecordActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_transfer_record;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar.setTitle("充值记录");
        mTitleBar.setOnBackPressListener(view -> finish());
        mRechargeAdapter = new RechargeRecordAdapter(mRecordInfoList);
        RclViewHelp.initRcLmVertical(this, mRvRedRecord, mRechargeAdapter);
        mRechargeAdapter.openLoadAnimation();
        mRechargeAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                queryRechargeRecord();
            }
        },mRvRedRecord);
        queryRechargeRecord();
    }





    /**
     * 查询红包
     */
    private void queryRechargeRecord() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("pageNum", page);
        map.put("pageSize", 15);

        ApiClient.requestNetHandle(this, AppConfig.GET_RECHARGE_RECORD, "", map, new ResultListener() {
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
