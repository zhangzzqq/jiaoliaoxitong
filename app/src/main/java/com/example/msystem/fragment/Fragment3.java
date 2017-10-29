package com.example.msystem.fragment;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.msystem.R;
import com.example.msystem.activities.TestScanActivity;
import com.example.msystem.adapter.ReceiveMaterialAdapter;
import com.example.msystem.base.App;
import com.example.msystem.base.BaseFragment;
import com.example.msystem.model.Constant;
import com.example.msystem.model.Material;
import com.example.msystem.utils.L;
import com.example.msystem.utils.ToastUtils;

import org.litepal.crud.DataSupport;

import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.msystem.base.App.mCache;
import static com.example.msystem.model.Constant.danhao;


/**
 * Created by stevenZhang on 2017/7/14.
 */

public class Fragment3 extends BaseFragment implements EasyPermissions.PermissionCallbacks {

    private View v;
    private RecyclerView recycler;
    private ReceiveMaterialAdapter adapter;
    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;
    private TextView emptyView;
    private ImageView ivAdd;
    private String beiLiaoDanHao;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (v == null){

            v = inflater.inflate(R.layout.fragment_home3,null);

            initView();

            initData();

        }
        Log.d("EvaluatePagerFragment", "onCreateView1");
        return v;
    }


    private void initView() {

        recycler = (RecyclerView) v.findViewById(R.id.recycler_receive);
        emptyView = (TextView) v.findViewById(R.id.empty_view);
      /*
          设置布局管理器，相当于ListView,不设置会报错出现No layout manager attached; skipping layout错误
      */
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        ivAdd = (ImageView) v.findViewById(R.id.iv_add);
    }


    private void initData() {


        List<Material> list = DataSupport.findAll(Material.class);
        adapter = new ReceiveMaterialAdapter(list);
        //判断list数据是否为空，进行提示
        if(list.isEmpty()){
            recycler.setVisibility(View.GONE);
            emptyView.setVisibility(View.VISIBLE);
            emptyView.setText("没有数据");
        }else {
            recycler.setVisibility(View.VISIBLE);
            emptyView.setVisibility(View.GONE);
        }

        recycler.setAdapter(adapter);

        ivAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //fragment1中生成的单号
                beiLiaoDanHao = App.mCache.getAsString(danhao);
                if(TextUtils.isEmpty(beiLiaoDanHao)){
                    ToastUtils.showShort("没有生成叫料单号");

                }else {
                    //扫描权限申请
                    requestCodeQRCodePermissions();
                }
            }
        });


        //注册广播
        broadCast();

    }

    private void jump() {

        Intent intent = new Intent(getActivity(), TestScanActivity.class);
        intent.putExtra(Constant.beiLiaoDanHao,beiLiaoDanHao);
        startActivity(intent);
    }


    //第一次进入的时候会调用，获得权限后就不在调用
    @Override
    public void onPermissionsGranted(int requestCode, List<String> perms) {

        //已经获得了权限
        jump();
        L.d("Fragment1", "onPermissionsGranted");
    }

    @Override
    public void onPermissionsDenied(int requestCode, List<String> perms) {


    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        L.d("Fragment1", "onRequestPermissionsResult");
    }


    //默认会调用的方法
    @AfterPermissionGranted(REQUEST_CODE_QRCODE_PERMISSIONS)
    private void requestCodeQRCodePermissions() {
        String[] perms = {Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE};
        if (!EasyPermissions.hasPermissions(getActivity(), perms)) {
            EasyPermissions.requestPermissions(this, "扫描二维码需要打开相机和散光灯的权限", REQUEST_CODE_QRCODE_PERMISSIONS, perms);
        } else {
            jump();

            L.d("Fragment1", "AfterPermissionGranted");
        }
    }

//    //从上个页面返回数据
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == 0x13 && resultCode == 0x14) {
//            String str = data.getExtras().getString("result");
//             /*
//             * 对扫描后的最终结果进行提示 ，如果1收料结束进行提示，2否则不处理
//             *
//             * 完成按钮  用户点击了完成会返回过来数据，显示在界面上   //按返回键 显示在界面上
//             */
//
//             if(adapter.isFinish()){
//                 recycler.setVisibility(View.GONE);
//                 emptyView.setVisibility(View.VISIBLE);
//                 emptyView.setText("收料完成");
//             }else {
//                 recycler.setVisibility(View.VISIBLE);
//                 emptyView.setVisibility(View.GONE);
//             }
//
////            recycler.setAdapter(adapter);
//           adapter.notifyDataSetChanged();
//
//        }
//    }

    /**
     *
     * <p>
     * 传入数据源list,更新adapter,如果把数据源放入adapter中，更细数据容易不成功
     * 同时清除跳转标记
     *
     * 完成按钮和按返回键 更新数据
     * 全部完成后要清空数据库和单号信息
     *
     * 同时显示出一个收料完毕的信号（标记）
     */
    @Override
    public void onResume() {
        super.onResume();
        String result = mCache.getAsString(Constant.fgJumpFinish);
        if (result != null) {
            refreshReceiveData();
            App.mCache.remove(Constant.fgJumpFinish);

            /*
             * 对扫描后的最终结果进行提示 ，如果1收料结束进行提示，2否则不处理
             *
             */
            if(adapter.isFinish()){
                recycler.setVisibility(View.GONE);
                emptyView.setVisibility(View.VISIBLE);
                emptyView.setText("收料完成");
                String str = App.mCache.getAsString(danhao);
                if(str!=null){
                    App.mCache.remove(danhao);
                }
                //删除数据库
                DataSupport.deleteAll(Material.class);
                //收料完毕标记
//                App.mCache.put(Constant.shouLiaoFinish,Constant.shouLiaoFinish);

            }else {
                recycler.setVisibility(View.VISIBLE);
                emptyView.setVisibility(View.GONE);
            }
        }



    }

    private void refreshReceiveData() {

        List<Material> list = DataSupport.findAll(Material.class);
        if(adapter!=null){

            adapter.refresh(list);
            L.v("Fragment3","++++adapterRefresh++++");
        }
    }

    /**
     *
     * 通过广播进行一次通信，
     * 当生成收料订单后会进行刷新
     */

    private void broadCast(){

        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager
                .getInstance(getActivity());
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Constant.refreshDataAction);
        BroadcastReceiver br = new BroadcastReceiver() {

            @Override
            public void onReceive(Context context, Intent intent) {

               String str = intent.getStringExtra(Constant.refreshData);
                if(TextUtils.equals(str,Constant.refreshData)){
//                    ToastUtils.showShort("已经被通知刷新数据");
                    refreshReceiveData();
                }
            }

        };

        localBroadcastManager.registerReceiver(br, intentFilter);
    }


}
