package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

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

    private TransferAdapter mTransferAdapter;
    private List<TransferRecordInfo> mRecordInfoList = new ArrayList<>();



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
        mTransferAdapter = new TransferAdapter(mContext,mRecordInfoList);
        RclViewHelp.initRcLmVertical(this, mRecyclerView, mTransferAdapter);
        transferRecord();
    }




    /**
     * 转账记录
     */
    private void transferRecord() {
        Map<String, Object> map = new HashMap<>(1);
        ApiClient.requestNetHandle(this, AppConfig.TRANSFER_RECORD, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (FastJsonUtil.getList(json, TransferRecordInfo.class) != null && FastJsonUtil.getList(json, TransferRecordInfo.class).size() > 0) {
                    mRecordInfoList.addAll(FastJsonUtil.getList(json, TransferRecordInfo.class));
                    mTransferAdapter.notifyDataSetChanged();
                    mTvNoData.setVisibility(View.GONE);
                } else {
                    mTvNoData.setVisibility(View.VISIBLE);
                }
            }
            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }


}
