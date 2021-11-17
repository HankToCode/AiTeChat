package com.ycf.qianzhihe.common.network.exception

/**
 * Created by 无人认领 on 2020/12/5.
 * desc:
 */
class ApiException : RuntimeException {

    private var code: Int? = null


    var errorCode: String? = null
    var errorMsg: String? = null


    constructor(throwable: Throwable, code: Int) : super(throwable) {
        this.code = code
    }

    constructor (errorCode: String?, errorMsg: String?) {
        this.errorCode = errorCode
        this.errorMsg = errorMsg
    }

    constructor(message: String) : super(Throwable(message))
}