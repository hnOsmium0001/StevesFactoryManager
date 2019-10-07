package vswe.stevesfactory.api.logic;

import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import vswe.stevesfactory.api.network.INetworkController;

public interface IProcedureType<P extends IProcedure> extends IForgeRegistryEntry<IProcedureType<?>> {

    /**
     * Create a blank procedure and a command graph with its root set as the returned procedure. This should <b>not</b> modify anything in
     * the controller, such as adding a command graph.
     *
     * @implSpec Created instances must return the same registry name ({@link #getRegistryName()}) as this factory object.
     */
    P createInstance(INetworkController controller);

    /**
     * Retrieve an invalid procedure object (that can be turned to valid later by connecting it to a command graph) from the given tag.
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
