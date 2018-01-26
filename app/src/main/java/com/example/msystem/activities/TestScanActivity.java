package com.example.msystem.activities;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.msystem.R;
import com.example.msystem.base.App;
import com.example.msystem.model.Constant;
import com.example.msystem.model.Material;
import com.example.msystem.model.ReceiveMaterialBean;
import com.example.msystem.model.SingleMaterial;
import com.example.msystem.utils.L;
import com.example.msystem.utils.ToastUtils;
import com.example.msystem.utils.XmlUtils;
import com.example.msystem.widget.LoadingDialogs;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.litepal.crud.DataSupport;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.List;

import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zbar.ZBarView;
import okhttp3.Call;
import okhttp3.Request;

import static com.example.msystem.model.Constant.danhao;


public class TestScanActivity extends AppCompatActivity implements QRCodeView.Delegate {

    private static final String TAG = TestScanActivity.class.getSimpleName();
    private static final int REQUEST_CODE_CHOOSE_QRCODE_FROM_GALLERY = 666;

    private QRCodeView mQRCodeView;
    private LoadingDialogs dialogs;
    private Intent intent;
    private ImageView ivBack;
    private TextView tvFinish;
    private String beiLiaoOrder;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_scan);

        //让顶部的状态栏为透明状态，好让布局可以顶上去
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window window = getWindow();
            // Translucent status bar
            window.setFlags(
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS,
                    WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }

        initView();
        initData();
    }

    private void initView() {

        mQRCodeView = (ZBarView) findViewById(R.id.zbarview);
        mQRCodeView.setDelegate(this);
        ivBack = (ImageView) findViewById(R.id.back);
        tvFinish = (TextView) findViewById(R.id.tv_finish);


    }


    private void initData() {

        intent = getIntent();
        beiLiaoOrder = intent.getStringExtra(Constant.beiLiaoDanHao);

        //结束当前页面
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finshActivity();
            }
        });

        /**
         * 完成操作，跟上面方法功能类似，都是需要返回
         *
         * fg1Jump 是表示fragment1页面跳转过来的
         *
         */
        tvFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                    finshActivity();

            }
        });
    }


    private void finshActivity(){
        App.mCache.put(Constant.fgJumpFinish,Constant.fgJumpFinish);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mQRCodeView.startCamera();
//        mQRCodeView.startCamera(Camera.CameraInfo.CAMERA_FACING_FRONT);

        mQRCodeView.showScanRect();
        //进行检测扫描
        mQRCodeView.startSpot();
    }

    @Override
    protected void onStop() {
        mQRCodeView.stopCamera();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mQRCodeView.onDestroy();
        if(dialogs!=null){
            dialogs.close();
        }
        super.onDestroy();
    }

    //震动
    private void vibrate() {
        Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        vibrator.vibrate(200);
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        L.i(TAG, "result:" + result);

        vibrate();

        /*
            两个业务逻辑，都是需要扫描
           叫料操作 submitServer  与后台交互提交数据
           收料操作 confirmServer  收料与后台进行确认比对
         */

        if(beiLiaoOrder!=null){

            confirmServer(result);
        }else {

            submitServer(result);
        }

    }

    //叫料操作
    private void submitServer(String str) {

        //通过扫描的结果获取后台对应的信息
                OkHttpUtils
                .post()
                .url(App.IP_ADDRESS+"/QueryNextMaterial?")
                .addParams("strReelID",str)
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);

                        dialogs = new LoadingDialogs(TestScanActivity.this,"数据处理中");
                        dialogs.show();

                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                        ToastUtils.showShort("扫描出现错误:"+e.getMessage());

                        dialogs.close();
                        //重新进行检测扫描
                        mQRCodeView.startSpot();
                    }

                    /*
                            两种情况
                            返回数据正常：存入数据库，继续扫描
                            返回数据错误：进行提示
                            返回数据为空  不存入数据库，继续进行扫描
                            额外的操作  等待对话框dialog和继续扫描
                            最后操作 结束当前界面（项目要求）

                     */
                    @Override
                    public void onResponse(String response, int id) {

                        try {

                            String json = XmlUtils.getJosn(response);
                            SingleMaterial singleMaterial =  new Gson().fromJson(json, SingleMaterial.class);
                            //如果json数据 Returns true if the string is null or 0-length.
                            if(TextUtils.isEmpty(json)||singleMaterial.getStrStationName()==null){
                                ToastUtils.showShort("返回数据为空");
                            }

                            if(singleMaterial.getStrResult().equals("0")){
                                ToastUtils.showShort("返回数据失败，请重新扫描");
                            }else {
                                //存入数据库
                                Material material = new Material();
                                material.setnID(singleMaterial.getStrID());
                                material.setStrStationName(singleMaterial.getStrStationName());//料站号
                                material.setStrReelID(singleMaterial.getStrReelID());//ReelID
                                material.setStrPartNo(singleMaterial.getStrPartNo());//料号
                                material.setnQty(singleMaterial.getStrQty());//数量
                                material.save();//保存这次的扫描数据
                                ToastUtils.showShort("加入成功");
                                L.i(TAG, "result==" + json);
                                List<Material> list = DataSupport.findAll(Material.class);
//                                ToastUtils.showShort("size0=="+list.size());
                                finshActivity();
                            }

                            if(dialogs!=null){
                                dialogs.close();
                            }
                            //重新进行检测扫描
                            mQRCodeView.startSpot();
                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    /*
        收料操作
        两种情况，params参数不正确，拒绝进行请求
        参数正确可以请求
        额外操作 重新扫描
     */

    private void confirmServer(final String reelId) {

       String beiLiaoDanHao =  App.mCache.getAsString(danhao);//fragment1中生成的单号
        if(TextUtils.isEmpty(beiLiaoDanHao)){
            ToastUtils.showShort("没有生成叫料单号");
            //重新进行检测扫描
            mQRCodeView.startSpot();
        }else {
            //通过扫描的结果与后台进行信息的比对
            OkHttpUtils
                    .post()
                    .url(App.IP_ADDRESS+"/ReceiveMaterial?")
                    .addParams("strReelID",reelId)
                    .addParams("strOrderNo",beiLiaoDanHao)
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onBefore(Request request, int id) {
                            super.onBefore(request, id);
                            if(dialogs==null){
                                dialogs = new LoadingDialogs(TestScanActivity.this,"数据处理中");
                            }
                            dialogs.show();
                        }

                        @Override
                        public void onError(Call call, Exception e, int id) {
                            ToastUtils.showShort("扫描出现错误:"+e.getMessage());
                            if(dialogs!=null){
                                dialogs.close();
                            }
                            //重新进行检测扫描
                            mQRCodeView.startSpot();
                        }

                        /*
                            两种情况
                            返回数据正常：更新数据库(出现问题？)，执行更新确认状态操作 ,继续扫描
                            返回数据为空  不存入数据库，继续进行扫描
                            额外的操作  等待对话框dialog和继续扫描
                       */
                        @Override
                        public void onResponse(String response, int id) {
                            try {
                                String json = XmlUtils.getJosn(response);
                                if(TextUtils.isEmpty(json)){
                                    ToastUtils.showShort("返回确认数据为空");
                                }else {
                                    ReceiveMaterialBean receiveMaterialBean = new Gson().fromJson(json,ReceiveMaterialBean.class);
                                    String status = receiveMaterialBean.getStrResult();
                                    //0 没有被后台确认，1已经被后台确认 reelId
                                    Material material = new Material();
                                    material.setStatus(status);
                                    material.updateAll("strReelID = ?", reelId);
                                    finshActivity();
                                }
                                if(dialogs!=null){
                                    dialogs.close();
                                }
                                //重新进行检测扫描
                                mQRCodeView.startSpot();
                            } catch (XmlPullParserException e) {
                                e.printStackTrace();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }

                        }
                    });
        }


    }

    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }




}