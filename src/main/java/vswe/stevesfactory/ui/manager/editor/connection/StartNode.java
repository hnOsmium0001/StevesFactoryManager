package vswe.stevesfactory.ui.manager.editor.connection;

import com.mojang.datafixers.util.Either;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.widget.AbstractIconButton;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

import static vswe.stevesfactory.ui.manager.editor.ConnectionsPanel.REGULAR_HEIGHT;
import static vswe.stevesfactory.ui.manager.editor.ConnectionsPanel.REGULAR_WIDTH;

public final class StartNode extends AbstractIconButton implements INode {

    public static final TextureWrapper OUTPUT_NORMAL = TextureWrapper.ofFlowComponent(18, 45, REGULAR_WIDTH, REGULAR_HEIGHT);
    public static final TextureWrapper OUTPUT_HOVERED = OUTPUT_NORMAL.toRight(1);

    private INode next;
    private EndNode end;
    private int index;

    public StartNode(int index) {
        super(0, 0, REGULAR_WIDTH, REGULAR_HEIGHT);
        this.index = index;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        FactoryManagerGUI.getActiveGUI().getTopLevel().connectionsPanel.onTerminalNodeClick(Either.left(this), button);
        return true;
    }

    @Override
    public void connectTo(INode next) {
        this.next = next;
        if (next instanceof EndNode) {
            this.end = (EndNode) next;
        }
    }

    @Override
    public void connectFrom(INode previous) {
    }

    @Override
    public void disconnectNext() {
        if (next == end) {
            this.end = null;
        }
        this.next = null;
    }

    @Override
    public void disconnectPrevious() {
    }

    @Nullable
    @Override
    public INode getPrevious() {
        return null;
    }

    @Nullable
    @Override
    public INode getNext() {
        return next;
    }

    @Override
    public TextureWrapper getTextureNormal() {
        return OUTPUT_NORMAL;
    }

    @Override
    public TextureWrapper getTextureHovered() {
        return OUTPUT_HOVERED;
    }

    public boolean isConnected() {
        // Two links are handled simultaneously, only one check is needed
        return end != null;
    }

    public int getIndex() {
        return index;
    }

    public EndNode getEnd() {
        return end;
    }

    @Nonnull
    @Override
    public ConnectionNodes<?> getParentWidget() {
        return (ConnectionNodes<?>) Objects.requireNonNull(super.getParentWidget());
    }

    public FlowComponent<?> getFlowComponent() {
        return (FlowComponent<?>) getParentWidget().getParentWidget();
    }

    public IProcedure getProcedure() {
        return getFlowComponent().getProcedure();
    }
}
