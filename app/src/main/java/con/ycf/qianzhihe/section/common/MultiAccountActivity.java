package con.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import androidx.recyclerview.widget.RecyclerView;

import con.ycf.qianzhihe.R;
import con.ycf.qianzhihe.app.adapter.LoginAccountAdapter;
import con.ycf.qianzhihe.app.base.BaseInitActivity;
import con.ycf.qianzhihe.app.domain.EaseUser;
import con.ycf.qianzhihe.app.utils.my.MyHelper;
import con.ycf.qianzhihe.app.utils.my.MyModel;
import con.ycf.qianzhihe.section.account.activity.LoginActivity;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.List;

import butterknife.BindView;

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
