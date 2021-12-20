package com.android.nanguo.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.nanguo.R;
import com.android.nanguo.app.adapter.ContactAdapter;
import com.android.nanguo.app.api.global.EventUtil;
import com.android.nanguo.app.api.old_data.ContactListInfo;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.app.help.RclViewHelp;
import com.android.nanguo.app.operate.UserOperateManager;
import com.zds.base.json.FastJsonUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

import com.zds.base.Toast.ToastUtil;

/**
 * @author lhb
 * 联系人
 */
public class ContactActivity extends BaseInitActivity {

    public static void actionStart(Context context, String from, String groupName, String groupId) {
        Intent intent = new Intent(context, ContactActivity.class);
        intent.putExtra("from", from);
        intent.putExtra("groupName", groupName);
        intent.putExtra("groupId", groupId);
        context.startActivity(intent);
    }

    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.toolbar_subtitle)
    TextView mToolSubTitle;
    @BindView(R.id.rv_group)
    RecyclerView mRvGroup;
    @BindView(R.id.query)
    EditText mQuery;
    @BindView(R.id.search_clear)
    ImageButton mSearchClear;
    @BindView(R.id.img_left_back)
    ImageView mImgLeftBack;
    @BindView(R.id.tv_back)
    TextView mTvBack;
    private String inGroupFriendUserId;
    private List<ContactListInfo.DataBean> mContactList;
    private ContactAdapter mContactAdapter;

    /**
     * 1: 新建群跳转
     * 2：已有群跳转过来
     * 3: 设置群管理员
     * 4: 发送专享红包
     */
    private String from = "2";
    private String groupId;
    private List<String> mIdList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        if (from.equals("3")) {
            mToolbarTitle.setText("添加群管理员");
            mToolSubTitle.setText("确定");
            mImgLeftBack.setVisibility(View.GONE);
            mTvBack.setText("取消");
            mTvBack.setOnClickListener(v -> finish());
        } else if (from.equals("4")) {
            mToolbarTitle.setText("群成员");
            mToolSubTitle.setText("确定");
            mToolSubTitle.setVisibility(View.VISIBLE);
            mImgLeftBack.setVisibility(View.VISIBLE);
            mImgLeftBack.setOnClickListener(v -> finish());
        } else {
            mToolbarTitle.setText("联系人");
            mToolSubTitle.setText("邀请");
            mToolSubTitle.setVisibility(View.VISIBLE);
            mImgLeftBack.setVisibility(View.VISIBLE);
            mImgLeftBack.setOnClickListener(v -> finish());
        }

        mQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mContactAdapter.getFilter().filter(s);

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() > 0) {
                    mSearchClear.setVisibility(View.VISIBLE);
                } else {
                    mSearchClear.setVisibility(View.GONE);
                }
            }
        });

        mSearchClear.setOnClickListener(v -> mQuery.getText().clear());

        mToolSubTitle.setOnClickListener(v -> {
            if (mContactAdapter.getIdList().size() <= 0) {
                ToastUtil.toast("请先选择群成员");
            } else {
                if (from.equals("1")) {
                    //新建群组邀请成员
                    startActivity(new Intent(ContactActivity.this, NewGroupActivity.class).putExtra("contact", (Serializable) mContactAdapter.getIdList()));
                    finish();
                } else if (from.equals("2")) {
                    //已有群组邀请成员 （从群聊过来）
                    inviteContact();
                } else if (from.equals("4")) {
                    EventBus.getDefault().post(new EventCenter<>(EventUtil.SEND_PERSON_RED_PKG_PRIVATE, mContactAdapter.getIdList().get(0)));
                    finish();
                }
            }
        });
        mContactList = new ArrayList<>();


        mContactAdapter = new ContactAdapter(mContactList);
        RclViewHelp.initRcLmVertical(this, mRvGroup, mContactAdapter);
        checkSeviceContactData();
        if (from.equals("2") || from.equals("4")) {
            queryGroupFriendUserList();
        }
    }

    @Override
    protected void onEventComing(EventCenter center) {

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        from = intent.getStringExtra("from");
        groupId = intent.getStringExtra("groupId");
    }


    public void checkSeviceContactData() {

        List localFriendList = UserOperateManager.getInstance().getContactList();

        if (localFriendList == null) {
            //本地不存在数据直接请求接口
            getContactList();
        } else {

            mContactList.addAll(localFriendList);

            setContactData();

            ApiClient.requestNetHandle(this, AppConfig.CHECK_FRIEND_DATA_VERSION, "",
                    null, new ResultListener() {
                        @Override
                        public void onSuccess(String json, String msg) {
                            int cacheVersion = FastJsonUtil.getInt(json, "cacheVersion");
                            //本地数据版本更服务器不一致 就需要更新数据接口
//                            if (cacheVersion != UserOperateManager.getInstance().getContactVersion()) {
//                                UserOperateManager.getInstance().setContactVersion(cacheVersion);
                            getContactList();
//                            }
                        }

                        @Override
                        public void onFailure(String msg) {
                            getContactList();
                        }
                    });
        }
    }

    /**
     * query contact
     *
     * @param
     */
    private void getContactList() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("pageNum", 1);
        map.put("pageSize", 10000);
        String url = AppConfig.USER_FRIEND_LIST;
        if (from.equals("2")) {
            map.put("groupId", groupId);
        }

        ApiClient.requestNetHandle(this, url, "请稍后...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ContactListInfo info = FastJsonUtil.getObject(json, ContactListInfo.class);
                mContactList.clear();
                mContactList.addAll(info.getData());

                if (mContactList.size() > 0) {
                    UserOperateManager.getInstance().saveContactListToLocal(info, json);
                    setContactData();
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    protected void setContactData() {
        if (from.equals("2") && !TextUtils.isEmpty(inGroupFriendUserId)) {
            for (int i = mContactList.size() - 1; i >= 0; i--) {
                ContactListInfo.DataBean dataBean = mContactList.get(i);
                if (inGroupFriendUserId.contains(dataBean.getFriendUserId())) {
                    mContactList.remove(dataBean);
                }
            }
        }

        mContactAdapter.notifyDataSetChanged();
    }

    public void queryGroupFriendUserList() {
        Map<String, Object> map = new HashMap<>(1);
        map.put("groupId", groupId);

        ApiClient.requestNetHandle(this, AppConfig.GET_USER_IN_GROUP, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                inGroupFriendUserId = json;
                setContactData();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });

    }

    /**
     * 邀请联系人建群
     *
     * @param
     */
    private void inviteContact() {
        if (mContactAdapter.getIdList().size() <= 0) {
            return;
        }

        mIdList = new ArrayList<>();
        for (ContactListInfo.DataBean bean : mContactAdapter.getIdList()) {
            mIdList.add(bean.getFriendUserId());
        }

        Map<String, Object> map = new HashMap<>(2);
        map.put("groupId", groupId);

        String userId = "";
        for (int i = 0; i < mIdList.size(); i++) {
            if (i != mIdList.size() - 1) {
                userId += mIdList.get(i) + ",";
            } else {
                userId += mIdList.get(i);
            }
        }

        map.put("userId", userId);


        ApiClient.requestNetHandle(this, AppConfig.SAVE_GROUP_USER, "正在邀请...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                EventBus.getDefault().post(new EventCenter<>(EventUtil.INVITE_USER_ADD_GROUP));
                ToastUtil.toast(msg);
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }


    /**
     * 设置群管理员
     *
     * @param
     */
    private void setGroupManage() {
        if (mContactAdapter.getIdList().size() <= 0) {
            return;
        }

        mIdList = new ArrayList<>();
        for (ContactListInfo.DataBean bean : mContactAdapter.getIdList()) {
            mIdList.add(bean.getFriendUserId());
        }

        Map<String, Object> map = new HashMap<>(2);
        map.put("groupId", groupId);
        map.put("userId", mIdList);


        ApiClient.requestNetHandle(this, AppConfig.SAVE_GROUP_USER, "正在邀请...", map, new ResultListener() {
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
