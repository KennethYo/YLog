package com.yiche.library.ylog;

import android.app.ActivityManager;
import android.app.Application;
import android.os.Process;

/**
 * Created by kenneth on 16/9/7.
 */

public class Setting {
  public static final String TAG = "YLog";
  private String tag = TAG;
  private boolean debug;
  private boolean showThreadName;
  private String processName;
  private boolean showStackTrace;
  private int showClassCount = 1;
  @Printer.Priority private int showPriority = Printer.VERBOSE;
  private Class wrapperClass;
  private ErrorListener errorListener;

  public String getTag() {
    return tag;
  }

  public void setTag(String tag) {
    this.tag = tag;
  }

  public boolean isDebug() {
    return debug;
  }

  public Setting debug(boolean debug) {
    this.debug = debug;
    return this;
  }

  public boolean isShowThreadName() {
    return showThreadName;
  }

  public Setting showThreadName(boolean show) {
    this.showThreadName = show;
    return this;
  }

  public String getProcessName() {
    return processName;
  }

  public Setting showProcessName(Application app) {
    int myPid = Process.myPid();
    ActivityManager am = (ActivityManager) app.getSystemService(Application.ACTIVITY_SERVICE);
    for (ActivityManager.RunningAppProcessInfo appProcess : am.getRunningAppProcesses()) {
      if (appProcess.pid == myPid) {
        processName = appProcess.processName;
        break;
      }
    }
    return this;
  }

  public boolean isShowStackTrace() {
    return showStackTrace;
  }

  public Setting showStackTrace(boolean show) {
    this.showStackTrace = show;
    return this;
  }

  public int getShowPriority() {
    return showPriority;
  }

  public Setting showPriority(@Printer.Priority int showPriority) {
    this.showPriority = showPriority;
    return this;
  }

  public int getShowClassCount() {
    return showClassCount;
  }

  public Setting setShowClassCount(int showClassCount) {
    if (showClassCount < 1) {
      return this;
    }
    this.showClassCount = showClassCount;
    return this;
  }

  public Class getWrapperClass() {
    return wrapperClass;
  }

  public Setting setWrapperClass(Class wrapperClass) {
    this.wrapperClass = wrapperClass;
    return this;
  }

  public ErrorListener getErrorListener() {
    return errorListener;
  }

  public Setting setErrorListener(ErrorListener errorListener) {
    this.errorListener = errorListener;
    return this;
  }
}
