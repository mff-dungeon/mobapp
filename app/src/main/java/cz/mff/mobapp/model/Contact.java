package cz.mff.mobapp.model;

public class Contact extends Bundle {

    private String label;

    @Override
    public Boolean isContact() {
        return true;
    }

    public static void copy(Contact from, Contact to) {
        Bundle.copy(from, to);
        if (from.getLabel() != null)
            to.setLabel(from.getLabel());
    }

    public String getLabel() {
        return label;
    }

    public Contact setLabel(String label) {
        this.label = label;
        return this;
    }
}
