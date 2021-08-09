package com.hyphenate.easeim.section.api

import com.hyphenate.easeim.section.api.bean.LoginInfo
import com.hyphenate.easeim.section.api.bean.ResponseBean
import io.reactivex.Observable
import retrofit2.http.*


/**
 * Created by 无人认领 on 2020/4/1.
 * name: API接口
 * desc:
 * Tips:
 *
 */
interface ApiService {


    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.multiLogin)
    @FormUrlEncoded
    fun login(@Header("token") token: String, @FieldMap map: HashMap<String, String>): Observable<ResponseBean<LoginInfo>>

/*
    */
    /**
     * 用户登录
     *//*
    @Headers(UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.LOGIN)
    @FormUrlEncoded
    fun login(@FieldMap map: HashMap<String, String>): Observable<BaseBean<UserInfo>>

    */
    /**
     * 退出登录
     *//*
    @Headers(UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.LOGOUT)
    fun logout(): Observable<BaseBean<String>>

    */
    /**
     * 用户注册
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.REGISTER)
    fun register(@Body send: RegisterSend): Observable<BaseBean<Any>>

    */
    /**
     * 接收验证码
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.RECEIVE_VERIFICATION_CODE)
    fun sms(@Body send: SMSSend): Observable<BaseBean<String>>

    */
    /**
     * 校验验证码
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.CHECK_VERIFICATION_CODE)
    fun smsCheck(@Body send: SMSCheckSend): Observable<BaseBean<String>>

    */
    /**
     * 忘记密码
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.RESET_PASSWORD)
    fun resetPassword(@Body send: ResetPasswordSend): Observable<BaseBean<String>>

    */
    /**
     * 修改登录密码
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.CHANGE_LOGIN_PWD)
    fun changeLoginPwd(@Body send: ChangeLoginPwdSend): Observable<BaseBean<String>>

    */
    /**
     * 修改兑换密码
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.CHANGE_TRADE_PWD)
    fun changeTradePwd(@Body send: ChangeTradePwdSend): Observable<BaseBean<String>>

    */
    /**
     * 忘记兑换密码
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.RESET_TRADE_PASSWORD)
    fun resetTradePassword(@Body send: ResetTradePwdSend): Observable<BaseBean<String>>

    */
    /**
     * 置换中心-顶部置换数据
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.GET_TRADE_DATA)
    fun getTradeData(): Observable<BaseBean<GetTradeData>>

    */
    /**
     * 置换中心-分类
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.GET_TRADE_CLASS)
    fun getTradeClass(): Observable<BaseBean<List<GetTradeClass>>>

    */
    /**
     * 兑换CCQ
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.SELL_CCQ)
    fun sellCCQ(@Body send: SellCCQSend): Observable<BaseBean<Any>>

    */
    /**
     * 验证置换暗号
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.CHECK_TRADE_SIGNAL)
    fun checkTradeSignal(@Body send: CheckTradeSignalSend): Observable<BaseBean<Any>>

    */
    /**
     * 验证置换暗号
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.CHECK_DEMAND_ORDER_INFO)
    fun checkDemandOrderInfo(@Body send: CheckDemandOrderInfoSend): Observable<BaseBean<Any>>

    */
    /**
     * 获取置换时间
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.GET_TRAD_TIME)
    fun getTradTime(): Observable<BaseBean<GetTradeTime>>

    */
    /**
     * 用户钱包接口-币种资产详情
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.GET_USER_ASSETS_DETAIL)
    fun getUserAssetsDetail(@Body send: GetUserAssetsDetailSend): Observable<BaseBean<GetUserAssetsDetail>>

    */
    /**
     * 我的页面数据
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.MINE_INFO)
    fun getMineInfo(): Observable<BaseBean<UserInfo.UserQuery>>

    */
    /**
     * 查询用户未读通知数量
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.FIND_USER_NOTIFICATION_COUNT)
    fun findUserNotificationCount(): Observable<BaseBean<String>>

    */
    /**
     * 用户通知列表
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.NOTIFICATION_LIST)
    fun getNotificationList(@Body send: CommonPageSend): Observable<BaseBean<BasePageBean<List<Notice>>>>

    */
    /**
     * 用户通知详情
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.NOTIFICATION_DETAIL)
    fun getNotificationDetail(@Body send: GetCommonInfoSend): Observable<BaseBean<Notice>>

    */
    /**
     * 修改通知为已读/未读
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.UPDATE_NOTIFICATION_BY_ID)
    fun updateNotificationById(@Body send: UpdateNoticeSend): Observable<BaseBean<String>>

    */
    /**
     * 批量修改消息为已读
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.UPDATE_NOTIFICATION)
    fun updateAllNotification(@Body send: UpdateAllNoticeSend): Observable<BaseBean<String>>

    */
    /**
     * 批量修改消息为已读
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.DELETE_ALL_NOTIFICATION)
    fun deleteAllNotification(): Observable<BaseBean<String>>

    */
    /**
     * 获取通知设置列表
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.NOTIFICATION_SETTING_INFO)
    fun getNoticeSettingInfo(): Observable<BaseBean<NoticeSettingInfo>>

    */
    /**
     * 获取通知设置列表
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.NOTIFICATION_SETTING)
    fun updateNoticeSetting(@Body send: NoticeSettingSend): Observable<BaseBean<String>>

    */
    /**
     * 置换中心-底部求购数据列表
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.GET_DEMAND_DATA)
    fun getDemandData(@Body send: GetDemandDataSend): Observable<BaseBean<List<GetDemandData>>>

    */
    /**
     * 修改用户头像
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.UPDATE_AVATAR)
    fun updateAvatar(@Body send: UpdateAvatarSend): Observable<BaseBean<String>>

    */
    /**
     * 查询联系方式
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.GET_PAYMENT_METHOD)
    fun getPaymentMethod(): Observable<BaseBean<List<PaymentMethodBean>>>

    */
    /**
     * 查询联系方式
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.SETTING_PATMENT_METHOD)
    fun settingPaymentMethod(@Body send: SettingPaymentSend): Observable<BaseBean<String>>


    */
    /**
     * 我的需求列表
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.SELECT_MY_NEED_BY_USER)
    fun selectMyNeedByUser(@Body send: GetOrderDemandDataSend): Observable<BaseBean<OrderDemandData>>

    */
    /**
     * 我的需求发布中列表
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.SELECT_DEMAND_BY_USER)
    fun selectDemandByUser(@Body send: GetOrderDemandDataSend): Observable<BaseBean<OrderDemandData>>

    */
    /**
     * 下架
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.SET_DEMAND_TO_SOLD_OUT)
    fun setDemandToSoldOut(@Body send: OrderDemandDataSend): Observable<BaseBean<String>>

    */
    /**
     * 删除
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.SET_DEMAND_TO_DELETE)
    fun setDemandToDelete(@Body send: OrderDemandDataSend): Observable<BaseBean<String>>

    */
    /**
     * 申诉
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.SET_DEMAND_TO_SS)
    fun setDemandToSS(@Body send: OrderSSSend): Observable<BaseBean<String>>

    */
    /**
     * 修改
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.UPDATE_DEMAND)
    fun updateDemand(@Body send: OrderDemandDataSend): Observable<BaseBean<String>>

    */
    /**
     * 发布需求-获取价格区间
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.GET_DEMAND_PRICE_SECTION)
    fun getDemandPriceSection(): Observable<BaseBean<GetDemandPriceSectionResponse>>

    */
    /**
     * 上架
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.SET_DEMAND_TO_PUBLISHED)
    fun setDemandToPublished(@Body send: OrderDemandDataSend): Observable<BaseBean<String>>

    */
    /**
     * 提交付款凭证
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.SUBMIT_BILL_OF_CREDIT)
    fun submitBillOfCredit(@Body send: OrderCertificateSend): Observable<BaseBean<String>>

    */
    /**
     * 提交付款凭证
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.COMMIT_VIDEO_CERTIFICATE)
    fun commitVideoCertificate(@Body send: OrderCertificateSend): Observable<BaseBean<String>>

    */
    /**
     * 订单申诉
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.COMMIT_APPEAL)
    fun commitAppeal(@Body send: OrderDemandDataSend): Observable<BaseBean<String>>

    */
    /**
     * 发布需求-前置条件检查
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.CHECK_DEMAND_PRE_CHECK)
    fun checkDemandPreCheck(): Observable<BaseBean<String>>

    */
    /**
     * 发布需求
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.INSERT_DEMAND)
    fun insertDemand(@Body send: FacultyNeedsSend): Observable<BaseBean<String>>

    */
    /**
     * 我的需求订单（详情）
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.SELECT_NEED_ORDER_DETAILS_BY_USER)
    fun selectNeedOrderDetailsByUser(@Body send: OrderDemandDataSend): Observable<BaseBean<OrderDemandResponse>>

    */
    /**
     * 发布中已完成（详情）
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.SELECT_DEMAND_DETAILS_BY_USER)
    fun selectDemandDetailsByUser(@Body send: OrderDemandDataSend): Observable<BaseBean<OrderDemandResponse>>

    */
    /**
     * 个人剩余发布需求次数
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.SELECT_HOW_MANY_BY_USER)
    fun selectHowManyByUser(@Body send: selectHowManyByUserSend): Observable<BaseBean<PostsRemainingData>>

    */
    /**
     * 订单删除
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.DELETE_ORDER_BY_ID)
    fun deleteOrderById(@Body send: OrderDemandDataSend): Observable<BaseBean<String>>


    */
    /**
     * oss上传获取临时凭证
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.GET_UPLOAD_CERTIFICATE)
    fun getUploadCertificate(): Observable<BaseBean<OSSResponseData>>

    */
    /**
     * 我的兑换列表
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.SELECT_MY_SELL_BY_USER)
    fun selectMySellByUser(@Body send: GetOrderDemandDataSend): Observable<BaseBean<OrderDemandData>>

    */
    /**
     * 我的兑换详情
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.SELECT_MY_SELL_ORDER_DETAIL_BY_USER)
    fun selectMySellOrderDetailsByUser(@Body send: OrderDemandDataSend): Observable<BaseBean<OrderDemandResponse>>

    */
    /**
     * 确认收款
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.AFFIRM_RECEIVED)
    fun affirmReceived(@Body send: OrderDemandDataSend): Observable<BaseBean<String>>

    */
    /**
     * 取消置换（需求）
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.CANCEL_ORDER)
    fun cancelOrder(@Body send: OrderDemandDataSend): Observable<BaseBean<String>>

    */
    /**
     * 订单首页数据
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.ORDER_HOME)
    fun getOrderHome(): Observable<BaseBean<OrderHomeResponse>>

    */
    /**
     * 我的兑换值列表
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.FIND_USER_SALE_VALUE_LIST)
    fun findUserSaleValueList(@Body send: CommonPageSend): Observable<BaseBean<GetSoldValueData>>

    */
    /**
     * 我的兑换值明细
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.FIND_USER_SALE_VALUE_BY_ID)
    fun findUserSaleValueById(@Body send: GetCommonInfoSend): Observable<BaseBean<SoldValueData>>

    */
    /**
     * 查询数据字典
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.GET_CONFIG_BY_CODE)
    fun getConfigByCode(@Body send: ConfigSend): Observable<BaseBean<String>>

    */
    /**
     * 查询数据字典
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.GET_SYSTEM_CONFIG_BY_CODE)
    fun getSystemConfigByCode(@Body send: GetSystemConfigByCodeSend): Observable<BaseBean<List<GetSystemConfigByCode>>>

    */
    /**
     * 用户充置换出
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.CCQ_HANDLER)
    fun ccqHandler(@Body send: EncryptionCcqHandlerSend): Observable<BaseBean<String>>

    */
    /**
     * 用户置换
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.CCQ_RECHARGE)
    fun ccqRecharge(@Body send: EncryptionCcqHandlerSend): Observable<BaseBean<String>>

    */
    /**
     * 用户置换
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.CCQ_RECHARGE2)
    fun ccqRecharge2(@Body send: EncryptionCcqHandlerSend): Observable<BaseBean<String>>

    */
    /**
     * 用户置换出
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.CCQ_WITHDRAW)
    fun ccqWithDraw(@Body send: EncryptionCcqHandlerSend): Observable<BaseBean<String>>

    */
    /**
     * 用户置换出
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.CCQ_WITHDRAW2)
    fun ccqWithDraw2(@Body send: EncryptionCcqHandlerSend): Observable<BaseBean<String>>

    */
    /**
     * 资产首页
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.GET_USER_ASSETS)
    fun getUserAssets(): Observable<BaseBean<AssetsListData>>

    */
    /**
     * 资产首页
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.GET_FROZEN_USER_ASSETS_DETAIL)
    fun getFrozenUserAssetsDetail(): Observable<BaseBean<FrozenUserAssetsDetail>>

    */
    /**
     * 充置换出验证用户手机号
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.VALID_LT_PHONE)
    fun validLtPhone(@Body send: GetLtCcqInfoSend): Observable<BaseBean<String>>

    */
    /**
     * 充置换出验证用户手机号
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.VALID_LT_PHONE2)
    fun validLtPhone2(@Body send: GetLtCcqInfoSend): Observable<BaseBean<String>>

    */
    /**
     * 验证LT用户是否实名
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.GET_LT_CCQ_INFO)
    fun getLtCcqInfo(@Body send: GetLtCcqInfoSend): Observable<BaseBean<GetLtInfoData>>

    */
    /**
     * 验证LT用户是否实名
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.GET_LT_CCQ_INFO2)
    fun getLtCcqInfo2(@Body send: GetLtCcqInfoSend): Observable<BaseBean<GetLtInfoData>>

    */
    /**
     * 充置换出发送短信验证码
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.SEND_VALIDCODE)
    fun sendValidCode(@Body send: SMSSend): Observable<BaseBean<String>>

    */
    /**
     * 查询用户充置换出列表
     * tradeType	是	int	置换类型 1 置换 2 置换出
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.GET_CCQ_OPERATE_ORDERS)
    fun getCcqOperateOrders(@Body send: AssetsRecordListSend): Observable<BaseBean<RecordResponse>>

    */
    /**
     * 用户充置换出详情
     * transferId	是	int	充提记录id
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.GET_CCQ_TRANS_FER_DETAIL)
    fun getCcqTransferDetail(@Body send: RecordDetailSend): Observable<BaseBean<AssetsRecordResponse>>

    */
    /**
     * 置换记录列表
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.TRADE_RECORD)
    fun getTradeRecord(@Body send: RecordListSend): Observable<BaseBean<RecordResponse>>

    */
    /**
     * 置换记录详情
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.TRADE_RECORD_BY_ID)
    fun getTradeRecordById(@Body send: RecordDetailSend): Observable<BaseBean<AssetsRecordResponse>>

    */
    /**
     * 获取版本信息
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.CHECK_FOR_VERSION)
    fun checkForVersion(@Body send: GetAppInfoSend): Observable<BaseBean<AppVersionBean>>

    */
    /**
     * 获取系统时间戳
     *//*
    @Headers(UrlConstant.HEADER_JSON_TYPE, UrlConstant.HEADER_JSON_ACCEPT)
    @POST(Urls.GET_SYSTEM_TIME)
    fun getSystemTime(): Observable<BaseBean<String>>*/

}