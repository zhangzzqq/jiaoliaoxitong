package com.example.msystem.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;

import com.example.msystem.R;
import com.example.msystem.adapter.AllLineAdapter;
import com.example.msystem.base.App;
import com.example.msystem.base.BaseActivity;
import com.example.msystem.model.ChaxunCuiLiao;
import com.example.msystem.model.Constant;
import com.example.msystem.utils.L;
import com.example.msystem.utils.ToastUtils;
import com.example.msystem.utils.XmlUtils;
import com.example.msystem.widget.LoadingDialogs;
import com.example.msystem.widget.MySwipeRefreshLayout;
import com.google.gson.Gson;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Request;

/**
 * Created by stevenZhang on 2017/8/9.
 */

public class AllLineCallMaterial extends BaseActivity implements SwipeRefreshLayout.OnRefreshListener {


    private LoadingDialogs dialogs;
    private RecyclerView mRecyclerView;
    private MySwipeRefreshLayout mSwipeRefreshLayout;
    private AllLineAdapter allLineAdapter;
    private ImageView ivBack;

    @Override
    public int getLayoutId() {

        return R.layout.activity_all_line;

    }

    @Override
    public void doMainUI() {

        initView();

        initData();
    }

    //
    private void initView() {

        ivBack = (ImageView) findViewById(R.id.back);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_total_line);
        //设置布局管理器
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mSwipeRefreshLayout = (MySwipeRefreshLayout) findViewById(R.id.swiperefresh);

        // 设置下拉圆圈上的颜色，蓝色、绿色、橙色、红色
        mSwipeRefreshLayout.setColorSchemeResources(
                android.R.color.holo_green_light,
                android.R.color.holo_blue_bright,
                android.R.color.holo_orange_light,
                android.R.color.holo_red_light);

        // 设置手指在屏幕下拉多少距离会触发下拉刷新
        mSwipeRefreshLayout.setDistanceToTriggerSync(300);
        // 设定下拉圆圈的背景
        mSwipeRefreshLayout.setProgressBackgroundColorSchemeColor(Color.WHITE);
        // 设定下拉圆圈的背景
        mSwipeRefreshLayout.setSize(SwipeRefreshLayout.DEFAULT); // 设置圆圈的大小
        //设置下拉刷新的监听
        mSwipeRefreshLayout.setOnRefreshListener(this);


    }


    private void initData() {

        //显示刷新
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                getDataFromServer();

            }
        });

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });


    }

    private void getDataFromServer() {

        OkHttpUtils.get().url(App.IP_ADDRESS+"/GetAllLineCallMaterial?").build().execute(new StringCallback() {

            @Override
            public void onBefore(Request request, int id) {
                super.onBefore(request, id);

                dialogs = new LoadingDialogs(mContext,"加载中...");
                dialogs.show();
            }


            @Override
            public void onError(Call call, Exception e, int id) {

                ToastUtils.showShort(e.getMessage());

                dialogs.close();
            }

            @Override
            public void onResponse(String response, int id) {

                try {

                    String result =  XmlUtils.getJosn(response);
                    JSONArray jsonArray = new JSONArray(result);
                    List <JSONObject>lists = new ArrayList();
                    for(int i=0;i<jsonArray.length();i++){

                        JSONObject jsonObject = new JSONObject();
                        jsonObject = (JSONObject) jsonArray.get(i);
                        lists.add(jsonObject);
                    }

                    allLineAdapter = new AllLineAdapter(lists);
                    mRecyclerView.setAdapter(allLineAdapter);


                    /**
                     * 催料回调，给一个提示对话框提示是否催料
                     *
                     * 如果是1 表示可以催料 ，如果状态对才能进行催料 否则不往下执行并给个提示
                     */
                    allLineAdapter.setOnItemClickLitener(new AllLineAdapter.OnItemClickLitener() {
                        @Override
                        public void onOperateClick(String  strResult, final String orderNo, final String line) {
                            if(!strResult.equals("1")){
                                ToastUtils.showShort("状态不对(测试版)");
                                return;
                            }
                            android.support.v7.app.AlertDialog.Builder builder = new android.support.v7.app.AlertDialog.Builder(mContext);
                            builder.setMessage("确定要进行催料操作吗?");
                            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //催料操作
                                    cuiLiao(orderNo,line);
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

                    });


                    /**
                     * 点击单条itemView回调
                     *
                     */

                    allLineAdapter.setOnViewClickLitener(new AllLineAdapter.OnItemViewClickLitener() {
                        @Override
                        public void onClick(int position, String orderNO) {

                            Intent intent = new Intent(mContext,AllLineDetails.class);
                            intent.putExtra(Constant.AllLineDanHao,orderNO);
                            startActivity(intent);

                        }
                    });


                } catch (XmlPullParserException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                dialogs.close();

                Boolean refresh =  mSwipeRefreshLayout.isRefreshing();
                //停止刷新
                if(refresh){
                    mSwipeRefreshLayout.setRefreshing(false);
                    L.v("SonPersonalFragment","setRefreshing(false)");
                }

            }

        });



    }


    private void cuiLiao(String orderNo,String line) {

        final LoadingDialogs dialogs = new LoadingDialogs(mContext, "加载中...");

        OkHttpUtils.post()
                .url(App.IP_ADDRESS + "/RemindCallMaterial?")
                .addParams("strOrderNo", orderNo)
                .addParams("strProductName", line)
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);

                        dialogs.show();
                    }

                    @Override
                    public void onError(Call call, Exception e, int id) {
                        ToastUtils.showShort(e.getMessage());
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


    @Override
    public void onRefresh() {

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                getDataFromServer();
            }
        },1000);
    }



}
