package vswe.stevesfactory.api.logic;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;

public interface IProcedure {

    ResourceLocation getRegistryName();

    IProcedure[] previous();

    IProcedure[] next();

    /**
     * Execute this procedure, and return the next procedure the control flow should go to.
     *
     * @return The next procedure that should be executed such that {@link #next()} contains {@code p}. {@code null} if the program should
     * terminate here.
     */
    @Nullable
    IProcedure execute(IExecutionContext context);

    /**
     * Serialize the procedure into a retrievable NBT format. This NBT compound should be able to be put into any factory with the same
     * registry name as this, and results in an equivalent procedure object using {@link IProcedureType#retrieveInstance(CompoundNBT)}.
     *
     * @implSpec The resulting NBT must contain an entry with the key "{@code ID}", associated with the registry name of the procedure.
     */
    CompoundNBT serialize();

    void linkTo(int outputIndex, IProcedure next, int nextInputIndex);

    void unlink(int outputIndex);

    void onLinkTo(IProcedure previous, int inputIndex);

    void onUnlink(IProcedure previous);
}
