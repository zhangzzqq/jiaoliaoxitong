package com.example.msystem.adapter;


import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.msystem.R;


/**
 * Created by stevenZhang on 2017/7/15.
 */

public class QueryAdapter extends RecyclerView.Adapter<QueryAdapter.ViewHolder> {


    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_chaxun, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        final View view = holder.mView;
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static  class ViewHolder extends RecyclerView.ViewHolder {

        public final View mView;
//        private final TextView form_tv1;
//        private final TextView form_tv2;
//        private final TextView tvRanking;
//        private final TextView tvSchoolName;
//        private final ImageView ivSchoolSigin;
//        private final TextView form_tv3;
//        private final TextView tvDifference;

        public ViewHolder(View view) {
            super(view);
            mView = view;
//            form_tv1 = (TextView) view.findViewById(R.id.form_tv1);
//            form_tv2 = (TextView) view.findViewById(R.id.form_tv2);
//            tvRanking = (TextView) view.findViewById(R.id.tv_ranking);
//            tvSchoolName = (TextView) view.findViewById(R.id.tv_school_name);
//            ivSchoolSigin = (ImageView) view.findViewById(R.id.iv_school_sigin);
//            form_tv3 = (TextView) view.findViewById(R.id.form_tv3);
//            tvDifference = (TextView) view.findViewById(R.id.tv_difference);

        }
    }
}