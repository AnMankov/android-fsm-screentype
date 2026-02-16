package com.antoniokoman.basics.screens.categories;

import android.widget.Button;
import com.antoniokoman.basics.fsm.*;
import com.antoniokoman.basics.repository.Repository;

public class CategoryCreateScreen extends BaseScreen {
    @Override
    protected void onRender() {
        Button btn = new Button(cachedView.getContext());
        btn.setText("СОЗДАТЬ И В КОНВЕРТЕР");
        btn.setOnClickListener(v -> {
            Repository.CategoryList.CategoryData cat = new Repository.CategoryList.CategoryData();
            cat.name = "Категория " + System.currentTimeMillis();
            Repository.getInstance().catList.categories.add(cat);
            Repository.getInstance().catList.index = Repository.getInstance().catList.categories.size() - 1;
            if (listener != null) listener.onScreenStateChanged(CatCreateState.PR_CRE);
        });
        cachedView.addView(btn);

        // Кнопка Назад (Правило 7 в MainActivity)
        renderButton("ОТМЕНА", CatCreateState.PR_BACK);
    }
    private void renderButton(String t, ScreenState s) {
        Button b = new Button(cachedView.getContext());
        b.setText(t);
        b.setOnClickListener(v -> { if (listener != null) listener.onScreenStateChanged(s); });
        cachedView.addView(b);
    }
    @Override public ScreenState getState() { return CatCreateState.IDLE; }
}
