package com.ycf.qianzhihe.section.common;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_data.GroupDetailInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.utils.ImageUtil;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.util.StringUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.zds.base.Toast.ToastUtil;


/**
 * 描   述: 群公告
 * 日   期: 2017/11/30 15:45
 * 更新日期: 2017/11/30
 *
 * @author lhb
 */
public class NoticeActivity extends BaseInitActivity {

    @BindView(R.id.title_bar)
    EaseTitleBar title_bar;

    @BindView(R.id.img_head)
    EaseImageView mImgHead;
    @BindView(R.id.tv_name)//群主
    TextView mTvName;
    @BindView(R.id.tv_time)//发布时间
    TextView mTvTime;
    @BindView(R.id.tv_notice)
    TextView mNotice;
    @BindView(R.id.et_notice)
    TextView mEtNotice;
    private String groupId;
    private String toChatUsername;
    private String noticeString;
    private long time;
    private Boolean isMyroom = false;
    private String img_head, tv_head;
    private int user_rank;
    private List<GroupDetailInfo.GroupUserDetailVoListBean> mDetailVoListBeanList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_notice;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        setTitle("群公告");

        if (user_rank == 2) {
            title_bar.setRightTitle("编辑");
            title_bar.getRightLayout().setVisibility(View.VISIBLE);
        } else {
            title_bar.getRightLayout().setVisibility(View.GONE);
        }
        if (noticeString == null) {
            mNotice.setText("暂无公告");
        } else {
            mNotice.setText(noticeString);
        }
        GlideUtils.loadImageViewLoding(AppConfig.checkimg(img_head), mImgHead, R.mipmap.img_default_avatar);
        mTvName.setText(tv_head);
        mTvTime.setText(StringUtil.formatDateMinute(time, ""));
        ImageUtil.setAvatar(mImgHead);
        title_bar.setOnBackPressListener(view -> finish());
        title_bar.setOnRightClickListener(new EaseTitleBar.OnRightClickListener() {
            @Override
            public void onRightClick(View view) {

                if (title_bar.getRightText().getText().toString().equals("编辑")) {
                    mNotice.setVisibility(View.GONE);
                    mEtNotice.setVisibility(View.VISIBLE);
                    title_bar.setRightTitle("提交");
                } else {
                    submit();
                }
            }
        });
    }

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {

    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);

        groupId = intent.getStringExtra("groupId");
        noticeString = intent.getStringExtra("notice");
        time = intent.getLongExtra("time",0L);
        img_head = intent.getStringExtra("img_head");
        tv_head = intent.getStringExtra("tv_head");
        user_rank = intent.getIntExtra("user_rank",0);
        toChatUsername = intent.getStringExtra("username");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ButterKnife.bind(this);
    }


    private void submit() {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("groupNotice", mEtNotice.getText().toString());
        ApiClient.requestNetHandle(NoticeActivity.this, AppConfig.MODIFY_GROUP_NOTICE, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast(msg);
                EventBus.getDefault().post(new EventCenter<String>(404, mEtNotice.getText().toString()));
                startActivity(new Intent(mContext, MyGroupDetailActivity.class).putExtra("username", toChatUsername).putExtra("groupId", groupId));
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }
}
