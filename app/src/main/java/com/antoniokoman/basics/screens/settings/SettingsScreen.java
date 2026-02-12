package com.antoniokoman.basics.screens.settings;

import android.view.ViewGroup;
import android.widget.Button;

import com.antoniokoman.basics.fsm.Screen;
import com.antoniokoman.basics.fsm.ScreenState;
import com.antoniokoman.basics.fsm.ScreenStateListener;
import com.antoniokoman.basics.screens.mainmenu.MainMenuState;

public class SettingsScreen implements Screen {

    private Button cachedView;
    private SettingsState state = SettingsState.IDLE;
    private ScreenStateListener listener;

    @Override
    public void onEnter(ViewGroup root, ScreenStateListener listener) {
        this.listener = listener;
        if (cachedView == null) {
            cachedView = new Button(root.getContext()); //Создаем один раз за всю жизнь приложения
            cachedView.setText("Back");
            cachedView.setOnClickListener(v -> {
                state = SettingsState.PRESSED_BACK;
                if (listener != null) {
                    listener.onScreenStateChanged(state);
                }
            });
        }

        root.addView(cachedView); //аттач вьюшки к новому родителю
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
