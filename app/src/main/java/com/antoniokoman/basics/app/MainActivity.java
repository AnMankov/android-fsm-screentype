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

        // --- ГРАФ ПЕРЕХОДОВ UNITCRAFT (16 ПРАВИЛ) ---

        // 1-4: Навигация из списка категорий
        screenManager.registerTransition(ScreenType.CAT_LIST, CatListState.PR_DEL, ScreenType.CAT_LIST);
        screenManager.registerTransition(ScreenType.CAT_LIST, CatListState.PR_EDIT, ScreenType.CAT_EDITOR);
        screenManager.registerTransition(ScreenType.CAT_LIST, CatListState.PR_ADD, ScreenType.CAT_CREATE);
        screenManager.registerTransition(ScreenType.CAT_LIST, CatListState.PR_CAT, ScreenType.CONV_VIEW);

        // 5-6: Навигация из редактора категорий
        screenManager.registerTransition(ScreenType.CAT_EDITOR, CatEditorState.PR_SAVE, ScreenType.CAT_LIST);
        screenManager.registerTransition(ScreenType.CAT_EDITOR, CatEditorState.PR_BACK, ScreenType.CAT_LIST);

        // 7-8: Навигация из создания категории
        screenManager.registerTransition(ScreenType.CAT_CREATE, CatCreateState.PR_BACK, ScreenType.CAT_LIST);
        screenManager.registerTransition(ScreenType.CAT_CREATE, CatCreateState.PR_CRE, ScreenType.CONV_VIEW);

        // 9-12: Навигация из просмотра конвертера
        screenManager.registerTransition(ScreenType.CONV_VIEW, ConvViewState.PR_BACK, ScreenType.CAT_LIST);
        screenManager.registerTransition(ScreenType.CONV_VIEW, ConvViewState.PR_EDIT, ScreenType.CONV_EDITOR);
        screenManager.registerTransition(ScreenType.CONV_VIEW, ConvViewState.PR_DEL, ScreenType.CONV_VIEW);
        screenManager.registerTransition(ScreenType.CONV_VIEW, ConvViewState.PR_CRE, ScreenType.CONV_CREATE);

        // 13-14: Навигация из редактора конвертера
        screenManager.registerTransition(ScreenType.CONV_EDITOR, ConvEditorState.PR_BACK, ScreenType.CONV_VIEW);
        screenManager.registerTransition(ScreenType.CONV_EDITOR, ConvEditorState.PR_SAVE, ScreenType.CONV_VIEW);

        // 15-16: Навигация из создания конвертера
        screenManager.registerTransition(ScreenType.CONV_CREATE, ConvCreateState.PR_BACK, ScreenType.CONV_VIEW);
        screenManager.registerTransition(ScreenType.CONV_CREATE, ConvCreateState.PR_CRE, ScreenType.CONV_VIEW);

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