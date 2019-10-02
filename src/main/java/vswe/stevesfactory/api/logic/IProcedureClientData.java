package vswe.stevesfactory.api.logic;

/**
 * Data bean object used for storing client data that needs to be persist.
 */
public interface IProcedureClientData {

    int getComponentX();

    void setComponentX(int x);

    int getComponentY();

    void setComponentY(int y);

    String getName();

    void setName(String name);
}
