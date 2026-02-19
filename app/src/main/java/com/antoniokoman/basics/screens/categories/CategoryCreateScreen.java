package com.antoniokoman.basics.screens.categories;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.GridLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

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
        container.addView(form, formLp);

        // 1. Поле "Название категории"
        OutlinedTextFieldView nameField = new OutlinedTextFieldView(context);
        nameField.setLabel(R.string.cat_create_name_label);
        nameField.setPlaceholder(R.string.cat_create_name_hint);

        nameField.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                isValid = !s.toString().trim().isEmpty();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        form.addView(nameField, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        int betweenFields = AppTheme.dimenPx(context, R.dimen.spacing_large);
        ((LinearLayout.LayoutParams) nameField.getLayoutParams()).bottomMargin = betweenFields;

        // 2. Поле "Описание (необязательно)"
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

        // 3. Поле "Иконка категории" (readonly + выбор)
        OutlinedTextFieldView iconField = new OutlinedTextFieldView(context);
        iconField.setLabel(R.string.cat_create_icon_title);
        iconField.setPlaceholder(R.string.cat_create_icon_hint);

        // делаем поле не редактируемым, но кликабельным
        iconField.getEdit().setFocusable(false);
        iconField.getEdit().setFocusableInTouchMode(false);
        iconField.getEdit().setCursorVisible(false);
        iconField.getEdit().setKeyListener(null);

        View.OnClickListener iconClickListener = v ->
                showIconPickerBottomSheet(context, iconField);

        // клик по иконке
        iconField.getLayout().setStartIconDrawable(
                context.getDrawable(R.drawable.ic_category_default)
        );
        iconField.getLayout().setStartIconContentDescription(
                context.getString(R.string.cat_create_icon_preview_default)
        );
        iconField.getLayout().setStartIconOnClickListener(iconClickListener);
        iconField.getLayout().setStartIconTintList(null);

        // клик по самому полю (лейбл/placeholder/пустое место)
        iconField.getLayout().setClickable(true);
        iconField.getEdit().setClickable(true);
        iconField.getLayout().setOnClickListener(iconClickListener);
        iconField.getEdit().setOnClickListener(iconClickListener);

        form.addView(iconField, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));
    }

    private void showIconPickerBottomSheet(Context context, OutlinedTextFieldView iconField) {
        BottomSheetDialog dialog = new BottomSheetDialog(context);
        Context themed = new ContextThemeWrapper(context, R.style.Theme_Basics);

        // Скроллируемый контейнер
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

        int itemSize = AppTheme.dimenPx(context, R.dimen.icon_size_medium);
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

        // кладём root в ScrollView
        scroll.addView(root, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        dialog.setContentView(scroll);

        // фиксируем лист и даём скролл только содержимому
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
                R.drawable.icons_architecture,
                R.drawable.icons_construction,
                R.drawable.icons_experiment,
                R.drawable.icons_school,
                R.drawable.icons_architecture,
                R.drawable.icons_construction,
                R.drawable.icons_experiment,
                R.drawable.icons_school,
                R.drawable.icons_architecture,
                R.drawable.icons_construction,
                R.drawable.icons_experiment,
                R.drawable.icons_school,
                R.drawable.icons_architecture,
                R.drawable.icons_construction,
                R.drawable.icons_experiment,
                R.drawable.icons_school,
                R.drawable.icons_architecture,
                R.drawable.icons_construction,
                R.drawable.icons_experiment,
                R.drawable.icons_school,
                R.drawable.icons_architecture,
                R.drawable.icons_construction,
                R.drawable.icons_experiment,
                R.drawable.icons_school,
                R.drawable.icons_architecture,
                R.drawable.icons_construction,
                R.drawable.icons_experiment,
                R.drawable.icons_school,
                R.drawable.icons_architecture,
                R.drawable.icons_construction,
                R.drawable.icons_experiment,
                R.drawable.icons_school,
                R.drawable.icons_architecture,
                R.drawable.icons_construction,
                R.drawable.icons_experiment,
                R.drawable.icons_school,
                R.drawable.icons_architecture,
                R.drawable.icons_construction,
                R.drawable.icons_experiment,
                R.drawable.icons_school,
                R.drawable.icons_architecture,
                R.drawable.icons_construction,
                R.drawable.icons_experiment,
                R.drawable.icons_school,
                // ...
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
}
