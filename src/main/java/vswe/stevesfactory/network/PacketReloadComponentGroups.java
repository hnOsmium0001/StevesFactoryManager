package vswe.stevesfactory.network;

import com.google.common.base.Preconditions;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.network.NetworkDirection;
import net.minecraftforge.fml.network.NetworkEvent;
import vswe.stevesfactory.ui.manager.selection.ComponentGroup;

import java.util.function.Supplier;

public final class PacketReloadComponentGroups {

    public static void reload(ServerPlayerEntity client) {
        NetworkHandler.sendTo(client, new PacketReloadComponentGroups(false));
    }

    public static void reset(ServerPlayerEntity client) {
        NetworkHandler.sendTo(client, new PacketReloadComponentGroups(true));
    }

    public static void encode(PacketReloadComponentGroups msg, PacketBuffer buf) {
        buf.writeBoolean(msg.reset);
    }

    public static PacketReloadComponentGroups decode(PacketBuffer buf) {
        boolean reset = buf.readBoolean();
        return new PacketReloadComponentGroups(reset);
    }

    public static void handle(PacketReloadComponentGroups msg, Supplier<NetworkEvent.Context> ctx) {
        Preconditions.checkState(ctx.get().getDirection() == NetworkDirection.PLAY_TO_CLIENT);
        DistExecutor.runWhenOn(Dist.CLIENT, () -> () -> handleClient(msg, ctx));
    }

    @OnlyIn(Dist.CLIENT)
    private static void handleClient(PacketReloadComponentGroups msg, Supplier<NetworkEvent.Context> ctx) {
        ComponentGroup.reload(msg.reset);
        ctx.get().enqueueWork(() -> Minecraft.getInstance().player.sendMessage(new TranslationTextComponent("message.sfm.reload.componentGroups.success")));
        ctx.get().setPacketHandled(true);
    }

    private boolean reset;

    public PacketReloadComponentGroups(boolean reset) {
        this.reset = reset;
    }
}
