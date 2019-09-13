package vswe.stevesfactory.logic.procedure;

import vswe.stevesfactory.logic.item.GroupItemFilter;

public interface IItemFilterTarget {

    GroupItemFilter getFilter(int id);

    default GroupItemFilter getFilter() {
        return getFilter(0);
    }
}
