package vswe.stevesfactory.logic.procedure;

import vswe.stevesfactory.logic.item.SingleItemFilter;

import java.util.List;

public interface IItemFilterTarget {

    List<SingleItemFilter> getFilters(int id);

    default List<SingleItemFilter> getFilters() {
        return getFilters(0);
    }
}
