package com.hyphenate.easeim.app.weight;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.core.content.ContextCompat;

import com.hyphenate.easeim.R;
import com.zds.base.util.Utils;


public class PopWinShare extends PopupWindow {
    private View mainView;
    private LinearLayout layoutGroup, layoutAddFirend, layoutSaoyisao, llMyQr, llTop;
    private Activity paramActivity;

    public PopWinShare(Activity paramActivity, View.OnClickListener paramOnClickListener, int paramInt1, int paramInt2) {
        super(paramActivity);
        this.paramActivity = paramActivity;
        //窗口布局
        mainView = LayoutInflater.from(paramActivity).inflate(R.layout.popwin_share, null);
        layoutGroup = ((LinearLayout) mainView.findViewById(R.id.layout_group));
        layoutAddFirend = (LinearLayout) mainView.findViewById(R.id.layout_add_firend);
        layoutSaoyisao = (LinearLayout) mainView.findViewById(R.id.layout_saoyisao);
        llMyQr = (LinearLayout) mainView.findViewById(R.id.layout_my_qr);
        llTop = (LinearLayout) mainView.findViewById(R.id.ll_top);

        //设置每个子布局的事件监听器
        if (paramOnClickListener != null) {
            layoutGroup.setOnClickListener(paramOnClickListener);
            layoutAddFirend.setOnClickListener(paramOnClickListener);
            layoutSaoyisao.setOnClickListener(paramOnClickListener);
            llMyQr.setOnClickListener(paramOnClickListener);

        }
        setContentView(mainView);
        //设置宽度
        setWidth(paramInt1);
        //设置高度
        setHeight(paramInt2);
        //设置显示隐藏动画
//        setAnimationStyle(R.style.AnimTools);
        //设置背景透明
        setBackgroundDrawable(ContextCompat.getDrawable(Utils.getContext(), R.color.transparent));

        llTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (this != null && isShowing()) {
                    dismiss();
                }
            }
        });
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
        setWindowBrightness(0.3f);
    }

    @Override
    public void dismiss() {
        setWindowBrightness(1f);
        super.dismiss();
    }

    private void setWindowBrightness(float brightness) {
        Window window = paramActivity.getWindow();
        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness * 255.0f;
        window.setAttributes(lp);
    }
}