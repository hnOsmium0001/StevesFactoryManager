package vswe.stevesfactory.ui.manager.menu;

import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IClientDataStorage;
import vswe.stevesfactory.library.gui.layout.properties.HorizontalAlignment;
import vswe.stevesfactory.library.gui.layout.properties.Side;
import vswe.stevesfactory.library.gui.widget.RadioButton;
import vswe.stevesfactory.library.gui.widget.RadioController;
import vswe.stevesfactory.library.gui.widget.box.ScrollArrow;
import vswe.stevesfactory.library.gui.widget.box.WrappingList;
import vswe.stevesfactory.logic.FilterType;
import vswe.stevesfactory.logic.item.ItemTraitsFilter;
import vswe.stevesfactory.logic.procedure.IItemFilterTarget;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;

import java.util.List;
import java.util.function.Supplier;

public class ItemTraitsFilterMenu<P extends IProcedure & IClientDataStorage & IItemFilterTarget> extends MultiLayerMenu<P> {

    private static final Supplier<Integer> FILTER_SLOTS = () -> 20;

    private final int id;
    private final String name;

    private final RadioButton whitelist, blacklist;
    private final WrappingList<FilterSlot> slots;
    private SettingsEditor settings;

    public ItemTraitsFilterMenu(int id) {
        this(id, I18n.format("gui.sfm.Menu.ItemFilter.Traits"));
    }

    public ItemTraitsFilterMenu(int id, String name) {
        this.id = id;
        this.name = name;

        RadioController filterTypeController = new RadioController();
        whitelist = new RadioButton(filterTypeController);
        blacklist = new RadioButton(filterTypeController);
        int y = HEADING_BOX.getPortionHeight() + 4;
        whitelist.setLocation(4, y);
        whitelist.setLabel(I18n.format("gui.sfm.whitelist"));
        blacklist.setLocation(getWidth() / 2, y);
        blacklist.setLabel(I18n.format("gui.sfm.blacklist"));

        slots = new WrappingList<>(false);
        slots.setLocation(4, whitelist.getYBottom() + 4);
        slots.setItemsPerRow(5);
        slots.setVisibleRows(2);
        slots.setDimensions(slots.getContentArea().width, getContentHeight() - whitelist.getHeight() - 4 * 2);
        slots.getScrollUpArrow().setLocation(100, 0);
        slots.alignArrows();

        OpenSettingsButton openSettings = new OpenSettingsButton(0, 0);
        ScrollArrow arrow = slots.getScrollDownArrow();
        int ax = slots.getX() + arrow.getX();
        int ay = slots.getY() + arrow.getY();
        openSettings.alignTo(ax, ay, ax + arrow.getWidth(), ay + arrow.getHeight(), Side.BOTTOM, HorizontalAlignment.CENTER);
        openSettings.moveY(8);

        addChildren(whitelist);
        addChildren(blacklist);
        addChildren(slots);
        addChildren(openSettings);
    }

    @Override
    public void onLinkFlowComponent(FlowComponent<P> flowComponent) {
        super.onLinkFlowComponent(flowComponent);
        ItemTraitsFilter filter = getLinkedFilter();
        for (int i = 0; i < FILTER_SLOTS.get(); i++) {
            ItemStack stack;
            if (i < filter.getItems().size()) {
                stack = filter.getItems().get(i);
            } else {
                stack = ItemStack.EMPTY;
                filter.getItems().add(ItemStack.EMPTY);
            }
            slots.addElement(new FilterSlot(filter, i, stack));
        }

        switch (filter.type) {
            case WHITELIST:
                whitelist.check(true);
                break;
            case BLACKLIST:
                blacklist.check(true);
                break;
        }
        whitelist.onChecked = () -> filter.type = FilterType.WHITELIST;
        blacklist.onChecked = () -> filter.type = FilterType.BLACKLIST;

        settings = new SettingsEditor(this);
        settings.addOption(filter.isMatchingAmount(), "gui.sfm.Menu.ItemFilter.Traits.MatchAmount").onStateChange = filter::setMatchingAmount;
        settings.addOption(filter.isMatchingDamage(), "gui.sfm.Menu.ItemFilter.Traits.MatchDamage").onStateChange = filter::setMatchingDamage;
        settings.addOption(filter.isMatchingTag(), "gui.sfm.Menu.ItemFilter.Traits.MatchTag").onStateChange = filter::setMatchingTag;
    }

    @Override
    public SettingsEditor getEditor() {
        return settings;
    }

    public ItemTraitsFilter getLinkedFilter() {
        return (ItemTraitsFilter) getLinkedProcedure().getFilter(id);
    }

    @Override
    public String getHeadingText() {
        return name;
    }

    @Override
    public List<String> populateErrors(List<String> errors) {
        return errors;
    }
}
