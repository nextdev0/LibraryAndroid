package com.nextstory.util;

import android.os.Handler;
import android.os.Looper;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;

import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Date;
import java.util.concurrent.CountDownLatch;
import java.util.function.Consumer;

import io.reactivex.rxjava3.core.Single;

/**
 * Rx 기반 날짜 선택
 *
 * @author troy
 * @version 1.0.2
 * @since 1.0
 */
@SuppressWarnings("UnusedDeclaration")
public final class RxDatePicker {
    private static final Handler mainThreadHandler = new Handler(Looper.getMainLooper());
    private final FragmentManager fragmentManager;
    private DateTime currentDateTime = DateTime.now();
    private String format = "yyyy-MM-dd HH:mm:ss";
    private boolean dateOrTime = true;

    public RxDatePicker(Fragment fragment) {
        fragmentManager = fragment.getChildFragmentManager();
    }

    public RxDatePicker(FragmentActivity activity) {
        fragmentManager = activity.getSupportFragmentManager();
    }

    public RxDatePicker currentDateTime(DateTime dateTime) {
        currentDateTime = dateTime == null ? DateTime.now() : dateTime;
        return this;
    }

    public RxDatePicker currentDate(String date, String format) {
        try {
            currentDateTime = DateTime.parse(date, DateTimeFormat.forPattern(format));
        } catch (Throwable ignore) {
            currentDateTime = DateTime.now();
        }
        return this;
    }

    public RxDatePicker currentDate(int year, int month, int day) {
        try {
            currentDateTime = currentDateTime.withDate(year, month, day);
        } catch (Throwable ignore) {
            currentDateTime = DateTime.now();
        }
        return this;
    }

    public RxDatePicker currentTime(int hour, int minute, int second) {
        try {
            currentDateTime = currentDateTime.withTime(hour, minute, second, 0);
        } catch (Throwable ignore) {
            currentDateTime = DateTime.now();
        }
        return this;
    }

    public RxDatePicker datePicker() {
        dateOrTime = true;
        return this;
    }

    public RxDatePicker timePicker() {
        dateOrTime = false;
        return this;
    }

    public RxDatePicker format(String format) {
        this.format = format;
        return this;
    }

    private void startPicker(Consumer<DateTime> dateTimeSupplier) throws InterruptedException {
        boolean isMainThread = Looper.myLooper() == Looper.getMainLooper();
        CountDownLatch lock = new CountDownLatch(1);
        mainThreadHandler.post(() -> {
            DialogFragment dialog;
            if (dateOrTime) {
                dialog = DatePickerDialog.newInstance(
                        (view, year, monthOfYear, dayOfMonth) -> {
                            currentDateTime =
                                    currentDateTime.withDate(year, monthOfYear + 1, dayOfMonth);
                            if (isMainThread) {
                                dateTimeSupplier.accept(currentDateTime);
                            } else {
                                lock.countDown();
                            }
                        },
                        currentDateTime.getYear(),
                        currentDateTime.getMonthOfYear() - 1,
                        currentDateTime.getDayOfMonth());
            } else {
                dialog = TimePickerDialog.newInstance(
                        (view, hourOfDay, minute, second) -> {
                            currentDateTime =
                                    currentDateTime.withTime(hourOfDay, minute, second, 0);
                            if (isMainThread) {
                                dateTimeSupplier.accept(currentDateTime);
                            } else {
                                lock.countDown();
                            }
                        },
                        currentDateTime.getHourOfDay(),
                        currentDateTime.getMinuteOfHour(),
                        false);
            }
            dialog.show(fragmentManager, "rx_date_picker_dialog");
        });
        if (!isMainThread) {
            lock.await();
            dateTimeSupplier.accept(currentDateTime);
        }
    }

    public Single<DateTime> asDateTime() {
        return Single.create(e -> startPicker(e::onSuccess));
    }

    public Single<String> asString() {
        return Single.create(e -> startPicker(dateTime -> e.onSuccess(dateTime.toString(format))));
    }

    public Single<Date> asDate() {
        return Single.create(e -> startPicker(dateTime -> e.onSuccess(dateTime.toDate())));
    }
}
