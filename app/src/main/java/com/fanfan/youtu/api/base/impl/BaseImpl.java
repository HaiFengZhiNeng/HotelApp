package com.fanfan.youtu.api.base.impl;

import android.content.Context;
import android.support.annotation.NonNull;

import com.fanfan.youtu.api.base.Constant;
import com.fanfan.youtu.token.YoutuSign;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.util.concurrent.TimeUnit;

import okhttp3.Authenticator;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.Route;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.GsonConverterFactory;
import retrofit2.Retrofit;

/**
 * Created by android on 2017/12/21.
 */

public class BaseImpl<Service> {

    private static Retrofit mRetrofit;
    protected Service mService;

    public BaseImpl(@NonNull Context context) {

        initRetrofit();
        this.mService = mRetrofit.create(getServiceClass());
    }


    private void initRetrofit() {
        if (null != mRetrofit)
            return;

        YoutuSign.init();

        // 设置 Log 拦截器，可以用于以后处理一些异常情况
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        // 为所有请求自动添加 token
        Interceptor mTokenInterceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                // 如果当前没有缓存 token 或者请求已经附带 token 了，就不再添加
                String accessToken = YoutuSign.getSingleInstance().getAccessToken();
                Request authorised = originalRequest.newBuilder()
                        .header("Authorization", accessToken)
                        .build();
                return chain.proceed(authorised);
            }
        };

        // 自动刷新 token
        Authenticator mAuthenticator = new Authenticator() {
            @Override
            public Request authenticate(Route route, Response response) {
                String accessToken = YoutuSign.getSingleInstance().getAccessToken();
                return response.request().newBuilder()
                        .header("Authorization", accessToken)
                        .build();
            }
        };

        // 配置 client
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(interceptor)                // 设置拦截器
                .retryOnConnectionFailure(true)             // 是否重试
                .connectTimeout(5, TimeUnit.SECONDS)        // 连接超时事件
                .readTimeout(5, TimeUnit.SECONDS)           // 读取超时时间
                .addNetworkInterceptor(mTokenInterceptor)   // 自动附加 token
//                .authenticator(mAuthenticator)              // 认证失败自动刷新token
                .build();

        // 配置 Retrofit
        mRetrofit = new Retrofit.Builder()
                .baseUrl(Constant.API_YOUTU_BASE)                         // 设置 base url
                .client(client)                                     // 设置 client
                .addConverterFactory(GsonConverterFactory.create()) // 设置 Json 转换工具
                .build();
    }

    private Class<Service> getServiceClass() {
        return (Class<Service>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

}
