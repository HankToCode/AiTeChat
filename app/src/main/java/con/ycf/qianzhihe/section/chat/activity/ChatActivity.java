package con.ycf.qianzhihe.section.chat.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import com.hyphenate.chat.EMMessage;
import con.ycf.qianzhihe.MainActivity;
import con.ycf.qianzhihe.R;
import con.ycf.qianzhihe.app.api.Constant;
import con.ycf.qianzhihe.app.api.EaseConstant;
import con.ycf.qianzhihe.app.api.global.UserComm;
import con.ycf.qianzhihe.app.api.old_data.EventCenter;
import con.ycf.qianzhihe.app.base.BaseInitActivity;
import con.ycf.qianzhihe.common.permission.PermissionsManager;
import con.ycf.qianzhihe.section.chat.fragment.BaseChatFragment;
import con.ycf.qianzhihe.section.chat.fragment.ChatFragment;
import com.hyphenate.util.EasyUtils;

/**
 * 作   者：赵大帅
 * 描   述: 聊天
 * 日   期: 2017/11/17 18:07
 * 更新日期: 2017/11/17
 */
public class ChatActivity extends BaseInitActivity {
    public static ChatActivity activityInstance;
    private BaseChatFragment chatFragment;
    String toChatUsername;

    public static void actionStart(Context context, String conversationId, int chatType) {
        Intent intent = new Intent(context, ChatActivity.class);
        intent.putExtra(EaseConstant.EXTRA_USER_ID, conversationId);
        intent.putExtra(EaseConstant.EXTRA_CHAT_TYPE, chatType);
        context.startActivity(intent);
    }


    @Override
    protected int getLayoutId() {
        return R.layout.activity_chat;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        activityInstance = this;
        //use EaseChatFratFragment
        chatFragment = new ChatFragment();
        //pass parameters to chat fragment
        chatFragment.setArguments(getIntent().getExtras());
        getSupportFragmentManager().beginTransaction().add(R.id.container,
                chatFragment).commit();
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
    }


    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {
        switch (center.getEventCode()) {
            case 404:
                EMMessage message1 =
                        EMMessage.createTxtSendMessage("群公告：\n" + (String) center.getData(),
                                toChatUsername);
                message1.setAttribute(Constant.AVATARURL,
                        UserComm.getUserInfo().getUserHead());
                message1.setAttribute(Constant.NICKNAME,
                        UserComm.getUserInfo().getNickName());
                if (chatFragment != null) {
                    chatFragment.sendMessage(message1);
                }
                break;
        }
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        toChatUsername = intent.getStringExtra("userId");

    }

    @Override
    protected void onDestroy() {
        try {
            int chatType =
                    getIntent().getExtras().getInt(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
            if (chatType == EaseConstant.CHATTYPE_GROUP) {

//            MyApplication.getInstance().layoutRoom(emChatId);
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
        super.onDestroy();
        activityInstance = null;


    }


    @Override
    protected void onNewIntent(Intent intent) {
        // make sure only one chat activity is opened
        String username = intent.getStringExtra("userId");
        if (toChatUsername.equals(username)) {
            super.onNewIntent(intent);
        } else {
            finish();
            startActivity(intent);
        }

    }

    @Override
    public void onBackPressed() {
        chatFragment.onBackPressed();
        if (EasyUtils.isSingleActivity(this)) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
        }
    }

    public String getToChatUsername() {
        return toChatUsername;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        PermissionsManager.getInstance().notifyPermissionsChange(permissions,
                grantResults);
    }


}
