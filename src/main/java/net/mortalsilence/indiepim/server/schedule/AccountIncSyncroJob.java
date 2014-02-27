package net.mortalsilence.indiepim.server.schedule;

import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.dao.UserDAO;
import net.mortalsilence.indiepim.server.domain.MessageAccountPO;
import net.mortalsilence.indiepim.server.domain.UserPO;
import net.mortalsilence.indiepim.server.message.MessageConstants;
import net.mortalsilence.indiepim.server.message.synchronisation.MsgSynchroService;
import org.apache.log4j.Logger;
import org.quartz.Job;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.stereotype.Service;

import javax.inject.Inject;

@Service
public class AccountIncSyncroJob implements Job, MessageConstants {

	final static Logger logger = Logger.getLogger("net.mortalsilence.indiepim");

    @Inject
    private UserDAO userDAO;
    @Inject
    private MessageDAO messageDAO;
    @Inject
    private MsgSynchroService msgSynchroService;

//    @Transactional(propagation = Propagation.REQUIRED, readOnly = false)
    /**
     * Transaction demarcation is handled inside the SynchroService respectivly its sub-services (PersistenceHelper)
     * and not on this global level.
     * This is due to the fact that a synchronization is really long running but single invalid messages should fail
     * recoverably and valid message should be "visible" right away (not until the sync tx is commited)
     * This could as well be solved by transaction suspension (REQUIRES_NEW) but this adds a lot complexity
     * (JtaTransactionManager is necessary instead of JpaTransactionManager, drivers must support it etc.)
     * So the current solutions is not to use a global transaction but single short transactions within the
     * synchro service.
     */
    public void execute(JobExecutionContext context) throws JobExecutionException {

        JobDataMap dataMap = context.getJobDetail().getJobDataMap();
        final Long userId = dataMap.getLong("userId");
        final Long accountId = dataMap.getLong("accountId");
        final UserPO user = userDAO.getUser(userId);
        final MessageAccountPO account = messageDAO.getMessageAccount(userId, accountId);

        if(account.getMessageAccountStats().getLastSyncRun() != null) {
            logger.info("Starting account synchronisation for user " + user.getUserName() + ", account " + account.getName() + ".");
            msgSynchroService.synchronize(user, account, SyncUpdateMethod.get(account.getSyncMethod()));
        } else {
            logger.info("Starting very first account synchronisation for user " + user.getUserName() + ", account " + account.getName());
            msgSynchroService.synchronize(user, account, SyncUpdateMethod.NONE);
        }
	}
}
