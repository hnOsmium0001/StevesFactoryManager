package vswe.stevesfactory.api.manager;

public interface ITriggerHook<T> extends IHook {

    void subscribe(T task);

    void unsubscribe(T task);
}
