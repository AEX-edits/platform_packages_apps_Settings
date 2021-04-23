/*
 * Copyright (C) 2019 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.accessibility;

import static com.android.settings.accessibility.ItemInfoArrayAdapter.ItemInfo;

import android.app.Dialog;
import android.app.settings.SettingsEnums;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ImageSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.IntDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.android.settings.R;
import com.android.settings.core.SubSettingLauncher;
import com.android.settings.utils.AnnotationSpan;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.List;


/**
 * Utility class for creating the edit dialog.
 */
public class AccessibilityEditDialogUtils {

    /**
     * IntDef enum for dialog type that indicates different dialog for user to choose the shortcut
     * type.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
         DialogType.EDIT_SHORTCUT_GENERIC,
         DialogType.EDIT_SHORTCUT_MAGNIFICATION,
         DialogType.EDIT_MAGNIFICATION_SWITCH_SHORTCUT,
    })

    private @interface DialogType {
        int EDIT_SHORTCUT_GENERIC = 0;
        int EDIT_SHORTCUT_MAGNIFICATION = 1;
        int EDIT_MAGNIFICATION_SWITCH_SHORTCUT = 2;
    }

    /**
     * Method to show the edit shortcut dialog.
     *
     * @param context A valid context
     * @param dialogTitle The title of edit shortcut dialog
     * @param listener The listener to determine the action of edit shortcut dialog
     * @return A edit shortcut dialog for showing
     */
    public static AlertDialog showEditShortcutDialog(Context context, CharSequence dialogTitle,
            DialogInterface.OnClickListener listener) {
        final AlertDialog alertDialog = createDialog(context, DialogType.EDIT_SHORTCUT_GENERIC,
                dialogTitle, listener);
        alertDialog.show();
        setScrollIndicators(alertDialog);
        return alertDialog;
    }

    /**
     * Method to show the edit shortcut dialog in Magnification.
     *
     * @param context A valid context
     * @param dialogTitle The title of edit shortcut dialog
     * @param listener The listener to determine the action of edit shortcut dialog
     * @return A edit shortcut dialog for showing in Magnification
     */
    public static AlertDialog showMagnificationEditShortcutDialog(Context context,
            CharSequence dialogTitle, DialogInterface.OnClickListener listener) {
        final AlertDialog alertDialog = createDialog(context,
                DialogType.EDIT_SHORTCUT_MAGNIFICATION, dialogTitle, listener);
        alertDialog.show();
        setScrollIndicators(alertDialog);
        return alertDialog;
    }

    /**
     * Method to show the magnification edit shortcut dialog in Magnification.
     *
     * @param context A valid context
     * @param dialogTitle The title of magnify edit shortcut dialog
     * @param positiveBtnListener The positive button listener
     * @return A magnification edit shortcut dialog in Magnification
     */
    public static Dialog createMagnificationSwitchShortcutDialog(Context context,
            CharSequence dialogTitle, CustomButtonsClickListener positiveBtnListener) {
        final View contentView = createSwitchShortcutDialogContentView(context);
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(contentView)
                .setTitle(dialogTitle)
                .create();
        setCustomButtonsClickListener(alertDialog, contentView,
                positiveBtnListener, /* negativeBtnListener= */ null);
        setScrollIndicators(contentView);
        return alertDialog;
    }

    private static AlertDialog createDialog(Context context, int dialogType,
            CharSequence dialogTitle, DialogInterface.OnClickListener listener) {

        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(createEditDialogContentView(context, dialogType))
                .setTitle(dialogTitle)
                .setPositiveButton(R.string.save, listener)
                .setNegativeButton(R.string.cancel,
                        (DialogInterface dialog, int which) -> dialog.dismiss())
                .create();

        return alertDialog;
    }

    /**
     * Sets the scroll indicators for dialog view. The indicators appears while content view is
     * out of vision for vertical scrolling.
     */
    private static void setScrollIndicators(AlertDialog dialog) {
        final ScrollView scrollView = dialog.findViewById(R.id.container_layout);
        setScrollIndicators(scrollView);
    }

    /**
     * Sets the scroll indicators for dialog view. The indicators appear while content view is
     * out of vision for vertical scrolling.
     *
     * @param view The view contains customized dialog content. Usually it is {@link ScrollView} or
     *             {@link AbsListView}
     */
    private static void setScrollIndicators(@NonNull View view) {
        view.setScrollIndicators(
                View.SCROLL_INDICATOR_TOP | View.SCROLL_INDICATOR_BOTTOM,
                View.SCROLL_INDICATOR_TOP | View.SCROLL_INDICATOR_BOTTOM);
    }


    interface CustomButtonsClickListener {
        void onClick(@CustomButton int which);
    }

    /**
     * Annotation for customized dialog button type.
     */
    @Retention(RetentionPolicy.SOURCE)
    @IntDef({
            CustomButton.POSITIVE,
            CustomButton.NEGATIVE,
    })

    public @interface CustomButton {
        int POSITIVE = 1;
        int NEGATIVE = 2;
    }

    private static void setCustomButtonsClickListener(Dialog dialog, View contentView,
            CustomButtonsClickListener positiveBtnListener,
            CustomButtonsClickListener negativeBtnListener) {
        final Button positiveButton = contentView.findViewById(
                R.id.custom_positive_button);
        final Button negativeButton = contentView.findViewById(
                R.id.custom_negative_button);

        if (positiveButton != null) {
            positiveButton.setOnClickListener(v -> {
                if (positiveBtnListener != null) {
                    positiveBtnListener.onClick(CustomButton.POSITIVE);
                }
                dialog.dismiss();
            });
        }

        if (negativeButton != null) {
            negativeButton.setOnClickListener(v -> {
                if (negativeBtnListener != null) {
                    negativeBtnListener.onClick(CustomButton.NEGATIVE);
                }
                dialog.dismiss();
            });
        }
    }

    private static View createSwitchShortcutDialogContentView(Context context) {
        return createEditDialogContentView(context, DialogType.EDIT_MAGNIFICATION_SWITCH_SHORTCUT);
    }

    /**
     * Get a content View for the edit shortcut dialog.
     *
     * @param context A valid context
     * @param dialogType The type of edit shortcut dialog
     * @return A content view suitable for viewing
     */
    private static View createEditDialogContentView(Context context, int dialogType) {
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(
                Context.LAYOUT_INFLATER_SERVICE);

        View contentView = null;

        switch (dialogType) {
            case DialogType.EDIT_SHORTCUT_GENERIC:
                contentView = inflater.inflate(
                        R.layout.accessibility_edit_shortcut, null);
                initSoftwareShortcut(context, contentView);
                initHardwareShortcut(context, contentView);
                break;
            case DialogType.EDIT_SHORTCUT_MAGNIFICATION:
                contentView = inflater.inflate(
                        R.layout.accessibility_edit_shortcut_magnification, null);
                initSoftwareShortcut(context, contentView);
                initHardwareShortcut(context, contentView);
                initMagnifyShortcut(context, contentView);
                initAdvancedWidget(contentView);
                break;
            case DialogType.EDIT_MAGNIFICATION_SWITCH_SHORTCUT:
                contentView = inflater.inflate(
                        R.layout.accessibility_edit_magnification_shortcut, null);
                final ImageView image = contentView.findViewById(R.id.image);
                image.setImageResource(retrieveSoftwareShortcutImageResId(context));
                break;
            default:
                throw new IllegalArgumentException();
        }

        return contentView;
    }

    private static void setupShortcutWidget(View view, CharSequence titleText,
            CharSequence summaryText, int imageResId) {
        final CheckBox checkBox = view.findViewById(R.id.checkbox);
        checkBox.setText(titleText);
        final TextView summary = view.findViewById(R.id.summary);
        if (TextUtils.isEmpty(summaryText)) {
            summary.setVisibility(View.GONE);
        } else {
            summary.setText(summaryText);
            summary.setMovementMethod(LinkMovementMethod.getInstance());
            summary.setFocusable(false);
        }
        final ImageView image = view.findViewById(R.id.image);
        image.setImageResource(imageResId);
    }

    private static void initSoftwareShortcut(Context context, View view) {
        final View dialogView = view.findViewById(R.id.software_shortcut);
        final CharSequence title = context.getText(
                R.string.accessibility_shortcut_edit_dialog_title_software);
        final TextView summary = dialogView.findViewById(R.id.summary);
        final int lineHeight = summary.getLineHeight();

        setupShortcutWidget(dialogView, title, retrieveSummary(context, lineHeight),
                retrieveSoftwareShortcutImageResId(context));
    }

    private static void initHardwareShortcut(Context context, View view) {
        final View dialogView = view.findViewById(R.id.hardware_shortcut);
        final CharSequence title = context.getText(
                R.string.accessibility_shortcut_edit_dialog_title_hardware);
        final CharSequence summary = context.getText(
                R.string.accessibility_shortcut_edit_dialog_summary_hardware);
        setupShortcutWidget(dialogView, title, summary,
                R.drawable.accessibility_shortcut_type_hardware);
        // TODO(b/142531156): Use vector drawable instead of temporal png file to avoid distorted.
    }

    private static void initMagnifyShortcut(Context context, View view) {
        final View dialogView = view.findViewById(R.id.triple_tap_shortcut);
        final CharSequence title = context.getText(
                R.string.accessibility_shortcut_edit_dialog_title_triple_tap);
        final CharSequence summary = context.getText(
                R.string.accessibility_shortcut_edit_dialog_summary_triple_tap);
        setupShortcutWidget(dialogView, title, summary,
                R.drawable.accessibility_shortcut_type_triple_tap);
        // TODO(b/142531156): Use vector drawable instead of temporal png file to avoid distorted.
    }

    private static void initAdvancedWidget(View view) {
        final LinearLayout advanced = view.findViewById(R.id.advanced_shortcut);
        final View tripleTap = view.findViewById(R.id.triple_tap_shortcut);
        advanced.setOnClickListener((View v) -> {
            advanced.setVisibility(View.GONE);
            tripleTap.setVisibility(View.VISIBLE);
        });
    }

    private static CharSequence retrieveSummary(Context context, int lineHeight) {
        final SpannableStringBuilder sb = new SpannableStringBuilder();
        if (!AccessibilityUtil.isFloatingMenuEnabled(context)) {
            sb.append(getSummaryStringWithIcon(context, lineHeight));
            sb.append("\n\n");
        }
        sb.append(getCustomizeAccessibilityButtonLink(context));
        return sb;
    }

    private static int retrieveSoftwareShortcutImageResId(Context context) {
        return AccessibilityUtil.isFloatingMenuEnabled(context)
                ? R.drawable.accessibility_shortcut_type_software_floating
                : R.drawable.accessibility_shortcut_type_software;
    }

    private static CharSequence getCustomizeAccessibilityButtonLink(Context context) {
        final View.OnClickListener linkListener = v -> new SubSettingLauncher(context)
                .setDestination(AccessibilityButtonFragment.class.getName())
                .setSourceMetricsCategory(
                        SettingsEnums.SWITCH_SHORTCUT_DIALOG_ACCESSIBILITY_BUTTON_SETTINGS)
                .launch();
        final AnnotationSpan.LinkInfo linkInfo = new AnnotationSpan.LinkInfo(
                AnnotationSpan.LinkInfo.DEFAULT_ANNOTATION, linkListener);

        return AnnotationSpan.linkify(context.getText(
                R.string.accessibility_shortcut_edit_dialog_summary_software_floating), linkInfo);
    }

    private static SpannableString getSummaryStringWithIcon(Context context, int lineHeight) {
        final String summary = context
                .getString(R.string.accessibility_shortcut_edit_dialog_summary_software);
        final SpannableString spannableMessage = SpannableString.valueOf(summary);

        // Icon
        final int indexIconStart = summary.indexOf("%s");
        final int indexIconEnd = indexIconStart + 2;
        final Drawable icon = context.getDrawable(R.drawable.ic_accessibility_new);
        final ImageSpan imageSpan = new ImageSpan(icon);
        imageSpan.setContentDescription("");
        icon.setBounds(0, 0, lineHeight, lineHeight);
        spannableMessage.setSpan(
                imageSpan, indexIconStart, indexIconEnd,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return spannableMessage;
    }

    /**
     * Returns the color associated with the specified attribute in the context's theme.
     */
    @ColorInt
    private static int getThemeAttrColor(final Context context, final int attributeColor) {
        final int colorResId = getAttrResourceId(context, attributeColor);
        return ContextCompat.getColor(context, colorResId);
    }

    /**
     * Returns the identifier of the resolved resource assigned to the given attribute.
     */
    private static int getAttrResourceId(final Context context, final int attributeColor) {
        final int[] attrs = {attributeColor};
        final TypedArray typedArray = context.obtainStyledAttributes(attrs);
        final int colorResId = typedArray.getResourceId(0, 0);
        typedArray.recycle();
        return colorResId;
    }

    /**
     * Creates a dialog with the given view.
     *
     * @param context A valid context
     * @param dialogTitle The title of the dialog
     * @param customView The customized view
     * @param listener This listener will be invoked when the positive button in the dialog is
     *                 clicked
     * @return the {@link Dialog} with the given view
     */
    public static Dialog createCustomDialog(Context context, CharSequence dialogTitle,
            View customView, DialogInterface.OnClickListener listener) {
        final AlertDialog alertDialog = new AlertDialog.Builder(context)
                .setView(customView)
                .setTitle(dialogTitle)
                .setCancelable(true)
                .setPositiveButton(R.string.save, listener)
                .setNegativeButton(R.string.cancel, null)
                .create();
        if (customView instanceof ScrollView || customView instanceof AbsListView) {
            setScrollIndicators(customView);
        }
        return alertDialog;
    }

    /**
     * Creates a single choice {@link ListView} with given {@link ItemInfo} list.
     *
     * @param context A context.
     * @param itemInfoList A {@link ItemInfo} list.
     * @param itemListener The listener will be invoked when the item is clicked.
     */
    @NonNull
    public static ListView createSingleChoiceListView(@NonNull Context context,
            @NonNull List<? extends ItemInfo> itemInfoList,
            @Nullable AdapterView.OnItemClickListener itemListener) {
        final ListView list = new ListView(context);
        // Set an id to save its state.
        list.setId(android.R.id.list);
        list.setDivider(/* divider= */ null);
        list.setChoiceMode(ListView.CHOICE_MODE_SINGLE);
        final ItemInfoArrayAdapter
                adapter = new ItemInfoArrayAdapter(context, itemInfoList);
        list.setAdapter(adapter);
        list.setOnItemClickListener(itemListener);
        return list;
    }
}
