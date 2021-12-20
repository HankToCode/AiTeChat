package com.android.nanguo.section.conversation;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.chat.EMClient;
import com.android.nanguo.R;
import com.android.nanguo.app.api.Constant;
import com.android.nanguo.app.api.global.EventUtil;
import com.android.nanguo.app.api.old_data.ApplyFriendData;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.app.utils.ProjectUtil;
import com.android.nanguo.app.utils.XClickUtil;
import com.zds.base.ImageLoad.GlideUtils;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;
import com.zds.base.Toast.ToastUtil;

public class AuditUserActivity extends BaseInitActivity {
    @BindView(R.id.img_head)
    ImageView imgHead;
    @BindView(R.id.tv_nick_name)
    TextView nickName;
    @BindView(R.id.line_status)
    TextView lineStatus;
    @BindView(R.id.tv_account)
    TextView tvAccount;
    @BindView(R.id.origin)
    TextView origin;

    ApplyFriendData applyFriendData;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_audit_user;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
    }

    @Override
    protected void initData() {
        super.initData();
        initLogic();
    }

    protected void initLogic() {
        nickName.setText(applyFriendData.getUserNickName());
        origin.setText(ProjectUtil.getFriendOrigin(applyFriendData.getOriginType(), applyFriendData.getOriginName()));
        GlideUtils.GlideLoadCircleErrorImageUtils(this, applyFriendData.getUserHead(), imgHead,
                R.mipmap.ic_ng_avatar);

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        applyFriendData = (ApplyFriendData) intent.getSerializableExtra("applyFriendData");

    }

    @Override
    protected void onEventComing(EventCenter center) {

    }


    @OnClick({R.id.black, R.id.reject, R.id.agree, R.id.tv_report})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.black:
                blackContact("1");
                break;
            case R.id.reject:
                if (!XClickUtil.isFastDoubleClick(view, 1000)) {
                    agreeApply(applyFriendData.getApplyId(), 2);
                }

                break;
            case R.id.agree:
                if (!XClickUtil.isFastDoubleClick(view, 1000)) {
                    agreeApply(applyFriendData.getApplyId(), 1);
                }
                break;
            case R.id.tv_report:
                //举报
                startActivity(new Intent(this, ReportActivity.class).putExtra("from", "1").putExtra("userGroupId", applyFriendData.getUserId()));
                break;
        }
    }

    /**
     * 拉黑好友
     */
    public void blackContact(String blackStatus) {
        Map<String, Object> map = new HashMap<>(2);

        map.put("friendUserId", applyFriendData.getUserId());
        //拉黑状态 0-未拉黑 1-已拉黑
        map.put("blackStatus", blackStatus);

        ApiClient.requestNetHandle(this, AppConfig.BLACK_USER_FRIEND,
                "正在拉黑...", map,
                new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        ToastUtil.toast("拉黑成功");
                        EMClient.getInstance().chatManager().deleteConversation(applyFriendData.getUserId() + Constant.ID_REDPROJECT, false);
                        EventBus.getDefault().post(new EventCenter<>(EventUtil.OPERATE_BLACK));
                        finish();
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });
    }

    /**
     * 同意拒绝好友申请
     */
    private void agreeApply(String applyId, int type) {
        Map<String, Object> map = new HashMap<>();
        map.put("applyId", applyId);
        map.put("applyStatus", type);

        ApiClient.requestNetHandle(this, AppConfig.APPLY_ADD_USER_STATUS, type == 1 ? "正在同意..." : "正在拒绝...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                //发起通知
//                page = 1;
//                queryUserStatus();

                if (type == 1) {
                    ToastUtil.toast("已同意");
                } else {
                    ToastUtil.toast("已拒绝");
                }
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);

            }
        });

    }
}
