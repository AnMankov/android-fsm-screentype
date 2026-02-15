package com.antoniokoman.basics.screens.categories;

import com.antoniokoman.basics.fsm.ScreenState;

public enum CatListState implements ScreenState {
    IDLE,
    PR_ADD,    // Нажали "Плюс"
    PR_EDIT,   // Нажали "Карандаш" на категории
    PR_DEL,    // Нажали "Корзину"
    PR_CAT     // Выбрали категорию для работы
}
