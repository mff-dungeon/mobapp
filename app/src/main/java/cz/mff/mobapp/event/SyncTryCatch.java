package cz.mff.mobapp.event;

import java.util.concurrent.Semaphore;

public class SyncTryCatch<T> implements Listener<T>  {
    private Semaphore semaphore;

    private volatile boolean haveResult;
    private volatile boolean success;
    private volatile Exception exception;
    private volatile T result;

    public SyncTryCatch() {
        haveResult = false;
        semaphore = new Semaphore(0);
    }

    @Override
    public void doCatch(Exception data) {
        haveResult = true;
        success = false;
        exception = data;

        semaphore.release();
    }

    @Override
    public void doTry(T data) throws Exception {
        haveResult = true;
        success = true;
        result = data;

        semaphore.release();
    }

    public T getOrDefault(T defaultValue) {
        while (!haveResult) {
            try {
                semaphore.acquire();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        return success ? result : defaultValue;
    }

    public T getOrNull() {
        return getOrDefault(null);
    }
}
