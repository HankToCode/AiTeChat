package com.ycf.qianzhihe.platform

import com.ycf.qianzhihe.app.platform.Platform
import com.ycf.qianzhihe2.BuildConfig

/**
 * @author HankGreen.
 * @Date 2021/11/19
 * @name
 * desc:
 *
 */
class PlatformQZH : Platform {
    override fun getBaseUrl(): String = BuildConfig.BASEURL

    override fun getIdRedProject(): String = BuildConfig.ID_REDPROJECT

    override fun getIsEncryption(): Boolean = BuildConfig.ISENCRYPTION

    override fun getVersionName(): String = BuildConfig.VERSION_NAME
}