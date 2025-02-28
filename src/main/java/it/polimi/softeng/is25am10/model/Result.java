package it.polimi.softeng.is25am10.model;

public class Result<T> {
    private final boolean accepted;
    private final T data;
    private final String reason;

    Result(boolean accepted, T data, String reason) {
        this.accepted = accepted;
        this.data = data;
        this.reason = reason;
    }

    public boolean isAccepted() {
        return accepted;
    }

    public T getData() {
        return data;
    }

    public String getReason() {
        return reason;
    }
}
