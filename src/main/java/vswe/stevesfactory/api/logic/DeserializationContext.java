package vswe.stevesfactory.api.logic;

import com.google.common.base.Preconditions;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.IntFunction;

public final class DeserializationContext {

    private IntFunction<IProcedure> indexer;

    DeserializationContext(IntFunction<IProcedure> indexer) {
        this.indexer = indexer;
    }

    public Optional<IProcedure> retrieve(int id) {
        return Optional.ofNullable(retrieveNonnull(id));
    }

    @Nullable
    public IProcedure retrieveNullable(int id) {
        return indexer.apply(id);
    }

    /**
     * Same as {@link #retrieveNullable(int)}, except throws an {@link IllegalArgumentException} if unable to retrieve a procedure from the
     * given ID.
     *
     * @throws IllegalArgumentException If unable to retrieve a procedure from the given ID.
     */
    public IProcedure retrieveNonnull(int id) {
        IProcedure p = retrieveNullable(id);
        Preconditions.checkArgument(p != null);
        return p;
    }
}
