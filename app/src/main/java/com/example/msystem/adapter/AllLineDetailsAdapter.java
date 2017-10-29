package com.example.msystem.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.msystem.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

/**
 * Created by stevenZhang on 2017/8/9.
 */

public class AllLineDetailsAdapter extends RecyclerView.Adapter<AllLineDetailsAdapter.ViewHolder> {

    private List<JSONObject> list;

    public AllLineDetailsAdapter(List<JSONObject> lists) {
        this.list = lists;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_line, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

       JSONObject jsonObject =  list.get(position);
        try {
           String strStationName = jsonObject.getString("strStationName"); //料站号
           String strReelID = jsonObject.getString("strReelID");//产reelId
           String strPartNo = jsonObject.getString("strPartNo"); //料号
           String strQty = jsonObject.getString("strQty"); //数量
            holder.tv_0.setText(strStationName);
            holder.tv_1.setText(strReelID);
            holder.tv_2.setText(strPartNo);
            holder.tv_3.setText(strQty);

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {

        return list.size();
    }


    class ViewHolder extends RecyclerView.ViewHolder{

        public final View mView;
        private final TextView tv_0;
        private final TextView tv_1;
        private final TextView tv_2;
        private final TextView tv_3;

        public ViewHolder(View view) {
            super(view);
            mView = view;
            tv_0 = (TextView) view.findViewById(R.id.tv_0);
            tv_1 = (TextView) view.findViewById(R.id.tv_1);
            tv_2 = (TextView) view.findViewById(R.id.tv_2);
            tv_3 = (TextView) view.findViewById(R.id.tv_3);
        }

    }


}
