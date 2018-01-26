package com.example.msystem.activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.view.menu.MenuPopupHelper;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.msystem.R;
import com.example.msystem.base.App;
import com.example.msystem.base.BaseActivity;
import com.example.msystem.model.Constant;
import com.example.msystem.model.UserLgoinBean;
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
import java.lang.reflect.Field;

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
    @Bind(R.id.bg_commit)
    Button btnCommit;
    @Bind(R.id.login_menu)
    ImageButton imageBtn;

    private DIYEditTextAccount et_account;
    private DIYEditTextPWD et_passward;
    private LoadingDialogs dialogs;
    private static final int CONFIG_IP_ADDRESS =0;


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
                }
            }
        });


        /**
         * 记住密码功能
         *
         * 默认是不记住密码的
         *
         * 如果用户选择记住密码，1则进行勾选，下次登录能够自动填写
         *
         */

        String jizhupd = App.mCache.getAsString(Constant.alreadyChecked);
        if (jizhupd != null && jizhupd.equals(Constant.alreadyChecked)) {
            rememberPass.setChecked(true);
            et_passward.setText(App.mCache.getAsString(Constant.mima));
        }

        imageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popupMenu = new PopupMenu(LoginActivity.this, imageBtn);
                popupMenu.getMenuInflater().inflate(R.menu.menu_login_set, popupMenu.getMenu());
                //使用反射，强制显示菜单图标
                try {
                    Field field = popupMenu.getClass().getDeclaredField("mPopup");
                    field.setAccessible(true);
                    MenuPopupHelper mHelper = (MenuPopupHelper) field.get(popupMenu);
                    mHelper.setForceShowIcon(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {

                    @Override
                    public boolean onMenuItemClick(@NonNull MenuItem item) {
                        // stub
                        switch (item.getItemId()) {
                            case R.id.login_menu_server:
                                toConfigActivity();
                                break;
                        }
                        return false;
                    }
                });
                popupMenu.show();
            }
        });


        /**
         * 记住用户名功能
         *
         * 1下次登录的时候直接自动输入
         */

        et_account.setText(App.mCache.getAsString(Constant.userName));

    }

    /**
     *
     * 实现功能：
     *
     * 记住用户名和密码
     *
     * 注意事项：
     *
     *
     */

    private void rememberpdAndName() {

        if (rememberPass.isChecked()) {
            System.out.println("记住密码已选中");
            App.mCache.put(Constant.alreadyChecked, Constant.alreadyChecked);
            App.mCache.put(Constant.mima, et_passward.getText().toString().trim());
        } else {
            App.mCache.remove(Constant.alreadyChecked);
            App.mCache.remove(Constant.mima);
            System.out.println("记住密码没有选中");
        }

        App.mCache.put(Constant.userName, et_account.getText().toString().trim());

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

                if (TextUtils.isEmpty(account) || TextUtils.isEmpty(passward)) {
                    ToastUtils.showShort("用户名或密码不能为空!");
                    return;
                }


                OkHttpUtils.post()
                        .url(App.IP_ADDRESS + "/UserLogin?")
                        .addParams("strUserName", account)
                        .addParams("strPwd", passward)
                        .build()
                        .execute(new StringCallback() {

                            @Override
                            public void onBefore(Request request, int id) {
                                super.onBefore(request, id);

                                dialogs = new LoadingDialogs(LoginActivity.this, "登录中");
                                dialogs.show();

                            }

                            @Override
                            public void onError(okhttp3.Call call, Exception e, int id) {
                                dialogs.close();
                                ToastUtils.showShort("信息" + e.getMessage());
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
                                        ToastUtils.showShort("登录成功!");
                                        rememberpdAndName();
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    } else if (TextUtils.equals(result, "5")) {
                                        ToastUtils.showShort("登录成功!");
                                        rememberpdAndName();
                                        Intent intent = new Intent(LoginActivity.this, AllLineCallMaterial.class);
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


    /**
     * 跳转到设置界面
     */
    private void toConfigActivity() {
        startActivityForResult(new Intent(this, ConfigURLActivity.class), CONFIG_IP_ADDRESS);
    }


}
