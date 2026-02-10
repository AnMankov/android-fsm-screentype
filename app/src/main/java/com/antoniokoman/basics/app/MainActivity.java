package com.antoniokoman.basics.app;

import android.os.Bundle;
import android.util.Log;
import android.widget.FrameLayout;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.antoniokoman.basics.fsm.ScreenManager;
import com.antoniokoman.basics.fsm.ScreenType;
import com.antoniokoman.basics.screens.mainmenu.MainMenuState;
import com.antoniokoman.basics.screens.settings.SettingsState;

public class MainActivity extends AppCompatActivity {

    private ScreenManager screenManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Создаём корневой контейнер на весь экран
        FrameLayout root = new FrameLayout(this);
        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, //width
                ViewGroup.LayoutParams.MATCH_PARENT  //height
        ));

        // 2. Делаем его корнем интерфейса Activity
        setContentView(root);

        // 3. Создаём ScreenManager на этот root
        screenManager = new ScreenManager(root);
        screenManager.registerTransition(
                ScreenType.MAIN_MENU,
                MainMenuState.PRESSED_SETTINGS,
                ScreenType.SETTINGS
        );
        screenManager.registerTransition(
                ScreenType.SETTINGS,
                SettingsState.PRESSED_BACK,
                ScreenType.MAIN_MENU
        );

        Log.d("FSM", "\n" + screenManager.dumpGraph());

        // 4. Показываем стартовый экран
        screenManager.showScreen(ScreenType.MAIN_MENU);
    }
}

