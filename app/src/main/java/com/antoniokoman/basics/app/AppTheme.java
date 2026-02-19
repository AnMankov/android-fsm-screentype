package com.antoniokoman.basics.app;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;

import androidx.annotation.DimenRes;
import androidx.core.content.ContextCompat;

import com.antoniokoman.basics.R;

public final class AppTheme {

    private AppTheme() {}

    // --- Цвета ---

    public static int backgroundColor(Context context) {
        return ContextCompat.getColor(context, R.color.app_background);
    }
    public static int getAppBarBackgroundColor(Context context) {
        return ContextCompat.getColor(context, R.color.app_bar_background);
    }

    public static int getAppBarContentColor(Context ctx) {
        return ContextCompat.getColor(ctx, R.color.app_bar_on_background);
    }

    public static int accentSoftColor(Context context) {
        return ContextCompat.getColor(context, R.color.app_accent_soft);
    }

    public static int iconTintColor(Context context) {
        return ContextCompat.getColor(context, R.color.app_icon_tint);
    }

    public static int fabIconTintColor(Context context) {
        return ContextCompat.getColor(context, R.color.app_fab_icon_tint);
    }

    public static int textMainColor(Context context) {
        return ContextCompat.getColor(context, R.color.app_text_main);
    }

    public static int textSecondaryColor(Context context) {
        return ContextCompat.getColor(context, R.color.app_text_secondary);
    }

    // --- Размеры из dimens ---

    public static int dimenPx(Context context, @DimenRes int resId) {
        return context.getResources().getDimensionPixelSize(resId);
    }

    // Если иногда всё-таки нужен "сырый" dp → px
    public static int dp(Context context, int value) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (value * density);
    }

    public static Drawable circleBackground(Context context) {
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.OVAL);
        shape.setColor(AppTheme.accentSoftColor(context)); // или другой цвет
        return shape;
    }

    // Прозрачности и прочее, чего нет смысла тащить в ресурсы
    public static final float ALPHA_DECORATIVE = 0.9f;
}
