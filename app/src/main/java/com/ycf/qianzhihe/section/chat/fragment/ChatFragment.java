package com.ycf.qianzhihe.section.chat.fragment;

import static android.app.Activity.RESULT_OK;
import static com.zds.base.util.FilePathUtils.getTakePhotoFilePath;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.Toast;

import com.hyphenate.chat.EMClient;
import com.hyphenate.chat.EMImageMessageBody;
import com.hyphenate.chat.EMMessage;
import com.hyphenate.chat.EMTextMessageBody;
import com.hyphenate.easecallkit.EaseCallKit;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.exceptions.HyphenateException;
import com.hyphenate.util.EasyUtils;
import com.hyphenate.util.PathUtil;
import com.ycf.qianzhihe.DemoHelper;
import com.ycf.qianzhihe.MainActivity;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.Global;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.CollectInfo;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_data.GroupDetailInfo;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_data.PZInfo;
import com.ycf.qianzhihe.app.api.old_data.RoomInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.WebViewActivity;
import com.ycf.qianzhihe.app.domain.EmojiconExampleGroupData;
import com.ycf.qianzhihe.app.operate.UserOperateManager;
import com.ycf.qianzhihe.app.utils.BitmapUtil;
import com.ycf.qianzhihe.app.utils.DonwloadSaveImg;
import com.ycf.qianzhihe.app.utils.ImageUtil;
import com.ycf.qianzhihe.app.utils.MyAnimation;
import com.ycf.qianzhihe.app.utils.OnClickSuccessResult;
import com.ycf.qianzhihe.app.utils.my.MyHelper;
import com.ycf.qianzhihe.app.utils.my.Storage;
import com.ycf.qianzhihe.app.weight.CommonDialog;
import com.ycf.qianzhihe.app.weight.ease.EaseChatRecallPresenter;
import com.ycf.qianzhihe.app.weight.ease.EaseChatVoiceCallPresenter;
import com.ycf.qianzhihe.app.weight.ease.chatrow.EaseCustomChatRowProvider;
import com.ycf.qianzhihe.app.weight.ease.emojicon.EaseEmojiconMenu;
import com.ycf.qianzhihe.app.weight.ease.presenter.EaseChatLocationPresenter;
import com.ycf.qianzhihe.app.weight.ease.presenter.EaseChatRowPresenter;
import com.ycf.qianzhihe.app.weight.my_message.ChatApplyFreindNoticePresenter;
import com.ycf.qianzhihe.app.weight.my_message.ChatPromotionPresenter;
import com.ycf.qianzhihe.app.weight.my_message.ChatRechargePresenter;
import com.ycf.qianzhihe.app.weight.my_message.ChatRedPacketAckPresenter;
import com.ycf.qianzhihe.app.weight.my_message.ChatRedPacketAddAllPresenter;
import com.ycf.qianzhihe.app.weight.my_message.ChatRedPacketAddPresenter;
import com.ycf.qianzhihe.app.weight.my_message.ChatRedPacketAddroomPresenter;
import com.ycf.qianzhihe.app.weight.my_message.ChatRedPacketFinalPresenter;
import com.ycf.qianzhihe.app.weight.my_message.ChatRedPacketGoReturnPresenter;
import com.ycf.qianzhihe.app.weight.my_message.ChatRedPacketLeftPresenter;
import com.ycf.qianzhihe.app.weight.my_message.ChatRedPacketPresenter;
import com.ycf.qianzhihe.app.weight.my_message.ChatRedPacketPunishmentPresenter;
import com.ycf.qianzhihe.app.weight.my_message.ChatRedPacketReturnPresenter;
import com.ycf.qianzhihe.app.weight.my_message.ChatRedPacketTPresenter;
import com.ycf.qianzhihe.app.weight.my_message.ChatRedPacketUpRoomNamePresenter;
import com.ycf.qianzhihe.app.weight.my_message.ChatRedPacketWelfarePresenter;
import com.ycf.qianzhihe.app.weight.my_message.ChatRedPacketturnPresenter;
import com.ycf.qianzhihe.app.weight.my_message.ChatSureTurnPresenter;
import com.ycf.qianzhihe.app.weight.my_message.ChatWithdrawPresenter;
import com.ycf.qianzhihe.app.weight.my_message.ChatidCardPresenter;
import com.ycf.qianzhihe.app.weight.my_message.GroupNoticePresenter;
import com.ycf.qianzhihe.app.weight.my_message.SystemNoticePresenter;
import com.ycf.qianzhihe.app.weight.my_message.UserOperatorGroupPresenter;
import com.ycf.qianzhihe.app.weight.my_message.WalletPresenter;
import com.ycf.qianzhihe.section.account.activity.UserInfoDetailActivity;
import com.ycf.qianzhihe.section.chat.activity.ChatActivity;
import com.ycf.qianzhihe.section.chat.activity.SelContactActivity;
import com.ycf.qianzhihe.section.common.ChatCollectActivity;
import com.ycf.qianzhihe.section.common.MyCollectActivity;
import com.ycf.qianzhihe.section.common.RealAuthActivity;
import com.ycf.qianzhihe.section.common.RedPacketDetailActivity;
import com.ycf.qianzhihe.section.common.SendGroupRedPackageActivity;
import com.ycf.qianzhihe.section.common.SendPersonRedPackageActivity;
import com.ycf.qianzhihe.section.common.TransferActivity;
import com.ycf.qianzhihe.section.common.TransferDetailActivity;
import com.ycf.qianzhihe.section.common.WithdrawActivity;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.NumberUtils;
import com.zds.base.util.StringUtil;
import com.zxy.tiny.Tiny;
import com.zxy.tiny.callback.BitmapCallback;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ChatFragment extends BaseChatFragment implements BaseChatFragment.EaseChatFragmentHelper {

    /**
     * constant start from 11 to avoid conflict with constant in base class
     */
    private static final int ITEM_VOICE_CALL = 13;
    private static final int ITEM_VIDEO_CALL = 14;
    private static final int REQUEST_CODE_SELECT_VIDEO = 11;
    private static final int REQUEST_CODE_SELECT_FILE = 12;
    private static final int REQUEST_CODE_GROUP_DETAIL = 13;
    private static final int REQUEST_CODE_CONTEXT_MENU = 14;
    private static final int REQUEST_CODE_SELECT_AT_USER = 15;
    private static final int MESSAGE_TYPE_SENT_VOICE_CALL = 1;
    private static final int MESSAGE_TYPE_RECV_VOICE_CALL = 2;
    private static final int MESSAGE_TYPE_SENT_VIDEO_CALL = 3;
    private static final int MESSAGE_TYPE_RECV_VIDEO_CALL = 4;
    private static final int MESSAGE_TYPE_RECALL = 9;
    /**
     * red packet code : 红包功能使用的常量
     */
    private static final int MESSAGE_TYPE_RECV_RED_PACKET = 5;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET = 6;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_ACK = 7;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_ACK = 8;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_FIN = 9;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_FIN = 10;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_RETURN = 11;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_RETURN = 12;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_TURN = 13;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_TURN = 14;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_ADD = 15;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_ADD = 16;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_DEL = 17;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_DEL = 18;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_T = 19;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_T = 20;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_ADDROOM = 21;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_ADDROOM = 22;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_ADDS = 23;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_ADDS = 24;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_RECHARGE = 25;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_RECHARGE = 26;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_PUNISHMENT = 27;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_PUNISHMENT = 28;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_FL = 29;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_FL = 30;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_RTH = 31;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_RTH = 32;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_PROMOTION = 33;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_PROMOTION = 34;
    private static final int MESSAGE_TYPE_SEND_RED_PACKET_RENAME = 36;
    private static final int MESSAGE_TYPE_RECV_RED_PACKET_RENAME = 35;
    private static final int MESSAGE_TYPE_SEND_WITHDRAW = 38;
    private static final int MESSAGE_TYPE_RECV_WITHDRAW = 37;
    private static final int MESSAGE_NOTICE_SEND = 40;
    private static final int MESSAGE_NOTICE_RECV = 39;
    private static final int MESSAGE_CARD_SEND = 41;
    private static final int MESSAGE_CARD_RECV = 42;
    private static final int MESSAGE_LOCATION_SEND = 43;
    private static final int MESSAGE_LOCATION_SRECV = 44;

    private static final int ITEM_RED_PACKET2 = 23;
    private static final int ITEM_TRANSFER = 17;
    private static final int ITEM_RECORD = 19;
    private static final int ITEM_MY_RED_PACKET = 18;
    private static final int ITEM_MY_CHONGZHI = 20;
    private static final int ITEM_MY_tixian = 21;


    private static final int ITEM_RED_PACKET = 16;
    private static final int ITEM_SEND_CARD = 50;
    private static final int ITEM_INVITE_GROUP_CHAT = 51;
    private static final int ITEM_INVITE_GROUP_CAMERA = 56;
    private static final int ITEM_MY_COLLECT = 52;

    static final int ITEM_YUY = 112;
    public static final int ITEM_ZHANGKAI = 113;



    private static final int MESSAGE_APPLY_FRIEND_SEND = 45;
    private static final int MESSAGE_APPLY_FRIEND_SRECV = 46;

    public static final int ITEM_MENU_DEL = 1;
    public static final int ITEM_MENU_SAVE_IMAGE = 2;
    public static final int ITEM_MENU_COLLECT = 3;
    public static final int ITEM_MENU_RECALL = 5;
    /**
     * ITEM_CONTACT_GROUP_OWER 联系群主
     * ITEM_SEND_RED 发红包
     */
    private static final int ITEM_CONTACT_GROUP_OWER = 110;
    private static final int ITEM_SEND_RED = 111;
    private static final int SYSTEM_NOTICE = 116;

    //end of red packet code
    /**
     * if it is chatBot
     */
    private boolean isRobot;

    /**
     * 旋转动画
     */
    protected MyAnimation operatingAnim;

    private String mFilePath = "";


    protected boolean isRoaming() {
        return true/*MyHelper.getInstance().getModel().isMsgRoaming() && (chatType != EaseConstant.CHATTYPE_CHATROOM)*/;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        mFilePath = getTakePhotoFilePath();
        operatingAnim = new MyAnimation();
        setChatFragmentHelper(this);
        super.initView(savedInstanceState);
        if (chatType == Constant.CHATTYPE_SINGLE) {
            mLlMoneyShow.setVisibility(View.GONE);
        } else if (chatType == EaseConstant.CHATTYPE_GROUP) {

            startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            //禁言
        }

        titleBar.setLeftLayoutClickListener(v -> {
            if (EasyUtils.isSingleActivity(requireActivity())) {
                MainActivity.actionStart(requireContext());
            }
            onBackPressed();
        });
    }

    @Override
    protected void initData() {
        super.initData();

        //增加兔斯基表情
        if (NumberUtils.parseDouble(UserComm.getUserInfo().getVipLevel()) != 0) {
            ((EaseEmojiconMenu) mInputMenu.getEmojiconMenu()).addEmojiconGroup(EmojiconExampleGroupData.getData());
        }
    }

    private boolean isregister;

    @Override
    protected void registerExtendMenuItem() {
        super.registerExtendMenuItem();

        //use the menu in base class
        if (chatType == Constant.CHATTYPE_SINGLE) {

            //语音
            mInputMenu.registerExtendSmallMenuItem(R.mipmap.yuy,
                    ITEM_YUY, extendMenuItemClickListener);
            //图片
            mInputMenu.registerExtendSmallMenuItem(R.mipmap.icon_zp,
                    ITEM_PICTURE, extendMenuItemClickListener);
            //相机
            mInputMenu.registerExtendSmallMenuItem(R.mipmap.icon_xj,
                    ITEM_INVITE_GROUP_CAMERA, extendMenuItemClickListener);
            //红包
            mInputMenu.registerExtendSmallMenuItem(R.mipmap.icon_hb,
                    ITEM_MY_RED_PACKET, extendMenuItemClickListener);
            //位置
            mInputMenu.registerExtendSmallMenuItem(R.mipmap.icon_wz,
                    ITEM_LOCATION, extendMenuItemClickListener);
            //展开
            mInputMenu.registerExtendSmallMenuItem(R.mipmap.zhankai,
                    ITEM_ZHANGKAI, extendMenuItemClickListener);


            /*//图片
            mInputMenu.registerExtendMenuItem("图片", R.mipmap.zhaop,
                    ITEM_PICTURE, extendMenuItemClickListener);
            //相机
            mInputMenu.registerExtendMenuItem("相机", R.mipmap.xj_11,
                    ITEM_INVITE_GROUP_CAMERA, extendMenuItemClickListener);*/
            //语音电话
            mInputMenu.registerExtendMenuItem("语音电话", R.mipmap.yuyt,
                    ITEM_VOICE_CALL, extendMenuItemClickListener);
            //视频通话
            mInputMenu.registerExtendMenuItem("视频通话", R.mipmap.ship,
                    ITEM_VIDEO_CALL, extendMenuItemClickListener);
            /*//位置
            mInputMenu.registerExtendMenuItem("位置", R.mipmap.dinwei,
                    ITEM_LOCATION, extendMenuItemClickListener);*/
            //名片
            mInputMenu.registerExtendMenuItem("名片", R.mipmap.minp,
                    ITEM_SEND_CARD, extendMenuItemClickListener);
            /*//红包
            mInputMenu.registerExtendMenuItem("红包", R.mipmap.hongbao,
                    ITEM_MY_RED_PACKET, extendMenuItemClickListener);*/
            //转账
            mInputMenu.registerExtendMenuItem("转账", R.mipmap.zhuanz,
                    ITEM_TRANSFER, extendMenuItemClickListener);
//            mInputMenu.registerExtendMenuItem("收藏", R.mipmap.sc_11,
//                    ITEM_MY_COLLECT, extendMenuItemClickListener);

        } else if (chatType == Constant.CHATTYPE_GROUP) {
            //语音
            mInputMenu.registerExtendSmallMenuItem(R.mipmap.yuy,
                    ITEM_YUY, extendMenuItemClickListener);
            //图片
            mInputMenu.registerExtendSmallMenuItem(R.mipmap.icon_zp,
                    ITEM_PICTURE, extendMenuItemClickListener);
            //相机
            mInputMenu.registerExtendSmallMenuItem(R.mipmap.icon_xj,
                    ITEM_INVITE_GROUP_CAMERA, extendMenuItemClickListener);
            //语音电话
          /*  mInputMenu.registerExtendSmallMenuItem(R.mipmap.yuyt_small,
                    ITEM_VOICE_CALL, extendMenuItemClickListener);*/
            //视频通话
            /*mInputMenu.registerExtendSmallMenuItem(R.mipmap.ship_small,
                    ITEM_VIDEO_CALL, extendMenuItemClickListener);*/
            //收藏
//            mInputMenu.registerExtendSmallMenuItem(R.mipmap.sc_ll_small,
//                    ITEM_MY_COLLECT, extendMenuItemClickListener);
            //红包
            mInputMenu.registerExtendSmallMenuItem(R.mipmap.icon_hb,
                    ITEM_MY_RED_PACKET, extendMenuItemClickListener);
            //位置
            mInputMenu.registerExtendSmallMenuItem(R.mipmap.icon_wz,
                    ITEM_LOCATION, extendMenuItemClickListener);
            //转账
            /*mInputMenu.registerExtendSmallMenuItem(R.mipmap.zhuanz_small,
                    ITEM_TRANSFER, extendMenuItemClickListener);*/
            //展开
           /* mInputMenu.registerExtendSmallMenuItem(R.mipmap.zhankai,
                    ITEM_ZHANGKAI, extendMenuItemClickListener);*/

            //名片
            mInputMenu.registerExtendSmallMenuItem(R.mipmap.icon_mp,
                    ITEM_SEND_CARD, extendMenuItemClickListener);
        }
        mInputMenu.setChatType(chatType);

    }

    private void registerMenu() {
        for (RoomInfo.RoomLeiListBean roomLeiListBean :
                roomInfo.getRoomLeiList()) {
            if (roomLeiListBean.getType() != null && roomLeiListBean.getType().equals("1")) {
                //发红包
                mInputMenu.registerMenuItemNumber("",
                        roomLeiListBean.getAmount(), ITEM_RED_PACKET,
                        extendMenuItemClickListener);
            } else if (roomLeiListBean.getType() != null && roomLeiListBean.getType().equals("2")) {
                //发红包
                mInputMenu.registerMenuItemNumber("",
                        roomLeiListBean.getAmount(), ITEM_RED_PACKET2,
                        extendMenuItemClickListener);
            }
        }

        //图片
        mInputMenu.registerExtendMenuItem("", R.mipmap.zhaop,
                ITEM_PICTURE, extendMenuItemClickListener);
    }

    @Override
    protected void onEventComing(EventCenter center) {//E1E1E3
        super.onEventComing(center);
        if (center.getEventCode() == EventUtil.REGISTERBUTTON) {
            if (roomType > 0 && !isregister) {
                if (chatType == Constant.CHATTYPE_GROUP) {
                    if (roomType > 0 && roomInfo != null && !isregister) {
                        isregister = true;
                        registerMenu();
                    }
                }
            }
        } else if (center.getEventCode() == EventUtil.UPDATE_PACK) {
            mInputMenu.getExtendMenu().removeAllViews();
            registerMenu();
        } else if (center.getEventCode() == EventUtil.DEL_EXIT_GROUP) {
            requireActivity().finish();
        } else if (center.getEventCode() == EventUtil.OPERATE_BLACK) {
            requireActivity().finish();
        } else if (center.getEventCode() == EventUtil.REFRESH_REMARK) {

            try {
                mMessageList.refresh();
                titleBar.setTitle(UserOperateManager.getInstance().getUserName(emChatId));
            } catch (Exception ignored) {
            }
        } else if (center.getEventCode() == EventUtil.REFRESH_MESSAGE_LIST) {
            mMessageList.refresh();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                //send the video
                case REQUEST_CODE_SELECT_VIDEO:
                    if (data != null) {
                        int duration = data.getIntExtra(Constant.DUR, 0);
                        String videoPath = data.getStringExtra(Constant.PATH);
                        File file =
                                new File(PathUtil.getInstance().getImagePath(), "thvideo" + System.currentTimeMillis());
                        try {
                            FileOutputStream fos = new FileOutputStream(file);
                            Bitmap ThumbBitmap =
                                    ThumbnailUtils.createVideoThumbnail(videoPath, 3);
                            ThumbBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                            fos.close();
                            sendVideoMessage(videoPath,
                                    file.getAbsolutePath(), duration);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                //send the file
                case REQUEST_CODE_SELECT_FILE:
                    if (data != null) {
                        Uri uri = data.getData();
                        if (uri != null) {
                            sendFileByUri(uri);
                        }
                    }
                    break;
                case REQUEST_CODE_SELECT_AT_USER:
                    if (data != null) {
                        String userId = data.getStringExtra("username");
                        inputAtUsername(userId, false);
                    }
                    break;

                case REQUEST_CODE_MAP:
                    // location
                    double latitude = data.getDoubleExtra(Constant.LATITUDE, 0);
                    double longitude = data.getDoubleExtra(Constant.LONGITUDE, 0);
                    String locationAddress = data.getStringExtra(Constant.ADDRESS);
                    String locationAddressDetail = data.getStringExtra(
                            Constant.ADDRESS_DETAIL);
                    String path = data.getStringExtra(Constant.PATH);

                    if (locationAddress != null && !"".equals(locationAddress)) {
                        sendCustomLocationMessage(latitude, longitude,
                                locationAddress, locationAddressDetail, path);
                    } else {
                        Toast.makeText(requireActivity(),
                                R.string.unable_to_get_loaction,
                                Toast.LENGTH_SHORT).show();
                    }
                    break;

                case REQUEST_CODE_SEND_CARD:
                    // 发送名片
                    String userName = data.getStringExtra("username");
                    String nickName = data.getStringExtra("nickname");
                    String avatar = data.getStringExtra("avatar");
                    String friendUserId = data.getStringExtra("friendUserId");

                    if (nickName != null && !"".equals(nickName)) {
                        sendExTextMessage("个人名片", nickName, userName, avatar,
                                friendUserId);
                    } else {
                        Toast.makeText(requireActivity(), "名片获取失败",
                                Toast.LENGTH_SHORT).show();
                    }
                    break;
                case REQUEST_CODE_CONTEXT_MENU:
                    switch (data.getIntExtra("MenuFlag", 1)) {
                        case ITEM_MENU_COLLECT:
                            chatCollect();
                            break;
                        case ITEM_MENU_SAVE_IMAGE:
                            saveImage();
                            break;
                        case ITEM_MENU_DEL:
                            delMessage(data);
                            break;
                        case ITEM_MENU_RECALL:
                            recallMessage(data);
                            break;
                    }
                    break;
                case REQUEST_CODE_TRNSFER://转账成功返回会话窗，发送消息至sdk
                    String money = data.getStringExtra("money");
                    String remark = data.getStringExtra("remark");
                    String turnId = data.getStringExtra("turnId");
                    String serialNumber = data.getStringExtra("serialNumber");
                    String requestId = data.getStringExtra("requestId");

                    EMMessage message = EMMessage.createTxtSendMessage("转账", emChatId);
                    message.setAttribute(Constant.TURN, true);
                    message.setAttribute("money", money);
                    message.setAttribute("remark", remark);
                    message.setAttribute("turnId", turnId);
                    message.setAttribute("serialNumber", serialNumber);
                    message.setAttribute("requestId", requestId);
                    sendMessage(message);
                    break;
                case REQUEST_CODE_PERSON_BALL:
//                    String money1 = data.getStringExtra("money");
//                    String remark1 = data.getStringExtra("remark");
//                    String redID = data.getStringExtra("redId");
//
//                    EMMessage message1 = EMMessage.createTxtSendMessage("红包",
//                            emChatId);
//                    message1.setAttribute(Constant.MSGTYPE,
//                            Constant.PERSON_RED_BAG);
//                    message1.setAttribute("money", money1);
//                    message1.setAttribute("remark", remark1);
//                    message1.setAttribute(Constant.REDPACKETID, redID);
//                    message1.setAttribute("redPacketType", "1");
//                    message1.setAttribute(Constant.AVATARURL,
//                            UserComm.getUserInfo().getUserHead());
//                    message1.setAttribute(Constant.NICKNAME,
//                            UserComm.getUserInfo().getNickName());
//                    sendMessage(message1);
                    break;
                case 1024:
                    FileInputStream is = null;
                    try {
                        // 获取输入流
                        is = new FileInputStream(mFilePath);
                        // 把流解析成bitmap,此时就得到了清晰的原图
                        Bitmap bitmap = BitmapFactory.decodeStream(is);
                        //接下来就可以展示了（或者做上传处理）
                        String p = DonwloadSaveImg.saveFile(bitmap, requireContext());
                        EMMessage m = EMMessage.createImageSendMessage(p, false
                                , emChatId);
                        m.setAttribute(Constant.AVATARURL,
                                UserComm.getUserInfo().getUserHead());
                        m.setAttribute(Constant.NICKNAME,
                                UserComm.getUserInfo().getNickName());
                        sendMessage(m);
                    } catch (FileNotFoundException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } finally {
                        // 关闭流
                        try {
                            if (is != null) {
                                is.close();
                            }
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    break;
                default:
                    break;
            }
        } else if (requestCode == 1023 && resultCode == 1023) {
            String json = data.getStringExtra(Constant.JSON);
            if (!StringUtil.isEmpty(json)) {
                CollectInfo.DataBean bean = FastJsonUtil.getObject(json, CollectInfo.DataBean.class);
                if (bean != null) {
                    if (bean.getItemType() == 1) {
                        //text
                        EMMessage message = EMMessage.createTxtSendMessage(bean.getLinkContent(),
                                emChatId);
                        message.setAttribute(Constant.AVATARURL,
                                UserComm.getUserInfo().getUserHead());
                        message.setAttribute(Constant.NICKNAME,
                                UserComm.getUserInfo().getNickName());
                        sendMessage(message);
                    } else {
                        //image

                        DonwloadSaveImg.donwloadImg(requireContext(), AppConfig.checkimg(bean.getLinkContent()), new OnClickSuccessResult() {
                            @Override
                            public void sunccess(@NotNull String path) {
                                EMMessage message = EMMessage.createImageSendMessage(path, false
                                        , emChatId);
                                message.setAttribute(Constant.AVATARURL,
                                        UserComm.getUserInfo().getUserHead());
                                message.setAttribute(Constant.NICKNAME,
                                        UserComm.getUserInfo().getNickName());
                                sendMessage(message);
                            }
                        });

                    }
                }
            }
        }
    }

    private void saveImage() {
        int type = contextMenuMessage.getType().ordinal();
        String linkContent = "";
        if (type == EMMessage.Type.TXT.ordinal()) {
            type = 1;
            linkContent =
                    ((EMTextMessageBody) contextMenuMessage.getBody()).getMessage();
            saveCollect(type, linkContent);
        } else if (type == EMMessage.Type.IMAGE.ordinal()) {
            type = 2;
            String linkContentPic =
                    ((EMImageMessageBody) contextMenuMessage.getBody()).getLocalUrl();
            if (linkContentPic != null) {
                File file = new File(linkContentPic);
                if (!file.exists()) {
                    // send thumb nail if original image does not exist
                    linkContentPic =
                            ((EMImageMessageBody) contextMenuMessage.getBody()).thumbnailLocalPath();
                }
            }
            saveFile(linkContentPic);
        }

    }

    private void delMessage(Intent data) {
//        //删除当前会话的某条聊天记录
//        EMConversation conversation = EMClient.getInstance().chatManager().getConversation(username);
        String msgId = data.getStringExtra("msgId");
        conversation.removeMessage(msgId);

    }

    private void recallMessage(Intent data) {
        try {
            String msgId = data.getStringExtra("msgId");
            EMMessage recallMessag = conversation.getMessage(msgId, false);
            EMClient.getInstance().chatManager().recallMessage(recallMessag);
        } catch (HyphenateException e) {
            e.printStackTrace();
        }


    }

    private void saveFile(String file) {
//        Bitmap QeBitmap = EncodingHandler.createQRCode(yuming + AppConfig.QR + MyApplication.getInstance().getUserInfo().getInviteCode(), 800, 800, BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher));
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(file);
            Bitmap bitmap = BitmapFactory.decodeStream(fis);
            Tiny.getInstance().source(bitmap).asBitmap().compress(new BitmapCallback() {
                @Override
                public void callback(boolean isSuccess, Bitmap bitmap1, Throwable t) {
                    if (isSuccess) {
                        BitmapUtil.saveBitmapInFile(requireActivity(), bitmap1);
                        ToastUtil.toast("保存成功");
                    } else {
                        ToastUtil.toast("保存失败");
                    }
                }
            });


        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onSetMessageAttributes(EMMessage message) {
        if (isRobot) {
            //set message extension
            message.setAttribute("em_robot_message", isRobot);
        }


    }

    @Override
    public EaseCustomChatRowProvider onSetCustomChatRowProvider() {
        return new CustomChatRowProvider();
    }


    @Override
    public void onEnterToChatDetails() {

    }

    @Override
    public void onAvatarClick(String userId) {
        Global.addUserOriginType = Constant.ADD_USER_ORIGIN_TYPE_GROUPCHAT;
        Global.addUserOriginName = groupName;
        Global.addUserOriginId = groupId;

        Intent intent = new Intent(requireContext(),
                UserInfoDetailActivity.class);
        intent.putExtra("friendUserId", userId)
                .putExtra("from", "1")
                .putExtra("chatType", chatType);

        if (chatType == EaseConstant.CHATTYPE_GROUP && groupDetailInfo.getGroupUserRank() != 0) {
            intent.putExtra(Constant.PARAM_GROUP_ID, groupId);
            intent.putExtra(Constant.PARAM_EM_GROUP_ID, emChatId);
        }
        if (groupDetailInfo != null && groupDetailInfo.getGroupUserDetailVoList() != null)
            for (GroupDetailInfo.GroupUserDetailVoListBean groupUserDetailVoListBean : groupDetailInfo.getGroupUserDetailVoList()) {
                if (userId.contains(groupUserDetailVoListBean.getUserId())) {
                    intent.putExtra("entryUserId", groupUserDetailVoListBean.getEntryUserId());
                }
            }
        requireContext().startActivity(intent);

        //handling when user click avatar
//        Intent intent = new Intent(requireActivity(), UserInfoActivity.class);
//        intent.putExtra("username", username);
//        startActivity(intent);
    }

    @Override
    public void onAvatarLongClick(String userId) {
        inputAtUsername(userId);
//         inputAtUsername(username);
    }


    @Override
    public boolean onMessageBubbleClick(EMMessage message) {
        //消息框点击事件，demo这里不做覆盖，如需覆盖，return true
        if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.REDPACKET)
                || message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.PERSON_RED_BAG)) {

            message.setAttribute("isClick", true);
            EMClient.getInstance().chatManager().saveMessage(message);
            checkRedPacket(message);
            return true;
        }
        if (message.getBooleanAttribute(Constant.TURN, false)) {
            checkTransfer(message);
            return true;
        }

        return false;
    }

    /**
     * 检测转账
     *
     * @param message
     */
    private void checkTransfer(EMMessage message) {
        String userId = message.getFrom();
        String turnId = message.getStringAttribute("turnId", "");
        String money = message.getStringAttribute("money", "");
        String serialNumber = message.getStringAttribute("serialNumber", "");
        String requestId = message.getStringAttribute("requestId", "");
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("transferId", turnId);
        ApiClient.requestNetHandle(requireContext(), AppConfig.TRANSFER_STATUS
                , "", map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        String j = json.replace(".0", "");
                        if ("2".equals(j)) {
                            ToastUtil.toast("金额已退回");
                            return;
                        }
                        Intent intent = new Intent(requireContext(), TransferDetailActivity.class);
                        Bundle bundle = new Bundle();
                        bundle.putString("status", j);
                        bundle.putString("money", money);
                        bundle.putString("turnId", turnId);
                        bundle.putString("serialNumber", serialNumber);
                        bundle.putString("requestId", requestId);
                        bundle.putBoolean("isSelf",
                                userId.split("-")[0].equals(UserComm.getUserInfo().getUserId()));
                        intent.putExtras(bundle);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });
    }

    @Override
    public void onCmdMessageReceived(List<EMMessage> messages) {
        super.onCmdMessageReceived(messages);
//        for (EMMessage emMessage:messages){
//            EMClient.getInstance().chatManager().saveMessage(emMessage);
//        }
    }

    @Override
    public void onMessageBubbleLongClick(EMMessage message) {
        if (message.getType() == EMMessage.Type.TXT || message.getType() == EMMessage.Type.IMAGE) {
            try {
                if (message.getStringAttribute(Constant.MSGTYPE).equals(Constant.REDPACKET)) {
                    return;
                }

            } catch (HyphenateException e) {
                e.printStackTrace();
            }


            if (message.getBooleanAttribute(Constant.TURN, false) || message.getBooleanAttribute(Constant.SEND_CARD, false)) {
                return;
            }


            //长按消息
            startActivityForResult((new Intent(requireActivity(), ChatCollectActivity.class))
                    .putExtra("message", message)
                    .putExtra("ischatroom", chatType == EaseConstant.CHATTYPE_CHATROOM), REQUEST_CODE_CONTEXT_MENU);
        }

    }


    @Override
    public boolean onExtendMenuItemClick(int itemId, View view) {
        switch (itemId) {
            case ITEM_YUY:
                mInputMenu.chatPrimaryMenu.switchModeKeyboardOrVoice();
                break;
            case ITEM_ZHANGKAI:
                mInputMenu.chatPrimaryMenu.onToggleExtendClicked();
                break;
            case ITEM_MY_COLLECT:
                startActivityForResult(new Intent(requireContext(), MyCollectActivity.class), 1023);
                break;
            case ITEM_VOICE_CALL:
                DemoHelper.getInstance().setEaseCallKitUserInfo(emChatId);
                startVoiceCall(emChatId, UserComm.getUserInfo().getUserHead());
//                startVideoCall();
                break;
            case ITEM_VIDEO_CALL:
                DemoHelper.getInstance().setEaseCallKitUserInfo(emChatId);
                startVideoCall(emChatId, UserComm.getUserInfo().getUserHead());
                break;
            case ITEM_CONTACT_GROUP_OWER:

                startActivity(new Intent(requireContext(), ChatActivity.class)
                        .putExtra("userId", roomInfo.getUserId() + Constant.ID_REDPROJECT)
                        .putExtra("groups_Id", roomInfo.getHuanxinGroupId()));

                break;
            case ITEM_MY_RED_PACKET:
                //个人红包
                if (chatType == EaseConstant.CHATTYPE_GROUP) {

                    startActivity(new Intent(requireActivity(), SendGroupRedPackageActivity.class)
                            .putExtra("groupId", groupId)
                            .putExtra(Constant.PARAM_EM_GROUP_ID, emChatId)
                            .putExtra("key_intent_group_user_count", groupDetailInfo.getGroupUsers())
                    );

                } else if (chatType == EaseConstant.CHATTYPE_SINGLE) {
                    startActivityForResult(new Intent(requireActivity(),
                                    SendPersonRedPackageActivity.class).putExtra(
                            "username", emChatId),
                            REQUEST_CODE_PERSON_BALL);
                }
                break;
            case ITEM_TRANSFER:
                //转账
                startActivityForResult(new Intent(requireActivity(),
                        TransferActivity.class/*TransferNewActivity.class*/).putExtra("emChatId",
                        emChatId), REQUEST_CODE_TRNSFER);

                break;
            case ITEM_RECORD:
                //转账记录

                break;
            case ITEM_MY_CHONGZHI:
                //充值
                PZInfo pzInfo = Storage.GetPZ();
                if (pzInfo != null) {
                    startActivity(new Intent(requireActivity(),
                            WebViewActivity.class)
                            .putExtra("title", "充值")
                            .putExtra("url", pzInfo.getRechargeUrl() + UserComm.getToken()));
                }
                break;
            case ITEM_MY_tixian:
                //提现
                if (UserComm.getUserInfo().getOpenAccountFlag() == 0) {
//                    showAuthDialog();
                    RealAuthActivity.actionStart(mContext);
                } else {
                    startActivity(new Intent(requireActivity(),
                            WithdrawActivity.class/*WithdrawNewActivity.class*/));
                }

                break;
            case ITEM_SEND_CARD:
                //发送名片
                startActivityForResult(new Intent(requireActivity(),
                        SelContactActivity.class).putExtra("userId", emChatId), REQUEST_CODE_SEND_CARD);
                break;

            case ITEM_INVITE_GROUP_CAMERA:
                // 获取SD卡路径
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    takePhotoBiggerThan7((new File(mFilePath)).getAbsolutePath());
                } else {
                    // 指定拍照意图
                    Intent openCameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    // 加载路径图片路径
                    Uri mUri = Uri.fromFile(new File(mFilePath));
                    // 指定存储路径，这样就可以保存原图了
                    openCameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, mUri);
                    startActivityForResult(openCameraIntent, 1024);
                }

                break;
            default:
                break;
        }
        return false;
    }

    private CommonDialog.Builder builderAuth;
    private EditText etName, etCard, etPhone;

    /**
     * 实名认证弹窗
     */
    private void showAuthDialog() {
        if (builderAuth != null) {
            builderAuth.dismiss();
        }
        builderAuth =
                new CommonDialog.Builder(requireActivity()).fullWidth().center()
                        .setView(R.layout.dialog_custinfo);

        builderAuth.setOnClickListener(R.id.tv_sure,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //确定
                        if (etName.getText().toString().trim().length() <= 0) {
                            ToastUtil.toast("请填写姓名");
                            return;
                        } else if (etCard.getText().toString().trim().length() <= 0) {
                            ToastUtil.toast("请填写身份证号码");
                            return;
                        } else if (etPhone.getText().toString().trim().length() <= 0) {
                            ToastUtil.toast("请填写手机号");
                            return;
                        }
                        openAccount(etName.getText().toString().trim(),
                                etCard.getText().toString().trim(),
                                etPhone.getText().toString().trim());

                    }
                });
        builderAuth.setOnClickListener(R.id.img_close,
                v -> builderAuth.dismiss());
        CommonDialog dialog = builderAuth.create();
        etName = (EditText) dialog.getView(R.id.et_name);
        etCard = (EditText) dialog.getView(R.id.et_card);
        etPhone = (EditText) dialog.getView(R.id.et_phone);
        dialog.show();
    }

    private boolean isrenzheng;

    /**
     * 认证
     */
    private void openAccount(String name, String certificateNo, String mobile) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("certificateNo", certificateNo);
        map.put("mobile", mobile);
        if (isrenzheng == true) {
            ToastUtil.toast("认证中，请勿重复提交");
            return;
        }
        isrenzheng = true;
        ApiClient.requestNetHandle(requireActivity(), AppConfig.openAccount,
                "认证中", map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        LoginInfo loginInfo =
                                UserComm.getUserInfo();
                        loginInfo.setOpenAccountFlag(1);
                        UserComm.saveUsersInfo(loginInfo);
                        ToastUtil.toast(msg);
                        if (builderAuth != null) {
                            builderAuth.dismiss();
                        }
                    }

                    @Override
                    public void onFinsh() {
                        super.onFinsh();
                        isrenzheng = false;
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);

                    }
                });
    }


    /**
     * 收藏
     */
    private void chatCollect() {
        int type = contextMenuMessage.getType().ordinal();
        String linkContent = "";
        if (type == EMMessage.Type.TXT.ordinal()) {
            type = 1;
            linkContent =
                    ((EMTextMessageBody) contextMenuMessage.getBody()).getMessage();
            saveCollect(type, linkContent);
        } else if (type == EMMessage.Type.IMAGE.ordinal()) {
            type = 2;
            String linkContentPic =
                    ((EMImageMessageBody) contextMenuMessage.getBody()).getLocalUrl();
            if (linkContentPic != null) {
                File file = new File(linkContentPic);
                if (!file.exists()) {
                    // send thumb nail if original image does not exist
                    linkContentPic =
                            ((EMImageMessageBody) contextMenuMessage.getBody()).thumbnailLocalPath();
                }
            }
            savePic(linkContentPic);
        }

    }

    private void saveCollect(int type, String linkContent) {
        Map<String, Object> map = new HashMap<>(2);
        //1-文本消息 2-图片消息 3-视频
        map.put("linkType", type);
        //收藏内容
        map.put("linkContent", linkContent);

        ApiClient.requestNetHandle(requireContext(), AppConfig.addCollect, "",
                map, new ResultListener() {

                    @Override
                    public void onSuccess(String json, String msg) {
                        ToastUtil.toast("收藏成功");
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });
    }


    /**
     * 保存收藏的图片到服务器
     *
     * @param filePath
     */
    private void savePic(String filePath) {

        ApiClient.requestNetHandleFile(requireContext(), AppConfig.uploadImg,
                "", new File(filePath), new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        saveCollect(2, json);

                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });

    }


    /**
     * 检测红包
     */
    private void checkRedPacket(final EMMessage emMessage) {
        String userId = emMessage.getFrom();
        final String head = emMessage.getStringAttribute(Constant.AVATARURL,
                "");
        final String nickname =
                emMessage.getStringAttribute(Constant.NICKNAME, "");
        final String id = emMessage.getStringAttribute(Constant.REDPACKETID,
                "");

        if (userId.split("-")[0].equals(UserComm.getUserInfo().getUserId()) && emMessage.getChatType() == EMMessage.ChatType.Chat) {
            //判断是群组还是个人消息 传入红包详情页面
            if (chatType == Constant.CHATTYPE_SINGLE) {
                startActivity(new Intent(requireActivity(), RedPacketDetailActivity.class).putExtra("rid", id).putExtra("head", head).putExtra(
                        "nickname", nickname).putExtra("type", "1"));
            } else {
                startActivity(new Intent(requireActivity(), RedPacketDetailActivity.class).putExtra("rid", id).putExtra("head", head).putExtra(
                        "nickname", nickname).putExtra("type", "0"));
            }
            return;
        }
        Map<String, Object> map = new HashMap<>();
        map.put("redPacketId", id);
        ApiClient.requestNetHandle(requireActivity(),
                AppConfig.getRedEnvelopeState, "正在加载", map,
                new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        if (json != null) {
                            double state;
                            try {
                                state = Double.parseDouble(json);
                            } catch (Exception e) {
                                return;
                            }
                            //红包已过期: -1
                            // 红包未过期: 未抢完[已参与 10  未参与 11], 已抢完[已参与 20  未参与 21]
                            if (state == 20 || state == 10) {

                                //判断是群组还是个人消息 传入红包详情页面
                                if (chatType == Constant.CHATTYPE_SINGLE) {
                                    startActivity(new Intent(requireActivity(),
                                            RedPacketDetailActivity.class).putExtra("rid"
                                            , id).putExtra("head", head).putExtra(
                                            "nickname", nickname).putExtra("type", "1"));
                                } else {
                                    startActivity(new Intent(requireActivity(),
                                            RedPacketDetailActivity.class).putExtra("rid"
                                            , id).putExtra("head", head).putExtra(
                                            "nickname", nickname).putExtra("type", "0"));
                                }
                            } else {
                                showRedPacket(state, emMessage);
                            }
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });
    }

    CommonDialog.Builder builder;

    private boolean isRabShow = false;

    /**
     * 显示红包弹窗
     */
    private void showRedPacket(double state, final EMMessage emMessage) {

        final String head = emMessage.getStringAttribute(Constant.AVATARURL,
                "");
        final String nickname =
                emMessage.getStringAttribute(Constant.NICKNAME, "");
        String remark = emMessage.getStringAttribute("remark", "恭喜发财，大吉大利!");
        final String id = emMessage.getStringAttribute(Constant.REDPACKETID,
                "");
        if (builder != null) {
            builder.dismiss();
        }
        View rootView =
                LayoutInflater.from(requireContext()).inflate(R.layout.dialog_red_packet, null, false);
        builder =
                new CommonDialog.Builder(requireActivity()).center().loadAniamtion()
                        .setView(rootView);
        builder.setOnClickListener(R.id.ll_closes, v -> builder.dismiss());

        /*if (emMessage.direct() == EMMessage.Direct.SEND) {
            View checkDetailView = rootView.findViewById(R.id.tv_check_detail);
            checkDetailView.setVisibility(View.VISIBLE);
            checkDetailView.setOnClickListener(view -> {
                builder.dismiss();
                startActivity(new Intent(requireActivity(),
                        RedPacketDetailActivity.class).putExtra("rid"
                        , id).putExtra("head", head).putExtra(
                        "nickname", nickname));

            });
        }*/

        builder.setText(R.id.tv_nick, nickname + "发出的红包");
        builder.setOnClickListener(R.id.img_open, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRabShow) {
                    ToastUtil.toast("抢红包中，请勿重复提交！");
                    return;
                }
                openAnimation(v);
                rabRedPacket(v, emMessage);
            }
        });
        builder.setOnClickListener(R.id.tv_red_detail,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (builder != null) {
                            builder.dismiss();
                        }
                        if (chatType == Constant.CHATTYPE_SINGLE) {
                            startActivity(new Intent(requireActivity(),
                                    RedPacketDetailActivity.class).putExtra("rid"
                                    , id).putExtra("head", head).putExtra(
                                    "nickname", nickname).putExtra("type", "1"));

                        } else {
                            startActivity(new Intent(requireActivity(),
                                    RedPacketDetailActivity.class).putExtra("rid"
                                    , id).putExtra("head", head).putExtra(
                                    "nickname", nickname).putExtra("type", "0"));
                        }

                    }
                });
        CommonDialog commonDialog = builder.create();
        builder.getView(R.id.tv_red_detail).setVisibility(View.VISIBLE);
        if (state == 11) {
            //红包未抢完 未过期 未参与
            builder.setText(R.id.tv_message,
                    (remark.equals("null") || remark.equals("")) ?
                            "恭喜发财，大吉大利！" : remark);
            builder.getView(R.id.rel_open).setVisibility(View.VISIBLE);
            if (emMessage.getFrom().equals(String.valueOf(UserComm.getUserInfo().getUserId()))) {
                builder.getView(R.id.tv_red_detail).setVisibility(View.VISIBLE);
            }
        } else if (state == 10) {
            //红包未抢完 未过期 已参与
            builder.setText(R.id.tv_message, "您已抢过红包！不能重复参与");
            builder.getView(R.id.rel_open).setVisibility(View.GONE);
            builder.getView(R.id.tv_red_detail).setVisibility(View.VISIBLE);
        } else if (state == 21) {
            //红包未过期 已抢完 未参与
            builder.setText(R.id.tv_message, "手慢了，红包派完了");
            builder.getView(R.id.rel_open).setVisibility(View.INVISIBLE);
            builder.getView(R.id.tv_red_detail).setVisibility(View.VISIBLE);
        } else if (state == -1) {
            //红包已过期
            builder.setText(R.id.tv_message, "红包过期");
            builder.getView(R.id.rel_open).setVisibility(View.INVISIBLE);
        }
        EaseImageView imageView =
                (EaseImageView) builder.getView(R.id.img_head);
        ImageUtil.setAvatar(imageView);
        GlideUtils.loadImageViewLodingByCircle(AppConfig.checkimg(head),
                imageView, R.mipmap.ic_ng_avatar);
        commonDialog.show();
    }

    /**
     * 抢红包
     */
    private void rabRedPacket(final View view, final EMMessage emMessage) {
        final String head = emMessage.getStringAttribute(Constant.AVATARURL,
                "");
        final String nickname =
                emMessage.getStringAttribute(Constant.NICKNAME, "");
        final String id = emMessage.getStringAttribute(Constant.REDPACKETID,
                "");
        isRabShow = true;
        Map<String, Object> map = new HashMap<>();
        map.put("redPacketId", id + "");
        ApiClient.requestNetHandle(requireActivity(), AppConfig.grabRedEnvelope,
                "", map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        isRabShow = false;
                        closeAnimation(view);
                        builder.dismiss();

                        if (chatType == Constant.CHATTYPE_SINGLE) {
                            startActivity(new Intent(requireActivity(),
                                    RedPacketDetailActivity.class).putExtra("rid"
                                    , id).putExtra("head", head).putExtra(
                                    "nickname", nickname).putExtra("type", "1"));

                        } else {
                            startActivity(new Intent(requireActivity(),
                                    RedPacketDetailActivity.class).putExtra("rid"
                                    , id).putExtra("head", head).putExtra(
                                    "nickname", nickname).putExtra("type", "0"));
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                        isRabShow = false;
                        closeAnimation(view);
                        builder.dismiss();
                    }
                });
    }

    /**
     * 开始动画
     */
    protected void openAnimation(View view) {
        if (operatingAnim != null) {
            view.startAnimation(operatingAnim);
        }
    }

    /**
     * 关闭动画
     */
    protected void closeAnimation(View view) {
        view.clearAnimation();
    }

    /**
     * select file
     */
    protected void selectFileFromLocal() {
        Intent intent = null;
        //api 19 and later, we can't use this way, demo just select from images
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("*/*");
            intent.addCategory(Intent.CATEGORY_OPENABLE);
        } else {
            intent = new Intent(Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_SELECT_FILE);
    }


    /**
     * make a video call
     *
     * @param emChatId
     */
    protected void startVideoCall(String emChatId, String avatarUrl) {
        if (!EMClient.getInstance().isConnected()) {
            Toast.makeText(requireActivity(), R.string.not_connect_to_server,
                    Toast.LENGTH_SHORT).show();
        } else {
            Map<String, Object> ext = new HashMap<>();
            ext.put(Constant.AVATARURL, avatarUrl);
            EaseCallKit.getInstance().startSingleCall(EaseCallType.SINGLE_VIDEO_CALL, emChatId, ext);
            mInputMenu.hideExtendMenuContainer();
        }
    }

    /**
     * 自定义聊天行提供者
     */
    private final class CustomChatRowProvider implements EaseCustomChatRowProvider {
        @Override
        public int getCustomChatRowTypeCount() {
            //here the number is the message type in EMMessage::Type
            //which is used to count the number of different chat row
            return 50;
        }

        @Override
        public int getCustomChatRowType(EMMessage message) {
            if (message.getType() == EMMessage.Type.TXT) {

                //voice call
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false)) {
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_TYPE_RECV_VOICE_CALL :
                            MESSAGE_TYPE_SENT_VOICE_CALL;
                } else if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    //video call
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_TYPE_RECV_VIDEO_CALL :
                            MESSAGE_TYPE_SENT_VIDEO_CALL;
                } else if (message.getBooleanAttribute(Constant.MESSAGE_TYPE_RECALL, false)) {
                    return MESSAGE_TYPE_RECALL;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.REDPACKET)
                        || message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.PERSON_RED_BAG)) {
                    //红包
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_TYPE_RECV_RED_PACKET :
                            MESSAGE_TYPE_SEND_RED_PACKET;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RAB) || message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RAB_SELF)) {
                    //抢红包
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_TYPE_RECV_RED_PACKET_ACK :
                            MESSAGE_TYPE_SEND_RED_PACKET_ACK;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RABEND)) {
                    //红包抢完
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_TYPE_RECV_RED_PACKET_FIN :
                            MESSAGE_TYPE_SEND_RED_PACKET_FIN;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RETURNGOLD)) {
                    //红包退还
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_TYPE_RECV_RED_PACKET_RETURN :
                            MESSAGE_TYPE_SEND_RED_PACKET_RETURN;
                } else if (message.getBooleanAttribute(Constant.TURN, false)) {
                    //转账
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_TYPE_RECV_RED_PACKET_TURN :
                            MESSAGE_TYPE_SEND_RED_PACKET_TURN;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.ADDUSER)) {
                    //加入房间
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_TYPE_RECV_RED_PACKET_ADD :
                            MESSAGE_TYPE_SEND_RED_PACKET_ADD;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.DELUSER)) {
                    //退出房间
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_TYPE_RECV_RED_PACKET_DEL :
                            MESSAGE_TYPE_SEND_RED_PACKET_DEL;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.SHOTUSER)) {
                    //踢人
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_TYPE_RECV_RED_PACKET_T :
                            MESSAGE_TYPE_SEND_RED_PACKET_T;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.ADDROOM)) {
                    //创建房间
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_TYPE_RECV_RED_PACKET_ADDROOM :
                            MESSAGE_TYPE_SEND_RED_PACKET_ADDROOM;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.ADDUSERS)) {
                    //创建房间
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_TYPE_RECV_RED_PACKET_ADDS :
                            MESSAGE_TYPE_SEND_RED_PACKET_ADDS;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RECHARGE)) {
                    //转账
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_TYPE_RECV_RED_PACKET_RECHARGE :
                            MESSAGE_TYPE_SEND_RED_PACKET_RECHARGE;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.PUNISHMENT)) {
                    //惩罚
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_TYPE_RECV_RED_PACKET_PUNISHMENT :
                            MESSAGE_TYPE_SEND_RED_PACKET_PUNISHMENT;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.WELFARE)) {
                    //福利
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_TYPE_RECV_RED_PACKET_FL :
                            MESSAGE_TYPE_SEND_RED_PACKET_FL;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RETURNGO)) {
                    //福利
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_TYPE_RECV_RED_PACKET_RTH :
                            MESSAGE_TYPE_SEND_RED_PACKET_RTH;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.PROMOTION)) {
                    //晋级代理
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_TYPE_RECV_RED_PACKET_PROMOTION :
                            MESSAGE_TYPE_SEND_RED_PACKET_PROMOTION;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RENAME)) {
                    //修改房间名称
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_TYPE_RECV_RED_PACKET_RENAME :
                            MESSAGE_TYPE_SEND_RED_PACKET_RENAME;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.WITHDRAW)) {
                    //提现审核通知
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_TYPE_RECV_WITHDRAW :
                            MESSAGE_TYPE_SEND_WITHDRAW;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.NOTICE)) {
                    //通知
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_NOTICE_RECV : MESSAGE_NOTICE_SEND;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.ADD_USER)) {
                    //好友申请，对方审核状态通知
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_APPLY_FRIEND_SRECV :
                            MESSAGE_APPLY_FRIEND_SEND;
                } else if (message.getBooleanAttribute(Constant.SEND_CARD,
                        false)) {
                    //名片
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            MESSAGE_CARD_RECV : MESSAGE_CARD_SEND;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.WITHDRAW)) {
                    //系统公告
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            SYSTEM_NOTICE : SYSTEM_NOTICE;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.TURN)) {
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            46 : 45;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.SURE_TURN)) {
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            48 : 47;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals("addgroupuser")
                        || message.getStringAttribute(Constant.MSGTYPE, "").equals("delgroupuser")
                ) {
                    return message.direct() == EMMessage.Direct.RECEIVE ?
                            49 : 49;
                }

            } else if (message.getType() == EMMessage.Type.LOCATION) {
                //地图
                return message.direct() == EMMessage.Direct.RECEIVE ?
                        MESSAGE_LOCATION_SRECV : MESSAGE_LOCATION_SEND;
            }
            return 0;
        }

        @Override
        public EaseChatRowPresenter getCustomChatRow(EMMessage message,
                                                     int position,
                                                     BaseAdapter adapter) {

            if (message.getType() == EMMessage.Type.TXT) {
                // voice call or video call
                if (message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VOICE_CALL, false) ||
                        message.getBooleanAttribute(Constant.MESSAGE_ATTR_IS_VIDEO_CALL, false)) {
                    EaseChatRowPresenter presenter =
                            new EaseChatVoiceCallPresenter();
                    return presenter;
                } else if (message.getBooleanAttribute(Constant.MESSAGE_TYPE_RECALL, false)) {
                    EaseChatRowPresenter presenter =
                            new EaseChatRecallPresenter();
                    return presenter;
                    //红包消息
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.REDPACKET)
                        || message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.PERSON_RED_BAG)) {
                    EaseChatRowPresenter presenter =
                            new ChatRedPacketPresenter();
                    return presenter;
                    //红包回执消息
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RAB)
                        || Constant.RAB_SELF.equals(message.getStringAttribute(Constant.MSGTYPE, ""))
                       /* || Constant.TURN.equals(message.getStringAttribute(Constant.MSGTYPE, ""))
                        || Constant.SURE_TURN.equals(message.getStringAttribute(Constant.MSGTYPE, ""))*/) {

                    EaseChatRowPresenter presenter =
                            new ChatRedPacketAckPresenter();
                    return presenter;
                    //系统公告
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.SYSTEM_NOTICE)) {
                    EaseChatRowPresenter presenter =
                            new SystemNoticePresenter();

                    mInputMenu.setVisibility(View.GONE);
                    titleBar.setRightLayoutVisibility(View.GONE);
                    return presenter;
                    //红包抢完
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.SYSTEM_WALLET)) {
                    WalletPresenter presenter =
                            new WalletPresenter();
                    mInputMenu.setVisibility(View.GONE);
                    titleBar.setRightLayoutVisibility(View.GONE);
                    return presenter;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.SYSTEM_GROUP_ADD)) {
                    GroupNoticePresenter presenter =
                            new GroupNoticePresenter();
                    return presenter;
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RABEND)) {
                    EaseChatRowPresenter presenter =
                            new ChatRedPacketFinalPresenter();
                    return presenter;
                    //金币退回
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RETURNGOLD)) {
                    EaseChatRowPresenter presenter =
                            new ChatRedPacketReturnPresenter();
                    return presenter;
                    //转账
                } else if (message.getBooleanAttribute(Constant.TURN, false)) {
                    EaseChatRowPresenter presenter =
                            new ChatRedPacketturnPresenter();
                    return presenter;
                    //确认转账
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.SURE_TURN) || message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.TURN)) {//
                    EaseChatRowPresenter presenter =
                            new ChatSureTurnPresenter();
                    return presenter;
                    //加入房间
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.ADDUSER)) {
                    EaseChatRowPresenter presenter =
                            new ChatRedPacketAddPresenter();
                    return presenter;
                    //惩罚
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.PUNISHMENT)) {
                    ChatRedPacketPunishmentPresenter presenter =
                            new ChatRedPacketPunishmentPresenter();
                    presenter.setRoomInfo(roomInfo);
                    return presenter;
                    //福利包
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.WELFARE)) {
                    EaseChatRowPresenter presenter =
                            new ChatRedPacketWelfarePresenter();
                    return presenter;
                    //红包退回
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RETURNGO)) {
                    EaseChatRowPresenter presenter =
                            new ChatRedPacketGoReturnPresenter();
                    return presenter;
                    //离开房间
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.DELUSER)) {
                    EaseChatRowPresenter presenter =
                            new ChatRedPacketLeftPresenter();
                    return presenter;
                    //踢人
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.SHOTUSER)) {
                    EaseChatRowPresenter presenter =
                            new ChatRedPacketTPresenter();
                    return presenter;
                    //创建房间
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.ADDROOM)) {
                    EaseChatRowPresenter presenter =
                            new ChatRedPacketAddroomPresenter();
                    return presenter;
                    //多人加入房间
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.ADDUSERS)) {
                    EaseChatRowPresenter presenter =
                            new ChatRedPacketAddAllPresenter();
                    return presenter;
                    //充值
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RECHARGE)) {
                    EaseChatRowPresenter presenter =
                            new ChatRechargePresenter();
                    return presenter;
                    //晋级代理
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.PROMOTION)) {
                    EaseChatRowPresenter presenter =
                            new ChatPromotionPresenter();
                    return presenter;
                    //修改房间名称
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.RENAME)) {
                    EaseChatRowPresenter presenter =
                            new ChatRedPacketUpRoomNamePresenter();
                    return presenter;
                    //提现审核通知
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.WITHDRAW)) {
                    EaseChatRowPresenter presenter =
                            new ChatWithdrawPresenter();
                    return presenter;
                    //通知
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.WITHDRAW)) {
                    EaseChatRowPresenter presenter =
                            new ChatWithdrawPresenter();
                    return presenter;
                    //好友申请，对方审核状态通知
                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals(Constant.ADD_USER)) {
                    EaseChatRowPresenter presenter =
                            new ChatApplyFreindNoticePresenter();
                    return presenter;
                } else if (message.getBooleanAttribute(Constant.SEND_CARD,
                        false)) {
                    //名片
                    EaseChatRowPresenter presenter = new ChatidCardPresenter();
                    return presenter;
                } else if (message.getBooleanAttribute(Constant.SEND_LOCATION
                        , false)) {
                    //自定义地图
                    EaseChatRowPresenter presenter =
                            new EaseChatLocationPresenter();
                    return presenter;

                } else if (message.getStringAttribute(Constant.MSGTYPE, "").equals("addgroupuser")
                        || message.getStringAttribute(Constant.MSGTYPE, "").equals("delgroupuser")) {

                    EaseChatRowPresenter presenter =
                            new UserOperatorGroupPresenter();
                    return presenter;
                }
            } else if (message.getType() == EMMessage.Type.LOCATION) {
                //自定义地图
                EaseChatRowPresenter presenter = new EaseChatLocationPresenter();
                return presenter;
            }
            return null;
        }

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void takePhotoBiggerThan7(String absolutePath) {
        Uri mCameraTempUri;
        try {
            ContentValues values = new ContentValues(1);
            values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpg");
            values.put(MediaStore.Images.Media.DATA, absolutePath);
            mCameraTempUri = requireContext().getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION
                    | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
            if (mCameraTempUri != null) {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, mCameraTempUri);
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            }
            startActivityForResult(intent, 1024);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}