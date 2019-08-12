package vswe.stevesfactory.network;

import io.netty.buffer.Unpooled;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import vswe.stevesfactory.api.logic.CommandGraph;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;

import java.util.Collection;
import java.util.Objects;
import java.util.function.Supplier;

// TODO better solution for requesting data
public final class PacketRequest {

    private static PacketBuffer buffer() {
        return new PacketBuffer(Unpooled.buffer());
    }

    public static final int LINKED_INVENTORIES = 0;
    public static final int OPEN_FACTORY_MANAGER_GUI = 1;

    public static void requestLinkedInventories(DimensionType dimension, BlockPos controllerPos) {
        PacketBuffer extra = buffer();
        extra.writeResourceLocation(Objects.requireNonNull(dimension.getRegistryName()));
        extra.writeBlockPos(controllerPos);
        NetworkHandler.sendToServer(new PacketRequest(LINKED_INVENTORIES, extra));
    }

    public static void openFactoryManager(ServerPlayerEntity client, DimensionType dimension, BlockPos controllerPos, Collection<CommandGraph> graphs) {
        PacketBuffer extra = buffer();
        extra.writeBlockPos(controllerPos);
        PacketSyncCommandGraphs.encode(new PacketSyncCommandGraphs(graphs, dimension, controllerPos), extra);
        NetworkHandler.sendTo(client, new PacketRequest(OPEN_FACTORY_MANAGER_GUI, extra));
    }

    public static void encode(PacketRequest msg, PacketBuffer buf) {
        buf.writeVarInt(msg.id);
        buf.writeInt(msg.extra.readableBytes());
        buf.writeBytes(msg.extra);
    }

    public static PacketRequest decode(PacketBuffer buf) {
        int id = buf.readVarInt();
        int size = buf.readInt();
        PacketBuffer extra = new PacketBuffer(buf.readBytes(size));
        return new PacketRequest(id, extra);
    }

    public static void handle(PacketRequest msg, Supplier<NetworkEvent.Context> ctxSupplier) {
        NetworkEvent.Context ctx = ctxSupplier.get();
        ctx.enqueueWork(() -> {
            switch (msg.id) {
                case LINKED_INVENTORIES: {
                    DimensionType dimension = Objects.requireNonNull(DimensionType.byName(msg.extra.readResourceLocation()));
                    BlockPos controllerPos = msg.extra.readBlockPos();
                    World world = ServerLifecycleHooks.getCurrentServer().getWorld(dimension);
                    INetworkController controller = Objects.requireNonNull((INetworkController) world.getTileEntity(controllerPos));

                    NetworkHandler.CHANNEL.reply(new PacketTransferLinkedInventories(controllerPos, controller.getLinkedInventories()), ctx);
                    break;
                }
                case OPEN_FACTORY_MANAGER_GUI: {
                    BlockPos controllerPos = msg.extra.readBlockPos();
                    PacketSyncCommandGraphs.handleBuffer(msg.extra);
                    Minecraft.getInstance().displayGuiScreen(new FactoryManagerGUI(controllerPos));
                    break;
                }
                default: throw new IllegalArgumentException("Invalid request id " + msg.id);
            }
            ctx.setPacketHandled(true);
        });
    }

    private int id;
    private PacketBuffer extra;

    public PacketRequest(int id, PacketBuffer extra) {
        this.id = id;
        this.extra = extra;
    }
}
