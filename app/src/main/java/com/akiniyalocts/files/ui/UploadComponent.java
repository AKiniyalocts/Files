package com.akiniyalocts.files.ui;

import com.akiniyalocts.files.base.ActivityScope;
import com.akiniyalocts.files.dagger.ApplicationComponent;

import dagger.Component;

/**
 * Created by anthonykiniyalocts on 1/28/17.
 */
@ActivityScope
@Component(dependencies = ApplicationComponent.class, modules = UploadModule.class)
public interface UploadComponent {

    void inject(UploadActivity uploadActivity);
}
