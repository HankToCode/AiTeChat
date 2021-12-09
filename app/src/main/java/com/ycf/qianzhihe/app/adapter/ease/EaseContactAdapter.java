package com.ycf.qianzhihe.app.adapter.ease;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.ImageView;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.util.EMLog;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.domain.EaseUser;
import com.ycf.qianzhihe.app.operate.UserOperateManager;
import com.ycf.qianzhihe.app.utils.ImageUtil;
import com.zds.base.ImageLoad.GlideUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author lhb
 */
public class EaseContactAdapter extends ArrayAdapter<EaseUser> implements SectionIndexer {
    private static final String TAG = "ContactAdapter";
    List<String> list;
    List<EaseUser> userList;
    List<EaseUser> copyUserList;
    private LayoutInflater layoutInflater;
    private SparseIntArray positionOfSection;
    private SparseIntArray sectionOfPosition;
    private int res;
    private MyFilter myFilter;
    private boolean notiyfyByFilter;

    public EaseContactAdapter(Context context, int resource, List<EaseUser> objects) {
        super(context, resource, objects);
        this.res = resource;
        this.userList = objects;
        copyUserList = new ArrayList<EaseUser>();
        copyUserList.addAll(objects);
        layoutInflater = LayoutInflater.from(context);
    }

    private static class ViewHolder {
        ImageView avatar;
        TextView nameView;
        TextView tv_sign;
        TextView headerView;
        ImageView onlineStatus;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            if (res == 0) {
                convertView = layoutInflater.inflate(R.layout.ease_row_contact, parent, false);
            } else {
                convertView = layoutInflater.inflate(res, null);
            }
            holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
            holder.nameView = (TextView) convertView.findViewById(R.id.name);
            holder.tv_sign = (TextView) convertView.findViewById(R.id.tv_sign);
            holder.headerView = (TextView) convertView.findViewById(R.id.header);
            holder.onlineStatus = (ImageView) convertView.findViewById(R.id.iv_online_status);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        EaseUser user = getItem(position);
        if (user == null) {
            Log.d("ContactAdapter", position + "");
        }
        String username = user.getUsername();
        String header = user.getInitialLetter();
        String isOnlineStr = "离线";
        //是否在线
        if (!TextUtils.isEmpty(user.getLine())) {
            if (user.getLine().equals("online")) {
                isOnlineStr = "在线";
                holder.onlineStatus.setBackgroundResource(R.drawable.dot_green);
            } else {
                holder.onlineStatus.setBackgroundResource(R.drawable.dot_gray);
            }
        } else {
            holder.onlineStatus.setBackgroundResource(R.drawable.dot_gray);
        }

        if (position == 0 || header != null && !header.equals(getItem(position - 1).getInitialLetter())) {
            if (TextUtils.isEmpty(header)) {
                holder.headerView.setVisibility(View.GONE);
            } else {
                holder.headerView.setVisibility(View.VISIBLE);
                holder.headerView.setText(header);
            }
        } else {
            holder.headerView.setVisibility(View.GONE);
        }

        holder.nameView.setText(UserOperateManager.getInstance().getUserName(username));
        if (!TextUtils.isEmpty(user.getUserSign())) {
            holder.tv_sign.setText("[" + isOnlineStr + "] " + user.getUserSign());
        } else {
            holder.tv_sign.setText("[" + isOnlineStr + "] " + "这家伙很懒,什么都没有留下,心里却有一个你");
        }
        //是否为会员vipLevel
        if (!TextUtils.isEmpty(user.getVipLevel())) {
            holder.nameView.setTextColor(Color.parseColor("#ffff0000"));
        } else {
            holder.nameView.setTextColor(Color.parseColor("#000000"));
        }

        ImageUtil.setAvatar((EaseImageView) holder.avatar);
        GlideUtils.loadImageViewLoding(UserOperateManager.getInstance().getUserAvatar(username), holder.avatar, R.mipmap.ic_ng_avatar);


        if (primaryColor != 0) {
            holder.nameView.setTextColor(primaryColor);
        }
        if (primarySize != 0) {
            holder.nameView.setTextSize(TypedValue.COMPLEX_UNIT_PX, primarySize);
        }
        if (initialLetterBg != null) {
            holder.headerView.setBackgroundDrawable(initialLetterBg);
        }
        if (initialLetterColor != 0) {
            holder.headerView.setTextColor(initialLetterColor);
        }

        return convertView;
    }

    @Override
    public EaseUser getItem(int position) {
        return super.getItem(position);
    }

    @Override
    public int getCount() {
        return super.getCount();
    }

    @Override
    public int getPositionForSection(int section) {
        return positionOfSection.get(section);
    }

    @Override
    public int getSectionForPosition(int position) {
        return sectionOfPosition.get(position);
    }

    @Override
    public Object[] getSections() {
        positionOfSection = new SparseIntArray();
        sectionOfPosition = new SparseIntArray();
        int count = getCount();
        list = new ArrayList<String>();
        list.add(getContext().getString(R.string.search_header));
        positionOfSection.put(0, 0);
        sectionOfPosition.put(0, 0);
        for (int i = 1; i < count; i++) {

            String letter = getItem(i).getInitialLetter();
            int section = list.size() - 1;
            if (list.get(section) != null && !list.get(section).equals(letter)) {
                list.add(letter);
                section++;
                positionOfSection.put(section, i);
            }
            sectionOfPosition.put(i, section);
        }
        return list.toArray(new String[list.size()]);
    }

    @Override
    public Filter getFilter() {
        if (myFilter == null) {
            myFilter = new MyFilter(userList);
        }
        return myFilter;
    }

    protected class MyFilter extends Filter {
        List<EaseUser> mOriginalList = null;

        public MyFilter(List<EaseUser> myList) {
            this.mOriginalList = myList;
        }

        @Override
        protected synchronized FilterResults performFiltering(CharSequence prefix) {
            FilterResults results = new FilterResults();
            if (mOriginalList == null) {
                mOriginalList = new ArrayList<EaseUser>();
            }
            EMLog.d(TAG, "contacts original size: " + mOriginalList.size());
            EMLog.d(TAG, "contacts copy size: " + copyUserList.size());

            if (prefix == null || prefix.length() == 0) {
                results.values = copyUserList;
                results.count = copyUserList.size();
            } else {

                if (copyUserList.size() > mOriginalList.size()) {
                    mOriginalList = copyUserList;
                }
                String prefixString = prefix.toString();
                final int count = mOriginalList.size();
                final ArrayList<EaseUser> newValues = new ArrayList<EaseUser>();
                for (int i = 0; i < count; i++) {
                    final EaseUser user = mOriginalList.get(i);
                    String username = user.getNickname();

                    if (username.contains(prefixString)) {
                        newValues.add(user);
                    } else {
                        final String[] words = username.split(" ");
                        final int wordCount = words.length;

                        // Start at index 0, in case valueText starts with space(s)
                        for (String word : words) {
                            if (word.contains(prefixString)) {
                                newValues.add(user);
                                break;
                            }
                        }
                    }
                }
                results.values = newValues;
                results.count = newValues.size();
            }
            EMLog.d(TAG, "contacts filter results size: " + results.count);
            return results;
        }

        @Override
        protected synchronized void publishResults(CharSequence constraint,
                                                   FilterResults results) {
            userList.clear();
            userList.addAll((List<EaseUser>) results.values);
            EMLog.d(TAG, "publish contacts filter results size: " + results.count);
            if (results.count > 0) {
                notiyfyByFilter = true;
                notifyDataSetChanged();
                notiyfyByFilter = false;
            } else {
                notifyDataSetInvalidated();
            }
        }
    }


    @Override
    public void notifyDataSetChanged() {
        super.notifyDataSetChanged();
        if (!notiyfyByFilter) {
            copyUserList.clear();
            copyUserList.addAll(userList);
        }
    }

    protected int primaryColor;
    protected int primarySize;
    protected Drawable initialLetterBg;
    protected int initialLetterColor;

    public EaseContactAdapter setPrimaryColor(int primaryColor) {
        this.primaryColor = primaryColor;
        return this;
    }


    public EaseContactAdapter setPrimarySize(int primarySize) {
        this.primarySize = primarySize;
        return this;
    }

    public EaseContactAdapter setInitialLetterBg(Drawable initialLetterBg) {
        this.initialLetterBg = initialLetterBg;
        return this;
    }

    public EaseContactAdapter setInitialLetterColor(int initialLetterColor) {
        this.initialLetterColor = initialLetterColor;
        return this;
    }

}
