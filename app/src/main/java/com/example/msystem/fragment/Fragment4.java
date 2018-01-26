package com.example.msystem.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.msystem.R;
import com.example.msystem.adapter.ChanXianAdapter;
import com.example.msystem.base.App;
import com.example.msystem.model.Constant;
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
 * Created by stevenZhang on 2017/7/14.
 */

public class Fragment4 extends Fragment {

    private View v;
    private RecyclerView recycler;
    private LoadingDialogs dialogs;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        if (v == null){
            v = inflater.inflate(R.layout.fragment_home4,null);
        }

        initView();
        initData();

        return v;
    }


    private void initView() {

        recycler = (RecyclerView) v.findViewById(R.id.recycler_chanxian);
     /*
        设置布局管理器，相当于ListView,不设置会报错出现No layout manager attached; skipping layout错误
      */

        recycler.setLayoutManager(new LinearLayoutManager(getContext()));

    }


    private void initData() {


//        List<String> list = getData();
//
//        ChanXianAdapter adapter  =  new ChanXianAdapter(list);
//        //显示文件中保存的产线记录
//        String chanxian =  App.mCache.getAsString(Constant.chanxian);
//        if(chanxian!=null){
//            adapter.selectedPos = Integer.parseInt(chanxian);
//        }
//        recycler.setAdapter(adapter);


        OkHttpUtils.get()
                .url(App.IP_ADDRESS+"/GetProductNameList?")
                .build()
                .execute(new StringCallback() {

                    @Override
                    public void onBefore(Request request, int id) {
                        super.onBefore(request, id);

                        dialogs = new LoadingDialogs(getContext(),"加载中...");
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
                            String str = XmlUtils.getJosn(response);
                            //添加数据源
                            List <String> list = new ArrayList();
                            JSONArray jsonArray = new JSONArray(str);

                            for(int i=0;i<jsonArray.length();i++){
                                JSONObject jsonObject = new JSONObject();
                                jsonObject = (JSONObject) jsonArray.get(i);
                               list.add((String)jsonObject.get("strResult")); ;
                            }
                            ChanXianAdapter adapter  =  new ChanXianAdapter(list);
                            //显示文件中保存的产线记录
                            String chanxian =  App.mCache.getAsString(Constant.chanxian);
                            if(chanxian!=null){
                                adapter.selectedPos = Integer.parseInt(chanxian);
                            }
                            recycler.setAdapter(adapter);

                        } catch (XmlPullParserException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        dialogs.close();

                    }
                });


    }

    private List getData() {

        List list = new ArrayList();
        for (int i=1;i<15;i++){
            list.add("Line"+i);
        }
        return list;


    }


}
