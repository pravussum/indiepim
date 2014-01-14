package net.mortalsilence.indiepim.server.command.actions;

import net.mortalsilence.indiepim.server.command.AbstractSessionAwareAction;
import net.mortalsilence.indiepim.server.command.results.IdResult;
import net.mortalsilence.indiepim.server.dto.UserDTO;

public class CreateOrUpdateUser extends AbstractSessionAwareAction<IdResult> {

	private UserDTO user;

	public UserDTO getUser() {
		return user;
	}

	public CreateOrUpdateUser(UserDTO user) {
		this.user = user;
	}

	public CreateOrUpdateUser() {
	}
	
}
