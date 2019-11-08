package vswe.stevesfactory.network;

import com.google.common.base.Preconditions;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import vswe.stevesfactory.blocks.FactoryManagerTileEntity;
import vswe.stevesfactory.blocks.ItemIntakeTileEntity;
import vswe.stevesfactory.ui.intake.ItemIntakeGUI;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;

import java.util.Objects;
import java.util.function.Supplier;

public final class PacketOpenGUI {

    private static final int FACTORY_MANAGER = 0;
    private static final int ITEM_INTAKE = 1;

    public static void openFactoryManager(ServerPlayerEntity client, DimensionType dimension, BlockPos controllerPos, CompoundNBT tag) {
        NetworkHandler.sendTo(client, new PacketOpenGUI(FACTORY_MANAGER, controllerPos, tag));
    }

    public static void openItemIntake(ServerPlayerEntity client, DimensionType dimension, BlockPos intakePos, CompoundNBT tag) {
        NetworkHandler.sendTo(client, new PacketOpenGUI(ITEM_INTAKE, intakePos, tag));
    }

    public static void encode(PacketOpenGUI msg, PacketBuffer buf) {
        buf.writeInt(msg.type);
        buf.writeBlockPos(msg.pos);
        buf.writeCompoundTag(msg.tag);
    }

    public static PacketOpenGUI decode(PacketBuffer buf) {
        return new PacketOpenGUI(buf.readInt(), buf.readBlockPos(), buf.readCompoundTag());
    }

    public static void handle(PacketOpenGUI msg, Supplier<NetworkEvent.Context> ctx) {
        Preconditions.checkState(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> handleClient(msg, ctx));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(PacketOpenGUI msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            World world = Minecraft.getInstance().world;
            switch (msg.type) {
                case FACTORY_MANAGER: {
                    FactoryManagerTileEntity controller = Objects.requireNonNull((FactoryManagerTileEntity) world.getTileEntity(msg.pos));
                    controller.read(msg.tag);
                    Minecraft.getInstance().displayGuiScreen(new FactoryManagerGUI(controller));
                    break;
                }
                case ITEM_INTAKE: {
                    ItemIntakeTileEntity intake = Objects.requireNonNull((ItemIntakeTileEntity) world.getTileEntity(msg.pos));
                    intake.read(msg.tag);
                    Minecraft.getInstance().displayGuiScreen(new ItemIntakeGUI(intake));
                    break;
                }
            }

        });
        ctx.get().setPacketHandled(true);
    }

    private int type;
    private BlockPos pos;
    private CompoundNBT tag;

    public PacketOpenGUI(int type, BlockPos pos, CompoundNBT tag) {
        this.type = type;
        this.pos = pos;
        this.tag = tag;
    }
}
