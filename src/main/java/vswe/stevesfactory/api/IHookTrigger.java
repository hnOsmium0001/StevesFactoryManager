package vswe.stevesfactory.api;

public interface IHookTrigger extends IHook {

    void subscribe(Runnable task);

    void unsubscribe(Runnable task);

}
