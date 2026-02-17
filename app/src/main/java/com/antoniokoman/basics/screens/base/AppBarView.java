package com.antoniokoman.basics.screens.base;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.antoniokoman.basics.R;
import com.antoniokoman.basics.app.AppTheme;

public final class AppBarView extends FrameLayout {

    private ImageView navIcon;
    private TextView titleView;
    private LinearLayout actionsContainer;

    public AppBarView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        int widthDp = getScreenWidthDp(context);
        boolean isExpanded = widthDp >= 840; // тот же порог

        int heightPx = AppTheme.dimenPx(
                context,
                isExpanded ? R.dimen.appbar_height_tablet : R.dimen.appbar_height_phone
        );

        LayoutParams lp = new LayoutParams(
                LayoutParams.MATCH_PARENT,
                heightPx
        );
        setLayoutParams(lp);
        setBackgroundColor(AppTheme.backgroundColor(context));

        LinearLayout row = new LinearLayout(context);
        row.setOrientation(LinearLayout.HORIZONTAL);
        row.setGravity(Gravity.CENTER_VERTICAL);
        row.setLayoutParams(new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT
        ));

        int horizontalPadding = AppTheme.dimenPx(context, R.dimen.appbar_horizontal_padding);
        row.setPadding(horizontalPadding, 0, horizontalPadding, 0);

        navIcon = new ImageView(context);
        int iconSize = AppTheme.dimenPx(context, R.dimen.appbar_icon_size);
        LinearLayout.LayoutParams navLp =
                new LinearLayout.LayoutParams(iconSize, iconSize);
        row.addView(navIcon, navLp);

        titleView = new TextView(context);
        titleView.setTextColor(AppTheme.textMainColor(context));
        titleView.setTextSize(20); // можно тоже вынести в dimen sp
        LinearLayout.LayoutParams titleLp =
                new LinearLayout.LayoutParams(0, LayoutParams.WRAP_CONTENT, 1f);
        titleLp.leftMargin = AppTheme.dimenPx(context, R.dimen.spacing_medium);
        row.addView(titleView, titleLp);

        actionsContainer = new LinearLayout(context);
        actionsContainer.setOrientation(LinearLayout.HORIZONTAL);
        actionsContainer.setGravity(Gravity.CENTER_VERTICAL);
        row.addView(actionsContainer,
                new LinearLayout.LayoutParams(
                        LayoutParams.WRAP_CONTENT,
                        LayoutParams.MATCH_PARENT));

        addView(row);
    }

    public void setTitle(CharSequence title) {
        titleView.setText(title);
    }

    public void setNavigationIcon(int resId, OnClickListener listener) {
        navIcon.setImageResource(resId);
        navIcon.setColorFilter(AppTheme.iconTintColor(getContext()));
        navIcon.setOnClickListener(listener);
        navIcon.setVisibility(VISIBLE);
    }

    public void clearNavigationIcon() {
        navIcon.setImageDrawable(null);
        navIcon.setOnClickListener(null);
        navIcon.setVisibility(GONE);
    }

    public void addAction(int resId, OnClickListener listener) {
        ImageView action = new ImageView(getContext());
        int size = AppTheme.dimenPx(getContext(), R.dimen.appbar_icon_size);
        LinearLayout.LayoutParams lp =
                new LinearLayout.LayoutParams(size, size);
        lp.leftMargin = AppTheme.dimenPx(getContext(), R.dimen.appbar_action_gap);
        action.setImageResource(resId);
        action.setColorFilter(AppTheme.iconTintColor(getContext()));
        action.setOnClickListener(listener);
        actionsContainer.addView(action, lp);
    }

    public void clearActions() {
        actionsContainer.removeAllViews();
    }

    private int getScreenWidthDp(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return (int) (dm.widthPixels / dm.density);
    }
}


