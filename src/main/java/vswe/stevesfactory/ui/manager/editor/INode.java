package vswe.stevesfactory.ui.manager.editor;

import vswe.stevesfactory.library.gui.widget.IWidget;

import javax.annotation.Nullable;

public interface INode extends IWidget {

    @Nullable
    INode getPrevious();

    @Nullable
    INode getNext();

    /**
     * Set link to the next node (returned by {@link #getNext()}) to the parameter. This should <b>only</b> change the
     * node's own state, nothing else needs and should be changed. This method should <b>not</b> be called manfully, use
     * methods in {@link vswe.stevesfactory.ui.manager.editor.ConnectionsPanel} to manipulate connections safely.
     */
    void connectTo(INode next);

    /**
     * Set link to the previous node (returned by {@link #getPrevious()}) to the parameter. This should <b>only</b>
     * change the node's own state, nothing else needs and should be changed. This method should <b>not</b> be called
     * manfully, use methods in {@link vswe.stevesfactory.ui.manager.editor.ConnectionsPanel} to manipulate connections
     * safely.
     */
    void connectFrom(INode previous);

    /**
     * Remove the link to the next node. This should <b>only</b> change the node's own state, nothing else needs and
     * should be changed. This method should <b>not</b> be called manfully, use methods in {@link
     * vswe.stevesfactory.ui.manager.editor.ConnectionsPanel} to manipulate connections safely.
     */
    void disconnectNext();

    /**
     * Remove the link to the previous node. This should <b>only</b> change the node's own state, nothing else needs and
     * * should be changed. This method should <b>not</b> be called manfully, use methods in {@link
     * vswe.stevesfactory.ui.manager.editor.ConnectionsPanel} to * manipulate connections safely.
     */
    void disconnectPrevious();
}
