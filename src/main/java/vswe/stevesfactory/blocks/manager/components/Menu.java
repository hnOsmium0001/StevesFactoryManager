package vswe.stevesfactory.blocks.manager.components;

import com.google.common.collect.ImmutableList;
import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.core.IContainer;
import vswe.stevesfactory.library.gui.core.IWidget;
import vswe.stevesfactory.library.gui.widget.AbstractIconButton;
import vswe.stevesfactory.library.gui.widget.AbstractWidget;
import vswe.stevesfactory.library.gui.widget.mixin.*;
import vswe.stevesfactory.utils.RenderingHelper;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.*;

public abstract class Menu extends AbstractWidget implements IContainer<IWidget>, ContainerWidgetMixin<IWidget>, RelocatableWidgetMixin, ResizableWidgetMixin {

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

        public final TextureWrapper toggleStateNormal;
        public final TextureWrapper toggleStateHovering;

        State(TextureWrapper toggleStateNormal, TextureWrapper toggleStateHovering) {
            this.toggleStateNormal = toggleStateNormal;
            this.toggleStateHovering = toggleStateHovering;
        }

        public abstract void toggleStateFor(Menu menu);
    }

    public static class ToggleStateButton extends AbstractIconButton {

        public ToggleStateButton(Menu parent) {
            super(110, 3, 9, 9);
            onParentChanged(parent);
        }

        @Override
        public TextureWrapper getTextureNormal() {
            return getParentWidget().state.toggleStateNormal;
        }

        @Override
        public TextureWrapper getTextureHovering() {
            return getParentWidget().state.toggleStateHovering;
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
        super(HEADING_BOX.getPortionWidth(), HEADING_BOX.getPortionHeight());
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
        HEADING_BOX.draw(getAbsoluteX(), getAbsoluteY());
        renderHeadingText();
        toggleStateButton.render(mouseX, mouseY, particleTicks);
        if (state == State.EXPANDED) {
            renderContents(mouseX, mouseY, particleTicks);
        }
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
