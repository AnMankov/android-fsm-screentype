package com.antoniokoman.basics.fsm;

import com.antoniokoman.basics.screens.mainmenu.MainMenuScreen;
import com.antoniokoman.basics.screens.settings.SettingsScreen;

public enum ScreenType { //это просто фабрика. Он не хранит экземпляры.
    MAIN_MENU {
        @Override
        Screen create() { return new MainMenuScreen(); }
    },
    SETTINGS {
        @Override Screen create() { return new SettingsScreen(); }
    };

    abstract Screen create();
}
