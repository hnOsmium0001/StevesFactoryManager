package vswe.stevesfactory.ui.manager;

import net.minecraft.entity.player.*;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.network.NetworkHooks;
import vswe.stevesfactory.blocks.FactoryManagerTileEntity;
import vswe.stevesfactory.library.gui.screen.WidgetContainer;
import vswe.stevesfactory.setup.ModContainers;

import java.util.Objects;

public class FactoryManagerContainer extends WidgetContainer {

    public static void openGUI(ServerPlayerEntity client, FactoryManagerTileEntity tile) {
        NetworkHooks.openGui(client, tile, buf -> {
            buf.writeBlockPos(tile.getPos());
            buf.writeCompoundTag(tile.writeCustom(new CompoundNBT()));
        });
    }

    public FactoryManagerTileEntity controller;

    public FactoryManagerContainer(int id, FactoryManagerTileEntity controller) {
        super(ModContainers.factoryManagerContainer, id);
        this.controller = controller;
    }

    public FactoryManagerContainer(int id, PlayerInventory inv, PacketBuffer data) {
        super(ModContainers.factoryManagerContainer, id);
        BlockPos pos = data.readBlockPos();
        controller = (FactoryManagerTileEntity) Objects.requireNonNull(inv.player.world.getTileEntity(pos));
        controller.readCustom(Objects.requireNonNull(data.readCompoundTag()));
    }

    @Override
    public void onContainerClosed(PlayerEntity player) {
        controller.sync();
        super.onContainerClosed(player);
    }
}
