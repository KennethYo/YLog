package com.yiche.ylog;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.yiche.library.ylog.YLog;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

  @Override protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    ButterKnife.bind(this);
  }

  @OnClick(R.id.show_tag) public void showTag() {
    YLog.t("showTag").d("Let's show a tag");
  }

  @OnClick(R.id.show_exception) public void showException() {
    YLog.e(new NullPointerException("Let's show an Exception"), "Let's show an Exception");
    //YLog.e(new NullPointerException("Let's show an Exception"));
    //YLog.e(null, "Let's show an Exception");
    //YLog.e(null);
  }

  @OnClick(R.id.show_child_thread) public void showChildThread() {
    new Thread() {
      @Override public void run() {
        setName("child thread");
        YLog.d("show in child thread");
      }
    }.start();
  }

  @OnClick(R.id.show_array) public void showArray() {
    int[] array = { 1, 2, 3, 4, 5, 6 };
    //boolean[] array ={true,false,true,false};
    //byte[] array = { 1, 2, 3, 4, 5, 6 };
    //char[] array = { 'a', 'b', 'c', 'd' };
    //String[] array = { "Tom", "Jack", "Jerry", "Tong" };
    YLog.d(array);
  }

  @OnClick(R.id.show_map) public void showMap() {
    Map<String, String> map = new HashMap<>();
    map.put("Tom", "male");
    map.put("Jack", "male");
    map.put("Jerry", "male");
    map.put("Tong", "male");

    YLog.d(map);
  }

  @OnClick(R.id.show_list) public void showList() {
    List<String> names = new ArrayList<>();
    names.add("Tom");
    names.add("Jack");
    names.add("Jerry");
    names.add("Tong");

    YLog.d(names);
  }

  @OnClick(R.id.show_json) public void showJson() {
    try {
      String json = inputStream2String("json");
      YLog.json(json);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @OnClick(R.id.show_xml) public void showXml() {
    try {
      String xml = inputStream2String("xml");
      YLog.xml(xml);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  @OnClick(R.id.show_warpper) public void showWarpper() {
    new WarpperLog().log();
  }

  private String inputStream2String(String fileName) throws IOException {
    InputStream inputStream = getAssets().open(fileName);
    String s;
    ByteArrayOutputStream outStream = null;
    try {
      outStream = new ByteArrayOutputStream();
      byte[] data = new byte[4096];
      int count = -1;
      while ((count = inputStream.read(data, 0, 4096)) != -1) outStream.write(data, 0, count);

      s = new String(outStream.toByteArray(), "UTF-8");
    } finally {
      if (outStream != null) {
        outStream.close();
      }
    }
    return s;
  }

  public static class WarpperLog {

    public void log() {
      YLog.d("Warpper YLog");
    }
  }
}
