package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.layout.FlowLayout;
import vswe.stevesfactory.library.gui.layout.properties.BoxSizing;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;

import javax.annotation.Nonnull;
import java.util.*;

public final class SettingsEditor extends AbstractContainer<IWidget> {

    private final List<IWidget> children = new ArrayList<>();

    public SettingsEditor(MultiLayerMenu<?> menu) {
        setDimensions(menu.getWidth(), menu.getContentHeight());

        AbstractIconButton close = new AbstractIconButton(getWidth() - 8 - 1, getHeight() - 8 - 1, 8, 8) {
            @Override
            public void render(int mouseX, int mouseY, float particleTicks) {
                super.render(mouseX, mouseY, particleTicks);
                if (isHovered()) {
                    WidgetScreen.getCurrent().setHoveringText(I18n.format("menu.sfm.CloseEditor.Info"), mouseX, mouseY);
                }
            }

            @Override
            public TextureWrapper getTextureNormal() {
                return FactoryManagerGUI.CLOSE_ICON;
            }

            @Override
            public TextureWrapper getTextureHovered() {
                return FactoryManagerGUI.CLOSE_ICON_HOVERED;
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                menu.openEditor(null);
                return super.mouseClicked(mouseX, mouseY, button);
            }

            @Override
            public BoxSizing getBoxSizing() {
                return BoxSizing.PHANTOM;
            }
        };
        close.setParentWidget(this);
        children.add(close);
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        super.render(mouseX, mouseY, particleTicks);
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    public Checkbox addOption(boolean value, String translationKey) {
        Checkbox checkbox = new Checkbox(0, 0, 8, 8);
        checkbox.setChecked(value);
        checkbox.translateLabel(translationKey);
        children.add(checkbox);
        reflow();
        return checkbox;
    }

    public NumberField<Integer> addIntegerInput(int defaultValue, int lowerBound, int upperBound) {
        NumberField<Integer> field = NumberField.integerFieldRanged(33, 12, defaultValue, lowerBound, upperBound);
        children.add(field);
        reflow();
        return field;
    }

    public void addLine(IWidget widget) {
        children.add(widget);
    }

    @Override
    public Collection<IWidget> getChildren() {
        return children;
    }

    @Override
    public void reflow() {
        FlowLayout.vertical(children, 4, 4, 4);
    }

    @Nonnull
    @Override
    public MultiLayerMenu<?> getParentWidget() {
        return (MultiLayerMenu<?>) Objects.requireNonNull(super.getParentWidget());
    }
}
