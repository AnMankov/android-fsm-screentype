package com.antoniokoman.basics.screens.converters;

import android.content.Context;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.app.AlertDialog;

import com.antoniokoman.basics.R;
import com.antoniokoman.basics.app.AppTheme;
import com.antoniokoman.basics.fsm.ConvViewState;
import com.antoniokoman.basics.fsm.ScreenState;
import com.antoniokoman.basics.repository.Repository;
import com.antoniokoman.basics.screens.base.AppBarView;
import com.antoniokoman.basics.screens.base.BaseContentScreen;

public class ConvertersViewScreen extends BaseContentScreen {

    private final Repository repo = Repository.getInstance();

    @Override
    protected AppBarView createAppBar(Context context) {
        AppBarView appBar = new AppBarView(context);

        // Текущая категория
        Repository.CategoryList.CategoryData cat = null;
        if (!repo.catList.categories.isEmpty()
                && repo.catList.index >= 0
                && repo.catList.index < repo.catList.categories.size()) {
            cat = repo.catList.categories.get((int) repo.catList.index);
        }

        // Заголовок: имя категории или дефолт
        String title = (cat != null && cat.name != null && !cat.name.isEmpty())
                ? cat.name
                : context.getString(R.string.conv_view_title_fallback);
        appBar.setTitle(title);

        // Кнопка назад: PR_BACK (Правило 9)
        appBar.setNavigationIcon(R.drawable.outline_arrow_back, v -> {
            if (listener != null) {
                listener.onScreenStateChanged(ConvViewState.PR_BACK);
            }
        });

        return appBar;
    }

    @Override
    protected void onRenderContent(FrameLayout contentContainer) {
        Context context = contentContainer.getContext();

        // Корневой контейнер под контент (как в CategoryCreateScreen)
        LinearLayout root = new LinearLayout(context);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setGravity(Gravity.TOP);
        FrameLayout.LayoutParams rootLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        contentContainer.addView(root, rootLp);

        // ScrollView для списка конвертеров
        ScrollView scrollView = new ScrollView(context);
        scrollView.setFillViewport(true);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        );
        root.addView(scrollView, scrollLp);

        // Внутренний контейнер формы/списка
        LinearLayout listContainer = new LinearLayout(context);
        listContainer.setOrientation(LinearLayout.VERTICAL);
        listContainer.setGravity(Gravity.START);

        int horizontalPadding = AppTheme.dimenPx(context, R.dimen.cat_create_horizontal_padding);
        int topPadding = AppTheme.dimenPx(context, R.dimen.cat_create_label_top_padding_phone);
        int betweenItems = AppTheme.dimenPx(context, R.dimen.spacing_medium);
        listContainer.setPadding(horizontalPadding, topPadding, horizontalPadding, 0);

        scrollView.addView(listContainer, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // Текущая категория
        if (repo.catList.categories.isEmpty()
                || repo.catList.index < 0
                || repo.catList.index >= repo.catList.categories.size()) {
            // Заглушка, если по какой-то причине категорий нет
            TextView empty = new TextView(context);
            empty.setText(R.string.conv_view_no_category);
            listContainer.addView(empty);
            // Нижняя панель с кнопками всё равно добавим ниже
        } else {
            Repository.CategoryList.CategoryData cat =
                    repo.catList.categories.get((int) repo.catList.index);

            // Список конвертеров (временный упрощённый UI)
            for (int i = 0; i < cat.converters.size(); i++) {
                final int converterIndex = i;
                Repository.ConverterData conv = cat.converters.get(i);

                Button btnDel = new Button(context);
                btnDel.setText(conv.name);
                LinearLayout.LayoutParams delLp = new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                );
                delLp.bottomMargin = betweenItems;
                btnDel.setLayoutParams(delLp);

                btnDel.setOnClickListener(v ->
                        showDeleteDialog(context, cat, converterIndex)
                );

                listContainer.addView(btnDel);
            }
        }

    }

    private void showDeleteDialog(
            Context context,
            Repository.CategoryList.CategoryData cat,
            int index
    ) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.conv_view_delete_title)
                .setMessage(R.string.conv_view_delete_message)
                .setPositiveButton(R.string.conv_view_delete_yes, (dialog, which) -> {
                    cat.converters.remove(index);
                    if (listener != null) listener.onScreenStateChanged(ConvViewState.PR_DEL);
                })
                .setNegativeButton(R.string.conv_view_delete_no, null)
                .show();
    }

    @Override
    public ScreenState getState() {
        return ConvViewState.IDLE;
    }

    @Override
    protected void onFabClick() {
        if (listener != null) {
            listener.onScreenStateChanged(ConvViewState.PR_CRE);
        }
    }
}
