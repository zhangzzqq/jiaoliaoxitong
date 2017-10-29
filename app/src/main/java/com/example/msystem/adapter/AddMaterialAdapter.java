package com.example.msystem.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.msystem.R;
import com.example.msystem.model.Material;

import org.litepal.crud.DataSupport;

import java.util.List;


/**
 * Created by stevenZhang on 2017/7/15.
 *
 * 有外界传入数据源
 */

public class AddMaterialAdapter extends RecyclerView.Adapter<AddMaterialAdapter.ViewHolder> {


   private List<Material> list;

    public AddMaterialAdapter(List<Material> list) {
        this.list = list;
    }

    public void refresh(List<Material> list){

        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_add_material, parent, false);
        return new ViewHolder(view);
    }

    //赋值
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        int size = list.size();
//        ToastUtils.showShort("size=="+size);
        final View view = holder.mView;
        String a0 = list.get(position).getStrStationName();
        String a1;
        //判断扫描后返回的结果是否为null
        StringBuffer stringBuffer = new StringBuffer(list.get(position).getStrReelID());
        if (stringBuffer.length() > 5) {
            a1 = stringBuffer.insert(5, "\r\n").toString();
        } else {
            a1 = list.get(position).getStrReelID();
        }
        String a2 = list.get(position).getStrPartNo();
        String a3 = list.get(position).getnQty();

        holder.tv_0.setText(a0);
        holder.tv_1.setText(a1);
        holder.tv_2.setText(a2);
        holder.tv_3.setText(a3);
        holder.tv_4.setText("删除");

        /**
         *
         * 然而RecyclerView自带notifyItemRemoved方法不仅可以实现删除功能,而且还有动画效果.
         * 然而并没有完,假如仅仅调用notifyItemRemoved的话,删除会出很多问题,
         * 比如:点击删除position = 1的Item,实际删除的是下一个,
         * 所以我们需要这么做,加上notifyItemRangeChanged这个方法,更新一下列表:
         */

        holder.tv_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //数据库中删除这条数据
                DataSupport.deleteAll(Material.class, "strReelID = ?", list.get(position).getStrReelID());
                //recyclerView 删除某条数据
                list.remove(position);
                notifyItemRemoved(position);
                notifyItemRangeChanged(position, getItemCount());
            }
        });

    }

    //有订单不刷新否则刷新
    @Override
    public int getItemCount() {

            return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        private final TextView tv_0;
        private final TextView tv_1;
        private final TextView tv_2;
        private final TextView tv_3;
        private final TextView tv_4;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            tv_0 = (TextView) view.findViewById(R.id.tv_0);
            tv_1 = (TextView) view.findViewById(R.id.tv_1);
            tv_2 = (TextView) view.findViewById(R.id.tv_2);
            tv_3 = (TextView) view.findViewById(R.id.tv_3);
            tv_4 = (TextView) view.findViewById(R.id.tv_4);
        }
    }
}