package com.example.msystem.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.Toolbar;
import android.telephony.TelephonyManager;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextUtils;
import android.text.style.AbsoluteSizeSpan;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.msystem.R;
import com.example.msystem.base.App;
import com.example.msystem.base.BaseActivity;
import com.example.msystem.model.Constant;
import com.example.msystem.model.UserLgoinBean;
import com.example.msystem.network.HttpUrl;
import com.example.msystem.utils.CommonUtils;
import com.example.msystem.utils.ToastUtils;
import com.example.msystem.utils.XmlUtils;
import com.example.msystem.widget.DIYEditTextAccount;
import com.example.msystem.widget.DIYEditTextPWD;
import com.example.msystem.widget.LoadingDialogs;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import butterknife.Bind;
import butterknife.OnClick;
import okhttp3.Request;

/**
 * Created by stevenzhang on 2016/11/13 0013.
 */
public class LoginActivity extends BaseActivity {

    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.tv_ans_person)
    TextView tvAnsPerson;
    @Bind(R.id.iv_pic_ans)
    ImageView ivPicAns;
    @Bind(R.id.remember_pass)
    CheckBox rememberPass;

    private DIYEditTextAccount et_account;
    private DIYEditTextPWD et_passward;
    private LoadingDialogs dialogs;

    @Override
    public int getLayoutId() {
        return R.layout.activity_login;
    }

    @Override
    public void doMainUI() {
        et_account = (DIYEditTextAccount) findViewById(R.id.et_account);
        et_passward = (DIYEditTextPWD) findViewById(R.id.et_passward);

        initData();

    }

    private void initData() {

        et_account.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    final ViewGroup.LayoutParams params = showHeight(60);
                    // 获得焦点  
                    toolbar.setLayoutParams(params);
                    tvAnsPerson.setTextSize(18);
                    ivPicAns.setVisibility(View.GONE);

                } else {
                    // 失去焦点
                }
            }
        });

        et_passward.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if (hasFocus) {
                    // 获得焦点  
                    final ViewGroup.LayoutParams params = showHeight(60);
                    toolbar.setLayoutParams(params);
                    tvAnsPerson.setTextSize(18);
                    ivPicAns.setVisibility(View.GONE);
                } else {
                    final ViewGroup.LayoutParams params = showHeight(200);
                    toolbar.setLayoutParams(params);
                    tvAnsPerson.setTextSize(26);
                    ivPicAns.setVisibility(View.VISIBLE);
                    // 失去焦点
                }
            }
        });

//        submitServer("13456");

        /**
         * 记住密码功能
         *
         * 默认是不记住密码的
         *
         * 如果用户选择记住密码，1则进行勾选，下次登录能够自动填写
         *
         */

      String jizhupd =  App.mCache.getAsString(Constant.alreadyChecked);
        if(jizhupd!=null&&jizhupd.equals(Constant.alreadyChecked)){
            rememberPass.setChecked(true);
            et_passward.setText(App.mCache.getAsString(Constant.mima));
        }

        /**
         * 记住用户名功能
         *
         * 1下次登录的时候直接自动输入
         */

           et_account.setText(App.mCache.getAsString(Constant.userName));
    }

    /**
     * 2记住用户的密码，在登录的时候进行处理
     *
     * 2记住用户名字，在登录成功的时候记住用户名
     */

    private void rememberpdAndName(){

            if(rememberPass.isChecked()){
                System.out.println("记住密码已选中");
                App.mCache.put(Constant.alreadyChecked,Constant.alreadyChecked);
                App.mCache.put(Constant.mima,et_passward.getText().toString().trim());
            }else {
                App.mCache.remove(Constant.alreadyChecked);
                App.mCache.remove(Constant.mima);
                System.out.println("记住密码没有选中");
            }

            App.mCache.put(Constant.userName,et_account.getText().toString().trim());

        }
    private ViewGroup.LayoutParams showHeight(int i) {
        ViewGroup.LayoutParams lp = toolbar.getLayoutParams();
        lp.height = (int) CommonUtils.dpToPixel(i, mContext);
        return lp;

    }


    @OnClick({R.id.bg_commit})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bg_commit:
                if (!BaseActivity.isNetworkAvailable(mContext)) {
                    ToastUtils.showShort("当前网络不可用，请检测网络状态");
                    return;
                }
                String account = et_account.getText().toString();
                String passward = et_passward.getText().toString();
//                App.mCache.put("accountName",account);
//                App.mCache.put("passward",passward);
                TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                final String DEVICE_ID = tm.getDeviceId();

                OkHttpUtils.post()
                        .url(HttpUrl.baseUrl+"/UserLogin?")
                        .addParams("strUserName", account)
                        .addParams("strPwd", passward)
                        .build()
                        .execute(new StringCallback() {

                            @Override
                            public void onBefore(Request request, int id) {
                                super.onBefore(request, id);

                                dialogs = new LoadingDialogs(LoginActivity.this,"登录中");
                                dialogs.show();

                            }

                            @Override
                            public void onError(okhttp3.Call call, Exception e, int id) {
                                dialogs.close();
                                ToastUtils.showShort("信息"+e.getMessage());
                            }

                            /**
                             *  4 叫料权限
                             *  5 仓库权限
                             *  0 登录失败
                             *
                             * @param response
                             * @param id
                             */

                            @Override
                            public void onResponse(String response, int id) {

                                try {
                                    dialogs.close();
                                    String json = XmlUtils.getJosn(response);
                                    UserLgoinBean bean = new Gson().fromJson(json, UserLgoinBean.class);
                                    String result = bean.getStrResult();
                                    if (TextUtils.equals(result, "4")) {
//                                    if (TextUtils.equals(result, "0")) {
                                        ToastUtils.showShort("登录成功!");
                                        rememberpdAndName();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else if(TextUtils.equals(result, "5")){
                                        ToastUtils.showShort("登录成功!");
                                        rememberpdAndName();
                                        Intent intent = new Intent(LoginActivity.this,AllLineCallMaterial.class);
                                        startActivity(intent);
                                        finish();

                                    } else {
                                        ToastUtils.showShort("登录名或者密码错误");
                                    }
                                } catch (XmlPullParserException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                            }
                        });


                break;
        }
    }


    public static void startActivity(Activity activity) {

        Intent intent = new Intent(activity, LoginActivity.class);
        activity.startActivity(intent);
    }

    // 单独设置EditText控件中hint文本的字体大小，可能与EditText文字大小不同
// @param editText 输入控件
// @param hintText hint的文本内容
// @param textSize hint的文本的文字大小（以dp为单位设置即可）
    public static void setHintTextSize(EditText editText, String hintText, int textSize) {
        // 新建一个可以添加属性的文本对象
        SpannableString ss = new SpannableString(hintText);
        // 新建一个属性对象,设置文字的大小
        AbsoluteSizeSpan ass = new AbsoluteSizeSpan(textSize, true);
        // 附加属性到文本
        ss.setSpan(ass, 0, ss.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);

        // 设置hint
        editText.setHint(new SpannedString(ss)); // 一定要进行转换,否则属性会消失
    }


//    private void submitServer(String str) {
//
//        OkHttpUtils
//                .post()
//                .url(HttpUrl.baseUrl+"QueryNextMaterial")
//                .addParams("strReelID",str)
//                .build()
//                .execute(new StringCallback() {
//
//                    @Override
//                    public void onBefore(Request request, int id) {
//                        super.onBefore(request, id);
//
//
//                    }
//
//                    @Override
//                    public void onError(Call call, Exception e, int id) {
//
//                        ToastUtils.showShort("扫描出现错误:"+e.getMessage());
//
//                    }
//
//                    @Override
//                    public void onResponse(String response, int id) {
//
//                        try {
//
//                            String json = XmlUtils.getJosn(response);
//
//                            ToastUtils.showShort(json);
//                            SingleMaterial addCallListBean =  new Gson().fromJson(json, SingleMaterial.class);
//                            //存入数据库
//                            Material material = new Material();
//                            material.setStrStationName(addCallListBean.getStrStationName());//料站号
//                            material.setStrReelID(addCallListBean.getStrReelID());//ReelID
//                            material.setStrPartNo(addCallListBean.getStrPartNo());//料号
//                            material.setnQty(addCallListBean.getNQty());//数量
//                            material.save();//保存这次的扫描数据
//
//                        } catch (XmlPullParserException e) {
//                            e.printStackTrace();
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//
//                    }
//                });
//
//
//
//    }

}
