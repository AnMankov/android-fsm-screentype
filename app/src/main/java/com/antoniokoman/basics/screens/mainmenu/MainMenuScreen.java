package com.antoniokoman.basics.screens.mainmenu;

import android.view.ViewGroup;
import android.widget.Button;

import com.antoniokoman.basics.fsm.Screen;
import com.antoniokoman.basics.fsm.ScreenState;
import com.antoniokoman.basics.fsm.ScreenStateListener;

public class MainMenuScreen implements Screen {

    private MainMenuState state = MainMenuState.IDLE;
    private ScreenStateListener listener;

    public MainMenuScreen(ScreenStateListener listener) {
        this.listener = listener;
    }

    @Override
    public ScreenState getState() {
        return state;
    }

    @Override
    public void onEnter(ViewGroup root) {
        Button settingsButton = new Button(root.getContext());
        settingsButton.setText("Settings");
        settingsButton.setOnClickListener(v -> {
            state = MainMenuState.PRESSED_SETTINGS;
            if (listener != null) {
                listener.onScreenStateChanged(state);
            }
        });

        root.addView(settingsButton);
    }

    @Override
    public void onExit(ViewGroup root) {
        root.removeAllViews();
    }

    private void onSettingsButtonClicked() {
        state = MainMenuState.PRESSED_SETTINGS;
        if (listener != null) {
            listener.onScreenStateChanged(state);
        }
    }
}
