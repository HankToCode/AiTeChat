package com.ycf.qianzhihe.section.contact.fragment;

import static com.darsh.multipleimageselect.helpers.Constants.REQUEST_CODE;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.viewpager.widget.ViewPager;

import com.blankj.utilcode.util.ScreenUtils;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.Global;
import com.ycf.qianzhihe.app.base.BaseInitFragment;
import com.ycf.qianzhihe.app.operate.UserOperateManager;
import com.ycf.qianzhihe.app.weight.PopWinShare;
import com.ycf.qianzhihe.common.utils.AdapterHelper;

import com.hyphenate.easeui.ui.base.EaseBaseFragment;
import com.scwang.smartrefresh.layout.util.DensityUtil;
import com.ycf.qianzhihe.section.account.activity.UserInfoDetailActivity;
import com.ycf.qianzhihe.section.common.ContactActivity;
import com.ycf.qianzhihe.section.common.ContactSearchActivity;
import com.ycf.qianzhihe.section.common.MyQrActivity;
import com.ycf.qianzhihe.section.contact.activity.AddUserActivity;
import com.zds.base.code.activity.CaptureActivity;

import net.lucode.hackware.magicindicator.MagicIndicator;
import net.lucode.hackware.magicindicator.ViewPagerHelper;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.CommonNavigator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.CommonNavigatorAdapter;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.abs.IPagerTitleView;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.indicators.WrapPagerIndicator;
import net.lucode.hackware.magicindicator.buildins.commonnavigator.titles.ColorTransitionPagerTitleView;

import java.util.Arrays;
import java.util.List;

public class ContactHomeFragment extends BaseInitFragment implements View.OnClickListener {
    private MagicIndicator indicator;
    private ViewPager viewPager;
    private TextView tvTitle;
    private TextView tvSearch;
    private ImageView ivOptions;

    private List<String> titles = Arrays.asList("我的好友", "我的群组", "我的分组");
    private List<EaseBaseFragment> fragments = Arrays.asList(new ContactListFragment(), new GroupContactManageFragment(), new GroupingListFragment());

    @Override
    protected int getLayoutId() {
        return R.layout.demo_fragment_contact_home;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        indicator = findViewById(R.id.indicator);
        viewPager = findViewById(R.id.view_pager);
        tvTitle = findViewById(R.id.tvTitle);
        tvSearch = findViewById(R.id.tvSearch);
        ivOptions = findViewById(R.id.ivOptions);

        tvTitle.setText("联系人");
        initIndicator(titles);
    }

    @Override
    protected void initListener() {
        super.initListener();

        ivOptions.setOnClickListener(this);
        tvSearch.setOnClickListener(this);
    }

    private void initIndicator(List<String> titles) {

        CommonNavigator navigator = new CommonNavigator(requireContext());
        navigator.setAdjustMode(true);
        navigator.setAdapter(new CommonNavigatorAdapter() {
            @Override
            public int getCount() {
                return titles.size();
            }

            @Override
            public IPagerTitleView getTitleView(Context context, int index) {

                ColorTransitionPagerTitleView colorTransitionPagerTitleView = new ColorTransitionPagerTitleView(context);
                colorTransitionPagerTitleView.setNormalColor(Color.parseColor("#999999"));
                colorTransitionPagerTitleView.setSelectedColor(Color.BLACK);
                colorTransitionPagerTitleView.setText(titles.get(index));
                colorTransitionPagerTitleView.setTextSize(15);
                colorTransitionPagerTitleView.setOnClickListener(view -> viewPager.setCurrentItem(index));

                return colorTransitionPagerTitleView;
            }

            @Override
            public IPagerIndicator getIndicator(Context context) {
                //设置头部标签指示器
                WrapPagerIndicator indicator = new WrapPagerIndicator(context);
                indicator.setFillColor(Color.WHITE);
                indicator.setHorizontalPadding(DensityUtil.dp2px(10));
                indicator.setVerticalPadding(DensityUtil.dp2px(5));
                indicator.setRoundRadius(DensityUtil.dp2px(25));
                return indicator;
            }
        });

        indicator.setNavigator(navigator);
        ViewPagerHelper.bind(indicator, viewPager);
        AdapterHelper.bind(viewPager, getChildFragmentManager(), fragments);

    }

    private PopWinShare popWinShare;

    /**
     * 显示浮窗菜单
     */
    private void showPopWinShare(View view) {
        if (popWinShare == null) {
            View.OnClickListener paramOnClickListener =
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            //扫一扫
                            if (v.getId() == R.id.layout_saoyisao) {
                                Global.addUserOriginType = Constant.ADD_USER_ORIGIN_TYPE_QRCODE;
                                Intent intent = new Intent(mContext,
                                        CaptureActivity.class);
                                startActivityForResult(intent, REQUEST_CODE);
                            }//添加好友  //搜索添加好友
                            else if (v.getId() == R.id.layout_add_firend) {
//                                AddContactActivity.actionStart(mContext, SearchType.CHAT);
                                AddUserActivity.actionStart(mContext);
                            } else if (v.getId() == R.id.layout_group) {
                                ContactActivity.actionStart(mContext, "1", null, null);
                            } else if (v.getId() == R.id.layout_my_qr) {
                                Intent intent = new Intent(mContext, MyQrActivity.class);
                                intent.putExtra("from", "1");
                                startActivity(intent);
                            }

                            popWinShare.dismiss();
                        }


                    };

            popWinShare = new PopWinShare(mContext, paramOnClickListener
                    , ivOptions.getTop() + ivOptions.getHeight() + 10,
                    (int) (ScreenUtils.getScreenWidth() - ivOptions.getX() - ivOptions.getWidth()));
            //监听窗口的焦点事件，点击窗口外面则取消显示
            popWinShare.getContentView().setOnFocusChangeListener((v, hasFocus) -> {
                if (!hasFocus) {
                    popWinShare.dismiss();
                }
            });
        }
        //设置默认获取焦点
        popWinShare.setFocusable(true);
        //以某个控件的x和y的偏移量位置开始显示窗口
        popWinShare.showAsDropDown(view);
        //如果窗口存在，则更新
        popWinShare.update();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            //处理扫描结果（在界面上显示）
            if (null != data) {
                Bundle bundle = data.getExtras();
                if (bundle == null) {
                    return;
                }
                String result = bundle.getString(CaptureActivity.INTENT_EXTRA_KEY_QR_SCAN);

                if (result.contains("person")) {
                    startActivity(new Intent(mContext,
                            UserInfoDetailActivity.class).putExtra(
                            "friendUserId", result.split("_")[0]));
                } else if (result.contains("group")) {
                    UserOperateManager.getInstance().scanInviteContact(mContext, result);
                }
            }
        }
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tvSearch) {
            ContactSearchActivity.actionStart(mContext);
        } else if (v.getId() == R.id.ivOptions) {
            showPopWinShare(ivOptions);
        }
    }
}
