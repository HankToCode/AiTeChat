package com.hyphenate.easeim.section.common;

import static com.zds.base.Toast.ToastUtil.toast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.hyphenate.easecallkit.widget.EaseImageView;
import com.hyphenate.easeim.R;
import com.hyphenate.easeim.app.api.old_data.EventCenter;
import com.hyphenate.easeim.app.api.old_data.GroupDetailInfo;
import com.hyphenate.easeim.app.api.old_http.ApiClient;
import com.hyphenate.easeim.app.api.old_http.AppConfig;
import com.hyphenate.easeim.app.api.old_http.ResultListener;
import com.hyphenate.easeim.app.base.BaseInitActivity;
import com.hyphenate.easeim.app.utils.ImageUtil;
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


/**
 * 描   述: 群公告
 * 日   期: 2017/11/30 15:45
 * 更新日期: 2017/11/30
 *
 * @author lhb
 */
public class NoticeActivity extends BaseInitActivity {

    @BindView(R.id.bar)
    View mBar;
    @BindView(R.id.ll_back)
    LinearLayout mLlBack;
    @BindView(R.id.toolbar_subtitle)
    TextView mToolbarSubtitle;
    @BindView(R.id.img_right)
    ImageView mImgRight;
    @BindView(R.id.toolbar_title)
    TextView mToolbarTitle;
    @BindView(R.id.toolbar)
    Toolbar mToolbar;
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
            mToolbarSubtitle.setText("编辑");
            mToolbarSubtitle.setVisibility(View.VISIBLE);
        } else {
            mToolbarSubtitle.setVisibility(View.GONE);
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
        Bundle extras = intent.getExtras();

        groupId = extras.getString("groupId");
        noticeString = extras.getString("notice");
        time = extras.getLong("time");
        img_head = extras.getString("img_head");
        tv_head = extras.getString("tv_head");
        user_rank = extras.getInt("user_rank");
        toChatUsername = extras.getString("username");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TODO: add setContentView(...) invocation
        ButterKnife.bind(this);
    }

    @OnClick(R.id.toolbar_subtitle)
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.toolbar_subtitle:
                TextView toolbar_subtitle = (TextView) findViewById(R.id.toolbar_subtitle);

                if (toolbar_subtitle.getText().toString().equals("编辑")) {
                    mNotice.setVisibility(View.GONE);
                    mEtNotice.setVisibility(View.VISIBLE);
                    toolbar_subtitle.setText("提交");
                } else {
                    submit();

                    startActivity(new Intent(this, MyGroupDetailActivity.class).putExtra("username", toChatUsername).putExtra("groupId", groupId));

                    finish();
                }
                break;
            default:
                break;
        }
    }

    private void submit() {
        Map<String, Object> map = new HashMap<>();
        map.put("groupId", groupId);
        map.put("groupNotice", mEtNotice.getText().toString());
        ApiClient.requestNetHandle(NoticeActivity.this, AppConfig.MODIFY_GROUP_NOTICE, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                toast(msg);
                EventBus.getDefault().post(new EventCenter<String>(404, mEtNotice.getText().toString()));
            }

            @Override
            public void onFailure(String msg) {
                toast(msg);
            }
        });
    }
}
