package com.antoniokoman.basics.screens.categories;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.antoniokoman.basics.R;
import com.antoniokoman.basics.app.AppTheme;
import com.antoniokoman.basics.fsm.CatCreateState;
import com.antoniokoman.basics.fsm.ScreenState;
import com.antoniokoman.basics.repository.Repository;
import com.antoniokoman.basics.screens.base.AppBarView;
import com.antoniokoman.basics.screens.base.BaseContentScreen;

public class CategoryCreateScreen extends BaseContentScreen {

    private EditText nameEdit;
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
        boolean expanded = isExpanded(context);

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

        // Заголовок поля
        TextView label = new TextView(context);
        label.setText(context.getString(R.string.cat_create_name_label));
        label.setTextColor(AppTheme.textMainColor(context));

        float labelSizeSp = context.getResources()
                .getDimension(expanded
                        ? R.dimen.empty_title_tablet
                        : R.dimen.empty_title_phone)
                / context.getResources().getDisplayMetrics().scaledDensity;
        label.setTextSize(labelSizeSp);
        label.setGravity(Gravity.START);

        int labelTopPadding = AppTheme.dimenPx(
                context,
                expanded
                        ? R.dimen.cat_create_label_top_padding_tablet
                        : R.dimen.cat_create_label_top_padding_phone
        );
        int labelBottomPadding =
                AppTheme.dimenPx(context, R.dimen.cat_create_label_bottom_padding);
        label.setPadding(0, labelTopPadding, 0, labelBottomPadding);

        form.addView(label, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        // Поле ввода имени
        nameEdit = new EditText(context);
        nameEdit.setHint(context.getString(R.string.cat_create_name_hint));
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

        LinearLayout.LayoutParams etLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        form.addView(nameEdit, etLp);
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

