package com.fj.aux.network.api;

import com.fj.aux.network.entity.config.Config;

import io.reactivex.Observable;
import retrofit2.http.GET;

/**
 * Created by Stardust on 2017/10/26.
 */

public interface ConfigApi {

    @GET("/api/config")
    Observable<Config> getConfig();

}
