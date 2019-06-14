package vswe.stevesfactory.api.network;

/**
 * Implement this interface in order to override the default connection logic of factory managers.
 * <p>
 * Note that even this interface is named "connectable", it handles inventory linking and has nothing to do with cable connections.
 */
public interface IConnectable {

    LinkType getConnectionType();

    enum LinkType {
        /**
         * Always connect to the tile entity
         */
        ALWAYS,
        /**
         * Use the default logic for connection. This should used as a
         */
        DEFAULT,
        NEVER,
        ;
    }

}
