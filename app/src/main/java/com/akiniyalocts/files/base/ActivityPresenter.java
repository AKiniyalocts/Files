package com.akiniyalocts.files.base;

import android.content.Intent;
import android.os.Bundle;

/**
 * Created by anthonykiniyalocts on 8/7/16.
 */
public interface ActivityPresenter {

    void onCreate(Bundle savedInstanceState, Intent launchIntent);

    void onSaveInstanceState(Bundle saveInstanceState);

    void onResume();

    void onDestroy();

    void onPause();
}
