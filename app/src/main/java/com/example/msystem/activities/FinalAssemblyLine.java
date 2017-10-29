package com.example.msystem.activities;

import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.msystem.R;
import com.example.msystem.adapter.TotalChanXianAdapter;
import com.example.msystem.base.BaseActivity;

import java.util.List;

/**
 * Created by stevenZhang on 2017/8/6.
 *
 * 第二种模式总装线
 */

public class FinalAssemblyLine extends BaseActivity {

    private RecyclerView recycler;


    @Override
    public int getLayoutId() {

        return R.layout.activity_assembly_line;
    }

    @Override
    public void doMainUI() {


        initView();

        initData();

    }

    private void initView() {

        recycler = (RecyclerView) findViewById(R.id.recycler_chanxian);
      /*
        设置布局管理器，相当于ListView,不设置会报错出现No layout manager attached; skipping layout错误
       */

        recycler.setLayoutManager(new LinearLayoutManager(mContext));


    }

    private void initData() {


        List list =null;
        TotalChanXianAdapter adapter = new TotalChanXianAdapter(list);
        recycler.setAdapter(adapter);
    }


}
