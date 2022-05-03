package com.android.nanguo.section.chat.fragment;

import static android.view.View.VISIBLE;

import static com.android.nanguo.app.api.Constant.ID_REDPROJECT;
import static com.android.nanguo.section.conversation.ChatBgActivity.BG_NONE;

import android.app.Activity;
import android.content.ClipboardManager;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.webkit.MimeTypeMap;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.android.nanguo.app.api.old_data.ContactListInfo;
import com.android.nanguo.section.conversation.BaseConversationListFragment;
import com.hyphenate.EMMessageListener;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.hyphenate.chat.EMGroup;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easecallkit.EaseCallKit;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easeui.manager.EaseThreadManager;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.android.nanguo.R;
import com.android.nanguo.app.api.Constant;
import com.android.nanguo.app.api.EaseConstant;
import com.android.nanguo.app.api.global.EventUtil;
import com.android.nanguo.app.api.global.UserComm;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.api.old_data.GroupDetailInfo;
import com.android.nanguo.app.api.old_data.RoomInfo;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitFragment;
import com.android.nanguo.app.domain.EaseEmojicon;
import com.android.nanguo.app.operate.GroupOperateManager;
import com.android.nanguo.app.operate.UserOperateManager;
import com.android.nanguo.app.utils.ProjectUtil;
import com.android.nanguo.app.utils.my.MyHelper;
import com.android.nanguo.app.utils.my.Storage;
import com.android.nanguo.app.weight.ease.EaseAlertDialog;
import com.android.nanguo.app.weight.ease.EaseChatExtendMenu;
import com.android.nanguo.app.weight.ease.EaseChatInputMenu;
import com.android.nanguo.app.weight.ease.EaseChatMessageList;
import com.android.nanguo.app.weight.ease.EaseCommonUtils;
import com.android.nanguo.app.weight.ease.EaseUI;
import com.android.nanguo.app.weight.ease.EaseVoiceRecorderView;
import com.android.nanguo.app.weight.ease.chatrow.EaseCustomChatRowProvider;
import com.android.nanguo.app.weight.ease.model.EaseAtMessageHelper;
import com.android.nanguo.common.rx.NextObserver;
import com.android.nanguo.section.account.activity.UserInfoDetailActivity;
import com.android.nanguo.section.common.GroupMemberActivity;
import com.android.nanguo.section.common.MyGroupDetailActivity;
import com.android.nanguo.section.common.SelAddrMapActivity;
import com.hyphenate.easeui.interfaces.EaseGroupListener;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.PathUtil;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.DensityUtils;
import com.zds.base.util.NumberUtils;
import com.zds.base.util.Preference;
import com.zds.base.util.StringUtil;
import com.zds.base.util.UriUtil;

import org.json.JSONArray;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class BaseChatFragment extends BaseInitFragment implements EMMessageListener {
    protected static final int REQUEST_CODE_MAP = 1;
    protected static final int REQUEST_CODE_CAMERA = 2;
    protected static final int REQUEST_CODE_LOCAL = 3;
    protected static final int REQUEST_CODE_SEND_CARD = 4;
    protected static final int REQUEST_CODE_TRNSFER = 5;
    protected static final int REQUEST_CODE_PERSON_BALL = 6;

    /**
     * params to fragment
     */
    protected int chatType;
    //环信userId 或者groupId
    protected String emChatId;

    protected int roomType;

    protected EMConversation conversation;

    protected InputMethodManager inputManager;
    protected ClipboardManager clipboard;

    protected File cameraFile;
    protected SwipeRefreshLayout swipeRefreshLayout;
    protected ListView listView;

    protected boolean isloading;
    protected boolean haveMoreData = true;
    protected int pagesize = 100;
    protected GroupListener groupListener;
    protected EMMessage contextMenuMessage;
    protected EMGroup group;
    protected boolean isBottom;
    static final int ITEM_TAKE_PICTURE = 1;
    static final int ITEM_PICTURE = 2;
    static final int ITEM_LOCATION = 3;


    //    protected int[] itemStrings = {R.string.attach_take_pic, R.string
//    .attach_picture};
//    protected int[] itemdrawables = {R.drawable.zhaop, R.drawable.zhaop};
//    protected int[] itemIds = {ITEM_TAKE_PICTURE, ITEM_PICTURE};
    int lastVisibleItemPosition;// 标记上次滑动位置
    boolean scrollFlag = false; // 标记是否滑动
    boolean canLoadMessage = true;

    private List<EMMessage> unReadMessages = new CopyOnWriteArrayList<>();
    //是否客服
    boolean isService = false;

    @BindView(R.id.message_list)
    EaseChatMessageList mMessageList;
    @BindView(R.id.voice_recorder)
    EaseVoiceRecorderView mVoiceRecorder;
    @BindView(R.id.input_menu)
    EaseChatInputMenu mInputMenu;
    @BindView(R.id.tv_yue)
    TextView mTvYue;
    @BindView(R.id.ll_money_show)
    LinearLayout mLlMoneyShow;
    @BindView(R.id.bg_img)
    ImageView mRlTop;
    @BindView(R.id.tv_unread)
    TextView tvUnread;
    @BindView(R.id.title_bar)
    EaseTitleBar titleBar;
    @BindView(R.id.rl_root)
    RelativeLayout rlRoot;

    //带入金额
//    protected double baseMoney = 0;
    protected String startTime;
    private boolean isMessageListInited;
    protected MyItemClickListener extendMenuItemClickListener;
    private ExecutorService fetchQueue;
    protected RoomInfo roomInfo;
    protected GroupDetailInfo groupDetailInfo;
    /**
     * 服务器的群id
     * groupUserName 用户在该群的昵称
     */
    protected String groupId;

    protected String groupName;

    protected String nickName;

    protected boolean isRoaming() {
        return false;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chat;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        titleBar.setRightLayoutVisibility(VISIBLE);

        LinearLayout.LayoutParams layoutParams =
                (LinearLayout.LayoutParams) mRlTop.getLayoutParams();
        layoutParams.width = (int) DensityUtils.getWidthInPx(getActivity());
        layoutParams.height = (int) DensityUtils.getHeightInPx(getActivity());
        mRlTop.setLayoutParams(layoutParams);
        // message list layout
        if (chatType != EaseConstant.CHATTYPE_SINGLE) {
            mMessageList.setShowUserNick(true);
        }
//      messageList.setAvatarShape(1);
        listView = mMessageList.getListView();
        extendMenuItemClickListener = new MyItemClickListener();
        registerExtendMenuItem();
        UserOperateManager.getInstance().loadGroupUserData(emChatId);
        // init input menu
        mInputMenu.init(null);
        mInputMenu.setChatInputMenuListener(new EaseChatInputMenu.ChatInputMenuListener() {

            @Override
            public void onSendMessage(String content) {

                sendTextMessage(content);
            }

            @Override
            public boolean onPressToSpeakBtnTouch(View v, MotionEvent event) {
                return mVoiceRecorder.onPressToSpeakBtnTouch(v, event,
                        new EaseVoiceRecorderView.EaseVoiceRecorderCallback() {

                            @Override
                            public void onVoiceRecordComplete(String voiceFilePath,
                                                              int voiceTimeLength) {
                                sendVoiceMessage(voiceFilePath,
                                        voiceTimeLength);
                            }
                        });
            }

            @Override
            public void onAtSomeOne() {
                if (chatType != EaseConstant.CHATTYPE_GROUP) {
                    return;
                }
                openAtPage();
            }


            @Override
            public void onBigExpressionClicked(EaseEmojicon emojicon) {
                sendBigExpressionMessage(emojicon.getName(),
                        emojicon.getIdentityCode());
            }
        });

        swipeRefreshLayout = mMessageList.getSwipeRefreshLayout();
        swipeRefreshLayout.setColorSchemeResources(R.color.holo_blue_bright,
                R.color.holo_green_light,
                R.color.holo_orange_light, R.color.holo_red_light);

        inputManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        clipboard =
                (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        if (isRoaming()) {
            fetchQueue = Executors.newSingleThreadExecutor();
        }


        if (!TextUtils.isEmpty(nickName))
            titleBar.setTitle(nickName);

        if (chatType == EaseConstant.CHATTYPE_SINGLE) {

            boolean isSystem = getArguments().getBoolean("isSystem");

            if (isSystem) {
                titleBar.setTitle(getArguments().getString(Constant.NICKNAME));
                mInputMenu.setVisibility(View.GONE);
            } else {
                if (emChatId.contains("d816636e130411ecab930c42a1a8807a")) {//fbce17090a6611ecab930c42a1a8807a
                    titleBar.setTitle("南国时光客服");
                } else if (emChatId.contains("0d777a9c8f9311eb844f00163e0654c2")) {
                    titleBar.setTitle("异常处理客服");
                } else if (UserOperateManager.getInstance().hasUserName(emChatId)) {
                    titleBar.setTitle(UserOperateManager.getInstance().getUserName(emChatId));
                }
                mInputMenu.setVisibility(VISIBLE);
            }
            titleBar.setRightImageResource(R.mipmap.ic_icon3);
            onConversationInit();
            onMessageListInit();
        } else {
            titleBar.setRightImageResource(R.mipmap.ic_icon3);
            if (chatType == EaseConstant.CHATTYPE_GROUP) {
                EMGroup group = EMClient.getInstance().groupManager().getGroup(emChatId);
                groupListener = new GroupListener();
                EMClient.getInstance().groupManager().addGroupChangeListener(groupListener);
                try {
                    group = EMClient.getInstance().groupManager().getGroup(emChatId);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                titleBar.setTitle(group.getGroupName());
                onConversationInit();
                onMessageListInit();
                if (roomType <= 0) {
                    if (roomInfo != null) {

                        roomType = roomInfo.getType();
                    }
                }
            }
        }
        titleBar.setOnBackPressListener(view -> onBackPressed());
        titleBar.setRightLayoutClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                    getContext().startActivity(new Intent(getContext(),
                            UserInfoDetailActivity.class).putExtra(
                            "friendUserId", emChatId).putExtra(
                            "from", "1").putExtra("chatType",
                            chatType));
                } else {
                    startActivity(new Intent(getActivity(),
                            MyGroupDetailActivity.class).putExtra("username",
                            emChatId).putExtra("groupId", groupId));
                }
            }
        });
        setRefreshLayoutListener();

        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view,
                                             int scrollState) {
                switch (scrollState) {
                    case SCROLL_STATE_TOUCH_SCROLL:
                        scrollFlag = true;
                        break;
                    case SCROLL_STATE_FLING:
                        scrollFlag = false;
                        break;
                    case SCROLL_STATE_IDLE:
                        scrollFlag = false;
                        break;
                    default:
                        break;
                }

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (scrollFlag) {
                    if ((firstVisibleItem + visibleItemCount) < totalItemCount) {
                        canLoadMessage = false;
                    }
                }
                if (visibleItemCount + firstVisibleItem == totalItemCount) {
                    if (mMessageList != null && unReadMessages.size() > 0 && isListViewReachBottomEdge(listView)) {
                        clearUnreadState();
                    }
                }
            }
        });

        tvUnread.setOnClickListener(view -> {
            listView.smoothScrollToPosition(listView.getCount());
            clearUnreadState();
        });

//        titleBar.setUnreadMsgCount(getOtherUnreadMsgCountTotal());

        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            getHxGroupIdToGroupId();
        }
        if (isService)
            mInputMenu.getPrimaryMenu().hideVoice();
    }


    /**
     * @点击监听
     */
    public void openAtPage() {
        Bundle bundle1 = new Bundle();
        bundle1.putBoolean(Constant.PARAM_FOR_AT_MERBER, true);
        bundle1.putString(Constant.PARAM_EM_GROUP_ID, emChatId);
        Intent intent = new Intent(getContext(), GroupMemberActivity.class);
        intent.putExtras(bundle1);
        startActivityForResult(intent, Constant.REQUEST_AT_MERBER_CODE);
    }

    public void clearUnreadState() {
        putToMessageList(unReadMessages);
        unReadMessages.clear();
        canLoadMessage = true;
        tvUnread.setVisibility(View.GONE);
    }

    /**
     * make a voice call
     */
    protected void startVoiceCall(String toChatUsername, String avatarUrl) {
        if (!EMClient.getInstance().isConnected()) {
            Toast.makeText(getActivity(), R.string.not_connect_to_server,
                    Toast.LENGTH_SHORT).show();
        } else {
            Map<String, Object> ext = new HashMap<>();
            ext.put(Constant.AVATARURL, avatarUrl);
            EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VOICE_CALL, toChatUsername, ext);
        }
    }


    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.FLUSHNOTICE) {
        } else if (center.getEventCode() == EventUtil.FLUSHRENAME) {
            if (center.getData().toString().equals(emChatId)) {
            }
        } else if (center.getEventCode() == EventUtil.ROOM_INFO) {
            //刷新房间信息（房间禁言）
            queryGroupDetail();
        } else if (center.getEventCode() == EventUtil.AGREE_SUCCESS) {
        } else if (center.getEventCode() == EventUtil.UPDATE_PACK) {
        } else if (center.getEventCode() == EventUtil.REFRESH_GROUP_NAME) {
            titleBar.setTitle(center.getData().toString());
            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        EMClient.getInstance().groupManager().getGroupFromServer(emChatId);
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        } else if (center.getEventCode() == EventUtil.CLEAR_HUISTROY) {
            emptyHistory();
        } else if (center.getEventCode() == EventUtil.DELETE_CONTACT) {
            getActivity().finish();
        } else if (center.getEventCode() == EventUtil.SET_CHAT_BG) {
            //单个背景设置回调
            String path =
                    MyHelper.getInstance().getModel().getChatBg(emChatId);
            if (path != null && path.length() > 0) {
                if (path.equals(BG_NONE)) {
                    mRlTop.setImageResource(NumberUtils.parseInt(Storage.getChatBgLocal(emChatId)));
                } else {
                    mRlTop.setImageDrawable(Drawable.createFromPath(path));
                }
            }
        } else if (center.getEventCode() == EventUtil.REFRESH_MY_GROUP_NAME) {
            //我的群昵称
            groupName = center.getData().toString();
        } else if (center.getEventCode() == EventUtil.SEND_PERSON_RED_PKG) {//xgp add
            // TODO: 2021/4/1 私发红包环信不推送红包消息的临时解决方案
            startTimer();
        }


    }


    /**
     * Bundle  传递数据
     */
    @Override
    protected void initArgument() {
        Bundle extras = getArguments();
        chatType = extras.getInt(EaseConstant.EXTRA_CHAT_TYPE,
                EaseConstant.CHATTYPE_SINGLE);
        // userId you are chat with or group id
        emChatId = extras.getString(EaseConstant.EXTRA_USER_ID);
        roomType = extras.getInt(Constant.ROOMTYPE);
        nickName = extras.getString(Constant.NICKNAME);
        isService = extras.getBoolean(Constant.CUSTOM_KF, false);
        String path =
                MyHelper.getInstance().getModel().getChatBg(emChatId);

        if (path != null && path.length() > 0) {
            if (path.equals(BG_NONE)) {
//                mRlTop.setImageResource(NumberUtils.parseInt(Storage.getChatBgLocal(emChatId)));//
            } else {
                mRlTop.setImageDrawable(Drawable.createFromPath(path));
            }
        } else {
            if (Storage.getGlobleChatBg() != null && Storage.getGlobleChatBg().length() > 0) {
                if (Storage.getGlobleChatBg().equals(BG_NONE)) {
                    mRlTop.setImageResource(Storage.getGlobleChatBgLocal());
                } else {
                    mRlTop.setImageDrawable(Drawable.createFromPath(Storage.getGlobleChatBg()));
                }
            }
        }

        //发送自己的名片和好友的名片给他人判断
        if (extras.getString("from") != null && extras.getString("from").equals("1")) {
            //发送自己的名片和好友的名片给他人判断,直接发送名片
            String nickName = extras.getString("nickName");
            String userCode = extras.getString("userCode");
            String avatar = extras.getString("avatar");
            String friendUserId = extras.getString("friendUserId");

            if (nickName != null && !"".equals(nickName)) {
                sendExTextMessage("个人名片", nickName, userCode, avatar,
                        friendUserId);
//                showDialog(nickName, userCode, avatar, friendUserId);
            } else {
                Toast.makeText(getActivity(), "名片发送失败", Toast.LENGTH_SHORT).show();
            }

        }
    }

    private void showDialog(String nickName, String userCode, String avatar,
                            String friendUserId) {
        new EaseAlertDialog(getContext(), null, "确定发送该名片？", null,
                new EaseAlertDialog.AlertDialogUser() {
                    @Override
                    public void onResult(boolean confirmed, Bundle bundle) {
                        if (confirmed) {
                            sendExTextMessage("个人名片", nickName, userCode,
                                    avatar,
                                    friendUserId);
                        }
                    }
                }, true).show();
    }


    /**
     * 根据环信群ID获取自己服务器的群id
     */
    private void getHxGroupIdToGroupId() {

        groupId = GroupOperateManager.getInstance().getGroupId(emChatId);

        if (TextUtils.isEmpty(groupId)) {
            Map<String, Object> map = new HashMap<>(1);
            map.put("huanxinGroupId", emChatId);
            ApiClient.requestNetHandle(getContext(), AppConfig.GET_GROUP_ID, "",
                    map, new ResultListener() {
                        @Override
                        public void onSuccess(String json, String msg) {
                            groupId = json;
                            GroupOperateManager.getInstance().saveGroupIdToLocal(emChatId, groupId);
                            loadGroupDataFromLocal();
                        }

                        @Override
                        public void onFailure(String msg) {
                        }
                    });
        } else {
            loadGroupDataFromLocal();
        }


    }

    public void loadGroupDataFromLocal() {
        groupDetailInfo = GroupOperateManager.getInstance().getGroupData(emChatId);
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        if (groupDetailInfo != null) {
            setGroupData();
        }

        queryGroupDetail();

//            ApiClient.requestNetHandle(getContext(), AppConfig.CHECK_GROUP_DATA_VERSION, "",
//                    map, new ResultListener() {
//                        @Override
//                        public void onSuccess(String json, String msg) {
//                            int groupVersion = FastJsonUtil.getInt(json, "groupVersion");
//                            int cacheVersion = FastJsonUtil.getInt(json, "cacheVersion");
//                            //本地数据版本更服务器不一致 就需要更新数据接口
//                            if (groupDetailInfo.getGroupVersion() != groupVersion) {
//                                queryGroupDetail();
//                            }
//                        }
//
//                        @Override
//                        public void onFailure(String msg) {
//                            queryGroupDetail();
//                        }
//                    });

    }


    /**
     * 查询群详情
     */
    public void queryGroupDetail() {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        ApiClient.requestNetHandle(getContext(), AppConfig.GET_GROUP_INFO, "",
                map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        if (json != null && json.length() > 0) {
                            groupDetailInfo = FastJsonUtil.getObject(json,
                                    GroupDetailInfo.class);
                            setGroupData();

                            GroupOperateManager.getInstance().saveGroupDetailToLocal(emChatId, groupDetailInfo, json);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                    }
                });
    }

    public void setGroupData() {
        groupName = TextUtils.isEmpty(groupDetailInfo.groupNickName) ? groupDetailInfo.getGroupName() : groupDetailInfo.groupNickName;
        titleBar.setTitle(groupName + "(" + groupDetailInfo.getGroupUsers() + ")");
        //单个禁言
        /*List<GroupDetailInfo.GroupUserDetailVoListBean> groupUserBeans = groupDetailInfo.getGroupUserDetailVoList();
        String curUserId = UserComm.getUserInfo().getUserId();
        for (int i = 0; i < groupUserBeans.size(); i++) {
            GroupDetailInfo.GroupUserDetailVoListBean groupUserBean = groupUserBeans.get(i);
            if(curUserId.equals(groupUserBean.getUserId())){
                //遍历到当前用户，判断是否禁言
                if("1".equals(groupUserBean.getSayStatus())){
                    mInputMenu.getPrimaryMenu().executeMute();
                }
            }

        }*/
        //全员禁言
        if (groupDetailInfo.getGroupSayFlag().equals("1") && groupDetailInfo.getGroupUserRank() == 0) {
            mInputMenu.getPrimaryMenu().executeMute();
            mInputMenu.getChatExtendSmallMenu().setIscansend(false);
        }
        if ("1".equals(groupDetailInfo.getSayStatus())) {
            mInputMenu.getPrimaryMenu().executeMute();
            mInputMenu.getChatExtendSmallMenu().setIscansend(false);
        }

        mMessageList.refresh();
    }


    /**
     * register extend menu, item id need > 3 if you override this method and
     * keep exist item
     */
    protected void registerExtendMenuItem() {

    }


    protected void onConversationInit() {

        Observable.just("")
                .map(str -> {
                    conversation =
                            EMClient.getInstance().chatManager().getConversation(emChatId, EaseCommonUtils.getConversationType(chatType), true);
                    conversation.markAllMessagesAsRead();
                    // the number of messages loaded into conversation is getChatOptions
                    // ().getNumberOfMessagesLoaded
                    // you can change this number
                    Log.d("interval", "onNext====================================0");

                    if (!isRoaming()) {
                        final List<EMMessage> msgs = conversation.getAllMessages();
                        int msgCount = msgs != null ? msgs.size() : 0;
                        if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
                            String msgId = null;
                            if (msgs != null && msgs.size() > 0) {
                                msgId = msgs.get(0).getMsgId();
                            }
                            conversation.loadMoreMsgFromDB(msgId, pagesize - msgCount);
                            Log.d("interval", "onNext====================================1");
                        }
                        Log.d("interval", "onNext====================================11");
                    } else {
                        fetchQueue.execute(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    EMClient.getInstance().chatManager().fetchHistoryMessages(
                                            emChatId,
                                            EaseCommonUtils.getConversationType(chatType)
                                            , pagesize, "");
                                    final List<EMMessage> msgs =
                                            conversation.getAllMessages();
                                    int msgCount = msgs != null ? msgs.size() : 0;
                                    if (msgCount < conversation.getAllMsgCount() && msgCount < pagesize) {
                                        String msgId = null;
                                        if (msgs != null && msgs.size() > 0) {
                                            msgId = msgs.get(0).getMsgId();
                                        }
                                        conversation.loadMoreMsgFromDB(msgId,
                                                pagesize - msgCount);
                                        Log.d("interval", "onNext====================================2");
                                    }
                                    if (mMessageList != null) {
                                        mMessageList.refreshSelectLast();
                                    }
                                    Log.d("interval", "onNext====================================3");
                                } catch (HyphenateException e) {
                                    e.printStackTrace();
                                    Log.d("interval", "onNext====================================4");
                                }
                            }
                        });
                    }

                    return str;
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(autoDispose())
                .subscribe(new NextObserver<String>() {
                    @Override
                    public void onNext(@NonNull String o) {
                    }
                });
    }

    protected void onMessageListInit() {
        mMessageList.init(emChatId, chatType, chatFragmentHelper != null ?
                chatFragmentHelper.onSetCustomChatRowProvider() : null);
        setListItemClickListener();
        mMessageList.getListView().setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                hideKeyboard();
                if (mInputMenu != null) {
                    mInputMenu.hideExtendMenuContainer();
                }
                return false;
            }
        });
        isMessageListInited = true;
    }

    protected void setListItemClickListener() {
        mMessageList.setItemClickListener(new EaseChatMessageList.MessageListItemClickListener() {

            @Override
            public void onUserAvatarClick(String userId) {
                if (chatFragmentHelper != null) {
                    if ((chatType != EaseConstant.CHATTYPE_GROUP) || (groupDetailInfo != null &&
                            (groupDetailInfo.getGroupUserRank() != 0 || groupDetailInfo.getSeeFriendFlag() == 1)))
                        chatFragmentHelper.onAvatarClick(userId);
                    else {
                        ToastUtil.toast("群主未开启群成员互查信息功能");
                    }
                }
            }

            @Override
            public void onUserAvatarLongClick(String userId) {
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onAvatarLongClick(userId);
                }
            }

            @Override
            public void onBubbleLongClick(EMMessage message) {
                contextMenuMessage = message;
                if (chatFragmentHelper != null) {
                    chatFragmentHelper.onMessageBubbleLongClick(message);
                }
            }

            @Override
            public boolean onBubbleClick(EMMessage message) {
                if (chatFragmentHelper == null) {
                    return false;
                }
                return chatFragmentHelper.onMessageBubbleClick(message);
            }

        });
    }

    protected void setRefreshLayoutListener() {
        swipeRefreshLayout.setOnRefreshListener(() -> new Handler().postDelayed(() -> {
            if (!isRoaming()) {
                loadMoreLocalMessage();
            } else {
                loadMoreRoamingMessages();
            }
        }, 600));
    }

    private void loadMoreLocalMessage() {
        if (listView.getFirstVisiblePosition() == 0 && !isloading && haveMoreData) {
            List<EMMessage> messages;
            try {
                messages =
                        conversation.loadMoreMsgFromDB(conversation.getAllMessages().size() == 0 ? "" : conversation.getAllMessages().get(0).getMsgId(),
                                pagesize);
            } catch (Exception e1) {
                swipeRefreshLayout.setRefreshing(false);
                return;
            }
            if (messages.size() > 0) {
                mMessageList.refreshSeekTo(messages.size() - 1);
                if (messages.size() != pagesize) {
                    haveMoreData = false;
                }
            } else {
                haveMoreData = false;
            }

            isloading = false;
        } else {
            ToastUtil.toast(getResources().getString(R.string.no_more_messages));
        }
        swipeRefreshLayout.setRefreshing(false);
    }

    private void loadMoreRoamingMessages() {
        if (!haveMoreData) {
            ToastUtil.toast(getResources().getString(R.string.no_more_messages));
            swipeRefreshLayout.setRefreshing(false);
            return;
        }
        if (fetchQueue != null) {
            fetchQueue.execute(new Runnable() {
                @Override
                public void run() {
                    try {
                        List<EMMessage> messages =
                                conversation.getAllMessages();
                        EMClient.getInstance().chatManager().fetchHistoryMessages(
                                emChatId,
                                EaseCommonUtils.getConversationType(chatType)
                                , pagesize,
                                (messages != null && messages.size() > 0) ?
                                        messages.get(0).getMsgId() : "");
                    } catch (HyphenateException e) {
                        e.printStackTrace();
                    } finally {
                        Activity activity = getActivity();
                        if (activity != null) {
                            activity.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    loadMoreLocalMessage();
                                }
                            });
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            // capture new image
            if (requestCode == REQUEST_CODE_CAMERA) {
                if (cameraFile != null && cameraFile.exists()) {
                    sendImageMessage(cameraFile.getAbsolutePath());
                }
            } else if (requestCode == REQUEST_CODE_LOCAL) {// send local image
                if (data != null) {
                       /* Uri selectedImage = data.getData();
                        if (selectedImage != null) {
                            sendPicByUri(selectedImage);
                        }*/

                    List<LocalMedia> selectList =
                            PictureSelector.obtainMultipleResult(data);

                    if (selectList != null && selectList.size() > 0) {
                        for (int i = 0; i < selectList.size(); i++) {
                            LocalMedia localMedia = selectList.get(i);
                            String path;
                            if (localMedia.getCompressPath() != null) {
                                path = localMedia.getCompressPath();
                            } else {
                                path = localMedia.getPath();
                            }

                            Uri selectedImage = Uri.fromFile(new File(path));
                            Uri selectedImageCursor = getMediaUriFromPath(requireContext(), path);
                            //第一步先判断是否需要发送视频
                            if (selectedImageCursor != null) {
                                String fileExtension = MimeTypeMap.getFileExtensionFromUrl(selectedImage.toString());
                                String mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension);
                                if (mimeType != null && mimeType.contains("video")) {
                                    try {
                                        String[] filePathColumn = {MediaStore.Video.Media.DATA, MediaStore.Video.Media.DURATION};
                                        Cursor cursor = requireContext().getContentResolver().query(selectedImageCursor,
                                                null, null, null, null);
                                        if (null != cursor) {
                                            cursor.moveToFirst();
                                            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                                            int columnIndexDur = cursor.getColumnIndex(filePathColumn[1]);
                                            String videoPath = cursor.getString(columnIndex);
                                            int dur = cursor.getInt(columnIndexDur);
                                            cursor.close();
                                            File file =
                                                    new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());

                                            FileOutputStream fos = new FileOutputStream(file);
                                            Bitmap thumbBitmap =
                                                    ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                                            if (thumbBitmap != null) {
                                                thumbBitmap.compress(Bitmap.CompressFormat.JPEG, 85, fos);
                                            }
                                            fos.close();
                                            sendVideoMessage(videoPath,
                                                    file.getAbsolutePath(), dur);
                                        }
                                        return;
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            //第二步再执行发送图片
                            sendPicByUri(selectedImage);
                        }
                    }
                }
            } else if (requestCode == Constant.REQUEST_AT_MERBER_CODE) {
                EaseAtMessageHelper.get().addAtUser(data.getStringExtra(Constant.PARAM_AT_USERID));
                inputAtUsername(data.getStringExtra(Constant.PARAM_AT_USERID), false);
            }

        }
    }

    public Uri getMediaUriFromPath(Context context, String path) {
        Uri mediaUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
        Cursor cursor = context.getContentResolver().query(mediaUri,
                null,
                null,
                null,
                null);

        Uri uri;
        if (cursor.getCount() > 0) {
            for (int i = 0; i < cursor.getCount(); i++) {
                cursor.moveToPosition(i);
                int index = cursor.getColumnIndex(MediaStore.Video.Media.DATA);
                if (index != -1) {
                    String path1 = cursor.getString(index);
                    if (path1.equals(path)) {
                        uri = ContentUris.withAppendedId(mediaUri,
                                cursor.getLong(cursor.getColumnIndex(MediaStore.Video.Media._ID)));
                        return uri;
                    }
                }

            }
        } else {
            return null;
        }
        cursor.close();
        return null;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isMessageListInited) {
            mMessageList.refresh();
        }
        EaseUI.getInstance().pushActivity(getActivity());
        // register the event listener when enter the foreground
        EMClient.getInstance().chatManager().addMessageListener(this);
    }

    @Override
    public void onStop() {
        super.onStop();
        // unregister this event listener when this activity enters the
        // background
        EMClient.getInstance().chatManager().removeMessageListener(this);

        // remove activity from foreground activity list
        EaseUI.getInstance().popActivity(getActivity());
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(0);
        if (groupListener != null) {
            EMClient.getInstance().groupManager().removeGroupChangeListener(groupListener);
        }

        // TODO: 2021/4/1  xgp
        stopTimer();

    }

    public void onBackPressed() {
        if (mInputMenu.onBackPressed()) {
            requireActivity().finish();
            if (chatType == EaseConstant.CHATTYPE_GROUP) {
//                EaseAtMessageHelper.get().removeAtMeGroup(emChatId);
//                EaseAtMessageHelper.get().cleanToAtUserList();
            }
        }
    }

    public boolean isListViewReachBottomEdge(final ListView listView) {
        boolean result = false;
        if (listView.getLastVisiblePosition() == (listView.getCount() - 1)) {
            final View bottomChildView = listView.getChildAt(listView.getLastVisiblePosition() - listView.getFirstVisiblePosition());
            result = (listView.getHeight() >= bottomChildView.getBottom());
        }
        return result;
    }

    /**
     * implement methods in EMMessageListener
     *
     * @param messages
     */

    @Override
    public void onMessageReceived(List<EMMessage> messages) {


        if (canLoadMessage) {
            putToMessageList(messages);
        } else {
            if (unReadMessages == null) {
                unReadMessages = new ArrayList<>();
            }

            for (EMMessage message : messages) {
                String username = null;
                // group message
                if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                    username = message.getTo();
                } else {
                    // single chat message
                    username = message.getFrom();
                }

                // if the message is for current conversation
                if (username.equals(emChatId) || message.getTo().equals(emChatId) || message.conversationId().equals(emChatId)) {
//                EaseUI.getInstance().getNotifier().vibrateAndPlayTone(message);
                    unReadMessages.add(message);
                }
            }
            if (unReadMessages.size() > 0) {
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (tvUnread != null) {
                            tvUnread.setText(getString(R.string.str_unread_msg, unReadMessages.size()));
                            tvUnread.setVisibility(VISIBLE);
                        }
                    }
                });
            }

        }

        /*getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if (titleBar != null) {
                    titleBar.setUnreadMsgCount(getOtherUnreadMsgCountTotal());
                }
            }
        });*/

    }

    //获取其他会话未读消息数
    /*public int getOtherUnreadMsgCountTotal() {
//        return 0;
        if (conversation == null) {
            return 0;
        }
        int conversationUnread = conversation.getUnreadMsgCount();
        int allUnread = UnReadMsgCount.getUnreadMessageCount();
        int unReadNum = allUnread - ((conversation != null && conversationUnread > 0) ? conversationUnread : 0);
        return unReadNum;
    }*/

    public void putToMessageList(List<EMMessage> messages) {
        for (EMMessage message : messages) {
            String username = null;
            EaseCommonUtils.initMessage(message);
            // group message
            if (message.getChatType() == EMMessage.ChatType.GroupChat || message.getChatType() == EMMessage.ChatType.ChatRoom) {
                username = message.getTo();
            } else {
                // single chat message
                username = message.getFrom();
            }

            // if the message is for current conversation
            if (username.equals(emChatId) || message.getTo().equals(emChatId) || message.conversationId().equals(emChatId)) {
//                EaseUI.getInstance().getNotifier().vibrateAndPlayTone(message);
                conversation.markMessageAsRead(message.getMsgId());
                mMessageList.refreshSelectLast();
            } else {
                EaseUI.getInstance().getNotifier().onNewMsg(message);
            }
        }
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        for (EMMessage emMessage : messages) {
            if (emMessage.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RAB)) {
                String currentUser =
                        UserComm.getUserInfo().getUserId() + "";
                String loginName = emMessage.getStringAttribute("id", "");//id
                String sendLoginName = emMessage.getStringAttribute("sendid",
                        "");//发红包的id
                if (!loginName.equals(currentUser) && !sendLoginName.equals(currentUser)) {
                    continue;
                }
            }
//            if (emMessage.getStringAttribute(Constant.MSGTYPE, "").equals
//            (Constant.REDPACKET)) {
//                String currentUser = MyApplication.getInstance()
//                .getUserLoginInfo().getUserId() + Constant.ID_REDPROJECT;
//                if (currentUser.equals(emMessage.getFrom())) {
//                } else {
//                    continue;
//                }
//            }
            EaseCommonUtils.initMessage(emMessage);
            EMMessage msg =
                    EMMessage.createTxtSendMessage(emMessage.getStringAttribute("message", ""), emMessage.getTo());
            msg.setChatType(EMMessage.ChatType.GroupChat);
            msg.setFrom(emMessage.getFrom());
            msg.setTo(emMessage.getTo());
            msg.setMsgId(emMessage.getMsgId());
            msg.setMsgTime(emMessage.getMsgTime());
            if (emMessage.getFrom().equals(EMClient.getInstance().getCurrentUser())) {
                msg.setDirection(EMMessage.Direct.SEND);
            } else {
                msg.setDirection(EMMessage.Direct.RECEIVE);
            }
            msg.setUnread(false);
            msg.setAttribute("cmd", true);
            for (String key : emMessage.ext().keySet()) {
                if (emMessage.ext().get(key) instanceof Integer) {
                    try {
                        msg.setAttribute(key,
                                (Integer) emMessage.ext().get(key));
                    } catch (Exception e) {
                    }
                } else if (emMessage.ext().get(key) instanceof String) {
                    try {
                        msg.setAttribute(key,
                                emMessage.ext().get(key).toString());
                    } catch (Exception e) {
                    }
                } else if (emMessage.ext().get(key) instanceof Boolean) {
                    try {
                        msg.setAttribute(key,
                                emMessage.ext().get(key).toString());
                    } catch (Exception e) {
                    }
                } else if (emMessage.ext().get(key) instanceof Boolean) {
                    try {
                        msg.setAttribute(key,
                                (Boolean) emMessage.ext().get(key));
                    } catch (Exception e) {
                    }
                } else if (emMessage.ext().get(key) instanceof Long) {
                    try {
                        msg.setAttribute(key, (Long) emMessage.ext().get(key));
                    } catch (Exception e) {
                    }
                }
            }
            //保存消息
            EaseThreadManager.getInstance().runOnIOThread(() -> EMClient.getInstance().chatManager().saveMessage(msg));
            mMessageList.refreshSelectLast();
        }
    }

    @Override
    public void onMessageRead(List<EMMessage> messages) {
        if (isMessageListInited) {
            mMessageList.refresh();
        }
    }

    @Override
    public void onMessageDelivered(List<EMMessage> messages) {
        if (isMessageListInited) {
            mMessageList.refresh();
        }
    }

    @Override
    public void onMessageRecalled(List<EMMessage> messages) {
        if (isMessageListInited) {
            mMessageList.refresh();
        }
    }

    @Override
    public void onMessageChanged(EMMessage emMessage, Object change) {
        if (isMessageListInited) {
            mMessageList.refresh();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /**
     * handle the click event for extend menu
     */
    class MyItemClickListener implements EaseChatExtendMenu.EaseChatExtendMenuItemClickListener {

        @Override
        public void onClick(int itemId, View view) {
            if (chatFragmentHelper != null) {
                if (chatFragmentHelper.onExtendMenuItemClick(itemId, view)) {
                    return;
                }
            }
            switch (itemId) {
                case ITEM_TAKE_PICTURE:
                    selectPicFromCamera();
                    break;
                case ITEM_PICTURE:
                    selectPicFromLocal();
                    break;
                case ITEM_LOCATION:
                    //无法使用暂时注释掉
                    startActivityForResult(new Intent(getActivity(), SelAddrMapActivity.class), REQUEST_CODE_MAP);
                    break;
                default:
                    break;
            }
        }

    }

    /**
     * input @
     *
     * @param userId
     */
    protected void inputAtUsername(String userId, boolean autoAddAtSymbol) {
        if (EMClient.getInstance().getCurrentUser().equals(userId) ||
                chatType != EaseConstant.CHATTYPE_GROUP) {
            return;
        }
        EaseAtMessageHelper.get().addAtUser(userId);
        String username = "";
        //旧的注释
        /*if (UserOperateManager.getInstance().hasUserName(userId)) {
            username = UserOperateManager.getInstance().getUserName(userId);
        }*/
        //这里本地处理，显示好友的账号昵称，非备注名
        List<ContactListInfo.DataBean> localFriendList = UserOperateManager.getInstance().getContactList();
        for (int i = 0; i < localFriendList.size(); i++) {
            if (userId.equals(localFriendList.get(i).getFriendUserId() + ID_REDPROJECT)) {
                if (!TextUtils.isEmpty(localFriendList.get(i).getNickName())) {
                    username = localFriendList.get(i).getNickName();
                } else {
                    username = localFriendList.get(i).getFriendNickName();
                }
                break;
            }
        }

        //新增非好友昵称显示
        if (StringUtil.isEmpty(username)) {
            if (UserOperateManager.getInstance().hasUserName(userId)) {
                username = UserOperateManager.getInstance().getUserName(userId);
            }
        }

        if (autoAddAtSymbol) {
            mInputMenu.insertText("@" + username + " ");
        } else {
            mInputMenu.insertText(username + " ");
        }
    }


    /**
     * input @
     */
    protected void inputAtUsername(String userId) {
        inputAtUsername(userId, true);
    }


    //send message
    protected void sendTextMessage(String content) {
        if (EaseAtMessageHelper.get().containsAtUsername(content)) {
            sendAtMessage(content);
        } else {
            EMMessage message = EMMessage.createTxtSendMessage(content,
                    emChatId);
            sendMessage(message);
        }
    }

    /**
     * send @ message, only support group chat message  （@人目前只支持群聊）18715083418.
     *
     * @param content
     */
    private void sendAtMessage(String content) {
        if (chatType != EaseConstant.CHATTYPE_GROUP) {
            return;
        }
        EMMessage message = EMMessage.createTxtSendMessage(content,
                emChatId);
        EMGroup group =
                EMClient.getInstance().groupManager().getGroup(emChatId);
        if (EMClient.getInstance().getCurrentUser().equals(group.getOwner()) && EaseAtMessageHelper.get().containsAtAll(content)) {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG,
                    EaseConstant.MESSAGE_ATTR_VALUE_AT_MSG_ALL);
        } else {
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, EaseAtMessageHelper.get().atListToJsonArray(EaseAtMessageHelper.get().getAtMessageUsernames(content)));
        }
        sendMessage(message);

    }


    protected void sendBigExpressionMessage(String name, String identityCode) {
        EMMessage message =
                EaseCommonUtils.createExpressionMessage(emChatId, name,
                        identityCode);
        sendMessage(message);
    }

    protected void sendVoiceMessage(String filePath, int length) {
        EMMessage message = EMMessage.createVoiceSendMessage(filePath, length
                , emChatId);
        sendMessage(message);
    }

    protected void sendImageMessage(String imagePath) {
        EMMessage message = EMMessage.createImageSendMessage(imagePath, true
                , emChatId);
        sendMessage(message);
    }

    protected void sendLocationMessage(double latitude, double longitude,
                                       String locationAddress) {
        EMMessage message = EMMessage.createLocationSendMessage(latitude,
                longitude, locationAddress, emChatId);
        sendMessage(message);
    }

    protected void sendVideoMessage(String videoPath, String thumbPath,
                                    int videoLength) {
        EMMessage message = EMMessage.createVideoSendMessage(videoPath,
                thumbPath, videoLength, emChatId);
        sendMessage(message);
    }

    protected void sendFileMessage(String filePath) {
        EMMessage message = EMMessage.createFileSendMessage(filePath,
                emChatId);
        sendMessage(message);
    }

    public void sendMessage(EMMessage message) {
        if (message == null) {
            return;
        }
        if (chatFragmentHelper != null) {
            //set extension
            chatFragmentHelper.onSetMessageAttributes(message);
        }
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            message.setChatType(EMMessage.ChatType.GroupChat);
            if (groupDetailInfo != null) {
                message.setAttribute("groupName", groupDetailInfo.getGroupName());
                message.setAttribute("groupHead", groupDetailInfo.getGroupHead());
            }
            message.setAttribute("groupId", groupId);
            message.setAttribute(Constant.GROUP_NICKNAME, groupName);

        }
        if (EaseAtMessageHelper.get().getAtList().size() > 0) {
            JSONArray atJson = new JSONArray(EaseAtMessageHelper.get().getAtList());
            message.setAttribute(EaseConstant.MESSAGE_ATTR_AT_MSG, atJson);
            EaseAtMessageHelper.get().cleanToAtUserList();
        }

        message.setAttribute(Constant.AVATARURL,
                UserComm.getUserInfo().getUserHead());
        message.setAttribute(Constant.NICKNAME,
                UserComm.getUserInfo().getNickName());

        message.setAttribute(Constant.USERVIPLEVEL,
                (int) NumberUtils.parseDouble(UserComm.getUserInfo().getVipLevel()));

        if (groupDetailInfo != null && !TextUtils.isEmpty(groupDetailInfo.getGroupUserNickName())) {
            message.setAttribute(Constant.NICKNAME, groupDetailInfo.getGroupUserNickName());
        }

        message.setAttribute(Constant.FRIEND_NICKNAME,
                UserOperateManager.getInstance().getUserName(emChatId));
        message.setAttribute(Constant.FRIEND_AVATARURL,
                UserOperateManager.getInstance().getUserAvatar(emChatId));
        message.setAttribute(Constant.FRIEND_USERID,
                ProjectUtil.transformId(emChatId));


        //Add to conversation
        EMClient.getInstance().chatManager().sendMessage(message);
//        EaseThreadManager.getInstance().runOnIOThread(() -> EMClient.getInstance().chatManager().saveMessage(message);
        //refresh ui
        if (isMessageListInited) {
            mMessageList.refreshSelectLast();
        }
        //保存发送时间
        Preference.saveLongPreferences(getActivity(), emChatId,
                System.currentTimeMillis() / 1000);
    }


    /**
     * send  拓展消息message （名片）
     */
    protected void sendExTextMessage(String content, String nickName,
                                     String userName, String avatar,
                                     String friendUserId) {
        if (EaseAtMessageHelper.get().containsAtUsername(content)) {
            sendAtMessage(content);
        } else {
            EMMessage message = EMMessage.createTxtSendMessage(content,
                    emChatId);
            if (message == null) {
                return;
            }
            if (chatFragmentHelper != null) {
                //set extension
                chatFragmentHelper.onSetMessageAttributes(message);
            }
            if (chatType == EaseConstant.CHATTYPE_GROUP) {
                message.setChatType(EMMessage.ChatType.GroupChat);
                if (groupDetailInfo != null) {
                    message.setAttribute("groupName",
                            groupDetailInfo.getGroupName());
                    message.setAttribute("groupHead",
                            groupDetailInfo.getGroupHead());
                }
                message.setAttribute("groupId", groupId);
                message.setAttribute(Constant.GROUP_NICKNAME, groupName);
            }

            message.setAttribute(Constant.SEND_CARD, true);
            message.setAttribute("otherName", nickName);
            message.setAttribute("otherUserCode", userName);
            message.setAttribute("otherImg", avatar);
            message.setAttribute("otherUserId", friendUserId);
            message.setAttribute(Constant.AVATARURL,
                    UserComm.getUserInfo().getUserHead());
            message.setAttribute(Constant.NICKNAME,
                    UserComm.getUserInfo().getNickName());
            message.setAttribute(Constant.FRIEND_NICKNAME,
                    UserOperateManager.getInstance().getUserName(emChatId));
            message.setAttribute(Constant.FRIEND_AVATARURL,
                    UserOperateManager.getInstance().getUserAvatar(emChatId));
            message.setAttribute(Constant.FRIEND_USERID,
                    ProjectUtil.transformId(emChatId));

            //Add to conversation
            EMClient.getInstance().chatManager().sendMessage(message);
            //refresh ui
            if (isMessageListInited) {
                mMessageList.refreshSelectLast();
            }
        }
    }


    /**
     * 发送自定义地图信息
     *
     * @param latitude        纬度
     * @param longitude       经度
     * @param locationAddress 位置
     * @param localDetail     详细位置
     * @param path            地图截图图片地址
     */
    protected void sendCustomLocationMessage(double latitude,
                                             double longitude,
                                             String locationAddress,
                                             String localDetail, String path) {
//        EMMessage message = EMMessage.createTxtSendMessage("" +
//                "位置", emChatId);

        EMMessage message = EMMessage.createLocationSendMessage(latitude,
                longitude, locationAddress, emChatId);

        message.setAttribute(Constant.LATITUDE, String.valueOf(latitude));
        message.setAttribute(Constant.LONGITUDE, String.valueOf(longitude));
        message.setAttribute(Constant.LOCATION_ADDRESS, locationAddress);
        message.setAttribute(Constant.LOCAL_DETAIL, localDetail);
        message.setAttribute(Constant.PATH, path);
        sendMessage(message);

    }


    //===================================================================================


    /**
     * send image
     *
     * @param selectedImage
     */
    protected void sendPicByUri(Uri selectedImage) {
        String[] filePathColumn = {MediaStore.Images.Media.DATA};
        Cursor cursor =
                getActivity().getContentResolver().query(selectedImage,
                        filePathColumn, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();
            cursor = null;

            if (picturePath == null || "null".equals(picturePath)) {
                Toast toast = Toast.makeText(getActivity(),
                        R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;
            }
            sendImageMessage(picturePath);
        } else {
            File file = new File(selectedImage.getPath());
            if (!file.exists()) {
                Toast toast = Toast.makeText(getActivity(),
                        R.string.cant_find_pictures, Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                return;

            }
            sendImageMessage(file.getAbsolutePath());
        }

    }

    /**
     * send file
     *
     * @param uri
     */
    protected void sendFileByUri(Uri uri) {
        String filePath = null;
        if ("content".equalsIgnoreCase(uri.getScheme())) {
            String[] filePathColumn = {MediaStore.Images.Media.DATA};
            Cursor cursor = null;
            try {
                cursor = getActivity().getContentResolver().query(uri,
                        filePathColumn, null, null, null);
                int column_index = cursor.getColumnIndexOrThrow("_data");
                if (cursor.moveToFirst()) {
                    filePath = cursor.getString(column_index);
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            filePath = uri.getPath();
        }
        if (filePath == null) {
            return;
        }
        File file = new File(filePath);
        if (!file.exists()) {
            Toast.makeText(getActivity(), R.string.File_does_not_exist,
                    Toast.LENGTH_SHORT).show();
            return;
        }
        sendFileMessage(filePath);
    }

    /**
     * capture new image
     */
    protected void selectPicFromCamera() {
        if (!EaseCommonUtils.isSdcardExist()) {
            Toast.makeText(getActivity(), R.string.sd_card_does_not_exist,
                    Toast.LENGTH_SHORT).show();
            return;
        }

        cameraFile = new File(PathUtil.getInstance().getImagePath(),
                EMClient.getInstance().getCurrentUser()
                        + System.currentTimeMillis() + ".jpg");
        //noinspection ResultOfMethodCallIgnored
        cameraFile.getParentFile().mkdirs();

        startActivityForResult(
                new Intent(MediaStore.ACTION_IMAGE_CAPTURE).putExtra(MediaStore.EXTRA_OUTPUT, UriUtil.getUri(getActivity(), cameraFile)),
                REQUEST_CODE_CAMERA);
    }

    /**
     * select local image
     */
    protected void selectPicFromLocal() {

       /* Matisse.from(getDialogFragment())
                .choose(MimeType.ofImage())
                .countable(true)
                .maxSelectable(1)
                //选择照片时，是否显示拍照
                .capture(true)
                .captureStrategy(new CaptureStrategy(true, "com.klzz.vipthink.pad.provider"))
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)
                .thumbnailScale(0.85f)
                .imageEngine(new MyGlideEngine())
                .forResult(REQUEST_CODE);*/

        PictureSelector.create(getActivity())
                .openGallery(PictureMimeType.ofAll())
                .selectionMode(PictureConfig.MULTIPLE)
                .maxSelectNum(3)
                .withAspectRatio(1, 1)
                .enableCrop(false)
                .showCropFrame(false)
                .showCropGrid(false)
                .freeStyleCropEnabled(false)
                .circleDimmedLayer(false)
                .forResult(REQUEST_CODE_LOCAL);

        /*Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);*/
    }


    /**
     * clear single the conversation history
     */
    protected void emptyHistory() {
        if (conversation.getAllMsgCount() == 0) {
            ToastUtil.toast("消息已清空");
            return;
        }
        if (conversation != null) {
            conversation.clearAllMessages();
        }
        mMessageList.refresh();
        haveMoreData = true;
    }


    /**
     * open group detail
     */
    protected void toGroupDetails() {
        if (chatType == EaseConstant.CHATTYPE_GROUP) {
            EMGroup group =
                    EMClient.getInstance().groupManager().getGroup(emChatId);
            if (group == null) {
                Toast.makeText(getActivity(), R.string.gorup_not_found,
                        Toast.LENGTH_SHORT).show();
                return;
            }
            if (chatFragmentHelper != null) {
                chatFragmentHelper.onEnterToChatDetails();
            }
        }
    }

    /**
     * hide
     */
    protected void hideKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null) {
                inputManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }
    }

    /**
     * forward message (转发消息)
     *
     * @param forward_msg_id
     */
    protected void forwardMessage(String forward_msg_id) {
        final EMMessage forward_msg =
                EMClient.getInstance().chatManager().getMessage(forward_msg_id);
        EMMessage.Type type = forward_msg.getType();
        switch (type) {
            case TXT:
                if (forward_msg.getBooleanAttribute(EaseConstant.MESSAGE_ATTR_IS_BIG_EXPRESSION, false)) {
                    sendBigExpressionMessage(((EMTextMessageBody) forward_msg.getBody()).getMessage(),
                            forward_msg.getStringAttribute(EaseConstant.MESSAGE_ATTR_EXPRESSION_ID, null));
                } else {
                    // get the content and send it
                    String content =
                            ((EMTextMessageBody) forward_msg.getBody()).getMessage();
                    sendTextMessage(content);
                }
                break;
            case IMAGE:
                // send image
                String filePath =
                        ((EMImageMessageBody) forward_msg.getBody()).getLocalUrl();
                if (filePath != null) {
                    File file = new File(filePath);
                    if (!file.exists()) {
                        // send thumb nail if original image does not exist
                        filePath =
                                ((EMImageMessageBody) forward_msg.getBody()).thumbnailLocalPath();
                    }
                    sendImageMessage(filePath);
                }
                break;
            default:
                break;
        }

        if (forward_msg.getChatType() == EMMessage.ChatType.ChatRoom) {
            EMClient.getInstance().chatroomManager().leaveChatRoom(forward_msg.getTo());
        }
    }

    /**
     * listen the group event
     */
    class GroupListener extends EaseGroupListener {

        @Override
        public void onUserRemoved(final String groupId, String groupName) {
            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        if (emChatId.equals(groupId)) {
                            //toast("您离开了了房间");
                            Activity activity = getActivity();
                            if (activity != null && !activity.isFinishing()) {
                                activity.finish();
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void onGroupDestroyed(final String groupId, String groupName) {
            // prompt group is dismissed and finish this activity
            Activity activity = getActivity();
            if (activity != null) {
                activity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (emChatId.equals(groupId)) {
                            //toast("房间已被群主解散");
                            Activity activity = getActivity();
                            if (activity != null && !activity.isFinishing()) {
                                activity.finish();
                            }
                        }
                    }
                });
            }
        }

        @Override
        public void onAutoAcceptInvitationFromGroup(String groupId,
                                                    String inviter,
                                                    String inviteMessage) {
            super.onAutoAcceptInvitationFromGroup(groupId, inviter,
                    inviteMessage);
            if (groupId.equals(emChatId)) {
                mHandler.sendEmptyMessageAtTime(0, 100);
            }
        }

        @Override
        public void onMemberJoined(final String groupId, final String member) {
            super.onMemberJoined(groupId, member);

        }

        @Override
        public void onMemberExited(final String groupId, final String member) {
            super.onMemberExited(groupId, member);
        }

        @Override
        public void onMuteListAdded(String groupId, List<String> mutes,
                                    long muteExpire) {
            super.onMuteListAdded(groupId, mutes, muteExpire);
            for (int i = 0; i < mutes.size(); i++) {
                if (mutes.get(i).equals(UserComm.getUserInfo().getUserId() + ID_REDPROJECT)
                        && groupId.equals(emChatId)) {
                    mInputMenu.getPrimaryMenu().executeMute();
                    mInputMenu.getChatExtendSmallMenu().setIscansend(false);
                }
            }
        }

        @Override
        public void onMuteListRemoved(String groupId, List<String> mutes) {
            for (int i = 0; i < mutes.size(); i++) {
                if (mutes.get(i).equals(UserComm.getUserInfo().getUserId() + ID_REDPROJECT)
                        && groupId.equals(emChatId)) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mInputMenu.getPrimaryMenu().relieveMute();
                            mInputMenu.getChatExtendSmallMenu().setIscansend(false);
                        }
                    });
                }
            }

        }
    }

    private static class MyHandler extends Handler {
        private final WeakReference<BaseChatFragment> weakReference;

        public MyHandler(BaseChatFragment fragment) {
            weakReference = new WeakReference<>(fragment);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if (weakReference.get() == null) return;
            BaseChatFragment fragment = weakReference.get();
            EMGroup group =
                    EMClient.getInstance().groupManager().getGroup(fragment.emChatId);
            if (group != null) {
                fragment.onConversationInit();
                fragment.onMessageListInit();
            }
        }
    }

    Handler mHandler = new MyHandler(this);


    protected EaseChatFragmentHelper chatFragmentHelper;

    public void setChatFragmentHelper(EaseChatFragmentHelper chatFragmentHelper) {
        this.chatFragmentHelper = chatFragmentHelper;
    }

    public interface EaseChatFragmentHelper {
        /**
         * set message attribute
         */
        void onSetMessageAttributes(EMMessage message);

        /**
         * enter to chat detail
         */
        void onEnterToChatDetails();

        /**
         * on avatar clicked
         *
         * @param userId
         */
        void onAvatarClick(String userId);

        /**
         * on avatar long pressed
         *
         * @param userId
         */
        void onAvatarLongClick(String userId);

        /**
         * on message bubble clicked
         */
        boolean onMessageBubbleClick(EMMessage message);

        /**
         * on message bubble long pressed
         */
        void onMessageBubbleLongClick(EMMessage message);

        /**
         * on extend menu item clicked, return true if you want to override
         *
         * @param view
         * @param itemId
         * @return
         */
        boolean onExtendMenuItemClick(int itemId, View view);

        /**
         * on set custom chat row provider
         *
         * @return
         */
        EaseCustomChatRowProvider onSetCustomChatRowProvider();
    }

    //===============================私发红包环信不推送红包消息的临时解决方案==========================================//
    private Disposable mDisposable;
    private int mCount;

    private void startTimer() {
        stopTimer();
        mCount = 0;
        Observable.interval(0, 2, TimeUnit.SECONDS)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(autoDispose())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(Long aLong) {
                        if (mCount < 5) {
                            if (isMessageListInited && mMessageList != null) {
                                mMessageList.refresh();
                                Log.d("interval", "onNext====================================refresh");
                            }
                            Log.d("interval", "onNext====================================" + mCount);
                        } else {
                            stopTimer();
                        }
                        mCount++;
                        Log.d("interval", "onNext====================================");
                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    private void stopTimer() {
        if (mDisposable != null) {
            mDisposable.dispose();
            mDisposable = null;
        }

    }

}
