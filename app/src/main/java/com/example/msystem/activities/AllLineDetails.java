package com.example.msystem.activities;

import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;

import com.example.msystem.R;
import com.example.msystem.adapter.AllLineDetailsAdapter;
import com.example.msystem.base.BaseActivity;
import com.example.msystem.model.Constant;
import com.example.msystem.network.HttpUrl;
import com.example.msystem.utils.ToastUtils;
import com.example.msystem.utils.XmlUtils;
import com.example.msystem.widget.LoadingDialogs;
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

public class AllLineDetails extends BaseActivity {


    private LoadingDialogs dialogs;
    private RecyclerView recycler;
    private ImageView ivBack;

    @Override
    public int getLayoutId() {


        return R.layout.activity_all_line_details;

    }

    @Override
    public void doMainUI() {

        initView();

        initData();

    }


    private void initView() {

        ivBack = (ImageView) findViewById(R.id.back);
        recycler = (RecyclerView) findViewById(R.id.recycle_all_line_details);
        recycler.setLayoutManager(new LinearLayoutManager(mContext));//设置布局管理器

    }

    private void initData() {

        Intent intent = getIntent();
        if(intent.getStringExtra(Constant.AllLineDanHao)!=null){
           String allLinedanhao =  intent.getStringExtra(Constant.AllLineDanHao);
            showDetails(allLinedanhao);
        }

        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                finish();

            }
        });

    }


    private void showDetails(String allLinedanhao){

        OkHttpUtils.post()
                .url(HttpUrl.baseUrl+"/GetMaterialListByCallOrderNo?")
                .addParams("strOrderNo",allLinedanhao)
                .build()
                .execute(new StringCallback() {

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

                            String result = XmlUtils.getJosn(response);
                            List<JSONObject> list = new ArrayList<JSONObject>();
                            JSONArray jsonArray = new JSONArray(result);
                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject = new JSONObject();
                                jsonObject = (JSONObject) jsonArray.get(i);
                                list.add(jsonObject);
                            }

                            AllLineDetailsAdapter adapter = new AllLineDetailsAdapter(list);

                            recycler.setAdapter(adapter);


                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }catch (JSONException e) {
                            e.printStackTrace();
                        }

                        dialogs.close();

                    }
                });
    }

}
