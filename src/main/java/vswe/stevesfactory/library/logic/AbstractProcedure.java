package vswe.stevesfactory.library.logic;

import com.google.common.base.Preconditions;
import net.minecraft.client.Minecraft;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.fml.server.ServerLifecycleHooks;
import vswe.stevesfactory.api.SFMAPI;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureType;
import vswe.stevesfactory.api.network.INetworkController;

public abstract class AbstractProcedure implements IProcedure {

    private IProcedureType<?> type;
    private INetworkController controller;
    private IProcedure[] nexts;

    public AbstractProcedure(IProcedureType<?> type, INetworkController controller, int possibleChildren) {
        this.type = type;
        this.setController(controller);
        this.nexts = new IProcedure[possibleChildren];
    }

    public INetworkController getController() {
        Preconditions.checkArgument(!controller.isRemoved(), "The controller object is invalid!");
        return controller;
    }

    public void setController(INetworkController controller) {
        Preconditions.checkArgument(!controller.isRemoved());
        this.controller = controller;
    }

    @Override
    public IProcedure[] nexts() {
        return nexts;
    }

    /**
     * {@inheritDoc}
     *
     * @implNote The default implementation of this method has the ID entry written. Unless child implementations have a special need,
     * reusing this method stub is sufficient.
     */
    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = new CompoundNBT();
        tag.putString("ID", getRegistryName().toString());
        tag.putString("DimensionType", controller.getDimension().toString());
        tag.put("ControllerPos", NBTUtil.writeBlockPos(controller.getPos()));
        return tag;
    }

    public static IProcedureType<?> getType(CompoundNBT tag) {
        ResourceLocation id = new ResourceLocation(tag.getString("ID"));
        return SFMAPI.getProceduresRegistry().getValue(id);
    }

    public static BlockPos getControllerPos(CompoundNBT tag) {
        return NBTUtil.readBlockPos(tag.getCompound("ControllerPos"));
    }

    public static DimensionType getDimensionType(CompoundNBT tag) {
        return DimensionType.byName(new ResourceLocation(tag.getString("Dimension")));
    }

    public static INetworkController getController(CompoundNBT tag) {
        return getController(getDimensionType(tag), getControllerPos(tag));
    }

    public static INetworkController getController(DimensionType dimensionType, BlockPos pos) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            return (INetworkController) server.getWorld(dimensionType).getTileEntity(pos);
        }
        return (INetworkController) Minecraft.getInstance().world.getTileEntity(pos);
    }

    @Override
    public ResourceLocation getRegistryName() {
        return type.getRegistryName();
    }
}
