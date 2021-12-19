package com.ycf.qianzhihe.section.conversation;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.FrameLayout;

import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.blankj.utilcode.util.GsonUtils;
import com.hyphenate.EMConnectionListener;
import com.hyphenate.EMError;
import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMConversation;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.old_data.ContactListInfo;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_data.MyGroupInfoList;
import com.ycf.qianzhihe.app.api.old_data.ToTopMap;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitFragment;
import com.ycf.qianzhihe.app.domain.EaseUser;
import com.ycf.qianzhihe.app.operate.GroupOperateManager;
import com.ycf.qianzhihe.app.operate.UserOperateManager;
import com.ycf.qianzhihe.common.db.DemoDbHelper;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.global.BaseConstant;
import com.zds.base.json.FastJsonUtil;
import com.ycf.qianzhihe.app.weight.ease.EaseConversationList;
import com.zds.base.util.Preference;
import com.zds.base.util.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * conversation list fragment
 *
 * @author Administrator
 */
public class BaseConversationListFragment extends BaseInitFragment {
    private final static int MSG_REFRESH = 2;
    protected boolean hidden;
    protected List<EMConversation> conversationList = new ArrayList<EMConversation>();
    protected EaseConversationList conversationListView;

    protected boolean isConflict;

    private InputMethodManager inputMethodManager;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        if (savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false)) {
            return;
        }
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.ease_fragment_conversation_list;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        conversationListView = (EaseConversationList) getView().findViewById(R.id.list);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                conversationList.clear();
                conversationList.addAll(loadConversationList());
                conversationListView.init(conversationList);
            }
        }, 1000);

        if (listItemClickListener != null) {
            conversationListView.setOnItemClickListener(new OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    EMConversation conversation = conversationListView.getItem(position);
                    listItemClickListener.onListItemClicked(conversation);
                }
            });
        }

        EMClient.getInstance().addConnectionListener(connectionListener);

    }

    @Override
    protected void initListener() {
        super.initListener();
    }

    protected EMConnectionListener connectionListener = new EMConnectionListener() {

        @Override
        public void onDisconnected(int error) {
            if (error == EMError.USER_REMOVED || error == EMError.USER_LOGIN_ANOTHER_DEVICE || error == EMError.SERVER_SERVICE_RESTRICTED
                    || error == EMError.USER_KICKED_BY_CHANGE_PASSWORD || error == EMError.USER_KICKED_BY_OTHER_DEVICE) {
                isConflict = true;
            } else {
                handler.sendEmptyMessage(0);
            }
        }

        @Override
        public void onConnected() {
            handler.sendEmptyMessage(1);
        }
    };
    private EaseConversationListItemClickListener listItemClickListener;

    protected Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 0:
                    onConnectionDisconnected();
                    break;
                case 1:
                    onConnectionConnected();
                    break;

                case MSG_REFRESH: {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            conversationList.clear();
                            conversationList.addAll(loadConversationList());
                            conversationListView.refresh();
                            EventBus.getDefault().post(new EventCenter<>(EventUtil.UNREADCOUNT));
                        }
                    }, 1000);

                    break;
                }
                default:
                    break;
            }
        }
    };

    /**
     * connected to server
     */
    protected void onConnectionConnected() {
    }

    /**
     * disconnected with server
     */
    protected void onConnectionDisconnected() {

    }


    /**
     * refresh ui
     */
    public void refresh() {
        if (!handler.hasMessages(MSG_REFRESH)) {
            handler.sendEmptyMessage(MSG_REFRESH);
        }

        refreshApplyLayout();
    }

    public void refreshApplyLayout() {
        //刷新本地群组和好友信息
        groupList();
        getContactList();
    }

    /**
     * load conversation list
     *
     * @return +
     */
    protected List<EMConversation> loadConversationList() {
        // get all conversations
        Map<String, EMConversation> conversations = EMClient.getInstance().chatManager().getAllConversations();

        //添加置顶消息
        List<Pair<Long, EMConversation>> topList = new ArrayList<Pair<Long, EMConversation>>();
        synchronized (conversations) {
            List<Pair<Long, EMConversation>> duTopList = new ArrayList<Pair<Long, EMConversation>>();

            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0 && conversation.getExtField().equals("toTop")) {
                    duTopList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                }
            }

            ToTopMap topMap = ToTopMap.give(getContext());
            if (topMap != null) {
                for (String key : topMap.getList()) {
                    for (Pair<Long, EMConversation> pair : duTopList) {
                        if (key.equals(pair.second.conversationId())) {
                            topList.add(pair);
                            break;
                        }
                    }
                }
            } else {
                topList.addAll(duTopList);
            }


        }


        List<Pair<Long, EMConversation>> sortList = new ArrayList<Pair<Long, EMConversation>>();
        /**
         * lastMsgTime will change if there is new message during sorting
         * so use synchronized to make sure timestamp of last message won't change.
         */
        synchronized (conversations) {

            List<String> localUsers = getLocalUsers();
            for (EMConversation conversation : conversations.values()) {
                if (conversation.getAllMessages().size() != 0 && !conversation.conversationId().equals(Constant.ADMIN) && !conversation.getExtField().equals("toTop")) {
                    if (!"系统管理员".equals(conversation.getLastMessage().getFrom()) && !"em_system".equals(conversation.getLastMessage().getFrom())) {

                        /*String json = FastJsonUtil.toJSONString(conversation.getLastMessage().ext());
                        String msgType = FastJsonUtil.getString(json, "msgType");

                        if (conversation.isGroup() || (localUsers != null && localUsers.contains(conversation.conversationId()))
                                || conversation.conversationId().contains("d816636e130411ecab930c42a1a8807a")
                                || conversation.conversationId().contains("0d777a9c8f9311eb844f00163e0654c2")
                                || conversation.conversationId().contains("d816636e130411ecab930c42a1a8807a")
                                || conversation.conversationId().contains("6a1bec8f64fe11eba89700163e0654c2")
                                || !TextUtils.isEmpty(msgType) && "systematic".equals(msgType)
                                || !TextUtils.isEmpty(msgType) && "walletMsg".equals(msgType)
                        ) {*/
                            sortList.add(new Pair<Long, EMConversation>(conversation.getLastMessage().getMsgTime(), conversation));
                        /*}*/
                    }
                }
            }

            /*if (localUsers == null) {
                getContactList();
            }*/

        }
        try {
            // Internal is TimSort algorithm, has bug
            sortConversationByLastChatTime(sortList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        List<EMConversation> list = new ArrayList<EMConversation>();

        //添加置顶消息
        for (Pair<Long, EMConversation> topItem : topList) {
            list.add(topItem.second);
        }

        for (Pair<Long, EMConversation> sortItem : sortList) {
            list.add(sortItem.second);
        }

        Iterator it = list.iterator();
        while (it.hasNext()) {
            EMConversation c = (EMConversation) it.next();
            String json = FastJsonUtil.toJSONString(c.getLastMessage().ext());

            if (json.contains("msgType") && "deleteuser".equals(FastJsonUtil.getString(json, "msgType"))) {
                it.remove();
            }
        }
        return list;
    }

    List<String> localUsers = null;

    private List<String> getLocalUsers() {
        //获取本地的好友列表
        if (DemoDbHelper.getInstance(mContext).getUserDao() != null) {
            localUsers = DemoDbHelper.getInstance(mContext).getUserDao().loadAllUsers();
        }


        return localUsers;
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
                        if (info != null && info.getData() != null && info.getData().size() > 0) {
                            UserOperateManager.getInstance().saveContactListToLocal(info, json);

                            localUsers = new ArrayList<>();
                            for (ContactListInfo.DataBean dataBean : info.getData()) {
                                localUsers.add(dataBean.getFriendUserId() + Constant.ID_REDPROJECT);
                            }

                            conversationList.clear();
                            conversationList.addAll(loadConversationList());
                            conversationListView.init(conversationList);
                            conversationListView.refresh();
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });


        //// sorting

    }

    /**
     * sort conversations according time stamp of last message
     *
     * @param conversationList
     */
    private void sortConversationByLastChatTime(List<Pair<Long, EMConversation>> conversationList) {
        Collections.sort(conversationList, new Comparator<Pair<Long, EMConversation>>() {
            @Override
            public int compare(final Pair<Long, EMConversation> con1, final Pair<Long, EMConversation> con2) {

                if (con1.first.equals(con2.first)) {
                    return 0;
                } else if (con2.first.longValue() > con1.first.longValue()) {
                    return 1;
                } else {
                    return -1;
                }
            }

        });
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden && !isConflict) {
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

    @Override
    public void onDestroy() {
        super.onDestroy();
        EMClient.getInstance().removeConnectionListener(connectionListener);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (isConflict) {
            outState.putBoolean("isConflict", true);
        }
    }


    public interface EaseConversationListItemClickListener {
        /**
         * click event for conversation list
         *
         * @param conversation -- clicked item
         */
        void onListItemClicked(EMConversation conversation);
    }

    /**
     * set conversation list item click listener
     *
     * @param listItemClickListener
     */
    public void setConversationListItemClickListener(EaseConversationListItemClickListener listItemClickListener) {
        this.listItemClickListener = listItemClickListener;
    }

    /**
     * 我的群组列表
     *
     * @param
     */
    private void groupList() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("pageSize", 1500);
        map.put("pageNum", 1);
        Log.d("群组刷新", "群组刷新");
        ApiClient.requestNetHandle(requireContext(), AppConfig.MY_GROUP_LIST, "", map,
                new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        MyGroupInfoList myGroupInfo = FastJsonUtil.getObject(json,
                                MyGroupInfoList.class);
                        if (myGroupInfo.getData() != null && myGroupInfo.getData().size() > 0) {
                            GroupOperateManager.getInstance().saveGroupsInfo(myGroupInfo, json);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {

                    }
                });
    }

}
