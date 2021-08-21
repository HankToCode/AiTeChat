package com.ycf.qianzhihe.section.contact.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.common.db.DemoDbHelper;
import com.ycf.qianzhihe.common.enums.SearchType;
import com.ycf.qianzhihe.common.interfaceOrImplement.OnResourceParseCallback;
import com.ycf.qianzhihe.section.contact.adapter.AddContactAdapter;
import com.ycf.qianzhihe.section.contact.viewmodels.AddContactViewModel;
import com.ycf.qianzhihe.section.search.SearchActivity;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.StringUtil;

import com.hyphenate.easeui.adapter.EaseBaseRecyclerViewAdapter;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AddContactActivity extends SearchActivity implements EaseTitleBar.OnBackPressListener, AddContactAdapter.OnItemAddClickListener {
    private AddContactViewModel mViewModel;
    private SearchType mType;

    private String username;
    private LoginInfo easeUserInfos;

    public static void actionStart(Context context, SearchType type) {
        Intent intent = new Intent(context, AddContactActivity.class);
        intent.putExtra("type", type);
        context.startActivity(intent);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        mType = (SearchType) getIntent().getSerializableExtra("type");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        titleBar.setTitle(getString(R.string.em_search_add_contact));
        query.setHint(getString(R.string.em_search_add_contact_hint));
    }

    @Override
    protected void initData() {
        super.initData();
        mViewModel = new ViewModelProvider(mContext).get(AddContactViewModel.class);
        mViewModel.getAddContact().observe(this, response -> {
            parseResource(response, new OnResourceParseCallback<Boolean>() {
                @Override
                public void onSuccess(Boolean data) {
                    // 添加好友
                    addUser();
                }
            });

        });
        //获取本地的好友列表
        List<String> localUsers = null;
        if (DemoDbHelper.getInstance(mContext).getUserDao() != null) {
            localUsers = DemoDbHelper.getInstance(mContext).getUserDao().loadAllUsers();
        }
        ((AddContactAdapter) adapter).addLocalContacts(localUsers);

        ((AddContactAdapter) adapter).setOnItemAddClickListener(this);
    }

    @Override
    protected EaseBaseRecyclerViewAdapter getAdapter() {
        return new AddContactAdapter();
    }

    @Override
    public void searchMessages(String query) {
        // you can search the user from your app server here.
        if (adapter.getData() != null && !adapter.getData().isEmpty()) {
            adapter.clearData();
        }
        getUserInfo();
    }

    @Override
    protected void onChildItemClick(View view, int position) {
        // 跳转到好友页面
        String item = (String) adapter.getItem(position);
        EaseUser user = new EaseUser(item);
        ContactDetailActivity.actionStart(mContext, user, false);
    }

    @Override
    public void onBackPress(View view) {
        onBackPressed();
    }

    @Override
    public void onItemAddClick(View view, int position) {

        mViewModel.addContact((String) adapter.getItem(position), getResources().getString(R.string.em_add_contact_add_a_friend));
    }


    /**
     * 查询好友
     */
    private void getUserInfo() {
        if (StringUtil.isEmpty(query.getText().toString())) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("phone", query.getText().toString());
        ApiClient.requestNetHandle(this, AppConfig.FIND_USER, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                // XLog.json(json);
                easeUserInfos = FastJsonUtil.getObject(json, LoginInfo.class);
                if (easeUserInfos != null) {
                    username = easeUserInfos.getNickName();
                    adapter.addData(username);
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }


    /**
     * 添加好友
     */
    private void addUser() {
        if (StringUtil.isEmpty(query.getText().toString()) || easeUserInfos == null) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("toUserId", easeUserInfos.getUserId());
        map.put("originType", Constant.ADD_USER_ORIGIN_TYPE_SEARCH);
        ApiClient.requestNetHandle(this, AppConfig.APPLY_ADD_USER, "正在添加", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);

//支持单聊和群聊，默认单聊，如果是群聊添加下面这行
//                cmdMsg.setChatType(ChatType.GroupChat)
                //action可以自定义
                String action = Constant.ACTION_APPLY_ADD_FRIEND;
                EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
                cmdMsg.addBody(cmdBody);
//                //发送给某个人
                String toUsername = easeUserInfos.getUserId() + Constant.ID_REDPROJECT;
                cmdMsg.setTo(toUsername);
                cmdMsg.setFrom(UserComm.getUserId());
                cmdMsg.setAttribute(Constant.APPLY_ADD_FRIEND_ID, UserComm.getUserInfo().getUserId());

                EMClient.getInstance().chatManager().sendMessage(cmdMsg);
                ToastUtil.toast(getResources().getString(R.string.em_add_contact_send_successful));
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });


    }
}
