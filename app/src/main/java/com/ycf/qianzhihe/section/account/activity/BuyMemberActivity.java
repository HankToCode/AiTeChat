package com.ycf.qianzhihe.section.account.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.constraintlayout.widget.ConstraintLayout;

import com.coorchice.library.SuperTextView;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.new_data.VipBean;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.CommonApi;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.weight.ChooseMemberLayout;
import com.ycf.qianzhihe.app.weight.CommonDialog;
import com.hyphenate.easeui.widget.EaseImageView;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.luck.picture.lib.PictureSelector;
import com.luck.picture.lib.config.PictureConfig;
import com.luck.picture.lib.config.PictureMimeType;
import com.luck.picture.lib.entity.LocalMedia;
import com.ycf.qianzhihe.app.weight.PasswordEditText;
import com.ycf.qianzhihe.app.weight.passdialog.PayDialog;
import com.ycf.qianzhihe.app.weight.passwoed_keyboard.OnNumberKeyboardListener;
import com.ycf.qianzhihe.app.weight.passwoed_keyboard.XNumberKeyboardView;
import com.ycf.qianzhihe.common.utils.ToastUtils;
import com.ycf.qianzhihe.section.common.BankActivity;
import com.ycf.qianzhihe.section.common.InputPasswordActivity;
import com.ycf.qianzhihe.section.common.ResetPayPwdActivity;
import com.ycf.qianzhihe.section.common.SendGroupRedPackageActivity;
import com.ycf.qianzhihe.section.common.VerifyingPayPasswordPhoneNumberActivity;
import com.zds.base.ImageLoad.GlideUtils;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

//会员信息
public class BuyMemberActivity extends BaseInitActivity implements View.OnClickListener {

    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.ll_my_info)
    ConstraintLayout mLlMyInfo;
    @BindView(R.id.iv_avatar)
    EaseImageView mIvAvatar;
    @BindView(R.id.tv_nick_name)
    TextView mTvNickName;
    @BindView(R.id.tv_user_id)
    TextView mTvUserId;
    @BindView(R.id.tv_user_level)
    SuperTextView tv_user_level;
    @BindView(R.id.iv_user_level_tag)
    ImageView iv_user_level_tag;
    @BindView(R.id.tv_pay)
    SuperTextView tv_pay;
    @BindView(R.id.cml_member)
    ChooseMemberLayout cml_member;
    private String vipId="";
    private int vipLevel=0;
    private String vipPrice = "";


    public static void actionStart(Context context) {
        Intent starter = new Intent(context, BuyMemberActivity.class);
        context.startActivity(starter);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_bug_member;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        initImmersionBar(true);
    }
    @Override
    protected void initListener() {
        super.initListener();
        mIvAvatar.setOnClickListener(this);
        tv_pay.setOnClickListener(this);
        mTitleBar.setOnBackPressListener(view -> finish());
        mTitleBar.setLeftImageResource(R.mipmap.icon_back_white);
    }

    @Override
    protected void initData() {
        super.initData();
        initUserInfo();
        initVipInfo();
    }
    private void initVipInfo() {
        Map<String, Object> map = new HashMap<>();
        ApiClient.requestNetHandle(mContext, AppConfig.getSysVipInfo, "加载中..", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    System.out.println("###会员返回="+json.toString());
                    List<VipBean> vipBeanList = new ArrayList<>(FastJsonUtil.getList(json, VipBean.class));
                    showVipView(vipBeanList);
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtils.showToast(msg);
            }
        });
    }

    private void showVipView(List<VipBean> data) {

        //初始化金额选择
        cml_member.setMoneyData(data);
        if (!TextUtils.isEmpty(UserComm.getUserInfo().getVipLevel())) {
            int lve = Double.valueOf(UserComm.getUserInfo().getVipLevel()).intValue();
            cml_member.setDefaultPositon(lve - 1);
        } else {
            cml_member.setDefaultPositon(0);
        }
        int size = 4;
        int length = 100;
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        int gridviewWidth = (int) (size * (length + 4) * density);
        int itemWidth = (int) (length * density);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                gridviewWidth, LinearLayout.LayoutParams.FILL_PARENT);
        cml_member.setLayoutParams(params); // 设置GirdView布局参数,横向布局的关键
        cml_member.setColumnWidth(itemWidth); // 设置列表项宽
        cml_member.setHorizontalSpacing(2); // 设置列表项水平间距
        cml_member.setStretchMode(GridView.NO_STRETCH);
        cml_member.setNumColumns(size); // 设置列数量=列表集合数
        cml_member.setOnChoseMoneyListener(new ChooseMemberLayout.onChoseMoneyListener() {
            @Override
            public void chooseMoney(int position, boolean isCheck, VipBean itemData) {
                Log.d("tag", "会员选择=" + itemData.getVipPrice());
                //选择金额回调
                if (isCheck) {
                    vipId = itemData.getVipId();
                    vipLevel = Double.valueOf(itemData.getVipLevel()).intValue();
                    vipPrice = itemData.getVipPrice();
                } else {
                    vipId = "";
                    vipPrice = "";
                    vipLevel=0;
                }
            }
        });
    }

    /**
     * 初始化用户信息
     */
    private void initUserInfo() {
        LoginInfo loginInfo = UserComm.getUserInfo();
        GlideUtils.GlideLoadCircleErrorImageUtils(this, AppConfig.checkimg(loginInfo.getUserHead()), mIvAvatar, R.mipmap.img_default_avatar);
        mTvNickName.setText(loginInfo.getNickName());
        if (TextUtils.isEmpty(loginInfo.getUserCode())) {
            mTvUserId.setText("ID： 无");
        } else {
            mTvUserId.setText("ID： " + loginInfo.getUserCode());
        }
        tv_user_level.setText("lv "+loginInfo.getUserLevel());
        //是否是会员 vipid
        if (!TextUtils.isEmpty(loginInfo.getVipLevel())) {
            iv_user_level_tag.setBackgroundResource(R.drawable.ic_mine_level_tag);
//            tv_pay.setShaderStartColor(R.color.cl72);
//            tv_pay.setShaderStartColor(R.color.cl21);
            if (Double.valueOf(loginInfo.getVipLevel()).intValue() < 4) {
                tv_pay.setText("立即升级 尊享更高权益");
            } else {
                tv_pay.setShaderStartColor(R.color.cl72);
//            tv_pay.setShaderStartColor(R.color.cl21);
                tv_pay.setText("您已是当前最高会员等级");
                tv_pay.setEnabled(false);
            }
        } else {
            tv_pay.setText("立即开通 永久尊享权益");
//            tv_pay.setShaderStartColor(R.color.cle1);
//            tv_pay.setShaderStartColor(R.color.clcb);
            iv_user_level_tag.setBackgroundResource(R.drawable.ic_mine_level_tag_normal);
        }

    }


    @Override
    public void onClick(View view) {

        switch (view.getId()) {
            case R.id.iv_avatar:
                toSelectPic();
                break;
            case R.id.tv_pay:
                if (!TextUtils.isEmpty(UserComm.getUserInfo().getVipLevel())) {
                    if (vipLevel <= Double.valueOf(UserComm.getUserInfo().getVipLevel()).intValue()) {
                        ToastUtil.toast("请选择更高会员等级");
                        return;
                    }
                }
//                payPassword();
                payDialog();
                break;
            default:
                break;
        }
    }

    private PayDialog payDialog;
    private void payDialog() {
        LoginInfo userInfo = UserComm.getUserInfo();
        if (userInfo.getPayPwdFlag() == 0) {
            startActivity(new Intent(BuyMemberActivity.this,
                    InputPasswordActivity.class));
            return;
        }
         payDialog = new PayDialog(mContext, "支付金额："+vipPrice, new PayDialog.PayInterface() {
            @Override
            public void Payfinish(String password) {
                bugVip(password);
            }

            @Override
            public void onSucc() {
                //回调 成功，关闭dialog 做自己的操作
                payDialog.cancel();
            }

            @Override
            public void onForget() {
                //当progress显示时，说明在请求网络，这时点击忘记密码不作处理
                if(payDialog.payPassView.progress.getVisibility()!=View.VISIBLE){
                    ResetPayPwdActivity.actionStart(mContext);
//                    startActivityForResult(new Intent(BuyMemberActivity.this, BankActivity.class), 66);
                }
            }
        });
        payDialog.show();
    }

    private void bugVip(String password) {
        Map<String, Object> map = new HashMap<>();
        map.put("vipId", vipId);
        map.put("payPassword", password);
        ApiClient.requestNetHandle(mContext, AppConfig.saveUserVip, "", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                if (json != null) {
                    payDialog.setSucc();
                    System.out.println("###会员返回=" + json.toString());
                    ToastUtils.showToast("购买成功");
                    finish();
                } else {
                    payDialog.setError("支付密码不正确");
                }
            }

            @Override
            public void onFailure(String msg) {
                payDialog.setError("支付失败");
                ToastUtils.showToast(msg);
            }
        });
    }

    /**
     * 选择图片上传
     */
    private void toSelectPic() {
        final CommonDialog.Builder builder = new CommonDialog.Builder(this).fullWidth().fromBottom()
                .setView(R.layout.dialog_select_head);
        builder.setOnClickListener(R.id.tv_cell, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
            }
        });
        builder.setOnClickListener(R.id.tv_xiangji, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
                PictureSelector.create(BuyMemberActivity.this)
                        .openCamera(PictureMimeType.ofImage())
                        .selectionMode(PictureConfig.SINGLE)
                        .withAspectRatio(1, 1)
                        .enableCrop(true)
                        .showCropFrame(false)
                        .showCropGrid(false)
                        .freeStyleCropEnabled(true)
                        .circleDimmedLayer(false)
                        .forResult(PictureConfig.CHOOSE_REQUEST);
            }
        });
        builder.setOnClickListener(R.id.tv_xiangce, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                builder.dismiss();
                PictureSelector.create(BuyMemberActivity.this)
                        .openGallery(PictureMimeType.ofImage())
                        .selectionMode(PictureConfig.SINGLE)
                        .withAspectRatio(1, 1)
                        .enableCrop(true)
                        .showCropFrame(false)
                        .showCropGrid(false)
                        .freeStyleCropEnabled(true)
                        .circleDimmedLayer(false)
                        .forResult(PictureConfig.CHOOSE_REQUEST);

            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == PictureConfig.CHOOSE_REQUEST) {
                List<LocalMedia> selectList = PictureSelector.obtainMultipleResult(data);
                if (selectList.size() > 0) {
                    if (selectList.get(0).isCut()) {
                        compressImg(selectList.get(0).getCutPath());

                    } else {
                        compressImg(selectList.get(0).getPath());
                    }
                }
            }
        }
    }

    public void compressImg(String path) {
        Luban.with(this)
                .load(path)                                     // 传人要压缩的图片列表
                .ignoreBy(80)                                   // 忽略不压缩图片的大小
                .setTargetDir(this.getExternalCacheDir().getAbsolutePath())                             // 设置压缩后文件存储位置
                .setCompressListener(new OnCompressListener() { //设置回调
                    @Override
                    public void onStart() {
                        // TODO 压缩开始前调用，可以在方法内启动 loading UI
                    }

                    @Override
                    public void onSuccess(File file) {
                        // TODO 压缩成功后调用，返回压缩后的图片文件
                        saveHead(file);
                    }

                    @Override
                    public void onError(Throwable e) {
                        // TODO 当压缩过程出现问题时调用
                    }
                }).launch();
    }

    /**
     * 上传头像地址到服务器
     */
    private void saveHead(File file) {
        ApiClient.requestNetHandleFile(BuyMemberActivity.this, AppConfig.groupUpHead, "正在上传...", file, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                modifyHead(json);
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });

    }


    /**
     * 更新头像
     *
     * @param filePath
     */
    private void modifyHead(String filePath) {
        Map<String, Object> map = new HashMap<>(1);
        map.put("userHead", filePath);

        ApiClient.requestNetHandle(BuyMemberActivity.this, AppConfig.MODIFY_USER_HEAD, "", map, new ResultListener() {

            @Override
            public void onSuccess(String json, String msg) {
                GlideUtils.GlideLoadCircleErrorImageUtils(BuyMemberActivity.this, AppConfig.checkimg(filePath), mIvAvatar, R.mipmap.img_default_avatar);
                CommonApi.upUserInfo(BuyMemberActivity.this);
                ToastUtil.toast("上传成功");
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });

    }

}