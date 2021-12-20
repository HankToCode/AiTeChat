package com.android.nanguo.common.network

import com.android.nanguo.DemoApplication
import com.android.nanguo.app.api.new_http.ApiService
import com.android.nanguo.app.api.new_http.UrlConstant
import com.android.nanguo.common.network.exception.ErrorStatus
import com.android.nanguo.common.network.exception.NetStatusException
import com.android.nanguo.common.network.interceptor.NetworkInterceptor
import com.android.nanguo.common.utils.NetworkUtil
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.*
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.util.concurrent.TimeUnit

/**
 * Created by 无人认领 on 2020/3/16.
 *
 */

object RetrofitManager {

    val service: ApiService by lazy {
        getRetrofit().create(ApiService::class.java)
    }

    /**
     * 设置公共参数
     */
    private fun addQueryParameterInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()
            val request: Request
            val modifiedUrl = originalRequest.url().newBuilder()
                    // Provide your custom parameter here
                    .addQueryParameter("udid", "d2807c895f0348a180148c9dfa6f2feeac0781b5")
//                    .addQueryParameter("deviceModel", AppUtil.getMobileModel())
                    .build()
            request = originalRequest.newBuilder().url(modifiedUrl).build()
            chain.proceed(request)
        }
    }

    /**
     * 设置头
     */
    private fun addHeaderInterceptor(): Interceptor {
        return Interceptor { chain ->

           /* val timestamp = System.currentTimeMillis().toString()
            val randomCode = HeaderConstant.getRandomCode()
            val salt = UrlConstant.CURRENT_DOMAIN.satl
            val nonce = HeaderConstant.getNonce(randomCode, timestamp, salt).md5()

            val authorization = timestamp + nonce
            val md5Authorization = authorization.md5()

            val fakeToken = UserManager.getFakeToken()

            val aes = AESOperator.encrypt(fakeToken, md5Authorization).replace("\n", "")*/


            val originalRequest = chain.request()
            val requestBuilder = originalRequest.newBuilder()
                    // 请自定义你的请求头
                    /*.header("version", HeaderConstant.URL_VERSION)
                    .header("randomCode", randomCode)
                    .header("nonce", nonce)
                    .header("os", HeaderConstant.OS.toString())
                    .header("appVersion", HeaderConstant.URL_APP_VERSION.toString())
                    .header("timestamp", timestamp)
                    .header("userAgent", HeaderConstant.getUserAgent())
                    .header("Authorization", aes)
                    .method(originalRequest.method(), originalRequest.body())*/
            val request = requestBuilder.build()
            chain.proceed(request)
        }
    }

    /**
     * 设置缓存
     */
    private fun addCacheInterceptor(): Interceptor {
        return Interceptor { chain ->
            var request = chain.request()
            if (!NetworkUtil.isNetworkAvailable(DemoApplication.getInstance().applicationContext)) {
                request = request.newBuilder()
                        .cacheControl(CacheControl.FORCE_CACHE)
                        .build()
            }
            val response = chain.proceed(request)
            if (NetworkUtil.isNetworkAvailable(DemoApplication.getInstance().applicationContext)) {
                val maxAge = 0
                // 有网络时 设置缓存超时时间0个小时 ,意思就是不读取缓存数据,只对get有用,post没有缓冲
                response.newBuilder()
                        .header("Cache-Control", "public, max-age=$maxAge")
                        .removeHeader("Retrofit")// 清除头信息，因为服务器如果不支持，会返回一些干扰信息，不清除下面无法生效
                        .build()
            } else {
                // 无网络时，设置超时为4周  只对get有用,post没有缓冲
                val maxStale = 60 * 60 * 24 * 28
                response.newBuilder()
                        .header("Cache-Control", "public, only-if-cached, max-stale=$maxStale")
                        .removeHeader("nyn")
                        .build()
            }
            response
        }
    }

    private fun getRetrofit(): Retrofit {
        // 获取retrofit的实例
        return Retrofit.Builder()
                .baseUrl(UrlConstant.CURRENT_DOMAIN.host)  //自己配置
                .client(getOkHttpClient())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .build()

    }

    private fun getOkHttpClient(): OkHttpClient {
        //添加一个log拦截器,打印所有的log
        val httpLoggingInterceptor = HttpLoggingInterceptor()
        //可以设置请求过滤的水平,body,basic,headers
        httpLoggingInterceptor.level = HttpLoggingInterceptor.Level.BODY

        //设置 请求的缓存的大小跟位置
        val cacheFile = File(DemoApplication.getInstance().applicationContext.cacheDir, "cache")
        val cache = Cache(cacheFile, 1024 * 1024 * 50) //50Mb 缓存的大小

        return OkHttpClient.Builder()
//                .addInterceptor(addQueryParameterInterceptor())  //参数添加
                .addInterceptor(addHeaderInterceptor()) // token过滤
                .addInterceptor(NetworkInterceptor())
                .addInterceptor(addCacheInterceptor())
//                .addInterceptor(LoggingInterceptor
//                        .Builder()//构建者模式
//                        .loggable(BuildConfig.PROJECT_DEBUG) //是否开启日志打印
//                        .setLevel(Level.BASIC) //打印的等级
//                        .log(Platform.INFO) // 打印类型
//                        .request("Request") // request的Tag
//                        .response("Response")// Response的Tag
//                        .addHeader("log-header", "I am the log request header.") // 添加打印头, 注意 key 和 value 都不能是中文
//                        .build()) //日志,所有的请求响应度看到
                .cache(cache)  //添加缓存
                .connectTimeout(20L, TimeUnit.SECONDS)
                .readTimeout(20L, TimeUnit.SECONDS)
                .writeTimeout(20L, TimeUnit.SECONDS)
                .build()
    }


    class CommonOptions<T> : ObservableTransformer<T, T> {
        override fun apply(observable: Observable<T>): ObservableSource<T> {
            return observable
                    .subscribeOn(Schedulers.io())
                    .unsubscribeOn(Schedulers.io())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    /*事件发送前执行，处理准备工作，在之后最近的subscribeOn指定的线程上执行*/
                    .doOnSubscribe {
                        if (!NetworkUtil.isNetworkAvailable(DemoApplication.getInstance().applicationContext)) {
                            throw NetStatusException(ErrorStatus.NETWORK_ERROR, "网络连接异常")
                        }
                    }
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .unsubscribeOn(Schedulers.io())
        }
    }
}
