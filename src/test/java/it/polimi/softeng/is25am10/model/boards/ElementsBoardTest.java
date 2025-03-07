package it.polimi.softeng.is25am10.model.boards;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


class ElementsBoardTest {
    ElementsPlaceholder placeholder;

    @BeforeEach
    void setUp() {
        placeholder = new ElementsPlaceholder(new TilesBoard());
    }

    @Test
    void testSetGet(){
        placeholder.put(3, 2, 10);
        assertEquals(10, placeholder.get(3, 2));
        assertEquals(10, placeholder.getTotal());
    }

    @Test
    void testRemove(){
        placeholder.put(3, 2, 10);
        placeholder.remove(3, 2, 5);
        assertEquals(5, placeholder.get(3, 2));
        assertEquals(5, placeholder.getTotal());
    }

    @Test
    void testMove(){
        placeholder.put(3, 2, 10);
        placeholder.move(3, 2, 2, 2, 5);

        assertEquals(5, placeholder.get(3, 2));
        assertEquals(5, placeholder.get(2, 2));
        assertEquals(10, placeholder.getTotal());
        assertEquals(10, placeholder.getTotal());
    }


}