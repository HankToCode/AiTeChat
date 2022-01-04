package com.android.nanguo.common.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.nanguo.R;
import com.android.nanguo.common.utils.AppUtil;


/**
 * 自定义普通的单行样式布局
 */
public class ItemInfoView extends LinearLayout {

    private TextView tvLeft, tvRight, tvLeftLayer;
    private ImageView ivRight, ivLeft;

    private boolean isCheck = false;


    public ItemInfoView(Context context) {
        this(context, null);
    }

    public ItemInfoView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ItemInfoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }


    private void init(Context context, AttributeSet attrs) {

        LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        setLayoutParams(params);


        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.ItemInfoView);

        String leftText = ta.getString(R.styleable.ItemInfoView_left_text);//左文本
        String rightText = ta.getString(R.styleable.ItemInfoView_right_text);//右文本
        Integer rightTextColor = ta.getInteger(R.styleable.ItemInfoView_right_text_color, AppUtil.getColorId(context, R.color.gray_e5));//右文本颜色
        Integer leftTextColor = ta.getInteger(R.styleable.ItemInfoView_left_text_color, AppUtil.getColorId(context, R.color.black));//左文本颜色
        Float font_14 = context.getResources().getDimension(R.dimen.dp_14);//默认大小
        Float rightTextSize = ta.getDimension(R.styleable.ItemInfoView_right_text_size, font_14);//右文本大小
        Float leftTextSize = ta.getDimension(R.styleable.ItemInfoView_left_text_size, font_14);//左文本大小
        Integer backgroundColor = ta.getInteger(R.styleable.ItemInfoView_background_color, AppUtil.getColorId(context, R.color.white));//背景颜色
        String rightHint = ta.getString(R.styleable.ItemInfoView_right_hint);//右文本提示
        boolean arrow = ta.getBoolean(R.styleable.ItemInfoView_arrow, false);//是否显示导向标志
        boolean isLeftSingleLine = ta.getBoolean(R.styleable.ItemInfoView_left_single_line, false);//左文本是否单行
        boolean isRightSingleLine = ta.getBoolean(R.styleable.ItemInfoView_right_single_line, false);//左文本是否单行
        boolean isDoubleLayer = ta.getBoolean(R.styleable.ItemInfoView_double_layer, false);//左文本是否双层


        Drawable iconDrawable = ta.getDrawable(R.styleable.ItemInfoView_icon_drawable);//左文本左边icon
        Drawable endDrawable = ta.getDrawable(R.styleable.ItemInfoView_end_drawable);//右文本右边icon
        Drawable endImage = ta.getDrawable(R.styleable.ItemInfoView_end_image);//居右的图片
        Drawable iconAfterDrawable = ta.getDrawable(R.styleable.ItemInfoView_icon_after_drawable);//左文本左边icon

        ta.recycle();

        initLeftText(leftText, isLeftSingleLine, isDoubleLayer, iconDrawable, iconAfterDrawable, leftTextColor, leftTextSize);

        initRightText(rightText, isRightSingleLine, rightHint, rightTextColor, arrow, endDrawable, rightTextSize);

        if (endImage != null) {
            LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
            ivRight = new ImageView(context);
            ivRight.setImageDrawable(endImage);
            lp.gravity = Gravity.CENTER_VERTICAL | Gravity.RIGHT;
//            lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT, RelativeLayout.TRUE);
//            lp.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
            addView(ivRight, lp);
        }

        setBackgroundColor(backgroundColor);
        setClickable(true);
    }

    public void initRightText(String rightText, boolean isRightSingleLine, String rightHint, Integer rightTextColor,
                              boolean arrow, Drawable endDrawable, float rightTextSize) {
        LayoutParams lp2 = new LayoutParams(0, LayoutParams.WRAP_CONTENT, 1);
        tvRight = new TextView(getContext());
        tvRight.setId(R.id.text2);
        tvRight.setText(rightText);
        tvRight.setHint(rightHint);
        tvRight.setTextColor(rightTextColor);
        tvRight.getPaint().setTextSize(rightTextSize);
        tvRight.setGravity(Gravity.RIGHT);

        if (isRightSingleLine) {
            tvRight.setSingleLine(true);
            tvRight.setEllipsize(TextUtils.TruncateAt.END);
        }
        if (arrow) {
            if (endDrawable == null) {
                endDrawable = getResources().getDrawable(R.mipmap.ic_right_arrow);
            }
        }
        setRightTextDrawable(endDrawable);

        lp2.gravity = Gravity.CENTER_VERTICAL;
        addView(tvRight, lp2);

    }

    public void initLeftText(String leftText, boolean isLeftSingleLine, boolean isDoubleLayer, Drawable iconDrawable, Drawable iconAfterDrawable,
                             Integer leftTextColor, float leftTextSize) {

        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        lp.gravity = Gravity.CENTER_VERTICAL;
        lp.setMargins(0, 0, 10, 0);

        tvLeft = new TextView(getContext());
        tvLeftLayer = new TextView(getContext());
        tvLeft.setTextColor(leftTextColor);
        tvLeft.setGravity(Gravity.CENTER_VERTICAL | Gravity.START);
        tvLeft.setTextSize(TypedValue.COMPLEX_UNIT_PX, leftTextSize);

        tvLeft.setText(leftText);
        ivLeft = new ImageView(getContext());

        if (isLeftSingleLine) {
            tvLeft.setSingleLine(true);
            tvLeft.setEllipsize(TextUtils.TruncateAt.END);
        }

        LinearLayout iconLinearLayout = new LinearLayout(getContext());
        iconLinearLayout.setOrientation(LinearLayout.HORIZONTAL);
        iconLinearLayout.setGravity(Gravity.CENTER_VERTICAL);

        //添加左文本左边的ICON图片
        LayoutParams ivLeftLayoutParmas = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        ivLeftLayoutParmas.setMargins(0, 0, 20, 0);
        setIconDrawable(iconDrawable);
        iconLinearLayout.addView(ivLeft, ivLeftLayoutParmas);

        //添加左文本右边的ICON图片
        setIconAfterDrawable(iconAfterDrawable);

        if (isDoubleLayer) {
            LinearLayout linearLayout = new LinearLayout(getContext());
            linearLayout.setOrientation(LinearLayout.VERTICAL);
            linearLayout.setGravity(Gravity.CENTER_VERTICAL);
            linearLayout.addView(tvLeft);
            linearLayout.addView(tvLeftLayer);
            iconLinearLayout.addView(linearLayout);
            addView(iconLinearLayout, lp);
        } else {
            iconLinearLayout.addView(tvLeft);
            addView(iconLinearLayout, lp);
        }
    }

    public void setIconAfterDrawable(Drawable iconAfterDrawable) {
        if (iconAfterDrawable != null) {
            iconAfterDrawable.setBounds(0, 0, iconAfterDrawable.getMinimumWidth(), iconAfterDrawable.getMinimumHeight());
        }
        tvLeft.setCompoundDrawablePadding(AppUtil.dp2Px(getContext(), 8));
        Drawable[] drawables = tvLeft.getCompoundDrawables();
        tvLeft.setCompoundDrawables(drawables[0], drawables[1], iconAfterDrawable, drawables[3]);
    }

    public void setIconDrawable(Drawable iconDrawable) {
        if (iconDrawable != null) {
            ivLeft.setVisibility(VISIBLE);
            ivLeft.setImageDrawable(iconDrawable);
        } else {
            ivLeft.setVisibility(GONE);
        }
    }

    public void setRightText(Object obj) {
        if (obj instanceof String) {
            tvRight.setText((String) obj);
        } else if (obj instanceof Integer) {
            tvRight.setText(this.getContext().getResources().getString((Integer) obj));
        } else if (obj instanceof SpannableStringBuilder) {
            tvRight.setText((SpannableStringBuilder) obj);
        }
    }

    public void setRightTextColor(int textColor) {
        tvRight.setTextColor(textColor);
    }

    public void setRightTextSingleLine(boolean isSingleLine) {
        tvRight.setSingleLine(isSingleLine);
    }

    public void setRightTextDrawable(Drawable endDrawable) {
        if (endDrawable != null) {
            endDrawable.setBounds(0, 0, endDrawable.getMinimumWidth(), endDrawable.getMinimumHeight());
            tvRight.setCompoundDrawablePadding(AppUtil.dp2Px(getContext(), 8));
        }
        tvRight.setCompoundDrawables(null, null, endDrawable, null);
    }

    public void setLeftText(String text) {
        tvLeft.setText(text);
    }

    public void setRightSelect(boolean isSelect) {
        ivRight.setSelected(isSelect);
    }

    public TextView getTvRight() {
        return tvRight;
    }

    public TextView getTvLeft() {
        return tvLeft;
    }

    public TextView getTvLeftLayer() {
        return tvLeftLayer;
    }
}