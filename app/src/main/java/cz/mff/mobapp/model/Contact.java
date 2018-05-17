package cz.mff.mobapp.model;

public class Contact extends Bundle {
    @Override
    public Boolean isContact() {
        return true;
    }

    public static void copy(Contact from, Contact to) {
        Bundle.copy(from, to);
    }
}
