package com.antoniokoman.basics.screens.base;

import android.content.Context;
import android.graphics.drawable.GradientDrawable;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.antoniokoman.basics.R;
import com.antoniokoman.basics.app.AppTheme;
import com.antoniokoman.basics.fsm.BaseScreen;

public abstract class BaseContentScreen extends BaseScreen {

    protected static final int MATCH_PARENT = FrameLayout.LayoutParams.MATCH_PARENT;
    protected static final int WRAP_CONTENT = FrameLayout.LayoutParams.WRAP_CONTENT;

    // --- Утилиты адаптивности ---

    protected int getScreenWidthDp(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) (dm.widthPixels / dm.density);
    }

    protected int getExpandedMinWidthDp(Context context) {
        return context.getResources().getInteger(R.integer.width_expanded_min_dp);
    }

    protected boolean isExpanded(Context context) {
        return getScreenWidthDp(context) >= getExpandedMinWidthDp(context);
    }

    protected int dp(Context context, int value) {
        return AppTheme.dp(context, value);
    }

    // --- Шаблон рендера ---

    @Override
    protected void onRender() {
        Context context = cachedView.getContext();
        cachedView.setBackgroundColor(AppTheme.backgroundColor(context));

        // 1. AppBar (экран сам решает, нужен ли он и какой)
        AppBarView appBar = createAppBar(context);
        if (appBar != null) {
            cachedView.addView(appBar);
        }

        int topOffsetPx = appBar != null ? appBar.getLayoutParams().height : 0;

        // 2. Контейнер под контент
        FrameLayout contentContainer = new FrameLayout(context);
        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        lp.topMargin = topOffsetPx;
        contentContainer.setLayoutParams(lp);
        cachedView.addView(contentContainer);

        onRenderContent(contentContainer);

        // FAB только если экран его хочет
        if (hasFab()) {
            renderFloatingButton(cachedView);
        }
    }

    // Экран сам создаёт/настраивает AppBar (или возвращает null, если он не нужен)
    protected abstract AppBarView createAppBar(Context context);

    // Экран рисует свой контент
    protected abstract void onRenderContent(FrameLayout contentContainer);

    // Что происходит по нажатию FAB
    protected abstract void onFabClick();

    // Экран может переопределить поведение «Назад»
    protected void onBackPressed() {
        // по умолчанию ничего
    }

    protected boolean hasFab() {
        return true; // по умолчанию есть
    }

    private void renderFloatingButton(FrameLayout root) {
        Context context = root.getContext();
        boolean expanded = isExpanded(context);

        int fabSizePx = AppTheme.dimenPx(
                context,
                expanded ? R.dimen.fab_size_large : R.dimen.fab_size_medium
        );
        int iconSizePx = fabSizePx / 2;

        int marginPx = AppTheme.dimenPx(
                context,
                expanded ? R.dimen.fab_margin_tablet : R.dimen.fab_margin_phone
        );
        int elevationPx = AppTheme.dimenPx(
                context,
                expanded ? R.dimen.fab_elevation_tablet : R.dimen.fab_elevation_phone
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

        FrameLayout.LayoutParams iconLp =
                new FrameLayout.LayoutParams(iconSizePx, iconSizePx);
        iconLp.gravity = Gravity.CENTER;
        fab.addView(icon, iconLp);

        fab.setOnClickListener(v -> onFabClick());

        FrameLayout.LayoutParams lp =
                new FrameLayout.LayoutParams(fabSizePx, fabSizePx);
        lp.gravity = Gravity.BOTTOM | Gravity.END;
        lp.setMargins(0, 0, marginPx, marginPx);

        root.addView(fab, lp);
    }
}
