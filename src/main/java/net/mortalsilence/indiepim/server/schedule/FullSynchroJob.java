package net.mortalsilence.indiepim.server.schedule;

import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.dao.UserDAO;
import net.mortalsilence.indiepim.server.domain.MessageAccountPO;
import net.mortalsilence.indiepim.server.domain.UserPO;
import net.mortalsilence.indiepim.server.message.MessageConstants.SyncUpdateMethod;
import net.mortalsilence.indiepim.server.message.synchronisation.MsgSynchroService;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;

@Service
public class FullSynchroJob implements Job/* implements JobListener */{

    @Inject
    private UserDAO userDAO;
    @Inject
    private MessageDAO messageDAO;
	@Inject
    private MsgSynchroService msgSynchroService;

    final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");

    @Transactional
    public void execute(JobExecutionContext context) throws JobExecutionException {

		JobDataMap dataMap = context.getJobDetail().getJobDataMap();
		final Long userId = dataMap.getLong("userId");
		final Long accountId = dataMap.getLong("accountId");
		UserPO user = userDAO.getUser(userId);
		MessageAccountPO account = messageDAO.getMessageAccount(userId, accountId);
		
		logger.info("Starting FULL account synchronisation for user " + user.getUserName() + ", account " + account.getName());
		msgSynchroService.synchronize(user, account, SyncUpdateMethod.FULL);
		
	}	
}
