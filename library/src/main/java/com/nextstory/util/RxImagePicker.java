package com.nextstory.util;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.provider.MediaStore;

import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.canhub.cropper.CropImageContract;
import com.canhub.cropper.CropImageContractOptions;
import com.canhub.cropper.CropImageOptions;
import com.esafirm.imagepicker.features.ImagePickerConfig;
import com.esafirm.imagepicker.features.ImagePickerLauncher;
import com.esafirm.imagepicker.features.ImagePickerLauncherKt;
import com.esafirm.imagepicker.features.ImagePickerMode;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.nextstory.app.AbstractBaseActivity;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import io.reactivex.rxjava3.core.Single;

/**
 * Rx 기반 이미지 선택
 *
 * @author troy
 * @since 1.0
 */
@SuppressWarnings({"UnusedDeclaration", "unchecked"})
public final class RxImagePicker {
    private static final String EXTRA_CROP_MODE = "extra_cropMode";
    private static final String EXTRA_COUNT = "extra_count";
    private static final String EXTRA_IMAGES = "extra_images";

    private static final Handler mainThreadHandler = new Handler(Looper.getMainLooper());

    private final FragmentActivity activity;
    private boolean singleOrMultiple = true;
    private int count = 1;

    public RxImagePicker(Fragment fragment) {
        this.activity = fragment.requireActivity();
    }

    public RxImagePicker(FragmentActivity activity) {
        this.activity = activity;
    }

    /**
     * 가져올 이미지 개수 지정
     * ({@link #multiple()}을 호출할 경우에 동작이 됨)
     *
     * @param count 개수
     * @return 빌더 인스턴스
     */
    public RxImagePicker count(@IntRange(from = 1) int count) {
        this.count = count;
        this.singleOrMultiple = false;
        return this;
    }

    /**
     * 단일 이미지 가져오기
     *
     * @return Rx 인스턴스, {@link Uri} 형식 반환
     */
    public Single<Uri> single() {
        this.count = 1;
        this.singleOrMultiple = true;
        return new RxActivityResult(activity)
                .setIntent(new Intent(activity, InternalActivity.class)
                        .putExtra(EXTRA_CROP_MODE, singleOrMultiple)
                        .putExtra(EXTRA_COUNT, count))
                .asSingle()
                .map(result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        List<Uri> urls = (List<Uri>) result.getData()
                                .getSerializableExtra(EXTRA_IMAGES);
                        return urls.get(0);
                    }
                    return Uri.EMPTY;
                });
    }

    /**
     * 여러 이미지 가져오기
     *
     * @return Rx 인스턴스, {@link Uri} 목록 형식 반환
     */
    public Single<List<Uri>> multiple() {
        this.singleOrMultiple = false;
        return new RxActivityResult(activity)
                .setIntent(new Intent(activity, InternalActivity.class)
                        .putExtra(EXTRA_CROP_MODE, singleOrMultiple)
                        .putExtra(EXTRA_COUNT, count))
                .asSingle()
                .map(result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        return (List<Uri>) result.getData().getSerializableExtra(EXTRA_IMAGES);
                    }
                    return new ArrayList<>();
                });
    }

    /**
     * 내부 액티비티
     */
    public static class InternalActivity extends AbstractBaseActivity {
        private final AtomicBoolean isCropMode = new AtomicBoolean(false);
        private final AtomicInteger count = new AtomicInteger(0);

        private final ImagePickerLauncher pickerLauncher =
                ImagePickerLauncherKt.registerImagePicker(this, () -> this, images -> {
                    if (images != null) {
                        if (isCropMode.get()) {
                            try {
                                Bitmap bitmap;
                                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                    ImageDecoder.Source source = ImageDecoder.createSource(
                                            getContentResolver(),
                                            images.get(0).getUri());
                                    bitmap = ImageDecoder.decodeBitmap(source);
                                } else {
                                    bitmap = MediaStore.Images.Media.getBitmap(
                                            getContentResolver(),
                                            images.get(0).getUri());
                                }
                                Objects.requireNonNull(bitmap);
                                startCrop(images.get(0).getUri());
                                return null;
                            } catch (IOException e) {
                                e.printStackTrace();
                                finish();
                            }
                        } else {
                            List<Uri> uris = new ArrayList<>();
                            for (Image image : images) {
                                try {
                                    Bitmap bitmap;
                                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                                        ImageDecoder.Source source = ImageDecoder.createSource(
                                                getContentResolver(),
                                                image.getUri());
                                        bitmap = ImageDecoder.decodeBitmap(source);
                                    } else {
                                        bitmap = MediaStore.Images.Media.getBitmap(
                                                getContentResolver(),
                                                image.getUri());
                                    }
                                    Objects.requireNonNull(bitmap);
                                    uris.add(image.getUri());
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                            }
                            Intent resultIntent = new Intent();
                            resultIntent.putExtra(EXTRA_IMAGES, (Serializable) uris);
                            setResult(RESULT_OK, resultIntent);
                            finish();
                            return null;
                        }
                    }
                    return null;
                });

        private final ActivityResultLauncher<CropImageContractOptions> cropImage =
                registerForActivityResult(new CropImageContract(), result -> {
                    if (result.isSuccessful()) {
                        Uri resultUri = result.getUriContent();
                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_IMAGES,
                                (Serializable) Collections.singletonList(resultUri));
                        setResult(RESULT_OK, intent);
                    }
                    finish();
                });

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            count.set(getIntent().getIntExtra(EXTRA_COUNT, 1));
            isCropMode.set(getIntent().getBooleanExtra(EXTRA_CROP_MODE, false));

            start();
        }

        private void start() {
            ImagePickerConfig config = new ImagePickerConfig();
            config.setShowCamera(true);
            config.setFolderMode(false);

            if (isCropMode.get()) {
                config.setReturnMode(ReturnMode.ALL);
                config.setMode(ImagePickerMode.SINGLE);
                config.setLimit(1);
            } else {
                config.setMode(ImagePickerMode.MULTIPLE);
                config.setLimit(count.get());
            }

            pickerLauncher.launch(config);
        }

        private void startCrop(Uri imageUri) {
            CropImageOptions options = new CropImageOptions();
            CropImageContractOptions config = new CropImageContractOptions(imageUri, options);
            cropImage.launch(config);
        }
    }
}
