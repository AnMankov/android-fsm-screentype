package com.antoniokoman.basics;

//есть небольшой набор допустимых состояний автомата (enum), и для каждого состояния чётко прописано, какая «подпрограмма экрана» за него отвечает

public enum ScreenType {
    MAIN_MENU {
        @Override public Screen create() { return new MainMenuScreen(); }
    },
    SETTINGS {
        @Override public Screen create() { return new SettingsScreen(); }
    };

    public abstract Screen create();
}

