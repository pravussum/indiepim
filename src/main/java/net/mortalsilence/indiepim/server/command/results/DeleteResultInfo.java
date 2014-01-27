package net.mortalsilence.indiepim.server.command.results;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 26.01.14
 * Time: 21:28
 */
public class DeleteResultInfo {
    public Boolean deleted;
    public String reason;

    public DeleteResultInfo(Boolean deleted) {
        this.deleted = deleted;
    }

    public DeleteResultInfo(Boolean deleted, String reason) {
        this.deleted = deleted;
        this.reason = reason;
    }
}
