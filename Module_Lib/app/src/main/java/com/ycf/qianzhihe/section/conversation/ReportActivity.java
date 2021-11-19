package com.ycf.qianzhihe.section.conversation;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.R2;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import com.zds.base.Toast.ToastUtil;

/**
 * @author lhb
 * 举报
 */
public class ReportActivity extends BaseInitActivity {
    @BindView(R2.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R2.id.et_report)
    EditText mEtReport;
    @BindView(R2.id.tv_report)
    TextView mTvReport;

    /**
     * 1 单聊 ， 2：群聊
     */
    private String from;
    private String userGroupId;

    @Override
    protected void initData() {
        super.initData();
        initLogic();
    }

    protected void initLogic() {
        mTitleBar.setTitle("举报");
        mTitleBar.setOnBackPressListener(view -> finish());
        mEtReport.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mTvReport.setBackgroundResource(R.drawable.wallet_tx_bg_sel);
                } else {
                    mTvReport.setBackgroundResource(R.drawable.report_bg_nor_sel);
                }

            }
        });
    }

    @Override
    protected void onEventComing(EventCenter center) {

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        from = intent.getStringExtra("from");
        userGroupId = intent.getStringExtra("userGroupId");
    }

    /**
     * 举报
     */
    private void report() {
        if (mEtReport.getText().toString().trim().length() <= 0) {
            ToastUtil.toast("请输入举报理由");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("userGroupId", userGroupId);
        //举报类型（1.个人 ， 2.群组）
        map.put("reportType", from);
        //举报详情
        map.put("reportDetails", mEtReport.getText().toString().trim());

        ApiClient.requestNetHandle(this, AppConfig.SAVE_REPORT, "正在举报...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast("举报成功");
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });


    }


    @OnClick({R2.id.et_report, R2.id.tv_report})
    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.et_report) {
        } else if (id == R.id.tv_report) {
            report();
        }
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_report;
    }
}
