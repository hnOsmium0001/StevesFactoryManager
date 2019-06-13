package vswe.stevesfactory.api.manager;

public interface IHookTrigger extends IHook {

    void subscribe(Runnable task);

    void unsubscribe(Runnable task);

}
