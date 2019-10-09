package vswe.stevesfactory.ui.manager.menu;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.item.ItemStack;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureClientData;
import vswe.stevesfactory.library.gui.RenderingHelper;
import vswe.stevesfactory.library.gui.layout.properties.HorizontalAlignment;
import vswe.stevesfactory.library.gui.layout.properties.Side;
import vswe.stevesfactory.library.gui.screen.WidgetScreen;
import vswe.stevesfactory.library.gui.widget.RadioButton;
import vswe.stevesfactory.library.gui.widget.RadioController;
import vswe.stevesfactory.library.gui.widget.box.ScrollArrow;
import vswe.stevesfactory.library.gui.widget.box.WrappingList;
import vswe.stevesfactory.library.gui.widget.slot.AbstractItemSlot;
import vswe.stevesfactory.library.gui.window.PlayerInventoryWindow;
import vswe.stevesfactory.logic.FilterType;
import vswe.stevesfactory.logic.item.ItemTraitsFilter;
import vswe.stevesfactory.logic.procedure.IItemFilterTarget;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;

import java.util.function.Supplier;

public class ItemTraitsFilterMenu<P extends IProcedure & IProcedureClientData & IItemFilterTarget> extends MultiLayerMenu<P> {

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
        whitelist.translateLabel("gui.sfm.whitelist");
        blacklist.setLocation(getWidth() / 2, y);
        blacklist.translateLabel("gui.sfm.blacklist");

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
        for (Integer i = 0; i < FILTER_SLOTS.get(); i++) {
            ItemStack stack;
            if (i < filter.getItems().size()) {
                stack = filter.getItems().get(i);
            } else {
                stack = ItemStack.EMPTY;
                filter.getItems().add(ItemStack.EMPTY);
            }
            FilterSlot slot = new FilterSlot(filter, stack);
            slot.onClick = () -> {
                AbstractItemSlot[] selected = new AbstractItemSlot[1];
                PlayerInventoryWindow popup = PlayerInventoryWindow.atCursor(in -> new AbstractItemSlot() {
                    private ItemStack representative;

                    @Override
                    public ItemStack getRenderedStack() {
                        return in;
                    }

                    @Override
                    public boolean mouseClicked(double mouseX, double mouseY, int button) {
                        if (isSelected() || in.isEmpty()) {
                            selected[0] = null;
                            slot.stack = ItemStack.EMPTY;
                        } else {
                            selected[0] = this;
                            slot.stack = getRepresentative();
                        }
                        return super.mouseClicked(mouseX, mouseY, button);
                    }

                    @Override
                    protected void renderBase() {
                        super.renderBase();
                        if (isSelected() && !in.isEmpty()) {
                            RenderingHelper.useBlendingGLStates();
                            RenderingHelper.drawRect(getAbsoluteX(), getAbsoluteY(), getAbsoluteXRight(), getAbsoluteYBottom(), 0x66ffff00);
                            GlStateManager.disableBlend();
                            GlStateManager.enableTexture();
                        }
                    }

                    private boolean isSelected() {
                        return selected[0] == this;
                    }

                    private ItemStack getRepresentative() {
                        if (representative == null) {
                            representative = in.copy();
                            representative.setCount(1);
                        }
                        return representative;
                    }
                });
                WidgetScreen.getCurrentScreen().addPopupWindow(popup);
            };
            slots.addElement(slot);
        }

        switch (filter.type) {
            case WHITELIST:
                whitelist.check(true);
                break;
            case BLACKLIST:
                blacklist.check(true);
                break;
        }

        settings = new SettingsEditor(this);
        settings.addOption(filter.isMatchingAmount(), filter::setMatchingAmount, "gui.sfm.Menu.MatchAmount");
        settings.addOption(filter.isMatchingDamage(), filter::setMatchingDamage, "gui.sfm.Menu.MatchDamage");
        settings.addOption(filter.isMatchingTag(), filter::setMatchingTag, "gui.sfm.Menu.MatchTag");
    }

    @Override
    protected void updateData() {
        ItemTraitsFilter filter = getLinkedFilter();
        int i = 0;
        for (FilterSlot slot : slots.getContents()) {
            filter.getItems().set(i, slot.stack);
            i++;
        }

        if (whitelist.isChecked()) {
            filter.type = FilterType.WHITELIST;
        } else {
            filter.type = FilterType.BLACKLIST;
        }
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
}
