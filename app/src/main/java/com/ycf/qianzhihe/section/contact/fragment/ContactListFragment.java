/**
 * 作   者：赵大帅
 */
package com.ycf.qianzhihe.section.contact.fragment;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.hyphenate.chat.EMClient;
import com.hyphenate.easeui.model.EaseEvent;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseActivity;
import com.ycf.qianzhihe.app.domain.EaseUser;
import com.ycf.qianzhihe.app.weight.ConversationItemView;
import com.ycf.qianzhihe.common.constant.DemoConstant;
import com.ycf.qianzhihe.section.MainViewModel;
import com.ycf.qianzhihe.section.chat.activity.ChatActivity;
import com.ycf.qianzhihe.section.common.EaseContactListFragment;
import com.ycf.qianzhihe.section.conversation.AuditMsgActivity;
import com.zds.base.Toast.ToastUtil;

import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;
import com.zds.base.util.NumberUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * contact list
 */
public class ContactListFragment extends EaseContactListFragment {

    private static final String TAG = ContactListFragment.class.getSimpleName();
    private View loadingView;

    private ConversationItemView mFriendNotice;

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);

        //add loading view
        loadingView = LayoutInflater.from(getActivity()).inflate(R.layout.em_layout_loading_data, null);
        contentContainer.addView(loadingView);
        registerForContextMenu(listView);
        //设置联系人数据
//        Map<String, EaseUser> m = MyHelper.getInstance().getContactList();
////        if (m instanceof Hashtable<?, ?>) {
////            m = (Map<String, EaseUser>) ((Hashtable<String, EaseUser>) m).clone();
////        }

        listView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                EaseUser user = (EaseUser) listView.getItemAtPosition(position);
                if (user != null) {
                    ChatActivity.actionStart(mContext, user.getUsername(), EaseConstant.CHATTYPE_SINGLE);
                }
            }
        });

        mFriendNotice = new ConversationItemView(requireContext());
        mFriendNotice.setUnreadCount(0);
        mFriendNotice.setName("新朋友");
        mFriendNotice.setAvatar(ContextCompat.getDrawable(requireContext(), R.mipmap.ic_new_friends));
        mFriendNotice.setVisibility(View.VISIBLE);
        contactListLayout.getListView().addHeaderView(mFriendNotice);

        mFriendNotice.setOnClickListener(v -> {
            mFriendNotice.setUnreadCount(0);
            AuditMsgActivity.actionStart(requireContext());
        });
    }

    private MainViewModel viewModel;

    @Override
    protected void initViewModel() {
        super.initViewModel();
        viewModel = new ViewModelProvider(mContext).get(MainViewModel.class);

        viewModel.homeUnReadContactObservable().observe(this, readCount -> {
            if (!TextUtils.isEmpty(readCount)) {
                mFriendNotice.setUnreadCount(NumberUtils.parseInt(readCount));
            } else {
                mFriendNotice.setUnreadCount(0);
            }
        });

        viewModel.messageChangeObservable().with(DemoConstant.GROUP_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        viewModel.messageChangeObservable().with(DemoConstant.NOTIFY_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        viewModel.messageChangeObservable().with(DemoConstant.MESSAGE_CHANGE_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);

        viewModel.messageChangeObservable().with(DemoConstant.CONVERSATION_DELETE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        viewModel.messageChangeObservable().with(DemoConstant.CONTACT_CHANGE, EaseEvent.class).observe(this, this::checkUnReadMsg);
        viewModel.messageChangeObservable().with(DemoConstant.CONVERSATION_READ, EaseEvent.class).observe(this, this::checkUnReadMsg);
    }

    private void checkUnReadMsg(EaseEvent event) {
        if (event == null) {
            return;
        }
        viewModel.checkUnreadMsg((BaseActivity) requireActivity());
    }

    @Override
    public void refresh() {
        super.refresh();
    }

    @Override
    protected void onEventComing(EventCenter center) {
        super.onEventComing(center);
        if (center.getEventCode() == EventUtil.REFRESH_REMARK) {
            refresh();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        toBeProcessUser = (EaseUser) listView.getItemAtPosition(((AdapterView.AdapterContextMenuInfo) menuInfo).position);
        if (toBeProcessUser == null) {
            return;
        }
        toBeProcessUsername = toBeProcessUser.getUsername();
        getActivity().getMenuInflater().inflate(R.menu.em_context_contact_list, menu);
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.delete_contact) {
            try {
                // delete contact
                deleteContact(toBeProcessUser);
            } catch (Exception e) {
                e.printStackTrace();
            }
            return true;
        }
        return super.onContextItemSelected(item);
    }


    /**
     * delete contact
     *
     * @param tobeDeleteUser
     */
    public void deleteContact(final EaseUser tobeDeleteUser) {
        Map<String, Object> map = new HashMap<>();
        map.put("friendUserId", tobeDeleteUser.getUsername().split("-")[0]);
        ApiClient.requestNetHandle(getActivity(), AppConfig.DEL_USER_FRIEND, "正在删除...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {

                try {
                    EMClient.getInstance().contactManager().deleteContact(tobeDeleteUser.getUsername());
                } catch (HyphenateException e) {
                    e.printStackTrace();
                }
                contactList.remove(tobeDeleteUser);
                contactListLayout.refresh();
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }
}
