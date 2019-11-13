package vswe.stevesfactory.ui.manager.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureClientData;
import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.AbstractIconButton;
import vswe.stevesfactory.library.gui.widget.IWidget;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;
import vswe.stevesfactory.ui.manager.editor.Menu;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

public abstract class MultiLayerMenu<P extends IProcedure & IProcedureClientData> extends Menu<P> {

    private IWidget openEditor;

    @Override
    public void renderContents(int mouseX, int mouseY, float particleTicks) {
        GlStateManager.color3f(1F, 1F, 1F);
        if (openEditor != null) {
            getToggleStateButton().render(mouseX, mouseY, particleTicks);
            openEditor.render(mouseX, mouseY, particleTicks);
        } else {
            super.renderContents(mouseX, mouseY, particleTicks);
        }
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (openEditor != null) {
            if (getToggleStateButton().isInside(mouseX, mouseY)) {
                return getToggleStateButton().mouseClicked(mouseX, mouseY, button);
            }
            return openEditor.mouseClicked(mouseX, mouseY, button);
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (openEditor != null) {
            if (getToggleStateButton().isInside(mouseX, mouseY)) {
                return getToggleStateButton().mouseReleased(mouseX, mouseY, button);
            }
            return openEditor.mouseReleased(mouseX, mouseY, button);
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (openEditor != null) {
            return openEditor.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (openEditor != null) {
            return openEditor.mouseScrolled(mouseX, mouseY, scroll);
        }
        return super.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (openEditor != null) {
            return openEditor.keyPressed(keyCode, scanCode, modifiers);
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (openEditor != null) {
            return openEditor.keyReleased(keyCode, scanCode, modifiers);
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char charTyped, int keyCode) {
        if (openEditor != null) {
            return openEditor.charTyped(charTyped, keyCode);
        }
        return super.charTyped(charTyped, keyCode);
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (openEditor != null) {
            openEditor.mouseMoved(mouseX, mouseY);
        } else {
            super.mouseMoved(mouseX, mouseY);
        }
    }

    @Override
    public void update(float particleTicks) {
        if (openEditor != null) {
            openEditor.update(particleTicks);
        } else {
            super.update(particleTicks);
        }
    }

    @Override
    public void notifyChildrenForPositionChange() {
        super.notifyChildrenForPositionChange();
        if (openEditor != null) {
            openEditor.onParentPositionChanged();
        }
    }

    public IWidget getOpenEditor() {
        return openEditor;
    }

    public void openEditor(@Nullable IWidget editor) {
        this.openEditor = editor;
        if (editor != null) {
            editor.setParentWidget(this);
            editor.setLocation(0, HEADING_BOX.getPortionHeight());
        }
    }

    public abstract IWidget getEditor();

    public static class OpenSettingsButton extends AbstractIconButton {

        public OpenSettingsButton(int x, int y) {
            super(x, y, 12, 12);
        }

        @Override
        public TextureWrapper getTextureNormal() {
            return FactoryManagerGUI.SETTINGS_ICON;
        }

        @Override
        public TextureWrapper getTextureHovered() {
            return FactoryManagerGUI.SETTINGS_ICON_HOVERED;
        }

        @Override
        public void render(int mouseX, int mouseY, float particleTicks) {
            super.render(mouseX, mouseY, particleTicks);
            if (isHovered()) {
                WidgetScreen.getCurrentScreen().setHoveringText(I18n.format("gui.sfm.Menu.ItemFilter.Traits.Settings"), mouseX, mouseY);
            }
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            MultiLayerMenu<?> parent = getParentWidget();
            parent.openEditor(parent.getEditor());
            return true;
        }

        @Nonnull
        @Override
        public MultiLayerMenu<?> getParentWidget() {
            return (MultiLayerMenu<?>) Objects.requireNonNull(super.getParentWidget());
        }
    }
}
