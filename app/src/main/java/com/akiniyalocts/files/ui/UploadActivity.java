package com.akiniyalocts.files.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

import com.akiniyalocts.files.FilesApp;
import com.akiniyalocts.files.R;
import com.akiniyalocts.files.base.HasComponent;
import com.akiniyalocts.files.databinding.UploadBinding;
import com.akiniyalocts.files.service.UploadService;
import com.pavlospt.rxfile.RxFile;
import com.squareup.picasso.Picasso;

import java.io.File;

import javax.inject.Inject;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class UploadActivity extends AppCompatActivity implements HasComponent<UploadComponent>, UploadView{

    private final static String[] perms = {Manifest.permission.WRITE_EXTERNAL_STORAGE};

    private final static int REQUEST_STORAGE = 1;

    private UploadBinding binding;

    @Inject
    Picasso picasso;

    private File file;

    @Override
    public UploadComponent getComponent() {
        return DaggerUploadComponent.builder()
                .applicationComponent(((FilesApp)getApplication()).getComponent())
                .build();
    }

    @Override
    public void injectComponent() {
        getComponent().inject(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        injectComponent();


        binding = DataBindingUtil.setContentView(this, R.layout.activity_upload);
        init();
        checkPermissions(getIntent());
    }

    @Override
    public void init() {
        binding.close.setOnClickListener(v -> UploadActivity.this.finish());

        binding.send.setOnClickListener(v -> {
            if(file != null) {
                startService(UploadService.buildIntent(UploadActivity.this, UploadActivity.this.getIntent().getData(), UploadActivity.this.getIntent().getClipData()));
                UploadActivity.this.finish();
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    @AfterPermissionGranted(REQUEST_STORAGE)
    private void checkPermissions(Intent intent){
        if(!EasyPermissions.hasPermissions(this, perms)){
            EasyPermissions.requestPermissions(this, getString(R.string.rationale), REQUEST_STORAGE, perms);
        } else {
            evaluateIntent(intent);
        }
    }


    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    private void evaluateIntent(Intent intent) {
        if(intent != null){

            if(intent.getClipData() != null){

                RxFile.createFilesFromClipData(this, intent.getClipData())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                files -> {

                                    if(files != null && !files.isEmpty()){

                                        file = files.get(0);

                                        binding.fileName.setText(file.getName());

                                        picasso.load(file)
                                                .fit()
                                                .error(R.drawable.ic_folder_black_24dp)
                                                .centerCrop()
                                                .into(binding.previewImage);
                                    }
                                },
                                throwable -> {}
                        );
            }
        }
    }


}
