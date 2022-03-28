package com.android.nanguo.section.conversation;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.nanguo.R;
import com.android.nanguo.app.api.Constant;
import com.android.nanguo.app.api.global.EventUtil;
import com.android.nanguo.app.api.global.UserComm;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.api.old_data.GroupManageListInfo;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.app.help.RclViewHelp;
import com.android.nanguo.section.conversation.adapter.SetGroupManageAdapter;
import com.blankj.utilcode.util.StringUtils;
import com.blankj.utilcode.util.ToastUtils;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * @author lhb
 * 群发好友消息
 */
public class GroupSendMessageActivity extends BaseInitActivity {


    public static void actionStart(Context context) {
        Intent starter = new Intent(context, GroupSendMessageActivity.class);
        context.startActivity(starter);
    }


    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.message)
    EditText message;
    @BindView(R.id.submit)
    TextView submit;


    @Override
    protected void initData() {
        super.initData();
        initLogic();
    }

    protected void initLogic() {

        mTitleBar.setTitle("一键转发");
        mTitleBar.setOnBackPressListener(view -> finish());


    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_send_message;
    }

    @OnClick({R.id.submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.submit:

                String text = message.getText().toString().trim();
                if (StringUtils.isEmpty(text)) {
                    ToastUtils.showLong("请输入群发好友消息");
                    return;
                }

                groupSendMessage(text);

                break;
            default:
                break;
        }
    }

    /**
     * 查询群管理员
     *
     * @param
     */
    private void groupSendMessage(String msg) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("userId", UserComm.getUserId());
        map.put("msg", msg);
        ApiClient.requestNetHandle(this, AppConfig.SEND_USER_TEXT_MESSAGE, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtils.showLong("发送成功");
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }
}
