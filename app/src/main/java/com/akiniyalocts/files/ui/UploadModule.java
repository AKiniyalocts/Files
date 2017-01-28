package com.akiniyalocts.files.ui;

import dagger.Module;

/**
 * Created by anthonykiniyalocts on 1/28/17.
 */
@Module
public class UploadModule {

    private final UploadActivity uploadActivity;

    public UploadModule(UploadActivity uploadActivity) {
        this.uploadActivity = uploadActivity;
    }

}
