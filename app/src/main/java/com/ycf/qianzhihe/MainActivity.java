package com.ycf.qianzhihe;

import static android.content.Intent.FLAG_ACTIVITY_NEW_TASK;

import static com.darsh.multipleimageselect.helpers.Constants.REQUEST_CODE;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.gyf.immersionbar.ImmersionBar;
import com.hyphenate.easecallkit.base.EaseCallType;
import com.hyphenate.easecallkit.ui.EaseMultipleVideoActivity;
import com.hyphenate.easecallkit.ui.EaseVideoCallActivity;
import com.ycf.qianzhihe.app.api.old_data.ContactListInfo;
import com.ycf.qianzhihe.app.api.old_data.MyGroupInfoList;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.operate.GroupOperateManager;
import com.ycf.qianzhihe.common.utils.ToastUtils;
import com.ycf.qianzhihe.section.contact.activity.AddUserActivity;
import com.ycf.qianzhihe.section.discover.NewsFragment;
import com.ycf.qianzhihe.section.me.MineFragment;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.code.activity.CaptureActivity;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.util.DensityUtils;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.Global;
import com.ycf.qianzhihe.app.api.global.EventUtil;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.old_data.EventCenter;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.base.ActivityStackManager;
import com.ycf.qianzhihe.app.operate.UserOperateManager;
import com.ycf.qianzhihe.app.weight.PopWinShare;
import com.ycf.qianzhihe.common.constant.DemoConstant;
import com.ycf.qianzhihe.common.permission.PermissionsManager;
import com.ycf.qianzhihe.common.permission.PermissionsResultAction;
import com.ycf.qianzhihe.common.utils.PushUtils;
import com.ycf.qianzhihe.section.MainViewModel;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.section.account.activity.LoginActivity;
import com.ycf.qianzhihe.section.account.activity.UserInfoDetailActivity;
import com.ycf.qianzhihe.section.chat.ChatPresenter;
import com.ycf.qianzhihe.section.common.ContactActivity;
import com.ycf.qianzhihe.section.common.MyQrActivity;
import com.ycf.qianzhihe.section.contact.fragment.ContactHomeFragment;
import com.ycf.qianzhihe.section.contact.viewmodels.ContactsViewModel;
import com.ycf.qianzhihe.section.conversation.ConversationListFragment;
import com.ycf.qianzhihe.section.discover.DiscoverFragment;
import com.ycf.qianzhihe.section.me.AboutMeFragment;
import com.hyphenate.easeui.model.EaseEvent;
import com.hyphenate.easeui.ui.base.EaseBaseFragment;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;


public class MainActivity extends BaseInitActivity implements BottomNavigationView.OnNavigationItemSelectedListener, View.OnClickListener {
    private BottomNavigationView navView;
    private EaseBaseFragment mContactsFragment, mMessageFragment, mDiscoverFragment, mFindFragment, mNewsFragment, mMineFragment;
    private EaseBaseFragment mCurrentFragment;
    private TextView mTvMainContactsMsg, mTvMainMessageMsg, mTvMainDiscoverMsg, mTvMainFindMsg;
    private int[] badgeIds = {R.layout.demo_badge_home, R.layout.demo_badge_about_me, R.layout.demo_badge_friends, R.layout.demo_badge_discover};
    private int[] msgIds = {R.id.tv_main_home_msg, R.id.tv_main_about_me_msg, R.id.tv_main_friends_msg, R.id.tv_main_discover_msg};
    private MainViewModel viewModel;
    private boolean showMenu = true;//是否显示菜单项
    private long exitTime;

    public static void actionStart(Context context) {
        Intent starter = new Intent(context, MainActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.demo_activity_main;
    }


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
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
//        initImmersionBar(true);
        navView = findViewById(R.id.nav_view);
        /*mTitleBar.setRightLayoutClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mTitleBar.getTitle().getText().equals("联系人")) {
//                    AddContactActivity.actionStart(mContext, SearchType.CHAT);//搜索>添加好友
                    AddUserActivity.actionStart(mContext);
                } else if (mTitleBar.getTitle().getText().equals("消息")) {
                    showPopWinShare(mTitleBar.getRightImage());
                }

            }
        });*/
        navView.setItemIconTintList(null);
        switchToMessage();
        checkIfShowSavedFragment(savedInstanceState);
        addTabBadge();
    }

    @Override
    protected void initListener() {
        super.initListener();
        navView.setOnNavigationItemSelectedListener(this);

        int period = 5;
        Map<String, Object> map = new HashMap<>();
        map.put("growthValue", period);
        Observable.interval(period, TimeUnit.MINUTES)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .as(autoDispose())
                .subscribe(new Observer<Long>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull Long aLong) {
                        ApiClient.requestNetHandle(MainActivity.this, AppConfig.saveUserGrowthValue, "", map,
                                new ResultListener() {
                                    @Override
                                    public void onSuccess(String json, String msg) {
                                    }

                                    @Override
                                    public void onFailure(String msg) {
//                                        ToastUtil.toast(msg);
                                    }
                                });
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });
        AppConfig.checkVersion(mContext, true);
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

        groupList();
        getContactList();

    }

    private void initViewModel() {
        viewModel = new ViewModelProvider(mContext).get(MainViewModel.class);

        viewModel.homeUnReadObservable().observe(this, readCount -> {
            if (!TextUtils.isEmpty(readCount)) {
                mTvMainMessageMsg.setVisibility(View.VISIBLE);
                mTvMainMessageMsg.setText(readCount);
            } else {
                mTvMainMessageMsg.setVisibility(View.GONE);
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
        viewModel.checkUnreadMsg(this);
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
                    mTvMainMessageMsg = badge.findViewById(msgIds[0]);
                    break;
                case 1:
                    mTvMainFindMsg = badge.findViewById(msgIds[1]);
                    break;

                case 2:
                    mTvMainContactsMsg = badge.findViewById(msgIds[2]);
                    break;
                case 3:
                    mTvMainDiscoverMsg = badge.findViewById(msgIds[3]);
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

    private void switchToNews() {
        if (mNewsFragment == null) {
            mNewsFragment = new NewsFragment();
        }
        replace(mNewsFragment, "news");
    }

    private void switchToMine() {
        if (mMineFragment == null) {
            mMineFragment = new MineFragment();
        }
        replace(mMineFragment, "mine");
    }

    private void switchToHome() {
        if (mContactsFragment == null) {
            mContactsFragment = new ContactHomeFragment();
        }
        replace(mContactsFragment, "contacts");
    }

    private void switchToMessage() {
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


    ImageView mIvAvatar;

    /**
     * 替换bar上按钮布局功能
     *
     * @param tag
     */
    private void replaceBarButtonLayout(String tag) {
        /*mTitleBar.getLeftLayout().removeAllViews();
        mTitleBar.getLeftLayout().setClickable(false);

        mTitleBar.getRightLayout().removeAllViews();
        mTitleBar.getRightLayout().setClickable(false);

        View leftView = View.inflate(this, R.layout.layout_toolbar_contacts_left, null);
        TextView tvTitle = leftView.findViewById(R.id.tv_title);
        tvTitle.setOnClickListener(this);
        mTitleBar.getLeftLayout().addView(leftView);


        View rightView = View.inflate(this, R.layout.layout_toolbar_contacts_right, null);
        mIvAvatar = rightView.findViewById(R.id.iv_avatar);
        mIvAvatar.setOnClickListener(view -> {
            CommonApi.upUserInfo(this);
            MineActivity.actionStart(mContext);
        });
        LoginInfo loginInfo = UserComm.getUserInfo();
        GlideUtils.GlideLoadCircleErrorImageUtils(this, AppConfig.checkimg(loginInfo.getUserHead()), mIvAvatar, R.mipmap.ic_ng_avatar);
        mTitleBar.getRightLayout().addView(rightView);

        if ("news".equals(tag)) {
            tvTitle.setText(getResources().getString(R.string.em_main_title_news));

        } else if ("contacts".equals(tag)) {
            tvTitle.setText(getResources().getString(R.string.em_main_title_contacts));
            rightView.findViewById(R.id.ivIcon4).setVisibility(View.VISIBLE);
            rightView.findViewById(R.id.ivIcon4).setOnClickListener(view -> {
//                AddUserActivity.actionStart(mContext);//搜索添加好友
                //扫一扫
                Global.addUserOriginType = Constant.ADD_USER_ORIGIN_TYPE_QRCODE;
                Intent intent = new Intent(MainActivity.this,
                        CaptureActivity.class);
                startActivityForResult(intent, REQUEST_CODE);
            });
        } else if ("message".equals(tag)) {
            tvTitle.setText(getResources().getString(R.string.em_main_title_message));
            rightView.findViewById(R.id.ivIcon1).setVisibility(View.VISIBLE);
            rightView.findViewById(R.id.ivIcon1).setOnClickListener(view -> {
                ApplyJoinGroupActivity.actionStart(mContext);//群通知
            });
            rightView.findViewById(R.id.ivIcon2).setVisibility(View.VISIBLE);
            rightView.findViewById(R.id.ivIcon2).setOnClickListener(view -> {
                AuditMsgActivity.actionStart(mContext);//好友申请列表
            });
            rightView.findViewById(R.id.ivIcon3).setVisibility(View.VISIBLE);
            rightView.findViewById(R.id.ivIcon3).setOnClickListener(view -> {
                showPopWinShare(rightView.findViewById(R.id.ivIcon3));
            });
        } else if ("discover".equals(tag)) {
            rightView.findViewById(R.id.ivIcon4).setVisibility(View.VISIBLE);
            rightView.findViewById(R.id.ivIcon4).setOnClickListener(view -> {
                AddUserActivity.actionStart(mContext);
            });
            tvTitle.setText(getResources().getString(R.string.em_main_title_discover));
        }*/
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        showMenu = true;
        boolean showNavigation = false;
        switch (menuItem.getItemId()) {
            case R.id.em_main_nav_contacts:
                switchToHome();
                showNavigation = true;
                break;
            case R.id.em_main_nav_message:
                switchToMessage();
                showNavigation = true;
                invalidateOptionsMenu();
                break;
            case R.id.em_main_nav_discover:
                switchToDiscover();
                showNavigation = true;
                break;
            /*case R.id.em_main_nav_find:
                switchToNews();//资讯
                showMenu = false;
                showNavigation = true;
                break;*/
            case R.id.em_main_nav_mine:
                switchToMine();
                showNavigation = true;
                break;
        }
        invalidateOptionsMenu();
        return showNavigation;
    }

    private void checkUnreadMsg() {
        viewModel.checkUnreadMsg(this);
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
    }

    /**
     * EventBus接收消息
     *
     * @param center 获取事件总线信息
     */
    @Override
    protected void onEventComing(EventCenter center) {
        if (center.getEventCode() == EventUtil.LOSETOKEN) {
            finish();
            ActivityStackManager.getInstance().killAllActivity();
            UserComm.clearUserInfo();
            LoginActivity.actionStart(this, "");
            //刷新公告数量
        } else if (center.getEventCode() == EventUtil.UNREADCOUNT) {
            checkUnreadMsg();
        } else if (center.getEventCode() == EventUtil.NOTICNUM) {
            checkUnreadMsg();
        } else if (center.getEventCode() == EventUtil.KEFU) {
//            change(2);
        } else if (center.getEventCode() == EventUtil.TONGXUNLU) {
//            change(1);
        } else if (center.getEventCode() == EventUtil.FLUSHGROUP) {
            groupList();
        } else if (center.getEventCode() == EventUtil.FLUSHUSERINFO) {
            LoginInfo loginInfo = UserComm.getUserInfo();
            if (loginInfo != null) {
                GlideUtils.GlideLoadCircleErrorImageUtils(this, AppConfig.checkimg(loginInfo.getUserHead()), mIvAvatar, R.mipmap.ic_ng_avatar);
            }
        } else if (center.getEventCode() == EventUtil.REFRESH_CONTACT) {
            getContactList();
        }
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
                                Intent intent = new Intent(MainActivity.this,
                                        CaptureActivity.class);
                                startActivityForResult(intent, REQUEST_CODE);
                            }//添加好友  //搜索添加好友
                            else if (v.getId() == R.id.layout_add_firend) {
//                                AddContactActivity.actionStart(mContext, SearchType.CHAT);
                                AddUserActivity.actionStart(mContext);
                            } else if (v.getId() == R.id.layout_group) {
                                ContactActivity.actionStart(MainActivity.this, "1", null, null);
                            } else if (v.getId() == R.id.layout_my_qr) {
                                Intent intent = new Intent(mContext, MyQrActivity.class);
                                intent.putExtra("from", "1");
                                startActivity(intent);
                            }

                            popWinShare.dismiss();
                        }


                    };

            popWinShare = new PopWinShare(mContext, paramOnClickListener
                    , (int) DensityUtils.getWidthInPx(MainActivity.this),
                    (int) DensityUtils.getHeightInPx(MainActivity.this) - DensityUtils.dip2px(mContext, 60) - DensityUtils.statusBarHeight2(mContext));
            //监听窗口的焦点事件，点击窗口外面则取消显示
            popWinShare.getContentView().setOnFocusChangeListener(new View.OnFocusChangeListener() {

                @Override
                public void onFocusChange(View v, boolean hasFocus) {
                    if (!hasFocus) {
                        popWinShare.dismiss();
                    }
                }
            });
        }
        //设置默认获取焦点
        popWinShare.setFocusable(true);
        //以某个控件的x和y的偏移量位置开始显示窗口
        popWinShare.showAsDropDown(view, 0,
                DensityUtils.dip2px(mContext, 8));
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


    /**
     * 我的群组列表
     *
     * @param
     */
    private void groupList() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("pageSize", 1500);
        map.put("pageNum", 1);
        Log.d("群组刷新", "群组刷新");
        ApiClient.requestNetHandle(this, AppConfig.MY_GROUP_LIST, "", map,
                new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        MyGroupInfoList myGroupInfo = FastJsonUtil.getObject(json,
                                MyGroupInfoList.class);
                        if (myGroupInfo.getData() != null && myGroupInfo.getData().size() > 0) {
                            GroupOperateManager.getInstance().saveGroupsInfo(myGroupInfo, json);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {

                    }
                });
    }

    /**
     * query contact
     *
     * @param
     */
    public void getContactList() {
        Map<String, Object> map = new HashMap<>(2);
        map.put("pageNum", 1);
        map.put("pageSize", 10000);

        //未同步通讯录到本地
        ApiClient.requestNetHandle(this, AppConfig.USER_FRIEND_LIST,
                "", map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        ContactListInfo info = FastJsonUtil.getObject(json,
                                ContactListInfo.class);
                        List<ContactListInfo.DataBean> mContactList = new ArrayList<>();
                        mContactList.addAll(info.getData());
                        if (mContactList.size() > 0) {
                            UserOperateManager.getInstance().saveContactListToLocal(info, json);
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });
    }


    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                ToastUtils.showToast(R.string.exit_text);
                exitTime = System.currentTimeMillis();
            } else {
                finish();
                ActivityStackManager.getInstance().killAllActivity();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}
