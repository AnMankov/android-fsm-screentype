package com.antoniokoman.basics.screens.base;

import android.content.Context;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.antoniokoman.basics.R;
import com.antoniokoman.basics.app.AppTheme;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class OutlinedTextFieldView extends LinearLayout {

    private final TextInputLayout layout;
    private final TextInputEditText edit;

    public OutlinedTextFieldView(Context context) {
        this(context, null);
    }

    public OutlinedTextFieldView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public OutlinedTextFieldView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        setOrientation(VERTICAL);

        ContextThemeWrapper themed = new ContextThemeWrapper(context, R.style.Theme_Basics);

        layout = new TextInputLayout(themed);
        layout.setBoxBackgroundMode(TextInputLayout.BOX_BACKGROUND_OUTLINE);

        edit = new TextInputEditText(layout.getContext());
        edit.setHint(null);

        TextInputLayout.LayoutParams lp = new TextInputLayout.LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        );
        layout.addView(edit, lp);

        edit.setTextColor(AppTheme.textMainColor(context));
        edit.setHintTextColor(AppTheme.textSecondaryColor(context));
        edit.setSingleLine(true);
        edit.setImeOptions(EditorInfo.IME_FLAG_NO_FULLSCREEN);

        addView(layout, new LayoutParams(
                LayoutParams.MATCH_PARENT,
                LayoutParams.WRAP_CONTENT
        ));
    }

    public void setReadOnlyIconField(int iconResId,
                                     View.OnClickListener clickListener,
                                     int cdResId) {
        // Запрет ввода
        edit.setEnabled(false);
        edit.setFocusable(false);
        edit.setFocusableInTouchMode(false);
        edit.setKeyListener(null);
        edit.setText("");

        // Иконка-превью
        layout.setStartIconDrawable(ContextCompat.getDrawable(getContext(), iconResId));
        layout.setStartIconContentDescription(getContext().getString(cdResId));
        layout.setStartIconOnClickListener(clickListener);
        layout.setStartIconTintList(null);

        // Клик по всему полю (область под лейблом + рамка)
        layout.setClickable(true);
        layout.setFocusable(true);
        layout.setOnClickListener(clickListener);

        // Клик по самому EditText
        edit.setClickable(true);
        edit.setOnClickListener(clickListener);
        edit.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) {
                v.performClick();
            }
        });
    }

    public void setOnFieldClickListener(View.OnClickListener listener) {
        layout.setClickable(true);
        layout.setFocusable(true);
        layout.setOnClickListener(listener);
        edit.setClickable(true);
        edit.setOnClickListener(listener);
    }


    public TextInputLayout getTextInputLayout() {
        return layout;
    }

    // --- Публичное API ---

    public void setLabel(CharSequence label) {
        layout.setHint(label);
    }

    public void setLabel(int resId) {
        layout.setHint(getContext().getString(resId));
    }

    public void setPlaceholder(CharSequence placeholder) {
        layout.setPlaceholderText(placeholder);
    }

    public void setPlaceholder(int resId) {
        layout.setPlaceholderText(getContext().getString(resId));
    }

    public void addTextChangedListener(TextWatcher watcher) {
        edit.addTextChangedListener(watcher);
    }

    public CharSequence getText() {
        return edit.getText();
    }

    public void setText(CharSequence text) {
        edit.setText(text);
    }

    public TextInputEditText getEdit() {
        return edit;
    }

    public TextInputLayout getLayout() {
        return layout;
    }
}
