package vswe.stevesfactory.logic.procedure;

public interface IAnalogTarget {

    default void setNoAnalog() {
        setAnalogRange(1, 15);
    }

    void setAnalogRange(int begin, int end);

    int getAnalogBegin();

    int getAnalogEnd();

    boolean isInverted();

    void setInverted(boolean inverted);
}