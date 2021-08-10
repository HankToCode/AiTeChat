package com.hyphenate.easeim.common.utils.log

import android.util.Log
import com.hyphenate.easeim.BuildConfig

/**
 *
 * Author: 无人认领
 * Date: 2018/11/27 10:24
 * Description:
 **/
class LogUtils {


    companion object {
        val showNormalLog = BuildConfig.DEBUG
        /**
         * 为大log使用
         * log最大输出为4000(包含tag得长度)
         * tag长度未知，所以分段输出
         *
         * @param tag
         * @param content
         */
        fun largeLogD(tag: String, content: String) {
            if (showNormalLog)
                if (content.length > 3000) {
                    d(tag, content.substring(0, 3000))
                    largeLogD(tag, content.substring(3000))
                } else {
                    d(tag, content)
                }
        }

        fun largeLogE(tag: String, content: String) {
            if (content.length > 3000) {
                e(tag, content.substring(0, 3000))
                largeLogE(tag, content.substring(3000))
            } else {
                e(tag, content)
            }
        }

        fun d(tag: String, msg: String?) {
            if (showNormalLog)
                Log.d(tag, msg ?: "msg is null !!!")
        }

        fun e(tag: String, msg: String?) {
            if (showNormalLog)
                Log.e(tag, msg ?: "msg is null !!!")
        }
    }

}
