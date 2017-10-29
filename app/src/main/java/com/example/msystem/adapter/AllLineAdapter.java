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

public class AllLineAdapter extends RecyclerView.Adapter<AllLineAdapter.ViewHolder> {

    private List<JSONObject> list;


    public AllLineAdapter(List<JSONObject> lists) {

        this.list = lists;

    }

    /**
     *
     * 点击催料 接口回调
     */
    public interface OnItemClickLitener
    {
        void onOperateClick (String  strResult,String orderNo,String line);

    }

    private OnItemClickLitener mOnItemClickLitener;

    public void setOnItemClickLitener(OnItemClickLitener mOnItemClickLitener)
    {
        this.mOnItemClickLitener = mOnItemClickLitener;
    }

    /**
     *
     * 点击单条itemview回调
     */
    public interface OnItemViewClickLitener
    {
        void onClick (int position,String orderNO);

    }

    private OnItemViewClickLitener mOnItemViewClickLitener;

    public void setOnViewClickLitener(OnItemViewClickLitener mOnItemViewClickLitener)
    {
        this.mOnItemViewClickLitener = mOnItemViewClickLitener;
    }




    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {


        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_all_line, parent, false);
        return new ViewHolder(view);
    }




    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

       final JSONObject jsonObject =  list.get(position);
        try {
           String strOrderNo = jsonObject.getString("strOrderNo"); //单号
           String strProductName = jsonObject.getString("strProductName");//产线
           String strStateName = jsonObject.getString("strStateName"); //状态名字
//           String strResult = jsonObject.getString("strResult"); //状态码
           holder.tv_0.setText(strProductName);
           holder.tv_1.setText(strOrderNo);
           holder.tv_2.setText(strStateName);
           holder.tv_3.setText("催料");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        /**
         * 需要
         * .addParams("strOrderNo", beiliaoOrder)
         .addParams("strProductName", line)
         */

        holder.tv_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    mOnItemClickLitener.onOperateClick(jsonObject.getString("strResult"),jsonObject.getString("strOrderNo"),jsonObject.getString("strProductName"));

                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });

        holder.mView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    mOnItemViewClickLitener.onClick(position,jsonObject.getString("strOrderNo"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


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
