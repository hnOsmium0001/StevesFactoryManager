package vswe.stevesfactory.logic.fluid;

import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;

public class SingleFluidBufferElement {

    public final IFluidHandler handler;

    public FluidStack stack;
    public int used = 0;

    public SingleFluidBufferElement(FluidStack stack, IFluidHandler handler) {
        this.handler = handler;
        this.stack = stack;
    }
}
