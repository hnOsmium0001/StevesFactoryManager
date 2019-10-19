package vswe.stevesfactory.logic.procedure;

import net.minecraft.client.resources.I18n;
import vswe.stevesfactory.api.logic.IProcedure;
import vswe.stevesfactory.api.logic.IProcedureClientData;
import vswe.stevesfactory.logic.item.*;
import vswe.stevesfactory.ui.manager.editor.FlowComponent;
import vswe.stevesfactory.ui.manager.editor.PropertyManager;
import vswe.stevesfactory.ui.manager.menu.ItemTagFilterMenu;
import vswe.stevesfactory.ui.manager.menu.ItemTraitsFilterMenu;

public interface IItemFilterTarget {

    static <P extends IProcedure & IProcedureClientData & IItemFilterTarget> PropertyManager<IItemFilter, P> createFilterMenu(P procedure, FlowComponent<P> flowComponent, int filterID) {
        PropertyManager<IItemFilter, P> pm = new PropertyManager<>(
                flowComponent,
                () -> procedure.getFilter(filterID),
                filter -> procedure.setFilter(filterID, filter));
        String filterName = I18n.format("gui.sfm.Menu.ItemFilter");
        pm.on(filter -> filter instanceof ItemTraitsFilter)
                .name(I18n.format("gui.sfm.Menu.ItemFilter.Traits"))
                .prop(ItemTraitsFilter::new)
                .then(() -> new ItemTraitsFilterMenu<>(filterID, I18n.format("gui.sfm.Menu.ItemFilter.Traits")));
        pm.on(filter -> filter instanceof ItemTagFilter)
                .name(I18n.format("gui.sfm.Menu.ItemFilter.Tags"))
                .prop(ItemTagFilter::new)
                .then(() -> new ItemTagFilterMenu<>(filterID, I18n.format("gui.sfm.Menu.ItemFilter.Tags")));
        pm.actionCycling();
        pm.setProperty(procedure.getFilter(filterID));
        return pm;
    }

    IItemFilter getFilter(int id);

    void setFilter(int id, IItemFilter filter);

    default IItemFilter getFilter() {
        return getFilter(0);
    }
}
