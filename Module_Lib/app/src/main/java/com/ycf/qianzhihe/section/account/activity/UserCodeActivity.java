package com.ycf.qianzhihe.section.account.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.TextView;

import com.hyphenate.easeui.widget.EaseTitleBar;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.R2;
import com.ycf.qianzhihe.app.adapter.SpecialOfferAdapter;
import com.ycf.qianzhihe.app.api.new_data.UserCodeMallListBean;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.common.utils.ToastUtils;
import com.ycf.qianzhihe.section.common.UserCodeMoreActivity;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

public class UserCodeActivity extends BaseInitActivity {

    @BindView(R2.id.title_bar)
    EaseTitleBar title_bar;
    @BindView(R2.id.gv_gridview)
    GridView gv_gridview;
    @BindView(R2.id.gv_gridview2)
    GridView gv_gridview2;
    @BindView(R2.id.gv_gridview3)
    GridView gv_gridview3;
    @BindView(R2.id.tv_more)
    TextView tv_more;
    @BindView(R2.id.tv_more3)
    TextView tv_more3;
    @BindView(R2.id.tv_more2)
    TextView tv_more2;
    private List<UserCodeMallListBean.SpecialOffer> tjDatas = new ArrayList<>();
    private List<UserCodeMallListBean.SpecialOffer> jxDatas = new ArrayList<>();
    private List<UserCodeMallListBean.SpecialOffer> dhDatas = new ArrayList<>();
    private SpecialOfferAdapter tjAdapter;
    private SpecialOfferAdapter jxAdapter;
    private SpecialOfferAdapter dhAdapter;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, UserCodeActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_beautiful_mall;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        title_bar.setOnBackPressListener(view -> finish());
        tjAdapter = new SpecialOfferAdapter();
        gv_gridview.setAdapter(tjAdapter);
        jxAdapter = new SpecialOfferAdapter();
        gv_gridview2.setAdapter(jxAdapter);

        dhAdapter = new SpecialOfferAdapter();
        gv_gridview3.setAdapter(dhAdapter);

        gv_gridview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BeautifulMallDetailActivity.actionStart(mContext, tjDatas.get(i).getMoney(), tjDatas.get(i).getUserCode(), tjDatas.get(i).getCodeId());
            }
        });
        gv_gridview2.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BeautifulMallDetailActivity.actionStart(mContext, jxDatas.get(i).getMoney(), jxDatas.get(i).getUserCode(), jxDatas.get(i).getCodeId());
            }
        });
        gv_gridview3.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                BeautifulMallDetailActivity.actionStart(mContext, dhDatas.get(i).getMoney(), dhDatas.get(i).getUserCode(), dhDatas.get(i).getCodeId());
            }
        });
    }

    @OnClick({R2.id.tv_more, R2.id.tv_more2, R2.id.tv_more3})
    public void click(View v) {
        int id = v.getId();
        if (id == R.id.tv_more) {//category":0//0：特价靓号，1：精选靓号，2：精选叠号 (必传）
            UserCodeMoreActivity.actionStart(mContext, "0");
        } else if (id == R.id.tv_more2) {
            UserCodeMoreActivity.actionStart(mContext, "1");
        } else if (id == R.id.tv_more3) {
            UserCodeMoreActivity.actionStart(mContext, "2");
        }

    }

    @Override
    protected void initData() {
        super.initData();
        getUserCodeMallList();
    }

    private void getUserCodeMallList() {
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandle(mContext, AppConfig.getUserCodeMallList, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    UserCodeMallListBean info = FastJsonUtil.getObject(json, UserCodeMallListBean.class);
                    if (info.getSpecialOffer() != null && info.getSpecialOffer().size() > 0) {
                        tjDatas.addAll(info.getSpecialOffer());
                        tjAdapter.setData(tjDatas, mContext);
                        tjAdapter.notifyDataSetChanged();
                    }
                    if (info.getChoiceness() != null && info.getChoiceness().size() > 0) {
                        jxDatas.addAll(info.getChoiceness());
                        jxAdapter.setData(jxDatas, mContext);
                        jxAdapter.notifyDataSetChanged();
                    }
                    if (info.getDoublingNo() != null && info.getDoublingNo().size() > 0) {
                        dhDatas.addAll(info.getDoublingNo());
                        dhAdapter.setData(dhDatas, mContext);
                        dhAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtils.showToast(msg);
            }
        });
    }

}
