
package com.antoniokoman.basics.screens.settings;

import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;

import com.antoniokoman.basics.fsm.Screen;
import com.antoniokoman.basics.fsm.ScreenState;
import com.antoniokoman.basics.fsm.ScreenStateListener;
import com.antoniokoman.basics.screens.mainmenu.MainMenuState;

import java.io.Serializable;

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
        this.listener = null; //разрыв цепочки утечки памяти
        root.removeView(this.cachedView); //отцепка вьюшки (вьюшка остается в памяти экрана)
    }

    @Override
    public ScreenState getState() {
        return state;
    }

    @Override
    public void saveState(Bundle outState) {
        // Сохраняем как строку (name), это максимально надежно
        outState.putString("my_enum_state", this.state.name());
    }

    @Override
    public void restoreState(Bundle inState) {
        String stateName = inState.getString("my_enum_state");
        if (stateName != null) {
            this.state = SettingsState.valueOf(stateName);
        }
    }
}