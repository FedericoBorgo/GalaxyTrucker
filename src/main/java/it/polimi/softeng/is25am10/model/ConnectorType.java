package it.polimi.softeng.is25am10.model;

/**
 * This class provides an interface to deal with the tile's connectors and check their type.
 */

public enum ConnectorType {
    ONE_PIPE,
    TWO_PIPE,
    UNIVERSAL,
    SMOOTH;

    /**
     * Converts a character into the corresponding {@code ConnectorType}.
     *
     * @param c the character to be converted, which represents a connector type.
     * @return the {@code ConnectorType} corresponding to the specified character,
     *         or {@code null} if the character does not match any valid connector type.
     */
    public static ConnectorType fromChar(char c) {
        return switch (c) {
            case 'o' -> ONE_PIPE;
            case 't' -> TWO_PIPE;
            case 'u' -> UNIVERSAL;
            case 's' -> SMOOTH;
            default -> null; // Should never come here
        };
    }

    /**
     * Converts the current ConnectorType instance to its corresponding character.
     *
     * @return a character representing the ConnectorType:
     */
    public char toChar() {
        return switch (this) {
            case ONE_PIPE -> 'o';
            case TWO_PIPE -> 't';
            case UNIVERSAL -> 'u';
            case SMOOTH -> 's';
        };
    }

    /**
     * Check if two connector are compatible.
     *
     * @param other
     * @return
     */
    public boolean connectable(ConnectorType other){
        if(this == SMOOTH){
            return other == SMOOTH;
        }

        if(other == SMOOTH){
            return false;
        }

        if(this == UNIVERSAL || other == UNIVERSAL){
            return true;
        }

        if(this == ONE_PIPE && other == ONE_PIPE){
            return true;
        }

        return this == TWO_PIPE && other == TWO_PIPE;
    }
}

