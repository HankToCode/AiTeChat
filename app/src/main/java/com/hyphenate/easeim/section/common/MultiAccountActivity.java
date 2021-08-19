package com.hyphenate.easeim.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.chat.EMDeviceInfo;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.app.adapter.LoginAccountAdapter;
import com.hyphenate.easeim.app.adapter.TransferAdapter;
import com.hyphenate.easeim.app.api.global.UserComm;
import com.hyphenate.easeim.app.api.old_data.LoginInfo;
import com.hyphenate.easeim.app.api.old_http.AppConfig;
import com.hyphenate.easeim.app.base.ActivityStackManager;
import com.hyphenate.easeim.app.base.BaseInitActivity;
import com.hyphenate.easeim.app.base.WebViewActivity;
import com.hyphenate.easeim.app.domain.EaseUser;
import com.hyphenate.easeim.app.utils.my.MyHelper;
import com.hyphenate.easeim.app.utils.my.MyModel;
import com.hyphenate.easeim.section.account.activity.LoginActivity;
import com.hyphenate.easeim.section.me.activity.MultiDeviceActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.util.DataCleanManager;
import com.zds.base.util.StringUtil;
import com.zds.base.util.SystemUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.zds.base.Toast.ToastUtil.toast;

public class MultiAccountActivity extends BaseInitActivity {

    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;

    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.ll_add)
    LinearLayout ll_add;

    List<EaseUser> loginInfos;
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
        recyclerView.setAdapter(adapter);

        myModel = MyHelper.getInstance().getModel();
       /* LoginInfo loginInfo = UserComm.getUserInfo();
        EaseUser user = new EaseUser();
        user.setNickName(loginInfo.getNickName());
        user.setAvatar(loginInfo.getUserHead());
        user.setUserCode(loginInfo.getUserCode());
        user.setAccount(loginInfo.getPhone());
        user.setPassword(loginInfo.getPassword());
        myModel.saveLoginAccount(user);*/

        loginInfos= myModel.getLoginAccount();
        adapter.notifyDataSetChanged();


        ll_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                ActivityStackManager.getInstance().killAllActivity();
//                UserComm.clearUserInfo();
                LoginActivity.actionStart(mContext);
            }
        });

    }

}
