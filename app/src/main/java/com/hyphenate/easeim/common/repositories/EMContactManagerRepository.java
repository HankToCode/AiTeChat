package com.hyphenate.easeim.common.repositories;

import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.EMCallBack;
import com.hyphenate.EMValueCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.easeim.DemoHelper;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.db.entity.EmUserEntity;
import com.hyphenate.easeim.common.interfaceOrImplement.ResultCallBack;
import com.hyphenate.easeim.common.net.ErrorCode;
import com.hyphenate.easeim.common.net.Resource;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.hyphenate.easeui.domain.EaseUser;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.exceptions.HyphenateException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class EMContactManagerRepository extends BaseEMRepository{

    public LiveData<Resource<Boolean>> addContact(String username, String reason) {
        return new NetworkOnlyResource<Boolean>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                if(getCurrentUser().equalsIgnoreCase(username)) {
                    callBack.onError(ErrorCode.EM_ADD_SELF_ERROR);
                    return;
                }
                List<String> users = null;
                if(getUserDao() != null) {
                    users = getUserDao().loadAllUsers();
                }
                if(users != null && users.contains(username)) {
                    if(getContactManager().getBlackListUsernames().contains(username)) {
                        callBack.onError(ErrorCode.EM_FRIEND_BLACK_ERROR);
                        return;
                    }
                    callBack.onError(ErrorCode.EM_FRIEND_ERROR);
                    return;
                }
                getContactManager().aysncAddContact(username, reason, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(new MutableLiveData<>(true));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            }

        }.asLiveData();
    }

    public LiveData<Resource<List<EaseUser>>> getContactList() {
        return new NetworkBoundResource<List<EaseUser>, List<EaseUser>>() {

            @Override
            protected boolean shouldFetch(List<EaseUser> data) {
                return true;
            }

            @Override
            protected LiveData<List<EaseUser>> loadFromDb() {
                return getUserDao().loadUsers();
            }

            @Override
            protected void createCall(ResultCallBack<LiveData<List<EaseUser>>> callBack) {
                if(!isLoggedIn()) {
                    callBack.onError(ErrorCode.EM_NOT_LOGIN);
                    return;
                }
                runOnIOThread(()-> {
                    try {
                        List<String> usernames = getContactManager().getAllContactsFromServer();
                        List<String> ids = getContactManager().getSelfIdsOnOtherPlatform();
                        if(usernames == null) {
                            usernames = new ArrayList<>();
                        }
                        if(ids != null && !ids.isEmpty()) {
                            usernames.addAll(ids);
                        }
                        List<EaseUser> easeUsers = EmUserEntity.parse(usernames);
                        if(easeUsers != null && !easeUsers.isEmpty()) {
                            List<String> blackListFromServer = getContactManager().getBlackListFromServer();
                            for (EaseUser user : easeUsers) {
                                if(blackListFromServer != null && !blackListFromServer.isEmpty()) {
                                    if(blackListFromServer.contains(user.getUsername())) {
                                        user.setContact(1);
                                    }
                                }
                            }
                        }
                        sortData(easeUsers);
                        callBack.onSuccess(createLiveData(easeUsers));

                    } catch (HyphenateException e) {
                        e.printStackTrace();
                        callBack.onError(e.getErrorCode(), e.getDescription());
                    }
                });
            }

            @Override
            protected void saveCallResult(List<EaseUser> items) {
                if(getUserDao() != null) {
                    getUserDao().clearUsers();
                    getUserDao().insert(EmUserEntity.parseList(items));
                }
            }

        }.asLiveData();
    }

    /**
     * ?????????????????????
     * @param callBack
     */
    public void getContactList(ResultCallBack<List<EaseUser>> callBack) {
        if(!isLoggedIn()) {
            callBack.onError(ErrorCode.EM_NOT_LOGIN);
            return;
        }
        runOnIOThread(()-> {
            try {
                List<String> usernames = getContactManager().getAllContactsFromServer();
                List<String> ids = getContactManager().getSelfIdsOnOtherPlatform();
                if(usernames == null) {
                    usernames = new ArrayList<>();
                }
                if(ids != null && !ids.isEmpty()) {
                    usernames.addAll(ids);
                }
                List<EaseUser> easeUsers = EmUserEntity.parse(usernames);
                if(usernames != null && !usernames.isEmpty()) {
                    List<String> blackListFromServer = getContactManager().getBlackListFromServer();
                    for (EaseUser user : easeUsers) {
                        if(blackListFromServer != null && !blackListFromServer.isEmpty()) {
                            if(blackListFromServer.contains(user.getUsername())) {
                                user.setContact(1);
                            }
                        }
                    }
                }
                sortData(easeUsers);
                if(callBack != null) {
                    callBack.onSuccess(easeUsers);
                }
            } catch (HyphenateException e) {
                e.printStackTrace();
                if(callBack != null) {
                    callBack.onError(e.getErrorCode(), e.getDescription());
                }
            }
        });
    }

    private void sortData(List<EaseUser> data) {
        if(data == null || data.isEmpty()) {
            return;
        }
        Collections.sort(data, new Comparator<EaseUser>() {

            @Override
            public int compare(EaseUser lhs, EaseUser rhs) {
                if(lhs.getInitialLetter().equals(rhs.getInitialLetter())){
                    return lhs.getNickname().compareTo(rhs.getNickname());
                }else{
                    if("#".equals(lhs.getInitialLetter())){
                        return 1;
                    }else if("#".equals(rhs.getInitialLetter())){
                        return -1;
                    }
                    return lhs.getInitialLetter().compareTo(rhs.getInitialLetter());
                }

            }
        });
    }

    /**
     * ???????????????
     * @return
     */
    public LiveData<Resource<List<EaseUser>>> getBlackContactList() {
        return new NetworkBoundResource<List<EaseUser>, List<EaseUser>>() {
            @Override
            protected boolean shouldFetch(List<EaseUser> data) {
                return true;
            }

            @Override
            protected LiveData<List<EaseUser>> loadFromDb() {
                return getUserDao().loadBlackUsers();
            }

            @Override
            protected void createCall(ResultCallBack<LiveData<List<EaseUser>>> callBack) {
                if(!isLoggedIn()) {
                    callBack.onError(ErrorCode.EM_NOT_LOGIN);
                    return;
                }
                getContactManager().aysncGetBlackListFromServer(new EMValueCallBack<List<String>>() {
                    @Override
                    public void onSuccess(List<String> value) {
                        List<EaseUser> users = EmUserEntity.parse(value);
                        if(users != null && !users.isEmpty()) {
                            for (EaseUser user : users) {
                                user.setContact(1);
                            }
                        }
                        callBack.onSuccess(createLiveData(users));
                    }

                    @Override
                    public void onError(int error, String errorMsg) {
                        callBack.onError(error, errorMsg);
                    }
                });
            }

            @Override
            protected void saveCallResult(List<EaseUser> items) {
                if(getUserDao() != null) {
                    getUserDao().clearBlackUsers();
                    getUserDao().insert(EmUserEntity.parseList(items));
                }
            }

        }.asLiveData();
    }

    /**
     * ???????????????????????????
     * @param callBack
     */
    public void getBlackContactList(ResultCallBack<List<EaseUser>> callBack) {
        if(!isLoggedIn()) {
            callBack.onError(ErrorCode.EM_NOT_LOGIN);
            return;
        }
        getContactManager().aysncGetBlackListFromServer(new EMValueCallBack<List<String>>() {
            @Override
            public void onSuccess(List<String> value) {
                List<EaseUser> users = EmUserEntity.parse(value);
                if(users != null && !users.isEmpty()) {
                    for (EaseUser user : users) {
                        user.setContact(1);
                    }
                }
                if(callBack != null) {
                    callBack.onSuccess(users);
                }
            }

            @Override
            public void onError(int error, String errorMsg) {
                if(callBack != null) {
                    callBack.onError(error, errorMsg);
                }
            }
        });
    }

    /**
     * ???????????????
     * @param username
     * @return
     */
    public LiveData<Resource<Boolean>> deleteContact(String username) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                DemoHelper.getInstance().getModel().deleteUsername(username, true);
                getContactManager().aysncDeleteContact(username, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        DemoHelper.getInstance().deleteContact(username);
                        callBack.onSuccess(createLiveData(true));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            }
        }.asLiveData();
    }

    /**
     * ??????????????????
     * @param username
     * @param both ???????????????????????????????????????both??????????????????????????????????????????????????????
     *             ???????????????????????????????????????????????????????????????????????????????????????
     * @return
     */
    public LiveData<Resource<Boolean>> addUserToBlackList(String username, boolean both) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                getContactManager().aysncAddUserToBlackList(username, both, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(createLiveData(true));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            }
        }.asLiveData();
    }

    /**
     * ???????????????
     * @param username
     * @return
     */
    public LiveData<Resource<Boolean>> removeUserFromBlackList(String username) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                getContactManager().aysncRemoveUserFromBlackList(username, new EMCallBack() {
                    @Override
                    public void onSuccess() {
                        callBack.onSuccess(createLiveData(true));
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                    }

                    @Override
                    public void onProgress(int progress, String status) {

                    }
                });
            }
        }.asLiveData();
    }

    public LiveData<Resource<List<EaseUser>>> getSearchContacts(String keyword) {
        return new NetworkOnlyResource<List<EaseUser>>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<List<EaseUser>>> callBack) {
                EaseThreadManager.getInstance().runOnIOThread(()-> {
                    List<EaseUser> easeUsers = null;
                    if(getUserDao() != null) {
                        easeUsers = getUserDao().loadContacts();
                    }
                    List<EaseUser> list = new ArrayList<>();
                    if(easeUsers != null && !easeUsers.isEmpty()) {
                        for (EaseUser user : easeUsers) {
                            if(user.getUsername().contains(keyword) || (!TextUtils.isEmpty(user.getNickname()) && user.getNickname().contains(keyword))) {
                                list.add(user);
                            }
                        }
                    }
                    callBack.onSuccess(createLiveData(list));
                });

            }
        }.asLiveData();
    }
}
