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
import com.antoniokoman.basics.screens.base.AppBarView;

public class CategoryListScreen extends BaseScreen {

    // Системные константы
    private static final int MATCH_PARENT = ViewGroup.LayoutParams.MATCH_PARENT;
    private static final int WRAP_CONTENT = ViewGroup.LayoutParams.WRAP_CONTENT;

    // Порог планшета
    private static final int WIDTH_EXPANDED_MIN_DP = 840;

    private final Repository repo = Repository.getInstance();

    @Override
    protected void onRender() {
        Context context = cachedView.getContext();
        cachedView.setBackgroundColor(AppTheme.backgroundColor(context));

        // 1. AppBar
        AppBarView appBar = createAppBar(context);
        cachedView.addView(appBar);

        // 2. Контент под аппбаром
        if (repo.catList.categories.isEmpty()) {
            renderEmptyState(cachedView, appBar.getLayoutParams().height);
        } else {
            // renderList(cachedView, appBar.getLayoutParams().height);
        }

        // 3. FAB поверх всего
        renderFloatingButton(cachedView);
    }

    @Override public ScreenState getState() { return CatListState.IDLE; }

    private AppBarView createAppBar(Context context) {
        AppBarView appBar = new AppBarView(context);
        appBar.setTitle(context.getString(R.string.cat_list_title)); // добавь строку в strings.xml

        appBar.setNavigationIcon(R.drawable.ic_menu, v -> {
            // TODO: открыть drawer, показать меню, или пока просто логика назад
            // if (listener != null) listener.onScreenStateChanged(CatListState.PR_BACK);
        });

        return appBar;
    }

    private void renderFloatingButton(FrameLayout root) {
        Context context = root.getContext();
        int widthDp = getScreenWidthDp(context);
        int expandedMin = getExpandedMinWidthDp(context);
        boolean isExpanded = widthDp >= expandedMin;


        int fabSizePx = AppTheme.dimenPx(
                context,
                isExpanded ? R.dimen.fab_size_large : R.dimen.fab_size_medium
        );
        int iconSizePx = fabSizePx / 2;

        int marginPx = AppTheme.dimenPx(
                context,
                isExpanded ? R.dimen.fab_margin_tablet : R.dimen.fab_margin_phone
        );
        int elevationPx = AppTheme.dimenPx(
                context,
                isExpanded ? R.dimen.fab_elevation_tablet : R.dimen.fab_elevation_phone
        );

        FrameLayout fab = new FrameLayout(context);

        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(AppTheme.accentSoftColor(context));
        shape.setCornerRadius(AppTheme.dimenPx(context, R.dimen.radius_standard));
        fab.setBackground(shape);
        fab.setElevation(elevationPx);

        ImageView icon = new ImageView(context);
        icon.setImageResource(R.drawable.ic_add_tiles);
        icon.setColorFilter(AppTheme.fabIconTintColor(context));

        FrameLayout.LayoutParams iconLp = new FrameLayout.LayoutParams(iconSizePx, iconSizePx);
        iconLp.gravity = Gravity.CENTER;
        fab.addView(icon, iconLp);

        fab.setOnClickListener(v -> {
            if (listener != null) listener.onScreenStateChanged(CatListState.PR_ADD);
        });

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(fabSizePx, fabSizePx);
        lp.gravity = Gravity.BOTTOM | Gravity.END;
        lp.setMargins(0, 0, marginPx, marginPx);

        root.addView(fab, lp);
    }

    private int getScreenWidthDp(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) (dm.widthPixels / dm.density);
    }

    private void renderEmptyState(FrameLayout root, int topOffsetPx) {
        Context context = root.getContext();
        int widthDp = getScreenWidthDp(context);
        int expandedMin = getExpandedMinWidthDp(context);
        boolean isExpanded = widthDp >= expandedMin;

        int titleSizeSpRes = isExpanded
                ? R.dimen.empty_title_tablet
                : R.dimen.empty_title_phone;
        float titleSizeSp = context.getResources()
                .getDimension(titleSizeSpRes)
                / context.getResources().getDisplayMetrics().scaledDensity;

        int topPaddingPx = AppTheme.dimenPx(
                context,
                isExpanded ? R.dimen.empty_top_padding_tablet : R.dimen.empty_top_padding_phone
        );
        int textIconGapPx = AppTheme.dimenPx(
                context,
                isExpanded ? R.dimen.empty_text_icon_gap_tablet : R.dimen.empty_text_icon_gap_phone
        );
        int iconSizePx = AppTheme.dimenPx(
                context,
                isExpanded ? R.dimen.empty_icon_tablet : R.dimen.empty_icon_phone
        );

        // Контейнер для центрирования контента по вертикали
        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER); // центр по вертикали и горизонтали

        // Сам empty layout (текст + иконка)
        LinearLayout emptyLayout = new LinearLayout(context);
        emptyLayout.setOrientation(LinearLayout.VERTICAL);
        emptyLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView tv = new TextView(context);
        tv.setText(context.getString(R.string.cat_list_empty));
        tv.setTextColor(AppTheme.textSecondaryColor(context));
        tv.setTextSize(titleSizeSp);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setPadding(0, topPaddingPx, 0, textIconGapPx);
        emptyLayout.addView(tv, new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        ImageView bigIcon = new ImageView(context);
        bigIcon.setImageResource(R.drawable.ic_add_tiles);
        bigIcon.setColorFilter(AppTheme.iconTintColor(context));
        bigIcon.setAlpha(AppTheme.ALPHA_DECORATIVE);
        bigIcon.setClickable(true);
        bigIcon.setFocusable(true);
        bigIcon.setOnClickListener(v -> {
            if (listener != null) listener.onScreenStateChanged(CatListState.PR_ADD);
        });

        LinearLayout.LayoutParams iconLp = new LinearLayout.LayoutParams(iconSizePx, iconSizePx);
        iconLp.gravity = Gravity.CENTER_HORIZONTAL;
        emptyLayout.addView(bigIcon, iconLp);

        // Кладём emptyLayout в контейнер
        container.addView(emptyLayout,
                new LinearLayout.LayoutParams(MATCH_PARENT, WRAP_CONTENT));

        // А контейнер — в root, с отступом от верха равным высоте app bar
        FrameLayout.LayoutParams rootLp =
                new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        rootLp.topMargin = topOffsetPx;

        root.addView(container, rootLp);
    }


    private int getExpandedMinWidthDp(Context context) {
        return context.getResources().getInteger(R.integer.width_expanded_min_dp);
    }
}
