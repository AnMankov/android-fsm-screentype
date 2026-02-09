package com.antoniokoman.basics;

import android.view.ViewGroup;

public class MainMenuScreen implements Screen {

    private MainMenuState state = MainMenuState.IDLE;
    private ScreenStateListener stateListener;

    @Override
    public void setStateListener(ScreenStateListener listener) {
        this.stateListener = listener;
    }

    @Override
    public ScreenState getState() {
        return state;
    }

    @Override
    public void onEnter(ViewGroup root) {
        // здесь создадим вьюшки и навесим onClick:
        // button.setOnClickListener(v -> onSettingsButtonClicked());
    }

    @Override
    public void onExit(ViewGroup root) {
        root.removeAllViews();
    }

    private void onSettingsButtonClicked() {
        state = MainMenuState.PRESSED_SETTINGS;
        if (stateListener != null) {
            stateListener.onScreenStateChanged(state);
        }
    }
}
