package it.polimi.softeng.is25am10.network.socket;

import it.polimi.softeng.is25am10.model.Result;
import it.polimi.softeng.is25am10.model.boards.FlightBoard;
import it.polimi.softeng.is25am10.network.Callback;
import it.polimi.softeng.is25am10.network.ClientInterface;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;

public class SocketClient extends ClientInterface {
    private Callback callback;
    private final ObjectOutputStream methodOutput;
    private final ObjectInputStream methodInput;
    private final ObjectOutputStream eventOutput;
    private final ObjectInputStream eventInput;

    public SocketClient(String name, String host, int port1, int port2) {
        super(name);

        try {
            Socket method = new Socket(host, port1);
            Socket event = new Socket(host, port2);
            eventOutput = new ObjectOutputStream(event.getOutputStream());
            eventInput = new ObjectInputStream(event.getInputStream());
            methodOutput = new ObjectOutputStream(method.getOutputStream());
            methodInput = new ObjectInputStream(method.getInputStream());
            start();
            eventOutput.writeObject(name);
        } catch (IOException e) {
            throw new RuntimeException("unable to connect to the server", e);
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Object obj = eventInput.readObject();
                Request request = (Request) obj;
                Method method = Callback.class.getMethod(request.getMethod(), request.getType());
                eventOutput.writeObject(method.invoke(callback, request.getArgs()));
                eventOutput.flush();
            } catch (IOException | ClassNotFoundException | InvocationTargetException | NoSuchMethodException |
                     IllegalAccessException e) {
                throw new RuntimeException("unable to handle the event", e);
            }
        }
    }

    @Override
    protected synchronized <T> T call(Object... args){
        try {
            methodOutput.reset();
            methodOutput.writeObject(new Request(getCallerName(), getClasses(args), args));
            methodOutput.flush();

            return (T) methodInput.readObject();
        } catch (IOException | ClassNotFoundException e) {
            throw new RuntimeException("unable to call", e);
        }
    }

    @Override
    public Result<FlightBoard.Pawn> join(Callback callback){
        this.callback = callback;
        return call(getPlayerName());
    }
}
