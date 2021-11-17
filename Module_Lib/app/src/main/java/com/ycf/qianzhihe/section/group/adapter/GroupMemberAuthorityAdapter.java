package com.ycf.qianzhihe.section.group.adapter;

import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.section.contact.adapter.ContactListAdapter;

public class GroupMemberAuthorityAdapter extends ContactListAdapter {

    @Override
    public int getEmptyLayoutId() {
        return R.layout.ease_layout_default_no_data;
    }
}
