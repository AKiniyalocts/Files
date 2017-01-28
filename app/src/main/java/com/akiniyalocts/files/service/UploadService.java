package com.akiniyalocts.files.service;

import android.annotation.TargetApi;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.ContextCompat;
import android.widget.Toast;

import com.akiniyalocts.files.FilesApp;
import com.akiniyalocts.files.R;
import com.akiniyalocts.files.api.FileIOApi;
import com.akiniyalocts.files.model.Link;
import com.google.gson.Gson;
import com.pavlospt.rxfile.RxFile;

import java.io.File;
import java.io.IOException;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class UploadService extends IntentService implements NotificationView{

    private final static int NOTIFICATION_ID = 10;

    private final static int REQUEST_CODE_CANCEL = 9;

    private final static int REQUEST_CODE_SHARE = 8;

    private final static String EXTRA_CLIPBOARD = "extra::clipboard";

    private final static String EXTRA_CANCEL = "extra::cancel";

    /**
     *
     * @param data
     * @param clipData
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public static Intent buildIntent(Context context, Uri data, ClipData clipData) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setData(data);
        intent.setClipData(clipData);
        return intent;
    }

    public static Intent buildCancelIntent(Context context){
        Intent intent =  new Intent(context, UploadService.class);
        intent.putExtra(EXTRA_CANCEL, EXTRA_CANCEL);
        return intent;
    }

    public static Intent buildClipboardIntent(Context context, String url){
        Intent intent = new Intent(context, UploadService.class);
        intent.putExtra(EXTRA_CLIPBOARD, url);

        return intent;
    }

    /**
     * Fallback for pre jelly-bean
     * @param data
     */
    public static Intent buildIntent(Context context, Uri data) {
        Intent intent = new Intent(context, UploadService.class);
        intent.setData(data);
        return intent;
    }

    public UploadService() {
        super("UploadService");
    }

    @Override
    public void onCreate() {
        super.onCreate();

        this.api = ((FilesApp)getApplication()).getComponent().api();

        this.gson = ((FilesApp)getApplication()).getComponent().gson();

        this.notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        this.clipboard =  (ClipboardManager)
                getSystemService(Context.CLIPBOARD_SERVICE);

        this.progressCallback = new ProgressCallback();
    }

    private FileIOApi api;

    private Gson gson;

    private ClipboardManager clipboard;

    private NotificationManager notificationManager;

    private Call<ResponseBody> uploadCall;

    private NotificationCompat.Builder currentNotification;

    private ProgressRequestBody.UploadCallbacks progressCallback;

    private Handler handler = new Handler(Looper.getMainLooper());

    private int oldProgress;

    @Override
    protected void onHandleIntent(Intent intent) {
        if(intent != null) {

            if (intent.getStringExtra(EXTRA_CLIPBOARD) != null) {
                copyToClipboard(intent.getStringExtra(EXTRA_CLIPBOARD));
            }

            else if(intent.getStringExtra(EXTRA_CANCEL) != null){
                cancelUpload();
            }

            else {

                RxFile.createFilesFromClipData(this, intent.getClipData())
                        .subscribe(
                                files -> {

                                    if (files != null && !files.isEmpty()) {

                                        File file = files.get(0);

                                        ProgressRequestBody progressRequestBody = new ProgressRequestBody(file, progressCallback);


                                        MultipartBody.Part body =
                                                MultipartBody.Part.createFormData("file", file.getName(), progressRequestBody);


                                        uploadCall = api.uploadFile(FileIOApi.base, body);

                                        showUploadingNotification(file.getName(), 0);

                                        uploadCall.enqueue(new Callback<ResponseBody>() {
                                            @Override
                                            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                                                if (response != null && response.isSuccessful()) {

                                                    try {
                                                        Link link = gson.fromJson(response.body().string(), Link.class);
                                                        showUploadFinishedNotification(link.getLink());
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<ResponseBody> call, Throwable t) {
                                                showRetryNotification();
                                            }
                                        });

                                    }
                                },
                                throwable -> {
                                    throwable.printStackTrace();
                                }
                        );
            }
        }
    }

    private void cancelUpload() {
        if(uploadCall != null){
            uploadCall.cancel();
            notificationManager.cancel(NOTIFICATION_ID);
        }
    }

    private void copyToClipboard(String url) {
        if(url != null) {
            ClipData clip = ClipData.newPlainText(getString(R.string.file_share_label), url);
            clipboard.setPrimaryClip(clip);
            handler.post(() -> Toast.makeText(getApplicationContext(),
                    getString(R.string.copied_to_clipboard),
                    Toast.LENGTH_SHORT).show());
        }

    }

    @Override
    public void updateNotificationProgress(int progress) {

    }

    @Override
    public void showUploadingNotification(@NonNull String fileName, int progress) {

        if(currentNotification == null) {
            PendingIntent cancelIntent = PendingIntent.getService(this, REQUEST_CODE_CANCEL, buildCancelIntent(this), PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Action cancel = new NotificationCompat.Action(0, getString(R.string.cancel), cancelIntent);

            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                            .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                            .setProgress(100, progress, false)

                            .setSmallIcon(R.drawable.ic_send_black_24dp)
                            .setContentText(fileName)
                            .setContentTitle(getString(R.string.uploading))
                            .addAction(cancel);

            currentNotification = builder;
            displayNotification(currentNotification.build());

        } else {

            if(progress > oldProgress + 2) {
                oldProgress = progress;
                currentNotification.setProgress(100, progress, false);
                displayNotification(currentNotification.build());
            }
        }


    }

    @Override
    public void showUploadFinishedNotification(@NonNull String url) {

        Intent shareIntent = new Intent(Intent.ACTION_SEND);

        shareIntent.putExtra(Intent.EXTRA_TEXT, url);
        shareIntent.setType("text/plain");

        PendingIntent pendingShareIntent = PendingIntent.getActivity(this, 0, Intent.createChooser(shareIntent, "Share..."),
                PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action share = new NotificationCompat.Action(0, getString(R.string.share), pendingShareIntent);



        Intent copyIntent = buildClipboardIntent(this, url);
        PendingIntent pendingCopyIntent = PendingIntent.getService(this, 0, copyIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Action copy = new NotificationCompat.Action(0, getString(R.string.copy), pendingCopyIntent);


        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(this)
                        .setColor(ContextCompat.getColor(this, R.color.colorAccent))
                        .setSmallIcon(R.drawable.ic_cloud_done_black_24dp)
                        .setContentText(url)
                        .setContentTitle(getString(R.string.upload_success))
                        .addAction(share)
                        .addAction(copy);

        displayNotification(builder.build());

    }

    @Override
    public void showRetryNotification() {

    }

    @Override
    public void displayNotification(Notification notification) {
        notificationManager.notify(NOTIFICATION_ID, notification);
    }


    private final class ProgressCallback implements ProgressRequestBody.UploadCallbacks{
        @Override
        public void onProgressUpdate(int percentage, String fileName) {
            showUploadingNotification(fileName, percentage);
        }
    }
}
