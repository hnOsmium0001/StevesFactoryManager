package vswe.stevesfactory.logic;

import com.google.common.base.Preconditions;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.util.ResourceLocation;
import vswe.stevesfactory.api.StevesFactoryManagerAPI;
import vswe.stevesfactory.api.logic.*;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractProcedure implements IProcedure, IClientDataStorage {

    private IProcedureType<?> type;

    private transient Connection[] successors;
    private transient Connection[] predecessors;

    // Client data
    private int componentX;
    private int componentY;
    private String name;
    private String group = "";

    public AbstractProcedure(IProcedureType<?> type) {
        this(type, 1, 1);
    }

    public AbstractProcedure(IProcedureType<?> type, int possibleParents, int possibleChildren) {
        this.type = type;
        this.successors = new Connection[possibleChildren];
        this.predecessors = new Connection[possibleParents];
    }

    @Override
    public boolean isValid() {
        return successors != null && predecessors != null;
    }

    @Override
    public void invalidate() {
        if (!this.isValid()) {
            return;
        }

        onPreInvalidate();

        for (Connection predecessor : predecessors) {
            if (predecessor != null) {
                predecessor.remove();
            }
        }
        for (Connection successor : successors) {
            if (successor != null) {
                successor.remove();
            }
        }
        successors = null;
        predecessors = null;

        onPostInvalidate();
    }

    protected void onPreInvalidate() {
    }

    protected void onPostInvalidate() {
    }

    @Override
    public Connection[] successors() {
        return successors;
    }

    @Override
    public Connection[] predecessors() {
        return predecessors;
    }

    @Override
    public void setInputConnection(@Nonnull Connection connection, int index) {
        predecessors[index] = connection;
    }

    @Override
    public void setOutputConnection(@Nonnull Connection connection, int index) {
        successors[index] = connection;
    }

    @Override
    public Connection removeInputConnection(int index) {
        Connection prev = predecessors[index];
        predecessors[index] = null;
        return prev;
    }

    @Override
    public Connection removeOutputConnection(int index) {
        Connection prev = successors[index];
        successors[index] = null;
        return prev;
    }

    @Override
    public IProcedureType<?> getType() {
        return type;
    }

    @Override
    public int getComponentX() {
        return componentX;
    }

    @Override
    public void setComponentX(int componentX) {
        this.componentX = componentX;
    }

    @Override
    public int getComponentY() {
        return componentY;
    }

    @Override
    public void setComponentY(int componentY) {
        this.componentY = componentY;
    }

    @Override
    public String getName() {
        if (name == null) {
            name = type.getLocalizedName();
        }
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String getGroup() {
        return group;
    }

    @Override
    public void setGroup(String group) {
        this.group = group;
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
        tag.putInt("CompX", componentX);
        tag.putInt("CompY", componentY);
        tag.putString("Name", getName());
        tag.putString("Group", getGroup());
        return tag;
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        Preconditions.checkArgument(readType(tag) == type);
        componentX = tag.getInt("CompX");
        componentY = tag.getInt("CompY");
        name = tag.getString("Name");
        group = tag.getString("Group");
    }

    @Override
    public ResourceLocation getRegistryName() {
        return type.getRegistryName();
    }

    protected final void pushFrame(IExecutionContext context, @Nullable Connection connection) {
        if (connection != null) {
            context.push(connection.getDestination());
        }
    }

    protected final void pushFrame(IExecutionContext context, int outputIndex) {
        pushFrame(context, successors[outputIndex]);
    }

    public static IProcedureType<?> readType(CompoundNBT tag) {
        ResourceLocation id = new ResourceLocation(tag.getString("ID"));
        return StevesFactoryManagerAPI.getProceduresRegistry().getValue(id);
    }
}
