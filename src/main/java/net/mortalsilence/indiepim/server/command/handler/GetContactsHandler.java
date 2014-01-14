package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.GetContacts;
import net.mortalsilence.indiepim.server.command.results.ContactListResult;
import net.mortalsilence.indiepim.server.dao.ContactDAO;
import net.mortalsilence.indiepim.server.domain.ContactPO;
import net.mortalsilence.indiepim.server.dto.ContactListDTO;
import net.mortalsilence.indiepim.server.utils.ContactUtils;
import org.apache.log4j.Logger;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.Calendar;
import java.util.List;

@Named
public class GetContactsHandler implements Command<GetContacts, ContactListResult> {

    @Inject
    private ContactDAO contactDAO;
	final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");
	
	@Transactional (readOnly = true)
    @Override
    public ContactListResult execute(GetContacts action) {
		final Long userId = ActionUtils.getUserId();
		final List<ContactPO> contacts;
		Long count;
		final Long start = Calendar.getInstance().getTimeInMillis();
		final Long queryTime;
		if(action.getTagLineageId() != null) {
			contacts = contactDAO.getContactsForTagLineage(userId, action.getTagLineageId(), action.getOffset(), action.getPageSize());
			queryTime = Calendar.getInstance().getTimeInMillis();
			count = contactDAO.getContactsForTagLineageCount(userId, action.getTagLineageId());
		}
		else if (action.getTagName() != null) {
			contacts = contactDAO.getContactsForTag(userId, action.getTagName(), action.getOffset(), action.getPageSize());
			queryTime = Calendar.getInstance().getTimeInMillis();
			count = contactDAO.getContactsForTagTotalCount(userId, action.getTagName());
		}
		else if(action.getSearchTerm() != null) {
            // TODO check input parameters !!!
            contacts = contactDAO.searchForContacts(userId, action.getSearchTerm(), action.getOffset(), action.getPageSize());
            queryTime = Calendar.getInstance().getTimeInMillis();
            count = contactDAO.searchForMessagesTotalCount(userId, action.getSearchTerm());

		}
		else {
			contacts = contactDAO.getAllContacts(userId, action.getOffset(), action.getPageSize());
			queryTime = Calendar.getInstance().getTimeInMillis();
			count = contactDAO.getAllContactsCount(userId);
		} 
		final Long countTime = Calendar.getInstance().getTimeInMillis();
		logger.info("GetContacts: Query took " + (queryTime.intValue() - start.intValue()) + "ms.");
		logger.info("GetContacts: Count took " + (countTime.intValue() - queryTime.intValue()) + "ms.");
		List<ContactListDTO> result = ContactUtils.mapContactPO2ContactListDTOs(contacts);
		final Long mapTime = Calendar.getInstance().getTimeInMillis();
		logger.info("GetContacts: Mapping took " + (mapTime.intValue() - countTime.intValue()) + "ms.");
		
		return new ContactListResult(result, count);
	}

	@Override
	public void rollback(GetContacts arg0, ContactListResult arg1) {
		// n/a
	}
	
	

}
