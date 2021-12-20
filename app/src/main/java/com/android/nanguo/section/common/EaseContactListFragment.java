/**
 * Copyright (C) 2016 Hyphenate Inc. All rights reserved.
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.nanguo.section.common;

import android.os.Bundle;
import android.os.Handler;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.android.nanguo.R;
import com.android.nanguo.app.api.global.EventUtil;
import com.android.nanguo.app.api.old_data.ContactListInfo;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitFragment;
import com.android.nanguo.app.domain.EaseUser;
import com.android.nanguo.app.operate.UserOperateManager;
import com.android.nanguo.app.weight.ease.EaseContactList;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * contact list
 */
public class EaseContactListFragment extends BaseInitFragment {
    private static final String TAG = "EaseContactListFragment";
    protected List<EaseUser> contactList;
    protected ListView listView;
    protected boolean hidden;
    protected EditText query;
    protected Handler handler = new Handler();
    protected EaseUser toBeProcessUser;
    protected String toBeProcessUsername;
    protected EaseContactList contactListLayout;
    protected boolean isConflict;
    protected FrameLayout contentContainer;
    List<ContactListInfo.DataBean> mContactList;
    protected TextView tv_number;
    protected FrameLayout mContentContainer;
    protected TextView mTvNumber;

    @Override
    protected int getLayoutId() {
        return R.layout.ease_fragment_contact_list;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        //to avoid crash when open app after long time stay in background
        // after user logged into another device
        if (savedInstanceState != null && savedInstanceState.getBoolean(
                "isConflict", false)) {
            return;
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        contentContainer =
                (FrameLayout) getView().findViewById(R.id.content_container);
        contactListLayout =
                (EaseContactList) getView().findViewById(R.id.contact_list);
        listView = contactListLayout.getListView();
        //search
        query = (EditText) getView().findViewById(R.id.query);
        tv_number = (TextView) getView().findViewById(R.id.tv_number);


        EMClient.getInstance().addConnectionListener(connectionListener);

        contactList = new ArrayList<EaseUser>();
        checkSeviceContactData();
        contactListLayout.setShowSiderBar(true);
        //init list
        contactListLayout.init(contactList);

        if (listItemClickListener != null) {
            listView.setOnItemClickListener((parent, view, position, id) -> {
                EaseUser user =
                        (EaseUser) listView.getItemAtPosition(position);
                listItemClickListener.onListItemClicked(user);
            });
        }

        mContentContainer = (FrameLayout) findViewById(R.id.content_container);
        mTvNumber = (TextView) findViewById(R.id.tv_number);
    }

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {
        //刷新通讯录和本地数据库
        if (center.getEventCode() == EventUtil.REFRESH_CONTACT) {
            getContactList();
        }

    }


    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }


    /**
     * refresh ui
     */
    public void refresh() {
        checkSeviceContactData();
        contactListLayout.refresh();
    }


    @Override
    public void onDestroy() {
        EMClient.getInstance().removeConnectionListener(connectionListener);
        super.onDestroy();
    }

    public void checkSeviceContactData() {

        mContactList = UserOperateManager.getInstance().getContactList();

        if (mContactList == null) {
            //本地不存在数据直接请求接口
            getContactList();
        } else {
            setContactData(mContactList);
            ApiClient.requestNetHandle(getContext(), AppConfig.CHECK_FRIEND_DATA_VERSION, "",
                    null, new ResultListener() {
                        @Override
                        public void onSuccess(String json, String msg) {
                            int cacheVersion = FastJsonUtil.getInt(json, "cacheVersion");
                            //本地数据版本更服务器不一致 就需要更新数据接口
//                            if (cacheVersion != UserOperateManager.getInstance().getContactVersion()) {
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
    public void getContactList() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("pageNum", 1);
        map.put("pageSize", 10000);

        //未同步通讯录到本地
        ApiClient.requestNetHandle(getActivity(), AppConfig.USER_FRIEND_LIST,
                "", map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        ContactListInfo info = FastJsonUtil.getObject(json,
                                ContactListInfo.class);
                        List<ContactListInfo.DataBean> mContactList = new ArrayList<>();
                        mContactList.addAll(info.getData());
                        tv_number.setText(mContactList.size() + "个朋友及联系人");
                        if (mContactList.size() > 0) {
                            UserOperateManager.getInstance().saveContactListToLocal(info, json);
                            setContactData(mContactList);
                        } else {
                            contactList.clear();
                            contactListLayout.refresh();
                        }


                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });


        //// sorting

    }


    protected void setContactData(List<ContactListInfo.DataBean> mContactList) {
        contactList.clear();

        contactList.addAll(UserOperateManager.getInstance().toContactList(mContactList));
        /*//todo 需要优化
        Map<String, EaseUser> userlist = new HashMap<String,
                EaseUser>(5);
        for (ContactListInfo.DataBean bean : mContactList) {
            EaseUser user = new EaseUser(bean.getFriendUserId() + Constant.ID_REDPROJECT);
            user.setNickname(bean.getFriendNickName());
            user.setAvatar(bean.getFriendUserHead());
            user.setLine(bean.getLine());//在线状态
            user.setUserSign(bean.getUserSign());
            user.setVipLevel(bean.getVipLevel());
            EaseCommonUtils.setUserInitialLetter(user);
            if (bean.getBlackStatus().equals("0")) {
                contactList.add(user);
            }
//            EaseUser userLocal = new EaseUser(bean.getFriendUserId() + Constant.ID_REDPROJECT);
//            userLocal.setAvatar(bean.getFriendUserHead());
//            userLocal.setNickname(bean.getFriendNickName());
//            userLocal.setLine(bean.getLine());
//            EaseCommonUtils.setUserInitialLetter(user);
            userlist.put(bean.getFriendUserId() + Constant.ID_REDPROJECT, user);

        }*/


        Collections.sort(contactList, (lhs, rhs) -> {
            if (lhs.getInitialLetter().equals(rhs.getInitialLetter())) {
                return lhs.getNickname().compareTo(rhs.getNickname());
            } else {
                if ("#".equals(lhs.getInitialLetter())) {
                    return 1;
                } else if ("#".equals(rhs.getInitialLetter())) {
                    return -1;
                }
                return lhs.getInitialLetter().compareTo(rhs
                        .getInitialLetter());
            }
        });

        contactListLayout.refresh();
    }

    protected EMConnectionListener connectionListener =
            new EMConnectionListener() {

                @Override
                public void onDisconnected(int error) {
                    if (error == EMError.USER_REMOVED || error == EMError.USER_LOGIN_ANOTHER_DEVICE || error == EMError.SERVER_SERVICE_RESTRICTED) {
                        isConflict = true;
                    } else {
                        getActivity().runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                onConnectionDisconnected();
                            }

                        });
                    }
                }

                @Override
                public void onConnected() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            onConnectionConnected();
                        }

                    });
                }
            };
    private EaseContactListItemClickListener listItemClickListener;


    protected void onConnectionDisconnected() {
    }

    protected void onConnectionConnected() {
    }

//    /**
//     * set contacts map, key is the hyphenate id
//     *
//     * @param contactsMap
//     */
//    public void setContactsMap(Map<String, EaseUser> contactsMap) {
//        this.contactsMap = contactsMap;
//    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    public interface EaseContactListItemClickListener {
        /**
         * on click event for item in contact list
         *
         * @param user --the user of item
         */
        void onListItemClicked(EaseUser user);

    }

    /**
     * set contact list item click listener
     *
     * @param listItemClickListener
     */
    public void setContactListItemClickListener(EaseContactListItemClickListener listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

}
