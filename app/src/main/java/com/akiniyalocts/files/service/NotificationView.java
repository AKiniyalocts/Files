package com.akiniyalocts.files.service;

import android.app.Notification;
import android.support.annotation.NonNull;

/**
 * Created by anthonykiniyalocts on 1/28/17.
 */
public interface NotificationView {

    void showUploadingNotification(@NonNull String filename, int progress);

    void showUploadFinishedNotification(@NonNull final String url);

    void showRetryNotification();

    void displayNotification(Notification notification);

    void updateNotificationProgress(int progress);

}
