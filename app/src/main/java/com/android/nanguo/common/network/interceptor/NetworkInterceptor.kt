package com.android.nanguo.common.network.interceptor

import com.android.nanguo.common.utils.log.LogUtils
import okhttp3.Interceptor
import okhttp3.Response
import okio.Buffer
import java.io.IOException
import java.net.URLDecoder

/**
 * @Author: 无人认领
 * @Date: 2020/4/15 20:35
 * desc:
 */
class NetworkInterceptor : Interceptor {
    val TAG = "NetworkInterceptor"
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response? {
        val oriRequest = chain.request()
        val requestUrl = URLDecoder.decode(oriRequest.url().toString(), "UTF-8")
        val requestMethod = URLDecoder.decode(oriRequest.method(), "UTF-8")
        val requestHeaders = URLDecoder.decode(oriRequest.headers().toString(), "UTF-8")
        var con = ""
        //打印url
        LogUtils.Companion.largeLogD(TAG, "requestUrl:$requestUrl")
        LogUtils.Companion.largeLogD(TAG, "RequestMethod:$requestMethod")
        LogUtils.Companion.largeLogD(TAG, "RequestHeaders:\n$requestHeaders")
        //打印requestBody
        if ("POST" == oriRequest.method()) {
            val buffer1 = Buffer()
            oriRequest.body()!!.writeTo(buffer1)
            con = buffer1.readUtf8()
            if (con.length > 0 && con.length < 2 * 1024 * 1024) {
                LogUtils.Companion.largeLogD(TAG, "requestBody：$con")
            } else {
                LogUtils.Companion.largeLogD(TAG, "requestBody：length=" + con.length)
            }
        }
        //打印responseBody
        val response = chain.proceed(oriRequest)
        val responseBody = response.body()
        if (responseBody!!.contentLength() != 0L) {
            val bufferedSource = responseBody.source()
            bufferedSource.request(Long.MAX_VALUE)
            val buffer = bufferedSource.buffer()
            LogUtils.Companion.largeLogD(TAG, "requestUrl：$requestUrl")
            LogUtils.Companion.largeLogD(TAG, "requestBody：$con")
            if (responseBody.contentType()!!.type() == "image") {
                LogUtils.Companion.largeLogD(TAG, "responseBody：" + responseBody.contentType())
            } else {
                LogUtils.Companion.largeLogD(TAG, "responseBody：" + buffer.clone().readUtf8())
            }
        } else {
            LogUtils.Companion.largeLogD(TAG, "responseBody：contentLength=" + responseBody.contentLength())
        }
        return response
    }
}