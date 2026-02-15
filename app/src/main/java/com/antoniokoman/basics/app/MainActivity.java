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
            // Достаем один Bundle и менеджер сам всё восстановит
            Bundle fsmData = savedInstanceState.getBundle("fsm_data");
            screenManager.restoreEverything(fsmData);
        } else {
            screenManager.navigateTo(ScreenType.MAIN_MENU, false);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) { //вызывается при повороте экрана или временном закрытии приложения системой для освобождения памяти; outState - мешок для временного хранения примитивов чтобы они не потерялись при пересоздании экрана
        //по кнопке Назад не вызывается (считается что пользователь сам закрывает активити)
        //используется для временных данных интерфейса
        //для постоянных данных надо использовать метод onPause с сохранением в БД|файлы
        super.onSaveInstanceState(outState); //стандартный механизм сохранения состояний UI: текст в EditText, положение прокрутки ScrollView и т.д.
        //при пересоздании этот бандл будет передан в onCreate и onRestoreInstanceState для восстановления данных
        outState.putBundle("fsm_data", screenManager.saveEverything()); // Просто просим менеджер упаковать чемоданы
    }

    @Override
    public void onBackPressed() { //пользователь нажал "Назад" или сделал жест
        if (!screenManager.handleBackPressed()) {
            super.onBackPressed(); //система берет верхний экран, выбрасывает его и показывает тот что лежал под ним, если под ним ничего, то приложение закрывается
        }
    }
}