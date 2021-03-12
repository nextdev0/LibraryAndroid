package com.nextstory.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;

import androidx.annotation.IntRange;
import androidx.annotation.Nullable;
import androidx.exifinterface.media.ExifInterface;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;
import com.nextstory.activity.AbstractBaseActivity;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import io.reactivex.rxjava3.core.Single;

/**
 * Rx 기반 이미지 선택
 *
 * @author troy
 * @version 1.0
 * @since 1.0
 */
@SuppressWarnings({"UnusedDeclaration", "unchecked"})
public final class RxImagePicker {
    private static final String EXTRA_CROP_MODE = "extra_cropMode";
    private static final String EXTRA_COUNT = "extra_count";
    private static final String EXTRA_IMAGES = "extra_images";
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
     * 파일 Uri에서 바이트 배열을 추출
     *
     * @param context 앱 컨텍스트
     * @param fileUri 파일 Uri
     * @return 바이트 배열
     */
    private static byte[] getBytesByFileUri(Context context, Uri fileUri) {
        try {
            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
            if (inputStream == null) {
                return null;
            }
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 75, byteBuffer);
            byte[] bytes = byteBuffer.toByteArray();
            byteBuffer.close();
            bitmap.recycle();
            return bytes;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public RxImagePicker count(@IntRange(from = 1) int count) {
        this.count = count;
        this.singleOrMultiple = false;
        return this;
    }

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
        private boolean isCropMode;
        private int count;

        @Override
        protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
            if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {
                List<Image> images = ImagePicker.getImages(data);
                if (images != null) {
                    if (isCropMode) {
                        try {
                            Bitmap bitmap = Objects.requireNonNull(MediaStore.Images.Media.getBitmap(
                                    getContentResolver(), images.get(0).getUri()));
                            ExifInterface ei = new ExifInterface(images.get(0).getPath());
                            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                    ExifInterface.ORIENTATION_UNDEFINED);
                            switch (orientation) {
                                case ExifInterface.ORIENTATION_ROTATE_90:
                                    rotateImage(images.get(0).getUri(), bitmap, 90);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_180:
                                    rotateImage(images.get(0).getUri(), bitmap, 180);
                                    break;
                                case ExifInterface.ORIENTATION_ROTATE_270:
                                    rotateImage(images.get(0).getUri(), bitmap, 270);
                                    break;
                            }
                            CropImage.activity(images.get(0).getUri()).start(this);
                            return;
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        List<Uri> uris = new ArrayList<>();
                        for (Image image : images) {
                            try {
                                Bitmap bitmap = Objects.requireNonNull(
                                        MediaStore.Images.Media.getBitmap(
                                                getContentResolver(),
                                                image.getUri()));
                                ExifInterface ei = new ExifInterface(image.getPath());
                                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                        ExifInterface.ORIENTATION_UNDEFINED);
                                switch (orientation) {
                                    case ExifInterface.ORIENTATION_ROTATE_90:
                                        rotateImage(image.getUri(), bitmap, 90);
                                        break;
                                    case ExifInterface.ORIENTATION_ROTATE_180:
                                        rotateImage(image.getUri(), bitmap, 180);
                                        break;
                                    case ExifInterface.ORIENTATION_ROTATE_270:
                                        rotateImage(image.getUri(), bitmap, 270);
                                        break;
                                }
                                uris.add(image.getUri());
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                        Intent intent = new Intent();
                        intent.putExtra(EXTRA_IMAGES, (Serializable) Collections.singletonList(uris));
                        setResult(RESULT_OK, intent);
                        finish();
                        return;
                    }
                }
            }
            if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK && result != null) {
                    Uri resultUri = result.getUri();
                    Intent intent = new Intent();
                    intent.putExtra(EXTRA_IMAGES, (Serializable) Collections.singletonList(resultUri));
                    setResult(RESULT_OK, intent);
                    finish();
                    return;
                }
            }
            finish();
            super.onActivityResult(requestCode, resultCode, data);
        }

        @Override
        protected void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            count = getIntent().getIntExtra(EXTRA_COUNT, 1);
            isCropMode = getIntent().getBooleanExtra(EXTRA_CROP_MODE, false);
            start();
        }

        private void rotateImage(Uri fileUri, Bitmap source, float angle) {
            Matrix matrix = new Matrix();
            matrix.postRotate(angle);
            Bitmap bitmap = Bitmap.createBitmap(source,
                    0, 0,
                    source.getWidth(), source.getHeight(),
                    matrix,
                    true);
            try {
                OutputStream outputStream = getContentResolver().openOutputStream(fileUri);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        private void start() {
            ImagePicker imagePicker = ImagePicker.create(this)
                    .showCamera(true)
                    .folderMode(false);
            if (isCropMode) {
                imagePicker.returnMode(ReturnMode.ALL);
                imagePicker.single();
            } else {
                imagePicker.multi();
                imagePicker.limit(count);
            }
            imagePicker.start();
        }
    }
}
