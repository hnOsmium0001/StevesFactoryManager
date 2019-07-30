package vswe.stevesfactory.library.gui.widget.scroll;

import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.debug.ITextReceiver;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.widget.AbstractIconButton;
import vswe.stevesfactory.library.gui.widget.mixin.LeafWidgetMixin;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableWidgetMixin;

import javax.annotation.Nonnull;
import java.util.Objects;

public abstract class Arrow extends AbstractIconButton implements LeafWidgetMixin, RelocatableWidgetMixin {

    private static final TextureWrapper UP_NORMAL = TextureWrapper.ofFlowComponent(0, 152, 10, 6);
    private static final TextureWrapper UP_HOVERED = UP_NORMAL.right(10);
    private static final TextureWrapper UP_CLICKED = UP_NORMAL.right(10 * 2);
    private static final TextureWrapper UP_DISABLED = UP_NORMAL.right(10 * 3);

    public static Arrow up(int x, int y) {
        return new Arrow(x, y) {
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
            protected int getScrollDirectionMask() {
                return -1;
            }
        };
    }

    private static final TextureWrapper DOWN_NORMAL = UP_NORMAL.down(6);
    private static final TextureWrapper DOWN_HOVERED = UP_HOVERED.down(6);
    private static final TextureWrapper DOWN_CLICKED = UP_CLICKED.down(6);
    private static final TextureWrapper DOWN_DISABLED = UP_DISABLED.down(6);

    public static Arrow down(int x, int y) {
        return new Arrow(x, y) {
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
            protected int getScrollDirectionMask() {
                return 1;
            }
        };
    }

    private boolean clicked = false;

    public Arrow(int x, int y) {
        super(x, y, 10, 6);
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        if (isEnabled()) {
            if (isClicked()) {
                getTextureClicked().draw(getAbsoluteX(), getAbsoluteY());
            } else if (isInside(mouseX, mouseY) && isEnabled()) {
                getTextureHovered().draw(getAbsoluteX(), getAbsoluteY());
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

    @Override
    public void update(float particleTicks) {
        if (clicked) {
            WrappingListView parent = getParentWidget();
            parent.scroll(parent.getScrollSpeed() * getScrollDirectionMask());
        }
    }

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
    public WrappingListView getParentWidget() {
        return Objects.requireNonNull((WrappingListView) super.getParentWidget());
    }

    @Override
    public void provideInformation(ITextReceiver receiver) {
        super.provideInformation(receiver);
        receiver.line("Clicked=" + clicked);
    }

    protected abstract int getScrollDirectionMask();
}
