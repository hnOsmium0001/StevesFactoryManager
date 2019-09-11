package vswe.stevesfactory.logic.procedure;

import vswe.stevesfactory.logic.item.GroupItemFilter;

public interface IItemFilterTarget {

    GroupItemFilter getFilters(int id);

    default GroupItemFilter getFilters() {
        return getFilters(0);
    }
}
