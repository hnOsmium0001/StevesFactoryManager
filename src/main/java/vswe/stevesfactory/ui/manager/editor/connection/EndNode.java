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

public final class EndNode extends AbstractIconButton implements INode {

    public static final TextureWrapper INPUT_NORMAL = TextureWrapper.ofFlowComponent(18, 51, REGULAR_WIDTH, REGULAR_HEIGHT);
    public static final TextureWrapper INPUT_HOVERED = INPUT_NORMAL.toRight(1);

    private INode previous;
    private StartNode start;
    private int index;

    public EndNode(int index) {
        super(0, 0, REGULAR_WIDTH, REGULAR_HEIGHT);
        this.index = index;
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        FactoryManagerGUI.getActiveGUI().getTopLevel().connectionsPanel.onTerminalNodeClick(Either.right(this), button);
        return true;
    }

    @Override
    public void connectTo(INode next) {
    }

    @Override
    public void connectFrom(INode previous) {
        if (previous instanceof StartNode) {
            this.start = (StartNode) previous;
        }
        this.previous = previous;
    }

    @Override
    public void disconnectNext() {
    }

    @Override
    public void disconnectPrevious() {
        if (previous == start) {
            this.start = null;
        }
        this.previous = null;
    }

    @Nullable
    @Override
    public INode getPrevious() {
        return previous;
    }

    @Nullable
    @Override
    public INode getNext() {
        return null;
    }

    @Override
    public TextureWrapper getTextureNormal() {
        return INPUT_NORMAL;
    }

    @Override
    public TextureWrapper getTextureHovered() {
        return INPUT_HOVERED;
    }

    public boolean isConnected() {
        // Two links are handled simultaneously, only one check is needed
        return start != null;
    }

    public int getIndex() {
        return index;
    }

    public StartNode getStart() {
        return start;
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
