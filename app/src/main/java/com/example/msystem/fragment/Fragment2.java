package com.example.msystem.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.msystem.R;
import com.example.msystem.base.App;
import com.example.msystem.base.BaseFragment;
import com.example.msystem.model.ChaxunCuiLiao;
import com.example.msystem.model.ChaxunStatus;
import com.example.msystem.model.Constant;
import com.example.msystem.utils.ToastUtils;
import com.example.msystem.utils.XmlUtils;
import com.example.msystem.widget.LoadingDialogs;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import okhttp3.Call;
import okhttp3.Request;

import static com.example.msystem.base.App.mCache;

/**
 * Created by stevenZhang on 2017/7/14.
 */

public class Fragment2 extends BaseFragment {


    TextView tvNumber;
    TextView tvStatus;
    Button btnCuiLiao;

    private View v;
    private String beiliaoOrder;
    private LoadingDialogs dialogs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        if (v == null) {
            v = inflater.inflate(R.layout.fragment_home2, null);
            ButterKnife.bind(this, v);
            initView();
            initData();
        }

        return v;

    }

    /**
     * 从文件中读取单号并显示
     *  有单号进行查询
     *  没有单号，不查询,在这种情况下，如果已经收料完毕，仍可以查询状态，或者手动写
     *
     */
    @Override
    public void onResume() {
        super.onResume();

        beiliaoOrder = mCache.getAsString(Constant.danhao);
        if (beiliaoOrder == null) {
            tvNumber.setText("目前没有叫料单！");
//            String str = App.mCache.getAsString(Constant.shouLiaoFinish);
//            if(str!=null){
//                tvStatus.setText("收料完成");
//            }
            tvStatus.setText("等待中");
            return;//不执行状态的查询
        } else {
            tvNumber.setText(beiliaoOrder);
        }

        chaKanStatus();


    }

    private void chaKanStatus() {


        OkHttpUtils.post()
                .url(App.IP_ADDRESS + "/QueryCallStatus?")
                .addParams("strOrderNo", beiliaoOrder)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);
                        dialogs = new LoadingDialogs(getContext(), "查询状态中...");
                        dialogs.show();


                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {

                        ToastUtils.showShort(e.getMessage()+call.toString());
                        dialogs.close();
                    }

                    @Override
                    public void onResponse(String response, int id) {

                        //解析json
                        try {
                            String result = XmlUtils.getJosn(response);

                            ChaxunStatus chaxunStatus = new Gson().fromJson(result, ChaxunStatus.class);

                            String strResult = chaxunStatus.getStrResult();

                            tvStatus.setText(strResult);
                            dialogs.close();

                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }


    private void initView() {

//         btnCuiLiao = (Button) v.findViewById(R.id.btn_cuiliao);
         tvNumber = (TextView) v.findViewById(R.id.tv_number);
         tvStatus = (TextView) v.findViewById(R.id.tv_status);

        //催料点击事件
        v.findViewById(R.id.btn_cuiliao).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cuiLiao();
            }

        });

    }

    //请求后台，催料
    private void cuiLiao() {

        //备料单号是null,则无法去查询
        if (beiliaoOrder == null) {

            ToastUtils.showShort("没有叫料单号");
            return;
        }
        String line = mCache.getAsString(Constant.chanxian);
        if (line == null) {
            ToastUtils.showShort("没有选择产线");
            return;
        }

        OkHttpUtils.post()
                .url(App.IP_ADDRESS + "/RemindCallMaterial?")
                .addParams("strOrderNo", beiliaoOrder)
                .addParams("strProductName", line)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);

                        dialogs = new LoadingDialogs(getContext(), "催料中...");
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
                            //0 失败 1成功
                            String result = XmlUtils.getJosn(response);
                            ChaxunCuiLiao chaxunCuiLiao = new Gson().fromJson(result, ChaxunCuiLiao.class);

                            if (TextUtils.equals(chaxunCuiLiao.getStrResult(), "1")) {

                                ToastUtils.showShort("催料成功！");

                            } else {

                                ToastUtils.showShort("催料失败！");
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


    private void initData() {


        //添加数据

        List<String> list = new ArrayList<>();

//        list.add();

//        //查询
//        services.getChaXun("").enqueue(new Callback<ChaxunCuiLiao>() {
//            @Override
//            public void onResponse(Call<ChaxunCuiLiao> call, Response<ChaxunCuiLiao> response) {
//
//                if(response.isSuccessful()){
//
////                    BaseBean<List<OrderBean>> baseBean1 = response.body();
//
//                    response.body();
//
//
//
//                }
//            }
//
//            @Override
//            public void onFailure(Call<ChaxunCuiLiao> call, Throwable t) {
//
//            }
//        });


    }


}
