package com.example.msystem.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.msystem.R;
import com.example.msystem.base.App;
import com.example.msystem.model.Constant;
import com.example.msystem.utils.ToastUtils;

import java.util.List;

/**
 * Created by stevenZhang on 2017/7/15.
 */

public class ChanXianAdapter extends RecyclerView.Adapter<ChanXianAdapter.ViewHolder> {


    private  List<String> list;
    public int selectedPos = -1;//默认值

    public ChanXianAdapter(List list) {

        this.list = list;

    }

    public void setSelectedPosition(int pos) {
        selectedPos = pos;
        notifyDataSetChanged();
    }

    //创建view
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chanxian, parent, false);
        return new ViewHolder(view);
    }

    //赋值
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final View view = holder.mView;
        holder.text.setText(list.get(position));

        if(selectedPos ==position){

            holder.iv.setVisibility(View.VISIBLE);
        }else {

            holder.iv.setVisibility(View.INVISIBLE);
        }

        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                /*
                * 用户选择产线的点击事件,1弹出提示，2并更改选择的钩
                *
                * 3同时记住用户选择的产线所在的position

                 */

                ToastUtils.showShort("您选择的产线是"+list.get(position));
                setSelectedPosition(position);
                App.mCache.put(Constant.chanxian,""+(position));
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    //id
    public static  class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;

        private  ImageView iv;
        private  TextView text;

        public ViewHolder(View view) {
            super(view);
            mView = view;

            iv = (ImageView) view.findViewById(R.id.iv_choose);
            text = (TextView) view.findViewById(R.id.tv_name);

        }
    }
}