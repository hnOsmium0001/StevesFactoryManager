package vswe.stevesfactory.library.gui;

import com.google.errorprone.annotations.CanIgnoreReturnValue;

import java.util.Collection;
import java.util.Iterator;

public interface IContainer<T extends IWidget> extends IWidget {

    Collection<T> getChildren();

    void reflow();

    @CanIgnoreReturnValue
    IContainer<T> addChildren(T widget);

    @CanIgnoreReturnValue
    IContainer<T> addChildren(Collection<T> widgets);

    @CanIgnoreReturnValue
    default IContainer<T> addChildren(Iterable<T> widgets) {
        return addChildren(widgets.iterator());
    }

    @CanIgnoreReturnValue
    default IContainer<T> addChildren(Iterator<T> widgets) {
        widgets.forEachRemaining(this::addChildren);
        return this;
    }

}
