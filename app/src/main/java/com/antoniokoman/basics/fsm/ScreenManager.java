
package com.antoniokoman.basics.fsm;

import android.os.Bundle;
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
    private Bundle savedStatesBundle = new Bundle(); //используем Bundle для хранения состояний экранов

    public ScreenManager(ViewGroup root) {
        this.root = root;
    }

    private Screen getOrCreateScreen(ScreenType type) {
        if (!screenCache.containsKey(type)) {
            Screen screen = type.create();

            // Достаем Bundle конкретно для этого экрана по его ключу (имени)
            Bundle screenState = savedStatesBundle.getBundle(type.name());
            if (screenState != null) {
                screen.restoreState(screenState);
            }

            screenCache.put(type, screen);
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
        if (!history.isEmpty()) { //в стеке есть экраны
            ScreenType previous = history.pop();
            navigateTo(previous, false);
            return true;
        }
        return false; //в стеке нет экранов
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

    public Bundle saveEverything() { //Вызывать в MainActivity.onSaveInstanceState
        // 1. Опрашиваем все живые экраны и просим их обновить свои данные в нашем хранилище
        for (Map.Entry<ScreenType, Screen> entry : screenCache.entrySet()) {
            Bundle screenBundle = new Bundle();
            entry.getValue().saveState(screenBundle);
            savedStatesBundle.putBundle(entry.getKey().name(), screenBundle);
        }

        // 2. Упаковываем всё в один финальный Bundle
        Bundle out = new Bundle();
        out.putBundle("all_screens_data", savedStatesBundle); // Вложенный Bundle
        out.putStringArrayList("history", getHistoryAsState());
        out.putString("current", currentType != null ? currentType.name() : null);
        return out;
    }

    public void restoreEverything(Bundle mainBundle) { // Восстанавливаем всё обратно
        if (mainBundle == null) return;

        // Восстанавливаем хранилище состояний
        Bundle restoredStates = mainBundle.getBundle("all_screens_data");
        if (restoredStates != null) {
            this.savedStatesBundle = restoredStates;
        }

        // Восстанавливаем историю
        restoreHistory(mainBundle.getStringArrayList("history"));

        // Переходим на текущий экран
        String current = mainBundle.getString("current");
        if (current != null) {
            navigateTo(ScreenType.valueOf(current), false);
        }
    }
}