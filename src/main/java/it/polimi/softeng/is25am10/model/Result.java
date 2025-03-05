package it.polimi.softeng.is25am10.model;

import java.util.NoSuchElementException;

/**
 * Result is basically an enhanced Optional. It is intended to be used as the type of
 * the return value in many of the project's functions. It provides a boolean value to describe
 * the type of answer, a reason to signal why some process failed and the data intended to be returned
 * by the function.
 *
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
     * Indicates whether the result is successful.
     *
     * @return true if the result is successful, false otherwise
     */
    public boolean isOk() {
        return ok;
    }

    /**
     * Determines if the result represents an error.
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
     * @return the data of type {@code T} contained in this result
     * @throws NoSuchElementException if the result is an error
     */
    public T getData() throws NoSuchElementException {
        if(isErr())
            throw new NoSuchElementException();
        return data;
    }

    /**
     * Returns the reason associated with the result.
     *
     * @return a string explaining an error
     */
    public String getReason() {
        return reason;
    }

    /**
     * Creates an error {@code Result} instance with the specified reason.
     *
     * @param reason the reason or message explaining the error
     * @return a {@code Result} instance marked as an error with the provided reason
     */
    static <T> Result<T> err(String reason){
        return new Result<>(false, null, reason);
    }

    /**
     * Creates a successful {@code Result} instance containing the provided data.
     *
     * @param data the data to be included in the successful result
     * @return a {@code Result} instance marked as successful with the provided data
     */
    static <T> Result<T> ok(T data){
        return new Result<>(true, data, null);
    }
}
