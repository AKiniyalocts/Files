package com.akiniyalocts.files;

import android.app.Application;

import com.akiniyalocts.files.base.HasComponent;
import com.akiniyalocts.files.dagger.AndroidModule;
import com.akiniyalocts.files.dagger.ApplicationComponent;
import com.akiniyalocts.files.dagger.DaggerApplicationComponent;
import com.akiniyalocts.files.dagger.DataModule;

import io.realm.Realm;
import io.realm.RealmConfiguration;

/**
 * Created by anthonykiniyalocts on 1/28/17.
 */

public class FilesApp extends Application implements HasComponent<ApplicationComponent>{

    @Override
    public void onCreate() {
        super.onCreate();

        injectComponent();

        initRealm();
    }

    private void initRealm() {
        Realm.init(this);
        RealmConfiguration config = new RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .build();
        Realm.setDefaultConfiguration(config);

    }


    @Override
    public ApplicationComponent getComponent() {
        return DaggerApplicationComponent.builder()
                .androidModule(new AndroidModule(this))
                .dataModule(new DataModule(this))
                .build();
    }

    @Override
    public void injectComponent() {
        getComponent().inject(this);
    }
}
