package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.GetEmailAddresses;
import net.mortalsilence.indiepim.server.command.results.EmailAddressListResult;
import net.mortalsilence.indiepim.server.dao.ContactDAO;
import net.mortalsilence.indiepim.server.dto.EmailAddressDTO;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.List;

@Named
public class GetEmailAddressesHandler implements Command<GetEmailAddresses, EmailAddressListResult> {

    @Inject
    private ContactDAO contactDAO;
	final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");
	
	@Transactional (readOnly = true)
    @Override
    public EmailAddressListResult execute(GetEmailAddresses action) {
		final Long userId = ActionUtils.getUserId();
        final List<EmailAddressDTO> emailAddressDTOs;

        if(action.getQuery() != null && !"".equals(action.getQuery()))
            emailAddressDTOs = contactDAO.searchForEmailAddresses(userId, action.getQuery());
        else
            emailAddressDTOs = contactDAO.getAllEmailAddresses(userId);

        return new EmailAddressListResult(emailAddressDTOs);
	}

	@Override
	public void rollback(GetEmailAddresses arg0, EmailAddressListResult arg1) {
		// n/a
	}
	
	

}
