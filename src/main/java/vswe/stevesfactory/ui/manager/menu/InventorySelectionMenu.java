package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;
import net.minecraft.util.math.BlockPos;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureClientData;
import vswe.stevesfactory.api.network.INetworkController;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.box.WrappingList;
import vswe.stevesfactory.logic.procedure.IInventoryTarget;
import vswe.stevesfactory.ui.manager.FactoryManagerGUI;
import vswe.stevesfactory.ui.manager.editor.Menu;

import java.util.Objects;

public class InventorySelectionMenu<P extends IInventoryTarget & IProcedure & IProcedureClientData> extends Menu<P> {

    public InventorySelectionMenu() {
        WrappingList<BlockIcon> list = new WrappingList<>("");
        list.setLocation(4, HEADING_BOX.getPortionHeight() + 4);
        list.setDimensions(getWidth() - 4 * 2, getContentHeight() - 4 * 2);
        list.getContentArea().y += list.getSearchBoxHeight() + 2;
        list.setItemsPerRow(5);
        list.setVisibleRows(2);
        list.getScrollUpArrow().setLocation(100, 24);
        list.alignArrows();
        FactoryManagerGUI gui = (FactoryManagerGUI) WidgetScreen.getCurrentScreen();
        INetworkController controller = Objects.requireNonNull((INetworkController) Minecraft.getInstance().world.getTileEntity(gui.controllerPos));
        for (BlockPos pos : controller.getLinkedInventories()) {
            BlockState state = Minecraft.getInstance().world.getBlockState(pos);
            BlockIcon icon = new BlockIcon();
            icon.setBlockState(state);
            list.addElement(icon);
        }

        addChildren(list);
    }

    @Override
    public void expand() {
        super.expand();
    }

    @Override
    public String getHeadingText() {
        return I18n.format("gui.sfm.Menu.InventorySelection");
    }

    @Override
    public void renderContents(int mouseX, int mouseY, float particleTicks) {

    }
}
