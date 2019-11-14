package vswe.stevesfactory.logic.procedure;

// No #markDirty() because there is no mutable references returned here
public interface IAnalogTarget {

    int getAnalogBegin();

    void setAnalogBegin(int begin);

    int getAnalogEnd();

    void setAnalogEnd(int end);

    boolean isInverted();

    void setInverted(boolean inverted);
}