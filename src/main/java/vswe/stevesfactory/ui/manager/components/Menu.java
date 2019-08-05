package vswe.stevesfactory.ui.manager.components;

import com.google.common.collect.ImmutableList;
import vswe.stevesfactory.library.gui.*;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.AbstractContainer;
import vswe.stevesfactory.library.gui.widget.button.AbstractIconButton;
import vswe.stevesfactory.library.gui.widget.mixin.ResizableWidgetMixin;
import vswe.stevesfactory.utils.RenderingHelper;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.*;

public abstract class Menu extends AbstractContainer<IWidget> implements ResizableWidgetMixin {

    public enum State {
        COLLAPSED(TextureWrapper.ofFlowComponent(1, 41, 9, 9),
                TextureWrapper.ofFlowComponent(10, 41, 9, 9)) {
            @Override
            public void toggleStateFor(Menu menu) {
                menu.expand();
            }
        },
        EXPANDED(TextureWrapper.ofFlowComponent(1, 50, 9, 9),
                TextureWrapper.ofFlowComponent(10, 50, 9, 9)) {
            @Override
            public void toggleStateFor(Menu menu) {
                menu.collapse();
            }
        };

        public final TextureWrapper toggleStateNormalTexture;
        public final TextureWrapper toggleStateHoveringTexture;

        State(TextureWrapper toggleStateNormalTexture, TextureWrapper toggleStateHoveringTexture) {
            this.toggleStateNormalTexture = toggleStateNormalTexture;
            this.toggleStateHoveringTexture = toggleStateHoveringTexture;
        }

        public abstract void toggleStateFor(Menu menu);
    }

    public static class ToggleStateButton extends AbstractIconButton {

        public ToggleStateButton(Menu parent) {
            super(110, 3, 9, 9);
            setParentWidget(parent);
        }

        @Override
        public TextureWrapper getTextureNormal() {
            return getParentWidget().state.toggleStateNormalTexture;
        }

        @Override
        public TextureWrapper getTextureHovered() {
            return getParentWidget().state.toggleStateHoveringTexture;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            getParentWidget().toggleState();
            return true;
        }

        @Nonnull
        @Override
        public Menu getParentWidget() {
            return Objects.requireNonNull((Menu) super.getParentWidget());
        }
    }

    public static final TextureWrapper HEADING_BOX = TextureWrapper.ofFlowComponent(67, 154, 120, 13);
    public static final int DEFAULT_CONTENT_HEIGHT = 65;

    private State state = State.COLLAPSED;

    private ToggleStateButton toggleStateButton;
    private final List<IWidget> children;

    public Menu() {
        // Start at a collapsed state
        super(0, 0, HEADING_BOX.getPortionWidth(), HEADING_BOX.getPortionHeight());
        this.toggleStateButton = new ToggleStateButton(this);
        this.children = ImmutableList.of(toggleStateButton);
    }

    @Override
    public List<IWidget> getChildren() {
        return children;
    }

    @Override
    public void reflow() {
    }

    @Override
    public IContainer<IWidget> addChildren(IWidget widget) {
        throw new UnsupportedOperationException();
    }

    @Override
    public IContainer<IWidget> addChildren(Collection<IWidget> widgets) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Dimension getDimensions() {
        return super.getDimensions();
    }

    public void toggleState() {
        state.toggleStateFor(this);
    }

    public void expand() {
        growHeight(getContentHeight());
    }

    public void collapse() {
        shrinkHeight(getContentHeight());
    }

    public void growHeight(int growth) {
        setHeight(getHeight() + growth);
    }

    public void shrinkHeight(int shrinkage) {
        growHeight(-shrinkage);
    }

    @Override
    public void setHeight(int height) {
        ResizableWidgetMixin.super.setHeight(height);
        getParentWidget().reflow();
    }

    @Override
    public void setWidth(int width) {
        throw new UnsupportedOperationException();
    }

    public int getContentHeight() {
        return DEFAULT_CONTENT_HEIGHT;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        HEADING_BOX.draw(getAbsoluteX(), getAbsoluteY());
        renderHeadingText();
        toggleStateButton.render(mouseX, mouseY, particleTicks);
        if (state == State.EXPANDED) {
            renderContents(mouseX, mouseY, particleTicks);
        }
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
    }

    public void renderHeadingText() {
        RenderingHelper.drawTextCenteredVertically(getHeadingText(), getHeadingLeftX(), getAbsoluteY(), getAbsoluteYBR(), getHeadingColor());
    }

    public int getHeadingLeftX() {
        return getAbsoluteX() + 5;
    }

    public int getHeadingColor() {
        return 0x000000;
    }

    public abstract String getHeadingText();

    public abstract void renderContents(int mouseX, int mouseY, float particleTicks);

    @Nonnull
    @Override
    public FlowComponent getParentWidget() {
        return Objects.requireNonNull((FlowComponent) super.getParentWidget());
    }
}
