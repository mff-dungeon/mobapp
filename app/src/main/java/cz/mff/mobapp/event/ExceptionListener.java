package cz.mff.mobapp.event;

@FunctionalInterface
public interface ExceptionListener {
    void doCatch(Exception data);
}
