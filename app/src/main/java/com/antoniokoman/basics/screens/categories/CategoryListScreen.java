package com.antoniokoman.basics.screens.categories;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.PopupMenu;

import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;

import com.antoniokoman.basics.R;
import com.antoniokoman.basics.app.AppTheme;
import com.antoniokoman.basics.fsm.CatListState;
import com.antoniokoman.basics.fsm.ScreenState;
import com.antoniokoman.basics.repository.Repository;
import com.antoniokoman.basics.screens.base.AppBarView;
import com.antoniokoman.basics.screens.base.BaseContentScreen;

public class CategoryListScreen extends BaseContentScreen {

    private final Repository repo = Repository.getInstance();
    private LinearLayout listContainer;
    private int draggingFrom = -1;
    private android.view.View draggingView = null;

    // режим выбора
    private boolean selectionMode = false;
    private final java.util.Set<Repository.CategoryList.CategoryData> selectedItems = new java.util.HashSet<>();

    // ссылка на AppBar
    private AppBarView appBar;

    // 1. AppBar для этого экрана
    @Override
    protected AppBarView createAppBar(Context context) {
        appBar = new AppBarView(context);
        appBar.setTitle(context.getString(R.string.cat_list_title));

        // справа — меню
        appBar.addAction(R.drawable.ic_menu, v -> {
            showAppBarMenu(v.getContext(), v);
        });

        return appBar;
    }

    // 2. Контент под аппбаром
    @Override
    protected void onRenderContent(FrameLayout contentContainer) {
        if (repo.catList.categories.isEmpty()) {
            renderEmptyState(contentContainer);
        } else {
            renderList(contentContainer);
        }
    }

    // 3. Действие FAB
    @Override
    protected boolean hasFab() {
        return true;
    }

    @Override
    protected void onFabClick() {
        if (listener != null) listener.onScreenStateChanged(CatListState.PR_ADD);
    }

    @Override
    public ScreenState getState() {
        return CatListState.IDLE;
    }

    // ----- EMPTY STATE -----

    private void renderEmptyState(FrameLayout root) {
        Context context = root.getContext();
        boolean expanded = isExpanded(context);

        int titleSizeSpRes = expanded
                ? R.dimen.empty_title_tablet
                : R.dimen.empty_title_phone;
        float titleSizeSp = context.getResources()
                .getDimension(titleSizeSpRes)
                / context.getResources().getDisplayMetrics().scaledDensity;

        int topPaddingPx = AppTheme.dimenPx(
                context,
                expanded ? R.dimen.empty_top_padding_tablet : R.dimen.empty_top_padding_phone
        );
        int textIconGapPx = AppTheme.dimenPx(
                context,
                expanded ? R.dimen.empty_text_icon_gap_tablet : R.dimen.empty_text_icon_gap_phone
        );
        int iconSizePx = AppTheme.dimenPx(
                context,
                expanded ? R.dimen.empty_icon_tablet : R.dimen.empty_icon_phone
        );

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.CENTER);

        LinearLayout emptyLayout = new LinearLayout(context);
        emptyLayout.setOrientation(LinearLayout.VERTICAL);
        emptyLayout.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView tv = new TextView(context);
        tv.setText(context.getString(R.string.cat_list_empty));
        tv.setTextColor(AppTheme.textSecondaryColor(context));
        tv.setTextSize(titleSizeSp);
        tv.setGravity(Gravity.CENTER_HORIZONTAL);
        tv.setPadding(0, topPaddingPx, 0, textIconGapPx);
        emptyLayout.addView(tv, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        ImageView bigIcon = new ImageView(context);
        bigIcon.setImageResource(R.drawable.ic_add_tiles);
        bigIcon.setColorFilter(AppTheme.iconTintColor(context));
        bigIcon.setAlpha(AppTheme.ALPHA_DECORATIVE);
        bigIcon.setClickable(true);
        bigIcon.setFocusable(true);
        bigIcon.setBackground(
                ContextCompat.getDrawable(context, R.drawable.bg_click_ripple)
        );
        bigIcon.setOnClickListener(v -> {
            if (listener != null) listener.onScreenStateChanged(CatListState.PR_ADD);
        });

        int touchSizePx = AppTheme.dimenPx(context, R.dimen.touch_target_min);
        LinearLayout.LayoutParams iconLp =
                new LinearLayout.LayoutParams(iconSizePx, iconSizePx);
        iconLp.gravity = Gravity.CENTER_HORIZONTAL;
        emptyLayout.addView(bigIcon, iconLp);

        container.addView(emptyLayout,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));

        FrameLayout.LayoutParams rootLp =
                new FrameLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT
                );

        root.addView(container, rootLp);
    }

    // ----- LIST -----

    private void renderList(FrameLayout root) {
        Context context = root.getContext();
        boolean expanded = isExpanded(context);

        LinearLayout container = new LinearLayout(context);
        container.setOrientation(LinearLayout.VERTICAL);
        container.setGravity(Gravity.TOP);

        FrameLayout.LayoutParams rootLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        root.addView(container, rootLp);

        android.widget.ScrollView scrollView = new android.widget.ScrollView(context);
        scrollView.setFillViewport(true);
        LinearLayout.LayoutParams scrollLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                0,
                1f
        );
        container.addView(scrollView, scrollLp);

        LinearLayout list = new LinearLayout(context);
        list.setOrientation(LinearLayout.VERTICAL);
        list.setGravity(Gravity.TOP);

        this.listContainer = list;
        list.setLayoutTransition(new android.animation.LayoutTransition());

        list.setOnDragListener((v, event) -> {
            if (selectionMode) return false; // в режиме выбора не перетаскиваем
            switch (event.getAction()) {
                case android.view.DragEvent.ACTION_DRAG_LOCATION:
                    handleDragLocation(event);
                    break;
                case android.view.DragEvent.ACTION_DRAG_ENDED:
                case android.view.DragEvent.ACTION_DRAG_EXITED:
                    draggingFrom = -1;
                    highlightDraggingCard(false);
                    draggingView = null;
                    break;
            }
            return true;
        });

        int horizontalPadding = AppTheme.dimenPx(context, R.dimen.cat_create_horizontal_padding);
        int topPadding = AppTheme.dimenPx(
                context,
                expanded
                        ? R.dimen.cat_create_label_top_padding_tablet
                        : R.dimen.cat_create_label_top_padding_phone
        );
        int betweenCards = AppTheme.dimenPx(context, R.dimen.spacing_medium);
        int bottomPadding = topPadding;

        list.setPadding(horizontalPadding, topPadding, horizontalPadding, bottomPadding);

        scrollView.addView(list, new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        ));

        for (int i = 0; i < repo.catList.categories.size(); i++) {
            Repository.CategoryList.CategoryData cat = repo.catList.categories.get(i);
            android.view.View cardRoot = createCategoryCardView(context, cat, i);

            LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            if (i > 0) {
                cardLp.topMargin = betweenCards;
            }
            list.addView(cardRoot, cardLp);
        }
    }


    // ----- CARD -----

    private android.view.View createCategoryCardView(
            Context context,
            Repository.CategoryList.CategoryData cat,
            int position
    ) {
        boolean expanded = isExpanded(context);

        int cardPaddingV = AppTheme.dimenPx(
                context,
                expanded ? R.dimen.spacing_large : R.dimen.spacing_medium
        );
        int cardPaddingH = AppTheme.dimenPx(context, R.dimen.spacing_medium);
        int cornerRadius = AppTheme.dimenPx(context, R.dimen.textfield_corner_radius);
        int strokeWidth = AppTheme.dimenPx(context, R.dimen.textfield_stroke_width);

        int badgeWidth = AppTheme.dimenPx(context, R.dimen.cat_card_badge_width);
        int iconPadding = AppTheme.dimenPx(context, R.dimen.spacing_extra_small);
        int textVerticalGap = AppTheme.dimenPx(context, R.dimen.spacing_small);
        int rectSize = AppTheme.dimenPx(context, R.dimen.icon_circle_size);

        int categoryColor;
        try {
            categoryColor = Color.parseColor(
                    cat.color != null ? cat.color : "#FFDDAC"
            );
        } catch (IllegalArgumentException e) {
            categoryColor = Color.parseColor("#FFDDAC");
        }

        // корневая карточка (контент)
        LinearLayout card = new LinearLayout(context);
        card.setOrientation(LinearLayout.HORIZONTAL);
        card.setGravity(Gravity.CENTER_VERTICAL);
        card.setClickable(false);          // нужно для long-click под drag
        card.setForeground(null);        // чтобы ripple был только на cardRoot
        card.setClipToPadding(false);
        card.setClipChildren(false);

        int minHeight = AppTheme.dimenPx(context, R.dimen.cat_card_height_min);
        card.setMinimumHeight(minHeight);

        int screenBg = AppTheme.backgroundColor(context);

        card.setBackground(AppTheme.roundedBox(
                screenBg,
                categoryColor,
                strokeWidth,
                cornerRadius
        ));

        card.setTag(R.id.tag_category_color, categoryColor);

        // drag только если не режим выбора
//        card.setOnLongClickListener(v -> {
//            if (selectionMode) return false;
//
//            // родитель cardRoot
//            ViewGroup cardRoot = (ViewGroup) v.getParent();
//            draggingFrom = listContainer.indexOfChild(cardRoot);
//            draggingView = cardRoot;
//
//            highlightDraggingCard(true);
//
//            android.content.ClipData data = android.content.ClipData.newPlainText("", "");
//            android.view.View.DragShadowBuilder shadowBuilder =
//                    new android.view.View.DragShadowBuilder(draggingView);
//            draggingView.startDragAndDrop(data, shadowBuilder, draggingView, 0);
//            return true;
//        });


        // --- левый бейдж ---
        LinearLayout leftBadge = new LinearLayout(context);
        leftBadge.setOrientation(LinearLayout.VERTICAL);
        leftBadge.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams leftBadgeLp = new LinearLayout.LayoutParams(
                badgeWidth,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        card.addView(leftBadge, leftBadgeLp);

        GradientDrawable iconBg = new GradientDrawable();
        iconBg.setShape(GradientDrawable.RECTANGLE);
        iconBg.setColor(screenBg);
        iconBg.setCornerRadius(cornerRadius);

        FrameLayout iconContainer = new FrameLayout(context);
        iconContainer.setBackground(iconBg);

        FrameLayout.LayoutParams iconContainerLp =
                new FrameLayout.LayoutParams(rectSize, rectSize);
        iconContainerLp.gravity = Gravity.CENTER;

        ImageView iconView = new ImageView(context);
        FrameLayout.LayoutParams iconLp =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );
        iconLp.gravity = Gravity.CENTER;
        iconView.setLayoutParams(iconLp);

        iconView.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
        iconView.setScaleType(ImageView.ScaleType.FIT_CENTER);

        if (cat.icon != null && !cat.icon.isEmpty()) {
            int resId = context.getResources()
                    .getIdentifier(cat.icon, "drawable", context.getPackageName());
            iconView.setImageResource(resId != 0 ? resId : R.drawable.ic_category_default);
        } else {
            iconView.setImageResource(R.drawable.ic_category_default);
        }
        iconView.setColorFilter(AppTheme.textMainColor(context));

        iconContainer.addView(iconView, iconLp);

        LinearLayout.LayoutParams iconSlotLp =
                new LinearLayout.LayoutParams(rectSize, rectSize);
        leftBadge.addView(iconContainer, iconSlotLp);

        // --- центральный блок ---
        LinearLayout centerWrapper = new LinearLayout(context);
        centerWrapper.setOrientation(LinearLayout.VERTICAL);
        centerWrapper.setGravity(Gravity.CENTER_VERTICAL);
        centerWrapper.setPadding(cardPaddingH, cardPaddingV, cardPaddingH, cardPaddingV);
        centerWrapper.setBackgroundColor(Color.TRANSPARENT);

        LinearLayout.LayoutParams centerLp = new LinearLayout.LayoutParams(
                0,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                1f
        );
        card.addView(centerWrapper, centerLp);

        LinearLayout content = new LinearLayout(context);
        content.setOrientation(LinearLayout.VERTICAL);
        content.setGravity(Gravity.CENTER_VERTICAL);

        centerWrapper.addView(content,
                new LinearLayout.LayoutParams(
                        ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.WRAP_CONTENT
                ));

        // имя категории
        TextView nameView = new TextView(context);
        nameView.setText(cat.name != null ? cat.name : "");
        nameView.setTextColor(AppTheme.textMainColor(context));
        nameView.setTextSize(16);
        nameView.setMaxLines(2);
        content.addView(nameView);

        // convCount + описание
        int convCount = cat.converters != null ? cat.converters.size() : 0;
        String baseDesc = cat.description != null ? cat.description : "";
        String convLabel = String.valueOf(convCount);

        String finalDesc;
        if (baseDesc.isEmpty()) {
            finalDesc = convLabel;
        } else {
            finalDesc = "(" + convLabel + ")  -  " + baseDesc;
        }

        TextView descView = new TextView(context);
        descView.setText(finalDesc);
        descView.setTextColor(AppTheme.textSecondaryColor(context));
        descView.setTextSize(14);
        descView.setMaxLines(2);

        LinearLayout.LayoutParams descLp = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        descLp.topMargin = textVerticalGap;
        content.addView(descView, descLp);

        // --- правый бейдж (меню) ---
        LinearLayout rightBadge = new LinearLayout(context);
        rightBadge.setOrientation(LinearLayout.VERTICAL);
        rightBadge.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams rightBadgeLp = new LinearLayout.LayoutParams(
                badgeWidth,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        card.addView(rightBadge, rightBadgeLp);

        GradientDrawable menuBg = new GradientDrawable();
        menuBg.setShape(GradientDrawable.RECTANGLE);
        menuBg.setColor(screenBg);
        menuBg.setCornerRadius(cornerRadius);

        int borderColor = Color.parseColor("#80000000");
        int borderWidth = AppTheme.dimenPx(context, R.dimen.menu_stroke_width);
        menuBg.setStroke(borderWidth, borderColor);

        FrameLayout menuContainer = new FrameLayout(context);
        menuContainer.setBackground(menuBg);

        menuContainer.setClickable(true);
        menuContainer.setForeground(
                ContextCompat.getDrawable(context, R.drawable.bg_click_ripple)
        );

        FrameLayout.LayoutParams menuContainerLp =
                new FrameLayout.LayoutParams(rectSize, rectSize);
        menuContainerLp.gravity = Gravity.CENTER;

        ImageView menuIcon = new ImageView(context);
        FrameLayout.LayoutParams menuLp =
                new FrameLayout.LayoutParams(
                        FrameLayout.LayoutParams.MATCH_PARENT,
                        FrameLayout.LayoutParams.MATCH_PARENT
                );
        menuLp.gravity = Gravity.CENTER;
        menuIcon.setLayoutParams(menuLp);

        menuIcon.setPadding(iconPadding, iconPadding, iconPadding, iconPadding);
        menuIcon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        menuIcon.setImageResource(R.drawable.ic_more_vert);
        menuIcon.setColorFilter(AppTheme.textMainColor(context));

        menuContainer.addView(menuIcon, menuLp);
        rightBadge.addView(menuContainer, new LinearLayout.LayoutParams(rectSize, rectSize));

        menuContainer.setContentDescription(
                context.getString(R.string.cat_menu_cd, cat.name != null ? cat.name : "")
        );

        // клики по карточке / выбор
        // root для списка будет FrameLayout, поэтому index берём у root, а не у card
        // cardRoot создадим ниже

        menuContainer.setOnClickListener(v -> {
            if (selectionMode) return;
            showCategoryMenu(context, v, cat);
        });

        // скруглённые бейджи
        GradientDrawable leftBg = new GradientDrawable();
        leftBg.setColor(categoryColor);
        leftBg.setCornerRadii(new float[]{
                cornerRadius, cornerRadius,
                0, 0,
                0, 0,
                cornerRadius, cornerRadius
        });
        leftBadge.setBackground(leftBg);

        GradientDrawable rightBg = new GradientDrawable();
        rightBg.setColor(categoryColor);
        rightBg.setCornerRadii(new float[]{
                0, 0,
                cornerRadius, cornerRadius,
                cornerRadius, cornerRadius,
                0, 0
        });
        rightBadge.setBackground(rightBg);

        // --- корневой FrameLayout с кружком выбора ---
        FrameLayout cardRoot = new FrameLayout(context);
        cardRoot.setClickable(true);
        cardRoot.setForeground(ContextCompat.getDrawable(context, R.drawable.bg_click_ripple));
//        card.setClickable(false);
//        card.setForeground(null);

        FrameLayout.LayoutParams cardInnerLp = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );

        cardRoot.setOnLongClickListener(v -> {
            if (selectionMode) return false;

            draggingFrom = listContainer.indexOfChild(v);
            draggingView = v;

            highlightDraggingCard(true);

            android.content.ClipData data = android.content.ClipData.newPlainText("", "");
            android.view.View.DragShadowBuilder shadowBuilder =
                    new android.view.View.DragShadowBuilder(draggingView);
            draggingView.startDragAndDrop(data, shadowBuilder, draggingView, 0);
            return true;
        });

        cardRoot.addView(card, cardInnerLp);

        int circleSize = AppTheme.dimenPx(context, R.dimen.selection_circle_size);
//        int circleMarginTop = AppTheme.dimenPx(context, R.dimen.selection_circle_margin_top);

        ImageView selectionCircle = new ImageView(context);

        GradientDrawable circleBg = new GradientDrawable();
        circleBg.setShape(GradientDrawable.OVAL);
        circleBg.setColor(Color.parseColor("#1E88E5")); // синий круг
        selectionCircle.setBackground(circleBg);
        selectionCircle.setImageResource(R.drawable.ic_check);
        selectionCircle.setColorFilter(Color.WHITE);
        selectionCircle.setScaleType(ImageView.ScaleType.CENTER);

        FrameLayout.LayoutParams selLp =
                new FrameLayout.LayoutParams(circleSize, circleSize);
        selLp.gravity = Gravity.CENTER;
//        selLp.topMargin = circleMarginTop;

        cardRoot.addView(selectionCircle, selLp);
        selectionCircle.setTag(R.id.tag_selection_circle);

        boolean isSelected = selectionMode && selectedItems.contains(cat);

        if (selectionMode) {
            selectionCircle.setVisibility(android.view.View.VISIBLE);
            selectionCircle.setImageAlpha(isSelected ? 255 : 0);
        } else {
            selectionCircle.setVisibility(android.view.View.GONE);
        }

        cardRoot.setOnClickListener(v -> {
            android.util.Log.d(
                    "CategoryListScreen",
                    "card click, selectionMode=" + selectionMode + ", pos=" + position
            );
            if (selectionMode) {
                toggleItemSelection(cat, (ImageView) v.findViewWithTag(R.id.tag_selection_circle));
            } else {
                repo.catList.index = position;
                if (listener != null) {
                    listener.onScreenStateChanged(CatListState.PR_CAT);
                }
            }
        });



        return cardRoot;
    }

    // ----- CONTEXT MENU -----

    private void showCategoryMenu(
            Context context,
            android.view.View anchor,
            Repository.CategoryList.CategoryData cat
    ) {
        PopupMenu popup = new PopupMenu(context, anchor);
        popup.getMenuInflater().inflate(R.menu.categories_item_menu, popup.getMenu());

        try {
            java.lang.reflect.Field mFieldPopup = popup.getClass().getDeclaredField("mPopup");
            mFieldPopup.setAccessible(true);
            Object mPopup = mFieldPopup.get(popup);
            Class<?> popupClass = mPopup.getClass();
            java.lang.reflect.Method setForceShowIcon =
                    popupClass.getDeclaredMethod("setForceShowIcon", boolean.class);
            setForceShowIcon.setAccessible(true);
            setForceShowIcon.invoke(mPopup, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        popup.setOnMenuItemClickListener(item -> {
            int index = findCardIndexForAnchor(anchor);
            if (index < 0 || index >= repo.catList.categories.size()) return false;

            int id = item.getItemId();
            if (id == R.id.action_edit) {
                repo.catList.index = index;
                if (listener != null) {
                    listener.onScreenStateChanged(CatListState.PR_EDIT);
                }
                return true;
            } else if (id == R.id.action_duplicate) {
                duplicateCategory(index);
                return true;
            } else if (id == R.id.action_delete) {
                Repository.CategoryList.CategoryData current =
                        repo.catList.categories.get(index);
                confirmDeleteCategory(context, current, index);
                return true;
            }
            return false;
        });

        popup.show();
    }

    private void duplicateCategory(int position) {
        Repository.CategoryList.CategoryData src = repo.catList.categories.get(position);

        Repository.CategoryList.CategoryData copy = new Repository.CategoryList.CategoryData();
        copy.name = src.name;
        copy.description = src.description;
        copy.color = src.color;
        copy.icon = src.icon;
        if (src.converters != null) {
            copy.converters = new java.util.ArrayList<>(src.converters);
        }

        repo.catList.categories.add(position + 1, copy);

        if (listContainer != null) {
            Context context = listContainer.getContext();
            android.view.View cardRoot = createCategoryCardView(context, copy, position + 1);

            int betweenCards = AppTheme.dimenPx(context, R.dimen.spacing_medium);

            LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            if (position + 1 > 0) {
                cardLp.topMargin = betweenCards;
            }

            listContainer.addView(cardRoot, position + 1, cardLp);
        }
    }

    private void confirmDeleteCategory(
            Context context,
            Repository.CategoryList.CategoryData cat,
            int position
    ) {
        String name = cat.name != null ? cat.name : "";
        String msg = context.getString(R.string.cat_delete_message, name);
        confirmDeleteCategories(context, msg, () -> deleteCategory(position));
    }

    private void confirmDeleteCategories(
            Context context,
            String message,
            Runnable onConfirm
    ) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.cat_delete_title)
                .setMessage(message)
                .setPositiveButton(R.string.cat_delete_positive, (dialog, which) -> {
                    onConfirm.run();
                })
                .setNegativeButton(R.string.cat_delete_negative, (dialog, which) -> {
                    dialog.dismiss();
                })
                .show();
    }

    private void deleteCategory(int position) {
        if (position >= 0 && position < repo.catList.categories.size()) {
            repo.catList.categories.remove(position);
        }

        if (listContainer == null) return;

        if (repo.catList.categories.isEmpty()) {
            ViewGroup scrollView = (ViewGroup) listContainer.getParent();
            ViewGroup container = (ViewGroup) scrollView.getParent();
            FrameLayout root = (FrameLayout) container.getParent();

            root.removeAllViews();
            renderEmptyState(root);
        } else {
            listContainer.removeViewAt(position);

            if (listContainer.getChildCount() > 0) {
                android.view.View first = listContainer.getChildAt(0);
                ViewGroup.LayoutParams lp = first.getLayoutParams();
                if (lp instanceof LinearLayout.LayoutParams) {
                    ((LinearLayout.LayoutParams) lp).topMargin = 0;
                    first.setLayoutParams(lp);
                }
            }
        }
    }

    // удаление нескольких выбранных
    private void deleteSelectedCategories() {
        if (selectedItems.isEmpty()) {
            exitSelectionMode();
            return;
        }

        if (listContainer == null) {
            selectedItems.clear();
            selectionMode = false;
            return;
        }

        Context context = listContainer.getContext();
        int count = selectedItems.size();

        String firstName = "";
        for (Repository.CategoryList.CategoryData c : selectedItems) {
            if (c.name != null) {
                firstName = c.name;
                break;
            }
        }

        String message;
        if (count == 1 && !firstName.isEmpty()) {
            message = context.getString(R.string.cat_delete_message, firstName);
        } else {
            message = context.getString(R.string.cat_delete_message_multi);
        }


        confirmDeleteCategories(context, message, () -> {
            repo.catList.categories.removeAll(selectedItems);

            if (repo.catList.categories.isEmpty()) {
                ViewGroup scrollView = (ViewGroup) listContainer.getParent();
                ViewGroup container = (ViewGroup) scrollView.getParent();
                FrameLayout root = (FrameLayout) container.getParent();

                exitSelectionMode();
                root.removeAllViews();
                renderEmptyState(root);
            } else {
                exitSelectionMode();
            }
        });
    }



    private void refreshList(Context context) {
        if (listContainer == null) return;

        listContainer.removeAllViews();

        int betweenCards = AppTheme.dimenPx(context, R.dimen.spacing_medium);

        for (int i = 0; i < repo.catList.categories.size(); i++) {
            Repository.CategoryList.CategoryData cat = repo.catList.categories.get(i);
            android.view.View cardRoot = createCategoryCardView(context, cat, i);

            LinearLayout.LayoutParams cardLp = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            if (i > 0) {
                cardLp.topMargin = betweenCards;
            }
            listContainer.addView(cardRoot, cardLp);
        }
    }

    private void handleDragLocation(android.view.DragEvent event) {
        if (listContainer == null || draggingFrom < 0) return;
        if (selectionMode) return;

        float y = event.getY();
        int childCount = listContainer.getChildCount();
        if (childCount <= 1) return;

        int targetIndex = -1;

        for (int i = 0; i < childCount; i++) {
            android.view.View child = listContainer.getChildAt(i);
            int top = child.getTop();
            int bottom = child.getBottom();
            int center = (top + bottom) / 2;

            if (y < center) {
                targetIndex = i;
                break;
            }
        }
        if (targetIndex == -1) {
            targetIndex = childCount - 1;
        }

        if (targetIndex == draggingFrom || targetIndex < 0 || targetIndex >= childCount) {
            return;
        }

        java.util.Collections.swap(repo.catList.categories, draggingFrom, targetIndex);

        android.view.View draggedView = listContainer.getChildAt(draggingFrom);
        listContainer.removeViewAt(draggingFrom);
        listContainer.addView(draggedView, targetIndex);

        draggingFrom = targetIndex;

        int betweenCards = AppTheme.dimenPx(
                listContainer.getContext(),
                R.dimen.spacing_medium
        );

        for (int i = 0; i < listContainer.getChildCount(); i++) {
            android.view.View child = listContainer.getChildAt(i);
            ViewGroup.LayoutParams lp = child.getLayoutParams();
            if (lp instanceof LinearLayout.LayoutParams) {
                LinearLayout.LayoutParams llp = (LinearLayout.LayoutParams) lp;
                llp.topMargin = (i == 0) ? 0 : betweenCards;
                child.setLayoutParams(llp);
            }
        }
    }

    private int findCardIndexForAnchor(android.view.View anchor) {
        if (listContainer == null) return -1;

        // anchor = menuContainer, его parent = rightBadge, parent.parent = card, parent.parent.parent = cardRoot
        android.view.View parent = (android.view.View) anchor.getParent();        // rightBadge
        if (parent == null) return -1;
        parent = (android.view.View) parent.getParent();                          // card
        if (parent == null) return -1;
        parent = (android.view.View) parent.getParent();                          // cardRoot
        if (parent == null) return -1;

        return listContainer.indexOfChild(parent);
    }

    private void highlightDraggingCard(boolean highlight) {
        if (draggingView == null) return;

        android.view.View card = ((ViewGroup) draggingView).getChildAt(0); // внутри cardRoot
        android.graphics.drawable.Drawable bg = card.getBackground();
        if (!(bg instanceof GradientDrawable)) {
            draggingView.setAlpha(highlight ? 0.95f : 1f);
            return;
        }

        GradientDrawable gd = (GradientDrawable) bg;

        int baseStroke = AppTheme.dimenPx(
                draggingView.getContext(),
                R.dimen.textfield_stroke_width
        );

        Object tagColor = card.getTag(R.id.tag_category_color);
        int baseColor = tagColor instanceof Integer
                ? (Integer) tagColor
                : Color.TRANSPARENT;

        if (highlight) {
            int highlightStroke = baseStroke * 3;
            int highlightColor = Color.parseColor("#FF5722");
            gd.setStroke(highlightStroke, highlightColor);
        } else {
            gd.setStroke(baseStroke, baseColor);
        }

        card.setBackground(gd);
    }

    private void showSortDialog(Context context) {
        String[] options = new String[]{
                context.getString(R.string.cat_sort_name_asc),
                context.getString(R.string.cat_sort_name_desc),
                context.getString(R.string.cat_sort_conv_asc),
                context.getString(R.string.cat_sort_conv_desc),
                context.getString(R.string.cat_sort_date_asc),
                context.getString(R.string.cat_sort_date_desc)
        };

        new AlertDialog.Builder(context)
                .setTitle(R.string.cat_sort_title)
                .setItems(options, (dialog, which) -> {
                    switch (which) {
                        case 0:
                            sortCategoriesByNameAsc();
                            break;
                        case 1:
                            sortCategoriesByNameDesc();
                            break;
                        case 2:
                            sortCategoriesByConvAsc();
                            break;
                        case 3:
                            sortCategoriesByConvDesc();
                            break;
                        case 4:
                            sortCategoriesByDateAsc();
                            break;
                        case 5:
                            sortCategoriesByDateDesc();
                            break;
                    }
                    refreshList(context);
                })
                .show();
    }

    private void sortCategoriesByNameAsc() {
        java.util.Collections.sort(
                repo.catList.categories,
                (a, b) -> String.valueOf(a.name)
                        .compareToIgnoreCase(String.valueOf(b.name))
        );
    }

    private void sortCategoriesByNameDesc() {
        java.util.Collections.sort(
                repo.catList.categories,
                (a, b) -> String.valueOf(b.name)
                        .compareToIgnoreCase(String.valueOf(a.name))
        );
    }

    private int convCount(Repository.CategoryList.CategoryData c) {
        return c.converters != null ? c.converters.size() : 0;
    }

    private void sortCategoriesByConvAsc() {
        java.util.Collections.sort(
                repo.catList.categories,
                (a, b) -> Integer.compare(convCount(a), convCount(b))
        );
    }

    private void sortCategoriesByConvDesc() {
        java.util.Collections.sort(
                repo.catList.categories,
                (a, b) -> Integer.compare(convCount(b), convCount(a))
        );
    }

    private long createdAt(Repository.CategoryList.CategoryData c) {
        return c.createdAt != null ? c.createdAt : 0L;
    }

    private void sortCategoriesByDateAsc() {
        java.util.Collections.sort(
                repo.catList.categories,
                (a, b) -> Long.compare(createdAt(a), createdAt(b))
        );
    }

    private void sortCategoriesByDateDesc() {
        java.util.Collections.sort(
                repo.catList.categories,
                (a, b) -> Long.compare(createdAt(b), createdAt(a))
        );
    }

    private void showAppBarMenu(Context context, android.view.View anchor) {
        PopupMenu popup = new PopupMenu(context, anchor);
        popup.getMenuInflater().inflate(R.menu.categories_actions_menu, popup.getMenu());

        try {
            java.lang.reflect.Field mFieldPopup = popup.getClass().getDeclaredField("mPopup");
            mFieldPopup.setAccessible(true);
            Object mPopup = mFieldPopup.get(popup);
            Class<?> popupClass = mPopup.getClass();
            java.lang.reflect.Method setForceShowIcon =
                    popupClass.getDeclaredMethod("setForceShowIcon", boolean.class);
            setForceShowIcon.setAccessible(true);
            setForceShowIcon.invoke(mPopup, true);
        } catch (Exception e) {
            e.printStackTrace();
        }

        popup.setOnMenuItemClickListener(item -> {
            int id = item.getItemId();
            if (id == R.id.action_sort) {
                showSortDialog(context);
                return true;
            } else if (id == R.id.action_reorder_hint) {
                showReorderHint(context);
                return true;
            } else if (id == R.id.action_select) {
                enterSelectionModeFromMenu();
                return true;
            }
            return false;
        });


        popup.show();
    }

    // ----- MULTI-SELECTION MODE -----

    private void enterSelectionModeFromMenu() {
        if (selectionMode || listContainer == null) return;
        selectionMode = true;
        selectedItems.clear();
        android.util.Log.d("CategoryListScreen", "enterSelectionMode, items=" + repo.catList.categories.size());
        refreshList(listContainer.getContext());
        updateAppBarForSelectionMode();
    }


    private void exitSelectionMode() {
        android.util.Log.d("CategoryListScreen", "exitSelectionMode");
        if (!selectionMode) return;
        selectionMode = false;
        selectedItems.clear();
        if (listContainer != null) {
            refreshList(listContainer.getContext());
        }
        updateAppBarForNormalMode();
    }


    private void toggleItemSelection(Repository.CategoryList.CategoryData item, ImageView circle) {
        if (!selectionMode) {
            android.util.Log.d("CategoryListScreen", "toggleItemSelection called but selectionMode=false");
            return;
        }

        boolean wasSelected = selectedItems.contains(item);
        android.util.Log.d("CategoryListScreen", "toggleItemSelection, wasSelected=" + wasSelected);

        if (wasSelected) {
            selectedItems.remove(item);
            if (circle != null) circle.setImageAlpha(0);
            if (selectedItems.isEmpty()) {
                exitSelectionMode();
                return;
            }
        } else {
            selectedItems.add(item);
            if (circle != null) circle.setImageAlpha(255);
        }

        updateAppBarSelectionCount();
    }

    private void updateAppBarForSelectionMode() {
        if (appBar == null) return;
        Context context = appBar.getContext();

        appBar.clearNavigationIcon(); // X больше не в навигации
        appBar.clearActions();

        // сначала X (Отмена), он будет ближе к числу
        appBar.addAction(R.drawable.ic_close, v -> exitSelectionMode());

        // потом корзина, останется в самом правом краю
        appBar.addAction(R.drawable.ic_delete_forever, v -> deleteSelectedCategories());

        appBar.setTitle(
                context.getString(R.string.cat_selected_count, selectedItems.size())
        );
    }


    private void updateAppBarSelectionCount() {
        if (!selectionMode || appBar == null) return;
        Context context = appBar.getContext();
        appBar.setTitle(context.getString(R.string.cat_selected_count, selectedItems.size()));
    }

    private void updateAppBarForNormalMode() {
        if (appBar == null) return;
        Context context = appBar.getContext();
        appBar.clearNavigationIcon();
        appBar.clearActions();
        appBar.setTitle(context.getString(R.string.cat_list_title));
        appBar.addAction(R.drawable.ic_menu, v -> showAppBarMenu(v.getContext(), v));
    }

    private void showReorderHint(Context context) {
        new AlertDialog.Builder(context)
                .setTitle(R.string.cat_reorder_hint_title)
                .setMessage(R.string.cat_reorder_hint_message)
                .setPositiveButton(R.string.cat_reorder_hint_ok, (dialog, which) -> dialog.dismiss())
                .show();
    }

}
