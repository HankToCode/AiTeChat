package com.android.nanguo.app.weight.ease;

import static com.android.nanguo.section.chat.fragment.ChatFragment.ITEM_ZHANGKAI;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.hyphenate.util.DensityUtil;
import com.android.nanguo.R;
import com.zds.base.Toast.ToastUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * Extend menu when user want send image, voice clip, etc
 */
public class EaseChatExtendSmallMenu extends GridView {

    protected Context context;
    private final List<ChatMenuItemSmallModel> itemModels = new ArrayList<ChatMenuItemSmallModel>();

    public boolean iscansend = true;

    public void setIscansend(boolean iscansend) {
        this.iscansend = iscansend;
    }

    public EaseChatExtendSmallMenu(Context context, AttributeSet attrs, int defStyle) {
        this(context, attrs);
    }

    public EaseChatExtendSmallMenu(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public EaseChatExtendSmallMenu(Context context) {
        super(context);
        init(context, null);
    }

    private void init(Context context, AttributeSet attrs) {
        this.context = context;
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.EaseChatExtendMenu);
        int numColumns = ta.getInt(R.styleable.EaseChatExtendMenu_numColumns, 6);
        ta.recycle();

        setNumColumns(numColumns);
        setStretchMode(GridView.STRETCH_COLUMN_WIDTH);
        setGravity(Gravity.CENTER_VERTICAL);
        setVerticalSpacing(DensityUtil.dip2px(context, 8));
    }

    /**
     * init
     */
    public void init() {
        setAdapter(new ItemAdapter(context, itemModels));
    }

    /**
     * register menu item
     *
     * @param drawableRes background of item
     * @param itemId      id
     * @param listener    on click event of item
     */
    public void registerMenuItem(int drawableRes, int itemId, EaseChatExtendMenu.EaseChatExtendMenuItemClickListener listener) {
        ChatMenuItemSmallModel item = new ChatMenuItemSmallModel();
        item.image = drawableRes;
        item.id = itemId;
        item.clickListener = listener;
        itemModels.add(item);
    }


    private class ItemAdapter extends ArrayAdapter<ChatMenuItemSmallModel> {

        private Context context;

        public ItemAdapter(Context context, List<ChatMenuItemSmallModel> objects) {
            super(context, R.layout.activity_edit, objects);
            this.context = context;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ChatMenuSmallItem menuItem;
            if (convertView == null) {
                convertView = new ChatMenuSmallItem(context);
            }
            menuItem = (ChatMenuSmallItem) convertView;
            if (getItem(position).number > 0) {
                menuItem.setTvImage(getItem(position).number);
            } else {
                menuItem.setImage(getItem(position).image);
            }
            menuItem.setOnClickListener(v -> {
                if (!iscansend) {
                    ToastUtil.toast("已禁言");
                    return;
                }

                if (getItem(position).id == ITEM_ZHANGKAI) {
                    if (getItem(position).image == R.mipmap.zhankai) {
                        getItem(position).image = R.mipmap.zhankai_select;
                    } else {
                        getItem(position).image = R.mipmap.zhankai;
                    }
                    menuItem.setImage(getItem(position).image);
                }

                if (getItem(position).clickListener != null) {
                    getItem(position).clickListener.onClick(getItem(position).id, v);
                }
            });
            return convertView;
        }


    }


    class ChatMenuItemSmallModel {
        int image;
        int id;
        int number;
        EaseChatExtendMenu.EaseChatExtendMenuItemClickListener clickListener;
    }

    class ChatMenuSmallItem extends LinearLayout {
        private ImageView imageView;

        public ChatMenuSmallItem(Context context, AttributeSet attrs, int defStyle) {
            this(context, attrs);
        }

        public ChatMenuSmallItem(Context context, AttributeSet attrs) {
            super(context, attrs);
            init(context, attrs);
        }

        public ChatMenuSmallItem(Context context) {
            super(context);
            init(context, null);
        }

        private void init(Context context, AttributeSet attrs) {
            LayoutInflater.from(context).inflate(R.layout.ease_chat_menu_small_item, this);
            imageView = (ImageView) findViewById(R.id.image);
        }

        public void setImage(int resid) {
            imageView.setVisibility(VISIBLE);
            imageView.setBackgroundResource(resid);
        }

        public void setTvImage(int number) {
            imageView.setVisibility(GONE);
        }
    }
}
