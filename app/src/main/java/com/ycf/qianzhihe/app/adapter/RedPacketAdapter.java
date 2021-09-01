package com.ycf.qianzhihe.app.adapter;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.hyphenate.easeui.widget.EaseImageView;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.utils.ImageUtil;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.util.StringUtil;
import com.ycf.qianzhihe.app.api.old_data.RedPacketInfo;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;

import java.util.List;

/**
 * 红包详情adapter
 */
public class RedPacketAdapter extends BaseQuickAdapter<RedPacketInfo.RedPacketDetailListBean, BaseViewHolder> {
    /**
     * 是否结束
     */
    private boolean isfirsh;

    public RedPacketAdapter(List<RedPacketInfo.RedPacketDetailListBean> list) {
        super(R.layout.adapter_item_redpacket, list);
    }

    @Override
    protected void convert(BaseViewHolder helper, RedPacketInfo.RedPacketDetailListBean item) {
        helper.setText(R.id.tv_name, item.getRobUserName());
        try {
            helper.setText(R.id.tv_time, StringUtil.formatDateMinute(item.getRobTime(),"HH:mm:ss"));
        } catch (Exception e) {
        }

        // luckFlag 手气最佳 0-不是 1-是
        if ("1".equals(item.getLuckFlag())) {
            helper.setGone(R.id.tv_cailei, true);
            if (!isfirsh) {
                StringBuilder sb = new StringBuilder(StringUtil.getFormatValue2(item.getMoney()));
                helper.setText(R.id.tv_money, sb.toString() + mContext.getResources().getString(R.string.glod));
            } else {
                helper.setText(R.id.tv_money, StringUtil.getFormatValue2(item.getMoney()) + mContext.getResources().getString(R.string.glod));

            }

        } else {
            helper.setGone(R.id.tv_cailei, false);
            helper.setText(R.id.tv_money, StringUtil.getFormatValue2(item.getMoney()) + mContext.getResources().getString(R.string.glod));
        }

        ImageUtil.setAvatar((EaseImageView) helper.getView(R.id.avatar_user));
        GlideUtils.loadRoundCircleImage(AppConfig.checkimg(item.getRobUserHead()), (EaseImageView) helper.getView(R.id.avatar_user), R.mipmap.img_default_avatar, 10);
    }


    public void setIsfirsh(boolean isfirsh) {
        this.isfirsh = isfirsh;
    }
}