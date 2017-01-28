package com.akiniyalocts.files.dagger;

import android.content.Context;

import com.akiniyalocts.files.BuildConfig;
import com.akiniyalocts.files.FilesApp;
import com.akiniyalocts.files.api.FileIOApi;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.squareup.picasso.Picasso;

import java.util.concurrent.Executors;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Created by anthonykiniyalocts on 1/28/17.
 */

@Module
public class DataModule {

    private final FilesApp app;

    public DataModule(FilesApp app) {
        this.app = app;
    }

    @Singleton
    @Provides
    OkHttpClient provideOkhttpClient(){
        OkHttpClient.Builder builder = new OkHttpClient.Builder();

        if (BuildConfig.DEBUG) {
            HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
            interceptor.setLevel(HttpLoggingInterceptor.Level.HEADERS);

            builder.interceptors().add(interceptor);
        }

        return builder.build();
    }

    @Singleton
    @Provides
    Gson gson(){
        return new GsonBuilder().create();
    }

    @Singleton
    @Provides
    GsonConverterFactory gsonConverterFactory(Gson gson){
        return GsonConverterFactory.create(gson);
    }

    @Singleton
    @Provides
    RxJavaCallAdapterFactory rxJavaCallAdapterFactory(Scheduler scheduler){
        return RxJavaCallAdapterFactory.createWithScheduler(scheduler);
    }

    @Singleton
    @Provides
    Scheduler provideScheduler(){
        return Schedulers.from(Executors.newFixedThreadPool(6));
    }

    @Singleton
    @Provides
    Picasso picasso(Context context){
        return new Picasso.Builder(context)
                .build();
    }

    @Singleton
    @Provides
    FileIOApi api(OkHttpClient client, GsonConverterFactory gsonConverterFactory, RxJavaCallAdapterFactory rxJavaCallAdapterFactory){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(FileIOApi.base)
                .addCallAdapterFactory(rxJavaCallAdapterFactory)
                .addConverterFactory(gsonConverterFactory)
                .client(client)
                .build();

        return retrofit.create(FileIOApi.class);
    }

}
