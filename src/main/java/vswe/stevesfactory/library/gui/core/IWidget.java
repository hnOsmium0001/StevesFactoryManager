package vswe.stevesfactory.library.gui.core;

import net.minecraft.client.gui.IRenderable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.awt.*;

@OnlyIn(Dist.CLIENT)
public interface IWidget extends IRenderable {

    /**
     * Local coordinate relative to the parent component
     */
    Point getPosition();

    int getX();

    int getY();

    int getAbsoluteX();

    int getAbsoluteY();

    Dimension getDimensions();

    int getWidth();

    int getHeight();

    @Override
    void render(int mouseX, int mouseY, float particleTicks);

    IWidget getParentWidget();

    IWindow getWindow();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    default boolean isFocused() {
        return getWindow().getFocusedWidget() == this;
    }

    default void onFocusChanged(boolean focus) {
    }

    default void onDestruct() {
    }

    /**
     * @implSpec Calling this method should update the value returned by {@link #getParentWidget()} and trigger {@link #onParentPositionChanged()}.
     */
    void onParentChanged(IWidget newParent);

    void onWindowChanged(IWindow newWindow, IWidget newParent);

    void onParentPositionChanged();

    /**
     * @implNote Event capture method. Return {@code true} to stop propagation of the event to other widgets, otherwise the process would
     * continue.
     */
    boolean isInside(double x, double y);

    /**
     * @implNote Event capture method. Return {@code true} to stop propagation of the event to other widgets, otherwise the process would
     * continue.
     */
    boolean mouseClicked(double mouseX, double mouseY, int button);

    /**
     * @implNote Event capture method. Return {@code true} to stop propagation of the event to other widgets, otherwise the process would
     * continue.
     */
    boolean mouseReleased(double mouseX, double mouseY, int button);

    /**
     * @implNote Event capture method. Return {@code true} to stop propagation of the event to other widgets, otherwise the process would
     * continue.
     */
    boolean mouseDragged(double mouseX, double mouseY, int button, double dragAmountX, double dragAmountY);

    /**
     * @implNote Event capture method. Return {@code true} to stop propagation of the event to other widgets, otherwise the process would
     * continue.
     */
    boolean mouseScrolled(double mouseX, double mouseY, double amountScrolled);

    /**
     * @implNote Event capture method. Return {@code true} to stop propagation of the event to other widgets, otherwise the process would
     * continue.
     */
    boolean keyPressed(int keyCode, int scanCode, int modifiers);

    /**
     * @implNote Event capture method. Return {@code true} to stop propagation of the event to other widgets, otherwise the process would
     * continue.
     */
    boolean keyReleased(int keyCode, int scanCode, int modifiers);

    /**
     * @implNote Event capture method. Return {@code true} to stop propagation of the event to other widgets, otherwise the process would
     * continue.
     */
    boolean charTyped(char charTyped, int keyCode);

}
