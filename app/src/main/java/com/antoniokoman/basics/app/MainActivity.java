package com.antoniokoman.basics.app;

import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.antoniokoman.basics.fsm.ScreenManager;
import com.antoniokoman.basics.fsm.ScreenType;
import com.antoniokoman.basics.screens.mainmenu.MainMenuState;
import com.antoniokoman.basics.screens.settings.SettingsState;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private ScreenManager screenManager;
    private static final String KEY_HISTORY = "history";
    private static final String KEY_CURRENT_SCREEN = "current_screen";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout root = new FrameLayout(this);
        root.setLayoutParams(new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        ));
        setContentView(root);

        screenManager = new ScreenManager(root);

        // Регистрация графа переходов
        screenManager.registerTransition(ScreenType.MAIN_MENU, MainMenuState.PRESSED_SETTINGS, ScreenType.SETTINGS);
        screenManager.registerTransition(ScreenType.SETTINGS, SettingsState.PRESSED_BACK, ScreenType.MAIN_MENU);

        Log.d("FSM", "\n" + screenManager.dumpGraph());

        if (savedInstanceState != null) {
            // Восстанавливаем историю и текущий экран
            ArrayList<String> savedHistory = savedInstanceState.getStringArrayList(KEY_HISTORY);
            screenManager.restoreHistory(savedHistory);

            String savedType = savedInstanceState.getString(KEY_CURRENT_SCREEN);
            screenManager.navigateTo(ScreenType.valueOf(savedType), false);
        } else {
            // Первый запуск
            screenManager.navigateTo(ScreenType.MAIN_MENU, false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (screenManager.getCurrentType() != null) {
            outState.putString(KEY_CURRENT_SCREEN, screenManager.getCurrentType().name());
            outState.putStringArrayList(KEY_HISTORY, screenManager.getHistoryAsState());
        }
    }

    @Override
    public void onBackPressed() {
        if (!screenManager.handleBackPressed()) {
            super.onBackPressed();
        }
    }
}
