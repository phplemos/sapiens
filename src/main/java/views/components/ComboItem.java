package views.components;

public class ComboItem {
    private int id;
    private String label;

    public ComboItem(int id, String label) {
        this.id = id;
        this.label = label;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return label;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ComboItem) {
            return ((ComboItem) obj).getId() == this.id;
        }
        return false;
    }
}