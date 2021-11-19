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
package com.ycf.qianzhihe.section.contact.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMCmdMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.easeui.widget.EaseAlertDialog;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.hyphenate.exceptions.HyphenateException;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.R2;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.weight.CommonDialog;
import com.ycf.qianzhihe.common.enums.SearchType;
import com.ycf.qianzhihe.common.utils.ToastUtils;
import com.ycf.qianzhihe.section.account.activity.AddUserDetailActivity;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.StringUtil;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

/**
 * @author lhb
 * 添加好友
 */
public class AddUserActivity extends BaseInitActivity {

    @BindView(R2.id.title_bar)
    EaseTitleBar title_bar;
    @BindView(R2.id.edit_note)
    EditText mEditNote;
    @BindView(R2.id.avatar)
    EaseImageView mAvatar;
    @BindView(R2.id.name)
    TextView mName;

    @BindView(R2.id.ll_user)
    RelativeLayout ll_user;
    private String username;
    private LoginInfo easeUserInfos;
    @BindView(R2.id.btn_add)
    Button btn_add;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_add_user;
    }

    public static void actionStart(Context context) {
        Intent intent = new Intent(context, AddUserActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        title_bar.setTitle("查找好友");
        title_bar.setOnBackPressListener(view -> finish());
        title_bar.setRightTitle("查找");
        title_bar.setOnRightClickListener(view ->  getUserInfo());

    }

    /**
     * 查询好友
     */
    private void getUserInfo() {
        if (StringUtil.isEmpty(mEditNote.getText().toString())) {
            ToastUtils.showToast("请输入好友账号");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("phone", mEditNote.getText().toString());
        ApiClient.requestNetHandle(this, AppConfig.FIND_USER, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                // XLog.json(json);
                easeUserInfos = FastJsonUtil.getObject(json, LoginInfo.class);
                if (easeUserInfos != null) {
                    username = easeUserInfos.getNickName();
                    mName.setText(easeUserInfos.getNickName());
                    GlideUtils.GlideLoadCircleErrorImageUtils(AddUserActivity.this,AppConfig.checkimg(easeUserInfos.getUserHead()), mAvatar, R.mipmap.img_default_avatar);
                    ll_user.setVisibility(View.VISIBLE);
                } else {
                    ll_user.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                ll_user.setVisibility(View.GONE);
            }
        });


    }


    /**
     * 添加好友
     */
    private void addUser() {
        if (StringUtil.isEmpty(mEditNote.getText().toString())) {
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("toUserId", easeUserInfos.getUserId());
        map.put("originType", Constant.ADD_USER_ORIGIN_TYPE_SEARCH);
        ApiClient.requestNetHandle(this, AppConfig.APPLY_ADD_USER, "正在添加", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                EMMessage cmdMsg = EMMessage.createSendMessage(EMMessage.Type.CMD);

//支持单聊和群聊，默认单聊，如果是群聊添加下面这行
//                cmdMsg.setChatType(ChatType.GroupChat)
                //action可以自定义
                String action = Constant.ACTION_APPLY_ADD_FRIEND;
                EMCmdMessageBody cmdBody = new EMCmdMessageBody(action);
                cmdMsg.addBody(cmdBody);
//                //发送给某个人
                String toUsername = easeUserInfos.getUserId() + Constant.ID_REDPROJECT;
                cmdMsg.setTo(toUsername);
                cmdMsg.setFrom(UserComm.getUserId());
                cmdMsg.setAttribute(Constant.APPLY_ADD_FRIEND_ID, UserComm.getUserInfo().getUserId());

                EMClient.getInstance().chatManager().sendMessage(cmdMsg);
                ToastUtils.showToast("申请成功");
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
                ll_user.setVisibility(View.GONE);
            }
        });


    }


    /**
     * add contact
     */
    public void addContact() {
        if (username == null) {
            ToastUtils.showToast("获取用户信息失败");
            return;
        }
        if (EMClient.getInstance().getCurrentUser().equals(username)) {
            new EaseAlertDialog(this, R.string.not_add_myself).show();
            return;
        }

//        if (UserOperateManager.getInstance().getContactList().contains(username)) {
//            new EaseAlertDialog(this, R.string.This_user_is_already_your_friend).show();
//            return;
//        }

        if (commonDialog != null && commonDialog.isShowing()) {
            commonDialog.dismiss();
        }
        commonDialog = new CommonDialog.Builder(this).setView(R.layout.add_friend_dialog).setOnClickListener(R.id.btn_ok, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonDialog.dismiss();
                if (mEditText != null && mEditText.getText().toString() != null && !"".equals(mEditText.getText().toString())) {
                    addFriend(username, mEditText.getText().toString());
                } else {
                    addFriend(username, "加个好友呗！");
                }

            }
        }).setOnClickListener(R.id.btn_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                commonDialog.dismiss();
            }
        }).setText(R.id.title, "说点啥子吧").create();
        commonDialog.getView(R.id.btn_cancel).setVisibility(View.VISIBLE);
        mEditText = (EditText) commonDialog.getView(R.id.et_message);
        commonDialog.show();
    }

    EditText mEditText;
    CommonDialog commonDialog;

    private void addFriend(final String username, final String reason) {
        showLoading("正在申请添加好友...");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    EMClient.getInstance().contactManager().addContact(username, reason);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast("成功发送申请");
                        }
                    });

                } catch (final HyphenateException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ToastUtils.showToast(e.getMessage());
                        }
                    });

                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        dismissLoading();
                    }
                });
            }
        }).start();
    }


    @OnClick({R2.id.ll_user, R2.id.btn_add})
    public void onViewClicked(View view) {
        hideSoftKeyboard();
        int id = view.getId();
        if (id == R.id.ll_user) {
            AddUserDetailActivity.actionStart(mContext, easeUserInfos);
        } else if (id == R.id.btn_add) {
            addUser();
        }
    }
}
