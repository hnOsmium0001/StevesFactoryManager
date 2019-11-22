package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.logic.procedure.SignUpdaterProcedure;
import vswe.stevesfactory.ui.manager.editor.Menu;

import java.util.List;

// TODO
public class SignUpdaterLinesMenu extends Menu<SignUpdaterProcedure> {

    @Override
    public String getHeadingText() {
        return I18n.format("gui.sfm.Menu.SignUpdater.Lines");
    }

    @Override
    public List<String> populateErrors(List<String> errors) {
        return errors;
    }
}
