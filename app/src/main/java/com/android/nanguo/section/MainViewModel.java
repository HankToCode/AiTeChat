package com.android.nanguo.section;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.android.nanguo.app.api.Constant;
import com.android.nanguo.app.api.global.SP;
import com.android.nanguo.app.base.BaseActivity;
import com.android.nanguo.app.utils.hxSetMessageFree.UnReadMsgCount;
import com.android.nanguo.common.db.DemoDbHelper;
import com.android.nanguo.common.db.dao.InviteMessageDao;
import com.android.nanguo.common.livedatas.LiveDataBus;
import com.android.nanguo.common.utils.PreferenceManager;

public class MainViewModel extends AndroidViewModel {
    private final InviteMessageDao inviteMessageDao;
    private final MutableLiveData<String> homeUnReadObservable;
    private final MutableLiveData<String> homeUnReadContactObservable;

    public MainViewModel(@NonNull Application application) {
        super(application);
        inviteMessageDao = DemoDbHelper.getInstance(application).getInviteMessageDao();
        homeUnReadObservable = new MutableLiveData<>();
        homeUnReadContactObservable = new MutableLiveData<>();
    }


    public LiveData<String> homeUnReadObservable() {
        return homeUnReadObservable;
    }

    public LiveData<String> homeUnReadContactObservable() {
        return homeUnReadContactObservable;
    }

    public LiveDataBus messageChangeObservable() {
        return LiveDataBus.get();
    }

    public void checkUnreadMsg(BaseActivity activity) {

        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(Constant.ADMIN);
        UnReadMsgCount.getUnreadMessageCount().as(activity.autoDispose()).subscribe(unreadMessageCount1 -> {
            /*int unreadCount = 0;
            if (inviteMessageDao != null) {
                unreadCount = inviteMessageDao.queryUnreadCount();
            }

            int unreadMessageCount = unreadMessageCount1 - ((conversation != null && conversation.getUnreadMsgCount() > 0) ? conversation.getUnreadMsgCount() : 0);
            int applyJoinGroupcount = (int) PreferenceManager.getInstance().getParam(SP.APPLY_JOIN_GROUP_NUM, 0);
            int addUserCount = (int) PreferenceManager.getInstance().getParam(SP.APPLY_ADD_USER_NUM, 0);

            String count = getUnreadCount(applyJoinGroupcount + addUserCount + unreadCount + unreadMessageCount);*/
            String count = getUnreadCount(unreadMessageCount1 - ((conversation != null && conversation.getUnreadMsgCount() > 0) ? conversation.getUnreadMsgCount() : 0));
            homeUnReadObservable.postValue(count);
        });

        int addUserCount = (int) PreferenceManager.getInstance().getParam(SP.APPLY_ADD_USER_NUM, 0);

        homeUnReadContactObservable.postValue(getUnreadCount(addUserCount));

    }

    /**
     * 获取未读消息数目
     *
     * @param count
     * @return
     */
    private String getUnreadCount(int count) {
        if (count <= 0) {
            return null;
        }
        if (count > 99) {
            return "99+";
        }
        return String.valueOf(count);
    }

}
