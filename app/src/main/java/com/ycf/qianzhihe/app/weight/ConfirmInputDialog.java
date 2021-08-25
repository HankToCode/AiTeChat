package com.ycf.qianzhihe.app.weight;

import android.app.Activity;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.ycf.qianzhihe.R;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.base.BaseDialog;

/**
 * 通用确定取消对话框
 */
public class ConfirmInputDialog extends BaseDialog {

    private TextView tv_title;
    private EditText et_content;
    private TextView tv_cancel;
    private TextView tv_confirm;

    private OnConfirmClickListener mOnConfirmClickListener;
    private OnCancelClickListener mOnCancelClickListener;

    public ConfirmInputDialog(Activity activity) {
        super(activity);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.dialog_input_confirm;
    }

    @Override
    protected void initView() {
        tv_title = findViewById(R.id.tv_title);
        et_content = findViewById(R.id.et_content);
        tv_cancel = findViewById(R.id.tv_cancel);
        tv_confirm = findViewById(R.id.tv_confirm);
    }

    @Override
    protected void initEventAndData() {
        tv_cancel.setOnClickListener(this);
        tv_confirm.setOnClickListener(this);
    }

    public void setTitle(String title) {
        tv_title.setText(title);
    }

    public void setContent(String content) {
        et_content.setText(content);
    }

    public void setButtonText(String leftButtonText, String rightButtonText) {
        tv_cancel.setText(leftButtonText);
        tv_confirm.setText(rightButtonText);
    }

    public void setButtonVisible(boolean leftButton, boolean rightButton) {
        tv_cancel.setVisibility(leftButton ? View.VISIBLE : View.GONE);
        tv_confirm.setVisibility(rightButton ? View.VISIBLE : View.GONE);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_cancel:
                dismiss();
                if (mOnCancelClickListener != null) {
                    mOnCancelClickListener.onCancelClick(tv_cancel);
                }
                break;
            case R.id.tv_confirm:
                if (TextUtils.isEmpty(et_content.getText().toString().trim())) {
                    ToastUtil.toast("请输入内容");
                    return;
                }
                if (mOnConfirmClickListener != null) {
                    mOnConfirmClickListener.onConfirmClick(et_content.getText().toString().trim());
                }
                dismiss();
                break;
        }
    }

    public void setOnConfirmClickListener(OnConfirmClickListener listener) {
        mOnConfirmClickListener = listener;
    }

    public interface OnConfirmClickListener {
        void onConfirmClick(String content);
    }

    public void setOnCancelClickListener(OnCancelClickListener listener) {
        mOnCancelClickListener = listener;
    }

    public interface OnCancelClickListener {
        void onCancelClick(View view);
    }

}
