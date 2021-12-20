package com.android.nanguo.app.adapter;

import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.android.nanguo.R;
import com.android.nanguo.app.api.old_data.BlackListInfo;
import com.android.nanguo.app.api.old_http.AppConfig;
import com.android.nanguo.section.account.activity.UserInfoDetailActivity;
import com.zds.base.ImageLoad.GlideUtils;

import java.util.List;

public class BlackAdapter extends BaseQuickAdapter<BlackListInfo.ContactInfo, BaseViewHolder> {
    public BlackAdapter(@Nullable List<BlackListInfo.ContactInfo> data) {
        super(R.layout.adapter_my_group, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, BlackListInfo.ContactInfo item) {
        helper.setText(R.id.tv_group_name, item.getFriendNickName());
        GlideUtils.GlideLoadCircleErrorImageUtils(mContext, AppConfig.checkimg(item.getFriendUserHead()), helper.getView(R.id.img_group), R.mipmap.ic_ng_avatar);

        helper.setOnClickListener(R.id.ll_item, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.startActivity(new Intent(mContext, UserInfoDetailActivity.class)
                        .putExtra("friendUserId", item.getFriendUserId())
                        .putExtra("from", "3"));
            }
        });
    }
}
