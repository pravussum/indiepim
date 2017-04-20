package net.mortalsilence.indiepim.server.command.handler;

import net.mortalsilence.indiepim.server.command.Command;
import net.mortalsilence.indiepim.server.command.actions.CreateOrUpdateMessageAccount;
import net.mortalsilence.indiepim.server.command.results.IdVersionResult;
import net.mortalsilence.indiepim.server.dao.GenericDAO;
import net.mortalsilence.indiepim.server.dao.MessageDAO;
import net.mortalsilence.indiepim.server.dao.TagDAO;
import net.mortalsilence.indiepim.server.dao.UserDAO;
import net.mortalsilence.indiepim.server.domain.MessageAccountPO;
import net.mortalsilence.indiepim.server.domain.MessageAccountStatsPO;
import net.mortalsilence.indiepim.server.domain.TagPO;
import net.mortalsilence.indiepim.server.domain.UserPO;
import net.mortalsilence.indiepim.server.dto.MessageAccountDTO;
import net.mortalsilence.indiepim.server.message.MessageConstants;
import net.mortalsilence.indiepim.server.schedule.AccountIncSyncroJob;
import net.mortalsilence.indiepim.server.utils.MessageUtils;
import org.quartz.DateBuilder.IntervalUnit;
import org.quartz.*;
import org.springframework.transaction.annotation.Transactional;

import javax.inject.Inject;
import javax.inject.Named;

import static org.quartz.DateBuilder.futureDate;
import static org.quartz.JobBuilder.newJob;
import static org.quartz.JobKey.jobKey;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

@Named
public class CreateOrUpdateMessageAccountHandler implements Command<CreateOrUpdateMessageAccount, IdVersionResult> {

    @Inject private UserDAO userDAO;
    @Inject private MessageDAO messageDAO;
    @Inject private GenericDAO genericDAO;
    @Inject private TagDAO tagDAO;
    @Inject private MessageUtils messageUtils;
    @Inject private Scheduler scheduler;

    @Transactional
    @Override
    public IdVersionResult execute(CreateOrUpdateMessageAccount action) {

		final MessageAccountDTO account = action.getAccount();
		final Long userId  = ActionUtils.getUserId();
        final UserPO user = userDAO.getUser(userId);
		MessageAccountPO accountPO;
		if(account.id != null) {
			accountPO = messageDAO.getMessageAccount(userId, account.id);
			if(accountPO == null)
				throw new RuntimeException("Message account with id " + account.id + " not found. Could not update.");
			/* set the persistence version to the original value */ 
			if(account.version == null)
				throw new IllegalArgumentException("The original version field must not be null if an account id is given!");
			accountPO.setTsUpdate(account.version);
		} else {
			accountPO = new MessageAccountPO();
			accountPO.setUser(user);
			accountPO.setDeleteMode(MessageConstants.MESSAGE_DELETE_MODE.MOVE_2_TRASH);
		}
		messageUtils.mapMessageAccountDTO2MessageAccountPO(account, accountPO);

		TagPO tag;
		tag = tagDAO.getTagByName(account.tag, userId);
		if(tag == null) {
			tag = new TagPO();
			tag.setTag(account.tag);
			tag.setUser(user);
			genericDAO.persist(tag);
		}
		accountPO.setTag(tag);

        /* create a statistics record as well (new accounts) */
        if(accountPO.getMessageAccountStats() == null) {
            final MessageAccountStatsPO stats = new MessageAccountStatsPO();
            genericDAO.persist(stats);
            accountPO.setMessageAccountStats(stats);
        }

		if(account.id != null) {
			genericDAO.update(accountPO);
		} else {
			genericDAO.persist(accountPO);
		}

    	if(accountPO.getSyncInterval() != null && accountPO.getSyncInterval() > 0)
			initSyncroJob(accountPO, accountPO.getSyncInterval());
		else {
			deleteSyncroJob(accountPO);
		}
		
		return new IdVersionResult(accountPO.getId(), accountPO.getTsUpdate());
	}

	private void initSyncroJob(final MessageAccountPO accountPO, final Integer syncInterval) {
		// Define job instance
		JobDetail newJob = newJob(AccountIncSyncroJob.class)
		    .withIdentity("AccountIncSyncroJob", "user_" + accountPO.getUser().getId() + "_account_" + accountPO.getId())
    		.usingJobData("userId", accountPO.getUser().getId())
			.usingJobData("accountId", accountPO.getId())
		    .build();
		    
		Trigger trigger = newTrigger()
		    .withIdentity("AccountIncSyncroTrigger", "user_" + accountPO.getUser().getId() + "_account_" + accountPO.getId())
		    .startAt(futureDate(syncInterval, IntervalUnit.MINUTE))
		    .withSchedule(simpleSchedule()
                    .withIntervalInMinutes(syncInterval)
                    .repeatForever()
            ).build();
			            
		deleteSyncroJob(accountPO);

		try {
			scheduler.scheduleJob(newJob, trigger);			
		} catch (SchedulerException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}
		
	}
	
	private void deleteSyncroJob(final MessageAccountPO accountPO) {
		try {
			scheduler.deleteJob(jobKey("AccountIncSyncroJob", "user_" + accountPO.getUser().getId() + "_account_" + accountPO.getId()));
		} catch (SchedulerException e) {
			e.printStackTrace();
			throw new RuntimeException(e);
		}			
	}

    @Override
	public void rollback(CreateOrUpdateMessageAccount arg0, IdVersionResult arg1) {
		// TODO implement rollback
	}

}
