package com.nextstory.app.locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Build;
import android.os.LocaleList;

import androidx.annotation.RestrictTo;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

/**
 * @author troy
 * @since 1.2
 */
@SuppressWarnings({"UnusedDeclaration", "deprecation"})
@RestrictTo(RestrictTo.Scope.LIBRARY)
public final class LocaleManagerImpl implements LocaleManager {
    private static final String LANGUAGE = "language";
    private static final String COUNTRY = "country";

    private static List<Locale> supportedLocales = null;

    private final Supplier<Context> context;
    private SharedPreferences localePreferences = null;

    public LocaleManagerImpl(Context context) {
        this.context = context::getApplicationContext;
    }

    public LocaleManagerImpl(Supplier<Context> context) {
        this.context = context;
    }

    @Override
    public void registerSupportedLocales(List<Locale> locales) {
        supportedLocales = locales;
    }

    @Override
    public Locale getLocale() {
        String language = getLocalePreferences(context.get()).getString(LANGUAGE, "");
        String country = getLocalePreferences(context.get()).getString(COUNTRY, "");
        Locale locale = null;
        for (Locale item : Locale.getAvailableLocales()) {
            if (item == null) {
                continue;
            }
            if (item.getCountry().equals(country) && item.getLanguage().equals(language)) {
                locale = item;
                break;
            }
        }
        if (locale != null) {
            return locale;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return context.get()
                    .getApplicationContext()
                    .getResources()
                    .getConfiguration()
                    .getLocales()
                    .get(0);
        } else {
            return context.get()
                    .getApplicationContext()
                    .getResources()
                    .getConfiguration().locale;
        }
    }

    private Locale getLocale(Context context, Resources resources) {
        String language = getLocalePreferences(context).getString(LANGUAGE, "");
        String country = getLocalePreferences(context).getString(COUNTRY, "");
        Locale locale = null;
        for (Locale item : Locale.getAvailableLocales()) {
            if (item == null) {
                continue;
            }
            if (item.getCountry().equals(country) && item.getLanguage().equals(language)) {
                locale = item;
                break;
            }
        }
        if (locale != null) {
            return locale;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            return resources
                    .getConfiguration()
                    .getLocales()
                    .get(0);
        } else {
            return resources.getConfiguration().locale;
        }
    }

    @Override
    public void applyLocale(Locale locale) {
        String language = locale.getLanguage();
        String country = locale.getCountry();
        if (supportedLocales != null) {
            List<Locale> sameLanguages = new ArrayList<>();
            boolean isSupportedLocale = false;
            for (Locale supported : supportedLocales) {
                if (supported.getLanguage().equals(language)) {
                    sameLanguages.add(supported);
                }
                if (supported.getCountry().equals(language)) {
                    isSupportedLocale = true;
                    break;
                }
            }
            if (!isSupportedLocale && sameLanguages.size() > 0) {
                Locale firstSameLocale = sameLanguages.get(0);
                getLocalePreferences(context.get())
                        .edit()
                        .putString(LANGUAGE, firstSameLocale.getLanguage())
                        .putString(COUNTRY, firstSameLocale.getCountry())
                        .apply();
                return;
            }
        }
        getLocalePreferences(context.get())
                .edit()
                .putString(LANGUAGE, locale.getLanguage())
                .putString(COUNTRY, locale.getCountry())
                .apply();
    }

    @Override
    public Context wrapContext(Context context, Resources resources) {
        Locale.getAvailableLocales();

        Resources realResources = resources == null
                ? context.getResources()
                : resources;

        Locale locale = getLocale(context, realResources);
        Configuration configuration = realResources.getConfiguration();
        Locale.setDefault(locale);
        configuration.locale = locale;
        configuration.setLocale(locale);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            configuration.setLocales(localeList);
        } else {
            Configuration config = new Configuration();
            config.locale = locale;
            realResources.updateConfiguration(config, null);
        }

        return context.createConfigurationContext(configuration);
    }

    private SharedPreferences getLocalePreferences(Context context) {
        if (localePreferences == null) {
            localePreferences = context.getSharedPreferences("app_locale", Context.MODE_PRIVATE);
        }
        return localePreferences;
    }
}
