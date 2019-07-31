package vswe.stevesfactory.api.logic;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

public interface IProcedure {

    ResourceLocation getRegistryName();

    IProcedure[] nexts();

    /**
     * Execute this procedure, and return the next procedure the control flow should go to.
     *
     * @return The next procedure that should be executed such that {@link #nexts()} contains {@code p}. {@code null} if the program should
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
}
