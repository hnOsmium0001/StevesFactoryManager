package vswe.stevesfactory.logic.procedure;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import vswe.stevesfactory.api.logic.IExecutionContext;
import vswe.stevesfactory.api.logic.IProcedureType;
import vswe.stevesfactory.logic.AbstractProcedure;
import vswe.stevesfactory.logic.ModProcedures;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.menu.FunctionNameMenu;

public class FunctionHatProcedure extends AbstractProcedure implements IFunctionHat {

    private String funcName = "";

    public static FunctionHatProcedure functionHat() {
        return new FunctionHatProcedure(ModProcedures.functionHat);
    }

    public FunctionHatProcedure(IProcedureType<?> type) {
        super(type, 0, 1);
    }

    @Override
    public void execute(IExecutionContext context) {
        pushFrame(context, 0);
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public FlowComponent<FunctionHatProcedure> createFlowComponent() {
        FlowComponent<FunctionHatProcedure> f = FlowComponent.of(this);
        f.addMenu(new FunctionNameMenu());
        return f;
    }

    @Override
    public CompoundNBT serialize() {
        CompoundNBT tag = super.serialize();
        tag.putString("FunctionName", funcName);
        return tag;
    }

    @Override
    public void deserialize(CompoundNBT tag) {
        super.deserialize(tag);
        funcName = tag.getString("FunctionName");
    }

    @Override
    public String getFunctionName() {
        return funcName;
    }

    public void setFunctionName(String funcName) {
        this.funcName = funcName;
    }
}
