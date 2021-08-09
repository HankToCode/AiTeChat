package com.hyphenate.easeim.common.utils

import com.google.gson.Gson

fun Any.toJsonString(): String = Gson().toJson(this) ?: "{}"

