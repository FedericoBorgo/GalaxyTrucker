package it.polimi.softeng.is25am10.model;

import java.util.NoSuchElementException;

public class Result<T> {
    private final boolean ok;
    private final T data;
    private final String reason;

    Result(boolean ok, T data, String reason) {
        this.ok = ok;
        this.data = data;
        this.reason = reason;
    }

    public boolean isOk() {
        return ok;
    }

    public boolean isErr(){
        return !ok;
    }

    public T getData() throws NoSuchElementException {
        if(isErr())
            throw new NoSuchElementException();
        return data;
    }

    public String getReason() {
        return reason;
    }

    static <T> Result<T> err(String reason){
        return new Result<>(false, null, reason);
    }

    static <T> Result<T> ok(T data){
        return new Result<>(true, data, null);
    }
}
