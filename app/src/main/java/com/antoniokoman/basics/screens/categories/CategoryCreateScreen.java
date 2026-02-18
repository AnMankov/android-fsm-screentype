package com.antoniokoman.basics.screens.categories;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.ContextThemeWrapper;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.antoniokoman.basics.R;
import com.antoniokoman.basics.app.AppTheme;
import com.antoniokoman.basics.fsm.CatCreateState;
import com.antoniokoman.basics.fsm.ScreenState;
import com.antoniokoman.basics.repository.Repository;
import com.antoniokoman.basics.screens.base.AppBarView;
import com.antoniokoman.basics.screens.base.BaseContentScreen;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class CategoryCreateScreen extends BaseContentScreen {

    private TextInputEditText nameEdit;
    private boolean isValid = false;

    private final Repository repo = Repository.getInstance();

    @Override
    protected AppBarView createAppBar(Context context) {
        AppBarView appBar = new AppBarView(context);
        appBar.setTitle(context.getString(R.string.cat_create_title));
        appBar.setNavigationIcon(R.drawable.outline_arrow_back, v -> onBackPressed());
        return appBar;
    }

    @Override
    protected void onBackPressed() {
        if (listener != null) listener.onScreenStateChanged(CatCreateState.PR_BACK);
    }

    @Override
    protected void onRenderContent(FrameLayout contentContainer) {
        Context context = contentContainer.getContext();

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.TOP);
        FrameLayout.LayoutParams rootLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        contentContainer.addView(container, rootLp);

        LinearLayout form = new LinearLayout(context);
        form.setOrientation(LinearLayout.VERTICAL);
        form.setGravity(Gravity.START);
        int horizontalPadding = AppTheme.dimenPx(context, R.dimen.cat_create_horizontal_padding);
        form.setPadding(horizontalPadding, 0, horizontalPadding, 0);
        container.addView(form, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // TextInputLayout с outlined-рамкой и floating label
        TextInputLayout nameLayout = new TextInputLayout(new ContextThemeWrapper(
                context,
                R.style.Theme_Basics   // твоя тема, если экран/Activity не под ней
        ));

        nameLayout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);
        nameLayout.setHint(context.getString(R.string.cat_create_name_label)); // "Название категории"
        // У edit'а убираем hint (или оставляем только layout)
        nameEdit = new TextInputEditText(nameLayout.getContext());
        // Либо совсем без hint:
        nameEdit.setHint(null);
        // либо перенеси текст из R.string.cat_create_name_hint в layout и здесь не ставь

        LinearLayout.LayoutParams tilLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        form.addView(nameLayout, tilLp);

        // Само поле ввода
        nameEdit = new TextInputEditText(nameLayout.getContext());
        nameEdit.setHint(context.getString(R.string.cat_create_name_hint)); // "Введите название"
        nameEdit.setTextColor(AppTheme.textMainColor(context));
        nameEdit.setHintTextColor(AppTheme.textSecondaryColor(context));
        nameEdit.setSingleLine(true);
        int pad = AppTheme.dimenPx(context, R.dimen.cat_create_field_padding);
        nameEdit.setPadding(pad, pad, pad, pad);
        nameEdit.setImeOptions(EditorInfo.IME_FLAG_NO_FULLSCREEN);

        nameEdit.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                isValid = !s.toString().trim().isEmpty();
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        nameLayout.addView(
                nameEdit,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                )
        );
    }

    @Override
    protected boolean hasFab() {
        return false;
    }

    @Override
    protected void onFabClick() {
        // не будет вызван
    }

    @Override
    public ScreenState getState() {
        return CatCreateState.IDLE;
    }
}
