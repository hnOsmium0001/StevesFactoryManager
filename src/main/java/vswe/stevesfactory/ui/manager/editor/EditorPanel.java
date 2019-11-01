package vswe.stevesfactory.ui.manager.editor;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import mcp.MethodsReturnNonnullByDefault;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.util.math.BlockPos;
import vswe.stevesfactory.Config;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.library.gui.RenderingHelper;
import vswe.stevesfactory.library.gui.ScissorTest;
import vswe.stevesfactory.library.gui.contextmenu.CallbackEntry;
import vswe.stevesfactory.library.gui.contextmenu.ContextMenu;
import vswe.stevesfactory.library.gui.debug.ITextReceiver;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.mixin.RelocatableContainerMixin;
import vswe.stevesfactory.library.gui.window.Dialog;
import vswe.stevesfactory.ui.manager.DynamicWidthWidget;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;
import vswe.stevesfactory.ui.manager.editor.ConnectionNodes.Node;
import vswe.stevesfactory.ui.manager.editor.ConnectionNodes.OutputNode;
import vswe.stevesfactory.ui.manager.DynamicWidthWidget;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;
import vswe.stevesfactory.ui.manager.UserPreferencesPanel;
import vswe.stevesfactory.ui.manager.editor.ControlFlow.Node;
import vswe.stevesfactory.ui.manager.editor.ControlFlow.OutputNode;
import vswe.stevesfactory.utils.NetworkHelper;

import javax.annotation.ParametersAreNonnullByDefault;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Stream;

import static org.lwjgl.glfw.GLFW.*;
import static vswe.stevesfactory.library.gui.RenderingHelper.fontRenderer;

public final class EditorPanel extends DynamicWidthWidget<FlowComponent<?>> implements RelocatableContainerMixin<FlowComponent<?>> {

    /**
     * This is a tree set (ordered set) because handling z-index of the flow components need things to be sorted.
     */
    private TreeSet<FlowComponent<?>> children = new TreeSet<>();
    private Collection<FlowComponent<?>> childrenView = new DescendingTreeSetBackedUnmodifiableCollection<>(children);
    private int nextZIndex = 0;

    // Node connection state
    private Node selectedNode;
    private String currentGroup = "";

    private OffsetText xOffset;
    private OffsetText yOffset;

    public EditorPanel() {
        super(WidthOccupierType.MAX_WIDTH);
        readProcedures();

        xOffset = new OffsetText(I18n.format("gui.sfm.FactoryManager.Editor.XOff"), 0, 0);
        xOffset.setParentWidget(this);
        yOffset = new OffsetText(I18n.format("gui.sfm.FactoryManager.Editor.YOff"), 0, 0);
        yOffset.setParentWidget(this);
    }

    public void readProcedures() {
        BlockPos controllerPos = ((FactoryManagerGUI) WidgetScreen.getCurrent()).getController().getPosition();
        INetworkController controller = Objects.requireNonNull((INetworkController) Minecraft.getInstance().world.getTileEntity(controllerPos));

        Map<IProcedure, FlowComponent<?>> m = new HashMap<>();
        for (IProcedure procedure : controller.getPGraph().iterableValidAll()) {
            FlowComponent<?> f = procedure.createFlowComponent();
            m.put(procedure, f);
            addChildren(f);
        }

        for (FlowComponent<?> child : children) {
            child.readConnections(m);
        }
    }

    public TreeSet<FlowComponent<?>> getFlowComponents() {
        return children;
    }

    @Override
    public Collection<FlowComponent<?>> getChildren() {
        return childrenView;
    }

    @Override
    public void reflow() {
    }

    @Override
    public EditorPanel addChildren(FlowComponent<?> widget) {
        widget.setParentWidget(this);
        widget.setZIndex(nextZIndex());
        children.add(widget);
        return this;
    }

    @Override
    public EditorPanel addChildren(Collection<FlowComponent<?>> widgets) {
        for (FlowComponent<?> widget : widgets) {
            widget.setParentWidget(this);
            widget.setZIndex(nextZIndex());
        }
        children.addAll(widgets);
        return this;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        ScissorTest test = ScissorTest.scaled(getAbsoluteX(), getAbsoluteY(), getWidth(), getHeight());
        GlStateManager.pushMatrix();
        {
            if (selectedNode != null) {
                Node.drawConnectionLine(selectedNode.getCenterX() + xOffset.get(), selectedNode.getCenterY() + yOffset.get(), mouseX, mouseY);
            }

            GlStateManager.translatef(xOffset.get(), yOffset.get(), 0F);
            RenderingHelper.translate(xOffset.get(), yOffset.get());
            // Widgets are translated on render, which means player inputs will go at the translated positions
            // we need to translate the inputs back to the original position for logic handling, since the data position isn't changed at all
            int translatedX = mouseX - xOffset.get();
            int translatedY = mouseY - yOffset.get();

            // If this is put into the rendering logic of the nodes, the connection line will be above of of the flow components if
            // they are in a certain order.
            // The ideal way to solve this is to use depth testing, however it is such a huge amount of work to change all the GUI code written,
            // TODO use depth test instead of painter's algorithm
            for (FlowComponent<?> child : children) {
                if (!currentGroup.equals(child.getGroup())) {
                    continue;
                }
                for (Node node : child.getOutputNodes().getChildren()) {
                    ((OutputNode) node).renderConnectionLine(translatedX, translatedY);
                }
            }

            // Iterate in ascending order for rendering as a special case
            for (FlowComponent<?> child : children) {
                if (!currentGroup.equals(child.getGroup())) {
                    continue;
                }
                child.render(translatedX, translatedY, particleTicks);
            }

            RenderingHelper.clearTranslation();
        }
        GlStateManager.popMatrix();
        test.destroy();

        xOffset.render(mouseX, mouseY, particleTicks);
        yOffset.render(mouseX, mouseY, particleTicks);

        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        double translatedX = mouseX - xOffset.get();
        double translatedY = mouseY - yOffset.get();

        // Cancel node selection
        if (selectedNode != null && button == GLFW_MOUSE_BUTTON_RIGHT) {
            selectedNode = null;
            return true;
        }

        // All other events will be iterated in descending order
        for (FlowComponent<?> child : getChildren()) {
            if (!currentGroup.equals(child.getGroup())) {
                continue;
            }
            if (child.mouseClicked(translatedX, translatedY, button)) {
                raiseComponentToTop(child);
                return true;
            }
        }

        if (xOffset.isInside(mouseX, mouseY)) {
            return xOffset.mouseClicked(mouseX, mouseY, button);
        }
        if (yOffset.isInside(mouseX, mouseY)) {
            return yOffset.mouseClicked(mouseX, mouseY, button);
        }
        if (isInside(mouseX, mouseY)) {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                getWindow().setFocusedWidget(this);
            } else if (button == GLFW_MOUSE_BUTTON_RIGHT) {
                openActionMenu();
            }
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (xOffset.isInside(mouseX, mouseY)) {
            return xOffset.mouseReleased(mouseX, mouseY, button);
        }
        if (yOffset.isInside(mouseX, mouseY)) {
            return yOffset.mouseReleased(mouseX, mouseY, button);
        }

        double translatedX = mouseX - xOffset.get();
        double translatedY = mouseY - yOffset.get();
        for (FlowComponent<?> child : getChildren()) {
            if (!currentGroup.equals(child.getGroup())) {
                continue;
            }
            if (child.mouseReleased(translatedX, translatedY, button)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (isFocused()) {
            xOffset.add((int) Math.round(deltaX));
            yOffset.add((int) Math.round(deltaY));
        }
        if (xOffset.isInside(mouseX, mouseY)) {
            return xOffset.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }
        if (yOffset.isInside(mouseX, mouseY)) {
            return yOffset.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
        }

        for (FlowComponent<?> child : getChildren()) {
            if (!currentGroup.equals(child.getGroup())) {
                continue;
            }
            if (child.mouseDragged(mouseX - xOffset.get(), mouseY - yOffset.get(), button, deltaX, deltaY)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (xOffset.isInside(mouseX, mouseY)) {
            return xOffset.mouseScrolled(mouseX, mouseY, scroll);
        }
        if (yOffset.isInside(mouseX, mouseY)) {
            return yOffset.mouseScrolled(mouseX, mouseY, scroll);
        }

        double translatedX = mouseX - xOffset.get();
        double translatedY = mouseY - yOffset.get();
        for (FlowComponent<?> child : getChildren()) {
            if (!currentGroup.equals(child.getGroup())) {
                continue;
            }
            if (child.mouseScrolled(translatedX, translatedY, scroll)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void mouseMoved(double mouseX, double mouseY) {
        if (xOffset.isInside(mouseX, mouseY)) {
            xOffset.mouseMoved(mouseX, mouseY);
        }
        if (yOffset.isInside(mouseX, mouseY)) {
            yOffset.mouseMoved(mouseX, mouseY);
        }

        for (FlowComponent<?> child : getChildren()) {
            if (!currentGroup.equals(child.getGroup())) {
                continue;
            }
            child.mouseMoved(mouseX - xOffset.get(), mouseY - yOffset.get());
        }
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        for (FlowComponent<?> child : getChildren()) {
            if (!currentGroup.equals(child.getGroup())) {
                continue;
            }
            if (child.keyPressed(keyCode, scanCode, modifiers)) {
                return true;
            }
        }

        int offset = Screen.hasShiftDown() ? Config.CLIENT.acceleratedEditorMoveSpeed.get() : Config.CLIENT.defaultEditorMoveSpeed.get();
        switch (keyCode) {
            case GLFW_KEY_UP:
                yOffset.subtract(offset);
                break;
            case GLFW_KEY_DOWN:
                yOffset.add(offset);
                break;
            case GLFW_KEY_LEFT:
                xOffset.subtract(offset);
                break;
            case GLFW_KEY_RIGHT:
                xOffset.add(offset);
                break;
        }
        return false;
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        for (FlowComponent<?> child : getChildren()) {
            if (!currentGroup.equals(child.getGroup())) {
                continue;
            }
            if (child.keyReleased(keyCode, scanCode, modifiers)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean charTyped(char charTyped, int keyCode) {
        for (FlowComponent<?> child : getChildren()) {
            if (!currentGroup.equals(child.getGroup())) {
                continue;
            }
            if (child.charTyped(charTyped, keyCode)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void update(float particleTicks) {
        for (FlowComponent<?> child : getChildren()) {
            if (!currentGroup.equals(child.getGroup())) {
                continue;
            }
            child.update(particleTicks);
        }
    }

    private void openActionMenu() {
        ContextMenu contextMenu = ContextMenu.atCursor(ImmutableList.of(
                new CallbackEntry(FactoryManagerGUI.PASTE_ICON, "gui.sfm.FactoryManager.Editor.CtxMenu.Paste", b -> actionPaste()),
                new CallbackEntry(null, "gui.sfm.FactoryManager.Editor.CtxMenu.CleanupProcedures", b -> actionCleanup()),
                new CallbackEntry(null, "gui.sfm.FactoryManager.CtxMenu.ToggleFullscreen", b -> FactoryManagerGUI.getActiveGUI().getPrimaryWindow().toggleFullscreen())
        ));
        WidgetScreen.getCurrent().addPopupWindow(contextMenu);
    }

    private void actionPaste() {
        String json = Minecraft.getInstance().keyboardListener.getClipboardString();
        CompoundNBT tag;
        try {
            tag = JsonToNBT.getTagFromJson(json);
        } catch (CommandSyntaxException e) {
            Dialog.createDialog("gui.sfm.FactoryManager.Editor.PopupMsg.PasteProcedure.Fail").tryAddSelfToActiveGUI();
            return;
        }

        INetworkController controller = FactoryManagerGUI.getActiveGUI().getController();
        IProcedure procedure = NetworkHelper.retrieveProcedureAndAdd(controller, tag);

        addChildren(procedure.createFlowComponent());
    }

    private void actionCleanup() {
        // TODO implement
    }

    @Override
    public void onRelativePositionChanged() {
        super.onRelativePositionChanged();
        updateOffsets();
    }

    @Override
    public void onParentPositionChanged() {
        super.onParentPositionChanged();
        updateOffsets();
    }

    private void updateOffsets() {
        xOffset.onParentPositionChanged();
        yOffset.onParentPositionChanged();
    }

    @Override
    public void onAfterReflow() {
        int statusX = getWidth() - 4;
        int fontHeight = fontRenderer().FONT_HEIGHT;
        int yStatusY = getHeight() - 4 - fontHeight;
        xOffset.rightX = statusX;
        xOffset.setY(yStatusY - 4 - fontHeight);
        xOffset.update();
        yOffset.rightX = statusX;
        yOffset.setY(yStatusY);
        yOffset.update();
    }

    public void removeFlowComponent(FlowComponent<?> flowComponent) {
        children.remove(flowComponent);
    }

    private void raiseComponentToTop(FlowComponent<?> target) {
        // Move the flow component to top by setting its z-index to the maximum z-index ever given out
        target.setZIndex(nextZIndex());
        updateChild(target);
    }

    private int nextZIndex() {
        return nextZIndex++;
    }

    private int getLastDistributedZIndex() {
        return nextZIndex - 1;
    }

    private void updateChild(FlowComponent<?> child) {
        children.remove(child);
        children.add(child);
    }

    public void startConnection(Node source) {
        selectedNode = source;
    }

    public boolean tryFinishConnection(Node target) {
        if (selectedNode != null && selectedNode.shouldConnect(target) && target.shouldConnect(selectedNode)) {
            target.connect(selectedNode);
            selectedNode = null;
            return true;
        }
        return false;
    }

    public void saveAll() {
        for (FlowComponent<?> flowComponent : getChildren()) {
            flowComponent.save();
        }
    }

    @Override
    public void provideInformation(ITextReceiver receiver) {
        super.provideInformation(receiver);
        receiver.line("XOff=" + xOffset.get());
        receiver.line("YOff=" + yOffset.get());
    }

    public String getCurrentGroup() {
        return currentGroup;
    }

    public void setCurrentGroup(String currentGroup) {
        this.currentGroup = currentGroup;
    }

    @MethodsReturnNonnullByDefault
    @ParametersAreNonnullByDefault
    private static class DescendingTreeSetBackedUnmodifiableCollection<E> extends AbstractCollection<E> {

        private final TreeSet<E> s;

        public DescendingTreeSetBackedUnmodifiableCollection(TreeSet<E> s) {
            this.s = s;
        }

        public int size() {
            return this.s.size();
        }

        public boolean isEmpty() {
            return this.s.isEmpty();
        }

        public boolean contains(Object var1) {
            return this.s.contains(var1);
        }

        public Object[] toArray() {
            return this.s.toArray();
        }

        public <T> T[] toArray(T[] a) {
            return this.s.toArray(a);
        }

        public String toString() {
            return this.s.toString();
        }

        @Override
        public Iterator<E> iterator() {
            return s.descendingIterator();
        }

        public boolean add(E e) {
            throw new UnsupportedOperationException();
        }

        public boolean remove(Object o) {
            throw new UnsupportedOperationException();
        }

        public boolean containsAll(Collection<?> c) {
            return this.s.containsAll(c);
        }

        public boolean addAll(Collection<? extends E> c) {
            throw new UnsupportedOperationException();
        }

        public boolean removeAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        public boolean retainAll(Collection<?> c) {
            throw new UnsupportedOperationException();
        }

        public void clear() {
            throw new UnsupportedOperationException();
        }

        public void forEach(Consumer<? super E> c) {
            this.s.forEach(c);
        }

        public boolean removeIf(Predicate<? super E> p) {
            throw new UnsupportedOperationException();
        }

        public Spliterator<E> spliterator() {
            return this.s.spliterator();
        }

        public Stream<E> stream() {
            return this.s.stream();
        }

        public Stream<E> parallelStream() {
            return this.s.parallelStream();
        }
    }
}
