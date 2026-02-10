package com.antoniokoman.basics;

import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

public class ScreenManager implements ScreenStateListener {

    private final ViewGroup root;
    private ScreenType currentType;
    private final Map<TransitionKey, ScreenType> transitions = new HashMap<>();

    public ScreenManager(ViewGroup root) {
        this.root = root;
    }

    public void showScreen(ScreenType type) {
        // 1. выключаем предыдущий, если был
        if (currentType != null) {
            Screen oldScreen = currentType.getInstance();
            oldScreen.onExit(root);
        }

        // 2. включаем новый
        currentType = type;
        Screen newScreen = currentType.getInstance();
        newScreen.onEnter(root);
    }

    public ScreenType getCurrentType() {
        return currentType;
    }

    public Screen getCurrentScreen() {
        return currentType != null ? currentType.getInstance() : null;
    }

    @Override
    public void onScreenStateChanged(ScreenState newState) {
        ScreenType next = resolveNextScreen(currentType, newState);
        if (next != null && next != currentType) {
            showScreen(next);
        }
    }

    private ScreenType resolveNextScreen(ScreenType type, ScreenState state) {
        if (type == null || state == null) return null;
        return transitions.get(new TransitionKey(type, state));
    }

    private static final class TransitionKey {
        final ScreenType type;
        final Class<? extends ScreenState> stateClass;
        final String stateName;

        TransitionKey(ScreenType type, ScreenState state) {
            this.type = type;
            this.stateClass = state.getClass();
            this.stateName = ((Enum<?>) state).name(); // у enum есть name()
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TransitionKey that = (TransitionKey) o;

            if (type != that.type) return false;
            if (!stateClass.equals(that.stateClass)) return false;
            return stateName.equals(that.stateName);
        }

        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + stateClass.hashCode();
            result = 31 * result + stateName.hashCode();
            return result;
        }
    }
}
