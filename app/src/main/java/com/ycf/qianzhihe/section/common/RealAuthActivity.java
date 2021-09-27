package com.ycf.qianzhihe.section.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.alibaba.fastjson.JSONObject;
import com.hyphenate.easeui.widget.EaseTitleBar;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;
import com.ycf.qianzhihe.R;
import com.ycf.qianzhihe.app.api.Constant;
import com.ycf.qianzhihe.app.api.global.UserComm;
import com.ycf.qianzhihe.app.api.new_data.CertifyBean;
import com.ycf.qianzhihe.app.api.old_data.LoginInfo;
import com.ycf.qianzhihe.app.api.old_data.WalletRechargeBean;
import com.ycf.qianzhihe.app.api.old_http.ApiClient;
import com.ycf.qianzhihe.app.api.old_http.AppConfig;
import com.ycf.qianzhihe.app.api.old_http.ResultListener;
import com.ycf.qianzhihe.app.api.old_http.ServerData;
import com.ycf.qianzhihe.app.base.BaseInitActivity;
import com.ycf.qianzhihe.app.utils.XClickUtil;
import com.ycf.qianzhihe.app.weight.ChooseMoneyLayout;
import com.ycf.qianzhihe.common.utils.ToastUtils;
import com.ycf.qianzhihe.section.account.activity.MineActivity;
import com.ymt.liveness.LivenessMainActivity;
import com.zds.base.Toast.ToastUtil;
import com.zds.base.json.FastJsonUtil;
import com.zds.base.log.XLog;
import com.zds.base.util.StringUtil;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.OnClick;

import static com.ycf.qianzhihe.app.api.Constant.APP_CODE;
import static com.ycf.qianzhihe.app.api.Constant.URL;

//实名认证
public class RealAuthActivity extends BaseInitActivity {

    @BindView(R.id.title_bar)
    EaseTitleBar mTitleBar;
    @BindView(R.id.et_name)
    EditText et_name;
    @BindView(R.id.et_idcard)
    EditText et_idcard;
    @BindView(R.id.et_phone)
    EditText et_phone;
    @BindView(R.id.tv_submit)
    TextView tv_submit;


    public static void actionStart(Context context) {
        Intent intent = new Intent(context, RealAuthActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.activity_real_auth;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        super.initView(savedInstanceState);
        mTitleBar.setTitle("实名认证");
        mTitleBar.setOnBackPressListener(view -> finish());

    }


    @OnClick({R.id.tv_submit})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.tv_submit:
                if (TextUtils.isEmpty(et_name.getText().toString().trim())) {
                    ToastUtils.showToast("请输入真实姓名");
                    return;
                }
                if (TextUtils.isEmpty(et_idcard.getText().toString().trim())) {
                    ToastUtils.showToast("请输入身份证号码");
                    return;
                }
                if (!XClickUtil.isFastDoubleClick(view, 1000)) {
                    //获取人脸认证流水号
                    getCertifyId();
                }
                break;
        }
    }

    private void getCertifyId() {
        OkGo.<String>post(Constant.URL_CERTIFY_ID)
                .tag(mContext)
                .upJson("")
                .headers("Authorization", "APPCODE " + APP_CODE)
                .execute(new StringCallback() {
                             @Override
                             public void onSuccess(Response<String> response) {
                                 //body:{"errcode":"L0000","
                                 // result":{"certifyid":"ZTA9FA4725092A4486AEDB34DEEB267D50"},"
                                 // encresult":"","jobid":"ZT20210907161436066961823","responsetime":"20210907161436088","errmsg":"正常使用"}
                                 String json = response.body();
                                 CertifyBean certifyBean = FastJsonUtil.getObject(json, CertifyBean.class);
                                 if (certifyBean.getErrcode().equals("L0000")) {
                                     doStartDetect(certifyBean.getResult().getCertifyid());
                                 } else {
                                     ToastUtils.showToast(certifyBean.getErrmsg());
                                 }
                             }

                             @Override
                             public void onFinish() {
                                 super.onFinish();
                             }

                             @Override
                             public void onError(Response<String> response) {
                                 ToastUtils.showToast("获取认证服务失败");
                             }
                         }

                );
    }

    private void doStartDetect(String certifyId) {
        Intent intent = new Intent(mContext, LivenessMainActivity.class);
        Bundle bundle = new Bundle();
        JSONObject obj = new JSONObject();
        obj.put("url", URL); // 云市场地址
        obj.put("appCode", "e2c9b94c8b114a7782d0d82ea56728ba"); // 云市场产品appcode
        obj.put("liveType", "2");
        obj.put("idcard", et_idcard.getText().toString().trim()); // 身份证
        obj.put("realname", et_name.getText().toString().trim()); // 姓名
        obj.put("certifyid", certifyId); // 认证流水号
        bundle.putString("bizData", obj.toJSONString());
        bundle.putString("actions", "1279");  // 动作组合 1 摇头 2点头 7张嘴 9眨眼
        bundle.putString("actionsNum", "3"); // 动作数量
        intent.putExtra("liveness", bundle);
        startActivityForResult(intent, 103);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable @org.jetbrains.annotations.Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 103) {
            switch (resultCode) { //resultCode 为回传的标记，RESULT_OK
                case RESULT_OK:
                    if (null != data) {
                        Bundle result = data.getBundleExtra("result");
                        System.out.println("###认证返回=" + result.toString());
                        // 活体错误码
                        String code = result.getString("code");
                        // 活体错误信息
                        String msg = result.getString("msg");
                        if (code.equals("0")) {
                            // 人像比对通过, data.errcode="P0000"时返回图片保存路径，完整图像
                            String passImgPath = result.getString("passImgPath");
                            // 人像比对通过, data.errcode="P0000"时返脸部 base64，剪裁后的脸部图像
                            String passFace = result.getString("passFace");
                            // 人像比对结果
                            String jsonData = result.getString("data");
                            //{"encresult":"","errcode":"P0000","errmsg":"系统判定为同一人","jobid":"ZT20210910221036818370532","responsetime":"20210910221037613","
                            // result":{"address":"河南省周口地区沈丘县","birthday":"19880916","score":"93","sex":"男"}}
                            String errorCode = FastJsonUtil.getString(jsonData, "errcode");
                            String errmsg = FastJsonUtil.getString(jsonData, "errmsg");
                            if (errorCode.equals("P0000")) {
//                                uplpadImg(passImgPath,passFace,jsonData);
                                ToastUtils.showToast("实名认证成功");
                            } else {
                                ToastUtils.showToast(errmsg);
                            }
                        } else {
                            ToastUtils.showToast(msg);
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 上传头像地址到服务器
     */
    private void uplpadImg(String passImgPath, String passFace, String jsonData) {
        ApiClient.requestNetHandleFile(mContext, AppConfig.uploadImg, "", new File(passImgPath), new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                System.out.println("####认证后上传人脸地址=" + json);
                //提交实名认证
                openAccount(et_name.getText().toString().trim(), et_phone.getText().toString().trim(), et_idcard.getText().toString().trim(),passImgPath,passFace,jsonData);
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });
    }


    private void openAccount(String name, String mobile, String idCardNo, String passImgPath,  String passFace, String jsonData) {
        Map<String, Object> map = new HashMap<>();
        map.put("name", name);
        map.put("idCardNo", idCardNo);
        map.put("mobile", mobile);
        map.put("passFace", passFace);//passFace":"",//比对成功后，图像Base64数据
        map.put("passImgPath", passImgPath);//passImgPath":"",//比对成功后，图像保存路径
        map.put("data", jsonData);//data":""//json
        ApiClient.requestNetHandle(this, AppConfig.openAccount, "认证中...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
                LoginInfo loginInfo = UserComm.getUserInfo();
                loginInfo.setOpenAccountFlag(1);
                UserComm.saveUsersInfo(loginInfo);
                ToastUtil.toast("认证成功");
                finish();
            }

            @Override
            public void onFinsh() {
                super.onFinsh();

            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);

            }
        });
    }




    private void doRechargeClick() {

       /* if (TextUtils.isEmpty(rechargeMoney)) {
            toast("请输入充值金额");
            return;
        }*/

        Map<String, Object> map = new HashMap<>();
        map.put("rechargeMoney", Double.parseDouble(String.valueOf("")));
        map.put("cardId", "xxx");
        //map.put("payPassword", psw);
        map.put("payType", 1);
        ApiClient.requestNetHandle(this, AppConfig.rechargeUrl, "充值中...", map, new ResultListener() {
            @Override
            public void onSuccess(String json, String msg) {
//                startActivity(new Intent(RechargeActivity.this, WebViewActivity.class).putExtra("url", json).putExtra("title", "充值"));
                Log.d("####", json.toString());
                if (json != null && json.length() > 0) {
                    WalletRechargeBean walletRechargeBean = FastJsonUtil.getObject(json, WalletRechargeBean.class);

                   /* WalletPay walletPay = WalletPay.Companion.getInstance();
                    walletPay.init(RechargeActivity.this);
                    walletPay.walletPayCallback = new WalletPay.WalletPayCallback() {
                        @Override
                        public void callback(@Nullable String source, @Nullable String status, @Nullable String errorMessage) {
                            if (status == "SUCCESS" || status == "PROCESS") {
                                //queryResult(walletRechargeBean.requestId);
                                ToastUtil.toast("充值成功");
                                finish();
                            } else {//后加的
                                ToastUtil.toast("充值失败");
                            }
                        }
                    };*/
                    //支付SDK
                    /*ArrayList<String> list = new ArrayList<>();
                    list.add(AuthType.APP_PAY.name());
                    walletPay.setOnlySupportBalance(true,list);*/
                    //商户编号  钱包id  后台返回的支付token和requestId
//                    walletPay.evoke(Constant.MERCHANT_ID, UserComm.getUserInfo().ncountUserId,
//                            walletRechargeBean.token, AuthType.APP_PAY.name());

                } else {
                    ToastUtil.toast("服务器开小差，请稍后重试");
                }
            }

            @Override
            public void onFailure(String msg) {
                ToastUtil.toast(msg);
            }
        });

    }

    private void queryResult(String requestId) {
        Map<String, Object> map = new HashMap<>();
        map.put("requestId", requestId);
        ApiClient.requestNetHandleByGet(this, AppConfig.walletRechargeQuery, "请稍等...",
                map, new ResultListener() {
                    @Override
                    public void onSuccess(String json, String msg) {
                        if (json != null && json.length() > 0) {
//                            WalletRechargeQueryBean walletRechargeQueryBean = FastJsonUtil.getObject(json, WalletRechargeQueryBean.class);
                            WalletRechargeBean walletRechargeQueryBean = FastJsonUtil.getObject(json, WalletRechargeBean.class);
                            switch (walletRechargeQueryBean.orderStatus) {
                                case "SUCCESS":
                                    // TODO: 2021/3/22 关闭当前页面，并刷新钱包余额
                                    ToastUtil.toast("充值成功");
                                    finish();
                                    break;
                                case "PROCESS":
                                    /*handler.postDelayed(new Runnable() {
                                        @Override
                                        public void run() {
                                            maxCount--;
                                            if (maxCount <= 0) {
                                                ToastUtil.toast("充值处理中");
                                                return;
                                            }
                                            queryResult(requestId);
                                        }
                                    }, 2000);*/
                                    break;
                                default:
                                    ToastUtil.toast("充值失败");
                                    break;
                            }

                        } else {
                            ToastUtil.toast("服务器开小差，请稍后重试");
                        }
                    }

                    @Override
                    public void onFailure(String msg) {
                        ToastUtil.toast(msg);
                    }
                });
    }

}
