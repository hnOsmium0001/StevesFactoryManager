package vswe.stevesfactory.api.logic;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;

import javax.annotation.Nullable;
import java.util.List;

public interface IProcedure {

    ResourceLocation getRegistryName();

    List<? extends IProcedure> nexts();

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
     * registry name as this, and results in an equivalent procedure object using {@link IProcedureFactory#retrieveInstance(CompoundNBT)}.
     *
     * @implSpec The resulting NBT must contain an entry with the key "{@code ID}", associated with the registry name of the procedure.
     * @implNote The default implementation of this method has the ID entry written. Unless child implementations have a special need,
     * reusing this method stub is sufficient.
     */
    default CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("ID", getRegistryName().toString());
        return tag;
    }
}
