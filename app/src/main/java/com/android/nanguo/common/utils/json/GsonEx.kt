package com.android.nanguo.common.utils

import com.google.gson.Gson
import com.android.nanguo.BuildConfig
import com.android.nanguo.common.aes.AESCipher

fun Any.toJsonString(): String = Gson().toJson(this) ?: "{}"

fun Any.aesParams(map: Map<String, Any>): String = if (BuildConfig.ISENCRYPTION) {
    AESCipher.encrypt(map.toString().toJsonString())
} else {
    Gson().toJson(map)
} ?: "{}"
