package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.hyphenate.easeui.widget.EaseTitleBar;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.adapter.BankCardAdapter;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_data.JsonBankCardList;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.weight.ease.EaseAlertDialog;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * 银行卡详情
 */
public class BankDetailActivity extends BaseInitActivity {



    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.tv_bank_name)
    TextView tv_bank_name;
    @BindView(R.id.tv_bank_card)
    TextView tv_bank_card;
    @BindView(R.id.ll_del)
    TextView ll_del;
    private JsonBankCardList.DataBean dataBean;

    public static void actionStart(Context context, JsonBankCardList.DataBean dataBean) {
        Intent intent = new Intent(context, BankDetailActivity.class);
        intent.putExtra("dataBean", dataBean);
        context.startActivity(intent);
    }
    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        dataBean = (JsonBankCardList.DataBean) intent.getSerializableExtra("dataBean");

    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bank_detail;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar.setTitle("");
        mTitleBar.setOnBackPressListener(view -> finish());

    }

    @Override
    protected void initData() {
        super.initData();
        tv_bank_name.setText(dataBean.getBankName());
        tv_bank_card.setText(dataBean.getBankCard());
    }

    @OnClick({R.id.ll_del})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.ll_del:
                new EaseAlertDialog(mContext, "确认删除该银行卡？", null, "删除", new EaseAlertDialog.AlertDialogUser() {
                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if (confirmed) {

                            Map<String, Object> map = new HashMap<>();
                            map.put("cardId", dataBean.getCardId());
                            ApiClient.requestNetHandle(mContext, AppConfig.removeBankCardList, "", map, new ResultListener() {
                                @Override
                                public void onSuccess(String json, String msg) {
                                    ToastUtil.toast(msg);
                                    finish();
                                }

                                @Override
                                public void onFailure(String msg) {
                                    ToastUtil.toast(msg);
                                }
                            });
                        }
                    }
                }).show();
                break;
            default:
        }
    }
}
