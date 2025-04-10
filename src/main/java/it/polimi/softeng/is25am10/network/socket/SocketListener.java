package it.polimi.softeng.is25am10.network.socket;

import it.polimi.softeng.is25am10.Controller;
import it.polimi.softeng.is25am10.Logger;
import it.polimi.softeng.is25am10.model.Model;
import it.polimi.softeng.is25am10.model.Tile;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.model.boards.ShipBoard;
import it.polimi.softeng.is25am10.model.cards.Card;
import it.polimi.softeng.is25am10.model.cards.CardOutput;
import it.polimi.softeng.is25am10.network.Callback;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.RemoteException;
import java.util.Arrays;
import java.util.HashMap;

public class SocketListener {
    private final ServerSocket methodInvoker;
    private final ServerSocket eventInvoker;
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
                    new MethodInvoker(controller, client);
                } catch (IOException e) {
                    throw new RuntimeException("unable to start the tcp server", e);
                }
            }
        });

        eventThread = new Thread(() -> {
            while(true) {
                try {
                    Socket client = eventInvoker.accept();
                    new EventInvoker(controller, client);
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
    private final String address;

    public MethodInvoker(Controller controller, Socket socket) throws IOException {
        super();
        output = new ObjectOutputStream(socket.getOutputStream());
        input = new ObjectInputStream(socket.getInputStream());
        output.flush();
        this.controller = controller;
        address = socket.getRemoteSocketAddress().toString();
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
                Logger.serverLog("[" +  address + "]disconnected");
                return;
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

    private synchronized <T> T call(Object... args){
        try {
            Class<?>[] types = Arrays.stream(args)
                    .map(Object::getClass)
                    .toArray(Class<?>[]::new);

            String methodName = Thread.currentThread()
                                .getStackTrace()[2]
                                .getMethodName();

            output.reset();
            output.writeObject(new Request(methodName, types, args));
            output.flush();

            return (T) input.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("unable to call", e);
        }
    }

    @Override
    public void setPlayers(HashMap<String, FlightBoard.Pawn> players) {
        call(players);
    }

    @Override
    public int askHowManyPlayers() {
        return call();
    }

    @Override
    public void pushState(Model.State.Type state) {
        call(state);
    }

    @Override
    public void pushCard(Card card) {
        call(card);
    }

    @Override
    public void pushCardChanges(CardOutput output) {
        call(output);
    }

    @Override
    public void askForInput() throws RemoteException {
        call();
    }

    @Override
    public void gaveTile(Tile t) throws RemoteException {
        call(t);
    }

    @Override
    public void gotTile(Tile t) throws RemoteException {
        call(t);
    }

    @Override
    public void pushBoard(ShipBoard board) throws RemoteException {
        call(board);
    }

    @Override
    public void pushFlight(FlightBoard board) throws RemoteException {
        call(board);
    }

    @Override
    public int ping() throws RemoteException{
        return call();
    }
}
