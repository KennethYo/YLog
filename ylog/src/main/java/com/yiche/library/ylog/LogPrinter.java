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

  @Override public void d(Object o) {
    l(getTag(), DEBUG, null, getString(o));
  }

  @Override public void d(String msg, Object... args) {
    l(getTag(), DEBUG, null, msg, args);
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
      e("json string not a real json");
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
    if (!setting.isDebug()) {
      return;
    }
    String message = formatMessage(msg, args);
    l(tag, priority, t, message);
  }

  @Override
  public synchronized void l(String tag, @Priority int priority, Throwable t, String msg) {
    if (!setting.isDebug()) {
      return;
    }
    if (priority > setting.getShowPriority()) {
      return;
    }

    if (setting.getErrorListener() != null) {
      setting.getErrorListener().onError(t);
    }

    androidLog(tag, priority, TOP_BORDER);
    logThread(tag, priority);
    logClass(tag, priority);
    logContent(tag, priority, t, msg);
    androidLog(tag, priority, BOTTOM_BORDER);
  }

  private void logContent(String tag, int priority, Throwable t, String msg) {
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
        androidLog(tag, priority, HORIZONTAL_DOUBLE_LINE + " " + part);
        i = end;
      } while (i < newline);
    }
  }

  private void logClass(String tag, @Priority int priority) {
    if (!setting.isShowClass()) {
      return;
    }

    StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();

    StackTraceElement[] stacks = getTargetStack(stackTrace, setting.getShowClassCount());
    for (int i = 0, size = stacks.length; i < size; i++) {
      StackTraceElement stack = stacks[i];
      if (stack == null) continue;
      StringBuilder sb = new StringBuilder();
      sb.append("Class : ");
      sb.append(stack.getClassName());
      sb.append(".");
      sb.append(stack.getMethodName());
      sb.append(" ");
      sb.append("(");
      sb.append(stack.getFileName());
      sb.append(":");
      sb.append(stack.getLineNumber());
      sb.append(")");
      androidLog(tag, priority, HORIZONTAL_DOUBLE_LINE + " " + sb.toString());
    }
    androidLog(tag, priority, MIDDLE_BORDER);
  }

  private void logThread(String tag, @Priority int priority) {
    if (!setting.isShowThread()) {
      return;
    }

    androidLog(tag, priority,
        HORIZONTAL_DOUBLE_LINE + " Thread : " + Thread.currentThread().getName());

    androidLog(tag, priority, MIDDLE_BORDER);
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

  private void androidLog(String tag, @Priority int priority, String msg) {
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
