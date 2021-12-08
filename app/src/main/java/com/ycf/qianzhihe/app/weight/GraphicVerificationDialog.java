package com.ycf.qianzhihe.app.weight;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.utils.XClickUtil;
import com.ycf.qianzhihe.common.utils.ToastUtils;
import com.zds.base.ImageLoad.GlideUtils;

import java.util.Random;

import butterknife.BindView;
import butterknife.ButterKnife;

public class GraphicVerificationDialog extends Dialog implements View.OnClickListener {

    @BindView(R.id.tv_negativeButton)
    TextView mTvNegativeButton;
    @BindView(R.id.tv_positiveButton)
    TextView mTvPositiveButton;
    @BindView(R.id.tv_title)
    TextView mTvTitle;

    @BindView(R.id.et_user_code)
    EditText mEtUserCode;
    @BindView(R.id.img_code)
    ImageView mImgCode;
    @BindView(R.id.tv_refresh)
    TextView mTvRefresh;

    private Context mContext;

    //在构造方法里预加载我们的样式，这样就不用每次创建都指定样式了
    public GraphicVerificationDialog(Context context) {
        this(context, R.style.MyDialog);

    }

    public GraphicVerificationDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        setContentView(R.layout.dialog_graphic_verification);
        ButterKnife.bind(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        flushTy();

        mTvRefresh.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_refresh) {
            if (!XClickUtil.isFastDoubleClick(v, 1500)) {
                flushTy();
            } else {
                ToastUtils.showToast("请勿连续点击");
            }
        }
    }

    public String getCode() {
        return mEtUserCode.getText().toString().trim();
    }


    private int flag;

    /**
     * 刷新图形验证码
     */
    private void flushTy() {
        flag = new Random().nextInt(99999);
        if (flag < 10000) {
            flag += 10000;
        }
        GlideUtils.loadImageViewLoding(AppConfig.tuxingCode + "?random=" + flag, mImgCode);
    }


    /**
     * 设置标题栏
     */
    public GraphicVerificationDialog setTitle(String title) {
        if (title != null) {
            mTvTitle.setText(title);
        }
        return this;
    }

    /**
     * 设置右边确定点击按钮
     *
     * @param text     按钮上的显示字
     * @param listener 点击事件监听
     */
    public GraphicVerificationDialog setPositiveButton(final String text, final OnMyDialogButtonClickListener listener) {
        if (text != null) {
            mTvPositiveButton.setText(text);
        }
        mTvPositiveButton.setOnClickListener(view -> {
            if (listener != null) {
                listener.onClick(this);
            }
            dismiss();
        });
        return this;
    }

    public interface OnMyDialogButtonClickListener {
        void onClick(GraphicVerificationDialog dialog);
    }


    /**
     * 设置右边确定点击按钮
     * 默认 点击 字体 为 确定
     *
     * @param listener 点击事件监听
     */
    public GraphicVerificationDialog setPositiveButton(final OnMyDialogButtonClickListener listener) {

        return setPositiveButton(null, listener);
    }

    /**
     * 设置左边取消点击按钮
     *
     * @param text     按钮上的显示字
     * @param listener 点击事件监听
     */
    public GraphicVerificationDialog setNegativeButton(String text, final OnMyDialogButtonClickListener listener) {
        if (text != null) {
            mTvNegativeButton.setText(text);
        }
        mTvNegativeButton.setOnClickListener(view -> {
            if (listener != null) {
                listener.onClick(this);
            }
            dismiss();
        });
        return this;
    }

    /**
     * 设置右边取消点击按钮
     * 默认 点击 字体 为 取消
     *
     * @param listener 点击事件监听
     */
    public GraphicVerificationDialog setNegativeButton(final OnMyDialogButtonClickListener listener) {
        return setNegativeButton(null, listener);
    }

    /**
     * 设置是否可以取消dialog，由于直接使用setCancelable返回的是Dialog，所以自定义方法
     */
    public GraphicVerificationDialog setDialogCancelable(boolean flag) {
        setCancelable(flag);
        return this;
    }


}