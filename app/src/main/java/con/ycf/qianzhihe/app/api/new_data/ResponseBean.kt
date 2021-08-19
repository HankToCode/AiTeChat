package con.ycf.qianzhihe.app.api.new_data

data class ResponseBean<T : Any?>(var code: String, var msg: String, var data: T, var token: String?)