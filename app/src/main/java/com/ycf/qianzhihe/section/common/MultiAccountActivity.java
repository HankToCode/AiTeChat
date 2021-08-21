package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import com.ycf.qianzhihe.DemoHelper;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.adapter.LoginAccountAdapter;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.domain.EaseUser;
import com.ycf.qianzhihe.app.help.RclViewHelp;
import com.ycf.qianzhihe.app.utils.my.MyHelper;
import com.ycf.qianzhihe.app.utils.my.MyModel;
import com.ycf.qianzhihe.common.utils.log.LogUtils;
import com.ycf.qianzhihe.section.account.activity.LoginActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class MultiAccountActivity extends BaseInitActivity {

    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ll_add)
    LinearLayout ll_add;

    List<EaseUser> loginInfos = new ArrayList<>();
    private LoginAccountAdapter adapter;
    private MyModel myModel;
    public static void actionStart(Context context) {
        Intent intent = new Intent(context, MultiAccountActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_multi_device;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar.setTitle("切换账号");
        mTitleBar.setOnBackPressListener(view -> finish());
        adapter = new LoginAccountAdapter(mContext,loginInfos);
        RclViewHelp.initRcLmVertical(this, recyclerView, adapter);

        myModel = MyHelper.getInstance().getModel();
       /* LoginInfo loginInfo = UserComm.getUserInfo();
        EaseUser user = new EaseUser();
        user.setNickName(loginInfo.getNickName());
        user.setAvatar(loginInfo.getUserHead());
        user.setUserCode(loginInfo.getUserCode());
        user.setAccount(loginInfo.getPhone());
        user.setPassword(loginInfo.getPassword());
        myModel.saveLoginAccount(user);*/
        LoginInfo currentUser = UserComm.getUserInfo();//当前登录用户
        Log.d("###", "当前登录用户"+currentUser.getNickName());

        loginInfos.addAll(myModel.getLoginAccount());
        adapter.notifyDataSetChanged();
        ll_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ActivityStackManager.getInstance().killAllActivity();
//                UserComm.clearUserInfo();
                LoginActivity.actionStart(mContext);
                finish();
            }
        });

    }

}
