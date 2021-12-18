package com.ycf.qianzhihe.section.common;

import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_data.InviteInfo;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.util.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

//提现结果
public class InviteActivity extends BaseInitActivity {

    @BindView(R.id.iv_head)
    ImageView iv_head;
    @BindView(R.id.tv_name)
    TextView tv_name;
    @BindView(R.id.tv_user_code)
    TextView tv_user_code;
    @BindView(R.id.tv_number)
    TextView tv_number;
    @BindView(R.id.tv_code)
    TextView tv_code;
    @BindView(R.id.iv_btn)
    ImageView iv_btn;

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, InviteActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_invite;
    }

    private String inviteCode = "";
    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initImmersionBar(true);
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandle(mContext, AppConfig.getInviteInfo, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    InviteInfo inviteInfo = JSON.parseObject(json, InviteInfo.class);
                    if (inviteInfo != null) {
                        GlideUtils.loadImageViewLoding(AppConfig.checkimg(inviteInfo.getUserHead()), iv_head, R.mipmap.ic_ng_avatar);
                        tv_name.setText(inviteInfo.getUserName());
                        tv_user_code.setText("ID："+inviteInfo.getUserCode());
                        tv_number.setText(inviteInfo.getInviteCount());
                        tv_code.setText(inviteInfo.getInviteCode());//邀请码
                        inviteCode = inviteInfo.getInviteCode();
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                tv_name.setText("");
                tv_user_code.setText("ID：");
                tv_code.setText("");//邀请码
            }
        });
    }

    @OnClick({R.id.tv_code, R.id.iv_btn})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.tv_code:
            case R.id.iv_btn:
                if (TextUtils.isEmpty(inviteCode)) {
                    showToast("未获取邀请码，请检查后重试");
                    return;
                }
                ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                // 将文本内容放到系统剪贴板里。
                cm.setText(inviteCode);
//                cm.setPrimaryClip(cm);
                showToast("复制成功");
                break;
        }

    }

}
