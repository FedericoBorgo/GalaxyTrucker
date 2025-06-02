package it.polimi.softeng.is25am10;

import it.polimi.softeng.is25am10.network.ClientInterface;
import it.polimi.softeng.is25am10.network.rmi.RMIClient;
import it.polimi.softeng.is25am10.tui.PlaceholderCallback;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ControllerTest {
    ClientInterface firstPlayer, secondPlayer;
    String playerName = "testPlayer1";

    @Test
    public void mainTest() throws IOException, InterruptedException {
        Controller.main(new String[]{"true"});
        firstPlayer = new RMIClient(playerName, "localhost", 1234);
        secondPlayer = new RMIClient("testPlayer2", "localhost", 1234);
        assertEquals(playerName, firstPlayer.getPlayerName());
    }
}
