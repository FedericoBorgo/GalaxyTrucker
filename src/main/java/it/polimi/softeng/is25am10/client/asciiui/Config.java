package it.polimi.softeng.is25am10.client.asciiui;

import com.googlecode.lanterna.gui2.*;
import com.googlecode.lanterna.gui2.dialogs.MessageDialog;
import com.googlecode.lanterna.gui2.dialogs.MessageDialogButton;
import com.googlecode.lanterna.screen.Screen;
import com.googlecode.lanterna.screen.TerminalScreen;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;
import com.googlecode.lanterna.terminal.Terminal;
import it.polimi.softeng.is25am10.network.ClientInterface;
import it.polimi.softeng.is25am10.network.rmi.RMIClient;
import it.polimi.softeng.is25am10.network.socket.SocketClient;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Factory for the ClientInterface. It creates a TUI for the
 * player to insert name, address, connection type and the ports.
 * It is blocking, it waits the player to give the inputs.
 * When the player gave the input, a connection to the server
 * is established.
 */
public class Config {
    // boxes for the ports
    private static final IntegerBox rmiPort = new IntegerBox(1234);
    private static final IntegerBox socketPort1 = new IntegerBox(1235);
    private static final IntegerBox socketPort2 = new IntegerBox(1236);

    // labels for the ports
    private static final Label rmiPortLabel = new Label("Porta");
    private static final Label socketPortLabel1 = new Label("Porta 1");
    private static final Label socketPortLabel2 = new Label("Porta 2");

    // address of the server
    private static final TextBox address = new TextBox("localhost");

    // rmi or socket?
    private static final ComboBox<String> connectionType = new ComboBox<>(new String[] {"RMI", "Socket"});

    private static final List<Component> toRemove = List.of(socketPortLabel1, socketPortLabel2
            , rmiPortLabel, socketPort1, socketPort2, rmiPort);

    public static ClientInterface getControls() throws IOException {
        //create a terminal, associate a screen and create a window
        //inside the screen
        Terminal terminal = new DefaultTerminalFactory().createTerminal();
        Screen  screen= new TerminalScreen(terminal);
        screen.startScreen();
        WindowBasedTextGUI textGUI = new MultiWindowTextGUI(screen);


        AtomicReference<ClientInterface> client = new AtomicReference<>(null);

        //creating the window
        Window window = new BasicWindow("Config");
        Panel panel = new Panel(new GridLayout(2));
        TextBox name = new TextBox();

        Button connect = new Button("Connect", () -> {
            //button pressed
            try{
                switch(connectionType.getSelectedIndex()){
                    case 0 -> {
                        client.set(new RMIClient(name.getText(), address.getText(), rmiPort.getInt()));
                    }
                    case 1 -> {
                        client.set(new SocketClient(name.getText(), address.getText(), socketPort1.getInt(), socketPort2.getInt()));
                    }
                    default -> throw new IllegalStateException("Unexpected value: " + connectionType.getSelectedIndex());
                };

                // the connection has succeeded and the window can be closed
                window.close();
            }catch (RuntimeException _){
                // socket or rmi can fail if the server is unreachable
                MessageDialog.showMessageDialog(textGUI, "Error", "Impossibile connettersi al server", MessageDialogButton.OK);
            }
        }).setLayoutData(GridLayout.createHorizontallyEndAlignedLayoutData(2));


        connectionType.addListener((index, _, _) -> {
            //remove all components under the connectionType field
            toRemove.forEach(panel::removeComponent);
            panel.removeComponent(connect);

            switch(index){
                case 1:
                    panel.addComponent(socketPortLabel1).addComponent(socketPort1);
                    panel.addComponent(socketPortLabel2).addComponent(socketPort2);
                    break;
                case 0:
                    panel.addComponent(rmiPortLabel).addComponent(rmiPort);
                    break;
            }
            panel.addComponent(connect);
        });

        panel.addComponent(new Label("Nome")).addComponent(name);
        panel.addComponent(new Label("Indirizzo")).addComponent(address);
        panel.addComponent(new Label("Connessione")).addComponent(connectionType);
        panel.addComponent(rmiPortLabel).addComponent(rmiPort);
        panel.addComponent(connect);

        window.setComponent(panel);
        window.setHints(Collections.singletonList(Window.Hint.CENTERED));

        textGUI.addWindow(window);
        textGUI.waitForWindowToClose(window);
        screen.close();
        terminal.close();
        return client.get();
    }
}
