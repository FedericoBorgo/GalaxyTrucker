package it.polimi.softeng.is25am10.tui.asciiui;

import com.googlecode.lanterna.gui2.TextBox;
import com.googlecode.lanterna.input.KeyStroke;
import com.googlecode.lanterna.input.KeyType;

public class IntegerBox extends TextBox {
    public IntegerBox(int defaultValue) {
        super(String.valueOf(defaultValue));
    }

    @Override
    public synchronized Result handleKeyStroke(KeyStroke keyStroke) {
        if (keyStroke.getKeyType() == KeyType.Backspace || keyStroke.getKeyType() == KeyType.Delete ||
                keyStroke.getKeyType() == KeyType.ArrowLeft || keyStroke.getKeyType() == KeyType.ArrowRight ||
                keyStroke.getKeyType() == KeyType.Enter || keyStroke.getKeyType() == KeyType.Tab) {
            return super.handleKeyStroke(keyStroke);
        }

        Character c = keyStroke.getCharacter();
        if (c != null && Character.isDigit(c)) {
            return super.handleKeyStroke(keyStroke);
        }

        return Result.UNHANDLED;
    }

    public int getInt(){
        return Integer.parseInt(getText());
    }
}