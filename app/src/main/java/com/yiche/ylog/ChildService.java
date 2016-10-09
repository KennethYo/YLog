package com.yiche.ylog;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.yiche.library.ylog.YLog;

/**
 * Created by kenneth on 2016/10/9.
 */

public class ChildService extends Service {
  @Nullable @Override public IBinder onBind(Intent intent) {
    return null;
  }

  @Override public void onCreate() {
    super.onCreate();
    YLog.d("onCreate");
  }

  @Override public void onDestroy() {
    super.onDestroy();
    YLog.d("onDestroy");
  }
}
