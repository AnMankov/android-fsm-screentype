package com.antoniokoman.basics.fsm;

import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.antoniokoman.basics.app.AppTheme;

public abstract class BaseScreen implements Screen {
    protected FrameLayout cachedView; // Сразу фиксируем тип, чтобы не кастить постоянно
    protected ScreenStateListener listener;

    // Берем density из контекста напрямую — так надежнее
    protected int dp(int value) {
        if (cachedView != null) {
            return AppTheme.dp(cachedView.getContext(), value);
        }
        // fallback на случай, если кто-то вызовет dp() до onEnter
        return value * 2; // или 0, или бросить IllegalStateException — как тебе удобнее
    }

    @Override
    public void onEnter(ViewGroup root, ScreenStateListener listener) {
        this.listener = listener;

        // 1. ОЧИСТКА КОРНЯ (Чтобы экраны не накладывались)
        root.removeAllViews();

        if (cachedView == null) {
            cachedView = new FrameLayout(root.getContext());
            // 2. ЧЕТКИЕ ПАРАМЕТРЫ (Чтобы верстка не "ехала")
            cachedView.setLayoutParams(new FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.MATCH_PARENT,
                    FrameLayout.LayoutParams.MATCH_PARENT
            ));
        }

        // 3. ОЧИСТКА ЭКРАНА (Перед каждой перерисовкой)
        cachedView.removeAllViews();

        onRender();
        root.addView(cachedView);
    }

    protected abstract void onRender();

    @Override
    public void onExit(ViewGroup root) {
        this.listener = null;
        if (cachedView != null) {
            root.removeView(cachedView);
        }
    }

    @Override
    public void clearGraphics() {
        cachedView = null;
    }
}
