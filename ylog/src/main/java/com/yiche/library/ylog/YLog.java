package com.yiche.library.ylog;

/**
 * Created by kenneth on 16/9/7.
 */

public class YLog {

  private static Printer printer = new LogPrinter();

  private YLog() {
  }

  public static Setting init(String tag) {

    printer = new LogPrinter();
    return printer.init(tag);
  }

  public static Printer t(String tag) {
    return printer.t(tag);
  }

  public static void v(Object o) {
    printer.v(o);
  }

  public static void v(String msg, Object... args) {
    printer.v(msg, args);
  }

  public static void d(Object o) {
    printer.d(o);
  }

  public static void d(String msg, Object... args) {
    printer.d(msg, args);
  }

  public static void i(Object o) {
    printer.i(o);
  }

  public static void i(String msg, Object... args) {
    printer.i(msg, args);
  }

  public static void w(Object o) {
    printer.w(o);
  }

  public static void w(String msg, Object... args) {
    printer.w(msg, args);
  }

  public static void e(Object o) {
    printer.e(o);
  }

  public static void e(String msg, Object... args) {
    printer.e(msg, args);
  }

  public static void e(Throwable t) {
    printer.e(t);
  }

  public static void e(Throwable t, Object o) {
    printer.e(t, o);
  }

  public static void e(Throwable t, String msg, Object... args) {
    printer.e(t, msg, args);
  }

  public static void l(String tag, @Printer.Priority int priority, Throwable t, String msg) {
    printer.l(tag, priority, t, msg);
  }

  public static void l(String tag, @Printer.Priority int priority, Throwable t, String msg,
      Object... args) {
    printer.l(tag, priority, t, msg, args);
  }

  public static void json(String json) {
    printer.json(json);
  }

  public static void xml(String xml) {
    printer.xml(xml);
  }
}
