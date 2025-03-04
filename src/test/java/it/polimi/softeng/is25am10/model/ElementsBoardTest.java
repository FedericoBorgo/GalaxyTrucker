package it.polimi.softeng.is25am10.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ElementsPlaceholder extends ElementsBoard {

    public ElementsPlaceholder(ShipBoard board) {
        super(board);
    }

    @Override
    public Result<Integer> put(int x, int y, int qty) {
        set(x, y, qty);
        total += qty;
        return new Result<>(true, qty, null);
    }
}


class ElementsBoardTest {
    ElementsPlaceholder placeholder;

    @BeforeEach
    void setUp() {
        placeholder = new ElementsPlaceholder(new ShipBoard());
    }

    @Test
    void testSetGet(){
        placeholder.put(3, 2, 10);
        assertEquals(10, placeholder.get(3, 2));
    }

    @Test
    void testRemove(){
        placeholder.put(3, 2, 10);
        placeholder.remove(3, 2, 5);
        assertEquals(5, placeholder.get(3, 2));
    }

    @Test
    void testMove(){
        placeholder.put(3, 2, 10);
        placeholder.move(3, 2, 2, 2, 5);

        assertEquals(5, placeholder.get(3, 2));
        assertEquals(5, placeholder.get(2, 2));
        assertEquals(10, placeholder.getTotal());
    }


}