package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.adapter.ChatRedRecordAdapter;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_data.MyRedInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.help.RclViewHelp;
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

    private ChatRedRecordAdapter mRedRecordAdapter;
    private List<MyRedInfo.DataBean> mPacketInfoList = new ArrayList<>();
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

        mRedRecordAdapter.setOnLoadMoreListener(new BaseQuickAdapter.RequestLoadMoreListener() {
            @Override
            public void onLoadMoreRequested() {
                page++;
                queryRed();
            }
        });


        queryRed();
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
        map.put("pageSize", 15);

        ApiClient.requestNetHandle(this, AppConfig.RED_PACK_RECORD, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                MyRedInfo info = FastJsonUtil.getObject(json, MyRedInfo.class);
                if (info.getData() != null && info.getData().size() > 0) {
                    mPacketInfoList.addAll(info.getData());
                    mRedRecordAdapter.notifyDataSetChanged();
                    mRedRecordAdapter.loadMoreComplete();
                } else {
                    mRedRecordAdapter.loadMoreEnd(true);
                }
            }

            @Override
            public void onFailure(String msg) {
                mRedRecordAdapter.loadMoreFail();
            }
        });


    }


}
