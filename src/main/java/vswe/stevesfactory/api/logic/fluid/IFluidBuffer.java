package vswe.stevesfactory.api.logic.fluid;

import net.minecraftforge.fluids.FluidStack;

public interface IFluidBuffer {

    FluidStack getStack();

    void setStack(FluidStack stack);

    int getUsed();

    void setUsed(int used);

    /**
     * Implementation may assume the parameter is always less than or equal to the stack size of {@link #getStack()}.
     */
    void use(int amount);

    void put(int amount);

    void cleanup();
}
