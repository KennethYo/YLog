package com.yiche.library.ylog;

import android.text.TextUtils;
import android.util.Log;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.yiche.library.ylog.Utils.formatMessage;
import static com.yiche.library.ylog.Utils.getString;
import static com.yiche.library.ylog.Utils.getTargetStack;

/**
 * Created by kenneth on 16/9/8.
 */

class LogPrinter implements Printer {

  private static final int INDENT_SPACES = 2;

  private static final char TOP_LEFT_CORNER = '╔';
  private static final char BOTTOM_LEFT_CORNER = '╚';
  private static final char MIDDLE_CORNER = '╟';
  private static final char HORIZONTAL_DOUBLE_LINE = '║';
  private static final String DOUBLE_DIVIDER = "════════════════════════════════════════════";
  private static final String SINGLE_DIVIDER = "────────────────────────────────────────────";
  private static final String TOP_BORDER = TOP_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
  private static final String BOTTOM_BORDER = BOTTOM_LEFT_CORNER + DOUBLE_DIVIDER + DOUBLE_DIVIDER;
  private static final String MIDDLE_BORDER = MIDDLE_CORNER + SINGLE_DIVIDER + SINGLE_DIVIDER;

  private final Setting setting = new Setting();
  private static final int MAX_LOG_LENGTH = 4000;

  LogPrinter() {
  }

  public Setting init(String tag) {
    if (TextUtils.isEmpty(tag)) {
      throw new NullPointerException("tag can not be null or empty");
    }
    setting.setTag(tag);
    return setting;
  }

  @Override public Printer t(String tag) {
    tagThread.set(tag);
    return this;
  }

  @Override public void v(Object o) {
    l(getTag(), VERBOSE, null, getString(o));
  }

  @Override public void v(String msg, Object... args) {
    l(getTag(), VERBOSE, null, msg, args);
  }

  @Override public void d(Object o) {
    l(getTag(), DEBUG, null, getString(o));
  }

  @Override public void d(String msg, Object... args) {
    l(getTag(), DEBUG, null, msg, args);
  }

  @Override public void i(Object o) {
    l(getTag(), INFO, null, getString(o));
  }

  @Override public void i(String msg, Object... args) {
    l(getTag(), INFO, null, msg, args);
  }

  @Override public void w(Object o) {
    l(getTag(), WARN, null, getString(o));
  }

  @Override public void w(String msg, Object... args) {
    l(getTag(), WARN, null, msg, args);
  }

  @Override public void w(Throwable t) {
    l(getTag(), WARN, t, null);
  }

  @Override public void w(Throwable t, Object o) {
    l(getTag(), WARN, t, getString(o));
  }

  @Override public void w(Throwable t, String msg, Object... args) {
    l(getTag(), ERROR, t, msg, args);
  }

  @Override public void e(Object o) {
    l(getTag(), ERROR, null, getString(o));
  }

  @Override public void e(Throwable t) {
    l(getTag(), ERROR, t, null);
  }

  @Override public void e(String msg, Object... args) {
    l(getTag(), ERROR, null, msg, args);
  }

  @Override public void e(Throwable t, Object o) {
    l(getTag(), ERROR, t, getString(o));
  }

  @Override public void e(Throwable t, String msg, Object... args) {
    l(getTag(), ERROR, t, msg, args);
  }

  @Override public void json(String json) {
    if (TextUtils.isEmpty(json)) {
      d("json string is empty");
      return;
    }
    try {
      if (json.startsWith("{")) {
        JSONObject jsonObject = new JSONObject(json);
        String message = jsonObject.toString(INDENT_SPACES);
        d(message);
        return;
      }
      if (json.startsWith("[")) {
        JSONArray jsonArray = new JSONArray(json);
        String message = jsonArray.toString(INDENT_SPACES);
        d(message);
        return;
      }
      e("json string is not a real json");
    } catch (JSONException e) {
      e(e);
    }
  }

  @Override public void xml(String xml) {
    if (TextUtils.isEmpty(xml)) {
      d("xml string is empty");
      return;
    }

    try {
      StreamSource streamSource = new StreamSource(new StringReader(xml));
      StreamResult streamResult = new StreamResult(new StringWriter());

      Transformer transformer = TransformerFactory.newInstance().newTransformer();
      transformer.setOutputProperty(OutputKeys.INDENT, "yes");
      transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
      transformer.transform(streamSource, streamResult);
      d(streamResult.getWriter().toString().replaceFirst(">", ">\n"));
    } catch (TransformerException e) {
      e(e);
    }
  }

  @Override public synchronized void l(String tag, @Priority int priority, Throwable t, String msg,
      Object... args) {
    String message = formatMessage(msg, args);
    l(tag, priority, t, message);
  }

  @Override
  public synchronized void l(String tag, @Priority int priority, Throwable t, String msg) {
    if (setting.getErrorListener() != null && t != null) {
      setting.getErrorListener().onThrowable(priority, t);
    }

    if (!setting.isDebug()) {
      return;
    }

    if (priority > setting.getShowPriority()) {
      return;
    }

    StringBuilder sb = new StringBuilder();

    sb.append(TOP_BORDER).append("\n");
    logProcessAndThread(sb);
    logStackTrace(sb);
    logContent(sb, t, msg);
    sb.append(BOTTOM_BORDER);

    androidLog(tag, priority, sb.toString());
  }

  private void logContent(StringBuilder sb, Throwable t, String msg) {
    if (TextUtils.isEmpty(msg)) {
      msg = t == null ? "content is null" : Log.getStackTraceString(t);
    } else {
      msg = t == null ? msg : Log.getStackTraceString(t) + "\n" + msg;
    }

    for (int i = 0, length = msg.length(); i < length; i++) {
      int newline = msg.indexOf('\n', i);
      newline = newline != -1 ? newline : length;
      do {
        int end = Math.min(newline, i + MAX_LOG_LENGTH);
        String part = msg.substring(i, end);
        sb.append(HORIZONTAL_DOUBLE_LINE + " ").append(part).append("\n");
        i = end;
      } while (i < newline);
    }
  }

  private void logStackTrace(StringBuilder sb) {
    if (!setting.isShowStackTrace()) {
      return;
    }

    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

    StackTraceElement[] stacks;

    if (setting.getWrapperClass() != null) {
      stacks = getTargetStack(stackTrace, setting.getShowClassCount(), YLog.class, LogPrinter.class,
          setting.getWrapperClass());
    } else {
      stacks =
          getTargetStack(stackTrace, setting.getShowClassCount(), YLog.class, LogPrinter.class);
    }

    for (StackTraceElement stack : stacks) {
      if (stack == null) continue;
      String stackString = "at "
          + stack.getClassName()
          + "."
          + stack.getMethodName()
          + " "
          + "("
          + stack.getFileName()
          + ":"
          + stack.getLineNumber()
          + ")";

      sb.append(HORIZONTAL_DOUBLE_LINE + " ").append(stackString).append("\n");
    }
    sb.append(MIDDLE_BORDER).append("\n");
  }

  private void logProcessAndThread(StringBuilder sb) {
    if (!TextUtils.isEmpty(setting.getProcessName())) {
      sb.append(HORIZONTAL_DOUBLE_LINE + " Process : ")
          .append(setting.getProcessName())
          .append("\n");
    }
    if (setting.isShowThreadName()) {
      sb.append(HORIZONTAL_DOUBLE_LINE + " Thread  : ")
          .append(Thread.currentThread().getName())
          .append("\n");
    }
    if (!TextUtils.isEmpty(setting.getProcessName()) || setting.isShowThreadName()) {
      sb.append(MIDDLE_BORDER).append("\n");
    }
  }

  private String getTag() {
    String tag = tagThread.get();
    if (tag != null) {
      tagThread.remove();
    } else {
      tag = setting.getTag();
    }
    return tag;
  }

  private void androidLog(String tag, int priority, String msg) {
    switch (priority) {
      case VERBOSE:
        Log.v(tag, msg);
        break;
      case INFO:
        Log.i(tag, msg);
        break;
      case WARN:
        Log.w(tag, msg);
        break;
      case DEBUG:
        Log.d(tag, msg);
        break;
      case ERROR:
        Log.e(tag, msg);
        break;
    }
  }
}
