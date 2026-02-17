package com.antoniokoman.basics.screens.categories;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowMetrics;
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

    // Константы размеров и отступов FAB (dp)
    private static final int FAB_SIZE_MEDIUM_DP = 56;   // phones (compact/medium)
    private static final int FAB_SIZE_LARGE_DP  = 80;   // tablets/foldables (expanded)

    private static final int FAB_MARGIN_PHONE_DP   = 16;
    private static final int FAB_MARGIN_TABLET_DP  = 24;

    private static final int FAB_ELEVATION_PHONE_DP  = 8;
    private static final int FAB_ELEVATION_TABLET_DP = 12;

    // Граница M3 Expanded (см. Window Size Classes)
    private static final int WIDTH_EXPANDED_MIN_DP = 840;

    // --- EMPTY STATE ---

    // Текст
    private static final int EMPTY_TITLE_PHONE_SP   = 18;
    private static final int EMPTY_TITLE_TABLET_SP  = 20;

    // Отступы
    private static final int EMPTY_TOP_PADDING_PHONE_DP    = 0;
    private static final int EMPTY_TOP_PADDING_TABLET_DP   = 24;
    private static final int EMPTY_TEXT_ICON_GAP_PHONE_DP  = 32;
    private static final int EMPTY_TEXT_ICON_GAP_TABLET_DP = 40;

    // Иконка
    private static final int EMPTY_ICON_PHONE_DP   = 96;
    private static final int EMPTY_ICON_TABLET_DP  = 126;

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
        Context context = root.getContext();
        int widthDp = getScreenWidthDp(context);

        int fabSizeDp = (widthDp >= WIDTH_EXPANDED_MIN_DP)
                ? FAB_SIZE_LARGE_DP
                : FAB_SIZE_MEDIUM_DP;

        int fabSizePx    = dp(fabSizeDp);
        int iconSizePx   = fabSizePx / 2;  // 50% FAB
        int marginDp     = (widthDp >= WIDTH_EXPANDED_MIN_DP) ? FAB_MARGIN_TABLET_DP : FAB_MARGIN_PHONE_DP;
        int elevationDp = (widthDp >= WIDTH_EXPANDED_MIN_DP)
                ? FAB_ELEVATION_TABLET_DP
                : FAB_ELEVATION_PHONE_DP;

        // 2. Контейнер FAB
        FrameLayout fab = new FrameLayout(context);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(AppTheme.COLOR_ACCENT_SOFT);
        shape.setCornerRadius(dp(AppTheme.RADIUS_STANDARD_DP));
        fab.setBackground(shape);

        fab.setElevation(dp(elevationDp));

        // 3. Иконка внутри FAB
        ImageView icon = new ImageView(context);
        icon.setImageResource(R.drawable.ic_add_tiles);
        icon.setColorFilter(AppTheme.COLOR_ICON_TINT);

        FrameLayout.LayoutParams iconLp = new FrameLayout.LayoutParams(iconSizePx, iconSizePx);
        iconLp.gravity = Gravity.CENTER;
        fab.addView(icon, iconLp);

        // 4. Логика клика
        fab.setOnClickListener(v -> {
            if (listener != null) listener.onScreenStateChanged(CatListState.PR_ADD);
        });

        // 5. Позиционирование FAB
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(fabSizePx, fabSizePx);
        lp.gravity = Gravity.BOTTOM | Gravity.END;
        lp.setMargins(
                SPACING_NONE,
                SPACING_NONE,
                dp(marginDp),
                dp(marginDp)
        );

        root.addView(fab, lp);
    }

    private int getScreenWidthDp(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) (dm.widthPixels / dm.density);
    }

    private void renderEmptyState(FrameLayout root) {
        Context context = root.getContext();
        int widthDp = getScreenWidthDp(context);
        boolean isExpanded = widthDp >= WIDTH_EXPANDED_MIN_DP;

        int titleSp     = isExpanded ? EMPTY_TITLE_TABLET_SP : EMPTY_TITLE_PHONE_SP;
        int topPadding  = isExpanded ? EMPTY_TOP_PADDING_TABLET_DP : EMPTY_TOP_PADDING_PHONE_DP;
        int textIconGap = isExpanded ? EMPTY_TEXT_ICON_GAP_TABLET_DP : EMPTY_TEXT_ICON_GAP_PHONE_DP;
        int iconSizePx  = dp(isExpanded ? EMPTY_ICON_TABLET_DP : EMPTY_ICON_PHONE_DP);

        LinearLayout emptyLayout = new LinearLayout(context);
        emptyLayout.setOrientation(LinearLayout.VERTICAL);
        emptyLayout.setGravity(Gravity.CENTER);

        TextView tv = new TextView(context);
        tv.setText(context.getString(R.string.cat_list_empty));
        tv.setTextColor(AppTheme.COLOR_TEXT_SECONDARY);
        tv.setTextSize(titleSp);
        tv.setGravity(Gravity.CENTER);
        tv.setPadding(
                SPACING_NONE,
                dp(topPadding),
                SPACING_NONE,
                dp(textIconGap)
        );
        emptyLayout.addView(tv, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        ImageView bigIcon = new ImageView(context);
        bigIcon.setImageResource(R.drawable.ic_add_tiles);
        bigIcon.setColorFilter(AppTheme.COLOR_ICON_TINT);
        bigIcon.setAlpha(AppTheme.ALPHA_DECORATIVE);

        // ✅ Делаем иконку кнопкой с тем же действием, что у FAB
        bigIcon.setClickable(true);
        bigIcon.setFocusable(true);
        bigIcon.setOnClickListener(v -> {
            if (listener != null) listener.onScreenStateChanged(CatListState.PR_ADD);
        });

        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(iconSizePx, iconSizePx);
        iconLp.gravity = Gravity.CENTER_HORIZONTAL;
        emptyLayout.addView(bigIcon, iconLp);

        root.addView(emptyLayout, new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
    }
}