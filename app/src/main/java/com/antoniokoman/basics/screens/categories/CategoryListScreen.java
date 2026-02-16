package com.antoniokoman.basics.screens.categories;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.antoniokoman.basics.R;
import com.antoniokoman.basics.app.AppTheme;
import com.antoniokoman.basics.fsm.BaseScreen;
import com.antoniokoman.basics.fsm.CatCreateState;
import com.antoniokoman.basics.fsm.CatListState;
import com.antoniokoman.basics.fsm.ScreenState;
import com.antoniokoman.basics.repository.Repository;

public class CategoryListScreen extends BaseScreen {

    // --- СИСТЕМНЫЕ КОНСТАНТЫ ---
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

    // --- СЕТКА (SPACING) ---
    private static final int SPACING_NONE = 0;
    private static final int SPACING_MEDIUM_DP = 16;
    private static final int SPACING_LARGE_DP = 32;
    private static final int SPACING_FAB_MARGIN_DP = 24;

    // --- РАЗМЕРЫ ЭЛЕМЕНТОВ ---
    private static final int SIZE_FAB_DP = 64;
    private static final int SIZE_EMPTY_STACK_DP = 126;
    private static final int TEXT_SIZE_TITLE_SP = 18;
    private static final int LIST_PADDING_BOTTOM_DP = 100;

    private final Repository repo = Repository.getInstance();

    @Override
    protected void onRender() {
        // 1. Устанавливаем фон всему экрану сразу
        cachedView.setBackgroundColor(AppTheme.COLOR_BACKGROUND);

        if (repo.catList.categories.isEmpty()) {
            renderEmptyState(cachedView);
        } else {
            //renderList(cachedView);
        }

        // Кнопка всегда поверх всего
        renderFloatingButton(cachedView);
    }

    @Override public ScreenState getState() { return CatListState.IDLE; }

    private void renderFloatingButton(FrameLayout root) {
        // 1. Создаем контейнер-плитку (зеленый квадрат)
        FrameLayout fab = new FrameLayout(root.getContext());

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(AppTheme.COLOR_ACCENT_SOFT);
        shape.setCornerRadius(dp(AppTheme.RADIUS_STANDARD_DP));
        fab.setBackground(shape);

        // Тень из темы
        fab.setElevation(dp((int) AppTheme.ELEVATION_FAB_DP));

        // 2. Иконка "Квадрат с плюсом" (ic_add_tiles)
        ImageView icon = new ImageView(fab.getContext());
        icon.setImageResource(R.drawable.ic_add_tiles); // Твой вектор со скрина
        icon.setColorFilter(AppTheme.COLOR_ICON_TINT);

        // Размер иконки внутри FAB (из темы)
        int iconSize = dp(AppTheme.ICON_SIZE_FAB_DP);
        FrameLayout.LayoutParams iconLp = new FrameLayout.LayoutParams(iconSize, iconSize);
        iconLp.gravity = Gravity.CENTER;
        fab.addView(icon, iconLp);

        // 3. Логика клика
        fab.setOnClickListener(v -> {
            if (listener != null) listener.onScreenStateChanged(CatListState.PR_ADD);
        });

        // 4. Позиционирование FAB в углу экрана
        int fabSize = dp(SIZE_FAB_DP);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(fabSize, fabSize);
        lp.gravity = Gravity.BOTTOM | Gravity.END;
        lp.setMargins(SPACING_NONE, SPACING_NONE, dp(SPACING_FAB_MARGIN_DP), dp(SPACING_FAB_MARGIN_DP));

        root.addView(fab, lp);
    }

    private void renderEmptyState(FrameLayout root) {
        // 1. Основной вертикальный контейнер
        LinearLayout emptyLayout = new LinearLayout(root.getContext());
        emptyLayout.setOrientation(LinearLayout.VERTICAL);
        emptyLayout.setGravity(Gravity.CENTER);

        // 2. ТЕКСТ (СВЕРХУ, как на референсе)
        TextView tv = new TextView(root.getContext());
        tv.setText(root.getContext().getString(R.string.cat_list_empty));
        tv.setTextColor(AppTheme.COLOR_TEXT_SECONDARY);
        tv.setTextSize(TEXT_SIZE_TITLE_SP);
        tv.setGravity(Gravity.CENTER);
        // Отступ снизу, чтобы "оттолкнуть" иконку
        tv.setPadding(SPACING_NONE, SPACING_NONE, SPACING_NONE, dp(SPACING_LARGE_DP));

        emptyLayout.addView(tv, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        // 3. ИКОНКА (СНИЗУ) — та самая library_add
        ImageView bigIcon = new ImageView(root.getContext());
        bigIcon.setImageResource(R.drawable.ic_add_tiles); // Стандарт из Material
        bigIcon.setColorFilter(AppTheme.COLOR_ICON_TINT);
        bigIcon.setAlpha(AppTheme.ALPHA_DECORATIVE); // Бледная (0.3f)

        // Размер покрупнее (96dp)
        int iconSize = dp(SIZE_EMPTY_STACK_DP);
        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(iconSize, iconSize);
        iconLp.gravity = Gravity.CENTER_HORIZONTAL;

        emptyLayout.addView(bigIcon, iconLp);

        // 4. Сажаем всё это в корень на весь экран
        root.addView(emptyLayout, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
    }

}