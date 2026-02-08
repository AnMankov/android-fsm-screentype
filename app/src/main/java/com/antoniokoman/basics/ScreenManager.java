package com.antoniokoman.basics;

import android.view.ViewGroup;

public class ScreenManager {

    private final ViewGroup root; //корневой контейнер у ScreenManager один и тот же за всю жизнь менеджера, мы не хотим случайно его переприсвоить
    private Screen currentScreen;
    private ScreenType currentType; //«текущий логический state», как enum State в embedded‑FSM

    public ScreenManager(ViewGroup root) {
        this.root = root;
    }

    public void showScreen(ScreenType type) {
        // 1. уходим с текущего, если есть
        if (currentScreen != null) {
            currentScreen.onExit(root);
        }

        // 2. создаём новый Screen через фабрику в enum
        Screen newScreen = type.create();

        // 3. включаем новый
        currentScreen = newScreen;
        currentType = type;
        currentScreen.onEnter(root);
    }

    public ScreenType getCurrentType() {
        return currentType;
    }

    public Screen getCurrentScreen() {
        return currentScreen;
    }
}
