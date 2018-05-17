package cz.mff.mobapp.event;

public class TryCatch<T> implements Listener<T> {
    private final TryListener<T> listener;
    private final ExceptionListener exceptionListener;

    public TryCatch(TryListener<T> listener, ExceptionListener exceptionListener) {
        this.listener = listener;
        this.exceptionListener = exceptionListener;
    }

    public void safeTry(T data) {
        try {
            listener.doTry(data);
        } catch (Exception e) {
            exceptionListener.doCatch(e);
        }
    }

    @Override
    public void doTry(T data) throws Exception {
        listener.doTry(data);
    }

    @Override
    public void doCatch(Exception data) {
        exceptionListener.doCatch(data);
    }
}
