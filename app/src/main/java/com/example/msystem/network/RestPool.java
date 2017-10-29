//package com.example.msystem.network;
//
//import retrofit2.Retrofit;
//import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
//
///**
// * Created by stevenzhang on 2016/5/30 0030.
// */
//public class RestPool  {
//
//
//
//    public static final String BASE_URL = HttpUrl.baseUrl;
//    private static  Retrofit retrofit;
//
//    //获取实例
//    public static RestPool getPool (){
//
//        return  PoolHolder.restPool;
//    };
//
//    static class PoolHolder{
//
//        private static RestPool restPool = new RestPool();
//    }
//
//    private RestServices services;
//
//    private RestPool(){
//        //生产retrofit对象
//        retrofit = new Retrofit.Builder()
//                .baseUrl(BASE_URL)
//                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
////                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        services = retrofit.create(RestServices.class);
//
//
//    }
//
//    //获取RestService实例
//    public RestServices getServices (){
//        return  services;
//    }
//
//
//    //修改增加一个方法
//
//    public <T> T create (Class<T> clazz){
//
//        return retrofit.create(clazz);
//    }
//
//
//
//}
