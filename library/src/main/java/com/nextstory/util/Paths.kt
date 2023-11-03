@file:Suppress("unused", "SpellCheckingInspection")

package com.nextstory.util

import android.media.MediaScannerConnection
import android.os.Environment
import com.nextstory.app.globalContext
import java.io.File

private val externalDir = Environment.getExternalStorageDirectory().absolutePath
private val emptyMediaScanListener = MediaScannerConnection.OnScanCompletedListener { _, _ -> }
private val fileSeparator = File.separator

/**
 * 경로 구성
 *
 * 예시:
 * ```
 * val path = pathOf("Test", "Second", "test.bin")
 * println(path) // "Test/Second/test.bin"
 * ```
 *
 * @param names 경로에 포함될 문자열 (가변 인자)
 * @return 경로 문자열
 */
fun pathOf(firstName: String, vararg names: String): String {
  if (names.isEmpty()) {
    return firstName
  }

  var result = firstName
  names.forEach {
    result = combinePath(result, it)
  }

  return result
}

/**
 * 외부 저장소 경로 구성
 *
 * 주의:
 * 범위 저장소 이용에 어려움이 있을 수 있음!
 * [Environment] 클래스의 'DIRECTORY_'접두어를 가진 경로에 접근할 것
 *
 * 예시:
 * ```
 * val path = externalDirPathOf(Environment.DIRECTORY_PICTURES, "TestApp", "test.png")
 * println(path) // "{외부 저장소 경로}/{사진 폴더 이름}/TestApp/test.png"
 *
 * val path2 = externalDirPathOf("TestApp", "test.png")
 * println(path2) // "{외부 저장소 경로}/TestApp/test.png"
 * ```
 *
 * @param names 경로에 포함될 문자열 (가변 인자)
 * @return 경로 문자열
 */
fun externalDirPathOf(vararg names: String): String {
  if (names.isEmpty()) {
    return externalDir
  }

  var result = externalDir
  names.forEach {
    result = combinePath(result, it)
  }

  return result
}

/**
 * 앱 데이터 경로 구성
 *
 * 예시:
 * ```
 * val path = dataDirPathOf("Test", "test.bin")
 * println(path) // "{앱 데이터 경로}/Test/test.bin"
 * ```
 *
 * @param names 경로에 포함될 문자열 (가변 인자)
 * @return 경로 문자열
 */
fun dataDirPathOf(vararg names: String): String {
  if (names.isEmpty()) {
    return globalContext.applicationInfo.dataDir
  }

  var result = globalContext.applicationInfo.dataDir
  names.forEach {
    result = combinePath(result, it)
  }

  return result
}

/**
 * 캐시 데이터 경로 구성
 *
 * 예시:
 * ```
 * val path = cacheDirPathOf("Test", "test.bin")
 * println(path) // "{앱 캐시 데이터 경로}/Test/test.bin"
 * ```
 *
 * @param names 경로에 포함될 문자열 (가변 인자)
 * @return 경로 문자열
 */
fun cacheDirPathOf(vararg names: String): String {
  if (names.isEmpty()) {
    return globalContext.cacheDir.absolutePath
  }

  var result = globalContext.cacheDir.absolutePath
  names.forEach {
    result = combinePath(result, it)
  }

  return result
}

private fun combinePath(path: String, fileName: String): String {
  val trimmedPath = path.trim()
  val trimmedName = fileName.trim()

  return when {
    trimmedPath.endsWith(fileSeparator) && trimmedName.startsWith(fileSeparator) -> {
      trimmedPath + trimmedName.substring(1)
    }

    trimmedPath.endsWith(fileSeparator) || trimmedName.startsWith(fileSeparator) -> {
      trimmedPath + trimmedName
    }

    else -> {
      "$trimmedPath$fileSeparator$trimmedName"
    }
  }
}

/**
 * 파일 미디어 스캔 실행
 *
 * @param paths 경로
 */
fun mediaScan(vararg paths: String) {
  MediaScannerConnection.scanFile(globalContext, paths, null, emptyMediaScanListener)
}
