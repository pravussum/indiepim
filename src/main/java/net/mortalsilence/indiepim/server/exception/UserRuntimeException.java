package net.mortalsilence.indiepim.server.exception;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 15.04.13
 * Time: 21:50
 * To change this template use File | Settings | File Templates.
 */
public class UserRuntimeException extends RuntimeException {

    public UserRuntimeException() {
        super("An error occured. Please see system log for details.");
    }

    public UserRuntimeException(String message) {
        super(message);
    }

}
