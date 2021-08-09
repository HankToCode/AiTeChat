package com.hyphenate.easeim.section.api

import com.hyphenate.easeim.BuildConfig

/**
 * Created by 无人认领 on 2020/4/1.
 * name:
 * desc:
 * Tips:
 *
 */

object Urls {


    fun checkimg(path: String?): String? {
        if (path == null) {
            return ""
        }
        return if (path.contains("http://") || path.contains("https://")) {
            path
        } else {
            ImageMainUrl + path
        }
    }

    /**
     * 服务器
     */
    val baseService: String = UrlConstant.CURRENT_DOMAIN.host

    /**
     * 主地址
     */
    val mainUrl = baseService + "aiteApi/"

    /**
     * 图片地址
     */
    var ImageMainUrl: String = BuildConfig.BASEURL + "aiteApi/profile/" //aiteApi/ xgp add 图像不显示问题


    /**
     * 商城地址
     */
    const val shopUrl = "http://8.135.109.189:82/#/index/"

    /**
     * 图片地址
     */
    val ImageMainUrl1 = baseService

    /**
     * 修改用户昵称
     */
    var MODIFY_USER_NICK_NAME = mainUrl + "app_user/modifyUserNickname"

    /**
     * 修改用户昵称
     */
    var GET_SYS_CONFIG = mainUrl + "common/getSysConfig"

    /**
     * 修改艾特号
     */
    var MODIFY_USER_CODE = mainUrl + "app_user/modifyUserCode"

    /**
     * 更新 添加我时是否需要验证
     */
    var MODIFY_FRIEND_CONSENT = mainUrl + "app_user/modifyFriendConsent"

    /**
     * 更新 性别
     */
    var MODIFY_SEX = mainUrl + "app_user/modifySex"

    /**
     * 修改个性签名
     */
    var MODIFY_SELF_LABLE = mainUrl + "app_user/modifyUserSign"


    /**
     * 认证
     */
    val openAccount = mainUrl + "app_user/openAccount/"


    /**
     * 提现
     */
    val withdrawUrl = mainUrl + "app_user_wallet/withdraw"

    /**
     * 提现到银行卡
     */
    val afterWithdraw = mainUrl + "app_user_wallet/afterWithdraw"


    val check_bank = mainUrl + "app_user_wallet/sureBankCard"

    /**
     * 支付密码
     */
    val updateYeepayPayPwd = mainUrl + "app_user_wallet/updateYeepayPayPwd"

    /**
     * 充值
     */
    val rechargeUrl = mainUrl + "app_user_wallet/recharge"

    /**
     * 银行卡
     */
    val queryBankCards = mainUrl + "app_user/queryBankCards"

    /**
     * 用户信息
     */
    var USER_INFO = mainUrl + "app_user/findUserDetail"

    /**
     * 提现说明
     */
    var getWithdrawExplain = mainUrl + "common/getWithdrawExplain"

    /**
     * 用户头像修改
     */
    var MODIFY_USER_HEAD = mainUrl + "app_user/modifyUserHead"


    /**
     * 搜索用户
     */
    var FIND_USER = mainUrl + "app_user_friend/findUserByPhoneAndUserCode"


    /**
     * 获取好友详情
     */
    var FRIEND_INFO = mainUrl + "app_user_friend/getFriendUser"


    /**
     * 申请加好友
     */
    var APPLY_ADD_USER = mainUrl + "app_user_friend/saveUserFriendApply"

    /**
     * 好友申请列表
     */
    var APPLY_ADD_USER_LIST = mainUrl + "app_user_friend/findUserFriendApplyVosByPage"


    /**
     * 好友申请同意或者拒绝
     */
    var APPLY_ADD_USER_STATUS = mainUrl + "app_user_friend/modifyApplyStatus"

    /**
     * 好友列表
     */
    var USER_FRIEND_LIST = mainUrl + "app_user_friend/findUserFriendVosByPageCache"

    var GET_INVITE_USER_LIST = mainUrl + "app_user_friend/findUserFriendVosByPage"

    var CHECK_FRIEND_DATA_VERSION = mainUrl + "app_user_friend/getUserFriendCacheVersion"

    /**
     * 拉黑好友
     */
    var BLACK_USER_FRIEND = mainUrl + "app_user_friend/saveUserToBlacklist"

    /**
     * 黑名单列表
     */
    var BLACK_USER_LIST = mainUrl + "app_user_friend/findUserBlacklistVosByPage"


    /**
     * 删除好友
     */
    var DEL_USER_FRIEND = mainUrl + "app_user_friend/deleteFriendByFriendUserId"

    /**
     * 通过手机号查询通讯录好友
     */
    var LIST_USER_PHONE = mainUrl + "app_user_friend/listUserByPhone"

    /**
     * 新建群
     */
    var SAVE_GROUP = mainUrl + "app_group/saveGroup"

    /**
     * 搜索群
     */
    var FIND_GROUP = mainUrl + "app_group/findGroupByName"

    /**
     * 删除群
     */
    var DEL_GROUP = mainUrl + "app_group/delGroup"

    /**
     * 修改群头像或昵称
     */
    var MODIFY_GROUP_NAME_OF_HEAD = mainUrl + "app_group/modifyGroupNameOrHead"


    /**
     * 修改群备注
     */
    var MODIFY_GROUP_REMARK = mainUrl + "app_group/modifyGroupUserGroupNickName"


    /**
     * 修改群公告
     */
    var MODIFY_GROUP_NOTICE = mainUrl + "app_group/modifyGroupNotice"

    /**
     * 设置群管理员/撤销管理员
     */
    var MODIFY_GROUP_MANEGER = mainUrl + "app_group/modifyGroupUserRankToMange"

    /**
     * 禁言/取消禁言
     */
    var MODIFY_GROUP_USER_SAY_STATUS = mainUrl + "app_group/modifyGroupUserSayStatus"

    /**
     * 禁言/取消禁言(全言禁言)
     */
    var MODIFY_GROUP_ALL_USER_SAY_STATUS = mainUrl + "app_group/modifyGroupSayFlag"

    /**
     * 邀请新成员入群
     */
    var SAVE_GROUP_USER = mainUrl + "app_group/saveGroupUser"

    /**
     * 查询群里有哪些好友
     */
    var GET_USER_IN_GROUP = mainUrl + "app_user_friend/getUserFriendInGroup"

    /**
     * 我的群组列表
     */
    var MY_GROUP_LIST = mainUrl + "app_group/listGroupByUserId"

    /**
     * 我的群组好友
     */
    var MY_GROUP_LIST_GOODS_FRIEND = mainUrl + "app_group/listGroupFriendUserByGroupId"

    /**
     * 修改我的群昵称
     */
    var MODIFY_USER_GROUP_NICKNAME = mainUrl + "app_group/modifyGroupUserNickName"

    /**
     * 移出群员
     */
    var DEL_GROUP_USER = mainUrl + "app_group/delGroupUser"

    /**
     * 群转让
     */
    var TRANSFER_GROUP = mainUrl + "app_group/modifyGroupUserRankToOwner"


    /**
     * 群详情
     */
    var GROUP_DETAIL = mainUrl + "app_group/groupDetail"

    var CHECK_GROUP_DATA_VERSION = mainUrl + "app_group/getGroupDetailCacheVersion"

    var GET_GROUP_DETAIL = mainUrl + "app_group/groupDetailCache"

    var GET_GROUP_INFO = mainUrl + "app_group/groupInfo"

    /**
     * 群详情用户能否查看之间的详情
     */
    var GROUP_DETAIL_READ_USER_DETAIL = mainUrl + "app_group/modifySeeFriendFlag"

    /**
     * 通过环信id获取群组id
     */
    var GET_GROUP_ID = mainUrl + "app_group/getGroupId"


    /**
     * 发现列表
     */
    var GET_FIND_LIST = mainUrl + "app_news/findAllNews"


    /**
     * 发现列表
     */
    var GET_FIND_LIST_NEW = mainUrl + "find/all"

    /**
     * 举报
     */
    var SAVE_REPORT = mainUrl + "app_report/saveReport"

    /**
     * 转账记录
     */
    var TRANSFER_RECORD = mainUrl + "app_user_transfer/findAllTransfer"


    /**
     * 新闻资讯详情
     */
    var FIND_NEW_BY_ID = mainUrl + "app_news/findOneNewsById"

    /**
     * 推荐阅读
     */
    var FIND_PUSH_NEWS = mainUrl + "app_news/findPushNews"


    /**
     * 充值记录
     */
    var GET_RECHARGE_RECORD = mainUrl + "app_user_wallet/listUserWalletRecharge"

    /**
     * 提现记录
     */
    var GET_WITHDRAAW_RECORD = mainUrl + "app_user_wallet/listUserWalletWithdraw"

    /**
     * 新建红包
     */
    var CREATE_RED_PACKE = mainUrl + "app_red_packet/createRedPacket"

    /**
     * 个人红包
     */
    var CREATE_PERSON_RED_PACKE = mainUrl + "app_red_packet/createAloneRedPacket"

    /**
     * 红包记录
     */
    var RED_PACK_RECORD = mainUrl + "app_red_packet/listRedPacketLog"

    /**
     * 获取未领取红包
     */
    var NO_RED_PACK_RECORD = mainUrl + "app_red_packet/listNoRobRedPackets"


    /**
     * 红包统计
     */
    var RED_PACK_TOTAL = mainUrl + "app_red_packet/totalRedPacket"

    /**
     * 客服列表
     */
    var CUSTOM_LIST = mainUrl + "app_user/listCustomerUser"

    /**
     * 客服问题展示
     */
    var CUSTOM_QUESTIONS = mainUrl + "app_customer/all"

    /**
     * 配置
     */
    val peizhi = mainUrl + "common/getConfig"

    /**
     * 图形验证码
     */
    val tuxingCode = mainUrl + "common/getCaptcha"

    /**
     * 获取验证码
     */
    var getPhoneCodeUrl = mainUrl + "app_user/sendRegisterCode"


    /**
     * 发送忘记支付密码短信验证码
     */
    var sendForgetPayPwdCode = mainUrl + "app_user_wallet/sendForgetPayPwdCode"

    /**
     * 获取验证码
     */
    var getForgetPhoneCodeUrl = mainUrl + "app_user/sendForgetPwdCode"

    /**
     * 获取登录验证码
     */
    var getSMSCodeForLogin = mainUrl + "app_user/sendLoginCode"

    /**
     * 获取冻结接口验证码
     */
    var getAccountFrozenSMSCode = mainUrl + "app_user/sendFrozenAccountCode"

    /**
     * 冻结账号
     */
    var frozenAccount = mainUrl + "app_user/frozenAccount"

    /**
     * 获取解冻接口验证码
     */
    var getAccountThawSMSCode = mainUrl + "app_user/sendThawAccountCode"

    /**
     * 解冻账号
     */
    var thawAccount = mainUrl + "app_user/thawAccountCode"

    /**
     * 获取房间列表
     */
    var getRoomList = mainUrl + "room/list"


    /**
     * 待审核列表
     */
    var applyRoomList = mainUrl + "room/applyRoomList"

    /**
     * 获取搜索群信息
     */
    var searchGroup = mainUrl + "room/selectRoom"

    /**
     * 获取搜索群信息
     */
    var updateRoomMsg = mainUrl + "room/updateRoomMsg"

    /**
     * 发起申请加群
     */
    var applyToRoom = mainUrl + "room/applyToRoom"

    /**
     * 群主审核
     */
    var groupApply = mainUrl + "room/groupApply"

    /**
     * 登录
     */
    var toLoginUrl = mainUrl + "app_user/login"
    const val multiLogin = "aiteApi/app_device/multiDeviceLogin"
    var getDeviceList = mainUrl + "app_device/getDevList"
    var isSingleDevice = mainUrl + "app_device/isSingleDevice"
    var sendMultiDeviceCode = mainUrl + "app_device/sendMultiDeviceCode"
    var openMultiDevice_Url = mainUrl + "app_device/openMultiDevice"
    var stopMultiDevice = mainUrl + "app_device/stopMultiDevice"
    var isOpenMultiDevice = mainUrl + "app_device/isOpenMultiDevice"
    var multiDeviceLogout = mainUrl + "app_device/multiDeviceLogout"

    /**
     * 验证码登录
     */
    var toSMSMultiLoginUrl = mainUrl + "app_device/smsMultiDeviceLogin"

    /**
     * 注册
     */
    var toRegister = mainUrl + "app_user/register"

    /**
     * 注销
     */
    var toLogoff = mainUrl + "app_user/delUser"

    /**
     * 加入房间
     */
    var AddRoom = mainUrl + "room/addUserToRoom"

    /**
     * 平台公告
     */
    var PlatformAnnouncement = mainUrl + "sysNotice/list"

    /**
     * 房间详情
     */
    var getRoomDetail = mainUrl + "room/get"

    /**
     * 获取当前群主所有的群
     */
    var listGroup = mainUrl + "room/listGroup"

    /**
     * 群主收益
     */
    var groupOwnerIncome = mainUrl + "room/groupOwnerIncome"

    /**
     * 群公告
     */
    var groupNotice = mainUrl + "room/getRoomNotice"

    /**
     * banner
     */
    val getBannerUrl = mainUrl + "common/listBanner"


    /**
     * 发踩雷红包
     */
    var sendCLRedEnvelope = mainUrl + "redPacket/createMineRedPacket"

    /**
     * 编辑群发包数和赔率
     */
    var groupPackageOdds = mainUrl + "room/updateRoomAmout"

    /**
     * 添加支付密码
     */
    var addPayPassword = mainUrl + "app_user_wallet/saveNewPayPassword"

    /**
     * 修改支付密码
     */
    var upPassword = mainUrl + "app_user_wallet/modifyPayPwd"

    /**
     * 检测红包
     */
    var getRedEnvelopeState = mainUrl + "app_red_packet/isRobRedPacket"

    /**
     * 抢红包
     */
    var grabRedEnvelope = mainUrl + "app_red_packet/robRedPacket"


    /**
     * 从本地缓存获取红包详情
     */
    var getRedPacket = mainUrl + "app_red_packet/getRedPacket"

    /**
     * 从数据库获取红包详情
     */
    var getRedPacketFromDB = mainUrl + "app_red_packet/getRedPacketFromDB"

    /**
     * 代理流水
     */
    var dlls = mainUrl + "award/listAward"

    /**
     * 首页 banner
     */
    var bannerUrl = mainUrl + "api/carouselFigure/list"

    /**
     * 月总收益
     */
    val monthMoney = mainUrl + "award/getTodayAndMonthAward"

    /**
     * 刷新token
     */
    var refreshToken = mainUrl + "api/frontBase/userThirdLogin/refreshLogin"

    /**
     * 玩法说明
     */
    var howToPlay = mainUrl + "api/frontBase/user/howToPlay?type="

    /**
     * 新_玩法说明
     */
    var howToPlayNew = "$mainUrl/h5/user/howToPlay?type=1"

    /**
     * 获取用户信息
     */
    var getUserByPhone = mainUrl + "api/frontBase/user/getUserByPhone"

    /**
     * 获取用户信息(新)
     */
    var getUserByInvite = mainUrl + "userFriend/searchUser"

    /**
     * 修改密码
     */
    var up_Password = mainUrl + "api/frontBase/user/updatePassword"


    /**
     * 忘记密码
     */
    var forgetpasswordUrl = mainUrl + "app_user/forgetPwd"


    /**
     * 创建房间
     */
    var creatRoom = mainUrl + "api/redPacket/room/create"

    /**
     * 修改房间信息
     */
    var upRoom = mainUrl + "api/redPacket/room/updateRoom"

    /**
     * 用户信息
     */
    var UserInfo = mainUrl + "user/getUserInfo"

    /**
     * 获取房间账户余额
     */
    var ROOM_MONEY = mainUrl + "room/getRoomMoney"

    /**
     * 退出房间
     */
    val layoutRoom = mainUrl + "room/deleteUserFromGroup"

    /**
     * 获取群所有成员信息
     */
    val roomAllUser = mainUrl + "room/getRoomAllUser"

    /**
     * 获取群所有成员信息
     */
    val updateAutoRob = mainUrl + "room/updateAutoRob"

    /**
     * 充值记录
     */
    var BalancePayments = mainUrl + "walletRecord/listUserWalletIn"

    /**
     * 忘记支付密码
     */
    var forgetPassword = mainUrl + "app_user_wallet/forgetPayPwd"

    /**
     * 收藏列表
     */
    var CollectList = mainUrl + "app_user/listCollect"

    /**
     * 收藏
     */
    var addCollect = mainUrl + "app_user/saveCollect"

    /**
     * 取消收藏
     */
    var CancelCollect = mainUrl + "app_user/delCollect"


    /**
     * 银行卡列表
     */
    var bankCardList = mainUrl + "app_user_wallet/findBankCardVosByPage"

    /**
     * 保存银行卡
     */
    var addBankCardList = mainUrl + "app_user_wallet/saveBankCard"

    /**
     * 提现银行卡
     */
    var withdrawBankCard = mainUrl + "app_user_wallet/withdraw"

    /**
     * 移除银行卡
     */
    var removeBankCardList = mainUrl + "app_user_wallet/cancelBindBankCard"

    /**
     * 提现记录
     */
    val tixianjil = mainUrl + "walletRecord/listUserWalletOut"

    /**
     * 修改昵称
     */
    var upDataNickName = mainUrl + "user/updateNickName"

    /**
     * 联系客服
     */
    var getCustemUrl = mainUrl + "common/getCustemUrl"

    /**
     * 更新用户真实姓名
     */
    val upDataRealName = mainUrl + "user/updateRealName"

    /**
     * 更新头像
     */
    var upHead = mainUrl + "user/uploadAvatar"


    /**
     * 更新群头像
     */
    var groupUpHead = mainUrl + "common/uploadImg"

    /**
     * 退出群组
     */
    var exitGroup = mainUrl + "app_group/exitGroup"


    /**
     * 群管理员列表
     */
    var LIST_GROUP_MANAGE = mainUrl + "app_group/listMangerByGroupId"


    /**
     * 更换手机号
     */
    var upPhone = mainUrl + "api/frontBase/user/updatePhone"

    /**
     * 加好友
     */
    var addFriend = mainUrl + "api/frontBase/userFriend/saveUserFriend"

    /**
     * 加好友(新)
     */
    var addFriend_NEW = mainUrl + "userFriend/addFriend"

    /**
     * 删除好友
     */
    var delFriend = mainUrl + "api/frontBase/userFriend/deleteUserFriend"

    /**
     * 更改密码
     */
    var upDataPassword = mainUrl + "user/updatePassword"


    /**
     * 群组踢人
     */
    var RemoveRoom = mainUrl + "api/redPacket/room/shotUserFromGroup"

    /**
     * 新_群组踢人
     */
    var NeWRemoveRoom = mainUrl + "room/kickRoomUser"

    /**
     * 群主解散房间
     */
    var deleteGroup = mainUrl + "api/redPacket/room/deleteGroup"

    /**
     * 发个人红包
     */
    var sendPerRedEnvelope = mainUrl + "api/redPacket/redPacket/createGroupChatRedPacket"

    /**
     * 发接龙红包
     */
    var sendDZRedEnvelope = mainUrl + "api/redPacket/redPacket/createFightRedPacket"


    /**
     * 我的下级
     */
    val xiaji = mainUrl + "user/listUserInvite"

    /**
     * 我的好友
     */
    val FRIEND_LIST = mainUrl + "userFriend/listFriend"

    /**
     *
     *
     * / **
     * 签到
     */
    var signIn = mainUrl + "api/frontBase/userSign/create"

    /**
     * 历史签到
     */
    var getSign = mainUrl + "api/frontBase/userSign/list"

    /**
     * 查询代理下会员
     */
    var getDlList = mainUrl + "user/listUserInvite"

    /**
     * 字典
     */
    var getZD = mainUrl + "api/dictionary/listByParentCode"

    /**
     * 单个配置
     */
    var getOneZD = mainUrl + "api/dictionary/getByParentAndItem"

    /**
     * 获取敏感词汇
     */
    var getMGCH = mainUrl + "api/frontBase/wordInfo/list"

    /**
     * 添加绑定支付宝
     */
    var addAliPay = mainUrl + "api/frontBase/user/updateAlipay"

    /**
     * 添加绑定银行卡
     */
    var addBank = mainUrl + "wallet/updateAccount"

    /**
     * 查询银行卡信息或者支付宝信息
     */
    val bankInfo = mainUrl + "wallet/getAccount"

    /**
     * 充值
     */
    var getRecharge = mainUrl + "app_user_wallet/recharge"

    /**
     * 趣币流水
     */
    var getQBLS = mainUrl + "api/frontBase/userMoney/page"

    /**
     * 收入
     */
    var getMOney = mainUrl + "api/frontBase/userMoney/get"

    /**
     * 排行榜
     */
    var getBank = mainUrl + "api/frontBase/userCenter/pageFunMoneyRank"

    /**
     * 排行榜（自己）
     */
    var getBankMine = mainUrl + "api/frontBase/userCenter/getUserCenter"

    /**
     * 转账
     */
    var transfer = mainUrl + "app_user_transfer/expend"

    /**
     * 转账记录
     */
    var transferRecord = mainUrl + "walletRecord/listTransferRecord"

    /**
     * 积分列表
     */
    val scoreList = mainUrl + "api/frontBase/exchange/page"


    /**
     * 房间加人
     */
    var addGroupPeople = mainUrl + "api/redPacket/room/addFriendToGroup"

    /**
     * 积分兑换
     */
    var creditChangeMoney = mainUrl + "api/frontBase/exchange/exchange"

    /**
     * 积分兑换记录
     */
    var exchangeCrediRecords = mainUrl + "api/frontBase/exchange/pageDetail"

    /**
     * 红包流水
     */
    var qhbls = mainUrl + "redPacket/listRedPacketLog"

    /**
     * 二维码
     */
    var QR = "?inviteCode="

    /**
     * 晋升代理
     */
    var promote = mainUrl + "api/frontBase/exchange/promote"

    /**
     * 关于我们
     */
    var about = mainUrl + "api/frontBase/user/aboutOur"

    /**
     * 我的会员总数
     */
    var memberCount = mainUrl + "api/frontBase/user/getCountByRecommend"

    /**
     * 盈亏
     */
    var ykUrl = mainUrl + "api/frontBase/userMoney/profitLoss"

    /**
     * 客服
     */
    var serviceUrl = mainUrl + "customerServe/list"

    /**
     * 审核
     */
    var review = mainUrl + "api/frontBase/userProxyDetail/audit"

    /**
     * 申请代理
     */
    var apply = mainUrl + "api/frontBase/userProxyDetail/apply"

    /**
     * 申请记录(审核记录)
     */
    var applyRecord = mainUrl + "api/frontBase/userProxyDetail/page"

    /**
     * 投诉意见
     */
    var complaints = mainUrl + "api/frontBase/complaintProposal/save"

    /**
     * 搜索
     */
    var search = mainUrl + "api/redPacket/room/getByRoomId"

    /**
     * 平台公告未读消息count
     */
    var unReadCount = mainUrl + "api/frontBase/noticeInfo/nonRead"

    /**
     * 平台公告设置为已读
     */
    var updateStatus = mainUrl + "api/frontBase/noticeInfo/updateNumStatus"

    /**
     * 未审核数量
     */
    var ApplyNumber = mainUrl + "api/frontBase/userProxyDetail/getCount"


    /**
     * 进群用户申请列表
     */
    var FIND_APPLY_GROUP_USER = mainUrl + "app_group/findApplyGroupUser"

    /**
     * 删除用户申请数据
     */
    var DEL_APPLY_GROUP_USER = mainUrl + "app_group/delApplyGroupUser"

    var DEL_APPLY_ADD_USER = mainUrl + "app_user_friend/delUserFriendApply"

    /**
     * 同意入群
     */
    var AGREE_GROUP_USER = mainUrl + "app_group/agreeGroupUser"


    /**
     * 快付通获取accesstoken
     */
    var accessTokenKft = mainUrl + "kftPay/accessToken"

    /**
     * 快付通获取预订单号
     */
    var accessCommonTradeKft = mainUrl + "kftPay/commonTrade"


    /**
     * 快付通获取充值，转账等预订单号
     */
    var accessPreTradeKft = mainUrl + "kftPay/transTrade"

    /**
     * 检查版本号
     */
    var checkVersion = mainUrl + "common/versionUpdate"


    /**
     * 消息免打扰设置
     */
    var getMessageSet = mainUrl + "api/redPacket/roomMessage/getAll"

    /**
     * 提现申请
     */
    var withdraw = mainUrl + "wallet/withdraw"

    /**
     * 更新群消息设置
     */
    var upMessageSet = mainUrl + "api/redPacket/roomMessage/update"

    /**
     * http://www.weaigu.com
     */
    var downloadUrl = "https://fir.im/flyingfish"


    var register_agree = mainUrl + "xieyi/web/user_agree.html" //隐私政策

    var user_agree = mainUrl + "xieyi/web/register_agree.html" //用户协议


    /**
     * 设置好友昵称
     */
    var ADD_GOODS_FRIEND_REMARK = mainUrl + "app_user_friend/modifyFriendNickName"

    /**
     * 转账状态
     */
    var TRANSFER_STATUS = mainUrl + "app_user_transfer/getExpendStatus"

    /**
     * 获取禁言状态
     */
    var WHETHER_THE_SILENCE = mainUrl + "app_group/getGroupUserSayStatus"

    /**
     * 确定转账
     */
    var CONFIRM = mainUrl + "app_user_transfer/sureExpend"

    /**
     * 温馨提示
     */
    var TIP = mainUrl + "common/getReminder"


    /**
     * 零钱锁状态
     */
    var walletLockStatus = mainUrl + "app_user_wallet/isPayLock"

    /**
     * 获取零钱锁验证码
     */
    var walletLockGetCode = mainUrl + "app_user_wallet/sendPayLockCode"

    /**
     * 零钱锁开启
     */
    var walletLockOpen = mainUrl + "app_user_wallet/setPayLock"

    /**
     * 零钱锁关闭
     */
    var walletLockClose = mainUrl + "app_user_wallet/delPayLock"

    //============================支付===============================//
    //============================支付===============================//
    /**
     * 开通钱包账户
     */
    var openWalletAccount = mainUrl + "app_user_wallet/accountOpen"

    /**
     * 充值预下单
     */
    var walletRecharge = mainUrl + "app_user_wallet/rechargeCreate"

    /**
     * 充值查询
     */
    var walletRechargeQuery = mainUrl + "app_user_wallet/rechargeQuery"

    /**
     * 提现预下单
     */
    var walletWithdraw = mainUrl + "app_user_wallet/withdrawCreate"

    /**
     * 提现查询
     */
    var walletWithdrawQuery = mainUrl + "app_user_wallet/withdrawQuery"

    /**
     * 转账
     */
    var walletTransfer = mainUrl + "app_user_transfer/transferCreate"

    /**
     * 转账查询
     */
    var walletTransferQuery = mainUrl + "app_user_transfer/transferQuery"

    /**
     * 转账确认收款
     */
    var walletTransferConfirm = mainUrl + "app_user_transfer/transferConfirm"


    /**
     * 商城入口开关
     */
    var showStore = mainUrl + "common/showStore"

    /**
     * 检查版本
     */
    /*fun checkVersion(context: Context, isinge: Boolean) {
        if (isinge) {
            CretinAutoUpdateUtils.getInstance(context).check(object : ForceExitCallBack() {
                fun exit() {
                    (context as Activity).finish()
                }

                fun isHaveVersion(isHave: Boolean) {}
                fun cancel() {}
            })
        } else {
            val map: MutableMap<String, Any> = HashMap()
            map["type"] = "Android"
            map["version"] = SystemUtil.getAppVersionName()
            ApiClient.requestNetHandle(context, checkVersion, "正在检测...", map, object : ResultListener() {
                fun onSuccess(json: String?, msg: String?) {
                    val versionInfo: VersionInfo = FastJsonUtil.getObject(json, VersionInfo::class.java)
                    if (versionInfo != null) {
                        if (versionInfo.getVersionCodes() > SystemUtil.getAppVersionNumber()) {
                            Builder(context).setTitle("新版本").setMessage(versionInfo.getUpdateLogs()).setPositiveButton("更新", DialogInterface.OnClickListener { anInterface, i ->
                                val updateEntity = UpdateEntity()
                                updateEntity.setVersionCode(versionInfo.getVersionCodes())
                                updateEntity.setIsForceUpdate(versionInfo.getIsForceUpdates())
                                updateEntity.setPreBaselineCode(versionInfo.getPreBaselineCodes())
                                updateEntity.setVersionName(versionInfo.getVersionNames())
                                updateEntity.setDownurl(versionInfo.getDownurls())
                                updateEntity.setUpdateLog(versionInfo.getUpdateLogs())
                                updateEntity.setSize(versionInfo.getApkSizes())
                                updateEntity.setHasAffectCodes(versionInfo.getHasAffectCodess())
                                val var8: UpdateEntity = updateEntity
                                CretinAutoUpdateUtils.getInstance(context).startUpdate(var8)
                            }).setNegativeButton("取消", DialogInterface.OnClickListener { anInterface, i -> anInterface.dismiss() }).show()
                        } else {
                            ToastUtil.toast("当前已是最新版本")
                        }
                    } else {
                        ToastUtil.toast("请求数据失败")
                    }
                }

                fun onFailure(msg: String?) {
                    ToastUtil.toast(msg)
                }
            })
        }
    }*/


}
