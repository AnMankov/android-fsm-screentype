package com.antoniokoman.basics.screens.categories;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.antoniokoman.basics.R;
import com.antoniokoman.basics.app.AppTheme;
import com.antoniokoman.basics.fsm.CatListState;
import com.antoniokoman.basics.fsm.ScreenState;
import com.antoniokoman.basics.repository.Repository;
import com.antoniokoman.basics.screens.base.AppBarView;
import com.antoniokoman.basics.screens.base.BaseContentScreen;

public class CategoryListScreen extends BaseContentScreen {

    private final Repository repo = Repository.getInstance();

    // 1. AppBar для этого экрана
    @Override
    protected AppBarView createAppBar(Context context) {
        AppBarView appBar = new AppBarView(context);
        appBar.setTitle(context.getString(R.string.cat_list_title));

        // Навигационная иконка (пока без реального Back)
        appBar.setNavigationIcon(R.drawable.ic_menu, v -> {
            // если захочешь обрабатывать назад:
            // if (listener != null) listener.onScreenStateChanged(CatListState.PR_BACK);
        });

        // Если нужны actions справа, добавишь тут:
        // appBar.addAction(R.drawable.ic_settings, v -> { ... });

        return appBar;
    }

    // 2. Контент под аппбаром
    @Override
    protected void onRenderContent(FrameLayout contentContainer) {
        if (repo.catList.categories.isEmpty()) {
            renderEmptyState(contentContainer);
        } else {
            renderList(contentContainer);
        }
    }

    // 3. Действие FAB
    @Override
    protected boolean hasFab() {
        return true;
    }

    @Override
    protected void onFabClick() {
        if (listener != null) listener.onScreenStateChanged(CatListState.PR_ADD);
    }

    @Override
    public ScreenState getState() {
        return CatListState.IDLE;
    }

    // ----- EMPTY STATE -----

    private void renderEmptyState(FrameLayout root) {
        Context context = root.getContext();
        boolean expanded = isExpanded(context);

        int titleSizeSpRes = expanded
                ? R.dimen.empty_title_tablet
                : R.dimen.empty_title_phone;
        float titleSizeSp = context.getResources()
                .getDimension(titleSizeSpRes)
                / context.getResources().getDisplayMetrics().scaledDensity;

        int topPaddingPx = AppTheme.dimenPx(
                context,
                expanded ? R.dimen.empty_top_padding_tablet : R.dimen.empty_top_padding_phone
        );
        int textIconGapPx = AppTheme.dimenPx(
                context,
                expanded ? R.dimen.empty_text_icon_gap_tablet : R.dimen.empty_text_icon_gap_phone
        );
        int iconSizePx = AppTheme.dimenPx(
                context,
                expanded ? R.dimen.empty_icon_tablet : R.dimen.empty_icon_phone
        );

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);

        LinearLayout emptyLayout = new LinearLayout(context);
        emptyLayout.setOrientation(LinearLayout.VERTICAL);
        emptyLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView tv = new TextView(context);
        tv.setText(context.getString(R.string.cat_list_empty));
        tv.setTextColor(AppTheme.textSecondaryColor(context));
        tv.setTextSize(titleSizeSp);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setPadding(0, topPaddingPx, 0, textIconGapPx);
        emptyLayout.addView(tv, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        ImageView bigIcon = new ImageView(context);
        bigIcon.setImageResource(R.drawable.ic_add_tiles);
        bigIcon.setColorFilter(AppTheme.iconTintColor(context));
        bigIcon.setAlpha(AppTheme.ALPHA_DECORATIVE);
        bigIcon.setClickable(true);
        bigIcon.setFocusable(true);
        bigIcon.setBackground(
                ContextCompat.getDrawable(context, R.drawable.bg_click_ripple)
        );
        bigIcon.setOnClickListener(v -> {
            if (listener != null) listener.onScreenStateChanged(CatListState.PR_ADD);
        });

        int touchSizePx = AppTheme.dimenPx(context, R.dimen.touch_target_min);
        LinearLayout.LayoutParams iconLp =
                new LinearLayout.LayoutParams(iconSizePx, iconSizePx);
        iconLp.gravity = Gravity.CENTER_HORIZONTAL;
        emptyLayout.addView(bigIcon, iconLp);

        container.addView(emptyLayout,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));

        FrameLayout.LayoutParams rootLp =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );

        root.addView(container, rootLp);
    }

    // ----- LIST -----

    private void renderList(FrameLayout root) {
        Context context = root.getContext();
        boolean expanded = isExpanded(context);

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.TOP);
        FrameLayout.LayoutParams rootLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        root.addView(container, rootLp);

        android.widget.ScrollView scrollView = new android.widget.ScrollView(context);
        scrollView.setFillViewport(true);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        );
        container.addView(scrollView, scrollLp);

        LinearLayout list = new LinearLayout(context);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setGravity(Gravity.TOP);

        int horizontalPadding = AppTheme.dimenPx(context, R.dimen.cat_create_horizontal_padding);
        int topPadding = AppTheme.dimenPx(
                context,
                expanded
                        ? R.dimen.cat_create_label_top_padding_tablet
                        : R.dimen.cat_create_label_top_padding_phone
        );
        int betweenCards = AppTheme.dimenPx(context, R.dimen.spacing_medium);

        list.setPadding(horizontalPadding, topPadding, horizontalPadding, 0);

        scrollView.addView(list, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        for (int i = 0; i < repo.catList.categories.size(); i++) {
            Repository.CategoryList.CategoryData cat = repo.catList.categories.get(i);
            android.view.View card = createCategoryCardView(context, cat, i);

            LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            if (i > 0) {
                cardLp.topMargin = betweenCards;
            }
            list.addView(card, cardLp);
        }
    }

    // ----- CARD -----

    private android.view.View createCategoryCardView(
            Context context,
            Repository.CategoryList.CategoryData cat,
            int position
    ) {
        boolean expanded = isExpanded(context);

        int cardPaddingV = AppTheme.dimenPx(
                context,
                expanded ? R.dimen.spacing_large : R.dimen.spacing_medium
        );
        int cardPaddingH = AppTheme.dimenPx(context, R.dimen.spacing_medium);
        int cornerRadius = AppTheme.dimenPx(context, R.dimen.textfield_corner_radius);
        int strokeWidth = AppTheme.dimenPx(context, R.dimen.textfield_stroke_width);

        int badgeWidth = AppTheme.dimenPx(context, R.dimen.cat_card_badge_width);
        int iconSize = AppTheme.dimenPx(context, R.dimen.icon_size_medium);
        int iconPadding = AppTheme.dimenPx(context, R.dimen.spacing_extra_small);
        int badgeInnerGap = AppTheme.dimenPx(context, R.dimen.spacing_small);
        int textVerticalGap = AppTheme.dimenPx(context, R.dimen.spacing_small);
        int rectSize = AppTheme.dimenPx(context, R.dimen.icon_circle_size);

        int categoryColor;
        try {
            categoryColor = Color.parseColor(
                    cat.color != null ? cat.color : "#FFDDAC"
            );
        } catch (IllegalArgumentException e) {
            categoryColor = Color.parseColor("#FFDDAC");
        }

        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setClickable(true);
        card.setForeground(ContextCompat.getDrawable(context, R.drawable.bg_click_ripple));

        int minHeight = AppTheme.dimenPx(context, R.dimen.cat_card_height_min);
        card.setMinimumHeight(minHeight);

        int screenBg = AppTheme.backgroundColor(context);

        card.setBackground(AppTheme.roundedBox(
                screenBg,
                categoryColor,
                strokeWidth,
                cornerRadius
        ));

        // --- левый бейдж ---
        LinearLayout leftBadge = new LinearLayout(context);
        leftBadge.setOrientation(LinearLayout.VERTICAL);
        leftBadge.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams leftBadgeLp = new LinearLayout.LayoutParams(
                badgeWidth,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        card.addView(leftBadge, leftBadgeLp);

        // прямоугольник под иконкой
        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setShape(GradientDrawable.RECTANGLE);
        iconBg.setColor(screenBg);
        iconBg.setCornerRadius(cornerRadius);

        FrameLayout iconContainer = new FrameLayout(context);
        iconContainer.setBackground(iconBg);

        FrameLayout.LayoutParams iconContainerLp =
                new FrameLayout.LayoutParams(rectSize, rectSize);
        iconContainerLp.gravity = Gravity.CENTER;

        ImageView iconView = new ImageView(context);
        FrameLayout.LayoutParams iconLp =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );
        iconLp.gravity = Gravity.CENTER;
        iconView.setLayoutParams(iconLp);

        // внутренний отступ от прямоугольника
        iconView.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
        iconView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        if (cat.icon != null && !cat.icon.isEmpty()) {
            int resId = context.getResources()
                    .getIdentifier(cat.icon, "drawable", context.getPackageName());
            iconView.setImageResource(resId != 0 ? resId : R.drawable.ic_category_default);
        } else {
            iconView.setImageResource(R.drawable.ic_category_default);
        }
        iconView.setColorFilter(AppTheme.textMainColor(context));

        iconContainer.addView(iconView, iconLp);

        LinearLayout.LayoutParams iconSlotLp =
                new LinearLayout.LayoutParams(rectSize, rectSize);
        iconSlotLp.bottomMargin = badgeInnerGap;
        leftBadge.addView(iconContainer, iconSlotLp);

        // прямоугольник под счётчиком
        GradientDrawable countBg = new GradientDrawable();
        countBg.setShape(GradientDrawable.RECTANGLE);
        countBg.setColor(screenBg);
        countBg.setCornerRadius(cornerRadius);

        FrameLayout countContainer = new FrameLayout(context);
        countContainer.setBackground(countBg);

        FrameLayout.LayoutParams countContainerLp =
                new FrameLayout.LayoutParams(rectSize, rectSize);
        countContainerLp.gravity = Gravity.CENTER;

        TextView countView = new TextView(context);
        int convCount = cat.converters != null ? cat.converters.size() : 0;
        countView.setText(String.valueOf(convCount));
        countView.setTextColor(AppTheme.textMainColor(context));
        countView.setTextSize(12);
        countView.setGravity(Gravity.CENTER);

        FrameLayout.LayoutParams countTextLp =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );
        countTextLp.gravity = Gravity.CENTER;

        countContainer.addView(countView, countTextLp);

        LinearLayout.LayoutParams countSlotLp =
                new LinearLayout.LayoutParams(rectSize, rectSize);
        leftBadge.addView(countContainer, countSlotLp);

        // --- центральный блок ---
        LinearLayout centerWrapper = new LinearLayout(context);
        centerWrapper.setOrientation(LinearLayout.VERTICAL);
        centerWrapper.setGravity(Gravity.CENTER_VERTICAL);
        centerWrapper.setPadding(cardPaddingH, cardPaddingV, cardPaddingH, cardPaddingV);
        centerWrapper.setBackgroundColor(Color.TRANSPARENT);

        LinearLayout.LayoutParams centerLp = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        );
        card.addView(centerWrapper, centerLp);

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_VERTICAL);

        centerWrapper.addView(content,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));

        TextView nameView = new TextView(context);
        nameView.setText(cat.name != null ? cat.name : "");
        nameView.setTextColor(AppTheme.textMainColor(context));
        nameView.setTextSize(16);
        nameView.setMaxLines(2);

        TextView descView = new TextView(context);
        descView.setText(cat.description != null ? cat.description : "");
        descView.setTextColor(AppTheme.textSecondaryColor(context));
        descView.setTextSize(14);
        descView.setMaxLines(2);

        content.addView(nameView);
        LinearLayout.LayoutParams descLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        descLp.topMargin = textVerticalGap;
        content.addView(descView, descLp);

        // --- правый бейдж ---
        LinearLayout rightBadge = new LinearLayout(context);
        rightBadge.setOrientation(LinearLayout.VERTICAL);
        rightBadge.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams rightBadgeLp = new LinearLayout.LayoutParams(
                badgeWidth,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        card.addView(rightBadge, rightBadgeLp);

        // прямоугольник под меню
        GradientDrawable menuBg = new GradientDrawable();
        menuBg.setShape(GradientDrawable.RECTANGLE);
        menuBg.setColor(screenBg);
        menuBg.setCornerRadius(cornerRadius);

        FrameLayout menuContainer = new FrameLayout(context);
        menuContainer.setBackground(menuBg);

        FrameLayout.LayoutParams menuContainerLp =
                new FrameLayout.LayoutParams(rectSize, rectSize);
        menuContainerLp.gravity = Gravity.CENTER;

        ImageView menuIcon = new ImageView(context);
        FrameLayout.LayoutParams menuLp =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );
        menuLp.gravity = Gravity.CENTER;
        menuIcon.setLayoutParams(menuLp);

        menuIcon.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
        menuIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        menuIcon.setImageResource(R.drawable.ic_more_vert);
        menuIcon.setColorFilter(AppTheme.textMainColor(context));

        menuContainer.addView(menuIcon, menuLp);
        rightBadge.addView(menuContainer, new LinearLayout.LayoutParams(rectSize, rectSize));

        // --- клики ---
        card.setOnClickListener(v -> {
            repo.catList.index = position;
            if (listener != null) {
                listener.onScreenStateChanged(CatListState.PR_CAT);
            }
        });

        rightBadge.setOnClickListener(v -> {
            // TODO: PopupMenu
        });

        // --- скруглённые бейджи ---
        GradientDrawable leftBg = new GradientDrawable();
        leftBg.setColor(categoryColor);
        leftBg.setCornerRadii(new float[]{
                cornerRadius, cornerRadius,   // top-left
                0, 0,                         // top-right
                0, 0,                         // bottom-right
                cornerRadius, cornerRadius    // bottom-left
        });
        leftBadge.setBackground(leftBg);

        GradientDrawable rightBg = new GradientDrawable();
        rightBg.setColor(categoryColor);
        rightBg.setCornerRadii(new float[]{
                0, 0,                         // top-left
                cornerRadius, cornerRadius,   // top-right
                cornerRadius, cornerRadius,   // bottom-right
                0, 0                          // bottom-left
        });
        rightBadge.setBackground(rightBg);

        return card;
    }



}
