package com.akiniyalocts.files.dagger;

import android.app.Application;
import android.app.IntentService;
import android.content.Context;

import com.akiniyalocts.files.api.FileIOApi;
import com.google.gson.Gson;
import com.squareup.picasso.Picasso;

import javax.inject.Singleton;

import dagger.Component;
import okhttp3.OkHttpClient;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by anthonykiniyalocts on 1/28/17.
 */

@Singleton
@Component(modules = {DataModule.class, AndroidModule.class})
public interface ApplicationComponent {

    OkHttpClient client();

    Application application();

    Context context();

    Gson gson();

    Picasso picasso();

    RxJavaCallAdapterFactory rxJavaCallAdapterFactory();

    GsonConverterFactory gsonConverterFactory();

    FileIOApi api();

    void inject(Application app);

    void inject(IntentService intentService);
}
