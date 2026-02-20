package com.antoniokoman.basics.screens.categories;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.antoniokoman.basics.R;
import com.antoniokoman.basics.app.AppTheme;
import com.antoniokoman.basics.fsm.CatCreateState;
import com.antoniokoman.basics.fsm.ScreenState;
import com.antoniokoman.basics.repository.Repository;
import com.antoniokoman.basics.screens.base.AppBarView;
import com.antoniokoman.basics.screens.base.BaseContentScreen;
import com.antoniokoman.basics.screens.base.OutlinedTextFieldView;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.bottomsheet.BottomSheetDialog;

public class CategoryCreateScreen extends BaseContentScreen {

    private boolean isValid = false;
    private String nameText = "";
    private String descText = "";
    private int selectedColor = 0xFFF44336;
    private int selectedIconResId = R.drawable.ic_category_default;
    private final Repository repo = Repository.getInstance();

    @Override
    protected AppBarView createAppBar(Context context) {
        AppBarView appBar = new AppBarView(context);
        appBar.setTitle(context.getString(R.string.cat_create_title));
        appBar.setNavigationIcon(R.drawable.outline_arrow_back, v -> onBackPressed());
        return appBar;
    }

    @Override
    protected void onBackPressed() {
        if (listener != null) listener.onScreenStateChanged(CatCreateState.PR_BACK);
    }

    @Override
    protected void onRenderContent(FrameLayout contentContainer) {
        Context context = contentContainer.getContext();

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.TOP);
        FrameLayout.LayoutParams rootLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        contentContainer.addView(container, rootLp);

        // Глушилка фокуса
        View focusGuard = new View(context);
        focusGuard.setFocusable(true);
        focusGuard.setFocusableInTouchMode(true);
        container.addView(focusGuard, new LinearLayout.LayoutParams(0, 0));
        focusGuard.requestFocus();

        // ScrollView
        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        );
        container.addView(scrollView, scrollLp);

        // Форма
        LinearLayout form = new LinearLayout(context);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setGravity(Gravity.START);

        int horizontalPadding = AppTheme.dimenPx(context, R.dimen.cat_create_horizontal_padding);
        int topPadding = AppTheme.dimenPx(
                context,
                isExpanded(context)
                        ? R.dimen.cat_create_label_top_padding_tablet
                        : R.dimen.cat_create_label_top_padding_phone
        );
        form.setPadding(horizontalPadding, topPadding, horizontalPadding, 0);

        LinearLayout.LayoutParams formLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        if (isExpanded(context)) {
            int maxWidthPx = AppTheme.dimenPx(context, R.dimen.form_max_width_expanded);
            formLp.width = maxWidthPx;
            formLp.gravity = Gravity.TOP | Gravity.CENTER_HORIZONTAL;
        }
        scrollView.addView(form, formLp);

        int betweenFields = AppTheme.dimenPx(context, R.dimen.spacing_large);

        // 1. Название
        OutlinedTextFieldView nameField = new OutlinedTextFieldView(context);
        nameField.setLabel(R.string.cat_create_name_label);
        nameField.setPlaceholder(R.string.cat_create_name_hint);

        form.addView(nameField, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        ((LinearLayout.LayoutParams) nameField.getLayoutParams()).bottomMargin = betweenFields;

        // 2. Описание
        OutlinedTextFieldView descField = new OutlinedTextFieldView(context);
        descField.setLabel(R.string.cat_create_description_label);
        descField.setPlaceholder(R.string.cat_create_description_hint);

        descField.getEdit().setSingleLine(false);
        descField.getEdit().setMinLines(3);
        descField.getEdit().setMaxLines(5);
        descField.getEdit().setImeOptions(EditorInfo.IME_ACTION_DONE);

        form.addView(descField, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        ((LinearLayout.LayoutParams) descField.getLayoutParams()).bottomMargin = betweenFields;

        // 3. Иконка (readonly + выбор)
        OutlinedTextFieldView iconField = new OutlinedTextFieldView(context);
        iconField.setLabel(R.string.cat_create_icon_title);
        iconField.setPlaceholder(R.string.cat_create_icon_hint);

        iconField.getEdit().setFocusable(false);
        iconField.getEdit().setFocusableInTouchMode(false);
        iconField.getEdit().setCursorVisible(false);
        iconField.getEdit().setKeyListener(null);

        View.OnClickListener iconClickListener = v ->
                showIconPickerBottomSheet(context, iconField);

        iconField.getLayout().setStartIconDrawable(
                context.getDrawable(selectedIconResId)
        );
        iconField.getLayout().setStartIconContentDescription(
                context.getString(R.string.cat_create_icon_preview_default)
        );
        iconField.getLayout().setStartIconOnClickListener(iconClickListener);
        iconField.getLayout().setStartIconTintList(null);

        iconField.getLayout().setClickable(true);
        iconField.getEdit().setClickable(true);
        iconField.getLayout().setOnClickListener(iconClickListener);
        iconField.getEdit().setOnClickListener(iconClickListener);

        form.addView(iconField, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        ((LinearLayout.LayoutParams) iconField.getLayoutParams()).bottomMargin = betweenFields;

        // 4. Палитра с собственной рамкой (без TextInputLayout)
        LinearLayout colorBox = new LinearLayout(context);
        colorBox.setOrientation(LinearLayout.HORIZONTAL);
        colorBox.setGravity(Gravity.CENTER_VERTICAL);

        int boxPaddingH = AppTheme.dimenPx(context, R.dimen.spacing_medium);
        int boxPaddingV = AppTheme.dimenPx(context, R.dimen.spacing_small);
        colorBox.setPadding(boxPaddingH, boxPaddingV, boxPaddingH, boxPaddingV);

        Drawable boxBg = AppTheme.roundedBox(
                AppTheme.backgroundColor(context),
                AppTheme.outlineColor(context),
                AppTheme.dimenPx(context, R.dimen.textfield_stroke_width),
                AppTheme.dimenPx(context, R.dimen.textfield_corner_radius)
        );
        colorBox.setBackground(boxBg);

        HorizontalScrollView hsv = new HorizontalScrollView(context);
        hsv.setHorizontalScrollBarEnabled(false);

        LinearLayout colorsRow = new LinearLayout(context);
        colorsRow.setOrientation(LinearLayout.HORIZONTAL);

        int colorItemMargin = AppTheme.dimenPx(context, R.dimen.spacing_small);
        int colorItemSize = AppTheme.dimenPx(context, R.dimen.icon_size_medium);

        int[] colors = new int[] {
                0xFFF44336, // Red 500
                0xFFE91E63, // Pink 500
                0xFF9C27B0, // Purple 500
                0xFF673AB7, // Deep Purple 500
                0xFF3F51B5, // Indigo 500
                0xFF2196F3, // Blue 500
                0xFF03A9F4, // Light Blue 500
                0xFF00BCD4, // Cyan 500
                0xFF009688, // Teal 500
                0xFF4CAF50, // Green 500
                0xFF8BC34A, // Light Green 500
                0xFFCDDC39, // Lime 500
                0xFFFFEB3B, // Yellow 500
                0xFFFFC107, // Amber 500
                0xFFFF9800, // Orange 500
                0xFFFF5722, // Deep Orange 500
                0xFF795548, // Brown 500
                0xFF9E9E9E, // Grey 500
                0xFF607D8B, // Blue Grey 500
                0xFF000000  // Black
        };

        final ImageView[] checkViews = new ImageView[colors.length];

        for (int i = 0; i < colors.length; i++) {
            int color = colors[i];

            FrameLayout dotContainer = new FrameLayout(context);
            LinearLayout.LayoutParams lpDot = new LinearLayout.LayoutParams(
                    colorItemSize,
                    colorItemSize
            );
            lpDot.leftMargin = colorItemMargin;
            lpDot.rightMargin = colorItemMargin;
            dotContainer.setLayoutParams(lpDot);

            View dot = new View(context);
            FrameLayout.LayoutParams dotLp = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
            );
            dotLp.gravity = Gravity.CENTER;

            GradientDrawable bg = new GradientDrawable();
            bg.setShape(GradientDrawable.OVAL);
            bg.setColor(color);

            dot.setBackground(bg);
            dot.setContentDescription(context.getString(R.string.cat_create_color_item_cd));

            dotContainer.addView(dot, dotLp);

            ImageView check = new ImageView(context);
            check.setImageResource(R.drawable.ic_check);
            check.setColorFilter(AppTheme.backgroundColor(context));
            FrameLayout.LayoutParams checkLp = new FrameLayout.LayoutParams(
                    colorItemSize / 2,
                    colorItemSize / 2
            );
            checkLp.gravity = Gravity.CENTER;
            check.setLayoutParams(checkLp);

            // галочка на выбранном цвете
            check.setVisibility(colors[i] == selectedColor ? View.VISIBLE : View.GONE);

            dotContainer.addView(check);
            checkViews[i] = check;

            int index = i;
            dotContainer.setOnClickListener(v -> {
                selectedColor = colors[index];
                for (int j = 0; j < checkViews.length; j++) {
                    checkViews[j].setVisibility(j == index ? View.VISIBLE : View.GONE);
                }
            });

            colorsRow.addView(dotContainer);
        }

        hsv.addView(colorsRow, new HorizontalScrollView.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        colorBox.addView(hsv, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        form.addView(colorBox, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
        ((LinearLayout.LayoutParams) colorBox.getLayoutParams()).bottomMargin = betweenFields;

        // 5. Кнопка "СОХРАНИТЬ"
        TextView saveButton = new TextView(context);
        saveButton.setText(R.string.cat_create_action_save);
        saveButton.setAllCaps(true);
        saveButton.setGravity(Gravity.CENTER);

        int saveButtonHeight = AppTheme.dimenPx(context, R.dimen.button_height_primary);
        Drawable saveBg = AppTheme.roundedBox(
                ContextCompat.getColor(context, R.color.app_primary_button),
                0,
                0,
                AppTheme.dimenPx(context, R.dimen.textfield_corner_radius)
        );
        saveButton.setBackground(saveBg);
        saveButton.setTextColor(AppTheme.fabIconTintColor(context));

        LinearLayout.LayoutParams saveLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                saveButtonHeight
        );
        saveLp.bottomMargin = betweenFields;
        form.addView(saveButton, saveLp);

        // Восстановление текстов и валидности
        nameField.setText(nameText);
        isValid = !nameText.trim().isEmpty();
        saveButton.setEnabled(isValid);
        saveButton.setAlpha(isValid ? 1f : 0.4f);

        descField.setText(descText);

        // Watcher для имени
        nameField.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                nameText = s.toString();
                isValid = !nameText.trim().isEmpty();
                saveButton.setEnabled(isValid);
                saveButton.setAlpha(isValid ? 1f : 0.4f);
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Watcher для описания
        descField.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                descText = s.toString();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        // Клик по кнопке
        saveButton.setOnClickListener(v -> {
            if (!isValid) return;

            String name = nameText.trim();
            String description = descText.trim();

            // Цвет: int -> "#RRGGBB"
            String colorString = String.format("#%06X", (0xFFFFFF & selectedColor)); // без альфы[web:104][web:109]

            // Иконка: entryName ресурса
            String iconEntryName = context.getResources()
                    .getResourceEntryName(selectedIconResId); // "icons_architecture"[web:112][web:116]

            repo.addCategory(
                    name,
                    description,
                    colorString,
                    iconEntryName
            );

            if (listener != null) {
                listener.onScreenStateChanged(CatCreateState.PR_CRE);
            }
        });
    }

    private void showIconPickerBottomSheet(Context context, OutlinedTextFieldView iconField) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        Context themed = new ContextThemeWrapper(context, R.style.Theme_Basics);

        ScrollView scroll = new ScrollView(themed);
        scroll.setFillViewport(true);

        LinearLayout root = new LinearLayout(themed);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(
                AppTheme.dimenPx(context, R.dimen.spacing_large),
                AppTheme.dimenPx(context, R.dimen.spacing_large),
                AppTheme.dimenPx(context, R.dimen.spacing_large),
                AppTheme.dimenPx(context, R.dimen.spacing_large)
        );

        TextView title = new TextView(themed);
        title.setText(R.string.cat_create_icon_picker_title);
        title.setTextColor(AppTheme.textMainColor(context));
        title.setTextSize(18);
        root.addView(title);

        View spacer = new View(themed);
        LinearLayout.LayoutParams spacerLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                AppTheme.dimenPx(context, R.dimen.spacing_medium)
        );
        root.addView(spacer, spacerLp);

        int columnCount = 4;
        GridLayout grid = new GridLayout(themed);
        grid.setColumnCount(columnCount);

        int[] iconResIds = getCategoryIconResIds();

        int itemSize = AppTheme.dimenPx(context, R.dimen.icon_size_big);
        int itemMargin = AppTheme.dimenPx(context, R.dimen.spacing_small);

        for (int resId : iconResIds) {
            ImageButton btn = new ImageButton(themed);
            GridLayout.LayoutParams glp = new GridLayout.LayoutParams();
            glp.width = itemSize;
            glp.height = itemSize;
            glp.setMargins(itemMargin, itemMargin, itemMargin, itemMargin);
            btn.setLayoutParams(glp);

            btn.setImageResource(resId);
            btn.setBackground(AppTheme.circleBackground(context));
            btn.setScaleType(ImageButton.ScaleType.CENTER_INSIDE);
            btn.setContentDescription(
                    context.getString(R.string.cat_create_icon_item_cd)
            );

            btn.setOnClickListener(v -> {
                selectedIconResId = resId;

                iconField.getLayout().setStartIconDrawable(
                        context.getDrawable(resId)
                );
                iconField.getLayout().setStartIconContentDescription(
                        context.getString(R.string.cat_create_icon_preview_selected)
                );
                iconField.getLayout().setStartIconOnClickListener(v2 ->
                        showIconPickerBottomSheet(context, iconField)
                );
                iconField.getLayout().setStartIconTintList(null);
                dialog.dismiss();
            });

            grid.addView(btn);
        }

        root.addView(grid, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        TextView customIconButton = new TextView(themed);
        customIconButton.setText(R.string.cat_create_icon_custom);
        customIconButton.setTextColor(AppTheme.accentSoftColor(context));
        customIconButton.setPadding(
                0,
                AppTheme.dimenPx(context, R.dimen.spacing_large),
                0,
                0
        );
        customIconButton.setClickable(true);
        customIconButton.setFocusable(true);
        customIconButton.setOnClickListener(v -> {
            // TODO: открытие галереи / SAF
            dialog.dismiss();
        });
        root.addView(customIconButton);

        scroll.addView(root, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        dialog.setContentView(scroll);

        dialog.setOnShowListener(d -> {
            FrameLayout bottomSheet = dialog.findViewById(
                    com.google.android.material.R.id.design_bottom_sheet
            );
            if (bottomSheet != null) {
                BottomSheetBehavior<FrameLayout> behavior =
                        BottomSheetBehavior.from(bottomSheet);
                behavior.setState(BottomSheetBehavior.STATE_EXPANDED);
                behavior.setDraggable(false);
            }
        });

        dialog.show();
    }

    private int[] getCategoryIconResIds() {
        return new int[] {
                R.drawable.icons_architecture,
                R.drawable.icons_construction,
                R.drawable.icons_experiment,
                R.drawable.icons_school,
                R.drawable.icons_waves,
                R.drawable.icons_engineering,
                R.drawable.icons_toys,
                R.drawable.icons_water,
                R.drawable.icons_surfing,
                R.drawable.icons_kayaking,
                R.drawable.icons_sports_basketball,
                R.drawable.icons_skateboarding,
                R.drawable.icons_sports_baseball,
                R.drawable.icons_sports_esports,
                R.drawable.icons_sports_kabaddi,
                R.drawable.icons_volunteer_activism,
                R.drawable.icons_personal_injury,
                R.drawable.icons_sports_soccer,
                R.drawable.icons_sports_volleyball,
                R.drawable.icons_self_improvement,
                R.drawable.icons_sports_tennis,
                R.drawable.icons_rewarded_ads,
                R.drawable.icons_sports_football,
                R.drawable.icons_downhill_skiing,
                R.drawable.icons_biotech,
                R.drawable.icons_sports_motorsports,
                R.drawable.icons_sports_handball,
                R.drawable.icons_sports_cricket,
                R.drawable.icons_air,
                R.drawable.icons_cake,
                R.drawable.icons_hiking,
                R.drawable.icons_trophy,
                R.drawable.icons_piano,
                R.drawable.icons_how_to_vote,
                R.drawable.icons_campaign,
                R.drawable.icons_experiment,
                R.drawable.icons_toys_fan,
                R.drawable.icons_theaters,
                R.drawable.icons_backpack,
                R.drawable.icons_sports,
                R.drawable.icons_campaign,
        };
    }

    @Override
    protected boolean hasFab() {
        return false;
    }

    @Override
    protected void onFabClick() {
        // не будет вызван
    }

    @Override
    public ScreenState getState() {
        return CatCreateState.IDLE;
    }

    @Override
    public void saveState(Bundle out) {
        out.putString("nameText", nameText);
        out.putString("descText", descText);
        out.putInt("selectedColor", selectedColor);
        out.putInt("selectedIconResId", selectedIconResId);
    }

    @Override
    public void restoreState(Bundle state) {
        if (state == null) return;
        nameText = state.getString("nameText", "");
        descText = state.getString("descText", "");
        selectedColor = state.getInt("selectedColor", 0xFFF44336);
        selectedIconResId = state.getInt(
                "selectedIconResId",
                R.drawable.ic_category_default
        );
    }
}
