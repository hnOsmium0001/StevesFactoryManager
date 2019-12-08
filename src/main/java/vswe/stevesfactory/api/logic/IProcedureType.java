package vswe.stevesfactory.api.logic;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;

public interface IProcedureType<P extends IProcedure> extends IForgeRegistryEntry<IProcedureType<?>> {

    /**
     * Create a blank procedure and a command graph with its root set as the returned procedure.
     *
     * @implSpec Created instances must return the same registry name ({@link #getRegistryName()}) as this factory object.
     */
    P createInstance();

    /**
     * Retrieve an invalid procedure object (that can be turned to valid later by adding to a procedure graph) from the given tag.
     *
     * @return A new, invalid procedure object corresponding to the data
     */
    P retrieveInstance(CompoundNBT tag);

    /**
     * Get the icon used for component selection panel
     * <p>
     * This should only be called on the client side for GUI purposes only.
     */
    ResourceLocation getIcon();

    String getTranslationKey();

    default String getLocalizedName() {
        return I18n.format(getTranslationKey());
    }

    default String getLocalizedName(Object... args) {
        return I18n.format(getTranslationKey(), args);
    }
}
