package vswe.stevesfactory.library.gui.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import org.apache.commons.lang3.tuple.Pair;
import org.lwjgl.glfw.GLFW;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.library.gui.*;
import vswe.stevesfactory.library.gui.actionmenu.ActionMenu;
import vswe.stevesfactory.library.gui.actionmenu.DiscardCondition;
import vswe.stevesfactory.library.gui.debug.Inspections;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;

import java.util.List;
import java.util.Queue;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;

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
    private List<IWindow> windows = new ArrayList<>();
    private EnumMap<DiscardCondition, Set<ActionMenu>> actionMenus = new EnumMap<>(DiscardCondition.class);

    private final Queue<Consumer<WidgetScreen>> tasks = new ArrayDeque<>();

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
        windows.clear();
        RenderEventDispatcher.listeners.put(Inspections.class, inspectionHandler);
    }

    @Override
    public void tick() {
        while (!tasks.isEmpty()) {
            tasks.remove().accept(this);
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
        // Dark background overlay
        renderBackground();

        inspectionHandler.startCycle();
        primaryWindow.render(mouseX, mouseY, particleTicks);
        for (IWindow window : windows) {
            window.render(mouseX, mouseY, particleTicks);
        }
        inspectionHandler.endCycle();

        // This should do nothing because we are not adding vanilla buttons
        super.render(mouseX, mouseY, particleTicks);
    }

    public void addWindow(IWindow window) {
        windows.add(window);
    }

    public void clearWindows() {
        windows.forEach(IWindow::onRemoved);
        windows.clear();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        Set<ActionMenu> clickDiscards = actionMenus.get(DiscardCondition.UNFOCUSED_CLICK);
        removeActionMenus(clickDiscards, a -> !a.isInside(mouseX, mouseY));

        if (windows.stream().anyMatch(window -> window.mouseClicked(mouseX, mouseY, button))) {
            return true;
        } else {
            return primaryWindow.mouseClicked(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (windows.stream().anyMatch(window -> window.mouseReleased(mouseX, mouseY, button))) {
            return true;
        } else {
            return primaryWindow.mouseReleased(mouseX, mouseY, button);
        }
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double dragAmountX, double dragAmountY) {
        if (windows.stream().anyMatch(window -> window.mouseDragged(mouseX, mouseY, button, dragAmountX, dragAmountY))) {
            return true;
        } else {
            return primaryWindow.mouseDragged(mouseX, mouseY, button, dragAmountX, dragAmountY);
        }
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double amountScrolled) {
        if (windows.stream().anyMatch(window -> window.mouseScrolled(mouseX, mouseY, amountScrolled))) {
            return true;
        } else {
            return primaryWindow.mouseScrolled(mouseX, mouseY, amountScrolled);
        }
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        Set<ActionMenu> exitHoverDiscards = this.actionMenus.get(DiscardCondition.EXIT_HOVER);
        removeActionMenus(exitHoverDiscards, a -> !a.isInside(mouseX, mouseY));

        for (IWindow window : windows) {
            window.mouseMoved(mouseX, mouseY);
        }
        primaryWindow.mouseMoved(mouseX, mouseY);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (windows.stream().anyMatch(window -> window.keyPressed(keyCode, scanCode, modifiers))) {
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
        if (windows.stream().anyMatch(window -> window.keyReleased(keyCode, scanCode, modifiers))) {
            return true;
        } else {
            return primaryWindow.keyReleased(keyCode, scanCode, modifiers);
        }
    }

    @Override
    public boolean charTyped(char charTyped, int keyCode) {
        if (windows.stream().anyMatch(window -> window.charTyped(charTyped, keyCode))) {
            return true;
        } else {
            return primaryWindow.charTyped(charTyped, keyCode);
        }
    }

    public void scheduleTask(Consumer<WidgetScreen> task) {
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
        scheduleTask(self -> self.discardActionMenu(actionMenu));
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
        windows.removeIf(a -> a == actionMenu);
    }
}
