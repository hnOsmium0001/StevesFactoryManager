package vswe.stevesfactory.library.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.Pair;
import vswe.stevesfactory.library.IWindow;
import vswe.stevesfactory.library.gui.window.IWindowPositionHandler;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;

public abstract class WidgetScreen extends Screen implements IGuiEventListener {

    public static int scaledWidth() {
        return Minecraft.getInstance().mainWindow.getScaledWidth();
    }

    public static int scaledHeight() {
        return Minecraft.getInstance().mainWindow.getScaledHeight();
    }

    private IWindow primaryWindow;

    private List<Pair<IWindow, IWindowPositionHandler>> windows = new ArrayList<>();

    protected WidgetScreen(ITextComponent title) {
        super(title);
    }

    @Override
    protected void init() {
        primaryWindow = null;
        windows.clear();
    }

    @Override
    public void tick() {
    }

    protected void initializePrimaryWindow(IWindow primaryWindow) {
        if (this.primaryWindow == null) {
            this.primaryWindow = primaryWindow;
        } else {
            throw new IllegalStateException("Already initialized the primary window " + this.primaryWindow);
        }
    }

    public IWindow getPrimaryWindow() {
        return primaryWindow;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        // Dark background overlay
        renderBackground();

        primaryWindow.render(mouseX, mouseY, particleTicks);
        for (Pair<IWindow, IWindowPositionHandler> pair : windows) {
            IWindow window = pair.getLeft();
            window.render(mouseX, mouseY, particleTicks);
        }

        // This should do nothing because we are not adding vanilla buttons
        super.render(mouseX, mouseY, particleTicks);
    }

    public void addWindow(IWindow window, IWindowPositionHandler positionHandler) {
        addWindow(Pair.of(window, positionHandler));
    }

    public void addWindow(Pair<IWindow, IWindowPositionHandler> window) {
        windows.add(window);
    }

    public void clearWindows() {
        windows.forEach(window -> window.getLeft().onRemoved());
        windows.clear();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (windows.stream().noneMatch(window -> window.getLeft().mouseClicked(mouseX, mouseY, button))) {
            primaryWindow.mouseClicked(mouseX, mouseY, button);
        }
        return true;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (windows.stream().noneMatch(window -> window.getLeft().mouseReleased(mouseX, mouseY, button))) {
            primaryWindow.mouseReleased(mouseX, mouseY, button);
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragAmountX, double dragAmountY) {
        if (windows.stream().noneMatch(window -> window.getLeft().mouseDragged(mouseX, mouseY, button, dragAmountX, dragAmountY))) {
            primaryWindow.mouseDragged(mouseX, mouseY, button, dragAmountX, dragAmountY);
        }
        return true;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amountScrolled) {
        if (windows.stream().noneMatch(window -> window.getLeft().mouseScrolled(mouseX, mouseY, amountScrolled))) {
            primaryWindow.mouseScrolled(mouseX, mouseY, amountScrolled);
        }
        return true;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        // TODO add this event to widgets
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (windows.stream().noneMatch(window -> window.getLeft().keyPressed(keyCode, scanCode, modifiers))) {
            if (primaryWindow.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }

        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == KeyEvent.VK_E) {
            Minecraft.getInstance().player.closeScreen();
            return true;
        }
        return true;
    }

    // TODO figure out why is is gone
//    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
//        if (windows.stream().noneMatch(window -> window.getLeft().keyReleased(keyCode, scanCode, modifiers))) {
//            primaryWindow.keyReleased(keyCode, scanCode, modifiers);
//        }
//        return true;
//    }

    @Override
    public boolean charTyped(char charTyped, int keyCode) {
        if (windows.stream().noneMatch(window -> window.getLeft().charTyped(charTyped, keyCode))) {
            primaryWindow.charTyped(charTyped, keyCode);
        }
        return true;
    }

}
