package vswe.stevesfactory.ui.manager.editor;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureClientData;
import vswe.stevesfactory.library.gui.actionmenu.AbstractEntry;
import vswe.stevesfactory.library.gui.actionmenu.IEntry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.*;

public final class PropertyManager<T, P extends IProcedure & IProcedureClientData> {

    private static final List<Supplier<IEntry>> EMPTY_LIST = ImmutableList.of();

    private final FlowComponent<P> flowComponent;
    private final List<Case<T, P>> cases = new ArrayList<>();
    private final Supplier<T> propertyGetter;
    private final Consumer<T> propertySetter;

    private List<Supplier<IEntry>> actions = EMPTY_LIST;

    private Menu<P> menu;
    private int selectedIndex = -1;

    public PropertyManager(FlowComponent<P> flowComponent, Supplier<T> propertyGetter, Consumer<T> propertySetter) {
        this.flowComponent = flowComponent;
        this.propertyGetter = propertyGetter;
        this.propertySetter = propertySetter;
    }

    public Case<T, P> on(Predicate<T> condition) {
        Case<T, P> caseElement = new Case<>(condition);
        cases.add(caseElement);
        return caseElement;
    }

    public T getProperty() {
        return propertyGetter.get();
    }

    private void setPropertyRaw(T property) {
        propertySetter.accept(property);
    }

    public void setProperty(@Nonnull T property) {
        setPropertyRaw(Objects.requireNonNull(property));
        int i = 0;
        for (Case<T, P> caseElement : cases) {
            if (caseElement.matches(property)) {
                if (i != selectedIndex) {
                    setPropertyBase(i, caseElement);
                }
                break;
            }
            i++;
        }
    }

    private void setProperty(int index) {
        Case<T, P> expectedCase = cases.get(index);
        T newProp = expectedCase.createProperty();
        Preconditions.checkNotNull(newProp);
        Preconditions.checkState(expectedCase.matches(newProp));
        setPropertyRaw(newProp);
        setPropertyBase(index, expectedCase);
    }

    private void setPropertyBase(int index, Case<T, P> caseElement) {
        selectedIndex = index;

        Menu<P> oldMenu = menu;
        menu = caseElement.menuFactory.get();
        menu.useActionList(actions);

        flowComponent.getMenusBox().getChildren().remove(oldMenu);
        flowComponent.addMenu(menu);
    }

    public Menu<P> getMenu() {
        return menu;
    }

    private void enableActions() {
        if (actions == EMPTY_LIST) {
            actions = new ArrayList<>();
        }
    }

    public void action(Supplier<IEntry> action) {
        enableActions();
        actions.add(action);
    }

    public void actionCycling() {
        action(() -> new AbstractEntry(null, "gui.sfm.ActionMenu.Menus.CycleProperty") {
            private int lastSelectedIndex = selectedIndex;
            private String cachedText;

            @Override
            public String getText() {
                // Lazy initialization so that even if the property is updated without using the cycling action, the text will still be in sync
                if (lastSelectedIndex != selectedIndex || cachedText == null) {
                    Case<T, P> caseElement = cases.get(selectedIndex);
                    cachedText = caseElement.hasName() ? I18n.format("gui.sfm.ActionMenu.Menus.CycleProperty.Named", caseElement.getName()) : super.getText();
                    lastSelectedIndex = selectedIndex;
                }
                return cachedText;
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                int nextIndex = selectedIndex + 1;
                selectedIndex = nextIndex >= cases.size() ? 0 : nextIndex;
                setProperty(selectedIndex);
                return true;
            }
        });
    }

    public static final class Case<T, P extends IProcedure & IProcedureClientData> {

        private Predicate<T> condition;
        private Supplier<T> propertyFactory;
        private Supplier<Menu<P>> menuFactory;
        private String name;

        private Case(Predicate<T> condition) {
            this.condition = condition;
        }

        public Case<T, P> then(Supplier<Menu<P>> factory) {
            Preconditions.checkState(this.menuFactory == null);
            this.menuFactory = factory;
            return this;
        }

        public Case<T, P> prop(Supplier<T> factory) {
            Preconditions.checkState(this.propertyFactory == null);
            this.propertyFactory = factory;
            return this;
        }

        public Case<T, P> name(String name) {
            this.name = name;
            return this;
        }

        public boolean matches(T property) {
            return condition.test(property);
        }

        @Nullable
        private T createProperty() {
            return propertyFactory == null ? null : propertyFactory.get();
        }

        public boolean hasName() {
            return name != null;
        }

        public String getName() {
            return name == null ? "" : name;
        }
    }
}
