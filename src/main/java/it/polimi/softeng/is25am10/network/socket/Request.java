package it.polimi.softeng.is25am10.network.socket;

import java.io.Serializable;

public class Request implements Serializable {
    private String method;
    private Object[] args;
    private Class<?>[] type;

    public Request(String method, Class<?>[] type, Object... args) {
        this.method = method;
        this.args = args;
        this.type = type;
    }

    public Class<?>[] getType(){
        return type;
    }

    public String getMethod() {
        return method;
    }

    public Object[] getArgs() {
        return args;
    }
}
