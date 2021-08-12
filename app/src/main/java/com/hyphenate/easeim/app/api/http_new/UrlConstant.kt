package com.hyphenate.easeim.app.api.http_new

import com.blankj.utilcode.util.SPUtils
import com.hyphenate.easeim.BuildConfig
import com.hyphenate.easeim.common.utils.toJsonString
import java.io.Serializable

/**
 * Created by 无人认领 on 2020/4/1.
 * name: 环境切换的URL常量
 * desc:
 * Tips:
 *  （retrofit需要加拦截器处理baseUrl）
 *
 */
object UrlConstant {


    const val HEADER_JSON_TYPE = "Content-Type: application/json;charset=UTF-8"
    const val HEADER_JSON_ACCEPT = "Accept: application/json"

    var CURRENT_DOMAIN = getCurrentHost()
    private var HTTP_HOST = CURRENT_DOMAIN.host

    private fun getCurrentHost(): Domain {
        return if (!BuildConfig.PROJECT_DEBUG) {
            Domain("生产服", "http://www.baixingmingjiang.com/", "")
        } else {
            Domain("测试服", "http://www.baixingmingjiang.com/", "")
        }
    }

    fun changeCurrentHost(domain: Domain) {
        if (!BuildConfig.PROJECT_DEBUG) {
        } else {
            CURRENT_DOMAIN = domain
            HTTP_HOST = CURRENT_DOMAIN.host
            SPUtils.getInstance().put("domain", domain.toJsonString())
        }
    }


    class Domain(val name: String, val host: String, val satl: String) : Serializable {

        override fun toString(): String {
            return "$name $host"
        }

        override fun equals(o: Any?): Boolean {
            if (this === o) {
                return true
            }
            if (o == null || javaClass != o.javaClass) {
                return false
            }
            val domain = o as Domain
            if (name != domain.name) {
                return false
            }
            return host == domain.host
        }

        /**
         * 复写hashCode,31 * 性能优化更好
         */
        override fun hashCode(): Int {
            var result = name.hashCode() ?: 0
            result = 31 * result + (host.hashCode() ?: 0)
            return result
        }

    }


}