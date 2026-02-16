//это просто фабрика. Он не хранит экземпляры.
package com.antoniokoman.basics.fsm;

import com.antoniokoman.basics.screens.categories.CategoryCreateScreen;
import com.antoniokoman.basics.screens.categories.CategoryEditorScreen;
import com.antoniokoman.basics.screens.categories.CategoryListScreen;
import com.antoniokoman.basics.screens.converters.ConverterCreateScreen;
import com.antoniokoman.basics.screens.converters.ConverterEditorScreen;
import com.antoniokoman.basics.screens.converters.ConverterViewScreen;

public enum ScreenType {
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

