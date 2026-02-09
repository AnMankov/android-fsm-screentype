package com.antoniokoman.basics;

import android.view.ViewGroup;

public interface Screen {
    void onEnter(ViewGroup root);
    void onExit(ViewGroup root);

    ScreenState getState();
    void setStateListener(ScreenStateListener listener);
}
