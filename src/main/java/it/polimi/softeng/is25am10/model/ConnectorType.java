package it.polimi.softeng.is25am10.model;

public enum ConnectorType {
    ONE_PIPE,
    TWO_PIPE,
    UNIVERSAL,
    SMOOTH;

    public static ConnectorType fromChar(char c) {
        return switch (c) {
            case 'o' -> ONE_PIPE;
            case 't' -> TWO_PIPE;
            case 'u' -> UNIVERSAL;
            case 's' -> SMOOTH;
            default -> null; // Should never come here
        };
    }

    public char toChar() {
        return switch (this) {
            case ONE_PIPE -> 'o';
            case TWO_PIPE -> 't';
            case UNIVERSAL -> 'u';
            case SMOOTH -> 's';
        };
    }
}

