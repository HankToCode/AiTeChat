package com.android.nanguo.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.hyphenate.easeui.widget.EaseTitleBar;
import com.android.nanguo.R;
import com.android.nanguo.app.adapter.UserCodeMoreAdapter;
import com.android.nanguo.app.api.old_data.UserCodeMallBean;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.section.account.activity.BeautifulMallDetailActivity;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;


/**
 * 靓号更多
 */
public class UserCodeMoreActivity extends BaseInitActivity {
    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.gv_gridview)
    GridView gv_gridview;
    @BindView(R.id.tv_no_data)
    TextView mTvNoData;
    private String category;
    private UserCodeMoreAdapter moreAdapter;
    private final List<UserCodeMallBean.DataBean> mDatas = new ArrayList<>();



    public static void actionStart(Context context) {
        Intent intent = new Intent(context, UserCodeMoreActivity.class);
        context.startActivity(intent);
    }
    public static void actionStart(Context context,String category) {
        Intent intent = new Intent(context, UserCodeMoreActivity.class);
        intent.putExtra("category", category);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_usercode_more;
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        category = intent.getStringExtra("category");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar.setTitle("更多靓号");
        mTitleBar.setOnBackPressListener(view -> finish());
        moreAdapter = new UserCodeMoreAdapter();
        gv_gridview.setAdapter(moreAdapter);

        transferRecord();
        gv_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BeautifulMallDetailActivity.actionStart(mContext, mDatas.get(i).getMoney(), mDatas.get(i).getUserCode(), mDatas.get(i).getCodeId());
            }
        });

    }

    private void transferRecord() {
        Map<String, Object> map = new HashMap<>(1);
        map.put("category", category);
        map.put("pageSize", "100");
        ApiClient.requestNetHandle(this, AppConfig.getUserCodeMallPage, "加载中...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                UserCodeMallBean data = FastJsonUtil.getObject(json, UserCodeMallBean.class);
                mDatas.addAll(data.getData());
                moreAdapter.setData(mDatas, mContext);
                moreAdapter.notifyDataSetChanged();
                if (mDatas.size() > 0) {
                    mTvNoData.setVisibility(View.GONE);
                    gv_gridview.setVisibility(View.VISIBLE);
                } else {
                    gv_gridview.setVisibility(View.GONE);
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
