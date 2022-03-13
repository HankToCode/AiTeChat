package com.android.nanguo.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.android.nanguo.R;
import com.android.nanguo.app.adapter.ChatRedRecordAdapter;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.api.old_data.MyRedInfo;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.app.help.RclViewHelp;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * @author lhb
 * 聊天红包记录
 */
public class ChatRedRecordActivity extends BaseInitActivity {
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.rv_red_record)
    RecyclerView mRvRedRecord;
    @BindView(R.id.ll_back)
    LinearLayout ll_back;
    @BindView(R.id.tv_no_data)
    TextView tv_no_data;
    @BindView(R.id.swipeRefreshLayout)
    SwipeRefreshLayout swipeRefreshLayout;
    private ChatRedRecordAdapter mRedRecordAdapter;
    private final List<MyRedInfo.DataBean> mPacketInfoList = new ArrayList<>();
    private int page = 1;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, ChatRedRecordActivity.class);
        context.startActivity(starter);
    }
    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat_red_record;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        mToolbarTitle.setText("红包记录");
        mRedRecordAdapter = new ChatRedRecordAdapter(mPacketInfoList);
        RclViewHelp.initRcLmVertical(this, mRvRedRecord, mRedRecordAdapter);
        ll_back.setOnClickListener(view -> finish());
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
    protected void onEventComing(EventCenter center) {

    }


    /**
     * 查询红包
     */
    private void queryRed() {
        Map<String, Object> map = new HashMap<>(3);
        map.put("type", "");
        map.put("pageNum", page);
        map.put("pageSize", 10);

        ApiClient.requestNetHandle(this, AppConfig.RED_PACK_RECORD, "", map, new ResultListener() {
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
