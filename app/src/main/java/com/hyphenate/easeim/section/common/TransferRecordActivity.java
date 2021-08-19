package com.hyphenate.easeim.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easeim.R;
import com.hyphenate.easeim.app.adapter.TransferAdapter;
import com.hyphenate.easeim.app.api.old_data.TransferRecordInfo;
import com.hyphenate.easeim.app.api.old_http.ApiClient;
import com.hyphenate.easeim.app.api.old_http.AppConfig;
import com.hyphenate.easeim.app.api.old_http.ResultListener;
import com.hyphenate.easeim.app.base.BaseActivity;
import com.hyphenate.easeim.app.base.BaseInitActivity;
import com.hyphenate.easeim.app.help.RclViewHelp;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.zds.base.Toast.ToastUtil.toast;


/**
 * 转账记录
 */
public class TransferRecordActivity extends BaseInitActivity {

    @BindView(R.id.recyclerView)
    RecyclerView mRecyclerView;
    @BindView(R.id.tv_no_data)
    TextView mTvNoData;

    private TransferAdapter mTransferAdapter;
    private List<TransferRecordInfo> mRecordInfoList = new ArrayList<>();



    public static void actionStart(Context context) {
        Intent intent = new Intent(context, BankActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_transfer_record;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setTitle("转账记录");
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
                toast(msg);
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }
}
