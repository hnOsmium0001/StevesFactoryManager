package vswe.stevesfactory.logic.procedure;

public interface IAnalogTarget {

    int getAnalogBegin();

    void setAnalogBegin(int begin);

    int getAnalogEnd();

    void setAnalogEnd(int end);

    boolean isInverted();

    void setInverted(boolean inverted);
}