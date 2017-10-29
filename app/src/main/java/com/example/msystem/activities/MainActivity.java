package com.example.msystem.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.FragmentTabHost;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.msystem.R;
import com.example.msystem.fragment.Fragment1;
import com.example.msystem.fragment.Fragment2;
import com.example.msystem.fragment.Fragment3;
import com.example.msystem.fragment.Fragment4;
import com.example.msystem.utils.L;
import com.example.msystem.utils.ToastUtils;

public class MainActivity extends AppCompatActivity {


    private FragmentTabHost tabHost;
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //让顶部的状态栏为透明状态，好让布局可以顶上去
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initView();

    }

    private void initView() {
        tabHost = (FragmentTabHost) findViewById(R.id.fragment_tabhost);
        //关联碎片
        tabHost.setup(this, getSupportFragmentManager(), R.id.fragment_tabcontent);
        //添加碎片
        tabHost.addTab(tabHost.newTabSpec("fragment1").setIndicator(getView(0)), Fragment1.class, null);
        tabHost.addTab(tabHost.newTabSpec("fragment2").setIndicator(getView(1)), Fragment2.class, null);
        tabHost.addTab(tabHost.newTabSpec("fragment3").setIndicator(getView(2)), Fragment3.class, null);
        tabHost.addTab(tabHost.newTabSpec("fragemnt4").setIndicator(getView(3)), Fragment4.class, null);
        //去掉间隔图片
        tabHost.getTabWidget().setDividerDrawable(null);
    }

    //得到视图 ,关联对应的tabhost
    private View getView(int n){
        View v = LayoutInflater.from(this).inflate(R.layout.layout_tab,null);
        ImageView tabImg = (ImageView) v.findViewById(R.id.tab_img);
        TextView tabText = (TextView) v.findViewById(R.id.tab_name);
        switch (n){
            case 0:
                tabImg.setBackgroundResource(R.drawable.tab_select1);
                tabText.setText(R.string.tab1);
                break;
            case 1:
                tabImg.setBackgroundResource(R.drawable.tab_select2);
                tabText.setText(R.string.tab2);
                break;
            case 2:
                tabImg.setBackgroundResource(R.drawable.tab_select3);
                tabText.setText(R.string.tab3);
                break;
            case 3:
                tabImg.setBackgroundResource(R.drawable.tab_select4);
                tabText.setText(R.string.tab4);
                break;
        }
        return v;


    }



}
