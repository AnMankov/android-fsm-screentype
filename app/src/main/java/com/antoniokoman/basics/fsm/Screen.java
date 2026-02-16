
package com.antoniokoman.basics.fsm;

import android.os.Bundle;
import android.view.ViewGroup;

public interface Screen {
    void onEnter(ViewGroup root, ScreenStateListener listener);
    void onExit(ViewGroup root);

    // Новый метод для освобождения графических ресурсов
    default void clearGraphics() {}

    ScreenState getState();

    default void saveState(Bundle outState) {}
    default void restoreState(Bundle inState) {}
}