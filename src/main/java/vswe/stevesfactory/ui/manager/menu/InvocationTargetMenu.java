package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.logic.procedure.FunctionInvokeProcedure;
import vswe.stevesfactory.ui.manager.editor.Menu;

import java.util.List;

// TODO
public class InvocationTargetMenu extends Menu<FunctionInvokeProcedure> {

    @Override
    public String getHeadingText() {
        return I18n.format("menu.sfm.InvocationTarget");
    }

    @Override
    public List<String> populateErrors(List<String> errors) {
        if (false) {
            errors.add(I18n.format("error.sfm.InvocationTarget.Unspecified"));
        }
        return errors;
    }
}
