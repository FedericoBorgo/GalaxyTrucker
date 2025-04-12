package it.polimi.softeng.is25am10.tui;

import it.polimi.softeng.is25am10.Controller;
import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.network.ClientInterface;
import it.polimi.softeng.is25am10.network.rmi.RMIClient;
import it.polimi.softeng.is25am10.network.rmi.RMIInterface;
import it.polimi.softeng.is25am10.network.socket.SocketClient;
import it.polimi.softeng.is25am10.tui.asciiui.Config;
import it.polimi.softeng.is25am10.tui.asciiui.Game;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Scanner;
import java.util.function.BiConsumer;

public class Asciiui {

    public static void main(String[] args) {
        try{
            new Thread(() -> {
                try {
                    Controller.main(new String[]{"true"});
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }).start();
            while(!Controller.ready);

            Game render_p2 = new Game(new SocketClient("p2", "localhost", 1235, 1236));
            Game render_p1 = new Game(new RMIClient("p1", "localhost", 1234));

            System.out.println(render_p1.execute("clessidra"));
            System.out.println(render_p1.execute("clessidra"));
            System.out.println(render_p1.execute("clessidra"));
            System.out.println(render_p1.execute("alieni no"));
            System.out.println(render_p2.execute("alieni no"));
            System.out.println(render_p1.execute("pesca"));

            Scanner scanner = new Scanner(System.in);

            /*
            TESTATE: OPEN_SPACE AB_SHIP EPIDEMIC PIRATES PLANETS STATION SMUGLERS
            SLAVERS STARDUST

             */

            while(true) {
                System.out.print("> ");
                String line = scanner.nextLine();
                String cmd = line.substring(1);

                boolean check = switch (line.charAt(0)){
                    case '1' -> render_p1.checkCommand(cmd);
                    case '2' -> render_p2.checkCommand(cmd);
                    default -> false;
                };

                if(check) {
                    System.out.println(switch(line.charAt(0)){
                        case '1' -> render_p1.execute(cmd);
                        case '2' -> render_p2.execute(cmd);
                        default -> "";
                    });
                }
                else
                    System.out.println("rifiutato");
            }
        }
        catch(Exception e){
            e.printStackTrace();
        }
    }
}
