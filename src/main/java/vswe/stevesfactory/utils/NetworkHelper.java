package vswe.stevesfactory.utils;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import vswe.stevesfactory.api.StevesFactoryManagerAPI;
import vswe.stevesfactory.api.logic.*;
import vswe.stevesfactory.api.network.*;
import vswe.stevesfactory.api.network.IConnectable.LinkType;

import javax.annotation.Nullable;
import java.util.function.Function;
import java.util.function.Supplier;

public final class NetworkHelper {

    private NetworkHelper() {
    }

    public static LinkType getLinkType(@Nullable TileEntity tile) {
        if (tile instanceof IConnectable) {
            return ((IConnectable) tile).getConnectionType();
        }
        return LinkType.DEFAULT;
    }

    public static <P extends IProcedure> P fabricateInstance(IProcedureType<P> type, INetworkController controller) {
        P procedure = type.createInstance(controller);
        CommandGraph graph = procedure.getGraph();
        controller.addCommandGraph(graph);
        return procedure;
    }

    public static IProcedure retrieveProcedureAndAdd(INetworkController controller, CompoundNBT tag) {
        IProcedure p = retrieveProcedure(new CommandGraph(controller), tag);
        controller.addCommandGraph(p.getGraph());
        return p;
    }

    public static IProcedure retrieveProcedure(CommandGraph graph, CompoundNBT tag) {
        IProcedure procedure = findTypeFor(tag).retrieveInstance(tag);
        procedure.setGraph(graph);
        return procedure;
    }

    public static IProcedureType<?> findTypeFor(CompoundNBT tag) {
        ResourceLocation id = new ResourceLocation(tag.getString("ID"));
        return findTypeFor(id);
    }

    public static IProcedureType<?> findTypeFor(ResourceLocation id) {
        IProcedureType<?> p = StevesFactoryManagerAPI.getProceduresRegistry().getValue(id);
        // Not using checkNotNull here because technically the above method returns null is a registry (game state) problem
        Preconditions.checkArgument(p != null, "Unable to find a procedure registered as " + id + "!");
        return p;
    }

    public static <P extends IProcedure> Function<INetworkController, P> wrapConstructor(Supplier<P> constructor) {
        return controller -> {
            P procedure = constructor.get();
            procedure.setGraph(new CommandGraph(controller, procedure));
            return procedure;
        };
    }

    public static void updateLinksFor(INetworkController controller, ICable cable) {
        for (Capability<?> cap : StevesFactoryManagerAPI.getRecognizableCapabilities()) {
            updateLinksFor(controller, cable, cap);
        }
    }

    public static void updateLinksFor(INetworkController controller, ICable cable, Capability<?> cap) {
        World world = controller.getControllerWorld();
        for (BlockPos neighbor : Utils.neighbors(cable.getPosition())) {
            TileEntity tile = world.getTileEntity(neighbor);
            if (tile == null) {
                continue;
            }
            if (Utils.hasCapabilityAtAll(tile, cap)) {
                controller.addLink(cap, neighbor);
            }
        }
    }
}
