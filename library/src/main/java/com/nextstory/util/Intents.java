package com.nextstory.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.Settings;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;

/**
 * 자주 사용하는 Intent 모음
 *
 * @author troy
 * @since 1.2
 */
@SuppressWarnings("UnusedDeclaration")
public final class Intents {
    private Intents() {
        // no-op
    }

    /**
     * 이메일 연결
     *
     * @param chooserTitle   이메일 앱 선택창 제목
     * @param title          이메일 제목
     * @param content        이메일 내용
     * @param emailAddresses 수신 이메일 주소, 여러개 지정할 수 있음
     * @return intent
     */
    @NonNull
    public static Intent email(String chooserTitle,
                               String title, String content,
                               @NonNull String... emailAddresses) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("message/rfc822");
        intent.putExtra(Intent.EXTRA_EMAIL, emailAddresses);
        intent.putExtra(Intent.EXTRA_TEXT, title);
        intent.putExtra(Intent.EXTRA_SUBJECT, content);
        return Intent.createChooser(intent, chooserTitle);
    }

    /**
     * 다이얼 연결
     *
     * @param tel 다이얼에 입력할 번호
     * @return intent
     */
    @NonNull
    public static Intent dial(String tel) {
        if (tel == null) {
            tel = "0";
        }
        return new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + tel.trim().replace("-", "")));
    }

    /**
     * 웹 브라우저 연결
     *
     * @param url 웹 Url
     * @return intent
     */
    @NonNull
    public static Intent webBrowser(String url) {
        return new Intent(Intent.ACTION_VIEW, Uri.parse(url));
    }

    /**
     * 현재 앱의 스토어 연결
     *
     * @param context 컨텍스트
     * @return intent
     */
    @NonNull
    public static Intent market(Context context) {
        return market(context.getPackageName());
    }

    /**
     * 패키지명에 해당하는 앱의 스토어 연결
     *
     * @param packageName 패키지명
     * @return intent
     */
    @NonNull
    public static Intent market(String packageName) {
        Uri uri = Uri.parse("market://details?id=" + packageName);
        return new Intent(Intent.ACTION_VIEW, uri);
    }

    /**
     * 현재 앱의 설정 연결
     *
     * @param context 컨텍스트
     * @return intent
     */
    @NonNull
    public static Intent appSettings(Context context) {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.parse("package:" + context.getPackageName()));
        return intent;
    }

    /**
     * 위치 설정 연결
     *
     * @return intent
     */
    @NonNull
    public static Intent locationSettings() {
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        intent.addCategory(Intent.CATEGORY_DEFAULT);
        return intent;
    }

    /**
     * 공유 연결
     *
     * @param chooserTitle 앱 선택창 제목
     * @param content      공유할 내용
     * @return intent
     */
    @NonNull
    public static Intent share(String chooserTitle, String content) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("text/plain");
        intent.putExtra(Intent.EXTRA_SUBJECT, "");
        intent.putExtra(Intent.EXTRA_TEXT, content);
        return Intent.createChooser(intent, chooserTitle);
    }

    /**
     * 공유 연결
     *
     * @param context      컨텍스트
     * @param chooserTitle 앱 선택창 제목
     * @param content      공유할 내용
     * @return intent
     */
    public static Intent share(Context context, String chooserTitle, Bitmap content) {
        File path = context.getCacheDir();
        String strFileName = "share_cache.png";
        String filePath = path.toString() + "/" + strFileName;
        File file = new File(filePath);
        try {
            FileOutputStream fos = new FileOutputStream(file);
            if (content.compress(Bitmap.CompressFormat.PNG, 100, fos)) {
                Uri imageUri = FileProvider.getUriForFile(
                        context,
                        context.getPackageName() + ".provider",
                        file);
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("image/*");
                intent.putExtra(Intent.EXTRA_STREAM, imageUri);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                return Intent.createChooser(intent, chooserTitle);
            }
        } catch (Throwable e) {
            e.printStackTrace();
        }
        return null;
    }
}
