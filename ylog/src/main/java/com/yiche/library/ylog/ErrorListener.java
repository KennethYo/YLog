package com.yiche.library.ylog;

/**
 * Created by kenneth on 16/9/8.
 */

public interface ErrorListener {

  void onThrowable(int priority, Throwable t);
}
