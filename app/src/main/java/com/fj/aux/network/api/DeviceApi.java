package com.fj.aux.network.api;

import java.util.Map;

import io.reactivex.Observable;
import okhttp3.ResponseBody;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.HeaderMap;
import retrofit2.http.POST;

/**
 * 设备接口
 */
public interface DeviceApi {

    @FormUrlEncoded
    @POST("api/verify")
    Observable<ResponseBody> verify(@Field("deviceKey") String deviceKey, @Field("ts") String ts, @Field("sign") String sign);
}
