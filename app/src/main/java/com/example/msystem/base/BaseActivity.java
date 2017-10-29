package com.example.msystem.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.WindowManager;
import android.widget.Toast;

import butterknife.ButterKnife;

/**
 * baseActivity,使用时候需要先调用getLayoutId();
 */
public abstract class BaseActivity extends AppCompatActivity {
    private static long lastClickTime;
    public ProgressDialog waitdialog;
    public Context mContext;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WindowManager.LayoutParams localLayoutParams =getWindow().getAttributes();
            localLayoutParams.flags = (WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS | localLayoutParams.flags);//状态栏变透明
        }

        mContext = BaseActivity.this;
        if (getLayoutId() != 0) {
            setContentView(getLayoutId());
            ButterKnife.bind(this);
        } else {
            toastShort("未加载布局");
        }
        //初始化
        doMainUI();
    }

    public abstract int getLayoutId();

    public abstract void doMainUI();
    /**
     * 弹出toast 显示时长short
     * @param pMsg
     */
    protected void toastShort(String pMsg) {
        Toast.makeText(this, pMsg, Toast.LENGTH_SHORT).show();
    }
    /**
     * 弹出toase 显示时长long
     * @param pMsg
     */
    protected void toastLong(String pMsg) {
        Toast.makeText(this, pMsg, Toast.LENGTH_LONG).show();
    }



    //判断手机是否有网络2g 3g wifi..
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager != null) {
            NetworkInfo info = connectivityManager.getActiveNetworkInfo();
            if (info != null && info.isConnected()) {
                //当前网络是连接的
                if (info.getState() == NetworkInfo.State.CONNECTED) {
                    return true;
                }
            }
        }
        return false;
    }

    public synchronized static boolean isFastClick() {
        long time = System.currentTimeMillis();
        if (time - lastClickTime < 500) {
            return true;
        }
        lastClickTime = time;
        return false;
    }




    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public ProgressDialog showWaitDialog(String text) {
        if (waitdialog != null) {
            if (!waitdialog.isShowing()) {
                waitdialog.setMessage(text);
                waitdialog.setCancelable(true);
                waitdialog.show();
                return waitdialog;
            }
            return null;
        } else {
            waitdialog = new ProgressDialog(mContext);
            waitdialog.setMessage(text);
            waitdialog.setCancelable(true);//按其他地方，对话框不会取消
            waitdialog.show();
            return waitdialog;
        }
    }

    public void hideWaitDialog() {
        if (waitdialog != null && waitdialog.isShowing()) {
            try {
                waitdialog.dismiss();//取消对话框
                waitdialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }



}
