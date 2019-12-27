package vswe.stevesfactory.api.logic;

import it.unimi.dsi.fastutil.objects.Object2IntFunction;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;

import java.util.Collection;

public final class SerializationContext {

    final Object2IntFunction<IProcedure> mapper;

    public SerializationContext(Object2IntFunction<IProcedure> mapper) {
        this.mapper = mapper;
    }

    public int identify(IProcedure procedure) {
        return mapper.applyAsInt(procedure);
    }
}
