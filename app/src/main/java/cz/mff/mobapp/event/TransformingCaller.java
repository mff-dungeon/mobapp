package cz.mff.mobapp.event;

public class TransformingCaller<T, S, E> {

    private final Caller<T> caller;
    private final Transformer<S, T> successTransformer;
    private final ExceptionTransformer<E> exceptionTransformer;

    public TransformingCaller(Listener<T> listener,
                              ExceptionListener exceptionListener,
                              Transformer<S, T> successTransformer,
                              ExceptionTransformer<E> exceptionTransformer) {
        this.caller = new Caller<>(listener, exceptionListener);
        this.successTransformer = successTransformer;
        this.exceptionTransformer = exceptionTransformer;
    }

    public void call(S success) {
        try {
            caller.call(successTransformer.transform(success));
        } catch (Exception e) {
            caller.exception(e);
        }
    }

    public void exception(E error) {
        try {
            caller.exception(exceptionTransformer.transform(error));
        } catch (Exception e) {
            caller.exception(e);
        }
    }

    public interface Transformer<F, T> {
        T transform(F from) throws Exception;
    }

    public interface ExceptionTransformer<F> extends Transformer<F, Exception> { }

}
