package vswe.stevesfactory.library.collections;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.PeekingIterator;

import javax.annotation.Nonnull;
import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;

public class CompositeCollection<E> extends AbstractCollection<E> {

    private Collection<E>[] collections;

    @SafeVarargs
    public CompositeCollection(Collection<E>... collections) {
        this.collections = collections;
    }

    @Nonnull
    @Override
    public Iterator<E> iterator() {
        if (collections.length == 0) {
            return Collections.emptyIterator();
        }
        return new It();
    }

    @Override
    public int size() {
        int s = 0;
        for (Collection<E> window : collections) {
            s += window.size();
        }
        return s;
    }

    private class It extends AbstractIterator<E> implements PeekingIterator<E> {

        private Iterator<E> currentIterator;
        private int currentIndex;

        public It() {
            currentIndex = 0;
            currentIterator = collections[currentIndex].iterator();
        }

        @Override
        protected E computeNext() {
            while (true) {
                if (!currentIterator.hasNext()) {
                    currentIndex++;
                    // Running out of collections to iterate with
                    if (currentIndex >= collections.length) {
                        return endOfData();
                    }
                    // Replace the target with next collection in the line
                    currentIterator = collections[currentIndex].iterator();
                    // Retry this function; can be replaced with returning a self call
                    continue;
                }
                return currentIterator.next();
            }
        }
    }
}
