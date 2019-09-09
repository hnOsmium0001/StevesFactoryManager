package vswe.stevesfactory.logic.procedure;

import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureClientData;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.editor.Menu;

public class ItemFilterMenu<P extends IProcedure & IProcedureClientData & IItemFilterTarget> extends Menu<P> {

    public ItemFilterMenu() {

    }

    @Override
    public void onLinkFlowComponent(FlowComponent<P> flowComponent) {
        super.onLinkFlowComponent(flowComponent);
    }

    @Override
    protected void updateData() {
    }

    @Override
    public String getHeadingText() {
        return I18n.format("gui.sfm.Menus.ItemFilter");
    }

    @Override
    public void renderContents(int mouseX, int mouseY, float particleTicks) {
    }
}
