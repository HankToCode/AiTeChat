package com.android.nanguo.section.common;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.android.nanguo.R;
import com.android.nanguo.app.adapter.RedPacketAdapter;
import com.android.nanguo.app.api.global.UserComm;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.api.old_data.RedPacketInfo;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.app.operate.UserOperateManager;
import com.android.nanguo.app.utils.ImageUtil;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.StringUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;

/**
 * 作   者：赵大帅
 * 描   述: 红包详情
 * 邮   箱: 2510722254@qqq.com
 * 日   期: 2017/11/27 14:08
 * 更新日期: 2017/11/27
 *
 * @author Administrator
 */
public class RedPacketDetailActivity extends BaseInitActivity {

    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;


    //红包id
    private String rid;
    //红包类型 0群聊红包 1单聊红包
    private String type = "0";

    private RedPacketAdapter mAdapter;
    private List<RedPacketInfo.RedPacketDetailListBean> mList;

    private TextView tv_money, tv_name;
    private EaseImageView img_head;
    //头像
    private String head;
    //昵称
    private String nickname;
    //判断是否从红包记录查询
    private boolean fromRecord;
    private TextView tv_message_hb, tv_intro;
    private LinearLayout ll_user_money;
    private EaseTitleBar title_bar;


    @Override
    protected int getLayoutId() {
        return R.layout.activity_red_packet;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initImmersionBar(false);
        View headView =
                LayoutInflater.from(this).inflate(R.layout.red_packet_head_view, null);
        title_bar = headView.findViewById(R.id.title_bar);
        tv_money = headView.findViewById(R.id.tv_money);
        ll_user_money = headView.findViewById(R.id.ll_user_money);
        ll_user_money.setVisibility(View.INVISIBLE);
        tv_name = headView.findViewById(R.id.tv_name);
        tv_intro = headView.findViewById(R.id.tv_intro);
        img_head = headView.findViewById(R.id.img_head);
        tv_message_hb = headView.findViewById(R.id.tv_message_hb);

        title_bar.setTitle("领取详情");
        title_bar.setOnBackPressListener(view -> finish());

        ImageUtil.setAvatar(img_head);
        img_head.setShapeType(2);

        mList = new ArrayList<>();
        mAdapter = new RedPacketAdapter(mList);
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setAdapter(mAdapter);
        mAdapter.addHeaderView(headView);
        initHeadView();
        getData();

        //TODO 红包记录列表
//        mToolbarSubtitle.setOnClickListener(v -> ChatRedRecordActivity.actionStart(RedPacketDetailActivity.this));
    }

    private void initHeadView() {
        if (!TextUtils.isEmpty(head))
            GlideUtils.loadRoundCircleImage(AppConfig.checkimg(head), img_head,
                    R.mipmap.ic_ng_avatar, 10);
        if (!TextUtils.isEmpty(nickname))
            tv_name.setText(nickname + "发出的红包");
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
        rid = intent.getStringExtra("rid");
        head = intent.getStringExtra("head");
        nickname = intent.getStringExtra("nickname");
        type = intent.getStringExtra("type");
        fromRecord = intent.getBooleanExtra("fromRecord", false);
    }

    /**
     * 获取红包数据
     */
    private void getData() {
        System.out.println("###fromRecord=" + fromRecord);
        Map<String, Object> map = new HashMap<>();
        map.put("redPacketId", rid + "");
        String url = fromRecord ? AppConfig.getRedPacketFromDB : AppConfig.getRedPacket;
        ApiClient.requestNetHandle(this, url, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    if ("1".equals(type)) {
                        tv_message_hb.setVisibility(View.VISIBLE);
                    }
                    RedPacketInfo redPacketInfo = FastJsonUtil.getObject(json, RedPacketInfo.class);

                    if (redPacketInfo == null) {
                        ToastUtil.toast("信息异常");
                        return;
                    }

                    String time = StringUtil.isEmpty(redPacketInfo.getRobFinishTime()) ? "" : "，" + redPacketInfo.getRobFinishTime() + "被抢光";
                    if (StringUtil.isEmpty(time)) {
                        if ("1".equals(type)) {//单聊
                            time = "等待对方领取";
                        }
                    }

                    head = redPacketInfo.getUserHead();
                    nickname = redPacketInfo.getUserNickName();

                    if (UserOperateManager.getInstance().hasUserName(redPacketInfo.getUserId())) {
                        nickname = UserOperateManager.getInstance().getUserName(redPacketInfo.getUserId());
                    }

                    initHeadView();
                    if (tv_intro != null) {
                        tv_intro.setText(redPacketInfo.getRemark() == null ? "恭喜发财，大吉大利！" : redPacketInfo.getRemark().toString());
                    }
                    ll_user_money.setVisibility(View.VISIBLE);

                    if (tv_money != null) {
                        tv_money.setText(StringUtil.getFormatValue2(redPacketInfo.getMoney()));
                        if (!TextUtils.isEmpty(redPacketInfo.getPacketAmount() + "")) {
                            if (redPacketInfo.getPacketAmount() > 0) {
                                for (int i = 0; i < redPacketInfo.getRedPacketDetailList().size(); i++) {
                                    if (redPacketInfo.getRedPacketDetailList().get(i).getUserId().equals(UserComm.getUserInfo().getUserId())) {
                                        tv_money.setText(StringUtil.getFormatValue2(redPacketInfo.getRedPacketDetailList().get(i).getMoney()));
                                        break;
                                    }
                                }
                            }
                        }
                    }

                    if (redPacketInfo.getRedPacketDetailList() != null && redPacketInfo.getRedPacketDetailList().size() > 0) {
                        mList.addAll(redPacketInfo.getRedPacketDetailList());
                        int ylSize = mList.size();//已领数量
                        int allSize = redPacketInfo.getPacketAmount();//总数量
                        mAdapter.setIsfirsh(false);
                        double allMoney = 0;
                        for (int i = 0; i < mList.size(); i++) {
                            allMoney += mList.get(i).getMoney();
                        }

                        tv_message_hb.setText("已领取" + ylSize + "/" + (allSize == 0 ? ylSize : allSize) +
                                "个，共" + StringUtil.getFormatValue2(allMoney) + "/" + StringUtil
                                .getFormatValue2(redPacketInfo.getMoney() == 0? allMoney: redPacketInfo.getMoney()) + "元");
                        if (ylSize == allSize) {
                            mAdapter.setIsfirsh(true);
                        } else {
                            mAdapter.setIsfirsh(false);
                        }
                        mAdapter.notifyDataSetChanged();
                    } else {

                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }


}
