package vswe.stevesfactory.api.item;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Supplier;

/**
 * A type to instance container for {@link IItemBufferElement}s.
 */
public final class ItemBuffers {

    // TODO use tagged union type as key
    Map<Class<? extends IItemBufferElement>, IItemBufferElement> instances = new IdentityHashMap<>();

    public Collection<IItemBufferElement> getAllElements() {
        return instances.values();
    }

    @Nullable
    @SuppressWarnings("unchecked")
    public <T extends IItemBufferElement> T getBuffer(Class<T> type) {
        return (T) instances.get(type);
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    public <T extends IItemBufferElement> T getBuffer(Class<T> type, Supplier<T> supplier) {
        T instance = (T) instances.get(type);
        if (instance == null) {
            T newInstance = supplier.get();
            instances.put(type, newInstance);
            return newInstance;
        } else {
            return instance;
        }
    }

    public <T extends IItemBufferElement> void putBuffer(Class<T> type, T buffer) {
        instances.put(type, buffer);
    }
}
