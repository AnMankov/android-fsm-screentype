package com.antoniokoman.basics.fsm;

import android.view.ViewGroup;

public interface Screen {
    void onEnter(ViewGroup root, ScreenStateListener listener);
    void onExit(ViewGroup root);

    ScreenState getState();
}
