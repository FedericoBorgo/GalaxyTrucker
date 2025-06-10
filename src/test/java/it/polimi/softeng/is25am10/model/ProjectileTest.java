package it.polimi.softeng.is25am10.model;

import com.googlecode.lanterna.TextColor;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

public class ProjectileTest {
    Projectile projectile = new Projectile(Projectile.Type.SMALL_ASTEROID, Tile.Side.LEFT, 5, 1);
    Projectile projectile2 = new Projectile(Projectile.Type.BIG_FIRE, Tile.Side.RIGHT, 6, 2);
    Projectile projectile3 = new Projectile(Projectile.Type.SMALL_FIRE, Tile.Side.LEFT, 7, 3);
    Projectile projectile4 = new Projectile(Projectile.Type.BIG_ASTEROID, Tile.Side.RIGHT, 5, 4);

    @Test
    public void testMethods() {
        String expected = "Sinistra:5 ID:1 Piccolo asteroide";
        assertEquals(expected, projectile.toString());
        assertEquals(TextColor.ANSI.RED_BRIGHT, projectile.getColor());
        assertEquals(Tile.Type.SHIELD, projectile.type().stoppedBy());
        assertEquals("Destra:6 ID:2 Grande fuoco", projectile2.toString());
        assertEquals(TextColor.ANSI.YELLOW_BRIGHT, projectile2.getColor());
        assertNull(projectile2.type().stoppedBy());
        assertEquals("Sinistra:7 ID:3 Piccolo fuoco", projectile3.toString());
        assertEquals(TextColor.ANSI.YELLOW_BRIGHT, projectile3.getColor());
        assertEquals(Tile.Type.SHIELD, projectile3.type().stoppedBy());
        assertEquals("Destra:5 ID:4 Grande asteroide", projectile4.toString());
        assertEquals(TextColor.ANSI.RED_BRIGHT, projectile4.getColor());
        assertEquals(Tile.Type.CANNON, projectile4.type().stoppedBy());
    }
}
