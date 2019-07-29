package vswe.stevesfactory.library.gui.widget.scroll;

import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.AbstractIconButton;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableWidgetMixin;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class ScrollArrow extends AbstractIconButton implements LeafWidgetMixin, RelocatableWidgetMixin {

    private static final TextureWrapper UP_NORMAL = TextureWrapper.ofFlowComponent(0, 152, 10, 6);
    private static final TextureWrapper UP_HOVERED = UP_NORMAL.right(10);
    private static final TextureWrapper UP_CLICKED = UP_HOVERED.right(10);
    private static final TextureWrapper UP_DISABLED = UP_CLICKED.right(10);

    public static ScrollArrow up(int x, int y) {
        return new ScrollArrow(x, y) {
            @Override
            public TextureWrapper getTextureNormal() {
                return UP_NORMAL;
            }

            @Override
            public TextureWrapper getTextureHovered() {
                return UP_HOVERED;
            }

            @Override
            public TextureWrapper getTextureClicked() {
                return UP_CLICKED;
            }

            @Override
            public TextureWrapper getTextureDisabled() {
                return UP_DISABLED;
            }

            @Override
            public void update(float particleTicks) {
                getParentWidget().scrollUpUnit();
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                getParentWidget().scrollUpUnit();
                return super.mouseClicked(mouseX, mouseY, button);
            }
        };
    }

    private static final TextureWrapper DOWN_NORMAL = UP_NORMAL.down(6);
    private static final TextureWrapper DOWN_HOVERED = UP_HOVERED.down(6);
    private static final TextureWrapper DOWN_CLICKED = UP_CLICKED.down(6);
    private static final TextureWrapper DOWN_DISABLED = UP_DISABLED.down(6);

    public static ScrollArrow down(int x, int y) {
        return new ScrollArrow(x, y) {
            @Override
            public TextureWrapper getTextureNormal() {
                return DOWN_NORMAL;
            }

            @Override
            public TextureWrapper getTextureHovered() {
                return DOWN_HOVERED;
            }

            @Override
            public TextureWrapper getTextureClicked() {
                return DOWN_CLICKED;
            }

            @Override
            public TextureWrapper getTextureDisabled() {
                return DOWN_DISABLED;
            }

            @Override
            public void update(float particleTicks) {
                getParentWidget().scrollDownUnit();
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {
                getParentWidget().scrollDownUnit();
                return super.mouseClicked(mouseX, mouseY, button);
            }
        };
    }

    private boolean clicked;

    public ScrollArrow(int x, int y) {
        super(x, y, 10, 6);
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        if (isEnabled()) {
            if (isInside(mouseX, mouseY) && isEnabled()) {
                getTextureHovered().draw(getAbsoluteX(), getAbsoluteY());
            } else if (isClicked()) {
                getTextureClicked().draw(getAbsoluteX(), getAbsoluteY());
            } else {
                getTextureNormal().draw(getAbsoluteX(), getAbsoluteY());
            }
        } else {
            getTextureDisabled().draw(getAbsoluteX(), getAbsoluteY());
        }
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        clicked = true;
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        clicked = false;
        return true;
    }

    public abstract void update(float particleTicks);

    @Override
    public abstract TextureWrapper getTextureNormal();

    @Override
    public abstract TextureWrapper getTextureHovered();

    public abstract TextureWrapper getTextureClicked();

    public abstract TextureWrapper getTextureDisabled();

    public boolean isClicked() {
        return clicked;
    }

    @Nonnull
    @Override
    public ScrollController getParentWidget() {
        return Objects.requireNonNull((ScrollController) super.getParentWidget());
    }
}
