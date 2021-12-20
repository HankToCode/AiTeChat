package com.android.nanguo.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.android.nanguo.R;
import com.android.nanguo.app.adapter.ContactAdapter;
import com.android.nanguo.app.api.global.EventUtil;
import com.android.nanguo.app.api.new_data.FriendGroupingBean;
import com.android.nanguo.app.api.old_data.ContactListInfo;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.app.help.RclViewHelp;
import com.android.nanguo.app.operate.UserOperateManager;
import com.android.nanguo.common.utils.ToastUtils;
import com.android.nanguo.section.contact.adapter.ContactGroupingAdapter;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * @author lhb
 * 联系人
 */
public class ContactGroupingActivity extends BaseInitActivity {

    public static void actionStart(Context context, String from, String groupName, String groupId) {
        Intent intent = new Intent(context, ContactGroupingActivity.class);
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
    private List<ContactListInfo.DataBean> selectItem = new ArrayList<>();//
    @BindView(R.id.elv_expand)
    ExpandableListView elv_expand;
    private ArrayList<String> mGroupingNameList;//分组数据
    //item成员数据
    private ArrayList<ArrayList<ContactListInfo.DataBean>> mItemSet;
    private ContactGroupingAdapter mGroupingAdapter;

    private String groupId;
    private List<String> mIdList;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_contact_grouping;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mToolbarTitle.setText("联系人");
        mToolSubTitle.setText("邀请");
        mToolSubTitle.setVisibility(View.VISIBLE);
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
                    mRvGroup.setVisibility(View.VISIBLE);
                    elv_expand.setVisibility(View.GONE);
                } else {
                    mSearchClear.setVisibility(View.GONE);
                    mRvGroup.setVisibility(View.GONE);
                    elv_expand.setVisibility(View.VISIBLE);
                }
            }
        });

        mSearchClear.setOnClickListener(v -> mQuery.getText().clear());
        mToolSubTitle.setOnClickListener(v -> {
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < mContactAdapter.getIdList().size(); i++) {
                if (!sb.toString().contains(mContactAdapter.getIdList().get(i).getFriendUserId())) {
                    sb.append(",");
                    sb.append(mContactAdapter.getIdList().get(i).getFriendUserId());
                }
            }
            for (int i = 0; i < selectItem.size(); i++) {
                sb.append(",");
                sb.append(selectItem.get(i).getFriendUserId());
            }
            if (sb.length() > 0) {
                sb.deleteCharAt(0);
            }

            if (sb.length() <= 0) {
                ToastUtils.showToast("请选择邀请人员");
                return;
            }
            //已有群组邀请成员 （从群聊过来）
            inviteContact(sb.toString());
        });
        mContactList = new ArrayList<>();

        mGroupingNameList = new ArrayList<>();
        mItemSet = new ArrayList<>();
        mGroupingAdapter = new ContactGroupingAdapter(mContext, mGroupingNameList, mItemSet);
        elv_expand.setAdapter(mGroupingAdapter);
        elv_expand.setGroupIndicator(null);//不设置大组指示器图标，因为我们自定义设置了
        elv_expand.setDivider(null);//设置图片可拉伸的


        mContactAdapter = new ContactAdapter(mContactList);
        RclViewHelp.initRcLmVertical(this, mRvGroup, mContactAdapter);
        groupingList();//拿分组数据

    }

    private List<FriendGroupingBean> categoryDatas = new ArrayList<>();

    private void groupingList() {
        Map<String, Object> map = new HashMap<>(2);
        ApiClient.requestNetHandle(mContext, AppConfig.selectFriendCategory, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                categoryDatas.clear();
                categoryDatas = FastJsonUtil.getList(json, FriendGroupingBean.class);
                getContactList();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    @Override
    protected void initListener() {
        super.initListener();
        elv_expand.setOnChildClickListener(new ExpandableListView.OnChildClickListener() {
            @Override
            public boolean onChildClick(ExpandableListView expandableListView, View view, int groupPosition, int childPosition, long id) {
                String userId = mItemSet.get(groupPosition).get(childPosition).getFriendUserId();
                boolean checked = mItemSet.get(groupPosition).get(childPosition).isChecked();
                mItemSet.get(groupPosition).get(childPosition).setChecked(!checked);
                mGroupingAdapter.notifyDataSetChanged();
                if (mItemSet.get(groupPosition).get(childPosition).isChecked()) {
                    selectItem.add(mItemSet.get(groupPosition).get(childPosition));
                } else {
                    selectItem.remove(mItemSet.get(groupPosition).get(childPosition));
                }
                return false;
            }
        });
        elv_expand.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int position) {
                for (int i = 0; i < mGroupingNameList.size(); i++) {
                    if (position != i) {
                        elv_expand.collapseGroup(i);
                    }
                }
            }
        });
    }

    @Override
    protected void initData() {
        super.initData();

    }

    @Override
    protected void onEventComing(EventCenter center) {

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        groupId = intent.getStringExtra("groupId");
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
        map.put("groupId", groupId);
        ApiClient.requestNetHandle(this, AppConfig.USER_FRIEND_LIST, "请稍等...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ContactListInfo info = FastJsonUtil.getObject(json, ContactListInfo.class);
                mContactList.clear();
                mContactList.addAll(info.getData());

                if (mContactList.size() > 0) {
                    UserOperateManager.getInstance().saveContactListToLocal(info, json);
                    queryGroupFriendUserList();//查询群里有哪些好友
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    protected void setContactData() {
        for (int i = mContactList.size() - 1; i >= 0; i--) {
//            ContactListInfo.DataBean dataBean = mContactList.get(i);
            if (inGroupFriendUserId.contains(mContactList.get(i).getFriendUserId())) {
                mContactList.remove(mContactList.get(i));
            }
        }
        mGroupingNameList.clear();//分组名
        mItemSet.clear();//分组成员
        //读取本地缓存好友数据
        //List<ContactListInfo.DataBean> localFriendList = UserOperateManager.getInstance().getContactList();
        /*if (categoryDatas.size() > 0) {
            for (int i = 0; i < categoryDatas.size(); i++) {
                mGroupingNameList.add(categoryDatas.get(i).getName());
                ArrayList<ContactListInfo.DataBean> item = new ArrayList<>();
                for (int k = 0; k < mContactList.size(); k++) {
                    if (categoryDatas.get(i).getCategoryId().equals(mContactList.get(k).getCategoryId())) {
                        item.add(mContactList.get(k));//成员
                    }
                }
                mItemSet.add(item);
            }
        }*/

        if (categoryDatas.size() > 0) {
            /*FriendGroupingBean bean = new FriendGroupingBean();
            bean.setName("默认分组");
            categoryDatas.add(bean);*/
            ArrayList<ContactListInfo.DataBean> defaultitem = new ArrayList<>();
            for (int i = 0; i < categoryDatas.size(); i++) {
                mGroupingNameList.add(categoryDatas.get(i).getName());
                ArrayList<ContactListInfo.DataBean> item = new ArrayList<>();
                for (int k = 0; k < mContactList.size(); k++) {
                    if (TextUtils.isEmpty(mContactList.get(k).getCategoryId())) {
                        if (!defaultitem.contains(mContactList.get(k))) {
                            mContactList.get(k).setChecked(false);
                            defaultitem.add(mContactList.get(k));
                        }
                    } else if (categoryDatas.get(i).getCategoryId().equals(mContactList.get(k).getCategoryId())) {
                        mContactList.get(k).setChecked(false);
                        item.add(mContactList.get(k));//成员
                    }
                }
                mItemSet.add(item);
            }
            mItemSet.add(defaultitem);
            mGroupingNameList.add(new String("默认分组"));
        } else {
            ArrayList<ContactListInfo.DataBean> defaultitem = new ArrayList<>();
            for (int k = 0; k < mContactList.size(); k++) {
                if (!defaultitem.contains(mContactList.get(k))) {
                    mContactList.get(k).setChecked(false);
                    defaultitem.add(mContactList.get(k));
                }
            }
            mItemSet.add(defaultitem);
            mGroupingNameList.add(new String("默认分组"));
        }


        mGroupingAdapter.notifyDataSetChanged();
        mContactAdapter.notifyDataSetChanged();
    }

    //查询群里有哪些好友
    public void queryGroupFriendUserList() {
        Map<String, Object> map = new HashMap<>(1);
        map.put("groupId", groupId);

        ApiClient.requestNetHandle(this, AppConfig.GET_USER_IN_GROUP, "请稍等...", map, new ResultListener() {
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
     * @param ids
     */
    private void inviteContact(String ids) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("groupId", groupId);

        /*String userId = "";
        for (int i = 0; i < mIdList.size(); i++) {
            if (i != mIdList.size() - 1) {
                userId += mIdList.get(i) + ",";
            } else {
                userId += mIdList.get(i);
            }
        }*/
        map.put("userId", ids);
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
