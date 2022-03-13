package com.android.nanguo.section.account.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.android.nanguo.R;
import com.android.nanguo.app.adapter.FriendGroupingAdapter;
import com.android.nanguo.app.api.global.EventUtil;
import com.android.nanguo.app.api.new_data.FriendGroupingBean;
import com.android.nanguo.app.api.old_data.EventCenter;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.base.BaseInitActivity;
import com.android.nanguo.app.help.RclViewHelp;
import com.android.nanguo.app.weight.ConfirmInputDialog;
import com.android.nanguo.common.utils.ToastUtils;
import com.zds.base.json.FastJsonUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

//好友分组
public class FriendGroupingActvity extends BaseInitActivity {
    @BindView(R.id.title_bar)
    EaseTitleBar title_bar;
    @BindView(R.id.recyclerView)
    RecyclerView recyclerView;
    @BindView(R.id.tv_chat_record)
    TextView tv_chat_record;

    private final List<FriendGroupingBean> groupingDatas = new ArrayList<>();
    private String friendUserId;
    private String categoryId;
    private String categoryName;
    private FriendGroupingAdapter groupingAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_friend_grouping;
    }

    public static void actionStart(Context context, String friendUserId, String categoryId, String categoryName) {
        Intent intent = new Intent(context, FriendGroupingActvity.class);
        intent.putExtra("friendUserId", friendUserId);
        intent.putExtra("categoryId", categoryId);
        intent.putExtra("categoryName", categoryName);
        context.startActivity(intent);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        friendUserId = intent.getStringExtra("friendUserId");
        categoryId = intent.getStringExtra("categoryId");
        categoryName = intent.getStringExtra("categoryName");
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        title_bar.setTitle("分组管理");
        title_bar.setOnBackPressListener(view -> finish());

        groupingAdapter = new FriendGroupingAdapter(groupingDatas);
        RclViewHelp.initRcLmVertical(mContext, recyclerView, groupingAdapter);
        groupingAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                modifyFriendCategory(position);
            }
        });

        queryGrouping();
    }

    private void modifyFriendCategory(int position) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("categoryId", groupingDatas.get(position).getCategoryId());
        map.put("friendUserId", friendUserId);
        ApiClient.requestNetHandle(this, AppConfig.modifyFriendCategory, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                EventBus.getDefault().post(new EventCenter(EventUtil.FLUSHGROUPING));//刷新好友分组
                ToastUtils.showToast("设置成功");
                Intent intent = new Intent();
                intent.putExtra("categoryName",groupingDatas.get(position).getName());
                setResult(RESULT_OK,intent);
                finish();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtils.showToast(msg);
            }
        });
    }

    @OnClick({R.id.tv_chat_record})
    public void click(View v) {
        switch (v.getId()) {
            case R.id.tv_chat_record:
                ConfirmInputDialog dialog = new ConfirmInputDialog(mContext);
                dialog.setOnConfirmClickListener(new ConfirmInputDialog.OnConfirmClickListener() {
                    @Override
                    public void onConfirmClick(String content) {
                        saveGrouping(content);
                    }
                });
                dialog.show();

                dialog.setCancelable(false);
                dialog.setCanceledOnTouchOutside(false);
                dialog.setTitle("添加新分组");
                break;
        }
    }

    private void saveGrouping(String content) {
        Map<String, Object> map = new HashMap<>(2);
        map.put("name", content);
        ApiClient.requestNetHandle(this, AppConfig.saveFriendCategory, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtils.showToast("添加分组成功");
                EventBus.getDefault().post(new EventCenter(EventUtil.FLUSHGROUPING));//刷新好友分组
                queryGrouping();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtils.showToast(msg);
            }
        });
    }


    private void queryGrouping() {
        Map<String, Object> map = new HashMap<>(2);
        ApiClient.requestNetHandle(this, AppConfig.selectFriendCategory, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                groupingDatas.clear();
                List<FriendGroupingBean> datas = FastJsonUtil.getList(json, FriendGroupingBean.class);
                if (!TextUtils.isEmpty(categoryId)) {
                    for (int i = 0; i < datas.size(); i++) {
                        if (categoryId.equals(datas.get(i).getCategoryId())) {
                            datas.get(i).setCheck(true);
                        }
                    }
                }
                groupingDatas.addAll(datas);
                groupingAdapter.notifyDataSetChanged();
                groupingAdapter.loadMoreComplete();
            }

            @Override
            public void onFailure(String msg) {
                groupingAdapter.loadMoreFail();
            }
        });


    }

}
