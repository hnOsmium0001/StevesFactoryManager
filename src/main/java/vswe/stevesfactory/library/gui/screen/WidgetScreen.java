package vswe.stevesfactory.library.gui.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.Pair;
import vswe.stevesfactory.library.gui.background.DisplayListCaches;
import vswe.stevesfactory.library.gui.core.IWindow;
import vswe.stevesfactory.library.gui.window.IWindowPositionHandler;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public abstract class WidgetScreen extends Screen {

    public static int scaledWidth() {
        return Minecraft.getInstance().mainWindow.getScaledWidth();
    }

    public static int scaledHeight() {
        return Minecraft.getInstance().mainWindow.getScaledHeight();
    }

    private IWindow primaryWindow;
    private IWindowPositionHandler positionHandler;

    private List<Pair<IWindow, IWindowPositionHandler>> windows = new ArrayList<>();

    private Rectangle screenBounds = new Rectangle();

    protected WidgetScreen(ITextComponent title, int width, int height) {
        super(title);
        this.setScreenBounds(width, height);
    }

    @Override
    protected void init() {
    }

    @Override
    public void tick() {
    }

    protected void initalizePrimaryWindow(IWindow primaryWindow, IWindowPositionHandler positionHandler) {
        if (this.primaryWindow == null && this.positionHandler == null) {
            this.primaryWindow = primaryWindow;
            this.positionHandler = positionHandler;
        }
        throw new IllegalStateException("Already initialized the primary window " + this.primaryWindow + " and position handler " + this.positionHandler);
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        // Dark overlay
        renderBackground();
        // Window frame
        GlStateManager.callList(DisplayListCaches.createVanillaStyleBackground(screenBounds));

        primaryWindow.resolvePosition(positionHandler);
        primaryWindow.render();
        for (Pair<IWindow, IWindowPositionHandler> pair : windows) {
            IWindow window = pair.getLeft();
            window.resolvePosition(pair.getRight());
            window.render();
        }

        // This should do nothing because we are not adding vanilla buttons
        super.render(mouseX, mouseY, particleTicks);
    }

    public Rectangle getScreenBounds() {
        return screenBounds;
    }

    public void setScreenBounds(int width, int height) {
        this.width = width;
        this.height = height;
        this.screenBounds.width = width;
        this.screenBounds.height = height;
        this.screenBounds.x = scaledWidth() / 2 - width / 2;
        this.screenBounds.y = scaledHeight() / 2 - height / 2;
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
    public boolean isMouseOver(double mouseX, double mouseY) {
        return true;
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
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (windows.stream().noneMatch(window -> window.getLeft().keyPressed(keyCode, scanCode, modifiers))) {
            primaryWindow.keyPressed(keyCode, scanCode, modifiers);
        }
        return true;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (windows.stream().noneMatch(window -> window.getLeft().keyReleased(keyCode, scanCode, modifiers))) {
            primaryWindow.keyReleased(keyCode, scanCode, modifiers);
        }
        return true;
    }

    @Override
    public boolean charTyped(char charTyped, int keyCode) {
        if (windows.stream().noneMatch(window -> window.getLeft().charTyped(charTyped, keyCode))) {
            primaryWindow.charTyped(charTyped, keyCode);
        }
        return true;
    }

}
