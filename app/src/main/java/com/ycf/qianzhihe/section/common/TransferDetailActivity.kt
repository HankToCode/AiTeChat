package com.ycf.qianzhihe.section.common

import android.content.Intent
import android.os.Bundle
import com.ycf.qianzhihe.R
import com.ycf.qianzhihe.app.api.old_data.EventCenter
import com.ycf.qianzhihe.app.api.old_http.ApiClient
import com.ycf.qianzhihe.app.api.old_http.AppConfig
import com.ycf.qianzhihe.app.api.old_http.ResultListener
import com.ycf.qianzhihe.app.base.BaseInitActivity
import com.zds.base.Toast.ToastUtil.toast
import kotlinx.android.synthetic.main.activity_transfer_detail.*
import kotlinx.android.synthetic.main.toolbar_layout.*
import java.util.*

/**
 *@创建者 Mr.zou
 *@创建时间 2020/2/25
 *@描述
 */
class TransferDetailActivity : BaseInitActivity() {

    private var status: String? = null
    private var money: String? = null
    private var turnId: String? = null
    private var serialNumber: String? = null
    private var requestId: String? = null
    private var isSelf: Boolean? = false

    override fun onEventComing(center: EventCenter<*>?) {
    }

    override fun getLayoutId(): Int  = R.layout.activity_transfer_detail

    override fun initIntent(intent: Intent?) {
        super.initIntent(intent)
        val extras = intent?.extras
        status = extras?.getString("status", "")
        money = extras?.getString("money", "")
        turnId = extras?.getString("turnId", "")
        serialNumber = extras?.getString("serialNumber", "")
        requestId = extras?.getString("requestId", "")

        isSelf = extras?.getBoolean("isSelf", false)
    }

    override fun initView(savedInstanceState: Bundle?) {
        super.initView(savedInstanceState)
        toolbar_title.text = "转账"
        tv_money.text = if (money.isNullOrEmpty()) "￥ 0.00" else "￥ $money"
        viewUI()
        tv_collection.setOnClickListener {
            checkTransfer()
        }
    }


    private fun viewUI() {
        when (status) {
            "0" -> {
                tv_status.text="待确认收款"
                tv_collection.setBackgroundResource(if (isSelf == false) R.drawable.group_search else R.drawable.group_search_gray)
                tv_collection.isEnabled = isSelf == false
                img_status.setImageResource(R.mipmap.daiqueren)
            }
            "1" -> {
                tv_status.text="已确认收款"
                tv_collection.setBackgroundResource(R.drawable.group_search_gray)
                tv_collection.isEnabled = isSelf == false
                img_status.setImageResource(R.drawable.yx)
            }
            "2" -> {
                tv_status.text="已退回"
                tv_collection.setBackgroundResource(R.drawable.group_search_gray)
                tv_collection.isEnabled = isSelf == false
                img_status.setImageResource(R.drawable.yx)
            }
        }
    }

    private fun confirmMoney() {
        val map = mapOf("transferId" to turnId)
        ApiClient.requestNetHandle(this, AppConfig.CONFIRM, "请稍后...", map, object : ResultListener() {
            override fun onSuccess(json: String?, msg: String?) {
                status = "1"
                viewUI()
            }

            override fun onFailure(msg: String?) {
                toast(msg)
            }
        })
    }

    /**
     * 检测转账
     *
     * @param message
     */
    private fun checkTransfer() {
        val map = mapOf("transferId" to turnId)
        ApiClient.requestNetHandle(this@TransferDetailActivity, AppConfig.TRANSFER_STATUS, "", map, object : ResultListener() {
            override fun onSuccess(json: String, msg: String) {
                val j = json.replace(".0", "")
                status = j
                when (j) {
                    "0" -> {
                        confirmMoney()
                    }
                    "1" -> {
//                        toast("已领取")
                        viewUI()
                        return
                    }
                    "2" -> {
//                        toast("金额已退回")
                        viewUI()
                        return
                    }

                }
            }

            override fun onFailure(msg: String) {
                toast(msg)
            }
        })
    }


}