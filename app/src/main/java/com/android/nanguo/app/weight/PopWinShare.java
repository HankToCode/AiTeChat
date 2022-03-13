package com.android.nanguo.app.weight;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;

import androidx.core.content.ContextCompat;

import com.android.nanguo.R;
import com.zds.base.util.Utils;


public class PopWinShare extends PopupWindow {
    private final View mainView;
    private final LinearLayout layoutGroup;
    private final LinearLayout layoutAddFirend;
    private final LinearLayout layoutSaoyisao;
    private final LinearLayout llMyQr;
    private final LinearLayout llTop;
    private final LinearLayout llParent;

    public PopWinShare(Activity paramActivity, View.OnClickListener paramOnClickListener, int marginTop, int marginEnd) {
        super(paramActivity);
        //窗口布局
        mainView = LayoutInflater.from(paramActivity).inflate(R.layout.popwin_share, null);
        layoutGroup = ((LinearLayout) mainView.findViewById(R.id.layout_group));
        llParent = ((LinearLayout) mainView.findViewById(R.id.llParent));
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
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置高度
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);
        //设置显示隐藏动画
//        setAnimationStyle(R.style.AnimTools);
        //设置背景透明
        setBackgroundDrawable(ContextCompat.getDrawable(Utils.getContext(), R.color.trans_half));

        llTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (this != null && isShowing()) {
                    dismiss();
                }
            }
        });

        LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) llParent.getLayoutParams();
        layoutParams.setMargins(0, marginTop, marginEnd, 0);
    }
}