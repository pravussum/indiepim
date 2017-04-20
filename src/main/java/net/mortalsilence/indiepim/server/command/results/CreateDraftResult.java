package net.mortalsilence.indiepim.server.command.results;

import net.mortalsilence.indiepim.server.command.Result;
import net.mortalsilence.indiepim.server.dto.MessageDTO;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 08.02.14
 * Time: 22:28
 */
public class CreateDraftResult implements Result {

    public MessageDTO origMessage;
    public MessageDTO draft;
    public Long id;
}
