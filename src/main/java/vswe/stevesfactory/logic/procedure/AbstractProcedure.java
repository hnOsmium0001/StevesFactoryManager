package vswe.stevesfactory.logic.procedure;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.nbt.NBTUtil;
import net.minecraftforge.registries.ForgeRegistryEntry;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureType;
import vswe.stevesfactory.api.network.INetworkController;

import java.util.Objects;

public abstract class AbstractProcedure extends ForgeRegistryEntry<IProcedureType<?>> implements IProcedure {

    private INetworkController controller;
    private IProcedure[] nexts;

    public AbstractProcedure(INetworkController controller, int possibleChildren) {
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
        tag.putString("ID", Objects.requireNonNull(getRegistryName()).toString());
        tag.put("ControllerPos", NBTUtil.writeBlockPos(controller.getPos()));
        return tag;
    }
}
