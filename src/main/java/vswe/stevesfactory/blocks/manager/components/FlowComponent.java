package vswe.stevesfactory.blocks.manager.components;

import com.google.common.collect.ImmutableList;
import vswe.stevesfactory.library.gui.TextureWrapper;
import vswe.stevesfactory.library.gui.core.*;
import vswe.stevesfactory.library.gui.layout.misc.PositionalLayout;
import vswe.stevesfactory.library.gui.widget.AbstractWidget;
import vswe.stevesfactory.library.gui.widget.mixin.ContainerWidgetMixin;

import java.util.Collection;
import java.util.List;

public abstract class FlowComponent extends AbstractWidget implements IContainer<IWidget>, ContainerWidgetMixin<IWidget> {

    public FlowComponent(TextureWrapper textureWrapper) {
        this(textureWrapper.getPortionWidth(), textureWrapper.getPortionHeight());
    }

    public FlowComponent(int width, int height) {
        super(width, height);
    }

    @Override
    public List<IWidget> getChildren() {
        // TODO
        return ImmutableList.of();
    }

    @Override
    public ILayout<IWidget> getLayout() {
        return PositionalLayout.INSTANCE;
    }

    @Override
    public final IContainer<IWidget> addChildren(IWidget widget) {
        throw new UnsupportedOperationException();
    }

    @Override
    public final IContainer<IWidget> addChildren(Collection<IWidget> widgets) {
        throw new UnsupportedOperationException();
    }

    public abstract TextureWrapper getBackgroundTexture();

    @Override
    public void render(int mouseX, int mouseY, float particleTicks) {
        getBackgroundTexture().draw(getAbsoluteX(), getAbsoluteY());
    }

}
