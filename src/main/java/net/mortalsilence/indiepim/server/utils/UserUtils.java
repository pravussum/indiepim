package net.mortalsilence.indiepim.server.utils;

import net.mortalsilence.indiepim.server.domain.UserPO;
import net.mortalsilence.indiepim.server.dto.UserDTO;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Created with IntelliJ IDEA.
 * User: AmIEvil
 * Date: 25.10.13
 * Time: 20:52
 * To change this template use File | Settings | File Templates.
 */
public class UserUtils {

    public static Collection<UserDTO> mapUserPOs2UserDTOs(final Collection<UserPO> userPOs) {

        final Iterator<UserPO> it = userPOs.iterator();
        final Collection<UserDTO> result = new LinkedList<UserDTO>();
        while(it.hasNext()) {
            final UserPO userPO = it.next();
            final UserDTO userDTO = new UserDTO();
            userDTO.setId(userPO.getId());
            userDTO.setUserName(userPO.getUserName());
            userDTO.setAdmin(userPO.isAdmin());
            result.add(userDTO);
        }
        return result;

    }

}
