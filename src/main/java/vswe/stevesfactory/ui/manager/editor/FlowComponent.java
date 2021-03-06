package vswe.stevesfactory.ui.manager.editor;

import com.google.common.collect.ImmutableList;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.nbt.CompoundNBT;
import vswe.stevesfactory.api.logic.Connection;
import vswe.stevesfactory.api.logic.IClientDataStorage;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.contextmenu.CallbackEntry;
import vswe.stevesfactory.library.gui.contextmenu.ContextMenu;
import vswe.stevesfactory.library.gui.debug.ITextReceiver;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;
import vswe.stevesfactory.library.gui.layout.properties.BoxSizing;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.AbstractContainer;
import vswe.stevesfactory.library.gui.widget.AbstractIconButton;
import vswe.stevesfactory.library.gui.widget.IWidget;
import vswe.stevesfactory.library.gui.widget.TextField;
import vswe.stevesfactory.library.gui.widget.box.LinearList;
import vswe.stevesfactory.library.gui.widget.box.MinimumLinearList;
import vswe.stevesfactory.library.gui.window.Dialog;
import vswe.stevesfactory.ui.manager.tool.group.Grouplist;
import vswe.stevesfactory.utils.NetworkHelper;

import javax.annotation.Nonnull;
import java.awt.*;
import java.util.List;
import java.util.Queue;
import java.util.*;

import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_LEFT;
import static org.lwjgl.glfw.GLFW.GLFW_MOUSE_BUTTON_RIGHT;
import static vswe.stevesfactory.ui.manager.FactoryManagerGUI.*;

public class FlowComponent<P extends IProcedure & IClientDataStorage> extends AbstractContainer<IWidget> implements Comparable<FlowComponent<?>> {

    public enum State {
        COLLAPSED(TextureWrapper.ofFlowComponent(0, 0, 64, 20),
                TextureWrapper.ofFlowComponent(0, 20, 9, 10),
                TextureWrapper.ofFlowComponent(0, 30, 9, 10)),
        EXPANDED(TextureWrapper.ofFlowComponent(64, 0, 124, 152),
                TextureWrapper.ofFlowComponent(9, 20, 9, 10),
                TextureWrapper.ofFlowComponent(9, 30, 9, 10));

        public final TextureWrapper background;
        public final TextureWrapper toggleStateNormal;
        public final TextureWrapper toggleStateHovered;

        State(TextureWrapper background, TextureWrapper toggleStateNormal, TextureWrapper toggleStateHovered) {
            this.background = background;
            this.toggleStateNormal = toggleStateNormal;
            this.toggleStateHovered = toggleStateHovered;
        }

        public int componentWidth() {
            return background.getPortionWidth();
        }

        public int componentHeight() {
            return background.getPortionHeight();
        }
    }

    public static class ToggleStateButton extends AbstractIconButton {

        public ToggleStateButton(FlowComponent<?> parent) {
            super(-1, -1, 9, 10);
            setParentWidget(parent);
        }

        @Override
        public TextureWrapper getTextureNormal() {
            return getParentWidget().getState().toggleStateNormal;
        }

        @Override
        public TextureWrapper getTextureHovered() {
            return getParentWidget().getState().toggleStateHovered;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (button == GLFW_MOUSE_BUTTON_LEFT) {
                getParentWidget().toggleState();
                return true;
            }
            return false;
        }

        @Nonnull
        @Override
        public FlowComponent<?> getParentWidget() {
            return Objects.requireNonNull((FlowComponent<?>) super.getParentWidget());
        }

        @Override
        public BoxSizing getBoxSizing() {
            return BoxSizing.PHANTOM;
        }
    }

    public static class RenameButton extends AbstractIconButton {

        public static final TextureWrapper NORMAL = TextureWrapper.ofFlowComponent(0, 124, 9, 9);
        public static final TextureWrapper HOVERING = NORMAL.toRight(1);

        public RenameButton(FlowComponent<?> parent) {
            super(-1, -1, 9, 9);
            setParentWidget(parent);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isEnabled() && button == GLFW_MOUSE_BUTTON_LEFT) {
                setEnabled(false);
                FlowComponent<?> parent = getParentWidget();
                parent.submitButton.setEnabled(true);
                parent.cancelButton.setEnabled(true);
                parent.nameBox.setEditable(true);
                getWindow().setFocusedWidget(parent.nameBox);
                return true;
            }
            return false;
        }

        @Override
        public TextureWrapper getTextureNormal() {
            return NORMAL;
        }

        @Override
        public TextureWrapper getTextureHovered() {
            return HOVERING;
        }

        @Nonnull
        @Override
        public FlowComponent<?> getParentWidget() {
            return Objects.requireNonNull((FlowComponent<?>) super.getParentWidget());
        }

        @Override
        public BoxSizing getBoxSizing() {
            return BoxSizing.PHANTOM;
        }
    }

    public static class SubmitButton extends AbstractIconButton {

        public static final TextureWrapper NORMAL = TextureWrapper.ofFlowComponent(0, 133, 7, 7);
        public static final TextureWrapper HOVERING = NORMAL.toRight(1);

        public SubmitButton(FlowComponent<?> parent) {
            super(-1, -1, 7, 7);
            setParentWidget(parent);
            setEnabled(false);
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isEnabled() && button == GLFW_MOUSE_BUTTON_LEFT) {
                submit();
                return true;
            }
            return false;
        }

        public void submit() {
            setEnabled(false);
            FlowComponent<?> parent = getParentWidget();
            parent.cancelButton.setEnabled(false);
            parent.renameButton.setEnabled(true);
            parent.nameBox.scrollToFront();
            parent.nameBox.setEditable(false);
            parent.getDataHandler().setName(parent.getName());
            getWindow().changeFocus(parent.nameBox, false);
        }

        @Override
        public TextureWrapper getTextureNormal() {
            return NORMAL;
        }

        @Override
        public TextureWrapper getTextureHovered() {
            return HOVERING;
        }

        @Nonnull
        @Override
        public FlowComponent<?> getParentWidget() {
            return Objects.requireNonNull((FlowComponent<?>) super.getParentWidget());
        }

        @Override
        public BoxSizing getBoxSizing() {
            return BoxSizing.PHANTOM;
        }
    }

    public static class CancelButton extends AbstractIconButton {

        public static final TextureWrapper NORMAL = TextureWrapper.ofFlowComponent(0, 140, 7, 7);
        public static final TextureWrapper HOVERING = NORMAL.toRight(1);

        private String previousName;

        public CancelButton(FlowComponent<?> parent, String previousName) {
            super(-1, -1, 7, 7);
            setParentWidget(parent);
            setEnabled(false);
            this.previousName = previousName;
        }

        @Override
        public boolean mouseClicked(double mouseX, double mouseY, int button) {
            if (isEnabled() && button == GLFW_MOUSE_BUTTON_LEFT) {
                cancel();
                return true;
            }
            return false;
        }

        public void cancel() {
            setEnabled(false);
            FlowComponent<?> parent = getParentWidget();
            parent.submitButton.setEnabled(false);
            parent.renameButton.setEnabled(true);
            parent.setName(previousName);
            parent.nameBox.scrollToFront();
            parent.nameBox.setEditable(false);
            getWindow().changeFocus(parent.nameBox, false);
            previousName = "";
        }

        @Override
        public void setEnabled(boolean enabled) {
            super.setEnabled(enabled);
            if (enabled) {
                this.previousName = getParentWidget().getName();
            }
        }

        @Override
        public TextureWrapper getTextureNormal() {
            return NORMAL;
        }

        @Override
        public TextureWrapper getTextureHovered() {
            return HOVERING;
        }

        @Nonnull
        @Override
        public FlowComponent<?> getParentWidget() {
            return Objects.requireNonNull((FlowComponent<?>) super.getParentWidget());
        }

        @Override
        public BoxSizing getBoxSizing() {
            return BoxSizing.PHANTOM;
        }
    }

    public static <P extends IProcedure & IClientDataStorage> FlowComponent<P> of(P procedure) {
        return new FlowComponent<>(procedure, procedure.predecessors().length, procedure.successors().length);
    }

    private P procedure;

    private final ToggleStateButton toggleStateButton;
    private final RenameButton renameButton;
    private final SubmitButton submitButton;
    private final CancelButton cancelButton;
    private final TextField nameBox;
    private final ConnectionNodes<EndNode> inputNodes;
    private final ConnectionNodes<StartNode> outputNodes;
    private final ErrorIndicator errorIndicator;
    private final MinimumLinearList<Menu<P>> menus;
    // A list that refers to all the widgets above
    private final List<IWidget> children;

    private State state;
    private int zIndex;

    // Temporary data
    private int initialDragLocalX;
    private int initialDragLocalY;

    public FlowComponent(P procedure, int amountInputs, int amountOutputs) {
        super(0, 0, 0, 0);
        String name = procedure.getName();
        this.toggleStateButton = new ToggleStateButton(this);
        this.renameButton = new RenameButton(this);
        this.submitButton = new SubmitButton(this);
        this.cancelButton = new CancelButton(this, name);
        // The cursor looks a bit to short (and cute) with these numbers, might want change them?
        this.nameBox = new TextField(6, 8, 35, 10);
        this.nameBox.setBackgroundStyle(TextField.BackgroundStyle.NONE);
        this.nameBox.setText(name);
        this.nameBox.setTextColor(0xff303030, 0xff303030);
        this.nameBox.setEditable(false);
        this.nameBox.setFontHeight(6);
        this.inputNodes = ConnectionNodes.inputNodes(amountInputs);
        this.outputNodes = ConnectionNodes.outputNodes(amountOutputs);
        this.errorIndicator = ErrorIndicator.error();
        this.menus = new MinimumLinearList<>(120, 130);
        this.menus.setLocation(2, 20);
        this.children = ImmutableList.of(toggleStateButton, renameButton, submitButton, cancelButton, nameBox, inputNodes, outputNodes, errorIndicator, menus);
        this.setLinkedProcedure(procedure);
        this.state = State.COLLAPSED;

        errorIndicator.setLocation(2, 8);
    }

    public TextureWrapper getBackgroundTexture() {
        return state.background;
    }

    public State getState() {
        return state;
    }

    public void toggleState() {
        switch (state) {
            case COLLAPSED:
                expand();
                break;
            case EXPANDED:
                collapse();
                break;
        }
    }

    public void expand() {
        state = State.EXPANDED;
        setDimensions(state.componentWidth(), state.componentHeight());

        nameBox.setWidth(95);
        nameBox.scrollToFront();
        toggleStateButton.setLocation(114, 5);
        renameButton.setLocation(103, 6);
        submitButton.setLocation(105, 3);
        cancelButton.setLocation(105, 11);
        renameButton.setEnabled(true);
        updateMenusEnableState(true);
        reflow();
    }

    public void collapse() {
        state = State.COLLAPSED;
        setDimensions(state.componentWidth(), state.componentHeight());

        nameBox.setWidth(35);
        nameBox.scrollToFront();
        toggleStateButton.setLocation(54, 5);
        renameButton.setLocation(43, 6);
        submitButton.setLocation(45, 3);
        cancelButton.setLocation(45, 11);
        if (isEditing()) {
            cancelButton.cancel();
        }
        renameButton.setEnabled(false);
        updateMenusEnableState(false);
        reflow();
    }

    public boolean isEditing() {
        return submitButton.isEnabled();
    }

    private void updateMenusEnableState(boolean enabled) {
        menus.setEnabled(enabled);
        for (Menu<P> menu : menus.getChildren()) {
            menu.setEnabled(enabled);
        }
    }

    public String getName() {
        return nameBox.getText();
    }

    public void setName(String name) {
        this.nameBox.setText(name);
        this.cancelButton.previousName = getName();
        this.procedure.setName(name);
    }

    @Override
    public List<IWidget> getChildren() {
        return children;
    }

    public LinearList<Menu<P>> getMenusBox() {
        return menus;
    }

    public void collapseAllMenus() {
        for (Menu<P> menu : menus.getChildren()) {
            menu.collapse();
        }
        menus.setScrollDistance(0F);
    }

    public void expandAllMenus() {
        for (Menu<P> menu : menus.getChildren()) {
            menu.expand();
        }
    }

    @Override
    public void reflow() {
        inputNodes.setWidth(getWidth());
        inputNodes.setY(-ConnectionsPanel.REGULAR_HEIGHT);
        inputNodes.reflow();
        outputNodes.setWidth(getWidth());
        outputNodes.setY(getHeight());
        outputNodes.reflow();
        menus.reflow();
    }

    public FlowComponent<P> addMenu(Menu<P> menu) {
        menus.addChildren(menu);
        menu.onLinkFlowComponent(this);
        return this;
    }

    @Override
    public FlowComponent<P> addChildren(IWidget widget) {
        if (widget instanceof Menu) {
            @SuppressWarnings("unchecked") Menu<P> menu = (Menu<P>) widget;
            return addMenu(menu);
        } else {
            throw new IllegalArgumentException("Flow components do not accept new child widgets with type other than Menu");
        }
    }

    @Override
    public FlowComponent<P> addChildren(Collection<IWidget> widgets) {
        for (IWidget widget : widgets) {
            addChildren(widget);
        }
        return this;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);

        GlStateManager.color3f(1F, 1F, 1F);
        getBackgroundTexture().draw(getAbsoluteX(), getAbsoluteY());

        // Renaming state (showing different buttons at different times) is handled inside the widgets' render method
        toggleStateButton.render(mouseX, mouseY, particleTicks);
        renameButton.render(mouseX, mouseY, particleTicks);
        submitButton.render(mouseX, mouseY, particleTicks);
        cancelButton.render(mouseX, mouseY, particleTicks);
        nameBox.render(mouseX, mouseY, particleTicks);
        inputNodes.render(mouseX, mouseY, particleTicks);
        outputNodes.render(mouseX, mouseY, particleTicks);
        errorIndicator.render(mouseX, mouseY, particleTicks);
        menus.render(mouseX, mouseY, particleTicks);

        if (nameBox.isInside(mouseX, mouseY)) {
            WidgetScreen.getCurrent().setHoveringText(getName(), mouseX, mouseY);
        }

        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (super.mouseClicked(mouseX, mouseY, button)) {
            clearDrag();
            return true;
        }
        if (!isInside(mouseX, mouseY)) {
            clearDrag();
            return false;
        }

        getWindow().setFocusedWidget(this);
        if (button == GLFW_MOUSE_BUTTON_LEFT) {
            initialDragLocalX = (int) mouseX - getAbsoluteX();
            initialDragLocalY = (int) mouseY - getAbsoluteY();
        } else {
            clearDrag();
        }
        if (button == GLFW_MOUSE_BUTTON_RIGHT) {
            openContextMenu();
        }
        return true;
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (!isEnabled()) {
            return false;
        }
        if (super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY)) {
            return true;
        }
        if (isDragging()) {
            EditorPanel parent = getParentWidget();
            int x = (int) mouseX - parent.getAbsoluteX() - initialDragLocalX;
            int y = (int) mouseY - parent.getAbsoluteY() - initialDragLocalY;
            setLocation(x, y);
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (!isEnabled()) {
            return false;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scroll) {
        if (!isEnabled()) {
            return false;
        }
        return super.mouseScrolled(mouseX, mouseY, scroll);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (!isEnabled()) {
            return false;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean keyReleased(int keyCode, int scanCode, int modifiers) {
        if (!isEnabled()) {
            return false;
        }
        return super.keyReleased(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean charTyped(char charTyped, int keyCode) {
        if (!isEnabled()) {
            return false;
        }
        return super.charTyped(charTyped, keyCode);
    }

    @Override
    public void update(float particleTicks) {
        super.update(particleTicks);
        // TODO don't do it the brutal force way: use data modification events
        if (Minecraft.getInstance().world.getGameTime() % 10 == 0) {
            repopulateErrors();
        }
    }

    private void clearDrag() {
        initialDragLocalX = -1;
        initialDragLocalY = -1;
    }

    private boolean isDragging() {
        return initialDragLocalX != -1 && initialDragLocalY != -1;
    }

    public void setParentWidget(EditorPanel parent) {
        this.setParentWidget((IWidget) parent);
        collapse();
    }

    private void openContextMenu() {
        ContextMenu contextMenu = ContextMenu.atCursor(ImmutableList.of(
                new CallbackEntry(DELETE_ICON, "gui.sfm.FactoryManager.Editor.CtxMenu.Delete", b -> actionDelete()),
                new CallbackEntry(CUT_ICON, "gui.sfm.FactoryManager.Editor.CtxMenu.Cut", b -> actionCut()),
                new CallbackEntry(COPY_ICON, "gui.sfm.FactoryManager.Editor.CtxMenu.Copy", b -> actionCopy()),
                new CallbackEntry(null, "gui.sfm.FactoryManager.Editor.CtxMenu.ChangeGroup", b -> actionChangeGroup())
        ));
        WidgetScreen.getCurrent().addPopupWindow(contextMenu);
    }

    private void actionDelete() {
        if (Screen.hasShiftDown()) {
            Dialog.createBiSelectionDialog(
                    "gui.sfm.FactoryManager.Editor.Dialog.DeleteAll.ConfirmMsg",
                    "gui.sfm.yes",
                    "gui.sfm.no",
                    b -> removeGraph(this), b -> {
                    }).tryAddSelfToActiveGUI();
        } else {
            remove();
        }
    }

    private void actionCopy() {
        save();
        CompoundNBT tag = procedure.serialize();
        Minecraft.getInstance().keyboardListener.setClipboardString(tag.toString());
    }

    private void actionCut() {
        actionCopy();
        remove();
    }

    private void actionChangeGroup() {
        Grouplist.createSelectGroupDialog(newGroup -> {
            disconnect();
            setGroup(newGroup);
        }, () -> {
        }).tryAddSelfToActiveGUI();
    }

    public void save() {
        for (Menu<?> menu : menus.getChildren()) {
            menu.updateData();
        }
        P proc = getProcedure();
        for (int i = 0; i < proc.successors().length; i++) {
            Connection conn = proc.successors()[i];
            if (conn == null) {
                continue;
            }
            StartNode start = outputNodes.getChildren().get(i);

            conn.getPolylineNodes().clear();
            INode next = start.getNext();
            while (next != null && !next.getType().isTerminal()) {
                INode current = next;
                conn.getPolylineNodes().add(new Point(current.getPosition()));
                next = current.getNext();
            }
        }
    }

    public static void removeGraph(FlowComponent<?> start) {
        Set<FlowComponent<?>> visited = new HashSet<>();
        Queue<FlowComponent<?>> nexts = new ArrayDeque<>();
        nexts.add(start);
        while (!nexts.isEmpty()) {
            FlowComponent<?> node = nexts.remove();
            for (EndNode conn : node.inputNodes.getChildren()) {
                StartNode pair = conn.getStart();
                if (pair == null) {
                    continue;
                }
                FlowComponent<?> prev = pair.getFlowComponent();
                if (visited.contains(prev)) {
                    continue;
                }
                visited.add(prev);
                nexts.add(prev);
            }
            for (StartNode conn : node.outputNodes.getChildren()) {
                EndNode pair = conn.getEnd();
                if (pair == null) {
                    continue;
                }
                FlowComponent<?> next = pair.getFlowComponent();
                if (visited.contains(next)) {
                    continue;
                }
                visited.add(next);
                nexts.add(next);
            }
        }
        for (FlowComponent<?> node : visited) {
            node.remove();
        }
    }

    public void disconnect() {
        // Update GUI connections
        inputNodes.removeAllConnections();
        outputNodes.removeAllConnections();
        // Update procedure graph connections
        NetworkHelper.removeAllConnectionsFor(procedure);
    }

    public void remove() {
        disconnect();
        procedure.invalidate();
        getParentWidget().removeFlowComponent(this);
    }

    public ConnectionNodes<EndNode> getInputNodes() {
        return inputNodes;
    }

    public ConnectionNodes<StartNode> getOutputNodes() {
        return outputNodes;
    }

    @Nonnull
    @Override
    public EditorPanel getParentWidget() {
        return Objects.requireNonNull((EditorPanel) super.getParentWidget());
    }

    public int getZIndex() {
        return zIndex;
    }

    public void setZIndex(int z) {
        this.zIndex = z;
    }

    @Override
    public int compareTo(FlowComponent<?> that) {
        return this.getZIndex() - that.getZIndex();
    }

    public P getProcedure() {
        return procedure;
    }

    public String getGroup() {
        return procedure.getGroup();
    }

    public void setGroup(String group) {
        procedure.setGroup(group);
    }

    public IClientDataStorage getDataHandler() {
        return procedure;
    }

    public void setLinkedProcedure(P procedure) {
        this.procedure = procedure;
        setName(procedure.getName());
        setLocation(procedure.getComponentX(), procedure.getComponentY());
        repopulateErrors();
    }

    private void repopulateErrors() {
        errorIndicator.clearErrors();
        for (Menu<P> menu : menus.getChildren()) {
            errorIndicator.populateErrors(menu);
        }
    }

    public void readConnections(Map<IProcedure, FlowComponent<?>> m) {
        this.inputNodes.readConnections(m, procedure);
        this.outputNodes.readConnections(m, procedure);
    }

    @Override
    public void setLocation(int x, int y) {
        super.setLocation(x, y);
        procedure.setComponentX(x);
        procedure.setComponentY(y);
    }

    @Override
    public void provideInformation(ITextReceiver receiver) {
        super.provideInformation(receiver);
        receiver.line("Z=" + this.getZIndex());
    }
}
