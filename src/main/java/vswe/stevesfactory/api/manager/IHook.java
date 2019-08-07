package vswe.stevesfactory.api.manager;

import vswe.stevesfactory.api.network.INetworkController;

public interface IHook {

    void tick(INetworkController controller);
}
