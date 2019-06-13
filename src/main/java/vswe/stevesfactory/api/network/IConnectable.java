package vswe.stevesfactory.api.network;

/**
 * Implement this interface in order to override the default connection logic of factory managers.
 */
public interface IConnectable {

    ConnectionType getConnectionType();

    enum ConnectionType {
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
