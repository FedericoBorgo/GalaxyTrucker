package it.polimi.softeng.is25am10.model;

import java.util.NoSuchElementException;

/**
 * Result is a type used to manage the return values in the entire project.
 * It is used to ascertain the success or failure of a method, encapsulating the result
 * and containing the reason of the failure.
 * It is inspired by the Class Optional in Java.util and by the Result type of Rust.
 */
public class Result<T> {
    private final boolean ok;
    private final T data;
    private final String reason;

    // Constructor
    Result(boolean ok, T data, String reason) {
        this.ok = ok;
        this.data = data;
        this.reason = reason;
    }

    /**
     * Indicates if the result is successful.
     *
     * @return true if the result is successful, false otherwise
     */
    public boolean isOk() {
        return ok;
    }

    /**
     * Indicates if an error occurred (the result is unsuccessful)
     *
     * @return true if the result is an error, false otherwise
     */
    public boolean isErr(){
        return !ok;
    }

    /**
     * Retrieves the data associated with this result.
     * If the result is an error, this method throws a {@code NoSuchElementException}.
     *
     * @return the data of type {@code T} contained in this result.
     * @throws NoSuchElementException if the result is an error.
     */
    public T getData() throws NoSuchElementException {
        if(isErr())
            throw new NoSuchElementException();
        return data;
    }

    /**
     * Get the reason the operation failed. Returns null if the operation was successful.
     *
     * @return a message explaining the error.
     */
    public String getReason() {
        return reason;
    }

    /**
     * Creates an unsuccessful {@code Result} instance with the specified reason for the error.
     *
     * @param reason the reason or message explaining the error
     * @return a {@code Result} instance marked as an error with the provided reason
     */
    public static <T> Result<T> err(String reason){
        return new Result<>(false, null, reason);
    }

    /**
     * Creates a successful {@code Result} instance containing the provided data.
     *
     * @param data the data {@code T} to be included in the successful result
     * @return a {@code Result} instance marked as successful with the provided data
     */
    public static <T> Result<T> ok(T data){
        return new Result<>(true, data, null);
    }
}
