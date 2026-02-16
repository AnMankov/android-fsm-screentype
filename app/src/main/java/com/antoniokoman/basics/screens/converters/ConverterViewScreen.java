package com.antoniokoman.basics.screens.converters;

import android.app.AlertDialog;
import android.widget.Button;
import android.widget.TextView;
import com.antoniokoman.basics.fsm.*;
import com.antoniokoman.basics.repository.Repository;

public class ConverterViewScreen extends BaseScreen {
    private final Repository repo = Repository.getInstance();

    @Override
    protected void onRender() {
        // 1. Получаем текущую категорию
        Repository.CategoryList.CategoryData cat = repo.catList.categories.get((int) repo.catList.index);

        TextView title = new TextView(cachedView.getContext());
        title.setText("КОНВЕРТЕРЫ В: " + cat.name);
        title.setPadding(20, 20, 20, 20);
        cachedView.addView(title);

        // 2. Список конвертеров с кнопками удаления
        for (int i = 0; i < cat.converters.size(); i++) {
            final int converterIndex = i;
            Repository.ConverterData conv = cat.converters.get(i);

            Button btnDel = new Button(cachedView.getContext());
            btnDel.setText("УДАЛИТЬ: " + conv.name);
            btnDel.setOnClickListener(v -> showDeleteDialog(cat, converterIndex));
            cachedView.addView(btnDel);
        }

        // 3. Навигационные кнопки (Правила 9, 10, 12)
        renderButton("+ СОЗДАТЬ НОВЫЙ", ConvViewState.PR_CRE);
        renderButton("< НАЗАД В КАТЕГОРИИ", ConvViewState.PR_BACK);
    }

    private void showDeleteDialog(Repository.CategoryList.CategoryData cat, int index) {
        new AlertDialog.Builder(cachedView.getContext())
                .setTitle("Удаление")
                .setMessage("Точно удалить этот конвертер?")
                .setPositiveButton("Да", (dialog, which) -> {
                    // Удаляем из данных
                    cat.converters.remove(index);
                    // Посылаем сигнал менеджеру (Правило 11: CONV_VIEW -> PR_DEL -> CONV_VIEW)
                    if (listener != null) listener.onScreenStateChanged(ConvViewState.PR_DEL);
                })
                .setNegativeButton("Нет", null)
                .show();
    }

    private void renderButton(String text, ScreenState state) {
        Button b = new Button(cachedView.getContext());
        b.setText(text);
        b.setOnClickListener(v -> {
            if (listener != null) listener.onScreenStateChanged(state);
        });
        cachedView.addView(b);
    }

    @Override
    public ScreenState getState() { return ConvViewState.IDLE; }
}
