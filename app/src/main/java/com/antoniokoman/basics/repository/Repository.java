package com.antoniokoman.basics.repository;

import java.util.ArrayList;

public class Repository {
    public enum LimitType {
        UNLIMITED,
        LIMITED,
    }

    public static class ConverterData {
        public String name;
        public String inputUnit;
        public String outputUnit;
        //public ArrayList<Formula> formula; //Formula - это сложный класс, в котором реализовано преобразование, хранящееся пошагово
        public String backgroundColor; //это некая кастомизация внешнего вида (украшательства)
        public String icon; //это некая кастомизация внешнего вида (украшательства)
    }

    public static class CategoryList {
        public ArrayList<CategoryData> categories = new ArrayList<>(); //отсюда можно узнать количество категорий и порядковый номер конкретной категории как индекс в списке
        public long index; //текущая просматриваемая категория
        public LimitType limitType; //есть ли ограничение на количество категорий/конвертеров - закладываем монетизацию

        public static class CategoryData {
            public String name;
            public String description;
            public long converterIndex; //текущий выбранный конвертер в категории
            public ArrayList<ConverterData> converters = new ArrayList<>(); //массив ссылок на конвертеры в конкретной категории (у разных категорий могут быть одинаковые конвертеры - для будущих операций копирования/вставки конвертера в категории).
            public String color; //это некая кастомизация внешнего вида (украшательства)
            public String icon; //это некая кастомизация внешнего вида (украшательства)
        }
    }

    // Экземпляры модулей (доступ через геттеры)
    public final CategoryList catList = new CategoryList();

    // Синглтон самого репозитория
    private static Repository instance;
    public static Repository getInstance() {
        if (instance == null) instance = new Repository();
        return instance;
    }

    // Удобный helper для добавления категории
    public CategoryList.CategoryData addCategory(
            String name,
            String description,
            String color,
            String icon
    ) {
        CategoryList.CategoryData cat = new CategoryList.CategoryData();
        cat.name = name;
        cat.description = description;
        cat.color = color;
        cat.icon = icon;
        cat.converterIndex = 0;
        cat.converters = new ArrayList<>();

        catList.categories.add(cat);
        catList.index = catList.categories.size() - 1;

        return cat;
    }
}
