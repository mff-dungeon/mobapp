package cz.mff.mobapp.event;

/**
 * Transforms values from event listeners and wraps methods, so that
 * neither doTry nor catchException can throw an exception.
 * Use to wrap listeners for libraries, our listeners should handle exceptions well.
 * @param <S> Accepts S to doTry(S)
 * @param <T> Calls listener(T)
 */
public class TransformingListener<S, T> implements Listener<S> {

    private final Transformer<S, T> successTransformer;
    private final Listener<T> listener;

    public TransformingListener(Transformer<S, T> successTransformer, Listener<T> listener) {
        this.listener = listener;
        this.successTransformer = successTransformer;
    }

    public void doTry(S success) {
        try {
            listener.doTry(successTransformer.transform(success));
        } catch (Exception e) {
            listener.doCatch(e);
        }
    }

    public void doCatch(Exception e) {
        listener.doCatch(e);
    }

    public interface Transformer<F, T> {
        T transform(F from) throws Exception;
    }

}
