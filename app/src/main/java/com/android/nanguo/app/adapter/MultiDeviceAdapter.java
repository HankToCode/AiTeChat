package com.android.nanguo.app.adapter;

import android.view.View;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.nanguo.R;
import com.android.nanguo.app.api.old_http.ApiClient;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.app.api.old_http.ResultListener;
import com.android.nanguo.app.domain.MultiDevice;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.util.StringUtil;
import androidx.annotation.Nullable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MultiDeviceAdapter extends BaseQuickAdapter<MultiDevice.Device, BaseViewHolder> {
    public MultiDeviceAdapter(@Nullable List<MultiDevice.Device> data) {
        super(R.layout.item_multi_device, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MultiDevice.Device item) {

        helper.setText(R.id.time,"登录时间: "+ StringUtil.formatTime(item.getUt(),"yyyy-MM-dd HH:mm:ss") );
        helper.setText(R.id.ip,"IP地址:  "+ item.getIp() );
        helper.setText(R.id.mobile,"IP地址:  "+ item.getDname() );
        helper.setText(R.id.address,"登录地点:  "+ item.getAddress());
        helper.setVisible(R.id.kick_out,true);
        helper.setOnClickListener(R.id.kick_out, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                kickOutOtherDevice(item.getDid());
                getData().remove(item);
                notifyDataSetChanged();
            }
        });
    }

    public void kickOutOtherDevice(String did){
        Map<String,Object> map =new HashMap<>();
        map.put("deviceId", did);
        ApiClient.requestNetHandle(mContext, AppConfig.multiDeviceLogout, "请稍候...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                ToastUtil.toast(json);
            }
            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);

            }
        });
    }
}