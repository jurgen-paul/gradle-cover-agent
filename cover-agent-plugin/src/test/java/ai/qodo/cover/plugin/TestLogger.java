package ai.qodo.cover.plugin;

import java.util.Arrays;
import org.gradle.api.logging.LogLevel;
import org.gradle.api.logging.Logger;
import org.slf4j.Marker;
public class TestLogger implements Logger {

  @Override
  public boolean isLifecycleEnabled() {
    return false;
  }

  @Override
  public String getName() {
    return "";
  }

  @Override
  public boolean isTraceEnabled() {
    return false;
  }

  @Override
  public void trace(String s) {}

  @Override
  public void trace(String s, Object o) {}

  @Override
  public void trace(String s, Object o, Object o1) {}

  @Override
  public void trace(String s, Object... objects) {}

  @Override
  public void trace(String s, Throwable throwable) {}

  @Override
  public boolean isTraceEnabled(Marker marker) {
    return false;
  }

  @Override
  public void trace(Marker marker, String s) {}

  @Override
  public void trace(Marker marker, String s, Object o) {}

  @Override
  public void trace(Marker marker, String s, Object o, Object o1) {}

  @Override
  public void trace(Marker marker, String s, Object... objects) {}

  @Override
  public void trace(Marker marker, String s, Throwable throwable) {}

  @Override
  public boolean isDebugEnabled() {
    return false;
  }

  @Override
  public void debug(String s) {
    System.out.println("DEBUG: " + s);
  }

  @Override
  public void debug(String s, Object o) {
    System.out.println("DEBUG: " + s + " " + o);
  }

  @Override
  public void debug(String s, Object o, Object o1) {
    System.out.println("DEBUG: " + s + " " + o + " " + o1);
  }

  @Override
  public void debug(String s, Object... objects) {
    System.out.println("DEBUG: " + s + " " + Arrays.toString(objects));
  }

  @Override
  public void debug(String s, Throwable throwable) {
    System.out.println("DEBUG: " + s);
    throwable.printStackTrace(System.out);
  }

  @Override
  public boolean isDebugEnabled(Marker marker) {
    return false;
  }

  @Override
  public void debug(Marker marker, String s) {
    System.out.println("DEBUG: " + s);
  }

  @Override
  public void debug(Marker marker, String s, Object o) {
    System.out.println("DEBUG: " + s + " " + o);
  }

  @Override
  public void debug(Marker marker, String s, Object o, Object o1) {
    System.out.println("DEBUG: " + s + " " + o + " " + o1);
  }

  @Override
  public void debug(Marker marker, String s, Object... objects) {
    System.out.println("DEBUG: " + s + " " + Arrays.toString(objects));
  }

  @Override
  public void debug(Marker marker, String s, Throwable throwable) {
    System.out.println("DEBUG: " + s);
    throwable.printStackTrace(System.out);
  }

  @Override
  public boolean isInfoEnabled() {
    return false;
  }

  @Override
  public void info(String s) {
    System.out.println("INFO: " + s);
  }

  @Override
  public void info(String s, Object o) {
    System.out.println("INFO: " + s + " " + o);
  }

  @Override
  public void info(String s, Object o, Object o1) {
    System.out.println("INFO: " + s + " " + o + " " + o1);
  }

  @Override
  public void info(String s, Object... objects) {
    System.out.println("INFO: " + s + " " + Arrays.toString(objects));
  }

  @Override
  public void info(String s, Throwable throwable) {
    System.out.println("INFO: " + s);
    throwable.printStackTrace(System.out);
  }

  @Override
  public boolean isInfoEnabled(Marker marker) {
    return false;
  }

  @Override
  public void info(Marker marker, String s) {
    System.out.println("INFO: " + s);
  }

  @Override
  public void info(Marker marker, String s, Object o) {
    System.out.println("INFO: " + s + " " + o);
  }

  @Override
  public void info(Marker marker, String s, Object o, Object o1) {
    System.out.println("INFO: " + s + " " + o + " " + o1);
  }

  @Override
  public void info(Marker marker, String s, Object... objects) {
    System.out.println("INFO: " + s + " " + Arrays.toString(objects));
  }

  @Override
  public void info(Marker marker, String s, Throwable throwable) {
    System.out.println("INFO: " + s);
    throwable.printStackTrace(System.out);
  }

  @Override
  public boolean isWarnEnabled() {
    return false;
  }

  @Override
  public void warn(String s) {}

  @Override
  public void warn(String s, Object o) {}

  @Override
  public void warn(String s, Object... objects) {}

  @Override
  public void warn(String s, Object o, Object o1) {}

  @Override
  public void warn(String s, Throwable throwable) {}

  @Override
  public boolean isWarnEnabled(Marker marker) {
    return false;
  }

  @Override
  public void warn(Marker marker, String s) {}

  @Override
  public void warn(Marker marker, String s, Object o) {}

  @Override
  public void warn(Marker marker, String s, Object o, Object o1) {}

  @Override
  public void warn(Marker marker, String s, Object... objects) {}

  @Override
  public void warn(Marker marker, String s, Throwable throwable) {}

  @Override
  public boolean isErrorEnabled() {
    return false;
  }

  @Override
  public void error(String s) {
    System.out.println("ERROR: " + s);
  }

  @Override
  public void error(String s, Object o) {
    System.out.println("ERROR: " + s + " " + o);
  }

  @Override
  public void error(String s, Object o, Object o1) {
    System.out.println("ERROR: " + s + " " + o + " " + o1);
  }

  @Override
  public void error(String s, Object... objects) {
    System.out.println("ERROR: " + s + " " + Arrays.toString(objects));
  }

  @Override
  public void error(String s, Throwable throwable) {
    System.out.println("ERROR: " + s);
    throwable.printStackTrace(System.out);
  }

  @Override
  public boolean isErrorEnabled(Marker marker) {
    return false;
  }

  @Override
  public void error(Marker marker, String s) {
    System.out.println("ERROR: " + s);
  }

  @Override
  public void error(Marker marker, String s, Object o) {
    System.out.println("ERROR: " + s + " " + o);
  }

  @Override
  public void error(Marker marker, String s, Object o, Object o1) {
    System.out.println("ERROR: " + s + " " + o + " " + o1);
  }

  @Override
  public void error(Marker marker, String s, Object... objects) {
    System.out.println("ERROR: " + s + " " + Arrays.toString(objects));
  }

  @Override
  public void error(Marker marker, String s, Throwable throwable) {
    System.out.println("ERROR: " + s);
    throwable.printStackTrace(System.out);
  }

  @Override
  public void lifecycle(String s) {
    System.out.println("LIFECYCLE: " + s);
  }

  @Override
  public void lifecycle(String s, Object... objects) {
    System.out.println("LIFECYCLE: " + s + " " + Arrays.toString(objects));
  }

  @Override
  public void lifecycle(String s, Throwable throwable) {
    System.out.println("LIFECYCLE: " + s);
    throwable.printStackTrace(System.out);
  }

  @Override
  public boolean isQuietEnabled() {
    return false;
  }

  @Override
  public void quiet(String s) {}

  @Override
  public void quiet(String s, Object... objects) {}

  @Override
  public void quiet(String s, Throwable throwable) {}

  @Override
  public boolean isEnabled(LogLevel logLevel) {
    return false;
  }

  @Override
  public void log(LogLevel logLevel, String s) {
    System.out.println("Level:" + logLevel + " Msg:" + s);
  }

  @Override
  public void log(LogLevel logLevel, String s, Object... objects) {
    System.out.println("Level:" + logLevel + " Msg:" + s);
    Arrays.stream(objects).forEach(o -> System.out.println(o));
  }

  @Override
  public void log(LogLevel logLevel, String s, Throwable throwable) {
    System.out.println("Level:" + logLevel + " Msg:" + s);
    throwable.printStackTrace(System.out);
  }
}