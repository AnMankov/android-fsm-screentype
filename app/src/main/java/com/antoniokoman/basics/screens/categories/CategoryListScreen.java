package com.antoniokoman.basics.screens.categories;

import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import com.antoniokoman.basics.fsm.BaseScreen;
import com.antoniokoman.basics.fsm.CatListState;
import com.antoniokoman.basics.fsm.ScreenState;
import com.antoniokoman.basics.repository.Repository;

public class CategoryListScreen extends BaseScreen {
    private final Repository repo = Repository.getInstance();

    @Override
    protected void onRender() {
        FrameLayout rootFrame = (FrameLayout) cachedView;

        if (repo.catList.categories.isEmpty()) {
            renderEmptyState(rootFrame);
        } else {
            renderList(rootFrame);
        }

        // 1. Плавающая кнопка (FAB) - 64dp
        renderFloatingButton(rootFrame);
    }

    private void renderEmptyState(FrameLayout root) {
        LinearLayout emptyLayout = new LinearLayout(root.getContext());
        emptyLayout.setOrientation(LinearLayout.VERTICAL);
        emptyLayout.setGravity(Gravity.CENTER);

        TextView tv = new TextView(root.getContext());
        tv.setText("Список пуст.\nДобавьте первую категорию!");
        tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
        tv.setTextSize(18);
        tv.setPadding(0, 0, 0, dp(32));
        emptyLayout.addView(tv);

        // 5. Дополнительная кнопка существенно больше (96dp против 64dp)
        Button btnCenter = createStyledButton(96, 36);
        emptyLayout.addView(btnCenter, new LinearLayout.LayoutParams(dp(96), dp(96)));

        root.addView(emptyLayout, new FrameLayout.LayoutParams(-1, -1));
    }

    private void renderFloatingButton(FrameLayout root) {
        // Стандартный размер FAB - 64dp
        Button fab = createStyledButton(64, 28);

        FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(dp(64), dp(64));
        lp.gravity = Gravity.BOTTOM | Gravity.END;
        lp.setMargins(0, 0, dp(24), dp(24));

        root.addView(fab, lp);
    }

    // Универсальный метод создания кнопки со скруглением и тенью
    private Button createStyledButton(int sizeDp, int textSizeSp) {
        Button btn = new Button(cachedView.getContext());
        btn.setText("+");
        btn.setTextSize(textSizeSp);
        btn.setTypeface(null, Typeface.BOLD);
        btn.setTextColor(Color.WHITE);

        // 5. Скругление углов (Radius = 1/4 от размера для мягкого квадрата или size/2 для круга)
        GradientDrawable shape = new GradientDrawable();
        shape.setShape(GradientDrawable.RECTANGLE);
        shape.setColor(Color.BLACK);
        shape.setCornerRadius(dp(12)); // Скругление 12dp
        btn.setBackground(shape);

        // 5. Небольшая тень (Elevation работает начиная с API 21)
        btn.setElevation(dp(6));

        btn.setOnClickListener(v -> {
            if (listener != null) listener.onScreenStateChanged(CatListState.PR_ADD);
        });

        return btn;
    }

    // Редактируем отображение элементов списка (теперь тоже с отступом под FAB)
    private void renderList(FrameLayout root) {
        ScrollView scroll = new ScrollView(root.getContext());
        LinearLayout list = new LinearLayout(root.getContext());
        list.setOrientation(LinearLayout.VERTICAL);
        list.setPadding(dp(16), dp(16), dp(16), dp(100));

        for (int i = 0; i < repo.catList.categories.size(); i++) {
            renderCategoryItem(list, i);
        }

        scroll.addView(list);
        root.addView(scroll, new FrameLayout.LayoutParams(-1, -1));
    }

    private void renderCategoryItem(LinearLayout container, int index) {
        Button item = new Button(container.getContext());
        item.setText(repo.catList.categories.get(index).name);
        item.setAllCaps(false);
        item.setOnClickListener(v -> {
            repo.catList.index = index;
            if (listener != null) listener.onScreenStateChanged(CatListState.PR_CAT);
        });

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(-1, -2);
        lp.setMargins(0, 0, 0, dp(8));
        container.addView(item, lp);
    }

    @Override public ScreenState getState() { return CatListState.IDLE; }
}
