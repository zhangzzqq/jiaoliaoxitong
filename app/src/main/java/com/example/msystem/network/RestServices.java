//package com.example.msystem.network;
//
//
//import com.example.msystem.model.SingleMaterial;
//import com.example.msystem.model.GetProductNameListBean;
//import com.example.msystem.model.ChaxunStatus;
//import com.example.msystem.model.ChaxunCuiLiao;
//import com.example.msystem.model.ReceiveMaterialBean;
//import com.example.msystem.model.RemindCallMaterialBean;
//
//import retrofit2.Call;
//import retrofit2.http.Field;
//import retrofit2.http.FormUrlEncoded;
//import retrofit2.http.GET;
//import retrofit2.http.POST;
//
///**
// * 1.  Service 接口  工程中所用到的请求后端的接口
// * 2.    RestPool 类  网络请求配置类
// */
//public interface RestServices {
//
////前两个是测试
////    @GET("/WebService")
////    Call<GridviewModule> getResult(@Query("action") String action, @Query("key") String key);
//
////    @FormUrlEncoded
////    @POST("/WebService")
////    Call<BaseBean<List<Fg1Product>>> getProductResult(@Field("action") String action, @Field("key") String key, @Field("cat_id")String cat_id, @Field("city_id")String city_id, @Field("flag")String flag);
//
//    // 提交料
//     @FormUrlEncoded
//    @POST("/AddCallList")
//    Call<SingleMaterial> getTiJiao(@Field("strProductName") String strProductName, @Field("strJsonList") String strJsonList);
//
//    //叫料
//    @FormUrlEncoded
//    @POST("/SingleMaterial")
//    Call<ChaxunStatus> getJiaoLiao(@Field("strReelID") String strReelID);
//
//    //收料
//    @FormUrlEncoded
//    @POST("/ReceiveMaterial")
//    Call<ReceiveMaterialBean> getReceiveMaterial(@Field("strReelID") String strReelID, @Field("strOrderNo") String strOrderNo);
//
//
//    //查询
//    @FormUrlEncoded
//    @POST("/QueryCallStatus")
//    Call<ChaxunCuiLiao> getChaXun(@Field("strOrderNo") String strOrderNo);
//
//   /* 催料
//   *
//   * 参数strProductName：线别
//   * */
//    @FormUrlEncoded
//    @POST("/RemindCallMaterial")
//    Call<RemindCallMaterialBean> getCuiLiao(@Field("strOrderNo") String strOrderNo,@Field("strProductName") String strProductName);
//
//
//    //用户登录
//    @FormUrlEncoded
//    @POST("/UserLgoin")
//    Call<String> getUserLogin(@Field("strUserName") String strUserName, @Field("strPwd") String strPwd);
//
//
//   /* 设置
//      获取线别列表
//    */
//    @GET("/GetProductNameList")
//    Call<GetProductNameListBean> getProductNameList();
//
//
//}
//
