package com.antoniokoman.basics.screens.converters;

import android.widget.Button;
import com.antoniokoman.basics.fsm.*;

public class ConverterCreateScreen extends BaseScreen {
    @Override
    protected void onRender() {
        // Кнопки для Правил 15 и 16 в MainActivity
        renderButton("ГОТОВО", ConvCreateState.PR_CRE);
        renderButton("ОТМЕНА", ConvCreateState.PR_BACK);
    }
    private void renderButton(String t, ScreenState s) {
        Button b = new Button(cachedView.getContext());
        b.setText(t);
        b.setOnClickListener(v -> { if (listener != null) listener.onScreenStateChanged(s); });
        cachedView.addView(b);
    }
    @Override public ScreenState getState() { return ConvCreateState.IDLE; }
}
