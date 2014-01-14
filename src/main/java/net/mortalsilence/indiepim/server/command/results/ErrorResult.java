package net.mortalsilence.indiepim.server.command.results;

import net.mortalsilence.indiepim.server.command.Result;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 07.01.14
 * Time: 20:30
 */
public class ErrorResult implements Result {

    private String error;

    public ErrorResult() {
    }

    public String getError() {
        return error;
    }

    public ErrorResult(String error) {
        this.error = error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
