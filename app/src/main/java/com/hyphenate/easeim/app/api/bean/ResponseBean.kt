package com.hyphenate.easeim.app.api.bean

data class ResponseBean<T : Any?>(var code: String, var msg: String, var data: T, var token: String?)