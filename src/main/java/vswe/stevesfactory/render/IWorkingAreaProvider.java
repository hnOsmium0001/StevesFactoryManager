package vswe.stevesfactory.render;

import net.minecraft.util.math.AxisAlignedBB;

public interface IWorkingAreaProvider {

    AxisAlignedBB getWorkingArea();

    boolean isRendering();
}
