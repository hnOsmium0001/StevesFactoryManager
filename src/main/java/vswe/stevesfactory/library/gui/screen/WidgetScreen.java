package vswe.stevesfactory.library.gui.screen;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.util.text.ITextComponent;
import org.lwjgl.glfw.GLFW;
import vswe.stevesfactory.StevesFactoryManager;
import vswe.stevesfactory.library.gui.IWindow;
import vswe.stevesfactory.library.gui.debug.Inspections;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.window.DiscardCondition;
import vswe.stevesfactory.library.gui.window.IPopupWindow;

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
    private List<IWindow> regularWindows = new ArrayList<>();
    private EnumMap<DiscardCondition, Set<IPopupWindow>> popupWindows = new EnumMap<>(DiscardCondition.class);
    private Collection<IWindow> windows;

    private final Queue<Consumer<WidgetScreen>> tasks = new ArrayDeque<>();
    private final Object2LongMap<IPopupWindow> lifespanCache = new Object2LongOpenHashMap<>();

    private final WidgetTreeInspections inspectionHandler = new WidgetTreeInspections();

    protected WidgetScreen(ITextComponent title) {
        super(title);
        for (DiscardCondition condition : DiscardCondition.values()) {
            popupWindows.put(condition, new HashSet<>());
        }
        windows = createWindowReferences();
    }

    private WindowCollection createWindowReferences() {
        // Internal usages only
        @SuppressWarnings("unchecked") Collection<IWindow>[] arr = new Collection[popupWindows.size() + 1];
        arr[0] = regularWindows;
        int i = 1;
        for (Set<IPopupWindow> set : popupWindows.values()) {
            // All downwards casting
            @SuppressWarnings("unchecked") Collection<IWindow> c = (Collection<IWindow>) (Collection<? extends IWindow>) set;
            arr[i] = c;
            i++;
        }
        return new WindowCollection(arr);
    }

    @Override
    protected void init() {
        StevesFactoryManager.logger.trace("(Re)initialized widget-based GUI {}", this);
        primaryWindow = null;
        regularWindows.clear();
        popupWindows.forEach((c, s) -> s.clear());
        RenderEventDispatcher.listeners.put(Inspections.class, inspectionHandler);
    }

    @Override
    public void tick() {
        while (!tasks.isEmpty()) {
            tasks.remove().accept(this);
        }

        long currentTime = Minecraft.getInstance().world.getGameTime();
        for (Object2LongMap.Entry<IPopupWindow> entry : lifespanCache.object2LongEntrySet()) {
            long diff = currentTime - entry.getLongValue();
            IPopupWindow popup = entry.getKey();
            if (diff >= popup.getLifespan()) {
                deferRemovePopupWindow(popup);
            }
        }

        float particleTicks = Minecraft.getInstance().getRenderPartialTicks();
        for (IWindow window : windows) {
            window.update(particleTicks);
        }
        primaryWindow.update(particleTicks);
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
        for (IWindow window : windows) {
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
        Set<IPopupWindow> clickDiscards = popupWindows.get(DiscardCondition.UNFOCUSED_CLICK);
        removePopupWindows(clickDiscards, p -> !p.isInside(mouseX, mouseY));

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
        // Dragging popup windows
        for (Set<IPopupWindow> windows : popupWindows.values()) {
            for (IPopupWindow popup : windows) {
                if (popup.isDraggable() && popup.shouldDrag(mouseX, mouseY)) {
                    popup.setPosition((int) mouseX, (int) mouseY);
                }
            }
        }

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
        Set<IPopupWindow> exitHoverDiscards = popupWindows.get(DiscardCondition.EXIT_HOVER);
        removePopupWindows(exitHoverDiscards, p -> !p.isInside(mouseX, mouseY));

        for (IWindow window : regularWindows) {
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
    // Popup window support
    ///////////////////////////////////////////////////////////////////////////

    public void addPopupWindow(IPopupWindow popup) {
        if (popup.getLifespan() == 0) {
            StevesFactoryManager.logger.debug("The popup {} has a lifespan of 0, therefore it is removed immediately", popup);
            return;
        }
        popupWindows.get(popup.getDiscardCondition()).add(popup);
        if (popup.getLifespan() > -1) {
            lifespanCache.put(popup, Minecraft.getInstance().world.getGameTime());
        }
    }

    public void removePopupWindow(IPopupWindow popup) {
        popupWindows.get(popup.getDiscardCondition()).remove(popup);
        popup.onRemoved();
    }

    public void deferRemovePopupWindow(IPopupWindow popup) {
        scheduleTask(self -> self.removePopupWindow(popup));
    }

    private void removePopupWindows(Set<? extends IPopupWindow> popupWindows, Predicate<IPopupWindow> condition) {
        popupWindows.removeIf(actionMenu -> {
            if (condition.test(actionMenu)) {
                actionMenu.onRemoved();
                return true;
            }
            return false;
        });
    }
}
