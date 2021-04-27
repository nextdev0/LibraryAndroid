package com.nextstory.app;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;

import androidx.annotation.ArrayRes;
import androidx.annotation.AttrRes;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RestrictTo;
import androidx.annotation.StringRes;
import androidx.annotation.StyleRes;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

/**
 * 안드로이드 기본 알림창 프래그먼트
 *
 * @author troy
 * @see AlertDialog
 * @since 1.3
 */
@SuppressWarnings("UnusedDeclaration")
public final class AlertDialogFragment extends DialogFragment {
    @RestrictTo(RestrictTo.Scope.LIBRARY)
    private AlertDialog dialog = null;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        if (dialog == null) {
            setShowsDialog(false);
        }
        return dialog;
    }

    @Override
    public void onStart() {
        if (dialog == null) {
            dismissAllowingStateLoss();
        }
        super.onStart();
    }

    public final void show(@NonNull FragmentActivity activity) {
        show(activity.getSupportFragmentManager());
    }

    public final void show(@NonNull FragmentManager fragmentManager) {
        super.show(fragmentManager, getClass().getSimpleName());
    }

    public final void show(@NonNull Fragment fragment) {
        show(fragment.requireFragmentManager());
    }

    public final void show(@NonNull FragmentTransaction fragmentTransaction) {
        super.show(fragmentTransaction, getClass().getSimpleName());
    }

    /**
     * 빌더 클래스
     *
     * @see AlertDialog.Builder
     */
    public static final class Builder {
        private final AlertDialog.Builder internalBuilder;

        public Builder(@NonNull Context context) {
            internalBuilder = new AlertDialog.Builder(context);
        }

        public Builder(@NonNull Context context, @StyleRes int themeResId) {
            internalBuilder = new AlertDialog.Builder(context, themeResId);
        }

        public Builder setTitle(@StringRes int titleId) {
            internalBuilder.setTitle(titleId);
            return this;
        }

        public Builder setTitle(@Nullable CharSequence title) {
            internalBuilder.setTitle(title);
            return this;
        }

        public Builder setCustomTitle(@Nullable View customTitleView) {
            internalBuilder.setCustomTitle(customTitleView);
            return this;
        }

        public Builder setMessage(@StringRes int messageId) {
            internalBuilder.setMessage(messageId);
            return this;
        }

        public Builder setMessage(@Nullable CharSequence message) {
            internalBuilder.setMessage(message);
            return this;
        }

        public Builder setIcon(@DrawableRes int iconId) {
            internalBuilder.setIcon(iconId);
            return this;
        }

        public Builder setIcon(@Nullable Drawable icon) {
            internalBuilder.setIcon(icon);
            return this;
        }

        public Builder setIconAttribute(@AttrRes int attrId) {
            internalBuilder.setIconAttribute(attrId);
            return this;
        }

        public Builder setPositiveButton(
                @StringRes int textId,
                DialogInterface.OnClickListener listener
        ) {
            internalBuilder.setPositiveButton(textId, listener);
            return this;
        }

        public Builder setPositiveButton(
                CharSequence text,
                DialogInterface.OnClickListener listener
        ) {
            internalBuilder.setPositiveButton(text, listener);
            return this;
        }

        public Builder setPositiveButtonIcon(Drawable icon) {
            internalBuilder.setPositiveButtonIcon(icon);
            return this;
        }

        public Builder setNegativeButton(
                @StringRes int textId,
                DialogInterface.OnClickListener listener
        ) {
            internalBuilder.setNegativeButton(textId, listener);
            return this;
        }

        public Builder setNegativeButton(
                CharSequence text,
                DialogInterface.OnClickListener listener
        ) {
            internalBuilder.setNegativeButton(text, listener);
            return this;
        }

        public Builder setNegativeButtonIcon(Drawable icon) {
            internalBuilder.setNegativeButtonIcon(icon);
            return this;
        }

        public Builder setNeutralButton(
                @StringRes int textId,
                DialogInterface.OnClickListener listener
        ) {
            internalBuilder.setNeutralButton(textId, listener);
            return this;
        }

        public Builder setNeutralButton(
                CharSequence text,
                DialogInterface.OnClickListener listener
        ) {
            internalBuilder.setNeutralButton(text, listener);
            return this;
        }

        public Builder setNeutralButtonIcon(Drawable icon) {
            internalBuilder.setNeutralButtonIcon(icon);
            return this;
        }

        public Builder setCancelable(boolean cancelable) {
            internalBuilder.setCancelable(cancelable);
            return this;
        }

        public Builder setOnCancelListener(
                DialogInterface.OnCancelListener onCancelListener
        ) {
            internalBuilder.setOnCancelListener(onCancelListener);
            return this;
        }

        public Builder setOnDismissListener(
                DialogInterface.OnDismissListener onDismissListener
        ) {
            internalBuilder.setOnDismissListener(onDismissListener);
            return this;
        }

        public Builder setOnKeyListener(DialogInterface.OnKeyListener onKeyListener) {
            internalBuilder.setOnKeyListener(onKeyListener);
            return this;
        }

        public Builder setItems(
                @ArrayRes int itemsId,
                DialogInterface.OnClickListener listener
        ) {
            internalBuilder.setItems(itemsId, listener);
            return this;
        }

        public Builder setItems(
                CharSequence[] items,
                DialogInterface.OnClickListener listener
        ) {
            internalBuilder.setItems(items, listener);
            return this;
        }

        public Builder setAdapter(
                ListAdapter adapter,
                DialogInterface.OnClickListener listener
        ) {
            internalBuilder.setAdapter(adapter, listener);
            return this;
        }

        public Builder setCursor(Cursor cursor,
                                 DialogInterface.OnClickListener listener,
                                 String labelColumn) {
            internalBuilder.setCursor(cursor, listener, labelColumn);
            return this;
        }

        public Builder setMultiChoiceItems(
                @ArrayRes int itemsId,
                boolean[] checkedItems,
                DialogInterface.OnMultiChoiceClickListener listener
        ) {
            internalBuilder.setMultiChoiceItems(itemsId, checkedItems, listener);
            return this;
        }

        public Builder setMultiChoiceItems(
                CharSequence[] items,
                boolean[] checkedItems,
                DialogInterface.OnMultiChoiceClickListener listener
        ) {
            internalBuilder.setMultiChoiceItems(items, checkedItems, listener);
            return this;
        }

        public Builder setMultiChoiceItems(
                Cursor cursor,
                String isCheckedColumn,
                String labelColumn,
                DialogInterface.OnMultiChoiceClickListener listener
        ) {
            internalBuilder.setMultiChoiceItems(cursor, isCheckedColumn, labelColumn, listener);
            return this;
        }

        public Builder setSingleChoiceItems(
                @ArrayRes int itemsId,
                int checkedItem,
                DialogInterface.OnClickListener listener
        ) {
            internalBuilder.setSingleChoiceItems(itemsId, checkedItem, listener);
            return this;
        }

        public Builder setSingleChoiceItems(
                Cursor cursor,
                int checkedItem,
                String labelColumn,
                DialogInterface.OnClickListener listener
        ) {
            internalBuilder.setSingleChoiceItems(cursor, checkedItem, labelColumn, listener);
            return this;
        }

        public Builder setSingleChoiceItems(
                CharSequence[] items,
                int checkedItem,
                DialogInterface.OnClickListener listener
        ) {
            internalBuilder.setSingleChoiceItems(items, checkedItem, listener);
            return this;
        }

        public Builder setSingleChoiceItems(
                ListAdapter adapter,
                int checkedItem,
                DialogInterface.OnClickListener listener
        ) {
            internalBuilder.setSingleChoiceItems(adapter, checkedItem, listener);
            return this;
        }

        public Builder setOnItemSelectedListener(AdapterView.OnItemSelectedListener l) {
            internalBuilder.setOnItemSelectedListener(l);
            return this;
        }

        public Builder setView(int layoutResId) {
            internalBuilder.setView(layoutResId);
            return this;
        }

        public Builder setView(View view) {
            internalBuilder.setView(view);
            return this;
        }

        @NonNull
        public AlertDialogFragment create() {
            AlertDialogFragment alertDialogFragment = new AlertDialogFragment();
            alertDialogFragment.dialog = internalBuilder.create();
            return alertDialogFragment;
        }

        public void show(@NonNull FragmentActivity activity) {
            AlertDialogFragment dialogFragment = create();
            dialogFragment.show(activity.getSupportFragmentManager());
        }

        public void show(@NonNull FragmentManager fragmentManager) {
            AlertDialogFragment dialogFragment = create();
            dialogFragment.show(fragmentManager, dialogFragment.getClass().getSimpleName());
        }

        public void show(@NonNull Fragment fragment) {
            AlertDialogFragment dialogFragment = create();
            dialogFragment.show(fragment.requireFragmentManager());
        }

        public void show(@NonNull FragmentTransaction fragmentTransaction) {
            AlertDialogFragment dialogFragment = create();
            dialogFragment.show(fragmentTransaction, dialogFragment.getClass().getSimpleName());
        }

        public void show(@NonNull FragmentActivity activity, @NonNull String tag) {
            AlertDialogFragment dialogFragment = create();
            dialogFragment.show(activity.getSupportFragmentManager(), tag);
        }

        public void show(@NonNull FragmentManager fragmentManager, @NonNull String tag) {
            AlertDialogFragment dialogFragment = create();
            dialogFragment.show(fragmentManager, tag);
        }

        public void show(@NonNull Fragment fragment, @NonNull String tag) {
            AlertDialogFragment dialogFragment = create();
            dialogFragment.show(fragment.requireFragmentManager(), tag);
        }

        public void show(@NonNull FragmentTransaction fragmentTransaction,
                         @NonNull String tag) {
            AlertDialogFragment dialogFragment = create();
            dialogFragment.show(fragmentTransaction, tag);
        }
    }
}
