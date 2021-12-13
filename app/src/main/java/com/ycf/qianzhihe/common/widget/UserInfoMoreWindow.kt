package com.ycf.qianzhihe.common.widget

import android.app.Activity
import android.graphics.drawable.BitmapDrawable
import android.view.*
import android.widget.*
import com.blankj.utilcode.util.ToastUtils
import com.coorchice.library.SuperTextView
import com.ycf.qianzhihe.R
import com.ycf.qianzhihe.section.account.activity.UserInfoDetailActivity

/**
 * author:       Shenme
 * date:         2020/09/1
 * description: 赠送特权
 */
class UserInfoMoreWindow : PopupWindow, View.OnClickListener {

    private var popupWindow: PopupWindow

    private var ivBack: ImageView

    private var llOption1: LinearLayout
    private var llOption2: LinearLayout
    private var llOption3: LinearLayout
    private var tvOptions1: TextView
    private var tvOptions2: TextView
    private var tvOptions3: TextView
    private var tvOptions4: TextView
    private var tvOptions5: TextView
    private var tvOptions6: TextView
    private var tvOptions7: TextView
    private var tvOptions8: TextView
    private var tvOptions9: TextView

    private var calback: (target: Int) -> Unit = {}

    private var userInterface: UserInfoDetailActivity.UserInfoDetailInfoInterface

    constructor(
        activity: Activity,
        calback: (target: Int) -> Unit,
        userInterface: UserInfoDetailActivity.UserInfoDetailInfoInterface
    ) {
        this.calback = calback
        this.userInterface = userInterface

        val layoutInflater = LayoutInflater.from(activity)
        val view = layoutInflater.inflate(R.layout.popupwindow_user_info_more, null)

        popupWindow = PopupWindow(
            view,
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )
        popupWindow.softInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
        popupWindow.isFocusable = true// 取得焦点
        //注意  要是点击外部空白处弹框消息  那么必须给弹框设置一个背景色  不然是不起作用的
        popupWindow.setBackgroundDrawable(BitmapDrawable())
        //点击外部消失
        popupWindow.isOutsideTouchable = true
        //设置可以点击
        popupWindow.isTouchable = true
//        //进入退出的动画
        popupWindow.animationStyle = R.style.AnimationPopupwindow
        val params = activity.window.attributes
        params.alpha = 0.7f
        activity.window.attributes = params
        popupWindow.showAtLocation(view, Gravity.BOTTOM, 0, 0)
        popupWindow.setOnDismissListener {
            val paramsa = activity.window.attributes
            paramsa.alpha = 1f
            activity.window.attributes = paramsa
        }


        ivBack = view.findViewById(R.id.ivBack)
        llOption1 = view.findViewById(R.id.llOption1)
        llOption2 = view.findViewById(R.id.llOption2)
        llOption3 = view.findViewById(R.id.llOption3)


        tvOptions1 = view.findViewById(R.id.tvOptions1)
        tvOptions2 = view.findViewById(R.id.tvOptions2)
        tvOptions3 = view.findViewById(R.id.tvOptions3)
        tvOptions4 = view.findViewById(R.id.tvOptions4)
        tvOptions5 = view.findViewById(R.id.tvOptions5)
        tvOptions6 = view.findViewById(R.id.tvOptions6)
        tvOptions7 = view.findViewById(R.id.tvOptions7)
        tvOptions8 = view.findViewById(R.id.tvOptions8)
        tvOptions9 = view.findViewById(R.id.tvOptions9)

        ivBack.setOnClickListener(this)
        tvOptions1.setOnClickListener(this)
        tvOptions2.setOnClickListener(this)
        tvOptions3.setOnClickListener(this)
        tvOptions4.setOnClickListener(this)
        tvOptions5.setOnClickListener(this)
        tvOptions6.setOnClickListener(this)
        tvOptions7.setOnClickListener(this)
        tvOptions8.setOnClickListener(this)
        tvOptions9.setOnClickListener(this)

        tvOptions1.isSelected = userInterface.isMute
        tvOptions7.isSelected = userInterface.isMute
        tvOptions5.isSelected = userInterface.isBlack

        if (userInterface.isFriend || userInterface.isUserRank) {
            llOption1.visibility = View.VISIBLE
            llOption2.visibility = View.VISIBLE
            llOption3.visibility = View.GONE
        } else {
            llOption1.visibility = View.GONE
            llOption2.visibility = View.GONE
            llOption3.visibility = View.VISIBLE
        }


    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.ivBack -> {
                popupWindow.dismiss()
            }
            R.id.tvOptions1 -> {
                if (!userInterface.isUserRank) {
                    ToastUtils.showLong("您无此权限")
                    return
                }
                tvOptions1.isSelected = !tvOptions1.isSelected
                calback.invoke(1)

            }
            R.id.tvOptions2 -> {
                if (!userInterface.isUserRank) {
                    ToastUtils.showLong("您无此权限")
                    return
                }
                calback.invoke(2)
            }
            R.id.tvOptions3 -> {
                calback.invoke(3)
            }
            R.id.tvOptions4 -> {
                calback.invoke(4)
            }
            R.id.tvOptions5 -> {
                if (!userInterface.isFriend) {
                    ToastUtils.showLong("您并非他好友")
                    return
                }
                tvOptions5.isSelected = !tvOptions5.isSelected
                calback.invoke(5)
            }
            R.id.tvOptions6 -> {
                calback.invoke(6)
            }
            R.id.tvOptions7 -> {
                if (!userInterface.isUserRank) {
                    ToastUtils.showLong("您无此权限")
                    return
                }
                tvOptions7.isSelected = !tvOptions7.isSelected
                calback.invoke(7)
            }
            R.id.tvOptions8 -> {
                if (!userInterface.isUserRank) {
                    ToastUtils.showLong("您无此权限")
                    return
                }
                calback.invoke(8)
            }
            R.id.tvOptions9 -> {
                calback.invoke(9)
            }
        }
    }


}