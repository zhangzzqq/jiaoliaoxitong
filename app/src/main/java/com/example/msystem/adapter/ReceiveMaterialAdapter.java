package com.example.msystem.adapter;


import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.msystem.R;
import com.example.msystem.base.App;
import com.example.msystem.model.Material;
import com.example.msystem.utils.ToastUtils;

import java.util.List;


/**
 * Created by stevenZhang on 2017/7/15.
 */

public class ReceiveMaterialAdapter extends RecyclerView.Adapter<ReceiveMaterialAdapter.ViewHolder> {

    public List<Material> list ;

    public ReceiveMaterialAdapter(List<Material> list) {
        this.list = list;
    }

    public void refresh(List<Material> list){
        this.list = list;
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_receive_material, parent, false);
        return new ViewHolder(view);
    }

    //赋值
    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {

        final View view = holder.mView;
        //先判断数据库中的数据值是否为null，否则返回不进行下一步
        String a0 =  list.get(position).getStrStationName();
        if(TextUtils.isEmpty(a0)){
            ToastUtils.showShort("数据为空");
            return;
        }
        //对reelid值进行检测
        String a1;
        StringBuffer stringBuffer = new StringBuffer(list.get(position).getStrReelID());
        if(stringBuffer.length()>5){
             a1 = stringBuffer.insert(5,"\r\n").toString();//加入换行符
        }else {
            a1= list.get(position).getStrReelID(); //
        }
        String a2 =  list.get(position).getStrPartNo();
        String a3 = list.get(position).getnQty();
        String a4 = list.get(position).getStatus(); //状态 0 没有确认 1已经被后台确认

        holder.tv_0.setText(a0);
        holder.tv_1.setText(a1);
        holder.tv_2.setText(a2);
        holder.tv_3.setText(a3);
        if(TextUtils.equals(a4,"1")){
            holder.iv_4.setImageBitmap(BitmapFactory.decodeResource(App.getContext().getResources(),R.mipmap.confirm));
        }else {
            holder.iv_4.setImageBitmap(BitmapFactory.decodeResource(App.getContext().getResources(),R.mipmap.no_confirm));
        }

    }

    @Override
    public int getItemCount() {

        return list==null? 0: list.size();
    }

    public static  class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
        private final TextView tv_0;
        private final TextView tv_1;
        private final TextView tv_2;
        private final TextView tv_3;
        private final ImageView iv_4;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            tv_0 = (TextView) view.findViewById(R.id.tv_0);
            tv_1 = (TextView) view.findViewById(R.id.tv_1);
            tv_2 = (TextView) view.findViewById(R.id.tv_2);
            tv_3 = (TextView) view.findViewById(R.id.tv_3);
            iv_4 = (ImageView) view.findViewById(R.id.iv_4);


        }
    }

   /**
       收料是否结束判断
     *  如果status是0则没有收料完，返回false，否则返回true
     *  需要加入是否为null的判断
     */

    public  Boolean isFinish(){

        for(int i=0;i<list.size();i++){
           String a =  list.get(i).getStatus();
            if(a!=null&&a.equals("0")){
                return false;
            }
        }

        return true;
    }
}