package com.antoniokoman.basics;

import android.view.ViewGroup;

public interface Screen {
    // Вызывается, когда экран становится активным, сюда отдаём корневой контейнер для добавления View
    void onEnter(ViewGroup root);

    // Вызывается, когда экран уходит, здесь можно убрать свои View, отписаться от слушателей и т.д.
    void onExit(ViewGroup root);
}
