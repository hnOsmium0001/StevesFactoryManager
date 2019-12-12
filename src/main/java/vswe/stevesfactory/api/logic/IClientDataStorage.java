package vswe.stevesfactory.api.logic;

public interface IClientDataStorage {

    String getGroup();

    void setGroup(String group);

    int getComponentX();

    void setComponentX(int x);

    int getComponentY();

    void setComponentY(int y);

    String getName();

    void setName(String name);
}
