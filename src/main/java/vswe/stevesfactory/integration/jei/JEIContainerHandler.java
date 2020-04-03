package vswe.stevesfactory.integration.jei;

import com.google.common.collect.ImmutableList;
import mezz.jei.api.MethodsReturnNonnullByDefault;
import mezz.jei.api.gui.handlers.IGuiClickableArea;
import mezz.jei.api.gui.handlers.IGuiContainerHandler;
import net.minecraft.client.renderer.Rectangle2d;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;

import javax.annotation.ParametersAreNonnullByDefault;
import java.awt.*;
import java.util.Collection;
import java.util.List;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class JEIContainerHandler<S extends WidgetScreen<?>> implements IGuiContainerHandler<S> {

    @Override
    public List<Rectangle2d> getGuiExtraAreas(S screen) {
        Dimension d = screen.getPrimaryWindow().getBorder();
        Point p = screen.getPrimaryWindow().getPosition();
        return ImmutableList.of(new Rectangle2d(p.x, p.y, d.width, d.height));
    }

    @Override
    public Collection<IGuiClickableArea> getGuiClickableAreas(S screen) {
        return ImmutableList.of();
    }
}
