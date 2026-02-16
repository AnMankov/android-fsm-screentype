package com.antoniokoman.basics.screens.categories;

import android.widget.Button;
import com.antoniokoman.basics.fsm.*;

public class CategoryEditorScreen extends BaseScreen {
    @Override
    protected void onRender() {
        // Кнопки для Правил 5 и 6 в MainActivity
        renderButton("СОХРАНИТЬ", CatEditorState.PR_SAVE);
        renderButton("НАЗАД", CatEditorState.PR_BACK);
    }
    private void renderButton(String t, ScreenState s) {
        Button b = new Button(cachedView.getContext());
        b.setText(t);
        b.setOnClickListener(v -> { if (listener != null) listener.onScreenStateChanged(s); });
        cachedView.addView(b);
    }
    @Override public ScreenState getState() { return CatEditorState.IDLE; }
}
