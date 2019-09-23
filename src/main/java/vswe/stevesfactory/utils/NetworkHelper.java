package vswe.stevesfactory.utils;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.energy.CapabilityEnergy;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import vswe.stevesfactory.api.StevesFactoryManagerAPI;
import vswe.stevesfactory.api.logic.*;
import vswe.stevesfactory.api.network.*;
import vswe.stevesfactory.api.network.IConnectable.LinkType;

import javax.annotation.Nullable;
import java.util.*;

public final class NetworkHelper {

    private NetworkHelper() {
    }

    public static LinkType getLinkType(@Nullable TileEntity tile) {
        if (tile instanceof IConnectable) {
            return ((IConnectable) tile).getConnectionType();
        }
        return LinkType.DEFAULT;
    }

    public static IProcedure recreateProcedureAndAdd(INetworkController controller, CompoundNBT tag) {
        IProcedureType<?> p = findTypeFor(tag);
        return p.retrieveInstance(controller, tag);
    }

    public static IProcedure retrieveProcedure(INetworkController controller, CompoundNBT tag) {
        // Note that this graph is invalid before we set its root
        CommandGraph graph = new CommandGraph(controller);
        IProcedure procedure = retrieveProcedure(graph, tag);
        graph.setRoot(procedure);
        return procedure;
    }

    public static IProcedure retrieveProcedure(CommandGraph graph, CompoundNBT tag) {
        IProcedureType<?> p = findTypeFor(tag);
        IProcedure procedure = p.retrieveInstance(graph, tag);
        procedure.setGraph(graph);
        return procedure;
    }

    public static IProcedureType<?> findTypeFor(CompoundNBT tag) {
        ResourceLocation id = new ResourceLocation(tag.getString("ID"));
        return findTypeFor(id);
    }

    public static IProcedureType<?> findTypeFor(ResourceLocation id) {
        IProcedureType<?> p = StevesFactoryManagerAPI.getProceduresRegistry().getValue(id);
        // Not using checkNotNull here because technically the above method returns null is a registry problem
        Preconditions.checkArgument(p != null, "Unable to find a procedure registered as " + id + "!");
        return p;
    }
}
