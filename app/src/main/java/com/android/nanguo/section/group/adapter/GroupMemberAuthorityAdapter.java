package com.android.nanguo.section.group.adapter;

import com.android.nanguo.R;
import com.android.nanguo.section.contact.adapter.ContactListAdapter;

public class GroupMemberAuthorityAdapter extends ContactListAdapter {

    @Override
    public int getEmptyLayoutId() {
        return R.layout.ease_layout_default_no_data;
    }
}
