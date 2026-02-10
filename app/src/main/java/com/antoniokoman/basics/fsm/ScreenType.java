package com.antoniokoman.basics.fsm;

import com.antoniokoman.basics.screens.mainmenu.MainMenuScreen;
import com.antoniokoman.basics.screens.settings.SettingsScreen;

public enum ScreenType {
    MAIN_MENU {
        @Override
        Screen create(ScreenStateListener listener) { return new MainMenuScreen(listener); }
    },
    SETTINGS {
        @Override Screen create(ScreenStateListener listener) { return new SettingsScreen(listener); }
    };

    private Screen instance;

    abstract Screen create(ScreenStateListener listener);

    public Screen getInstance(ScreenStateListener listener) {
        if (instance == null) {
            instance = create(listener);
        }
        return instance;
    }
}
