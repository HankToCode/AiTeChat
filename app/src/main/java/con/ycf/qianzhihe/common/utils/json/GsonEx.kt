package con.ycf.qianzhihe.common.utils

import com.google.gson.Gson
import con.ycf.qianzhihe.BuildConfig
import con.ycf.qianzhihe.common.aes.AESCipher

fun Any.toJsonString(): String = Gson().toJson(this) ?: "{}"

fun Any.aesParams(map: Map<String, Any>): String = if (BuildConfig.ISENCRYPTION) {
    AESCipher.encrypt(map.toString().toJsonString())
} else {
    Gson().toJson(map)
} ?: "{}"
