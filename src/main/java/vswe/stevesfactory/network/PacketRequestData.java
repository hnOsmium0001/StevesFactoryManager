package vswe.stevesfactory.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import vswe.stevesfactory.api.network.INetworkController;

import java.util.Objects;
import java.util.function.Supplier;

public final class PacketRequestData {

    private static PacketBuffer buffer() {
        return new PacketBuffer(Unpooled.buffer());
    }

    public static final int LINKED_INVENTORIES = 0;

    public static void requestLinkedInventories(DimensionType dimension, BlockPos controllerPos) {
        PacketBuffer extra = buffer();
        extra.writeResourceLocation(Objects.requireNonNull(dimension.getRegistryName()));
        extra.writeBlockPos(controllerPos);
        NetworkHandler.sendToServer(new PacketRequestData(LINKED_INVENTORIES, extra));
    }

    public static void encode(PacketRequestData msg, PacketBuffer buf) {
        buf.writeInt(msg.id);
        buf.writeInt(msg.extra.readableBytes());
        buf.writeBytes(msg.extra);
    }

    public static PacketRequestData decode(PacketBuffer buf) {
        int id = buf.readInt();
        int size = buf.readInt();
        PacketBuffer extra = new PacketBuffer(buf.readBytes(size));
        return new PacketRequestData(id, extra);
    }

    public static void handle(PacketRequestData msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            switch (msg.id) {
                case LINKED_INVENTORIES: {
                    DimensionType dimension = Objects.requireNonNull(DimensionType.byName(msg.extra.readResourceLocation()));
                    BlockPos controllerPos = msg.extra.readBlockPos();
                    World world = ServerLifecycleHooks.getCurrentServer().getWorld(dimension);
                    INetworkController controller = Objects.requireNonNull((INetworkController) world.getTileEntity(controllerPos));

                    NetworkHandler.sendTo(ctx.get().getSender(), new PacketTransferLinkables(controllerPos, controller.getLinkedInventories()));
                    break;
                }
                default: throw new IllegalArgumentException("Invalid request id " + msg.id);
            }
        });
    }

    private int id;
    private PacketBuffer extra;

    public PacketRequestData(int id, PacketBuffer extra) {
        this.id = id;
        this.extra = extra;
    }
}
