package vswe.stevesfactory.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.network.NetworkEvent;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.utils.IOHelper;

import java.util.*;
import java.util.function.Supplier;

public final class PacketTransferLinkedInventories {

    public static void encode(PacketTransferLinkedInventories msg, PacketBuffer buf) {
        buf.writeBlockPos(msg.controllerPos);
        IOHelper.writeBlockPoses(msg.linkedInventories, buf);
    }

    public static PacketTransferLinkedInventories decode(PacketBuffer buf) {
        BlockPos controllerPos = buf.readBlockPos();
        List<BlockPos> linkedInventories = IOHelper.readBlockPosesSized(buf, ArrayList::new);
        return new PacketTransferLinkedInventories(controllerPos, linkedInventories);
    }

    public static void handle(PacketTransferLinkedInventories msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            handle(msg);
            ctx.setPacketHandled(true);
        });
    }

    static void handle(PacketTransferLinkedInventories msg) {
        World world = Minecraft.getInstance().world;
        INetworkController controller = Objects.requireNonNull((INetworkController) world.getTileEntity(msg.controllerPos));
        controller.removeAllLinks();
        controller.addLinks(msg.linkedInventories);
    }

    private BlockPos controllerPos;
    private Collection<BlockPos> linkedInventories;

    public PacketTransferLinkedInventories(BlockPos controllerPos, Collection<BlockPos> poses) {
        this.controllerPos = controllerPos;
        this.linkedInventories = poses;
    }
}
