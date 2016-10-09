package com.yiche.library.ylog;

/**
 * Created by kenneth on 16/9/7.
 */

public class Setting {
  public static final String TAG = "YLog";
  private String tag = TAG;
  private boolean debug;
  private boolean showThread;
  private boolean showStackTrace;
  private int showClassCount = 1;
  private boolean showBorder;
  @Printer.Priority private int showPriority = Printer.VERBOSE;
  private Class warpperClass;
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

  public boolean isShowThread() {
    return showThread;
  }

  public Setting showThread(boolean show) {
    this.showThread = show;
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

  public Class getWarpperClass() {
    return warpperClass;
  }

  public Setting setWarpperClass(Class warpperClass) {
    this.warpperClass = warpperClass;
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
