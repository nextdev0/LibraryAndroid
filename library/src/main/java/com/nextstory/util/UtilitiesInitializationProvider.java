package com.nextstory.util;

import android.content.ComponentName;
import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ProviderInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;

import java.util.Set;

/**
 * 유틸리티 초기화 클래스
 *
 * @author troy
 * @since 1.1
 */
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class UtilitiesInitializationProvider extends ContentProvider {
  @SuppressWarnings("unchecked")
  @Override
  public boolean onCreate() {
    try {
      Context context = getContext();
      ComponentName provider = new ComponentName(
        context.getPackageName(), UtilitiesInitializationProvider.class.getName());
      ProviderInfo providerInfo = context.getPackageManager()
        .getProviderInfo(provider, PackageManager.GET_META_DATA);
      Bundle metadata = providerInfo.metaData;
      if (metadata != null) {
        Set<String> keys = metadata.keySet();
        for (String key : keys) {
          try {
            String value = metadata.getString(key, null);
            Class<?> clazz = Class.forName(key);
            if (LibraryInitializer.class.isAssignableFrom(clazz)) {
              Class<? extends LibraryInitializer> component =
                (Class<? extends LibraryInitializer>) clazz;
              LibraryInitializer newInstance = component.newInstance();
              newInstance.onInitialized(context, value);
            }
          } catch (Throwable ignore) {
          }
        }
      }
    } catch (Throwable ignore) {
    }

    return true;
  }

  @Nullable
  @Override
  public Cursor query(@NonNull Uri uri,
                      @Nullable String[] projection,
                      @Nullable String selection,
                      @Nullable String[] selectionArgs,
                      @Nullable String sortOrder) {
    throw new IllegalStateException("not supported");
  }

  @Nullable
  @Override
  public String getType(@NonNull Uri uri) {
    throw new IllegalStateException("not supported");
  }

  @Nullable
  @Override
  public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
    throw new IllegalStateException("not supported");
  }

  @Override
  public int delete(@NonNull Uri uri,
                    @Nullable String selection,
                    @Nullable String[] selectionArgs) {
    throw new IllegalStateException("not supported");
  }

  @Override
  public int update(@NonNull Uri uri,
                    @Nullable ContentValues values,
                    @Nullable String selection,
                    @Nullable String[] selectionArgs) {
    throw new IllegalStateException("not supported");
  }
}
