package com.antoniokoman.basics.fsm;

import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

public abstract class BaseScreen implements Screen {
    protected ViewGroup cachedView; // Изменим на ViewGroup для гибкости (Frame/Linear)
    protected ScreenStateListener listener;

    // Глобальный метод конвертации для всех экранов
    protected int dp(int value) {
        if (cachedView == null) return value; // Защита, если вызвали слишком рано
        return (int) (value * cachedView.getResources().getDisplayMetrics().density);
    }

    @Override
    public void onEnter(ViewGroup root, ScreenStateListener listener) {
        this.listener = listener;

        if (cachedView == null) {
            // Создаем контейнер (по умолчанию FrameLayout, так как он универсальнее)
            cachedView = new FrameLayout(root.getContext());
            cachedView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        }

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


