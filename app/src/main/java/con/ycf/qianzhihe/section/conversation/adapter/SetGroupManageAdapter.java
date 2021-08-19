package con.ycf.qianzhihe.section.conversation.adapter;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import con.ycf.qianzhihe.R;
import con.ycf.qianzhihe.app.api.old_data.GroupManageListInfo;

import java.util.List;

public class SetGroupManageAdapter extends BaseQuickAdapter<GroupManageListInfo, BaseViewHolder> {
    private OnCancelGroupUserListener mCancelGroupUserListener;

    public void setOnDelGroupUserListener(OnCancelGroupUserListener onCancelGroupUserListener) {
        mCancelGroupUserListener = onCancelGroupUserListener;
    }

    public SetGroupManageAdapter(@Nullable List<GroupManageListInfo> data) {
        super(R.layout.adapter_set_group_manage, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, GroupManageListInfo item) {
        helper.setText(R.id.tv_group_user, item.getUserNickName());
        TextView mDeleteTv = helper.getView(R.id.tv_del);

        if (!mDeleteTv.hasOnClickListeners()) {
            mDeleteTv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (null != mCancelGroupUserListener) {
                        mCancelGroupUserListener.delGroup(item.getUserId(), helper.getAdapterPosition());
                    }

                }
            });
        }
    }


    /**
     * 取消群管理员接口回调
     */
    public interface OnCancelGroupUserListener {
        void delGroup(String userId, int pos);
    }
}
