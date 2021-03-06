package vswe.stevesfactory.library.collections;

import java.util.AbstractList;
import java.util.List;

public class CompositeUnmodifiableList<E> extends AbstractList<E> {

    public static <E> CompositeUnmodifiableList<E> of(List<E> list1, List<E> list2) {
        return new CompositeUnmodifiableList<>(list1, list2);
    }

    private final List<E> list1;
    private final List<E> list2;

    private CompositeUnmodifiableList(List<E> list1, List<E> list2) {
        this.list1 = list1;
        this.list2 = list2;
    }

    @Override
    public E get(int index) {
        if (index < list1.size()) {
            return list1.get(index);
        }
        return list2.get(index - list1.size());
    }

    @Override
    public int size() {
        return list1.size() + list2.size();
    }
}