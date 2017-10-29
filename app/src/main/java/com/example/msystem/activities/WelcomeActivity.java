package com.example.msystem.activities;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.example.msystem.R;
import com.example.msystem.base.App;
import com.example.msystem.base.BaseActivity;
import com.example.msystem.utils.ToastUtils;

/**
 * Created by stevenzhang on 2016/11/15 0015.
 */
public class WelcomeActivity extends BaseActivity {

    private static final int TIME = 2000;
    private static final int GO_HOME = 0x11;
    private static final int GO_LOGIN = 0x10;
    private static final String TAG = "WelcomeActivity";


    @Override
    public int getLayoutId() {
        return R.layout.activity_welcome;
    }

    @Override
    public void doMainUI() {


        Boolean netWork = BaseActivity.isNetworkAvailable(mContext);
        if(!netWork){

            ToastUtils.showShort("当前网络不可用，请检测网络状态");
            return;
        }
        
        String str = App.mCache.getAsString("alreadyLogin");

        //每次都需要重新登录
        if (str != null && str.equals("yes")) {
//            mHandler.sendEmptyMessageDelayed(GO_HOME, TIME);
            mHandler.sendEmptyMessageDelayed(GO_LOGIN, TIME);
        } else {
//            mHandler.sendEmptyMessageDelayed(GO_HOME, TIME); //测试，直接进入MainActivity
            mHandler.sendEmptyMessageDelayed(GO_LOGIN, TIME);
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case GO_HOME:
                    goHome();
                    break;
                case GO_LOGIN:
                    goLogin();
                    break;
            }
            super.handleMessage(msg);
        }
    };


    private void goHome() {

        Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
        WelcomeActivity.this.startActivity(intent);
        finish();
    }

    private void goLogin() {

        Intent intent = new Intent(WelcomeActivity.this, LoginActivity.class);
        WelcomeActivity.this.startActivity(intent);
        finish();
    }


}
