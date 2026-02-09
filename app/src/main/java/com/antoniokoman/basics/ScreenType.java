package com.antoniokoman.basics;

public enum ScreenType {
    MAIN_MENU {
        @Override Screen create() { return new MainMenuScreen(); }
    },
    SETTINGS {
        @Override Screen create() { return new SettingsScreen(); }
    };

    private Screen instance;

    abstract Screen create();

    public Screen getInstance() {
        if (instance == null) {
            instance = create();
        }
        return instance;
    }
}
