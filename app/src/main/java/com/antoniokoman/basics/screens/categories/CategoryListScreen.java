package com.antoniokoman.basics.screens.categories;

import android.content.Context;
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
            // TODO: позже включишь список
            // renderList(contentContainer);
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

        // Размер вью = touch target (например, 72dp)
        int touchSizePx = AppTheme.dimenPx(context, R.dimen.touch_target_min); // или отдельный dimen, типа 72dp
        LinearLayout.LayoutParams iconLp =
                new LinearLayout.LayoutParams(iconSizePx, iconSizePx);
        iconLp.gravity = Gravity.CENTER_HORIZONTAL;
        emptyLayout.addView(bigIcon, iconLp);
        bigIcon.setBackground(
                ContextCompat.getDrawable(context, R.drawable.bg_click_ripple)
        );

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

    // private void renderList(FrameLayout root) { ... } — позже
}
