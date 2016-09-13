package com.yiche.ylog;

import android.app.Application;
import android.os.Handler;
import android.os.Looper;
import android.widget.Toast;
import com.yiche.library.ylog.ErrorListener;
import com.yiche.library.ylog.Printer;
import com.yiche.library.ylog.YLog;

/**
 * Created by kenneth on 16/9/8.
 */

public class MyApplication extends Application {

  @Override public void onCreate() {
    super.onCreate();
    YLog.init("YLogDemo")
        .debug(true)
        .showThread(true)
        .showClass(true)
        .setShowClassCount(2)
        .showBorder(true)
        .showPriority(Printer.ERROR)
        .setErrorListener(new ErrorListener() {
          @Override public void onError(Throwable t) {
            new Handler(Looper.getMainLooper()).post(new Runnable() {
              @Override public void run() {
                Toast.makeText(MyApplication.this, "Oops, error", Toast.LENGTH_SHORT).show();
              }
            });
          }
        });
  }
}
