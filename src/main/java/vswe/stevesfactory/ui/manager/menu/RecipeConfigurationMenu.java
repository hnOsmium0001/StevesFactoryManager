package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureClientData;
import vswe.stevesfactory.logic.procedure.IRecipeTarget;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.editor.Menu;

public class RecipeConfigurationMenu<P extends IProcedure & IProcedureClientData & IRecipeTarget> extends Menu<P> {

    public RecipeConfigurationMenu() {
    }

    @Override
    public void onLinkFlowComponent(FlowComponent<P> flowComponent) {
        super.onLinkFlowComponent(flowComponent);
    }

    @Override
    protected void updateData() {
        super.updateData();
    }

    @Override
    public String getHeadingText() {
        return I18n.format("gui.sfm.Menus.RecipeConfiguration");
    }
}
