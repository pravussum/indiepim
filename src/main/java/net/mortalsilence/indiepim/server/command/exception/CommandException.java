package net.mortalsilence.indiepim.server.command.exception;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 07.01.14
 * Time: 20:36
 */
public class CommandException extends Exception {

    public CommandException(String message, Throwable cause) {
        super(message, cause);
    }

    public CommandException(String message) {
        super(message);
    }

    public CommandException(Throwable cause) {
        super(cause);
    }
}
