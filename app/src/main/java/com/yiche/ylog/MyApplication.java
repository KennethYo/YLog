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
    //初始化
    YLog.init("YLogDemo")//默认 tag
        .debug(true)//设置 debug
        .showProcessName(this)//显示进程名
        .showThreadName(true)//显示线程
        .showStackTrace(true)//显示调用栈
        .setShowClassCount(2)//显示调用栈行数
        .showPriority(Printer.ERROR)//显示 log 级别
        .setWarpperClass(MainActivity.WarpperLog.class)//包裹类
        .setErrorListener(new ErrorListener() {
          @Override public void onError(Throwable t) {//throwable 回调
            new Handler(Looper.getMainLooper()).post(new Runnable() {
              @Override public void run() {
                Toast.makeText(MyApplication.this, "Oops, error", Toast.LENGTH_SHORT).show();
              }
            });
          }
        });
  }
}
