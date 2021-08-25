package com.ycf.qianzhihe.section.chat.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.hyphenate.easeui.ui.base.EaseBaseFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.adapter.ServiceAdapter;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.EaseConstant;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_data.ServiceInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.section.chat.activity.ChatActivity;
import com.ycf.qianzhihe.section.chat.activity.Custom1Activity;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

/**
 * @author lhb
 * 客服fragment
 */
public class CustomServiceFragment extends EaseBaseFragment {

    @BindView(R.id.recycle_view)
    RecyclerView mRecycleView;
    @BindView(R.id.title_bar)
    EaseTitleBar title_bar;
    @BindView(R.id.custom1)
    RelativeLayout mCuston1;

    Unbinder unbinder;

    ServiceAdapter mServiceAdapter;
    List<ServiceInfo.DataBean> list;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_custom_service, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initLogic();
    }


    protected void initLogic() {
        title_bar.setTitle("客服列表");
        title_bar.setOnBackPressListener(view -> mContext.finish());
        list = new ArrayList<>();
        mServiceAdapter = new ServiceAdapter(list);
        mRecycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecycleView.setAdapter(mServiceAdapter);
        mServiceAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                // start chat acitivity
                Intent intent = new Intent(getActivity(), ChatActivity.class);
                ServiceInfo.DataBean dataBean = list.get(position);
                intent.putExtra(Constant.EXTRA_USER_ID, dataBean.getUserId() + Constant.ID_REDPROJECT)
                        .putExtra(Constant.NICKNAME, dataBean.getNickName())
                        .putExtra(Constant.CUSTOM_KF,true)
                        .putExtra(EaseConstant.EXTRA_CHAT_TYPE, EaseConstant.CHATTYPE_SINGLE);
                startActivity(intent);
            }
        });
        getService();
    }




    /**
     * 客服列表
     */
    private void getService() {
        Map<String, Object> map = new HashMap<>(3);
        map.put("pageNum", "1");
        map.put("pageSize", "15");
        ApiClient.requestNetHandle(getActivity(), AppConfig.CUSTOM_LIST, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (null != json && json.length() > 0) {
                    ServiceInfo info = FastJsonUtil.getObject(json, ServiceInfo.class);
                    if (info.getData() != null && info.getData().size() > 0) {
                        list.addAll(info.getData());
                        mServiceAdapter.notifyDataSetChanged();
                    }
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }


    @OnClick({R.id.tv_help})
    public void onViewClicked(View view) {
        switch (view.getId()) {

            case R.id.tv_help:
                Intent intent = new Intent(getActivity(), Custom1Activity.class);
                getActivity().startActivity(intent);
                break;
            default:
                break;
        }
    }
}
