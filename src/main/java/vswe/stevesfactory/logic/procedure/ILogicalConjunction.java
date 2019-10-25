package vswe.stevesfactory.logic.procedure;

public interface ILogicalConjunction {

    enum Type {
        ANY, ALL;

        public static Type[] VALUES = values();
    }

    Type getConjunctionType();

    void setConjunctionType(Type type);
}
