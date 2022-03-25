package com.changan.ota.decrypt;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.io.IoUtil;
import cn.hutool.core.io.file.FileNameUtil;
import cn.hutool.core.util.HexUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.CipherMode;
import cn.hutool.crypto.Mode;
import cn.hutool.crypto.Padding;
import cn.hutool.crypto.symmetric.AES;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;

public class Decrypt {

  private static final int CACHE_SIZE = 1024 * 1024 * 10;

  public static void main(String[] args) throws IOException {
    String filePath = "~/IncallUpgrade/HuOs/update.zip";// update.zip文件路径
    byte[] key = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};// 不同车型可能不一样，自行获取
    byte[] iv = new byte[]{1, 0, 0, 0, 0, 0, 6, 0, 0, 0, 3, 0, 0, 4, 0, 0};
    AES aes = new AES(Mode.CBC, Padding.PKCS5Padding, key, iv);
    decrypt(aes, filePath);
  }

  public static void decrypt(AES aes, String filePath) throws IOException {
    if (!FileUtil.exist(filePath)) {
      System.out.println("文件不存在!");
      return;
    }
    InputStream in = FileUtil.getInputStream(filePath);
    byte[] bytes = IoUtil.readBytes(in, 5);
    if (bytes.length < 5 || 1 != bytes[4]) {
      System.out.println("解包失败，文件格式不支持!");
      IoUtil.close(in);
      return;
    }
    int rawSize = rawSize(bytes);
    String rawFilePath = newRawFile(filePath);
    OutputStream out = FileUtil.getOutputStream(rawFilePath);
    decrypt(aes, in, out, rawSize);
    System.out.println("解包成功，文件路径：" + rawFilePath);
  }

  private static void decrypt(AES aes, InputStream in, OutputStream out, int rawSize) throws IOException {
    int totalReadSize = 0;
    int readSize;
    byte[] bytes;
    aes.setMode(CipherMode.decrypt);
    do {
      bytes = IoUtil.readBytes(in, CACHE_SIZE);
      readSize = bytes.length;
      if (readSize > 0) {
        totalReadSize += readSize;
        String percentage = new BigDecimal(totalReadSize).multiply(new BigDecimal(100)).divide(new BigDecimal(rawSize), 0, RoundingMode.DOWN).toPlainString();
        System.out.println(StrUtil.format("解包进度：{}%", percentage));
        out.write(aes.update(bytes));
      }
    } while (readSize >= CACHE_SIZE);
    out.flush();
    IoUtil.close(in);
    IoUtil.close(out);
  }

  private static String newRawFile(String filePath) {
    String parentPath = FileUtil.getParent(filePath, 1);
    String fileName = FileNameUtil.mainName(filePath);
    String rawFilePath = parentPath + "/" + fileName + "_raw." + FileUtil.getSuffix(filePath);
    File rawFile = FileUtil.file(rawFilePath);
    if (rawFile.exists()) {
      FileUtil.del(rawFile);
    }
    FileUtil.touch(rawFile);
    return rawFilePath;
  }

  private static int rawSize(byte[] bytes) {
    return HexUtil.hexToInt(
        HexUtil.toHex(bytes[3] & Byte.MAX_VALUE) +
        HexUtil.toHex(bytes[2] & Byte.MAX_VALUE) +
        HexUtil.toHex(bytes[1] & Byte.MAX_VALUE) +
        HexUtil.toHex(bytes[0] & Byte.MAX_VALUE)
    );
  }
}
