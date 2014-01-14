package net.mortalsilence.indiepim.server.command.results;

import net.mortalsilence.indiepim.server.command.Result;
import net.mortalsilence.indiepim.server.dto.UserDTO;

import java.util.Collection;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 25.10.13
 * Time: 20:46
 * To change this template use File | Settings | File Templates.
 */
public class UserDTOListResult implements Result {

    private Collection<UserDTO> users;

    public UserDTOListResult() {
    }

    public UserDTOListResult(Collection<UserDTO> users) {
        this.users = users;
    }

    public Collection<UserDTO> getUsers() {
        return users;
    }
}
