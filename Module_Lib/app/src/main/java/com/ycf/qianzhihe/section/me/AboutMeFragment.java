package com.ycf.qianzhihe.section.me;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.hyphenate.EMCallBack;
import com.ycf.qianzhihe.DemoHelper;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.base.BaseInitFragment;
import com.ycf.qianzhihe.common.widget.ArrowItemView;

import com.hyphenate.easeui.manager.EaseThreadManager;

import com.ycf.qianzhihe.section.dialog.DemoDialogFragment;
import com.ycf.qianzhihe.section.dialog.SimpleDialogFragment;
import com.ycf.qianzhihe.section.account.activity.LoginActivity;
import com.ycf.qianzhihe.section.me.activity.AboutHxActivity;
import com.ycf.qianzhihe.section.me.activity.DeveloperSetActivity;
import com.ycf.qianzhihe.section.me.activity.FeedbackActivity;
import com.ycf.qianzhihe.section.me.activity.SetIndexActivity;
import com.ycf.qianzhihe.section.me.activity.UserDetailActivity;

import androidx.constraintlayout.widget.ConstraintLayout;

public class AboutMeFragment extends BaseInitFragment implements View.OnClickListener {
    private ConstraintLayout clUser;
    private TextView name;
    private ArrowItemView itemCommonSet;
    private ArrowItemView itemFeedback;
    private ArrowItemView itemAboutHx;
    private ArrowItemView itemDeveloperSet;
    private Button mBtnLogout;
    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_about_me;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        clUser = findViewById(R.id.cl_user);
        name = findViewById(R.id.name);
        itemCommonSet = findViewById(R.id.item_common_set);
        itemFeedback = findViewById(R.id.item_feedback);
        itemAboutHx = findViewById(R.id.item_about_hx);
        itemDeveloperSet = findViewById(R.id.item_developer_set);
        mBtnLogout = findViewById(R.id.btn_logout);

        name.setText(DemoHelper.getInstance().getCurrentUser());
    }

    @Override
    protected void initListener() {
        super.initListener();
        mBtnLogout.setOnClickListener(this);
        clUser.setOnClickListener(this);
        itemCommonSet.setOnClickListener(this);
        itemFeedback.setOnClickListener(this);
        itemAboutHx.setOnClickListener(this);
        itemDeveloperSet.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btn_logout) {
            logout();
        } else if (id == R.id.cl_user) {
            UserDetailActivity.actionStart(mContext);
        } else if (id == R.id.item_common_set) {
            SetIndexActivity.actionStart(mContext);
        } else if (id == R.id.item_feedback) {
            FeedbackActivity.actionStart(mContext);
        } else if (id == R.id.item_about_hx) {
            AboutHxActivity.actionStart(mContext);
        } else if (id == R.id.item_developer_set) {
            DeveloperSetActivity.actionStart(mContext);
        }
    }

    private void logout() {
        new SimpleDialogFragment.Builder(mContext)
                .setTitle(R.string.em_login_out_hint)
                .showCancelButton(true)
                .setOnConfirmClickListener(R.string.em_dialog_btn_confirm, new DemoDialogFragment.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(View view) {
                        DemoHelper.getInstance().logout(true, new EMCallBack() {
                            @Override
                            public void onSuccess() {
                                LoginActivity.actionStart(mContext,"");
                                mContext.finish();
                            }

                            @Override
                            public void onError(int code, String error) {
                                EaseThreadManager.getInstance().runOnMainThread(()-> showToast(error));
                            }

                            @Override
                            public void onProgress(int progress, String status) {

                            }
                        });
                    }
                })
                .show();
    }
}
