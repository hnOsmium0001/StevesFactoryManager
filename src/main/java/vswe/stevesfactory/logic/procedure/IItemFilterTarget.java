package vswe.stevesfactory.logic.procedure;

import vswe.stevesfactory.logic.item.IItemFilter;

// No #markDirty() here because no capabilities are involved in this data target
public interface IItemFilterTarget {

    IItemFilter getFilter(int id);

    void setFilter(int id, IItemFilter filter);

    default IItemFilter getFilter() {
        return getFilter(0);
    }
}
