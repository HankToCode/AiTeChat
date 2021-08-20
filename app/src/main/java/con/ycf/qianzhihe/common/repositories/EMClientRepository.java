package con.ycf.qianzhihe.common.repositories;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.hyphenate.EMCallBack;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMGroup;
import con.ycf.qianzhihe.DemoApplication;
import con.ycf.qianzhihe.DemoHelper;
import con.ycf.qianzhihe.app.domain.EaseUser;
import con.ycf.qianzhihe.common.constant.DemoConstant;
import con.ycf.qianzhihe.common.db.DemoDbHelper;
import con.ycf.qianzhihe.common.interfaceOrImplement.DemoEmCallBack;
import con.ycf.qianzhihe.common.livedatas.LiveDataBus;
import con.ycf.qianzhihe.common.net.ErrorCode;
import con.ycf.qianzhihe.common.net.Resource;
import con.ycf.qianzhihe.common.interfaceOrImplement.ResultCallBack;
import con.ycf.qianzhihe.app.api.old_data.LoginInfo;
import con.ycf.qianzhihe.app.api.global.UserComm;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EMLog;

import java.util.List;

/**
 * 作为EMClient的repository,处理EMClient相关的逻辑
 */
public class EMClientRepository extends BaseEMRepository {

    private static final String TAG = EMClientRepository.class.getSimpleName();

    /**
     * 登录过后需要加载的数据
     *
     * @return
     */
    public LiveData<Resource<Boolean>> loadAllInfoFromHX() {
        return new NetworkOnlyResource<Boolean>() {

            @Override
            protected void createCall(ResultCallBack<LiveData<Boolean>> callBack) {
                if (isAutoLogin()) {
                    runOnIOThread(() -> {
                        if (isLoggedIn()) {
                            loadAllConversationsAndGroups();
                            callBack.onSuccess(createLiveData(true));
                        } else {
                            callBack.onError(ErrorCode.EM_NOT_LOGIN);
                        }

                    });
                } else {
                    callBack.onError(ErrorCode.EM_NOT_LOGIN);
                }

            }
        }.asLiveData();
    }

    /**
     * 从本地数据库加载所有的对话及群组
     */
    private void loadAllConversationsAndGroups() {
        // 初始化数据库
        initDb();
        // 从本地数据库加载所有的对话及群组
        getChatManager().loadAllConversations();
        getGroupManager().loadAllGroups();
    }

    /**
     * 注册
     *
     * @param userName
     * @param pwd
     * @return
     */
    public LiveData<Resource<String>> registerToHx(String userName, String pwd) {
        return new NetworkOnlyResource<String>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<String>> callBack) {
                //注册之前先判断SDK是否已经初始化，如果没有先进行SDK的初始化
                if (!DemoHelper.getInstance().isSDKInit) {
                    DemoHelper.getInstance().init(DemoApplication.getInstance());
                    DemoHelper.getInstance().getModel().setCurrentUserName(userName);
                }
                runOnIOThread(() -> {
                    try {
                        EMClient.getInstance().createAccount(userName, pwd);
                        callBack.onSuccess(createLiveData(userName));
                    } catch (HyphenateException e) {
                        callBack.onError(e.getErrorCode(), e.getMessage());
                    }
                });
            }

        }.asLiveData();
    }

    /**
     * 登录到服务器，可选择密码登录或者token登录
     * 登录之前先初始化数据库，如果登录失败，再关闭数据库;如果登录成功，则再次检查是否初始化数据库
     *
     * @return
     */
    public LiveData<Resource<EaseUser>> loginToServer() {
        return new NetworkOnlyResource<EaseUser>() {

            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<EaseUser>> callBack) {
                final LoginInfo loginInfo = UserComm.getUserInfo();

                DemoHelper.getInstance().init(DemoApplication.getInstance());
                DemoHelper.getInstance().getModel().setCurrentUserName(loginInfo.getNickName());
                DemoHelper.getInstance().getModel().setCurrentUserPwd(loginInfo.getPassword());
                EMClient.getInstance().login(loginInfo.getIdh(), "123456", new DemoEmCallBack() {
                    @Override
                    public void onSuccess() {
                        successForCallBack(callBack);
                    }

                    @Override
                    public void onError(int code, String error) {
                        callBack.onError(code, error);
                        closeDb();
                    }
                });
            }

        }.asLiveData();
    }

    /**
     * 退出登录
     *
     * @param unbindDeviceToken
     * @return
     */
    public LiveData<Resource<Boolean>> logout(boolean unbindDeviceToken) {
        return new NetworkOnlyResource<Boolean>() {
            @Override
            protected void createCall(@NonNull ResultCallBack<LiveData<Boolean>> callBack) {
                EMClient.getInstance().logout(unbindDeviceToken, new EMCallBack() {

                    @Override
                    public void onSuccess() {
                        DemoHelper.getInstance().logoutSuccess();
                        //reset();
                        if (callBack != null) {
                            callBack.onSuccess(createLiveData(true));
                        }

                    }

                    @Override
                    public void onProgress(int progress, String status) {
                    }

                    @Override
                    public void onError(int code, String error) {
                        //reset();
                        if (callBack != null) {
                            callBack.onError(code, error);
                        }
                    }
                });
            }
        }.asLiveData();
    }

    private void successForCallBack(@NonNull ResultCallBack<LiveData<EaseUser>> callBack) {
        final LoginInfo loginInfo = UserComm.getUserInfo();
        // ** manually load all local groups and conversation
        loadAllConversationsAndGroups();
        // update current user's display name for APNs
        try {
            EMClient.getInstance().pushManager().updatePushNickname(loginInfo.getNickName());
        } catch (HyphenateException ignored) {
        }

        //从服务器拉取加入的群，防止进入会话页面只显示id
        getAllJoinGroup();
        // get current user id
        String currentUser = EMClient.getInstance().getCurrentUser();
        EaseUser user = new EaseUser(currentUser);
        callBack.onSuccess(new MutableLiveData<>(user));
    }

    private void getAllJoinGroup() {
        new EMGroupManagerRepository().getAllGroups(new ResultCallBack<List<EMGroup>>() {
            @Override
            public void onSuccess(List<EMGroup> value) {
                //加载完群组信息后，刷新会话列表页面，保证展示群组名称
                EMLog.i("ChatPresenter", "isGroupsSyncedWithServer success");
                EaseEvent event = EaseEvent.create(DemoConstant.GROUP_CHANGE, EaseEvent.TYPE.GROUP);
                LiveDataBus.get().with(DemoConstant.GROUP_CHANGE).postValue(event);
            }

            @Override
            public void onError(int error, String errorMsg) {

            }
        });
    }

    private void closeDb() {
        DemoDbHelper.getInstance(DemoApplication.getInstance()).closeDb();
    }
}
