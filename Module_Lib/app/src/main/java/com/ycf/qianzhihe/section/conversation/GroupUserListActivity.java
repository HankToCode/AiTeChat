package com.ycf.qianzhihe.section.conversation;

import android.content.Context;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.R2;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_data.GroupDetailInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.help.RclViewHelp;
import com.ycf.qianzhihe.app.operate.GroupOperateManager;
import com.ycf.qianzhihe.section.conversation.adapter.GroupUserListAdapter;
import com.hyphenate.easeui.widget.EaseTitleBar;
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
public class GroupUserListActivity extends BaseInitActivity {


    public static void actionStart(Context context, String groupId, String emChatId) {
        Intent starter = new Intent(context, GroupUserListActivity.class);
        starter.putExtra("groupId", groupId);
        starter.putExtra(Constant.PARAM_EM_CHAT_ID, emChatId);
        context.startActivity(starter);
    }

    @BindView(R2.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R2.id.rv_group)
    RecyclerView mRvGroup;
    @BindView(R2.id.query)
    EditText mQuery;
    @BindView(R2.id.search_clear)
    ImageButton mSearchClear;
    private List<GroupDetailInfo.GroupUserDetailVoListBean> mGroupUserList = new ArrayList<>();
    private GroupUserListAdapter mGroupUserListAdapter;
    private GroupDetailInfo groupDetailInfo;

    private String groupId;
    private String emChatId;


    @Override
    protected void initData() {
        super.initData();
        initLogic();
    }

    protected void initLogic() {
        mTitleBar.setTitle("添加群管理员");
        mTitleBar.setOnBackPressListener(view -> finish());

        mTitleBar.setRightTitle("确定");
        mTitleBar.setOnRightClickListener(view -> {
            setGroupManage();
        });

        mQuery.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

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

        mRvGroup.setHasFixedSize(true);


        mGroupUserListAdapter = new GroupUserListAdapter(mGroupUserList);
        mGroupUserListAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                mGroupUserListAdapter.setSelected(position);
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
        //用户的群等级 0-普通用户 1-管理员 2-群主
        mGroupUserList.clear();
        mGroupUserList.addAll(groupDetailInfo.getGroupUserDetailVoList());

        for (int i = mGroupUserList.size() - 1; i >= 0; i--) {
            GroupDetailInfo.GroupUserDetailVoListBean bean = mGroupUserList.get(i);
            if (bean.getUserRank().equals("2") || bean.getUserRank().equals("1"))
                mGroupUserList.remove(i);
        }
        mGroupUserListAdapter.notifyDataSetChanged();
//        if (null != groupDetailInfo && groupDetailInfo.getGroupUserDetailVoList().size() > 0) {
//            mStringList.clear();
//            mGroupMemberAdapter.setGroupUserRank(groupDetailInfo.getGroupUserRank());
//            mGroupMemberAdapter.setUserReadDetail(groupDetailInfo.getGroupUserRank() != 0 || groupDetailInfo.getSeeFriendFlag() == 1);
//            mStringList.addAll(SortUtil.getInstance().groupUserAlphabetical(groupDetailInfo.getGroupUserDetailVoList()));
//        }
//
//
//        for (int i = 0; i < mStringList.size(); i++) {
//            if (!lettes.containsKey(mStringList.get(i).getTop())) {
//                lettes.put(mStringList.get(i).getTop(), i);
//            }
//        }
//
//        mGroupMemberAdapter.notifyDataSetChanged();
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

    /**
     * 设置群管理员
     *
     * @param
     */
    private void setGroupManage() {
        if (mGroupUserListAdapter.getSelected() < 0) {
            ToastUtil.toast("请选择群成员");
            return;
        }

        Map<String, Object> map = new HashMap<>(2);
        map.put("groupId", groupId);
        //0-普通用户 1-管理员
        map.put("userRank", 1);
        if (mGroupUserList.get(mGroupUserListAdapter.getSelected()).getUserId().equals(UserComm.getUserId())) {
            ToastUtil.toast("你已是群主");
            return;
        }
        map.put("userId", mGroupUserList.get(mGroupUserListAdapter.getSelected()).getUserId());


        ApiClient.requestNetHandle(this, AppConfig.MODIFY_GROUP_MANEGER, "设置群管理员...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast(msg);
                EventBus.getDefault().post(new EventCenter<>(EventUtil.SET_GROUP_MANAGE));
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_group_user_list;
    }
}
