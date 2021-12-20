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


import static com.android.nanguo.section.chat.fragment.ChatFragment.ITEM_MENU_COLLECT;
import static com.android.nanguo.section.chat.fragment.ChatFragment.ITEM_MENU_DEL;
import static com.android.nanguo.section.chat.fragment.ChatFragment.ITEM_MENU_RECALL;
import static com.android.nanguo.section.chat.fragment.ChatFragment.ITEM_MENU_SAVE_IMAGE;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.android.nanguo.R;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.app.utils.my.Storage;

/**
 * @author lhb
 * 聊天收藏
 */
public class ChatCollectActivity extends BaseInitActivity {
    EMMessage message;
    TextView tvRecall;
    int type;

    @Override
    protected int getLayoutId() {

        message = getIntent().getParcelableExtra("message");

        type = message.getType().ordinal();
        int layoutId = 0;
        if (type == EMMessage.Type.TXT.ordinal()) {
            layoutId = R.layout.em_context_menu_for_collect;

        } else if (type == EMMessage.Type.IMAGE.ordinal()) {
            layoutId = R.layout.em_context_menu_for_collect;
        }
        return layoutId;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        if (type == EMMessage.Type.IMAGE.ordinal()) {
            TextView tv_save = findViewById(R.id.tv_save);
            tv_save.setVisibility(View.VISIBLE);
        }

        if (type == EMMessage.Type.TXT.ordinal()) {
            TextView tv_copy = findViewById(R.id.tv_copy);
            tv_copy.setVisibility(View.VISIBLE);
        }

        tvRecall = findViewById(R.id.tv_recall);

        if (message.direct() == EMMessage.Direct.RECEIVE) {
            tvRecall.setVisibility(View.GONE);
            tvRecall.setTextColor(getResources().getColor(R.color.gray));
        } else if (System.currentTimeMillis() - message.getMsgTime() > 120000) {
            tvRecall.setTextColor(getResources().getColor(R.color.gray));
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        finish();
        return true;
    }

    public void collect(View view) {
        Intent intent = new Intent();
        intent.putExtra("MenuFlag", ITEM_MENU_COLLECT);
        setResult(Activity.RESULT_OK, intent);
        Storage.saveImage("0");
        finish();
    }

    public void saveImage(View view) {
        Intent intent = new Intent();
        intent.putExtra("MenuFlag", ITEM_MENU_SAVE_IMAGE);
        setResult(Activity.RESULT_OK, intent);
        Storage.saveImage("1");
        finish();
    }

    public void delete(View view) {
        Intent intent = new Intent();
        intent.putExtra("MenuFlag", ITEM_MENU_DEL);
        intent.putExtra("msgId", message.getMsgId());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    public void recall(View view) {
        if (System.currentTimeMillis() - message.getMsgTime() > 120000) {
            Toast.makeText(this, "超出两分钟无法撤回", Toast.LENGTH_SHORT).show();
            return;
        }

        Intent intent = new Intent();
        intent.putExtra("MenuFlag", ITEM_MENU_RECALL);
        intent.putExtra("msgId", message.getMsgId());
        setResult(Activity.RESULT_OK, intent);
        finish();
    }


    public void copyText(View view) {
        EMMessage message = getIntent().getParcelableExtra("message");

        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        //第一个参数只是一个标记，随便传入。
        //第二个参数是要复制到剪贴版的内容
        EMTextMessageBody emTextMessageBody = (EMTextMessageBody) message.getBody();
        ClipData clip = ClipData.newPlainText("IntersetTag", emTextMessageBody.getMessage());
        clipboard.setPrimaryClip(clip);
        finish();
    }
}
