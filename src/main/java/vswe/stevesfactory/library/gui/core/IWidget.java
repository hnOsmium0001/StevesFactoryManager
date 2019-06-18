package vswe.stevesfactory.library.gui.core;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * A component of the GUI that has a rendering
 */
@OnlyIn(Dist.CLIENT)
public interface IWidget extends IGuiEventListener, IRenderable {

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

    @Nullable
    IWidget getParentWidget();

    IWindow getWindow();

    boolean isEnabled();

    void setEnabled(boolean enabled);

    default boolean isFocused() {
        return getWindow().getFocusedWidget() == this;
    }

    default void onFocusChanged(boolean focus) {
    }

    @Override
    default boolean changeFocus(boolean focus) {
        return getWindow().changeFocus(this, focus);
    }

    @Override
    boolean isMouseOver(double mouseX, double mouseY);

    @Override
    default boolean mouseClicked(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    default boolean mouseReleased(double mouseX, double mouseY, int button) {
        return false;
    }

    @Override
    default boolean mouseDragged(double mouseX, double mouseY, int button, double dragAmountX, double dragAmountY) {
        return false;
    }

    @Override
    default boolean mouseScrolled(double mouseX, double mouseY, double amountScrolled) {
        return false;
    }

    @Override
    default boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    default boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        return false;
    }

    @Override
    default boolean charTyped(char charTyped, int keyCode) {
        return false;
    }

}
