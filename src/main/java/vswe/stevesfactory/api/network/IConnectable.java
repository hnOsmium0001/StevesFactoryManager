package vswe.stevesfactory.api.network;

import com.google.common.collect.ImmutableList;
import net.minecraftforge.common.capabilities.Capability;

import java.util.Arrays;
import java.util.Comparator;

/**
 * Implement this interface in order to override the default connection logic of factory managers.
 * <p>
 * Note that even this interface is named "connectable", it handles inventory linking and has nothing to do with cable connections.
 */
public interface IConnectable {

    LinkType getConnectionType();

    /**
     * Capability key for {@link INetworkController#getLinkedInventories(Capability)} for {@link IConnectable} tile entities that doesn't
     * have a supported capability.
     * <b>Do NOT use this directly</b> for usages other than {@link INetworkController#getLinkedInventories(Capability)}. Almost nothing
     * else expects a {@code null} capability instance!
     */
    Capability<?> UNKNOWN_CONNECTION_CAPABILITY = null;

    enum LinkType {
        /**
         * Always connect to the tile entity
         */
        ALWAYS(0),
        /**
         * Use the default logic for connection.
         */
        DEFAULT(1),
        NEVER(2),
        ;

        private final int ID;

        LinkType(int id) {
            this.ID = id;
        }

        public int getID() {
            return ID;
        }

        public static final ImmutableList<LinkType> VALUES;

        static {
            LinkType[] sortedValues = values();
            Arrays.sort(sortedValues, Comparator.comparing(LinkType::getID));
            VALUES = ImmutableList.copyOf(sortedValues);
        }

        public static LinkType fromID(int id) {
            return VALUES.get(id);
        }
    }
}
