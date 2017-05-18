package com.howshea.artisanmusic.network;

import com.howshea.artisanmusic.model.HomePage;

import retrofit2.http.GET;
import retrofit2.http.Path;
import rx.Observable;

/**
 * Created by haipo
 * on 2016/10/28.
 *
 */

interface ApiService {
    @GET("?w=720&h=1280")
    Observable<HomePage> getHome();
}
