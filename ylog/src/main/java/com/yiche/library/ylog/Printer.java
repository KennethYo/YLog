package com.yiche.library.ylog;

import android.support.annotation.IntDef;
import android.util.Log;
import java.lang.annotation.Retention;

import static java.lang.annotation.RetentionPolicy.SOURCE;

/**
 * Created by kenneth on 16/9/8.
 */

public interface Printer {

  int VERBOSE = Log.VERBOSE;

  int DEBUG = Log.DEBUG;

  int INFO = Log.INFO;

  int WARN = Log.WARN;

  int ERROR = Log.ERROR;

  ThreadLocal<String> tagThread = new ThreadLocal<>();

  @Retention(SOURCE) @IntDef({ VERBOSE, DEBUG, INFO, WARN, ERROR }) @interface Priority {
  }

  Setting init(String tag);

  Printer t(String tag);

  void v(Object o);

  void v(String string, Object... args);

  void i(Object o);

  void i(String string, Object... args);

  void w(Object o);

  void w(String string, Object... args);

  void d(Object o);

  void d(String string, Object... args);

  void e(Object o);

  void e(Throwable t);

  void e(String string, Object... args);

  void e(Throwable t, Object o);

  void e(Throwable t, String message, Object... args);

  void json(String json);

  void xml(String xml);

  void l(String tag, @Priority int priority, Throwable t, String msg);

  void l(String tag, @Priority int priority, Throwable t, String msg, Object... args);
}
