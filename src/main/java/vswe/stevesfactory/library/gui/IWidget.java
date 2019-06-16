package vswe.stevesfactory.library.gui;

import net.minecraft.client.gui.IGuiEventListener;
import net.minecraft.client.gui.IRenderable;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.awt.*;

/**
 * A component of the GUI that has a rendering
 */
@OnlyIn(Dist.CLIENT)
public interface IWidget extends IGuiEventListener, IRenderable {

    /**
     * Local coordinate relative to the parent component
     */
    Point getLocation();

    Dimension getDimensions();

    @Override
    void render(int mouseX, int mouseY, float particleTicks);

    @Nullable
    IWidget getParentWidget();

    IWindow getWindow();

}
