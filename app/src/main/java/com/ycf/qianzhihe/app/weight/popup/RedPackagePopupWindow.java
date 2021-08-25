package com.ycf.qianzhihe.app.weight.popup;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.ColorDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.hyphenate.easeui.modules.interfaces.IPopupWindow;
import com.wx.wheelview.adapter.ArrayWheelAdapter;
import com.wx.wheelview.widget.WheelView;
import com.ycf.qianzhihe.R;

import java.util.ArrayList;
import java.util.List;

public class RedPackagePopupWindow extends PopupWindow {
    private Context context;
    private CallBack callBack;
    private int currentRedPackageMethod;

    public RedPackagePopupWindow(Context context, CallBack callBack, int currentRedPackageMethod) {
        super(context);
        this.context = context;
        this.callBack = callBack;
        this.currentRedPackageMethod = currentRedPackageMethod;
        try {
            setFocusable(true);
            setTouchable(true);
            setOutsideTouchable(true);
            setBackgroundDrawable(new ColorDrawable(context.getResources().getColor(android.R.color.transparent)));
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    public void show(Activity content, View locationView, List<String> list) {
        LayoutInflater layoutInflater = LayoutInflater.from(content);
        ViewGroup rootView = (ViewGroup) layoutInflater.inflate(R.layout.dialog_red_package_pop, null);


        WheelView<String> wheelView = rootView.findViewById(R.id.wheelView);
        wheelView.setWheelAdapter(new ArrayWheelAdapter(context)); // 文本数据源
        wheelView.setSkin(WheelView.Skin.Holo);

        wheelView.setWheelData(list);
        wheelView.setSelection(currentRedPackageMethod);


        rootView.findViewById(R.id.tvCancel).setOnClickListener(view -> dismiss());
        rootView.findViewById(R.id.tvConfirm).setOnClickListener(view -> {

            if (callBack != null) {
                callBack.onSelection(wheelView.getCurrentPosition());
            }
            dismiss();
        });

        // 把菜单都添加进去
        setContentView(rootView);
        setWindowLayoutMode(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.MATCH_PARENT);

        showAtLocation(locationView, Gravity.CENTER, 0, 0);
    }

    public interface CallBack {
        void onSelection(int selection);
    }

}
