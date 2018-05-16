package cz.mff.mobapp.event;

import org.json.JSONException;

public class Caller<T> {

    private final Listener<T> listener;
    private final ExceptionListener exceptionListener;

    public Caller(Listener<T> listener, ExceptionListener exceptionListener) {
        this.listener = listener;
        this.exceptionListener = exceptionListener;
    }

    public void call(T result) {
        try {
            listener.fire(result);
        } catch (Exception t) {
            exceptionListener.catchException(t);
        }
    }

    public void exception(Exception e) {
            exceptionListener.catchException(e);
    }
}
