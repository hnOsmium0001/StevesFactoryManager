package vswe.stevesfactory.blocks.manager.components;

import com.google.common.collect.ImmutableList;
import vswe.stevesfactory.blocks.manager.FactoryManagerGUI;
import vswe.stevesfactory.library.gui.core.*;
import vswe.stevesfactory.library.gui.debug.RenderEventDispatcher;

import java.util.Collection;
import java.util.List;

public class EditorPanel extends DynamicWidthWidget<IWidget> {

    public EditorPanel(FactoryManagerGUI.TopLevelWidget parent, IWindow window) {
        super(parent, window, WidthOccupierType.MAX_WIDTH);
    }

    // TODO
    @Override
    public List<IWidget> getChildren() {
        return ImmutableList.of();
    }

    @Override
    public ILayout<IWidget> getLayout() {
        return null;
    }

    @Override
    public IContainer<IWidget> addChildren(IWidget widget) {
        return null;
    }

    @Override
    public IContainer<IWidget> addChildren(Collection<IWidget> widgets) {
        return null;
    }

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        RenderEventDispatcher.onPreRender(this, mouseX, mouseY);
        RenderEventDispatcher.onPostRender(this, mouseX, mouseY);
    }

}
