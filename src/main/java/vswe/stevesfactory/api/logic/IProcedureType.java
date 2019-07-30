package vswe.stevesfactory.api.logic;

import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.IForgeRegistryEntry;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.blocks.manager.components.FlowComponent;

public interface IProcedureType<P extends IProcedure> extends IForgeRegistryEntry<IProcedureType<?>> {

    /**
     * Create a blank procedure object.
     *
     * @implSpec Created instances must return the same registry name ({@link #getRegistryName()}) as this factory object.
     */
    P createInstance(INetworkController controller);

    P retrieveInstance(CompoundNBT tag);

    /**
     * Get the icon used for component selection panel
     * <p>
     * This should only be called on the client side for GUI purposes only.
     */
    ResourceLocation getIcon();

    /**
     * Create a flow component object that represents the procedure instance such that we can retrieve a identical instance (i.e. {@link
     * #equals(Object)} returns {@code true}) based the flow component created.
     * <p>
     * This should only be called on the client side for GUI purposes only.
     */
    FlowComponent createWidget(P procedure);
}
