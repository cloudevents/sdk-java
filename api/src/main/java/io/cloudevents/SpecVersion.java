package io.cloudevents;

public enum SpecVersion {
    V03("0.3"),
    V1("1.0");

    private final String stringValue;

    SpecVersion(String stringValue) {
        this.stringValue = stringValue;
    }

    @Override
    public String toString() {
        return this.stringValue;
    }

    public static SpecVersion parse(String sv) {
        switch (sv) {
            case "0.3": return SpecVersion.V03;
            case "1.0": return SpecVersion.V1;
            default: throw new IllegalArgumentException("Unrecognized SpecVersion "+ sv);
        }
    }
}
