package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureClientData;
import vswe.stevesfactory.ui.manager.editor.Menu;

import java.util.List;

// TODO
public class AnalogRedstoneMenu<P extends IProcedure & IProcedureClientData> extends Menu<P> {

    @Override
    public String getHeadingText() {
        return I18n.format("gui.sfm.Menu.RedstoneTrigger.Analog");
    }

    @Override
    public List<String> populateErrors(List<String> errors) {
        return errors;
    }
}
