package vswe.stevesfactory.library.gui.screen;

import com.google.common.collect.AbstractIterator;
import com.google.common.collect.PeekingIterator;
import vswe.stevesfactory.library.gui.IWindow;

import javax.annotation.Nonnull;
import java.util.*;

class WindowCollection extends AbstractCollection<IWindow> {

    private Collection<IWindow>[] windows;

    @SafeVarargs // Internal usages only
    public WindowCollection(Collection<IWindow>... windows) {
        this.windows = windows;
    }

    @Nonnull
    @Override
    public Iterator<IWindow> iterator() {
        return It.it(this);
    }

    @Override
    public int size() {
        int s = 0;
        for (Collection<IWindow> window : windows) {
            s += window.size();
        }
        return s;
    }

    private static class It extends AbstractIterator<IWindow> implements PeekingIterator<IWindow> {

        public static Iterator<IWindow> it(WindowCollection collection) {
            if (collection.windows.length == 0) {
                return Collections.emptyIterator();
            }
            return new It(collection);
        }

        private final Collection<IWindow>[] windows;
        private Iterator<IWindow> currentIterator;
        private int currentIndex;

        public It(WindowCollection collection) {
            windows = collection.windows;
            currentIndex = 0;
            currentIterator = windows[currentIndex].iterator();
        }

        @Override
        protected IWindow computeNext() {
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
