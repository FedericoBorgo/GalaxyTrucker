package it.polimi.softeng.is25am10.network.socket;

import it.polimi.softeng.is25am10.Controller;
import it.polimi.softeng.is25am10.Logger;
import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.network.Callback;
import org.json.JSONObject;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SocketListener {
    private final ServerSocket methodInvoker;
    private final ServerSocket eventInvoker;
    private final List<MethodInvoker> methods = new ArrayList<>();
    private final List<EventInvoker> events = new ArrayList<>();
    private final Thread methodThread;
    private final Thread eventThread;

    public SocketListener(Controller controller, int methodPort, int eventPort) throws IOException {
        super();
        methodInvoker = new ServerSocket(methodPort);
        eventInvoker = new ServerSocket(eventPort);

        methodThread = new Thread(() -> {
            while(true) {
                try {
                    Socket client = methodInvoker.accept();
                    methods.add(new MethodInvoker(controller, client));
                } catch (IOException e) {
                    throw new RuntimeException("unable to start the tcp server", e);
                }
            }
        });

        eventThread = new Thread(() -> {
            while(true) {
                try {
                    Socket client = eventInvoker.accept();
                    events.add(new EventInvoker(controller, client));
                } catch (IOException e) {
                    throw new RuntimeException("unable to start the tcp server", e);
                }
            }
        });

        eventThread.start();
        methodThread.start();
    }

    public void join() throws InterruptedException {
        methodThread.join();
        eventThread.join();
    }
}

class MethodInvoker extends Thread {
    private final ObjectOutputStream output;
    private final ObjectInputStream input;
    private final Controller controller;

    public MethodInvoker(Controller controller, Socket socket) throws IOException {
        super();
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
        output.flush();
        this.controller = controller;
        start();
    }

    @Override
    public void run() {
        while (true) {
            try {
                Request request = (Request) input.readObject();
                Method method = Controller.class.getMethod(request.getMethod(), request.getType());
                output.writeObject(method.invoke(controller, request.getArgs()));
            } catch (IOException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                     IllegalAccessException e) {
                throw new RuntimeException("unable to invoke the method", e);
            }
        }
    }
}

class EventInvoker implements Callback {
    private final ObjectOutputStream output;
    private final ObjectInputStream input;

    public EventInvoker(Controller controller, Socket socket){
        super();
        try {
            output = new ObjectOutputStream(socket.getOutputStream());
            input = new ObjectInputStream(socket.getInputStream());
            String name = (String) input.readObject();
            controller.setCallback(name, this);
            Logger.socketLog("connected: " + socket.getRemoteSocketAddress() + " -> " + name);

        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("unable to connect", e);
        }
    }

    private <T> T call(String name, Class<?>[] types, Object... args){
        try {
            output.writeObject(new Request(name, types, args));
            return (T) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("unable to call: " + name, e);
        }
    }

    @Override
    public void setPlayers(Map<String, FlightBoard.Pawn> players) {
        call("setPlayers", new Class[]{Map.class}, players);
    }

    @Override
    public int askHowManyPlayers() {
        return call("askHowManyPlayers", new Class[]{});
    }

    @Override
    public void notifyState(Model.State.Type state) {
        call("notifyState", new Class[]{Model.State.Type.class}, state);
    }

    @Override
    public void movedTimer() {
        call("movedTimer", new Class[]{});
    }

    @Override
    public void pushPositions(List<FlightBoard.Pawn> order, List<Integer> offset) {
        call("pushPositions", new Class[]{List.class, List.class}, order, offset);
    }

    @Override
    public void pushCard(Card.CompressedCard card) {
        call("pushCard", new Class[]{Card.CompressedCard.class}, card);
    }

    @Override
    public void pushCardChanges(JSONObject data) {
        call("pushCardChanges", new Class[]{JSONObject.class}, data);
    }

    @Override
    public void askForInput() throws RemoteException {
        call("askForInput", new Class[]{});
    }

    @Override
    public void gaveTile(Tile t) throws RemoteException {
        call("gaveTile", new Class[]{Tile.class}, t);
    }

    @Override
    public void gotTile(Tile t) throws RemoteException {
        call("gotTile", new Class[]{Tile.class}, t);
    }
}
