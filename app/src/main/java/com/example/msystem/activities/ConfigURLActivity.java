package com.example.msystem.activities;


import android.app.ProgressDialog;

import com.example.msystem.R;
import com.example.msystem.base.BaseActivity;

/**
 * Created by steven on 2017/10/18.
 */

public  class ConfigURLActivity extends BaseActivity{

    private ProgressDialog progressDialog;


    @Override
    public int getLayoutId() {
        return R.layout.activity_config_url;
    }

    @Override
    public void doMainUI() {


    }


    protected void showTestingProgress(boolean show) {
        if (show) {
            if (this.progressDialog == null) {
                this.progressDialog = ProgressDialog.show(this, null, "连接测试中，请稍候...", true, false);
            } else if (!this.progressDialog.isShowing()) {
                this.progressDialog.show();
            }
        } else if (this.progressDialog != null) {
            this.progressDialog.cancel();
        }
    }


}
