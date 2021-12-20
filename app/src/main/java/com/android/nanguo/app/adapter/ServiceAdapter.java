package com.android.nanguo.app.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hyphenate.easeui.widget.EaseImageView;
import com.android.nanguo.R;
import com.android.nanguo.app.api.old_data.ServiceInfo;
import com.android.nanguo.app.utils.ImageUtil;

import java.util.List;

/**
 * 客服中心
 *
 * @author Administrator
 */
public class ServiceAdapter extends BaseQuickAdapter<ServiceInfo.DataBean, BaseViewHolder> {

    public ServiceAdapter(List<ServiceInfo.DataBean> list) {
        super(R.layout.adapter_service_list, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, ServiceInfo.DataBean item) {
        ImageUtil.setAvatar((EaseImageView) helper.getView(R.id.img_head));
        helper.setText(R.id.tv_title, item.getNickName());
        helper.setGone(R.id.tv_number, false);
//        GlideUtils.loadImageViewLoding(AppConfig.checkimg(item.getUserHead()) , (EaseImageView) helper.getView(R.id.img_head), R.mipmap.ic_ng_avatar);
        helper.setImageResource(R.id.img_head, R.mipmap.icon_exception_handle_kefu_avatar);
    }
}