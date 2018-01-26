package com.example.msystem.utils;

/**
 * Created by stevenzhang on 2016/9/3 0003.
 */
import android.content.Context;
import android.widget.Toast;

/**
 * Toast统一管理类
 *
 */
public class ToastUtils
{

    private static ToastUtils toastUtils;
    private static Context context;
    public static synchronized ToastUtils getInstance(){

        if(toastUtils ==null){
            toastUtils = new ToastUtils();
        }
        return toastUtils;
    }
    public void initContext(Context cx){
        context  = cx;
    }




    public static boolean isShow = true;

    /**
     * 短时间显示Toast
     *
     * param context
     * @param message
     */
    public static void showShort(CharSequence message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 短时间显示Toast
     *
     * param context
     * @param message
     */
    public static void showShort(int message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
    }

    /**
     * 长时间显示Toast
     *
     * param context
     * @param message
     */
    public static void showLong(CharSequence message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 长时间显示Toast
     *
     * param context
     * @param message
     */
    public static void showLong(int message)
    {
        if (isShow)
            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * param context
     * @param message
     * @param duration
     */
    public static void show(CharSequence message, int duration)
    {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

    /**
     * 自定义显示Toast时间
     *
     * param context
     * @param message
     * @param duration
     */
    public static void show(int message, int duration)
    {
        if (isShow)
            Toast.makeText(context, message, duration).show();
    }

}
