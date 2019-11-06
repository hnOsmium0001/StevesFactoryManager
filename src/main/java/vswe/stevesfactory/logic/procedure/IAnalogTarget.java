package vswe.stevesfactory.logic.procedure;

public interface IAnalogTarget {

    void setAnalogRange(int begin, int end);

    int getAnalogBegin();

    int getAnalogEnd();

    boolean isInverted();

    void setInverted(boolean inverted);
}