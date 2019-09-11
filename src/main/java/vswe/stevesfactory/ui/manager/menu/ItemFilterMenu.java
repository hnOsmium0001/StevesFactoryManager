package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureClientData;
import vswe.stevesfactory.library.gui.IWidget;
import vswe.stevesfactory.library.gui.widget.RadioButton;
import vswe.stevesfactory.library.gui.widget.RadioController;
import vswe.stevesfactory.library.gui.widget.box.WrappingList;
import vswe.stevesfactory.logic.item.GroupItemFilter;
import vswe.stevesfactory.logic.procedure.IItemFilterTarget;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.editor.Menu;

import javax.annotation.Nullable;
import java.util.List;
import java.util.function.Supplier;

public class ItemFilterMenu<P extends IProcedure & IProcedureClientData & IItemFilterTarget> extends Menu<P> {

    private static final Supplier<Integer> FILTER_SLOTS = () -> 20;

    private final int id;

    private final WrappingList<FilterSlot> slots;
    private IWidget openEditor;

    public ItemFilterMenu(int id) {
        this.id = id;

        RadioController filterTypeController = new RadioController();
        RadioButton whitelist = new RadioButton(filterTypeController);
        RadioButton blacklist = new RadioButton(filterTypeController);
        int y = HEADING_BOX.getPortionHeight() + 4;
        whitelist.setLocation(4, y);
        whitelist.setLabel(I18n.format("gui.sfm.whitelist"));
        blacklist.setLocation(getWidth() / 2, y);
        blacklist.setLabel(I18n.format("gui.sfm.blacklist"));

        slots = new WrappingList<>(false);
        slots.setLocation(4, y + 2);
        slots.setDimensions(getWidth() - 4 * 2 - slots.getScrollUpArrow().getWidth(), getContentHeight() - whitelist.getHeight() - 2 - 4 * 2);
        slots.getContentArea().y += slots.getSearchBoxHeight() + 2;
        slots.setItemsPerRow(5);
        slots.setVisibleRows(2);
        slots.getScrollUpArrow().setLocation(100, 24);
        slots.alignArrows();

        addChildren(whitelist);
        addChildren(blacklist);
        addChildren(slots);
    }

    @Override
    public void onLinkFlowComponent(FlowComponent<P> flowComponent) {
        super.onLinkFlowComponent(flowComponent);
        GroupItemFilter filter = getLinkedProcedure().getFilters(id);
        for (Integer i = 0; i < FILTER_SLOTS.get(); i++) {
            ItemStack stack;
            if (i < filter.getItems().size()) {
                stack = filter.getItems().get(i);
            } else {
                stack = ItemStack.EMPTY;
                filter.getItems().add(ItemStack.EMPTY);
            }
            FilterSlot slot = new FilterSlot(stack);
            slots.addElement(slot);
        }
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

    public IWidget getOpenEditor() {
        return openEditor;
    }

    public void openEditor(@Nullable IWidget editor) {
        this.openEditor = editor;
        List<IWidget> children = getChildren();
        if (editor != null) {
            children.add(editor);
        } else {
            children.remove(children.size() - 1);
        }
    }
}
