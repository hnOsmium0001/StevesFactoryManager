package vswe.stevesfactory.logic.procedure;

// No #markDirty() here because no mutable reference is returned
public interface ILogicalConjunction {

    enum Type {
        ANY {
            @Override
            public boolean combine(boolean previous, boolean current) {
                return previous || current;
            }
        },
        ALL {
            @Override
            public boolean combine(boolean previous, boolean current) {
                return previous && current;
            }
        };

        public abstract boolean combine(boolean previous, boolean current);

        public static Type[] VALUES = values();
    }

    Type getConjunction();

    void setConjunction(Type type);
}
