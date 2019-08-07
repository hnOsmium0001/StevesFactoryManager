package vswe.stevesfactory.logic.hooks;

import vswe.stevesfactory.api.network.INetworkController;

public interface ITimedTask {

    void run(INetworkController controller);

    int getInterval();
}
