package com.route.apis;




import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;


public class ServiceFactory {


    private static ServiceAPIs sServiceAPIs;

    public static ServiceAPIs getServiceAPIs() {
        if(sServiceAPIs == null) {
            sServiceAPIs = createServiceAPIs();
        }
        return sServiceAPIs;
    }

    private ServiceFactory() {
    }


    private static ServiceAPIs createServiceAPIs() {
        final Retrofit retrofit = createRetrofit();
        return retrofit.create(ServiceAPIs.class);
    }

    /**
     * This creates OKHttpClient
     */
    private static OkHttpClient createOkHttpClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        final OkHttpClient.Builder httpClient =
                new OkHttpClient.Builder();
        httpClient.addInterceptor(interceptor).build();
        httpClient.readTimeout(40, TimeUnit.SECONDS);
        httpClient.connectTimeout(40, TimeUnit.SECONDS);
        return httpClient.build();
    }

    /**
     * Creates a pre configured Retrofit instance
     */
    private static Retrofit createRetrofit() {
        return new Retrofit.Builder()
                .baseUrl(ApiPathUtil.CONNECTION_HOST)
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .addConverterFactory(GsonConverterFactory.create())
                .client(createOkHttpClient())
                .build();
    }
}
