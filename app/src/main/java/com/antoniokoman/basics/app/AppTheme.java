package com.antoniokoman.basics.app;

import android.graphics.Color;

public final class AppTheme {
    // 1. Цветовая палитра (светлая и чистая)
    public static final int COLOR_BACKGROUND = Color.parseColor("#F5F7F9");
    public static final int COLOR_ACCENT_SOFT = Color.parseColor("#C8E6C9"); // Зеленый для FAB
    public static final int COLOR_ICON_TINT = Color.parseColor("#424242");

    public static final int COLOR_TEXT_MAIN = Color.parseColor("#212121");
    public static final int COLOR_TEXT_SECONDARY = Color.parseColor("#757575");

    // 2. Геометрия (скругления и рамки)
    public static final int RADIUS_STANDARD_DP = 16;
    public static final int STROKE_WIDTH_DP = 2;        // Тонкий контур для эффекта слоев
    public static final float ALPHA_DECORATIVE = 0.2f;  // Легкая прозрачность центральной иконки

    // 3. Тени и слои
    public static final float ELEVATION_FAB_DP = 6f;
    public static final float ELEVATION_CARD_DP = 2f;
    public static final int LAYER_OFFSET_DP = 12;       // Смещение карточек в стеке

    // 4. Размеры элементов
    public static final int ICON_SIZE_BIG_DP = 48;
    public static final int ICON_SIZE_FAB_DP = 28;

    private AppTheme() {}
}
