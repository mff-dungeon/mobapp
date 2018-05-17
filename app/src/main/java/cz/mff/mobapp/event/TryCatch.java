package cz.mff.mobapp.event;

public class TryCatch<T> implements Listener<T> {
    private final TryListener<T> listener;
    private final ExceptionListener exceptionListener;

    public TryCatch(TryListener<T> listener, ExceptionListener exceptionListener) {
        this.listener = listener;
        this.exceptionListener = exceptionListener;
    }

    public TryCatch(Listener<T> listener) {
        this.listener = listener;
        this.exceptionListener = listener;
    }

    @Override
    public void doTry(T data) {
        if (listener == null)
            return;

        try {
            listener.doTry(data);
        } catch (Exception e) {
            exceptionListener.doCatch(e);
        }
    }

    @Override
    public void doCatch(Exception data) {
        if (exceptionListener == null)
            throw new Error("Ignored exception.", data);

        exceptionListener.doCatch(data);
    }
}
