package com.ycf.qianzhihe.app.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.hyphenate.easecallkit.ui.EaseVideoCallActivity;
import com.ycf.qianzhihe.app.utils.my.MyHelper;
import com.hyphenate.util.EMLog;

/**
 * @author lhb
 */
public class CallReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (!MyHelper.getInstance().isLoggedIn()) {
            return;
        }
        //username
        String from = intent.getStringExtra("from");
        //call typePACKAGE_REMOVEDPACKAGE_REMOVED
        String type = intent.getStringExtra("type");
        if ("video".equals(type)) {
            //video call
            context.startActivity(new Intent(context, EaseVideoCallActivity.class).
                    putExtra("username", from).putExtra("isComingCall", true).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        } else {
            //voice call
            context.startActivity(new Intent(context, EaseVideoCallActivity.class).
                    putExtra("username", from).putExtra("isComingCall", true).
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
        }

        EMLog.d("CallReceiver11", "app received a incoming call");

    }

}