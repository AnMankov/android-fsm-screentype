package com.antoniokoman.basics.screens.categories;

import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.antoniokoman.basics.repository.Repository;
import com.antoniokoman.basics.fsm.Screen;
import com.antoniokoman.basics.fsm.ScreenState;
import com.antoniokoman.basics.fsm.ScreenStateListener;

public class CategoryListScreen implements Screen {

    private LinearLayout cachedView;
    private ScreenStateListener listener;
    private final Repository repo = Repository.getInstance();

    @Override
    public void onEnter(ViewGroup root, ScreenStateListener listener) {
        this.listener = listener;

        if (cachedView == null) {
            cachedView = new LinearLayout(root.getContext());
            cachedView.setOrientation(LinearLayout.VERTICAL);
            render(); // Рисуем содержимое
        }

        root.addView(cachedView);
    }

    private void render() {
        cachedView.removeAllViews();

        // 1. Кнопка добавления новой категории
        Button btnAdd = new Button(cachedView.getContext());
        btnAdd.setText("+ Добавить категорию");
        btnAdd.setOnClickListener(v -> {
            if (listener != null) listener.onScreenStateChanged(CatListState.PR_ADD);
        });
        cachedView.addView(btnAdd);

        // 2. Список категорий из репозитория
        for (int i = 0; i < repo.catList.categories.size(); i++) {
            Repository.CategoryList.CategoryData cat = repo.catList.categories.get(i);

            Button catBtn = new Button(cachedView.getContext());
            catBtn.setText(cat.name);

            final int index = i;
            catBtn.setOnClickListener(v -> {
                repo.catList.index = index; // Запоминаем выбор в репозитории
                if (listener != null) listener.onScreenStateChanged(CatListState.PR_CAT);
            });

            cachedView.addView(catBtn);
        }

        if (repo.catList.categories.isEmpty()) {
            TextView tv = new TextView(cachedView.getContext());
            tv.setText("Список пуст. Создайте первую категорию!");
            cachedView.addView(tv);
        }
    }

    @Override
    public void onExit(ViewGroup root) {
        this.listener = null;
        root.removeView(cachedView);
    }

    @Override
    public ScreenState getState() {
        return CatListState.IDLE;
    }
}
