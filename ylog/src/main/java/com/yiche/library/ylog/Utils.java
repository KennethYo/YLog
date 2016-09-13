package com.yiche.library.ylog;

import android.util.Log;
import java.util.Arrays;

/**
 * Created by kenneth on 16/9/9.
 */

class Utils {
  static String getString(Object o) {
    String message;
    if (o == null) {
      message = "object is null";
    } else if (o instanceof int[]) {
      message = Arrays.toString((int[]) o);
    } else if (o instanceof long[]) {
      message = Arrays.toString((long[]) o);
    } else if (o instanceof short[]) {
      message = Arrays.toString((short[]) o);
    } else if (o instanceof float[]) {
      message = Arrays.toString((float[]) o);
    } else if (o instanceof double[]) {
      message = Arrays.toString((double[]) o);
    } else if (o instanceof boolean[]) {
      message = Arrays.toString((boolean[]) o);
    } else if (o instanceof byte[]) {
      message = Arrays.toString((byte[]) o);
    } else if (o instanceof char[]) {
      message = Arrays.toString((char[]) o);
    } else if (o.getClass().isArray()) {
      message = Arrays.deepToString((Object[]) o);
    } else {
      message = o.toString();
    }
    return message;
  }

  static String formatMessage(String msg, Object... args) {
    if (args == null || args.length == 0) {
      return msg;
    }
    return String.format(msg, args);
  }

  static StackTraceElement[] getTargetStack(StackTraceElement[] elements, int offset) {
    int targetIndex = 0, length = elements.length;
    for (int i = 0; i < length; i++) {
      if (elements[i].getClassName().equals(YLog.class.getName()) || elements[i].getClassName()
          .equals(LogPrinter.class.getName())) {
        targetIndex = i;
      }
    }

    if (BuildConfig.DEBUG) {
      for (StackTraceElement traceElement : elements) {
        Log.d(Setting.TAG, traceElement.toString());
      }
    }

    StackTraceElement[] stackTraceElements = new StackTraceElement[offset];

    if (targetIndex + 1 + (offset - 1) < length) {
      System.arraycopy(elements, targetIndex + 1, stackTraceElements, 0, offset);
    } else if (offset <= length) {
      System.arraycopy(elements, length - offset, stackTraceElements, 0, offset);
    }

    return stackTraceElements;
  }
}
