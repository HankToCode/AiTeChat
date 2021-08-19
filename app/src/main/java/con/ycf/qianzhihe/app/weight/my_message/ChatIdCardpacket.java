package con.ycf.qianzhihe.app.weight.my_message;

import android.content.Context;
import android.text.TextUtils;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.hyphenate.chat.EMMessage;
import com.hyphenate.easecallkit.widget.EaseImageView;
import con.ycf.qianzhihe.R;
import con.ycf.qianzhihe.app.api.Constant;
import con.ycf.qianzhihe.app.utils.ImageUtil;
import con.zds.base.ImageLoad.GlideUtils;
import con.ycf.qianzhihe.app.weight.ease.chatrow.EaseChatRow;

/**
 * @author lhb
 * 名片
 */
public class ChatIdCardpacket extends EaseChatRow {
    private TextView tv_message, tv_youxin_account;
    private EaseImageView img_card_head;

    public ChatIdCardpacket(Context context, EMMessage message, int position, BaseAdapter adapter) {
        super(context, message, position, adapter);
    }


    @Override
    protected void onInflateView() {
        if (message.getBooleanAttribute(Constant.SEND_CARD, false)) {
            inflater.inflate(message.direct() == EMMessage.Direct.RECEIVE ?
                    R.layout.row_received_id_card : R.layout.row_send_id_card, this);
        }
    }

    @Override
    protected void onFindViewById() {
        tv_message = (TextView) findViewById(R.id.message);
        tv_youxin_account = (TextView) findViewById(R.id.tv_youxin_account);
        img_card_head = (EaseImageView) findViewById(R.id.img_card_head);

    }

    @Override
    protected void onViewUpdate(EMMessage msg) {

    }

    @Override
    protected void onSetUpView() {
        String account = message.getStringAttribute("otherUserCode", "");
        String nickname = message.getStringAttribute("otherName", "");
        String avatar = message.getStringAttribute("otherImg", "");
        tv_message.setText(nickname);

        if (!TextUtils.isEmpty(account)) {
            tv_youxin_account.setText("艾特号：" + account);
        } else {
            tv_youxin_account.setText("艾特号：暂无");
        }

        ImageUtil.setAvatar(img_card_head);
        GlideUtils.loadImageViewLoding(avatar, img_card_head, R.mipmap.img_default_avatar);
    }
}