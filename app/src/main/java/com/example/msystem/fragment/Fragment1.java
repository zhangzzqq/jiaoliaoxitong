package com.example.msystem.fragment;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.msystem.R;
import com.example.msystem.activities.TestScanActivity;
import com.example.msystem.adapter.AddMaterialAdapter;
import com.example.msystem.base.App;
import com.example.msystem.base.BaseFragment;
import com.example.msystem.model.Constant;
import com.example.msystem.model.CreateMaterialBean;
import com.example.msystem.model.Material;
import com.example.msystem.utils.CommonUtils;
import com.example.msystem.utils.L;
import com.example.msystem.utils.ToastUtils;
import com.example.msystem.utils.XmlUtils;
import com.example.msystem.widget.LoadingDialogs;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.litepal.LitePal;
import org.litepal.crud.DataSupport;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import okhttp3.Call;
import okhttp3.Request;
import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.msystem.base.App.mCache;

/**
 * Created by stevenZhang on 2017/7/14.
 * <p>
 * 第一个fragment 加料
 */

public class Fragment1 extends BaseFragment implements EasyPermissions.PermissionCallbacks {


    @Bind(R.id.back)
    ImageView back;
    @Bind(R.id.iv_add)
    ImageView ivAdd;
    @Bind(R.id.recycler)
    RecyclerView recycler;
    @Bind(R.id.tv_order)
    TextView tvOrder;
    private View v;

    private static final int REQUEST_CODE_QRCODE_PERMISSIONS = 1;
    private AddMaterialAdapter adapter;
    private LoadingDialogs dialogs;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (v == null) {

            v = inflater.inflate(R.layout.fragment_home1, null);
            ButterKnife.bind(this, v);
            initView();
            initData();
        }

        return v;
    }


    private void initView() {

        //bugly测试
//        CrashReport.testJavaCrash();
    }


    private void initData() {
       /*
        设置布局管理器，相当于ListView,不设置会报错出现No layout manager attached; skipping layout错误
      */
        recycler.setLayoutManager(new LinearLayoutManager(getContext()));
        //创建数据库
        SQLiteDatabase db = LitePal.getDatabase();
//      Connector.getDatabase();
        /*
         *  setAdapter
         *
         *  如果已经生成了订单，则不显示出来
         *
         */
        List<Material> list = DataSupport.findAll(Material.class);
        String str = App.mCache.getAsString(Constant.danhao);
        if(str!=null){
            list.clear();
        }
        adapter = new AddMaterialAdapter(list);
        recycler.setAdapter(adapter);

    }

    /**
     * 生成json 提交给后台
     * <p>
     * 如果没有数据给个提示
     */
    private String getDataJson() {

        List<Material> materials = DataSupport.findAll(Material.class);
        //判断是list size()的值是否为0
        if (materials.size() == 0) {
            return null;
        }
        final JSONArray jsonArray = new JSONArray();
        for (Material material : materials) {
//            L.d("MainActivity", " tab0 " + material.getStrStationName());
//            L.d("MainActivity", " tab1 " + material.getStrReelID());
//            L.d("MainActivity", " tab2 " + material.getStrPartNo());
//            L.d("MainActivity", " tab3 " + material.getnQty());
            JSONObject jsonObject = new JSONObject();
            try {
                jsonObject.put("nID", material.getnID());
                jsonObject.put("strStationName", material.getStrStationName());
                jsonObject.put("nQty", material.getnQty());
                jsonObject.put("strPartNo", material.getStrPartNo());
                jsonObject.put("strReelID", material.getStrReelID());
                jsonArray.put(jsonObject);//生成一个json数据
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return String.valueOf(jsonArray);
    }


    @OnClick({R.id.back, R.id.iv_add, R.id.tv_order})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.back:
                getActivity().finish();
                break;
            //需要收料完成，才能继续扫描
            case R.id.iv_add:
               String result =  App.mCache.getAsString(Constant.danhao);
                if(result==null){
                    //扫描权限申请
                    requestCodeQRCodePermissions();
                }else {

                    ToastUtils.showShort("收料未结束，请先完成收料");
                }

                break;

            case R.id.tv_order:

                commitOrder();
                break;
        }
    }

    /**
     * //提交，返回备料单号
     *
     * 默认生成完订单后，不再刷新数据
     * 当收料结束后再可以刷新数据   ，以订单号为依据，有订单号不刷新，没有订单号可以刷新
     *
     * 1 生成订单后，把现在扫描的数据清空 ，以订单号判断是否显示数据，
     * 数据库中的数据要到收料完毕之后再清空
     * 2 收料结束后清除订单  ??
     *
     * 有一个提示用户选择的操作
     */
    private void commitOrder() {

        final String json = getDataJson();
        if (json == null) {
            ToastUtils.showShort("没有叫料数据");
            return;
        }
        final String line = mCache.getAsString(Constant.chanxian);
        if (CommonUtils.getLine() == null) {
            ToastUtils.showShort("请您先在设置中选择产线");
            return;
        }

        android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(getActivity());
        builder.setMessage("确定要进行催料操作吗?");
        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //生成叫料单操作
                createJiaoLiaoOrder(json,line);
            }
        });
        builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        builder.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });
        builder.create().show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        L.d("Fragment1", "onRequestPermissionsResult");
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


    private void jump() {
        Intent intent = new Intent(getActivity(), TestScanActivity.class);
        intent.putExtra(Constant.fragment1jump, "fragment1jump");
        startActivity(intent);
    }


    /**
     *
     * <p>
     * 传入数据源list,更新adapter,如果把数据源放入adapter中，更细数据容易不成功
     * 同时清除跳转标记
     */
    @Override
    public void onResume() {
        super.onResume();

        String result = App.mCache.getAsString(Constant.fgJumpFinish);
        if (result != null) {
            List<Material> list = DataSupport.findAll(Material.class);
            adapter.refresh(list);
            App.mCache.remove(Constant.fgJumpFinish);
            //通知收料fragment也刷新数据 ，保持数据的同步
            Intent intent = new Intent(Constant.refreshDataAction);
            intent.putExtra(Constant.refreshData,Constant.refreshData);
            LocalBroadcastManager.getInstance(getActivity())
                    .sendBroadcast(intent);
        }

    }

    /**
     * 生成叫料单后，清空数据 list.clear()
     *
     * @param json
     * @param line
     */
    private  void createJiaoLiaoOrder(String json,String line){

        OkHttpUtils.post()
                .url(App.IP_ADDRESS + "/AddCallList?")
                .addParams("strProductName", line)
                .addParams("strJsonList", json)
                .build()
                .execute(new StringCallback() {


                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);

                        dialogs = new LoadingDialogs(getActivity(),"提交中...");
                        dialogs.show();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                        ToastUtils.showShort(e.getMessage()+call.toString());
                        dialogs.close();
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        try {
                            //获取并保存单号
                            String result = XmlUtils.getJosn(response);
                            CreateMaterialBean bean = new Gson().fromJson(result, CreateMaterialBean.class);
                            String str = bean.getStrResult();
                            //判断是否为空字符串 即空单号
                            if (TextUtils.isEmpty(str)) {
                              ToastUtils.showShort("返回订单号为空");
                            }else {
                                ToastUtils.showShort("生成订单成功！");
                                List<Material> list = DataSupport.findAll(Material.class);
                                list.clear();
                                adapter.refresh(list);

                                App.mCache.put(Constant.danhao, str);
//                                String shouLiaoFinish =  App.mCache.getAsString(Constant.shouLiaoFinish);
//                                if(shouLiaoFinish!=null){
//                                    App.mCache.remove(Constant.shouLiaoFinish);
//                                }



                            }

                            dialogs.close();
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                    }
                });

    }



}
