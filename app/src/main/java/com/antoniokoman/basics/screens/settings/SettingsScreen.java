package com.antoniokoman.basics.screens.settings;

import android.view.ViewGroup;
import android.widget.Button;

import com.antoniokoman.basics.fsm.Screen;
import com.antoniokoman.basics.fsm.ScreenState;
import com.antoniokoman.basics.fsm.ScreenStateListener;
import com.antoniokoman.basics.screens.mainmenu.MainMenuState;

public class SettingsScreen implements Screen {

    private SettingsState state = SettingsState.IDLE;
    private ScreenStateListener listener;

    public SettingsScreen(ScreenStateListener listener) {
        this.listener = listener;
    }

    @Override
    public void onEnter(ViewGroup root) {
        Button backButton = new Button(root.getContext());
        backButton.setText("Back");
        backButton.setOnClickListener(v -> {
            state = SettingsState.PRESSED_BACK;
            if (listener != null) {
                listener.onScreenStateChanged(state);
            }
        });

        root.addView(backButton);
    }

    @Override
    public void onExit(ViewGroup root) {
        root.removeAllViews();
    }

    @Override
    public ScreenState getState() {
        return state;
    }
}
