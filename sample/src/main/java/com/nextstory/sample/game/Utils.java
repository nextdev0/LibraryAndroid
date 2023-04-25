package com.nextstory.sample.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.RawRes;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author troy
 * @since 1.0
 */
class Utils {
  private static final String TAG = "FileUtils";

  /**
   * @return AR 캐시 디렉토리 경로
   */
  public static String getCachePath(Context context) {
    return context.getCacheDir().getAbsolutePath() + "/libgdx";
  }

  /**
   * 파일 및 디렉토리 제거
   *
   * @param file 파일
   */
  public static void delete(File file) {
    File[] contents = file.listFiles();
    if (contents != null) {
      for (File content : contents) {
        delete(content);
      }
    }
    if (file.delete()) {
      Log.d(TAG, "deleted successful. (" + file.getAbsolutePath() + ")");
    }
  }

  /**
   * raw 리소스로 비트맵 로딩
   *
   * @param context 컨텍스트
   * @param res     리소스
   * @return 비트맵
   */
  public static Bitmap getBitmapByRawResource(Context context, @RawRes int res) {
    try {
      InputStream inputStream = context.getResources().openRawResource(res);
      return BitmapFactory.decodeStream(inputStream);
    } catch (Exception e) {
      e.printStackTrace();
    }
    return null;
  }

  @SuppressWarnings("ResultOfMethodCallIgnored")
  public static String getModelPathByRawResources(Context context, @RawRes int res) {
    String name = "ar_" + res;
    String arPath = getCachePath(context);
    String zipDecompressPath = combinePath(arPath, name);
    String temporaryName = combinePath(arPath, name + "_temp");
    try {
      File arPathFile = new File(arPath);
      if (!arPathFile.exists()) {
        arPathFile.mkdirs();
      }

      // zip 파일을 캐시 디렉토리에 복사
      InputStream inputStream = context.getResources().openRawResource(res);
      byte[] buffer = new byte[inputStream.available()];
      inputStream.read(buffer);
      inputStream.close();
      File temporaryFile = new File(temporaryName);
      OutputStream outputStream = new FileOutputStream(temporaryFile);
      outputStream.write(buffer);
      outputStream.flush();
      outputStream.close();

      // 압축해제
      decompressZip(temporaryFile.getAbsolutePath(), zipDecompressPath);
      delete(temporaryFile);
      File decompressedFile = new File(zipDecompressPath);
      for (File file : Objects.requireNonNull(decompressedFile.listFiles())) {
        if (file.getAbsolutePath().endsWith(".obj")) {
          return file.getAbsolutePath();
        }
      }
    } catch (Exception e) {
      e.printStackTrace();
    }

    return "";
  }

  /**
   * zip 파일 압축해제
   *
   * @param srcPath  zip 파일 경로
   * @param destPath 해제될 파일 경로
   */
  @SuppressWarnings("ResultOfMethodCallIgnored")
  public static void decompressZip(String srcPath, String destPath) throws IOException {
    FileInputStream fileInputStream = new FileInputStream(srcPath);
    ZipInputStream zipInputStream =
      new ZipInputStream(new BufferedInputStream(fileInputStream));

    byte[] buffer = new byte[1024];

    File destPathFile = new File(destPath);
    if (destPathFile.exists()) {
      delete(destPathFile);
    }
    destPathFile.mkdirs();

    ZipEntry zipEntry;
    while ((zipEntry = zipInputStream.getNextEntry()) != null) {
      if (!zipEntry.isDirectory()) {
        String filename = combinePath(destPath, zipEntry.getName());

        try {
          FileOutputStream fileOutputStream = new FileOutputStream(filename);
          int read;
          while ((read = zipInputStream.read(buffer, 0, buffer.length)) != -1)
            fileOutputStream.write(buffer, 0, read);
          zipInputStream.closeEntry();
        } catch (IOException ignore) {
        }
      }
    }
    zipInputStream.close();
  }

  /**
   * 경로 파일명 조합
   *
   * @param path     경로
   * @param fileName 파일명
   * @return 조합된 경로
   */
  public static String combinePath(@NonNull String path, @NonNull String fileName) {
    String trimPath = path.trim();
    String trimName = fileName.trim();
    if (trimPath.endsWith("/")) {
      if (trimName.startsWith("/")) {
        return trimPath.concat(trimName.substring(1));
      } else {
        return trimPath.substring(0, trimPath.length() - 1).concat(trimName);
      }
    } else {
      if (trimName.startsWith("/")) {
        return trimPath.concat(trimName);
      } else {
        return trimPath.concat("/").concat(trimName);
      }
    }
  }
}
