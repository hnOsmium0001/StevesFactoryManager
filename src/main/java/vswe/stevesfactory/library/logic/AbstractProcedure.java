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
import vswe.stevesfactory.logic.graph.CommandGraph;

public abstract class AbstractProcedure implements IProcedure {

    private IProcedureType<?> type;

    private INetworkController controller;
    private IProcedure[] previousNodes;
    private IProcedure[] nextNodes;

    private transient CommandGraph tree;

    public AbstractProcedure(IProcedureType<?> type, INetworkController controller, int possibleParents, int possibleChildren) {
        this.type = type;
        this.setController(controller);
        this.previousNodes = new IProcedure[possibleParents];
        this.nextNodes = new IProcedure[possibleChildren];
    }

    public INetworkController getController() {
        Preconditions.checkArgument(!controller.isRemoved(), "The controller object is invalid!");
        return controller;
    }

    public void setController(INetworkController controller) {
        Preconditions.checkArgument(!controller.isRemoved(), "The controller object is invalid!");
        this.controller = controller;
    }

    @Override
    public IProcedure[] next() {
        return nextNodes;
    }

    @Override
    public IProcedure[] previous() {
        return previousNodes;
    }

    @Override
    public void linkTo(int outputIndex, IProcedure next, int nextInputIndex) {
        unlink(outputIndex);

//        tree.childCount++;

        nextNodes[outputIndex] = next;
        next.onLinkTo(this, nextInputIndex);
    }

    @Override
    public void unlink(int outputIndex) {
        IProcedure oldChild = nextNodes[outputIndex];
        if (oldChild != null) {
            oldChild.onUnlink(this);
//            tree.childCount--;
        }
        nextNodes[outputIndex] = null;
    }

    @Override
    public void onLinkTo(IProcedure previous, int inputIndex) {
        previousNodes[inputIndex] = previous;
    }

    @Override
    public void onUnlink(IProcedure previous) {
        for (int i = 0; i < previousNodes.length; i++) {
            IProcedure previousNode = previousNodes[i];
            if (previousNode == previous) {
                previousNodes[i] = null;
            }
        }
    }

    public void remove() {
        for (int i = 0; i < nextNodes.length; i++) {
            unlink(i);
        }

    }

    public IProcedureType<?> getType() {
        return type;
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

    @Override
    public ResourceLocation getRegistryName() {
        return type.getRegistryName();
    }

    public static IProcedureType<?> readType(CompoundNBT tag) {
        ResourceLocation id = new ResourceLocation(tag.getString("ID"));
        return SFMAPI.getProceduresRegistry().getValue(id);
    }

    public static BlockPos readControllerPos(CompoundNBT tag) {
        return NBTUtil.readBlockPos(tag.getCompound("ControllerPos"));
    }

    public static DimensionType raedDimensionType(CompoundNBT tag) {
        return DimensionType.byName(new ResourceLocation(tag.getString("Dimension")));
    }

    public static INetworkController readController(CompoundNBT tag) {
        return readController(raedDimensionType(tag), readControllerPos(tag));
    }

    public static INetworkController readController(DimensionType dimensionType, BlockPos pos) {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server != null) {
            return (INetworkController) server.getWorld(dimensionType).getTileEntity(pos);
        }
        return (INetworkController) Minecraft.getInstance().world.getTileEntity(pos);
    }
}
