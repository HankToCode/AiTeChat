/**
 * 作   者：赵大帅
 */
package com.ycf.qianzhihe.section.contact.fragment;

import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.hyphenate.chat.EMClient;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.domain.EaseUser;
import com.ycf.qianzhihe.section.chat.activity.ChatActivity;
import com.ycf.qianzhihe.section.common.EaseContactListFragment;
import com.zds.base.Toast.ToastUtil;

import com.hyphenate.easeui.constants.EaseConstant;
import com.hyphenate.exceptions.HyphenateException;

import java.util.HashMap;
import java.util.Map;

/**
 * contact list
 */
public class ContactListFragment extends EaseContactListFragment {

    private static final String TAG = ContactListFragment.class.getSimpleName();
    private View loadingView;

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
