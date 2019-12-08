package vswe.stevesfactory.api.logic;

import vswe.stevesfactory.api.network.INetworkController;

public interface ITrigger extends IProcedure {

    void tick(INetworkController controller);
}
