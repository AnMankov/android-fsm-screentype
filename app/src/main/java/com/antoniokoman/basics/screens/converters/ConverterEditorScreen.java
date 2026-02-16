package com.antoniokoman.basics.screens.converters;

import android.widget.Button;
import com.antoniokoman.basics.fsm.*;

public class ConverterEditorScreen extends BaseScreen {
    @Override
    protected void onRender() {
        // Кнопки для Правил 13 и 14 в MainActivity
        renderButton("СОХРАНИТЬ", ConvEditorState.PR_SAVE);
        renderButton("ОТМЕНА", ConvEditorState.PR_BACK);
    }
    private void renderButton(String t, ScreenState s) {
        Button b = new Button(cachedView.getContext());
        b.setText(t);
        b.setOnClickListener(v -> { if (listener != null) listener.onScreenStateChanged(s); });
        cachedView.addView(b);
    }
    @Override public ScreenState getState() { return ConvEditorState.IDLE; }
}
