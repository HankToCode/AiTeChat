package com.ycf.qianzhihe.app.adapter;

import android.content.Intent;
import android.view.View;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.old_data.BlackListInfo;
import com.ycf.qianzhihe.app.api.old_data.NewsBean;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.section.account.activity.UserInfoDetailActivity;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;

import java.util.List;

public class NewsAdapter extends BaseQuickAdapter<NewsBean, BaseViewHolder> {
    public NewsAdapter(@Nullable List<NewsBean> data) {
        super(R.layout.adapter_item_news, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, NewsBean item) {
        helper.setText(R.id.tv_title, item.getTitle());
//        GlideUtils.GlideLoadCircleErrorImageUtils(mContext, AppConfig.checkimg(item.getCoverImg()), helper.getView(R.id.iv_img), R.mipmap.em_logo_uidemo);
        GlideUtils.loadImageViewLoding(AppConfig.checkimg(AppConfig.checkimg(item.getCoverImg())), helper.getView(R.id.iv_img), R.mipmap.em_logo_uidemo);

        helper.setOnClickListener(R.id.ll_item, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ToastUtil.toast("111");
            }
        });
    }
}
