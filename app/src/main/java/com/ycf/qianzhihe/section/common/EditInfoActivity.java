package com.ycf.qianzhihe.section.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.text.method.DigitsKeyListener;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ycf.qianzhihe.DemoApplication;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.CommonApi;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.zds.base.Toast.ToastUtil;

/**
 * @author lhb
 * 编辑用户信息，eg:昵称
 */
public class EditInfoActivity extends BaseInitActivity {

    public static final String FUNC_TYPE_MODIFY_GROUP_REMARK = "6";

    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.toolbar_subtitle)
    TextView mToolSubTitle;
    @BindView(R.id.et_nick_name)
    EditText mEtNickName;
    @BindView(R.id.img_del)
    ImageView mImgDel;
    @BindView(R.id.tv_hint_account)
    TextView mTvHintAccount;


    /**
     * 1:修改昵称
     * 2：设置千纸鹤号
     * 3：设置个性签名
     * 4.修改群昵称
     * 5.修改我的群昵称
     */
    private String from;

    /**
     * 群ID
     */
    private String groupId;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_edit_my_info;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mToolSubTitle.setVisibility(View.VISIBLE);
        mToolSubTitle.setText("保存");
        if (mEtNickName.getText().length() > 0) {
            mImgDel.setVisibility(View.VISIBLE);
        }


        //昵称保存
        mToolSubTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //设置昵称
                if (from.equals("1")) {
                    editNickName();
                }//设置千纸鹤号
                else if (from.equals("2")) {
                    setYxAccount();
                }//设置个性签名
                else if (from.equals("3")) {
                    setSelfLable();
                } else if (from.equals("4")) {
                    setGroupNickName();
                } else if (from.equals("5")) {
                    setMyGroupNickName();
                } else if (from.equals(FUNC_TYPE_MODIFY_GROUP_REMARK)) {
                    setGroupRemark();
                }
            }
        });
        mEtNickName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().length() > 0) {
                    mImgDel.setVisibility(View.VISIBLE);
                } else {
                    mImgDel.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    protected void onEventComing(EventCenter center) {

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        from = intent.getStringExtra("from");
        if ("1".equals(from)) {
            mToolbarTitle.setText("修改昵称");
            mEtNickName.setHint("输入昵称");
            mEtNickName.setText(UserComm.getUserInfo().getNickName());
            mEtNickName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});

        } else if (from.equals("2")) {
            mToolbarTitle.setText("千纸鹤号");
            mEtNickName.setHint("请输入您的千纸鹤号");
            mEtNickName.setText(UserComm.getUserInfo().getUserCode());
            mTvHintAccount.setVisibility(View.VISIBLE);
            String dig = "0123456789qwertyuiopasdfghjklzxcvbnmQWERTYUIOPASDFGHJKLZXCVBNM_";
            mEtNickName.setKeyListener(DigitsKeyListener.getInstance(dig));

        } else if (from.equals("3")) {
            mToolbarTitle.setText("个性签名");
            mEtNickName.setHint("输入个性签名");
            mEtNickName.setText(UserComm.getUserInfo().getSign());
        } else if (from.equals("4")) {
            mToolbarTitle.setText("修改群名称");
            mEtNickName.setHint("输入群名称");
            mEtNickName.setText(intent.getStringExtra("groupName"));
            groupId = intent.getStringExtra("groupId");
            mEtNickName.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        } else if (from.equals("5")) {
            mToolbarTitle.setText("修改我的群昵称");
            mEtNickName.setHint("输入我的群昵称");
            mEtNickName.setText(intent.getStringExtra("myGroupName"));
            groupId = intent.getStringExtra("groupId");
        } else if (from.equals(FUNC_TYPE_MODIFY_GROUP_REMARK)) {
            mToolbarTitle.setText("群备注");
            mEtNickName.setHint("输入群备注");
            mEtNickName.setText(intent.getStringExtra("key_intent_group_remark"));
            groupId = intent.getStringExtra("groupId");
        }
    }


    /**
     * 保存昵称
     */
    private void editNickName() {
        if (mEtNickName.getText().toString().trim().length() <= 0) {
            ToastUtil.toast("请先填写昵称");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("nickName", mEtNickName.getText().toString().trim());
        ApiClient.requestNetHandle(this, AppConfig.MODIFY_USER_NICK_NAME, "正在保存...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast("修改成功");
                CommonApi.upUserInfo(DemoApplication.getInstance().getApplicationContext());
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }


    /**
     * 设置千纸鹤号
     */
    private void setYxAccount() {
        if (mEtNickName.getText().toString().trim().length() <= 0) {
            ToastUtil.toast("请先填写千纸鹤号");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("userCode", mEtNickName.getText().toString().trim());
        ApiClient.requestNetHandle(this, AppConfig.MODIFY_USER_CODE, "正在保存...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast("修改成功");
                CommonApi.upUserInfo(DemoApplication.getInstance().getApplicationContext());
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }


    /**
     * 修改个性签名
     */
    private void setSelfLable() {
        if (mEtNickName.getText().toString().trim().length() <= 0) {
            ToastUtil.toast("请先填写个性签名");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("sign", mEtNickName.getText().toString().trim());
        ApiClient.requestNetHandle(this, AppConfig.MODIFY_SELF_LABLE, "正在保存...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast("修改成功");
                CommonApi.upUserInfo(DemoApplication.getInstance().getApplicationContext());
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }


    /**
     * 修改群名称
     */
    private void setGroupNickName() {
        if (mEtNickName.getText().toString().trim().length() <= 0) {
            ToastUtil.toast("请先填写群昵称");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("groupName", mEtNickName.getText().toString().trim());
        map.put("groupHead", "");
        map.put("groupId", groupId);

        //1-群名称 2-群头像
        map.put("updateType", "1");

        ApiClient.requestNetHandle(this, AppConfig.MODIFY_GROUP_NAME_OF_HEAD, "正在保存...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                EventBus.getDefault().post(new EventCenter<>(EventUtil.REFRESH_GROUP_NAME, mEtNickName.getText().toString().trim()));
                EventBus.getDefault().post(new EventCenter(EventUtil.FLUSHGROUP));
                ToastUtil.toast("修改成功");
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }


    /**
     * 修改我的群昵称
     */
    private void setMyGroupNickName() {
        if (mEtNickName.getText().toString().trim().length() <= 0) {
            ToastUtil.toast("请先填写群昵称");
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("userNickName", mEtNickName.getText().toString().trim());
        map.put("groupId", groupId);

        //1-群名称 2-群头像
        map.put("updateType", "1");

        ApiClient.requestNetHandle(this, AppConfig.MODIFY_USER_GROUP_NICKNAME, "正在保存...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                EventBus.getDefault().post(new EventCenter<>(EventUtil.REFRESH_MY_GROUP_NAME, mEtNickName.getText().toString().trim()));
                ToastUtil.toast("修改成功");
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }

    /**
     * 修改群备注
     */
    private void setGroupRemark() {

        Map<String, Object> map = new HashMap<>();
        map.put("groupNickName", mEtNickName.getText().toString().trim());
        map.put("groupId", groupId);


        ApiClient.requestNetHandle(this, AppConfig.MODIFY_GROUP_REMARK, "正在保存...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                //修改群备注之后，原先所有显示群昵称的地方都要被群备注替代
                EventBus.getDefault().post(new EventCenter<>(EventUtil.REFRESH_GROUP_NAME, mEtNickName.getText().toString().trim()));
                EventBus.getDefault().post(new EventCenter(EventUtil.FLUSHGROUP));
                ToastUtil.toast("保存成功");
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }


    @OnClick(R.id.img_del)
    public void onViewClicked() {
        mEtNickName.getText().clear();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }


}