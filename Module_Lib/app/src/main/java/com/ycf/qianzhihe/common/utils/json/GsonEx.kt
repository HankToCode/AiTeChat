package com.ycf.qianzhihe.common.utils.json

import com.google.gson.Gson
import com.ycf.qianzhihe.BuildConfig
import com.ycf.qianzhihe.common.aes.AESCipher

fun Any.toJsonString(): String = Gson().toJson(this) ?: "{}"

/*fun Any.aesParams(map: Map<String, Any>): String = if (BuildConfig.ISENCRYPTION) {
    AESCipher.encrypt(map.toString().toJsonString())
} else {
    Gson().toJson(map)
} ?: "{}"*/
