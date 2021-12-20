package com.android.nanguo.common.image

import android.app.Dialog
import android.content.Context
import android.graphics.drawable.ColorDrawable
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import com.android.nanguo.R

class HintDialog(context: Context) : Dialog(context), View.OnClickListener {
    companion object {
        /**
         * 左边按钮点击回调
         */
        const val ONCLICK_LEFT = 100
        /**
         * 右边按钮点击回调
         */
        const val ONCLICK_RIGHT = 200
        /**
         * 单按钮点击回调
         */
        const val ONCLICK_CENTER = 300
    }

    private var tvTitle: TextView
    private var tvHint: TextView
    private var view: View
    private var llLeftRight: LinearLayout
    private var linearLayout: LinearLayout
    private var btLeft: Button
    private var btRight: Button
    private var btCenter: Button

    private var mHintDialogCallBack: HintDialogCallBack? = null


    init {
        setContentView(R.layout.dialog_hint)
        window?.setBackgroundDrawable(ColorDrawable())
        window?.attributes?.width = ViewGroup.LayoutParams.MATCH_PARENT

        tvTitle = findViewById(R.id.tvTitle)
        tvHint = findViewById(R.id.tv_hint)
        view = findViewById(R.id.view)
        llLeftRight = findViewById(R.id.llLeftRight)
        linearLayout = findViewById(R.id.ll_hint_area)
        btLeft = findViewById(R.id.btLeft)
        btRight = findViewById(R.id.btRight)
        btCenter = findViewById(R.id.btCenter)

        btLeft.setOnClickListener(this)
        btRight.setOnClickListener(this)
        btCenter.setOnClickListener(this)

        setTitle("温馨提示")
        setButtonTextLeftRight("取消", "确定")
        setButtonTextCenter("确定")
        setCenterButtonVisiblity(false)
    }

    /**
     * 设置标题
     * @param title 标题
     */
    fun setTitle(title: String) {
        tvTitle.text = title
    }

    /**
     * 设置提示
     * @param hint 提示
     */
    fun setHint(hint: String) {
        tvHint.text = hint
        linearLayout.visibility = if (hint.isNotEmpty()) {
            View.VISIBLE
        } else {
            View.GONE
        }
    }

    /**
     * 设置按钮文本
     * @param left 左按钮文本
     * @param right 右按钮文本
     */
    fun setButtonTextLeftRight(left: String, right: String) {
        btLeft.text = left
        btRight.text = right
    }

    /**
     * 设置单按钮按钮文本
     * @param center 单按文本
     */
    fun setButtonTextCenter(center: String) {
        btCenter.text = center
    }

    /**
     * 设置单按钮是否显示
     * @param  visible true:显示，false:不显示
     */
    fun setCenterButtonVisiblity(visible: Boolean) {
        if (visible) {
            btCenter.visibility = View.VISIBLE
            llLeftRight.visibility = View.GONE
            view.visibility = View.GONE
        } else {
            btCenter.visibility = View.GONE
            llLeftRight.visibility = View.VISIBLE
            view.visibility = View.VISIBLE
        }
    }


    /**
     * 设置回调
     */
    fun setHintDialogCallBack(hintDialogCallBack: HintDialogCallBack) {
        this.mHintDialogCallBack = hintDialogCallBack
    }

    override fun onClick(v: View) {
        dismiss()
        when (v) {
            btLeft -> {
                mHintDialogCallBack?.hintDialogCallBack(this, ONCLICK_LEFT)
            }
            btRight -> {
                mHintDialogCallBack?.hintDialogCallBack(this, ONCLICK_RIGHT)
            }
            btCenter -> {
                mHintDialogCallBack?.hintDialogCallBack(this, ONCLICK_CENTER)
            }
        }
    }


    interface HintDialogCallBack {
        /**
         * 点击回调
         * @param dialog  Dialog实体
         * @param onClick [HintDialog.ONCLICK_LEFT]，[HintDialog.ONCLICK_RIGHT]，[HintDialog.ONCLICK_CENTER]
         */
        fun hintDialogCallBack(dialog: Dialog, onClick: Int)
    }
    }
