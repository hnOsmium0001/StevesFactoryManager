package vswe.stevesfactory.library.logic;

import com.google.common.base.Preconditions;
import net.minecraft.client.resources.I18n;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.registries.ForgeRegistryEntry;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureType;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;
import java.util.function.BiFunction;
import java.util.function.Function;

public class SimpleProcedureType<P extends IProcedure> extends ForgeRegistryEntry<IProcedureType<?>> implements IProcedureType<P> {

    private final Function<INetworkController, P> constructor;
    private final Function<CompoundNBT, P> retriever;
    private final ResourceLocation icon;

    private Function<P, FlowComponent> flowComponentFactory = this::createWidgetDefault;

    public SimpleProcedureType(BiFunction<IProcedureType<P>, INetworkController, P> constructor, Function<CompoundNBT, P> retriever, ResourceLocation icon) {
        this.constructor = c -> constructor.apply(this, c);
        this.icon = icon;
        this.retriever = retriever;
    }

    public SimpleProcedureType(Function<INetworkController, P> constructor, Function<CompoundNBT, P> retriever, ResourceLocation icon) {
        this.constructor = constructor;
        this.icon = icon;
        this.retriever = retriever;
    }

    @Override
    public P createInstance(INetworkController controller) {
        return constructor.apply(controller);
    }

    @Override
    public P retrieveInstance(CompoundNBT tag) {
        Preconditions.checkArgument(getRegistryNameNonnull().toString().equals(tag.getString("ID")));
        return retriever.apply(tag);
    }

    @Override
    public ResourceLocation getIcon() {
        return icon;
    }

    @Override
    public FlowComponent createWidget(P procedure) {
        return flowComponentFactory.apply(procedure);
    }

    public Function<P, FlowComponent> getFlowComponentFactory() {
        return flowComponentFactory;
    }

    public void setFlowComponentFactory(@Nullable Function<P, FlowComponent> flowComponentFactory) {
        if (flowComponentFactory == null) {
            resetFlowComponentFactory();
        } else {
            this.flowComponentFactory = flowComponentFactory;
        }
    }

    public void resetFlowComponentFactory() {
        this.flowComponentFactory = this::createWidgetDefault;
    }

    public final FlowComponent<P> createWidgetDefault(P procedure) {
        return createWidgetDefault(procedure, 1, 1);
    }

    public final FlowComponent<P> createWidgetDefault(P procedure, int inputNodes, int outputNodes) {
        FlowComponent<P> component = new FlowComponent<P>(procedure, inputNodes, outputNodes) {};
        component.setName(I18n.format("logic.sfm." + getRegistryNameNonnull().getPath()));
        return component;
    }

    @Nonnull
    public ResourceLocation getRegistryNameNonnull() {
        ResourceLocation id = super.getRegistryName();
        Preconditions.checkState(id != null, "Procedure type " + this + " does not have a registry name!");
        return id;
    }
}