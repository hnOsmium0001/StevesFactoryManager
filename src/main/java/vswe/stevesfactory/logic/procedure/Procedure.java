package vswe.stevesfactory.logic.procedure;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.registries.ForgeRegistryEntry;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureType;
import vswe.stevesfactory.api.network.INetworkController;

import java.util.ArrayList;
import java.util.List;

public abstract class Procedure extends ForgeRegistryEntry<IProcedureType<?>> implements IProcedure {

    private INetworkController controller;
    private List<? extends IProcedure> nexts = new ArrayList<>();

    public Procedure(INetworkController controller, int possibleChildren) {
        this.controller = controller;
        this.nexts = new ArrayList<>(possibleChildren);
    }

    public INetworkController getController() {
        if (controller.isRemoved()) {
            throw new IllegalStateException("The controller is already removed");
        }
        return controller;
    }

    @Override
    public List<? extends IProcedure> nexts() {
        return nexts;
    }

    @Override
    public CompoundNBT serialize() {
        return null;
    }
}
