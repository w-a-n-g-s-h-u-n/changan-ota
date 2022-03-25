package com.iflytek.autofly.launcher.apps.entity;

import android.graphics.drawable.Drawable;
import java.io.Serializable;
import lombok.Data;

@Data
public class AppInfo implements Serializable, Comparable<AppInfo> {

  private static final long serialVersionUID = -5980636418872707856L;
  int appSort;
  int from;
  int nameId;
  int position = 10000;
  int resId;
  int typeSort;
  Drawable icon;
  String id;
  Long mId;
  String name;
  String packageName;
  String pic;
  String type;
  String typeName;
  String uri;

  @Override
  public int compareTo(AppInfo o) {
    return getPosition() - o.getPosition();
  }
}
