package com.changan.ota.cache;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import com.iflytek.autofly.launcher.apps.entity.AppInfo;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

public class AppManager {

  public static void main(String[] args) {
    String filePath = "/data/data/com.iflytek.autofly.launcher/cache/ACache/xxxxxxxx";
    AppInfo appInfo = new AppInfo();
    appInfo.setName("软件");
    appInfo.setPackageName("com.changan.ota");
    AppManager.addApp(filePath, appInfo);
  }

  public static void addApp(String filePath, AppInfo appInfo) {
    ArrayList<AppInfo> data = read(filePath);
    data.add(appInfo);
    File newFile = newFile(filePath);
    write(data, newFile);
    System.out.println("添加软件成功，新文件路径：" + newFile.getPath());
  }

  public static ArrayList<AppInfo> read(String filePath) {
    try (InputStream in = FileUtil.getInputStream(filePath)) {
      return IoUtil.readObj(in);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static void write(ArrayList<AppInfo> data, File newFile) {
    IoUtil.writeObj(FileUtil.getOutputStream(newFile), true, data);
  }

  private static File newFile(String filePath) {
    String parentPath = FileUtil.getParent(filePath, 1);
    String fileName = FileNameUtil.mainName(filePath);
    String rawFilePath = parentPath + "/" + fileName + "_new";
    File rawFile = FileUtil.file(rawFilePath);
    if (rawFile.exists()) {
      FileUtil.del(rawFile);
    }
    FileUtil.touch(rawFile);
    return rawFile;
  }
}
