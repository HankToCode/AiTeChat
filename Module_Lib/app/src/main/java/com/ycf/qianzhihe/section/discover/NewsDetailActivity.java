package com.ycf.qianzhihe.section.discover;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.hyphenate.easeui.widget.EaseTitleBar;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.R2;
import com.ycf.qianzhihe.app.api.EaseConstant;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.section.account.activity.MineActivity;
import com.ycf.qianzhihe.section.chat.activity.ChatActivity;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.util.StringUtil;

import butterknife.BindView;

public class NewsDetailActivity extends BaseInitActivity {

    @BindView(R2.id.tv_content)
    TextView tv_content;
    @BindView(R2.id.tv_time)
    TextView tv_time;
    @BindView(R2.id.tv_title)
    TextView tv_title;
    @BindView(R2.id.title_bar)
    EaseTitleBar title_bar;
    @BindView(R2.id.iv_img)
    ImageView iv_img;
    @Override
    protected int getLayoutId() {
        return R.layout.activity_news_detail;
    }

    private String title,img,content;
    private long time;

    public static void actionStart(Context context, String title, String coverImg, String content, long time) {
        Intent intent = new Intent(context, NewsDetailActivity.class);
        intent.putExtra("title", title);
        intent.putExtra("img", coverImg);
        intent.putExtra("content", content);
        intent.putExtra("time", time);
        context.startActivity(intent);
    }

    @Override
    protected void initIntent(Intent intent) {
        super.initIntent(intent);
        title = intent.getStringExtra("title");
        img = intent.getStringExtra("img");
        content = intent.getStringExtra("content");
        time = intent.getLongExtra("time",0);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        title_bar.setOnBackPressListener(view -> finish());
        tv_title.setText(title);
        tv_time.setText(StringUtil.formatDateMinute(time, ""));
        GlideUtils.loadImageViewLoding(img, iv_img, R.mipmap.em_logo_uidemo);
        tv_content.setText(content);
    }
}
