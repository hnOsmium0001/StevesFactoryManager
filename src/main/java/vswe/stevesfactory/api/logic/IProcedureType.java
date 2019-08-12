package vswe.stevesfactory.api.logic;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;

public interface IProcedureType<P extends IProcedure> extends IForgeRegistryEntry<IProcedureType<?>> {

    /**
     * Create a blank procedure object.
     *
     * @implSpec Created instances must return the same registry name ({@link #getRegistryName()}) as this factory object.
     */
    P createInstance(INetworkController controller);

    P retrieveInstance(CommandGraph graph, CompoundNBT tag);

    /**
     * Get the icon used for component selection panel
     * <p>
     * This should only be called on the client side for GUI purposes only.
     */
    ResourceLocation getIcon();
}
