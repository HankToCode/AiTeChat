package com.ycf.qianzhihe.section.chat.activity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.R2;
import com.ycf.qianzhihe.app.adapter.Custom1Adapter;
import com.ycf.qianzhihe.app.api.old_data.Custom1BaseInfo;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseActivity;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ycf.qianzhihe.app.api.old_http.AppConfig.CUSTOM_QUESTIONS;


public class Custom1Activity extends BaseInitActivity {

    private List<Custom1BaseInfo> mNewInfoList = new ArrayList<>();
    private Custom1Adapter mAdapter;
    private RecyclerView mCustoms;

    @BindView(R2.id.title_bar)
    EaseTitleBar title_bar;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_custom1;

    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        title_bar.setTitle("帮助中心");
        title_bar.setOnBackPressListener(view -> finish());
        mCustoms = findViewById(R.id.recycle_view);
        mCustoms.setLayoutManager(new LinearLayoutManager(this));
        mAdapter = new Custom1Adapter(mNewInfoList);
        mAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter baseQuickAdapter, View view, int i) {
                Custom1BaseInfo info = mAdapter.getItem(i);
                if (info == null) return;
                boolean isOpen = info.isOpen();
                info.setOpen(!isOpen);
                mAdapter.notifyDataSetChanged();
            }
        });
        mCustoms.setAdapter(mAdapter);

        ApiClient.requestNetHandle(this,CUSTOM_QUESTIONS,"",new HashMap<>(),new ResultListener() {

            @Override
            public void onSuccess(String json, String msg) {
                Log.e("Custom1Activity","json="+json);
                Log.e("Custom1Activity","msg="+msg);
                if (json != null && json.length() > 0) {
                    List<Custom1BaseInfo> findInfos = FastJsonUtil.getList(json,Custom1BaseInfo.class);
                    if (null != findInfos && findInfos.size() > 0) {
                        if (mNewInfoList.size() >0) {
                            mNewInfoList.clear();
                        }
                        mNewInfoList.addAll(findInfos);
                    }
                    mAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(String msg) {
                Log.e("Custom1Activity",msg);
                ToastUtil.toast(msg);
            }
        });

    }



    @Override
    protected void onEventComing(EventCenter center) {

    }


}
