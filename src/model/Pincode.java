package model;

public class Pincode {
    private final String code;

    public Pincode(String code) {
        validatePincode(code);
        this.code = code;
    }

    private void validatePincode(String code) {
        if (code == null || code.length() != 6 || !code.matches("\\d+")) {
            throw new IllegalArgumentException("Invalid pincode: " + code);
        }
    }

    public String getCode() { return code; }

    @Override public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pincode)) return false;
        return code.equals(((Pincode) o).code);
    }

    @Override public int hashCode() { return code.hashCode(); }
    @Override public String toString() { return code; }

}
