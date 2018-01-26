package com.example.msystem.activities;


import android.app.ProgressDialog;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.msystem.R;
import com.example.msystem.base.App;
import com.example.msystem.base.BaseActivity;
import com.example.msystem.base.ConstantField;
import com.example.msystem.model.IpAddressBean;
import com.example.msystem.utils.ToastUtils;
import com.example.msystem.utils.XmlUtils;
import com.example.msystem.widget.LoadingDialogs;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by steven on 2017/10/18.
 */

public class ConfigURLActivity extends BaseActivity implements View.OnClickListener {

    private ProgressDialog progressDialog;
    private LoadingDialogs dialogs;
    private EditText etAddress;


    @Override
    public int getLayoutId() {

        return R.layout.activity_config_url;

    }

    @Override
    public void doMainUI() {


        initView();

        initData();

    }


    private void initView() {

        Button btnTest = (Button) findViewById(R.id.btnTest);
        Button btnSave = (Button) findViewById(R.id.btnSave);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        etAddress = (EditText) findViewById(R.id.et_address);
        btnSave.setOnClickListener(this);
        btnTest.setOnClickListener(this);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);//设置返回图标
        getSupportActionBar().setHomeButtonEnabled(true);
        toolbar.setTitle("设置服务器地址");


    }

    private void initData() {

       String strInputAddress = App.mCache.getAsString(ConstantField.SAVEADDRESS);
        if(strInputAddress!=null){
            etAddress.setText(strInputAddress);
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btnSave:

                checkAddress();

                break;

            case R.id.btnTest:
                checkAddress();
                break;


        }
    }


    /**
     *
     * 实现功能：
     *
     * 测试ip地址连接是否正确
     * 保存输入的地址
     *
     *
     * 注意事项：
     *
     *
     */
    public void checkAddress() {

        final String strAddress = etAddress.getText().toString().trim();

        if (TextUtils.isEmpty(strAddress)) {
            ToastUtils.showShort("输入的地址不能为空");
            return;
        }

        App.mCache.put(ConstantField.SAVEADDRESS,strAddress);

        OkHttpUtils.post().url("http://"+strAddress +"/AutoStoreWebSrv.asmx/TestConnect")
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        dialogs = new LoadingDialogs(ConfigURLActivity.this, "连接测试中，请稍候...");
                        dialogs.show();

                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                        dialogs.close();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        dialogs.close();
                        try {
                            String json = XmlUtils.getJosn(response);
                            IpAddressBean ipAddressBean = new Gson().fromJson(json, IpAddressBean.class);
                            String strValue = ipAddressBean.getStrResult();
                            if (TextUtils.equals("1", strValue)) {
                                ToastUtils.showShort("连接成功");
                                App.mCache.put(ConstantField.SAVEADDRESS,strAddress);
                                finish();
                            }

                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }


                    }
                });


    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                checkAddress();
                break;
        }
        return super.onOptionsItemSelected(item);
    }


}
