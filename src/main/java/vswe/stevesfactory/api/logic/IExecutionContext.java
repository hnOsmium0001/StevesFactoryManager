package vswe.stevesfactory.api.logic;

import net.minecraft.world.IWorld;
import vswe.stevesfactory.api.network.INetworkController;

/**
 * A one-use only context object for program execution data storage.
 */
// TODO variables, heads, tails, subsequences (functions), pid, forking
public interface IExecutionContext {

    INetworkController getController();

    IWorld getControllerWorld();
}
