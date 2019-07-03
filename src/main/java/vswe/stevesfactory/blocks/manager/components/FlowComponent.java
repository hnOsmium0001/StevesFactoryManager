package vswe.stevesfactory.blocks.manager.components;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.core.*;
import vswe.stevesfactory.library.gui.layout.misc.PositionalLayout;
import vswe.stevesfactory.library.gui.widget.TextField;
import vswe.stevesfactory.library.gui.widget.*;
import vswe.stevesfactory.library.gui.widget.mixin.ContainerWidgetMixin;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.*;

public abstract class FlowComponent extends AbstractWidget implements IContainer<IWidget>, ContainerWidgetMixin<IWidget> {

    public enum State {
        EXPANDED(TextureWrapper.ofFlowComponent(63, 1, 124, 152),
                TextureWrapper.ofFlowComponent(1, 21, 9, 10),
                TextureWrapper.ofFlowComponent(10, 21, 9, 10)) {
            @Override
            public void changeState(FlowComponent flowComponent) {
                flowComponent.collapse();
            }
        },
        COLLAPSED(TextureWrapper.ofFlowComponent(0, 0, 64, 20),
                TextureWrapper.ofFlowComponent(1, 31, 9, 10),
                TextureWrapper.ofFlowComponent(10, 31, 9, 10)) {
            @Override
            public void changeState(FlowComponent flowComponent) {
                flowComponent.expand();
            }
        };

        public final TextureWrapper background;
        public final TextureWrapper toggleStateNormal;
        public final TextureWrapper toggleStateHovered;

        public final Dimension dimensions;

        State(TextureWrapper background, TextureWrapper toggleStateNormal, TextureWrapper toggleStateHovered) {
            this.background = background;
            this.toggleStateNormal = toggleStateNormal;
            this.toggleStateHovered = toggleStateHovered;

            this.dimensions = new Dimension(componentWidth(), componentHeight());
        }

        public int componentWidth() {
            return background.getPortionWidth();
        }

        public int componentHeight() {
            return background.getPortionHeight();
        }

        public abstract void changeState(FlowComponent flowComponent);
    }

    public static class ToggleStateButton extends AbstractIconButton {

        public ToggleStateButton(FlowComponent parent) {
            super(55, 6, 9, 10);
            onParentChanged(parent);
        }

        @Override
        public TextureWrapper getTextureNormal() {
            return getParentWidget().getState().toggleStateNormal;
        }

        @Override
        public TextureWrapper getTextureHovering() {
            return getParentWidget().getState().toggleStateHovered;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            getParentWidget().toggleState();
            return true;
        }

        @Nonnull
        @Override
        public FlowComponent getParentWidget() {
            return Objects.requireNonNull((FlowComponent) super.getParentWidget());
        }
    }

    public static class RenameButton extends AbstractIconButton {

        public static final TextureWrapper NORMAL = TextureWrapper.ofFlowComponent(33, 190, 9, 9);
        public static final TextureWrapper HOVERING = TextureWrapper.ofFlowComponent(42, 190, 9, 9);

        public RenameButton(FlowComponent parent) {
            super(44, 7, 9, 9);
            onParentChanged(parent);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isEnabled()) {
                setEnabled(false);
                FlowComponent parent = getParentWidget();
                parent.submitButton.setEnabled(true);
                parent.cancelButton.setEnabled(true);
                return true;
            }
            return false;
        }

        @Override
        public TextureWrapper getTextureNormal() {
            return NORMAL;
        }

        @Override
        public TextureWrapper getTextureHovering() {
            return HOVERING;
        }

        @Nonnull
        @Override
        public FlowComponent getParentWidget() {
            return Objects.requireNonNull((FlowComponent) super.getParentWidget());
        }
    }

    public static class SubmitButton extends AbstractIconButton {

        public static final TextureWrapper NORMAL = TextureWrapper.ofFlowComponent(33, 199, 7, 7);
        public static final TextureWrapper HOVERING = TextureWrapper.ofFlowComponent(40, 199, 7, 7);

        public SubmitButton(FlowComponent parent) {
            super(46, 3, 7, 7);
            onParentChanged(parent);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isEnabled()) {
                setEnabled(false);
                FlowComponent parent = getParentWidget();
                parent.cancelButton.setEnabled(false);
                parent.renameButton.setEnabled(true);
                return true;
            }
            return false;
        }

        @Override
        public TextureWrapper getTextureNormal() {
            return NORMAL;
        }

        @Override
        public TextureWrapper getTextureHovering() {
            return HOVERING;
        }

        @Nonnull
        @Override
        public FlowComponent getParentWidget() {
            return Objects.requireNonNull((FlowComponent) super.getParentWidget());
        }
    }

    public static class CancelButton extends AbstractIconButton {

        public static final TextureWrapper NORMAL = TextureWrapper.ofFlowComponent(33, 206, 7, 7);
        public static final TextureWrapper HOVERING = TextureWrapper.ofFlowComponent(40, 206, 7, 7);

        private String previousName;

        public CancelButton(FlowComponent parent) {
            super(46, 12, 7, 7);
            onParentChanged(parent);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isEnabled()) {
                setEnabled(false);
                FlowComponent parent = getParentWidget();
                parent.submitButton.setEnabled(false);
                parent.renameButton.setEnabled(true);
                parent.setName(previousName);
                previousName = "";
                return true;
            }
            return false;
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            if (enabled) {
                previousName = getParentWidget().getName();
            }
        }

        @Override
        public TextureWrapper getTextureNormal() {
            return NORMAL;
        }

        @Override
        public TextureWrapper getTextureHovering() {
            return HOVERING;
        }

        @Nonnull
        @Override
        public FlowComponent getParentWidget() {
            return Objects.requireNonNull((FlowComponent) super.getParentWidget());
        }
    }


    private ToggleStateButton toggleStateButton;
    private RenameButton renameButton;
    private SubmitButton submitButton;
    private CancelButton cancelButton;
    private TextField name;
    private final ImmutableList<IWidget> children;

    private State state = State.COLLAPSED;

    public FlowComponent() {
        super(0, 0);
        this.toggleStateButton = new ToggleStateButton(this);
        this.renameButton = new RenameButton(this);
        this.submitButton = new SubmitButton(this);
        this.cancelButton = new CancelButton(this);
        this.name = new TextField(0, 0, 0, 0);
        this.name.onParentChanged(this);
        this.children = ImmutableList.of(toggleStateButton, renameButton, submitButton, cancelButton);
    }

    @Override
    public Dimension getDimensions() {
        return state.dimensions;
    }

    public TextureWrapper getBackgroundTexture() {
        return state.background;
    }

    public State getState() {
        return state;
    }

    public void expand() {
        Preconditions.checkState(state == State.COLLAPSED);
        state = State.EXPANDED;
    }

    public void collapse() {
        Preconditions.checkState(state == State.EXPANDED);
        state = State.COLLAPSED;
    }

    public void toggleState() {
        state.changeState(this);
    }

    public String getName() {
        return name.getText();
    }

    public void setName(String name) {
        this.name.setText(name);
    }

    @Override
    public List<IWidget> getChildren() {
        return children;
    }

    @Override
    public ILayout<IWidget> getLayout() {
        return PositionalLayout.INSTANCE;
    }

    @Override
    public final IContainer<IWidget> addChildren(IWidget widget) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final IContainer<IWidget> addChildren(Collection<IWidget> widgets) {
        throw new UnsupportedOperationException();
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        getBackgroundTexture().draw(getAbsoluteX(), getAbsoluteY());
        ContainerWidgetMixin.super.render(mouseX, mouseY, particleTicks);
    }
}