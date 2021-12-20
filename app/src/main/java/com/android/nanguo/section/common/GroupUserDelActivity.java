package com.android.nanguo.section.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.android.nanguo.R;
import com.android.nanguo.app.adapter.GroupUserMultiSelectListAdapter;
import com.android.nanguo.app.api.Constant;
import com.android.nanguo.app.api.global.EventUtil;
import com.android.nanguo.app.api.global.UserComm;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.api.old_data.GroupDetailInfo;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.app.help.RclViewHelp;
import com.android.nanguo.app.operate.GroupOperateManager;
import com.android.nanguo.app.utils.SortUtil;
import com.zds.base.json.FastJsonUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import com.zds.base.Toast.ToastUtil;

/**
 * @author lhb
 * 设置群管理员-群用户列表，选择管理员
 */
public class GroupUserDelActivity extends BaseInitActivity {
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
    private GroupDetailInfo groupDetailInfo;
    private List<GroupDetailInfo.GroupUserDetailVoListBean> mGroupUserList = new ArrayList<>();
    private GroupUserMultiSelectListAdapter mGroupUserListAdapter;

    private String groupId;
    private String emChatId;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mToolbarTitle.setText("删除群成员");
        mToolSubTitle.setText("删除");
        mToolSubTitle.setVisibility(View.VISIBLE);
        mQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mGroupUserListAdapter.getFilter().filter(s);
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

        mSearchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mQuery.setText("");
            }
        });

        mToolSubTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mGroupUserListAdapter.getIdList().size() <= 0){
                    return;
                }
                for (GroupDetailInfo.GroupUserDetailVoListBean groupUserDetailVoListBean : mGroupUserListAdapter.getIdList()) {
                    delGroupUser(groupUserDetailVoListBean);
                }
                EventBus.getDefault().post(new EventCenter<>(EventUtil.DEL_GROUP_MEMBER, mGroupUserListAdapter.getIdList()));
                ToastUtil.toast("踢出成功");
                finish();
            }
        });

        mRvGroup.setHasFixedSize(true);

        mGroupUserListAdapter = new GroupUserMultiSelectListAdapter(mGroupUserList);

        mGroupUserListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mGroupUserListAdapter.notifyDataSetChanged();
            }
        });

        RclViewHelp.initRcLmVertical(this, mRvGroup, mGroupUserListAdapter);

        loadGroupDataFromLocal();
    }


    public void loadGroupDataFromLocal() {
        groupDetailInfo = GroupOperateManager.getInstance().getGroupMemberList(emChatId);
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        if (groupDetailInfo != null)
            setGroupMemberData();

        ApiClient.requestNetHandle(this, AppConfig.CHECK_GROUP_DATA_VERSION, "",
                map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
//                        int groupVersion = FastJsonUtil.getInt(json, "groupVersion");
                        //本地数据版本更服务器不一致 就需要更新数据接口
//                        if (groupDetailInfo.getGroupVersion() != groupVersion) {
                            queryGroupDetail();
//                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        queryGroupDetail();
                    }
                });

    }

    public void queryGroupDetail() {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        ApiClient.requestNetHandle(this, AppConfig.GET_GROUP_DETAIL, "请稍等...",
                map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        if (json != null && json.length() > 0) {
                            groupDetailInfo = FastJsonUtil.getObject(json,
                                    GroupDetailInfo.class);
                            setGroupMemberData();
                            GroupOperateManager.getInstance().saveGroupMemberList(emChatId, groupDetailInfo, json);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                    }
                });
    }

    public void setGroupMemberData() {

        mGroupUserList.clear();
        //用户的群等级 0-普通用户 1-管理员 2-群主
        mGroupUserList.addAll(groupDetailInfo.getGroupUserDetailVoList());


        for (int i = mGroupUserList.size() - 1; i >= 0; i--) {
            GroupDetailInfo.GroupUserDetailVoListBean bean = mGroupUserList.get(i);
            if (bean.getUserRank().equals("2")) {
                mGroupUserList.remove(bean);
                continue;
            }
            if (groupDetailInfo.getGroupUserRank() == 1 && bean.getUserRank().equals("1")) {
                mGroupUserList.remove(bean);
                continue;
            }
            if (bean.getUserId().equals(UserComm.getUserId())) {
                mGroupUserList.remove(bean);
                continue;
            }
        }
        mGroupUserList = SortUtil.getInstance().groupUserAlphabetical(mGroupUserList);
        mGroupUserListAdapter.setNewData(mGroupUserList);
        mGroupUserListAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onEventComing(EventCenter center) {

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupId = intent.getStringExtra("groupId");
        emChatId = intent.getStringExtra(Constant.PARAM_EM_CHAT_ID);
    }

    private void delGroupUser(GroupDetailInfo.GroupUserDetailVoListBean groupUserDetailVoListBean) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("groupId", groupId);
        map.put("userId", groupUserDetailVoListBean.getUserId());

        ApiClient.requestNetHandle(this, AppConfig.DEL_GROUP_USER, "正在踢出成员...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {

                mGroupUserListAdapter.getData().remove(groupUserDetailVoListBean);
                mGroupUserListAdapter.notifyDataSetChanged();
//
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }
}
