package vswe.stevesfactory.library.gui.screen;

import com.mojang.datafixers.types.Func;
import it.unimi.dsi.fastutil.objects.Object2BooleanFunction;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.glfw.GLFW;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.library.collections.CompositeUnmodifiableList;
import vswe.stevesfactory.library.gui.IWindow;
import vswe.stevesfactory.library.gui.actionmenu.ActionMenu;
import vswe.stevesfactory.library.gui.actionmenu.DiscardCondition;
import vswe.stevesfactory.library.gui.debug.Inspections;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.window.IPopupWindow;

import java.util.*;
import java.util.function.*;

public abstract class WidgetScreen extends Screen implements IGuiEventListener {

    public static WidgetScreen getCurrentScreen() {
        return (WidgetScreen) Minecraft.getInstance().currentScreen;
    }

    public static int scaledWidth() {
        return Minecraft.getInstance().mainWindow.getScaledWidth();
    }

    public static int scaledHeight() {
        return Minecraft.getInstance().mainWindow.getScaledHeight();
    }

    private IWindow primaryWindow;
    private List<IWindow> regularWindows = new ArrayList<>();
    private List<IPopupWindow> popupWindows = new ArrayList<>();

    // TODO remove
    private EnumMap<DiscardCondition, Set<ActionMenu>> actionMenus = new EnumMap<>(DiscardCondition.class);
    private final Queue<Function<WidgetScreen, Boolean>> tasks = new ArrayDeque<>();

    private final WidgetTreeInspections inspectionHandler = new WidgetTreeInspections();

    protected WidgetScreen(ITextComponent title) {
        super(title);
        for (DiscardCondition condition : DiscardCondition.values()) {
            actionMenus.put(condition, new HashSet<>());
        }
    }

    @Override
    protected void init() {
        StevesFactoryManager.logger.trace("(Re)initialized widget-based GUI {}", this);
        primaryWindow = null;
        regularWindows.clear();
        popupWindows.clear();
        RenderEventDispatcher.listeners.put(Inspections.class, inspectionHandler);
    }

    @Override
    public void tick() {
        while (!tasks.isEmpty()) {
            if (tasks.peek().apply(this)) {
                tasks.remove();
            }
        }
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
        // Dark transparent overlay
        renderBackground();

        inspectionHandler.startCycle();
        primaryWindow.render(mouseX, mouseY, particleTicks);
        for (IWindow window : regularWindows) {
            window.render(mouseX, mouseY, particleTicks);
        }
        inspectionHandler.endCycle();

        // This should do nothing because we are not adding vanilla buttons
        super.render(mouseX, mouseY, particleTicks);
    }

    public void addWindow(IWindow window) {
        regularWindows.add(window);
    }

    public void clearWindows() {
        regularWindows.forEach(IWindow::onRemoved);
        regularWindows.clear();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Set<ActionMenu> clickDiscards = actionMenus.get(DiscardCondition.UNFOCUSED_CLICK);
        removeActionMenus(clickDiscards, a -> !a.isInside(mouseX, mouseY));

        if (regularWindows.stream().anyMatch(window -> window.mouseClicked(mouseX, mouseY, button))) {
            return true;
        } else {
            return primaryWindow.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (regularWindows.stream().anyMatch(window -> window.mouseReleased(mouseX, mouseY, button))) {
            return true;
        } else {
            return primaryWindow.mouseReleased(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragAmountX, double dragAmountY) {
        for (IPopupWindow popup : popupWindows) {
            if (popup.isDraggable() && popup.shouldDrag(mouseX, mouseY)) {
                popup.setPosition((int)mouseX, (int)mouseY);
            }
        }
        if (regularWindows.stream().anyMatch(window -> window.mouseDragged(mouseX, mouseY, button, dragAmountX, dragAmountY))) {
            return true;
        } else {
            return primaryWindow.mouseDragged(mouseX, mouseY, button, dragAmountX, dragAmountY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amountScrolled) {
        if (regularWindows.stream().anyMatch(window -> window.mouseScrolled(mouseX, mouseY, amountScrolled))) {
            return true;
        } else {
            return primaryWindow.mouseScrolled(mouseX, mouseY, amountScrolled);
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        Set<ActionMenu> exitHoverDiscards = this.actionMenus.get(DiscardCondition.EXIT_HOVER);
        removeActionMenus(exitHoverDiscards, a -> !a.isInside(mouseX, mouseY));

        for (IWindow window : regularWindows) {
            window.mouseMoved(mouseX, mouseY);
        }
        primaryWindow.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (regularWindows.stream().anyMatch(window -> window.keyPressed(keyCode, scanCode, modifiers))) {
            return true;
        } else if (primaryWindow.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }

        if (super.keyPressed(keyCode, scanCode, modifiers)) {
            return true;
        }
        if (keyCode == GLFW.GLFW_KEY_E) {
            Minecraft.getInstance().player.closeScreen();
            return true;
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (regularWindows.stream().anyMatch(window -> window.keyReleased(keyCode, scanCode, modifiers))) {
            return true;
        } else {
            return primaryWindow.keyReleased(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean charTyped(char charTyped, int keyCode) {
        if (regularWindows.stream().anyMatch(window -> window.charTyped(charTyped, keyCode))) {
            return true;
        } else {
            return primaryWindow.charTyped(charTyped, keyCode);
        }
    }

    public void scheduleTask(Function<WidgetScreen, Boolean> task) {
        tasks.add(task);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Action menu support
    ///////////////////////////////////////////////////////////////////////////

    public void openActionMenu(ActionMenu actionMenu, DiscardCondition discardCondition) {
        actionMenus.get(discardCondition).add(actionMenu);
        addWindow(actionMenu);
    }

    public void deferDiscardActionMenu(ActionMenu actionMenu) {
        scheduleTask(self -> {
            self.discardActionMenu(actionMenu);
            return true;
        });
    }

    public void discardActionMenu(ActionMenu actionMenu) {
        for (Set<ActionMenu> value : actionMenus.values()) {
            removeActionMenus(value, actionMenu::equals);
        }
    }

    private void removeActionMenus(Set<? extends ActionMenu> actionMenus, Predicate<ActionMenu> condition) {
        actionMenus.removeIf(actionMenu -> {
            if (condition.test(actionMenu)) {
                actionMenu.onDiscard();
                removeActionMenuAsWindow(actionMenu);
                return true;
            }
            return false;
        });
    }

    private void removeActionMenuAsWindow(ActionMenu actionMenu) {
        regularWindows.removeIf(a -> a == actionMenu);
    }

    ///////////////////////////////////////////////////////////////////////////
    // Popup window support
    ///////////////////////////////////////////////////////////////////////////

    public void addPopupWindow(IPopupWindow popup) {
        popup.initialize(popupWindows.size());
        popupWindows.add(popup);
    }

    public void removePopupWindow(int id) {
        popupWindows.remove(id);
    }
}
