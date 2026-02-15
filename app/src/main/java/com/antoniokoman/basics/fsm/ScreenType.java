
package com.antoniokoman.basics.fsm;

import com.antoniokoman.basics.screens.mainmenu.MainMenuScreen;
import com.antoniokoman.basics.screens.settings.SettingsScreen;

public enum ScreenType { //это просто фабрика. Он не хранит экземпляры.
    CAT_LIST {
        @Override Screen create() { return new CategoryListScreen(); }
    },
    CAT_CREATE {
        @Override Screen create() { return new CategoryCreateScreen(); }
    },
    CAT_EDITOR {
        @Override Screen create() { return new CategoryEditorScreen(); }
    },
    CONV_VIEW {
        @Override Screen create() { return new ConverterViewScreen(); }
    },
    CONV_CREATE {
        @Override Screen create() { return new ConverterCreateScreen(); }
    },
    CONV_EDITOR {
        @Override Screen create() { return new ConverterEditorScreen(); }
    };

    abstract Screen create();
}