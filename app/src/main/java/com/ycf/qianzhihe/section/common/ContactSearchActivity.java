package com.ycf.qianzhihe.section.common;

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

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hyphenate.easeui.constants.EaseConstant;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.adapter.ContactAdapter;
import com.ycf.qianzhihe.app.adapter.ContactSearchAdapter;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.old_data.ContactListInfo;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_data.GroupDetailInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.domain.EaseUser;
import com.ycf.qianzhihe.app.help.RclViewHelp;
import com.ycf.qianzhihe.app.operate.UserOperateManager;
import com.ycf.qianzhihe.section.chat.activity.ChatActivity;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * @author lhb
 * 联系人
 */
public class ContactSearchActivity extends BaseInitActivity {
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.rv_group)
    RecyclerView mRecyclerView;
    @BindView(R.id.query)
    EditText mQuery;
    @BindView(R.id.search_clear)
    ImageButton mSearchClear;
    @BindView(R.id.img_left_back)
    ImageView mImgLeftBack;
    @BindView(R.id.tv_back)
    TextView mTvBack;
    private List<ContactListInfo.DataBean> mContactList;
    private ContactSearchAdapter mContactAdapter;


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, ContactSearchActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mToolbarTitle.setText("联系人");
        mImgLeftBack.setVisibility(View.VISIBLE);
        mImgLeftBack.setOnClickListener(v -> finish());
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
        mContactList = new ArrayList<>();
        mContactAdapter = new ContactSearchAdapter(mContactList);
        RclViewHelp.initRcLmVertical(this, mRecyclerView, mContactAdapter);
        checkSeviceContactData();
        mContactAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
               /* EaseUser user = (EaseUser) mContactList.get(position);
                if (user != null) {
                    ChatActivity.actionStart(mContext, user.getUsername(), EaseConstant.CHATTYPE_SINGLE);
                }*/
                String userId = mContactList.get(position).getFriendUserId();
                if (!userId.contains(Constant.ID_REDPROJECT)) {//friendUserId
                    userId += Constant.ID_REDPROJECT;
                }
                ChatActivity.actionStart(mContext, userId, EaseConstant.CHATTYPE_SINGLE);
            }
        });
    }

    @Override
    protected void onEventComing(EventCenter center) {

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);

    }

    public void checkSeviceContactData() {
        List localFriendList = UserOperateManager.getInstance().getContactList();
        if (localFriendList == null) {
            //本地不存在数据直接请求接口
            getContactList();
        } else {
            mContactList.addAll(localFriendList);
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
        ApiClient.requestNetHandle(this, url, "请稍后...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ContactListInfo info = FastJsonUtil.getObject(json, ContactListInfo.class);
                mContactList.clear();
                mContactList.addAll(info.getData());
                if (mContactList.size() > 0) {
                    UserOperateManager.getInstance().saveContactListToLocal(info, json);
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }
}
