package vswe.stevesfactory.utils;

import com.google.common.base.Preconditions;
import com.google.errorprone.annotations.CanIgnoreReturnValue;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.items.CapabilityItemHandler;
import vswe.stevesfactory.api.SFMAPI;
import vswe.stevesfactory.api.logic.IProcedureType;
import vswe.stevesfactory.api.network.*;
import vswe.stevesfactory.api.network.IConnectable.LinkType;
import vswe.stevesfactory.api.logic.IProcedure;

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

    @CanIgnoreReturnValue
    public static LinkingStatus updateLinkType(World world, LinkingStatus linkingStatus) {
        BlockPos center = linkingStatus.getCenter();
        for (Direction direction : Direction.values()) {
            TileEntity tile = world.getTileEntity(center.offset(direction));
            linkingStatus.set(direction, getLinkType(tile));
        }
        return linkingStatus;
    }

    public static boolean shouldLink(@Nullable ICapabilityProvider provider) {
        if (provider == null) {
            return false;
        }
        // TODO registry for capabilities
        return Utils.hasCapabilityAtAll(provider, CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) ||
                Utils.hasCapabilityAtAll(provider, CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY);
    }

    @Nullable
    public static INetworkController getNetworkAt(World world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);
        if (tile instanceof INetworkController) {
            return (INetworkController) tile;
        }
        return null;
    }

    public static List<INetworkController> getNetworksAt(World world, Collection<BlockPos> poses) {
        List<INetworkController> result = new ArrayList<>();
        for (BlockPos pos : poses) {
            INetworkController networkCandidate = getNetworkAt(world, pos);
            if (networkCandidate != null) {
                result.add(networkCandidate);
            }
        }
        return result;
    }

    public static IProcedure retreiveProcedure(CompoundNBT tag) {
        ResourceLocation id = new ResourceLocation(tag.getString("ID"));
        IProcedureType<?> p = SFMAPI.getProceduresRegistry().getValue(id);
        // Not using checkNotNull here because technically the above method returns null is a registry problem
        Preconditions.checkArgument(p != null, "Unable to find a procedure registered as " + id + "!");
        return p.retrieveInstance(tag);
    }
}
