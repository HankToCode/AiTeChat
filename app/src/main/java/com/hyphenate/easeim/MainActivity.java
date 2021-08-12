package com.hyphenate.easeim;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easecallkit.ui.EaseMultipleVideoActivity;
import com.hyphenate.easecallkit.ui.EaseVideoCallActivity;
import com.hyphenate.easeim.common.constant.DemoConstant;
import com.hyphenate.easeim.common.enums.SearchType;
import com.hyphenate.easeim.common.permission.PermissionsManager;
import com.hyphenate.easeim.common.permission.PermissionsResultAction;
import com.hyphenate.easeim.common.utils.PushUtils;
import com.hyphenate.easeim.section.MainViewModel;
import com.hyphenate.easeim.app.base.BaseInitActivity;
import com.hyphenate.easeim.section.chat.ChatPresenter;
import com.hyphenate.easeim.section.contact.activity.AddContactActivity;
import com.hyphenate.easeim.section.contact.fragment.ContactHomeFragment;
import com.hyphenate.easeim.section.contact.viewmodels.ContactsViewModel;
import com.hyphenate.easeim.section.conversation.ConversationListFragment;
import com.hyphenate.easeim.section.discover.DiscoverFragment;
import com.hyphenate.easeim.section.me.AboutMeFragment;
import com.hyphenate.easeim.section.search.SearchConversationActivity;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.ui.base.EaseBaseFragment;
import com.hyphenate.easeui.widget.EaseTitleBar;

import java.lang.reflect.Method;


public class MainActivity extends BaseInitActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private BottomNavigationView navView;
    private EaseTitleBar mTitleBar;
    private EaseBaseFragment mContactsFragment, mMessageFragment, mDiscoverFragment, mFindFragment;
    private EaseBaseFragment mCurrentFragment;
    private TextView mTvMainContactsMsg, mTvMainMessageMsg, mTvMainDiscoverMsg, mTvMainFindMsg;
    private int[] badgeIds = {R.layout.demo_badge_home, R.layout.demo_badge_friends, R.layout.demo_badge_discover, R.layout.demo_badge_about_me};
    private int[] msgIds = {R.id.tv_main_home_msg, R.id.tv_main_friends_msg, R.id.tv_main_discover_msg, R.id.tv_main_about_me_msg};
    private MainViewModel viewModel;
    private boolean showMenu = true;//是否显示菜单项

    public static void startAction(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_main;
    }

    /*@Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mCurrentFragment != null) {
            if (mCurrentFragment instanceof ContactListFragment) {
                menu.findItem(R.id.action_group).setVisible(false);
                menu.findItem(R.id.action_friend).setVisible(false);
                menu.findItem(R.id.action_search_friend).setVisible(true);
                menu.findItem(R.id.action_search_group).setVisible(true);
            } else {
                menu.findItem(R.id.action_group).setVisible(true);
                menu.findItem(R.id.action_friend).setVisible(true);
                menu.findItem(R.id.action_search_friend).setVisible(false);
                menu.findItem(R.id.action_search_group).setVisible(false);
            }
        }
        return showMenu;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.demo_contacts_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_video:
                break;
            case R.id.action_group:
                GroupPrePickActivity.actionStart(mContext);
                break;
            case R.id.action_friend:
            case R.id.action_search_friend:
                AddContactActivity.startAction(mContext, SearchType.CHAT);
                break;
            case R.id.action_search_group:
                GroupContactManageActivity.actionStart(mContext, true);
                break;
            case R.id.action_scan:
                showToast("扫一扫");
                break;
        }
        return true;
    }*/

    /**
     * 显示menu的icon，通过反射，设置menu的icon显示
     *
     * @param featureId
     * @param menu
     * @return
     */
    @Override
    public boolean onMenuOpened(int featureId, Menu menu) {
        if (menu != null) {
            if (menu.getClass().getSimpleName().equalsIgnoreCase("MenuBuilder")) {
                try {
                    Method method = menu.getClass().getDeclaredMethod("setOptionalIconsVisible", Boolean.TYPE);
                    method.setAccessible(true);
                    method.invoke(menu, true);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
        return super.onMenuOpened(featureId, menu);
    }

    @Override
    protected void initSystemFit() {
        setFitSystemForTheme(false);
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        navView = findViewById(R.id.nav_view);
        mTitleBar = findViewById(R.id.title_bar_main);
        navView.setItemIconTintList(null);
        // 可以动态显示隐藏相应tab
        //navView.getMenu().findItem(R.id.em_main_nav_me).setVisible(false);
        switchToHome();
        checkIfShowSavedFragment(savedInstanceState);
        addTabBadge();
    }

    @Override
    protected void initListener() {
        super.initListener();
        navView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    protected void initData() {
        super.initData();
        initViewModel();
        requestPermissions();
        checkUnreadMsg();
        ChatPresenter.getInstance().init();
        // 获取华为 HMS 推送 token
        HMSPushHelper.getInstance().getHMSToken(this);

        //判断是否为来电推送
        if (PushUtils.isRtcCall) {
            if (EaseCallType.getfrom(PushUtils.type) != EaseCallType.CONFERENCE_CALL) {
                EaseVideoCallActivity callActivity = new EaseVideoCallActivity();
                Intent intent = new Intent(getApplicationContext(), callActivity.getClass()).addFlags(FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            } else {
                EaseMultipleVideoActivity callActivity = new EaseMultipleVideoActivity();
                Intent intent = new Intent(getApplication().getApplicationContext(), callActivity.getClass()).addFlags(FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
            PushUtils.isRtcCall = false;
        }
    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(mContext).get(MainViewModel.class);
        viewModel.getSwitchObservable().observe(this, response -> {
            if (response == null || response == 0) {
                return;
            }
            if (response == R.string.em_main_title_discover) {
                mTitleBar.setVisibility(View.GONE);
            } else {
                mTitleBar.setVisibility(View.VISIBLE);
                mTitleBar.setTitle(getResources().getString(response));
            }
        });

        viewModel.homeUnReadObservable().observe(this, readCount -> {
            if (!TextUtils.isEmpty(readCount)) {
                mTvMainContactsMsg.setVisibility(View.VISIBLE);
                mTvMainContactsMsg.setText(readCount);
            } else {
                mTvMainContactsMsg.setVisibility(View.GONE);
            }
        });

        //加载联系人
        ContactsViewModel contactsViewModel = new ViewModelProvider(mContext).get(ContactsViewModel.class);
        contactsViewModel.loadContactList();

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
        viewModel.checkUnreadMsg();
    }

    /**
     * 添加BottomNavigationView中每个item右上角的红点
     */
    private void addTabBadge() {
        BottomNavigationMenuView menuView = (BottomNavigationMenuView) navView.getChildAt(0);
        int childCount = menuView.getChildCount();
        Log.e("TAG", "bottom child count = " + childCount);
        BottomNavigationItemView itemTab;
        for (int i = 0; i < childCount; i++) {
            itemTab = (BottomNavigationItemView) menuView.getChildAt(i);
            View badge = LayoutInflater.from(mContext).inflate(badgeIds[i], menuView, false);
            switch (i) {
                case 0:
                    mTvMainContactsMsg = badge.findViewById(msgIds[0]);
                    break;
                case 1:
                    mTvMainMessageMsg = badge.findViewById(msgIds[1]);
                    break;
                case 2:
                    mTvMainDiscoverMsg = badge.findViewById(msgIds[2]);
                    break;
                case 3:
                    mTvMainFindMsg = badge.findViewById(msgIds[3]);
                    break;
            }
            itemTab.addView(badge);
        }
    }

    /**
     * 用于展示是否已经存在的Fragment
     *
     * @param savedInstanceState
     */
    private void checkIfShowSavedFragment(Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String tag = savedInstanceState.getString("tag");
            if (!TextUtils.isEmpty(tag)) {
                Fragment fragment = getSupportFragmentManager().findFragmentByTag(tag);
                if (fragment instanceof EaseBaseFragment) {
                    replace((EaseBaseFragment) fragment, tag);
                }
            }
        }
    }

    /**
     * 申请权限
     */
    // TODO: 2019/12/19 0019 有必要修改一下
    private void requestPermissions() {
        PermissionsManager.getInstance()
                .requestAllManifestPermissionsIfNecessary(mContext, new PermissionsResultAction() {
                    @Override
                    public void onGranted() {

                    }

                    @Override
                    public void onDenied(String permission) {

                    }
                });
    }

    private void switchToHome() {
        if (mContactsFragment == null) {
            mContactsFragment = new ContactHomeFragment();
        }
        replace(mContactsFragment, "contacts");
    }

    private void switchToFriends() {
        if (mMessageFragment == null) {
            mMessageFragment = new ConversationListFragment();
        }
        replace(mMessageFragment, "message");
    }

    private void switchToDiscover() {
        if (mDiscoverFragment == null) {
            mDiscoverFragment = new DiscoverFragment();
        }
        replace(mDiscoverFragment, "discover");
    }

    private void switchToAboutMe() {
        if (mFindFragment == null) {
            mFindFragment = new AboutMeFragment();
        }
        replace(mFindFragment, "find");
    }

    private void replace(EaseBaseFragment fragment, String tag) {
        if (mCurrentFragment != fragment) {

            //替换Fragment
            FragmentTransaction t = getSupportFragmentManager().beginTransaction();
            if (mCurrentFragment != null) {
                t.hide(mCurrentFragment);
            }
            mCurrentFragment = fragment;
            if (!fragment.isAdded()) {
                t.add(R.id.fl_main_fragment, fragment, tag).show(fragment).commit();
            } else {
                t.show(fragment).commit();
            }

            //替换Bar上功能
            replaceBarButtonLayout(tag);
        }
    }

    /**
     * 替换bar上按钮布局功能
     *
     * @param tag
     */
    private void replaceBarButtonLayout(String tag) {
        mTitleBar.getRightLayout().removeAllViews();
        mTitleBar.getLeftLayout().removeAllViews();
        switch (tag) {
            case "contacts":
            case "message":
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
                RelativeLayout.LayoutParams layoutParams1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                layoutParams1.addRule(RelativeLayout.CENTER_IN_PARENT);

                View rightView = View.inflate(this, R.layout.layout_toolbar_contacts_right, null);
                rightView.findViewById(R.id.iv_add_friends).setOnClickListener(this);
                rightView.findViewById(R.id.iv_search_friends).setOnClickListener(this);
                mTitleBar.getRightLayout().addView(rightView, layoutParams);

                View leftView = View.inflate(this, R.layout.layout_toolbar_contacts_left, null);
                leftView.findViewById(R.id.iv_avatar).setOnClickListener(this);
                mTitleBar.getLeftLayout().addView(leftView, layoutParams1);
                break;
            case "find":

                break;
            default:
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        mTitleBar.setVisibility(View.VISIBLE);
        showMenu = true;
        boolean showNavigation = false;
        switch (menuItem.getItemId()) {
            case R.id.em_main_nav_contacts:
                switchToHome();
                mTitleBar.setTitle(getResources().getString(R.string.em_main_title_contacts));
                showNavigation = true;
                break;
            case R.id.em_main_nav_message:
                switchToFriends();
                mTitleBar.setTitle(getResources().getString(R.string.em_main_title_message));
                showNavigation = true;
                invalidateOptionsMenu();
                break;
            case R.id.em_main_nav_discover:
                switchToDiscover();
                mTitleBar.setTitle(getResources().getString(R.string.em_main_title_discover));
                showNavigation = true;
                break;
            case R.id.em_main_nav_find:
                switchToAboutMe();
                mTitleBar.setTitle(getResources().getString(R.string.em_main_title_discover));
                showMenu = false;
                showNavigation = true;
                break;
        }
        invalidateOptionsMenu();
        return showNavigation;
    }

    private void checkUnreadMsg() {
        viewModel.checkUnreadMsg();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DemoHelper.getInstance().showNotificationPermissionDialog();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (mCurrentFragment != null) {
            outState.putString("tag", mCurrentFragment.getTag());
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.iv_add_friends:
                AddContactActivity.startAction(mContext, SearchType.CHAT);
                break;
            case R.id.iv_search_friends:
                SearchConversationActivity.actionStart(mContext);
                break;

            case R.id.iv_avatar:

                break;
            default:
                break;
        }
    }
}
