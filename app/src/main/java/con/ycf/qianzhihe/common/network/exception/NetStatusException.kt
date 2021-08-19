package con.ycf.qianzhihe.common.network.exception

/**
 * @Author: 无人认领
 * @Date: 2020/4/19 10:33
 * desc: 网络状态
 */
class NetStatusException(errorCode: Int, msg: String?) : RuntimeException(msg) {
    private var errorCode: Int
    fun getErrorCode(): Int {
        return errorCode
    }

    fun setErrorCode(errorCode: Int) {
        this.errorCode = errorCode
    }

    init {
        this.errorCode = errorCode
    }
}