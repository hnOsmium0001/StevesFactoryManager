package vswe.stevesfactory.library.collections;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.PeekingIterator;

import javax.annotation.Nonnull;
import java.util.*;

public class CompositeCollection<E> extends AbstractCollection<E> {

    private Collection<E>[] windows;

    @SafeVarargs
    public CompositeCollection(Collection<E>... windows) {
        this.windows = windows;
    }

    @Nonnull
    @Override
    public Iterator<E> iterator() {
        if (windows.length == 0) {
            return Collections.emptyIterator();
        }
        return new It();
    }

    @Override
    public int size() {
        int s = 0;
        for (Collection<E> window : windows) {
            s += window.size();
        }
        return s;
    }

    private class It extends AbstractIterator<E> implements PeekingIterator<E> {

        private Iterator<E> currentIterator;
        private int currentIndex;

        public It() {
            currentIndex = 0;
            currentIterator = windows[currentIndex].iterator();
        }

        @Override
        protected E computeNext() {
            while (true) {
                if (!currentIterator.hasNext()) {
                    currentIndex++;
                    // Running out of collections to iterate with
                    if (currentIndex >= windows.length) {
                        return endOfData();
                    }
                    // Replace the target with next collection in the line
                    currentIterator = windows[currentIndex].iterator();
                    // Retry this function; can be replaced with returning a self call
                    continue;
                }
                return currentIterator.next();
            }
        }
    }

}
