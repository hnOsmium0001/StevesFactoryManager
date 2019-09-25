package vswe.stevesfactory.ui.manager.menu;

import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.layout.properties.BoxSizing;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

public final class SettingsEditor extends AbstractContainer<IWidget> {

    private final List<IWidget> children = new ArrayList<>();

    public SettingsEditor(MultiLayerMenu<?> menu) {
        setDimensions(menu.getWidth(), menu.getContentHeight());

        AbstractIconButton close = new AbstractIconButton(getWidth() - 8 - 1, getHeight() - 8 - 1, 8, 8) {
            @Override
            public void render(int mouseX, int mouseY, float particleTicks) {
                super.render(mouseX, mouseY, particleTicks);
                if (isHovered()) {
                    WidgetScreen.getCurrentScreen().setHoveringText(I18n.format("gui.sfm.Menu.CloseEditor.Info"), mouseX, mouseY);
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

    public Checkbox addOption(boolean value, BooleanConsumer setter, String translationKey) {
        Checkbox checkbox = new Checkbox(0, 0, 8, 8);
        checkbox.setChecked(value);
        checkbox.translateLabel(translationKey);
        checkbox.onStateChange = setter;
        children.add(checkbox);
        reflow();
        return checkbox;
    }

    public NumberField<Integer> addIntegerInput(int value, int lowerBound, int upperBound) {
        NumberField<Integer> field = NumberField.integerFieldRanged(33, 12, value, lowerBound, upperBound);
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
        int y = 4;
        // Exclude the close button
        for (int i = 1; i < children.size(); i++) {
            IWidget widget = children.get(i);
            if (BoxSizing.shouldIncludeWidget(widget)) {
                widget.setLocation(4, y);
                y += widget.getHeight() + 4;
            }
        }
    }

    @Nonnull
    @Override
    public MultiLayerMenu<?> getParentWidget() {
        return (MultiLayerMenu<?>) Objects.requireNonNull(super.getParentWidget());
    }
}
