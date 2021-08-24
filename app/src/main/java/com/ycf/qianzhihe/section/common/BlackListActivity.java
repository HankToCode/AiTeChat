package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easeui.widget.EaseTitleBar;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.adapter.BlackAdapter;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.old_data.BlackListInfo;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
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
 * 黑名单
 */
public class BlackListActivity extends BaseInitActivity {

    @BindView(R.id.rv_black)
    RecyclerView mRvBlack;

    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    private BlackAdapter mBlackAdapter;
    private List<BlackListInfo.ContactInfo> mBeanList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_black_list;
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, BlackListActivity.class);
        context.startActivity(intent);
    }


    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar.setOnBackPressListener(view -> finish());
        mBeanList = new ArrayList<>();
        mBlackAdapter = new BlackAdapter(mBeanList);
        RclViewHelp.initRcLmVertical(this, mRvBlack, mBlackAdapter);

    }

    @Override
    protected void onResume() {
        super.onResume();
        queryBlack();
    }

    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.REFRESH_BLACK) {
            queryBlack();
        }
    }



    /**
     * 黑名单
     */
    private void queryBlack() {
        Map<String, Object> map = new HashMap<>(1);
        ApiClient.requestNetHandle(this, AppConfig.BLACK_USER_LIST, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                BlackListInfo contactListInfo = FastJsonUtil.getObject(json, BlackListInfo.class);
                mBeanList.clear();
                if (contactListInfo.getData().size() > 0) {
                    mBeanList.addAll(contactListInfo.getData());
                }

                mBlackAdapter.notifyDataSetChanged();

            }

            @Override
            public void onFailure(String msg) {

            }
        });
    }
}
