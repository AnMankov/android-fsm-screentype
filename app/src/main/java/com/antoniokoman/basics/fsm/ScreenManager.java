package com.antoniokoman.basics.fsm;

import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

public class ScreenManager implements ScreenStateListener {

    private final ViewGroup root;
    private ScreenType currentType;
    private final Map<TransitionKey, ScreenType> transitions = new HashMap<>();
    private final Map<ScreenType, Screen> screenCache = new HashMap<>();
    private final Stack<ScreenType> history = new Stack<>();

    public ScreenManager(ViewGroup root) {
        this.root = root;
    }

    private Screen getOrCreateScreen(ScreenType type) {
        if (!screenCache.containsKey(type)) {
            screenCache.put(type, type.create());
        }
        return screenCache.get(type);
    }

    public void navigateTo(ScreenType type, boolean addToHistory) {
        if (currentType != null) {
            if (addToHistory) history.push(currentType);
            getOrCreateScreen(currentType).onExit(root);
        }
        currentType = type;
        getOrCreateScreen(type).onEnter(root, this);
    }

    public boolean handleBackPressed() {
        if (!history.isEmpty()) {
            ScreenType previous = history.pop();
            navigateTo(previous, false);
            return true;
        }
        return false;
    }

    public ScreenType getCurrentType() {
        return currentType;
    }

    public ArrayList<String> getHistoryAsState() {
        ArrayList<String> state = new ArrayList<>();
        for (ScreenType type : history) state.add(type.name());
        return state;
    }

    public void restoreHistory(ArrayList<String> state) {
        history.clear();
        if (state != null) {
            for (String typeName : state) history.push(ScreenType.valueOf(typeName));
        }
    }

    public void registerTransition(ScreenType from, ScreenState state, ScreenType to) {
        transitions.put(new TransitionKey(from, state), to);
    }

    @Override
    public void onScreenStateChanged(ScreenState newState) {
        ScreenType next = resolveNextScreen(currentType, newState);
        if (next != null && next != currentType) {
            navigateTo(next, true);
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
            this.stateName = ((Enum<?>) state).name();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            TransitionKey that = (TransitionKey) o;
            return type == that.type && stateClass.equals(that.stateClass) && stateName.equals(that.stateName);
        }

        @Override
        public int hashCode() {
            int result = type.hashCode();
            result = 31 * result + stateClass.hashCode();
            result = 31 * result + stateName.hashCode();
            return result;
        }
    }

    public String dumpGraph() {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<TransitionKey, ScreenType> entry : transitions.entrySet()) {
            sb.append(entry.getKey().type).append(" -- ").append(entry.getKey().stateName)
                    .append(" --> ").append(entry.getValue()).append("\n");
        }
        return sb.toString();
    }
}
