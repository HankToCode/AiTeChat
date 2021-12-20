package com.android.nanguo.common.network.exception

import com.blankj.utilcode.util.LogUtils
import com.google.gson.JsonParseException

import org.json.JSONException

import java.net.ConnectException

import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.text.ParseException

/**
 * Created by 无人认领 on 2020/12/5.
 * desc: 异常处理类
 */

class ExceptionHandle {

    companion object {
        var errorCode = ErrorStatus.UNKNOWN_ERROR
        var errorMsg = "请求失败，请稍后重试"

        fun handleException(e: Throwable): ApiException {
//            e.printStackTrace()
            if (e is SocketTimeoutException) {//网络超时
                errorCode = ErrorStatus.NETWORK_ERROR
                errorMsg = "网络连接超时"
            } else if (e is ConnectException) { //均视为网络错误
                errorCode = ErrorStatus.NETWORK_ERROR
                errorMsg = "网络连接异常"
            } else if (e is JsonParseException
                    || e is JSONException
                    || e is ParseException) {   //均视为解析错误
                errorCode = ErrorStatus.SERVER_ERROR
                errorMsg = "数据解析异常"
            } else if (e is ApiException) {//服务器返回的错误信息
                errorCode = ErrorStatus.SERVER_ERROR
                errorMsg = e.errorMsg.toString()
                errorMsg = ""
            } else if (e is UnknownHostException) {
                errorCode = ErrorStatus.NETWORK_ERROR
                errorMsg = "网络连接异常"
            } else if (e is IllegalArgumentException) {
                errorCode = ErrorStatus.NETWORK_ERROR
                errorMsg = "参数错误"
            } else if (e is NetStatusException) {
                errorCode = e.getErrorCode()
                errorMsg = "加载失败，请检查网络"
            } else {//未知错误
                try {
                    LogUtils.e("TAG", "错误: " + e.message)
                } catch (e1: Exception) {
                    LogUtils.e("TAG", "未知错误Debug调试 ")
                }
                errorCode = ErrorStatus.UNKNOWN_ERROR
//                errorMsg = "未知错误"
                errorMsg = "${e.message}"
            }
            return ApiException("$errorCode", errorMsg)
        }

    }


}
